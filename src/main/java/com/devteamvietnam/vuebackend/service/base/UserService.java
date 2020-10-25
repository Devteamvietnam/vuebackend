package com.devteamvietnam.vuebackend.service.base;

import java.util.List;
import java.util.Optional;

import com.devteamvietnam.vuebackend.dto.base.User;
import com.devteamvietnam.vuebackend.entity.base.UserEntity;
import com.devteamvietnam.vuebackend.entity.base.UserImageEntity;
import com.devteamvietnam.vuebackend.entity.base.UserPreferenceEntity;
import com.devteamvietnam.vuebackend.exceptionhandler.AppException;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface UserService extends CRUDOperationService<UserEntity, User> {
	
	//check if the user has already registered
	boolean exists(String username);
	
	//send verification code
	boolean sendVerificationCode(String username);
	
	//log the user in with a password
	UserEntity find(String username, String password);
	
	UserEntity find(String username);
	// register
	UserEntity register(User user, String pin) throws AppException;
// reset
	UserEntity resetPassword(String username, String pin, String password) throws AppException;
	// checkpin
	boolean checkPIN(String username, String pin) throws AppException;
	
	Optional<UserImageEntity> getUserImage(String fileId);

	UserImageEntity saveUserImage(UserImageEntity img);
	

	void init();
	
	public UserPreferenceEntity getUserPreference(String type, String userId);
	
	public List<UserPreferenceEntity> getUserPreference(String userId);
	
	public UserPreferenceEntity savePreference(UserPreferenceEntity en);
}