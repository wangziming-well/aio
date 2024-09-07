package com.wzm.aio.service;


import com.wzm.aio.api.entity.MomoCloudNotepad;
import com.wzm.aio.domain.MomoLocalNotepad;
import com.wzm.aio.dto.MomoNotepadDTO;
import com.wzm.aio.util.BeanUtils;
import com.wzm.aio.util.WordListParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MomoService {

    private final Log logger = LogFactory.getLog(MomoService.class);

    private final MomoLocalService localService;
    private final MomoCloudService cloudService;

    public MomoService(MomoLocalService localService, MomoCloudService cloudService){
        this.localService = localService;
        this.cloudService = cloudService;
    }



    //云端词库同步到本地
    public boolean pull(){
        List<MomoCloudNotepad> cloudNotepads = cloudService.getAllNotepads();
        localService.clearNotepad();
        for(MomoCloudNotepad cloudNotepad : cloudNotepads){
            MomoLocalNotepad local = cloudNotepad.toLocal();
            boolean result = localService.addNotepad(local);
            if (!result)
                return false;
        }
        return true;
    }
    //本地notepad同步到云端
    public boolean push(){
        List<MomoLocalNotepad> localNotepads =  localService.getAllNotepads();
        for (MomoLocalNotepad localNotepad : localNotepads){
            MomoCloudNotepad cloud = localNotepad.toCloud();
            String cloudId = cloud.getId();
            if (cloudService.exists(cloudId))
                cloudService.updateNotepad(cloud);
            else{
                cloudId =cloudService.createNotepad(cloud);
                localNotepad.setCloudId(cloudId);
                localService.updateNotepad(localNotepad);
            }
        }
        return false;
    }

    public void addNotepad(MomoNotepadDTO notepadDTO){
        MomoLocalNotepad localNotepad = BeanUtils.transfer(notepadDTO, MomoLocalNotepad.class);
        MomoCloudNotepad cloudNotepad = localNotepad.toCloud();
        String cloudId = cloudService.createNotepad(cloudNotepad);
        localNotepad.setCloudId(cloudId);
        localService.addNotepad(localNotepad);
    }

    public void updateNotepad(MomoNotepadDTO notepadDTO){
        MomoLocalNotepad localNotepad = BeanUtils.transfer(notepadDTO, MomoLocalNotepad.class);
        MomoCloudNotepad cloudNotepad = localNotepad.toCloud();
        localService.updateNotepad(localNotepad);
        cloudService.updateNotepad(cloudNotepad);
    }

    public void deleteNotepad(int localId){
        MomoLocalNotepad notepad = localService.getNotepad(localId);
        String cloudId = notepad.getCloudId();
        localService.deleteNotepad(localId);
        cloudService.deleteNotepad(cloudId);

    }

    public MomoNotepadDTO getNotepad(int localId){
        MomoLocalNotepad notepad = localService.getNotepad(localId);
        return BeanUtils.transfer(notepad,MomoNotepadDTO.class);
    }


}
