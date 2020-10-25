package com.devteamvietnam.vuebackend.entity.base;

import javax.persistence.*;


import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "user_preference")
@Data
public class UserPreferenceEntity {
	
	@Id
	@GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
	private String id;

	@EqualsAndHashCode.Exclude 
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;
	
	private String preferenceType;
	
	private String preferenceData;
	
}