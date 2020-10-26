package com.devteamvietnam.vuebackend.entity.base;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "user_preference")
@Data
public class UserPreferenceEntity {
	@Id
	@GeneratedValue
	private UUID id; 

	@EqualsAndHashCode.Exclude 
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;
	
	private String preferenceType;
	
	private String preferenceData;
	
}