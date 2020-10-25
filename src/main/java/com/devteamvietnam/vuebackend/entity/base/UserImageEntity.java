package com.devteamvietnam.vuebackend.entity.base;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "user_image")
@Data
public class UserImageEntity {

	@Id
	@GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
	private String id;	
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