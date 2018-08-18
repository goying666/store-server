package com.renchaigao.zujuba.storeserver.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.dao.Store;
import com.renchaigao.zujuba.dao.User;
import com.renchaigao.zujuba.dao.mapper.StoreMapper;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.domain.response.RespCode;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.mongoDB.info.AddressInfo;
import com.renchaigao.zujuba.mongoDB.info.Photo;
import com.renchaigao.zujuba.mongoDB.info.store.BusinessPart.StoreBusinessInfo;
import com.renchaigao.zujuba.mongoDB.info.store.EquipmentPart.StoreEquipmentInfo;
import com.renchaigao.zujuba.mongoDB.info.store.GoodsPart.StorePackageInfo;
import com.renchaigao.zujuba.mongoDB.info.store.HardwarePart.StoreHardwareInfo;
import com.renchaigao.zujuba.mongoDB.info.store.*;
import com.renchaigao.zujuba.storeserver.service.StoreService;
import normal.dateUse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import store.DistanceFunc;

import java.io.*;
import java.util.*;

@Service
public class StoreServiceImpl implements StoreService {

    private static Logger logger = Logger.getLogger(StoreServiceImpl.class);

    @Autowired
    StoreMapper storeMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    MongoTemplate mongoTemplate;

    /**********************************************
     * 功能：生成系统下 各用户对应的recording 目录
     * 入参：用户id  userId
     **********************************************/
    private String creatFilePathOnservice(String userId) {
        File file = new File("/fpfolder/recording/users/" + userId);
        if (!file.exists())
            file.mkdirs();
        return "/fpfolder/recording/users/" + userId.toString() + "/";
    }

    private String creatPhotoFilePath(String userId, String storeId) {
        String path = "/fpfolder/recording/users/" + userId + "/" + storeId;
        File file = new File(path);
        if (!file.exists())
            file.mkdirs();
        return path + "/";
    }

//    @Override
//    public ResponseEntity addPic(MultipartFile file) {
//        String filePathOnService = creatFilePathOnservice(1);
//        if (!file.isEmpty()) {
//            try {
//                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(
//                        new File(filePathOnService + file.getOriginalFilename())));
//                out.write(file.getBytes());
//                out.flush();
//                out.close();
//            } catch (FileNotFoundException e) {
//                logger.warn(e);
//                return new ResponseEntity(RespCode.WARN, e.getMessage());
//            } catch (IOException eIO) {
//                logger.warn(eIO);
//                return new ResponseEntity(RespCode.WARN, eIO.getMessage());
//            }
//            return new ResponseEntity(RespCode.SUCCESS);
//        } else {
//            return new ResponseEntity(RespCode.EXCEPTION);
//        }
//    }

