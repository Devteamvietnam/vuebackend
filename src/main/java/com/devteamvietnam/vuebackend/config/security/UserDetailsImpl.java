package com.devteamvietnam.vuebackend.config.security;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.devteamvietnam.vuebackend.converter.base.UserConverter;
import com.devteamvietnam.vuebackend.dto.base.User;
import com.devteamvietnam.vuebackend.entity.base.UserEntity;


public class UserDetailsImpl implements UserDetails {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -4951721180071337084L;

	private User user;

	private Collection<? extends GrantedAuthority> authorities;

	public UserDetailsImpl(User user,
			Collection<? extends GrantedAuthority> authorities) {
		this.authorities = authorities;
		this.user = user;
	}

	public static UserDetailsImpl build(UserEntity user) {
		List<GrantedAuthority> authorities = user.getUserRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getName().name()))
				.collect(Collectors.toList());

		return new UserDetailsImpl(UserConverter.getInstance().entityToDto(user), authorities);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserDetailsImpl u = (UserDetailsImpl) o;
		return Objects.equals(u.getId(), getUser().getId());
	}

	@Override
	public String getPassword() {
		return getUser().getPassword();
	}

	@Override
	public String getUsername() {
		return getUser().getUsername();
	}

	public String getId() {
		return getUser().getId();
	}

	public User getUser() {
		return user;
	}

	public String getFullname() {
		return getUser().getFullname();
	}
}