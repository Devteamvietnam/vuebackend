package com.devteamvietnam.vuebackend.response;


public class JwtResponse {
	private String token;
	private String username;
	private String fullname;

	public JwtResponse(String accessToken, String username, String fullname) {
		this.token = accessToken;
		this.username = username;
		this.fullname = fullname;
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


	public String getFullname() {
		return fullname;
	}


	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	
}
