package com.renchaigao.zujuba.storeserver.service;

import com.renchaigao.zujuba.domain.response.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface StoreService {

//    ResponseEntity addPic(MultipartFile file);

    ResponseEntity addStore(String json, MultipartFile[] photos);
    ResponseEntity getStoreInfoByUserId(String userId);

}
