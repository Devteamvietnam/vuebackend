package com.devteamvietnam.vuebackend.converter;

import java.util.List;
import java.util.stream.Collectors;

import com.devteamvietnam.vuebackend.dto.base.User;
import com.devteamvietnam.vuebackend.entity.base.ERole;
import com.devteamvietnam.vuebackend.entity.base.UserEntity;

public class UserConverter extends BaseConverter {
	
	private static UserConverter instance; 

	public static UserConverter getInstance() {
		if(instance==null) {
			instance = new UserConverter();
		}
		return instance;
	}

	public UserConverter() {}

	public UserEntity dtoToEntity(User dto) {
		return map(dto, UserEntity.class);
	}

	public User entityToDto(UserEntity e) {
		User u = map(e, User.class); 
		u.getRoles().clear();
		e.getUserRoles().stream().forEach(i -> {
			if(i.getName() == ERole.ROLE_ADMIN) {
				u.getRoles().add("admin");
			} else if(i.getName() == ERole.ROLE_MODERATOR) {
				u.getRoles().add("mod");
			} else {
				u.getRoles().add("user");
			}
		});
		
		
		
		
		return u;
	}

	public List<UserEntity> dtoToEntityList(List<User> list){
		return list.stream().map(i -> { return dtoToEntity(i); }).collect(Collectors.toList());	
	}
	
	public List<User> entityToDtoList(List<UserEntity> list){
		return list.stream().map(i -> { return entityToDto(i); }).collect(Collectors.toList());	
	}

}
