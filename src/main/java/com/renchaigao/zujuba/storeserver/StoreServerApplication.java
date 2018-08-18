package com.renchaigao.zujuba.storeserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAutoConfiguration
@MapperScan("com.renchaigao.zujuba.dao.mapper")
@EnableScheduling
public class StoreServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(StoreServerApplication.class, args);
	}
}
