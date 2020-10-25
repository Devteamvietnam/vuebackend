package com.devteamvietnam.vuebackend.dto.base;

import lombok.Data;

@Data
public class UserPreference {
	
	String id;
	
	private String preferenceType;
	
	private String preferenceData;
	
}
