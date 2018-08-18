package com.renchaigao.zujuba.storeserver.controller;

import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.storeserver.service.impl.StoreServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping(value = "/get")
public class StoreController {
    @Autowired
    StoreServiceImpl storeServiceImpl;

    @GetMapping(value = "/storeinfo/{userid}", consumes = "application/json")
    @ResponseBody
    public ResponseEntity getStoreInfoByUserId(@PathVariable("userid") String userid) {
        return storeServiceImpl.getStoreInfoByUserId(userid);
    }
}
