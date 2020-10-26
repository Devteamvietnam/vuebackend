package com.devteamvietnam.vuebackend.repository.base;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devteamvietnam.vuebackend.entity.base.UserEntity;


@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

	boolean existsUserEntityByUsername(String username);
	
	UserEntity findByUsernameAndPassword(String username, String password);
	
	UserEntity findByUsername(String username);
	
}