    @Override
    public ResponseEntity addStore(String json, MultipartFile[] photos) {
        if (null != json) {
            try {
                logger.info("addStore is here : 1 ");
                JSONObject storeJsonObject = JSONObject.parseObject(json);
                StoreInfo storeInfo = JSONObject.parseObject(json, StoreInfo.class);
                logger.info("addStore is here : 1.1 ");
                String userId = storeInfo.getOwnerId();
                logger.info("addStore is here : 1.2 ");
                String storeId = storeInfo.getId();
                logger.info("addStore is here : 1.3 ");
                StorePhotoInfo storePhotoInfo = storeInfo.getStorePhotoInfo();
                logger.info("addStore is here : 2 ");
                ArrayList<Photo> photoInfoList = new ArrayList<>();
//                判断file数组不能为空并且长度大于0
                if (photos != null && photos.length > 0) {
                    logger.info("addStore is here : 2.1 ");
                    //循环获取file数组中得文件
                    for (int i = 0; i < photos.length; i++) {
                        logger.info("addStore is here : 3 ");
                        MultipartFile file = photos[i];
                        logger.info("addStore is here : 3.1 ");
                        //将不同storeID的文件放入不同的以storeID命名的文件夹下；
                        String filePath = creatPhotoFilePath(userId, storeId)
                                + file.getOriginalFilename();

                        logger.info("filePath is " + filePath);
                        // 转存文件
                        try {
                            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(filePath)));

                            logger.info("addStore is here : 3.2 ");
                            out.write(file.getBytes());
                            logger.info("addStore is here : 3.3 ");
                            out.flush();
                            out.close();
                            logger.info("addStore is here : 3.4 ");

                        } catch (FileNotFoundException e) {
                            logger.warn(e);
                            return new ResponseEntity(RespCode.WARN, e.getMessage());
                        } catch (IOException eIO) {
                            logger.warn(eIO);
                            return new ResponseEntity(RespCode.WARN, eIO.getMessage());
                        }
                        logger.info("addStore is here : 4");
                        Photo photoUse = new Photo();
                        logger.info("addStore is here : 5" + i);
                        ArrayList<String> photoPathList = new ArrayList<>();
                        photoPathList.add(filePath);
                        photoUse.setOwnerId(storeId);
                        photoUse.setOwnerClass("D");
                        photoUse.setPathList(photoPathList);
                        photoInfoList.add(photoUse);
                        logger.info("addStore is here : 6" + i);
                    }
                    storeInfo.setStoreAllInfoId();
                    //    StoreAddress	地址信息信息
                    AddressInfo addressInfo = storeInfo.getAddressInfo();
                    mongoTemplate.save(addressInfo);
                    //    StoreTeamInfo	组局信息
                    StoreTeamInfo storeTeamInfo = storeInfo.getStoreTeamInfo();
                    mongoTemplate.save(storeTeamInfo);
                    //    StoreShoppingInfo	消费信息
                    StoreShoppingInfo storeShoppingInfo = storeInfo.getStoreShoppingInfo();
                    mongoTemplate.save(storeShoppingInfo);
                    //    StoreEvaluationInfo	评价信息
                    StoreEvaluationInfo storeEvaluationInfo = storeInfo.getStoreEvaluationInfo();
                    mongoTemplate.save(storeEvaluationInfo);
                    //    StorePackageInfo	套餐信息
                    StorePackageInfo storePackageInfo = storeInfo.getStorePackageInfo();
                    mongoTemplate.save(storePackageInfo);
                    //    StoreHardwareInfo	环境信息
                    StoreHardwareInfo storeHardwareInfo = storeInfo.getStoreHardwareInfo();
                    mongoTemplate.save(storeHardwareInfo);
                    //    StoreEquipmentInfo	设备信息
                    StoreEquipmentInfo storeEquipmentInfo = storeInfo.getStoreEquipmentInfo();
                    mongoTemplate.save(storeEquipmentInfo);
                    //    StoreIntegrationInfo	积分信息
                    StoreIntegrationInfo storeIntegrationInfo = storeInfo.getStoreIntegrationInfo();
                    mongoTemplate.save(storeIntegrationInfo);
                    //    StoreBusinessInfo	运营信息
                    StoreBusinessInfo storeBusinessInfo = storeInfo.getStoreBusinessInfo();
                    mongoTemplate.save(storeBusinessInfo);
                    //    StorePhotoInfo	图片信息
                    storePhotoInfo.setStoreAllPhotos(photoInfoList);
                    mongoTemplate.save(storePhotoInfo);
                    //    StoreRankInfo  排名信息
                    StoreRankInfo storeRankInfo = storeInfo.getStoreRankInfo();
                    mongoTemplate.save(storeRankInfo);

                    storeInfo.setUpTime(dateUse.DateToString(new Date()));
                    storeInfo.setCreaterId(storeInfo.getOwnerId());
                    storeInfo.setCreateTime(dateUse.DateToString(new Date()));
                    storeInfo.setState("S");
                    storeInfo.setDeleteStyle(false);

                    mongoTemplate.save(storeInfo);
                    Store store = storeInfo;
                    storeMapper.insert(store);

                    return new ResponseEntity(RespCode.SUCCESS, storeInfo);
                } else {
                    return new ResponseEntity(RespCode.STOREFILEWRONG, json);
                }
            } catch (Exception e) {
                return new ResponseEntity(RespCode.EXCEPTION, e);
            }
        } else {
            return new ResponseEntity(RespCode.STOREWRONG, json);
        }
    }

    //    通过某个城市的code获取该城市所有的StoreInfo信息List；
    public List<StoreInfo> getCityStoreListByCityCode(String cityCode) {
        Criteria criteria = Criteria.where("addressInfo.citycode").is(cityCode);
        try {
            return mongoTemplate.find(Query.query(criteria), StoreInfo.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public ResponseEntity getStoreInfoByUserId(String userId) {
//        通过用户id查询用户的基本数据，获得用户的城市信息city 和 经纬度
        User user = userMapper.selectByPrimaryKey(userId);
        AddressInfo userAddress = mongoTemplate.findById(user.getMyAddressId(), AddressInfo.class);
        String userCityCode = userAddress.getCitycode();
        Double userX = userAddress.getLatitude(), userY = userAddress.getLongitude();
//        获取同用户所在城市的所有商铺的信息，并存入redis，保留id、经纬度；
        List<StoreInfo> storeInfosList = getCityStoreListByCityCode("0755");
//        计算在城市中所有商铺距离用户的距离，排序；
        StoreInfo storeInfoUse = new StoreInfo();
        for (int i = 0; i < storeInfosList.size(); i++) {
            storeInfoUse = storeInfosList.get(i);
            storeInfoUse.getAddressInfo().setDistance(DistanceFunc.getDistance
                    (userX, userY, storeInfoUse.getAddressInfo().getLatitude(), storeInfoUse.getAddressInfo().getLongitude()));
        }
        Collections.sort(storeInfosList, new Comparator<StoreInfo>() {
            @Override
            public int compare(StoreInfo o1, StoreInfo o2) {
//                    从小到大
                return (int) (o1.getAddressInfo().getDistance() - o2.getAddressInfo().getDistance());
//                    从大到小
//                return (int)(o2.getDistance() - o1.getDistance());
            }
        });
//        返回数据给用户前端；
        return new ResponseEntity(RespCode.SUCCESS, storeInfosList);
//        return null;
    }

}
