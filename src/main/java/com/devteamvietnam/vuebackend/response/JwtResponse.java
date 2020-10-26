package com.devteamvietnam.vuebackend.response;

import com.devteamvietnam.vuebackend.dto.base.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
	private String accessToken;
	private String tokenType = "Bearer";
	private User user;
}
