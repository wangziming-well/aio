package com.wzm.aio.service;


import com.wzm.aio.api.entity.MomoCloudNotepad;
import com.wzm.aio.domain.MomoLocalNotepad;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MomoService {

    private final Log logger = LogFactory.getLog(MomoService.class);

    private final MomoLocalService localService;
    private final MomoCloudService cloudService;

    public MomoService(MomoLocalService localService, MomoCloudService cloudService){
        this.localService = localService;
        this.cloudService = cloudService;
    }



    //远端词库同步到本地
    public boolean syncCloud(){
        List<MomoCloudNotepad> allMomoCloudNotepads = cloudService.getAllNotepads();
        for(MomoCloudNotepad momoCloudNotepad : allMomoCloudNotepads){
            MomoLocalNotepad local = momoCloudNotepad.toLocal();
            //boolean insert = localService.insert(local);
            //System.out.println(insert);
        }
        return false;
    }



}
