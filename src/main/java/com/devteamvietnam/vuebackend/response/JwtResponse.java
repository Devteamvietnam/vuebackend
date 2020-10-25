package com.devteamvietnam.vuebackend.response;


public class JwtResponse {
	private String token;
	private String username;

	public JwtResponse(String accessToken, String username) {
		this.token = accessToken;
		this.username = username;
	}


	public String getAccessToken() {
		return token;
	}

	public void setAccessToken(String accessToken) {
		this.token = accessToken;
	}


	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
