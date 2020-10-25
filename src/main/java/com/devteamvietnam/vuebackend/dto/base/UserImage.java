package com.devteamvietnam.vuebackend.dto.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@JsonInclude(Include.NON_NULL)
@Data
public class UserImage {

	private String id;
	private String fileName;
	private String fileType;
}
