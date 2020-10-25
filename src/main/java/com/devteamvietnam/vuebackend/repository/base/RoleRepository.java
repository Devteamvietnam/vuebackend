package com.devteamvietnam.vuebackend.repository.base;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devteamvietnam.vuebackend.entity.base.ERole;
import com.devteamvietnam.vuebackend.entity.base.UserRoleEntity;

@Repository
public interface RoleRepository extends JpaRepository<UserRoleEntity, String> {
	Optional<UserRoleEntity> findByName(ERole name);
}