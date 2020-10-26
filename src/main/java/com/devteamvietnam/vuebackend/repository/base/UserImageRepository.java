package com.devteamvietnam.vuebackend.repository.base;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devteamvietnam.vuebackend.entity.base.UserImageEntity;


@Repository
public interface UserImageRepository extends JpaRepository<UserImageEntity, UUID> {}
