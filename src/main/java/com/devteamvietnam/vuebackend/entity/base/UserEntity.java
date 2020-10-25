package com.devteamvietnam.vuebackend.entity.base;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "user")
@Data
public class UserEntity {

	@Id
	@GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
	private String id;
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
	
	public UserEntity(String username, String email, String password) {
		this.username = username;
		this.email = email;
		this.password = password;
	}

}
