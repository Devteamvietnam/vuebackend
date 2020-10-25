package com.devteamvietnam.vuebackend.entity.base;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

@Data
@Entity
@Table(name = "role")
public class UserRoleEntity {
	
	@Id
	@GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
	private String id;
	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private ERole name;
}
