package com.devteamvietnam.vuebackend.entity.base;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "\"user\"")
@Data
public class UserEntity {

    @Id
    @GeneratedValue
    private UUID id;
    private String username;
	private String email;
	private String password;
	private String fullname;
	private String firstname;
	private String lastname;
	private String phonenumber;
	private String gender;
	private String address;
	private String city;
	private String job;
	private String birthdate;
	
	
	@EqualsAndHashCode.Exclude 
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(	name = "user_role", 
				joinColumns = @JoinColumn(name = "user_id"), 
				inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<UserRoleEntity> userRoles = new HashSet<UserRoleEntity>();
	
	@EqualsAndHashCode.Exclude 
	@OneToOne(mappedBy = "user",cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private UserImageEntity image;
	
	@EqualsAndHashCode.Exclude 
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<UserPreferenceEntity> preferences = new HashSet<UserPreferenceEntity>();
}
