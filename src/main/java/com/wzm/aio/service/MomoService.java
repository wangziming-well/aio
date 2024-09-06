package com.wzm.aio.service;

import com.wzm.aio.api.MomoOpenApi;
import com.wzm.aio.api.MomoResponse;
import com.wzm.aio.api.entity.Notepad;
import com.wzm.aio.api.entity.NotepadList;
import com.wzm.aio.domain.*;
import com.wzm.aio.properties.MomoProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;

@Service
public class MomoService {

    private static final Log logger = LogFactory.getLog(MomoService.class);

    private final MomoOpenApi momoOpenApi;

    public MomoService(MomoOpenApi momoOpenApi) {
        this.momoOpenApi = momoOpenApi;

    }


    //获取当前用户的所有notepad
    public List<Notepad> getAllNotepads() {
        ResponseEntity<MomoResponse<NotepadList>> result = momoOpenApi.getAllNotepads();
        NotepadList data = result.getBody().getData();
        return data.getNotepads();
    }

    public boolean deleteNotepad(String id) {
        ResponseEntity<MomoResponse<Void>> response = momoOpenApi.deleteNotepad(id);

        return false;
    }


    public boolean saveNotepad(MomoNotepad notepad) {

        return true;
    }

    public boolean deleteNotepad(MomoNotepad notepad) {

        return true;
    }


}
