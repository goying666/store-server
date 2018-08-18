package com.renchaigao.zujuba.storeserver.controller;

import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.storeserver.service.impl.StoreServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping(value = "/join")
public class JoinController {
    @Autowired
    StoreServiceImpl storeServiceImpl;

    @PostMapping(consumes = "multipart/form-data")
    @ResponseBody
    public ResponseEntity addRecordingFile(@RequestParam("json") String storeInfo, @RequestParam("photo") MultipartFile[] photos) {
        return storeServiceImpl.addStore(storeInfo, photos);
    }
}
