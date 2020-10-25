package com.devteamvietnam.vuebackend.repository.base;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devteamvietnam.vuebackend.entity.base.UserPreferenceEntity;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreferenceEntity, String> {

	UserPreferenceEntity findByUserIdAndPreferenceType(String userId, String type);
	List<UserPreferenceEntity> findByUserId(String userId);
	
}
