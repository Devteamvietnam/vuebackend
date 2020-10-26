package com.devteamvietnam.vuebackend.entity.base;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "user_image")
@Data
public class UserImageEntity {

	@Id
	@GeneratedValue
	private UUID id;	
	private String fileName;
	private String fileType;
	
	@EqualsAndHashCode.Exclude 
	@Lob
	private byte[] data;
	
	@EqualsAndHashCode.Exclude 
	@OneToOne
	@JoinColumn(name="user_id", nullable = false)
	UserEntity user; 
}