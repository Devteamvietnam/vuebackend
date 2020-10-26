package com.devteamvietnam.vuebackend.controller.base;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devteamvietnam.vuebackend.config.GeneralConfig;
import com.devteamvietnam.vuebackend.service.base.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;



@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/")
public class AppController {

    private static final Logger log = LoggerFactory.getLogger(AppController.class);


    public static String applicationInstanceName = "Unistars back-end API";

    @Autowired GeneralConfig config;
    @Autowired UserService userService;


    @PostConstruct
	public void init() throws JsonProcessingException {
    	log.info("Initialize data");
    	
    	userService.init();

		
    	if(config.DUMMY_DATA_ENABLED || config.BYPASS_AUTHENTICATION) {
    		userService.initDummyData();

    	}
	}	
   
    
}