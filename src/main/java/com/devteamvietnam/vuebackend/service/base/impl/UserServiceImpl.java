package com.devteamvietnam.vuebackend.service.base.impl;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devteamvietnam.vuebackend.config.GeneralConfig;
import com.devteamvietnam.vuebackend.config.MailConfig;
import com.devteamvietnam.vuebackend.converter.base.UserConverter;
import com.devteamvietnam.vuebackend.dto.base.Mail;
import com.devteamvietnam.vuebackend.dto.base.User;
import com.devteamvietnam.vuebackend.dto.base.UserPin;
import com.devteamvietnam.vuebackend.entity.base.ERole;
import com.devteamvietnam.vuebackend.entity.base.UserEntity;
import com.devteamvietnam.vuebackend.entity.base.UserImageEntity;
import com.devteamvietnam.vuebackend.entity.base.UserPreferenceEntity;
import com.devteamvietnam.vuebackend.entity.base.UserRoleEntity;
import com.devteamvietnam.vuebackend.exceptionhandler.AppException;
import com.devteamvietnam.vuebackend.exceptionhandler.AppExceptionCode;
import com.devteamvietnam.vuebackend.repository.base.RoleRepository;
import com.devteamvietnam.vuebackend.repository.base.UserImageRepository;
import com.devteamvietnam.vuebackend.repository.base.UserPreferenceRepository;
import com.devteamvietnam.vuebackend.repository.base.UserRepository;
import com.devteamvietnam.vuebackend.service.base.MailService;
import com.devteamvietnam.vuebackend.service.base.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
@Transactional
@Qualifier("userService")
public class UserServiceImpl implements UserService {

	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	UserPreferenceRepository preferenceRepo;
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	UserImageRepository userImageRepo;
	
	@Autowired
	RoleRepository roleRepo;
	
	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	private MailConfig mailConfig;

	@Autowired
	private MailService mailService;
	
	Map<String, UserPin> userIdAndPinMap = new ConcurrentHashMap<String, UserPin>();
	
	@Autowired
	private GeneralConfig config;

	@Override
	public boolean sendVerificationCode(String username) {	
	
		
		User u = new User();
		u.setFullname("New User");
		
		UserEntity en = userRepo.findByUsername(username);
		if(en!=null) {
			u = UserConverter.getInstance().entityToDto(en);
		}
		
		try {
			//try removing old pins
			removeOldPins();
			//reuse pin or create a new one
			UserPin code =null;
			if(userIdAndPinMap.containsKey(username)) {
				code = userIdAndPinMap.get(username);
				code.setAttempts(code.getAttempts()+1);
			} else {
				code = UserPin.getOne();
				userIdAndPinMap.put(username, code);
			}
			// limit to 5 attempts to protect our server from spamming
			if(code.getAttempts()<5) {
				
				//TODO: check if username is an email or a phone number
				//we currently support registration by email
				sendCodeByEmail(username, u.getFullname(), code);
			}			
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}

		
	}

	private void sendCodeByEmail(String email, String fullName, UserPin code) {
		Mail mail = new Mail();
		mail.setMailFrom(mailConfig.EMAIL_SMTP_FROM_ADDRESS);
		mail.setMailTo(email);
		mail.setMailSubject(mailConfig.EMAIL_SUBJECT);

		// TODO: edit the template and add placeholder's values here
		// hard-coded b/c the model of the content is least likely to be changed so
		// often
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("name", fullName);
		model.put("code", code.getPin());
		model.put("location", "DevteamVN");
		model.put("signature", "https://devteamvietnam.com");
		mail.setModel(model);

		mailService.sendEmail(mail);
	}

	@Override
	public UserEntity find(String username, String password) {
		UserEntity u = userRepo.findByUsernameAndPassword(username, password);
		if(u!=null) {
			return userRepo.findByUsernameAndPassword(username, password);
		} else {
			return null;
		}
	}

	@Override
	public long count() {
		return userRepo.count();
	}

	@Override
	public boolean exists(String username) {
		return userRepo.existsUserEntityByUsername(username);
	}

	@Override
	public List<UserEntity> findAll() {
		return userRepo.findAll();
	}

	@Override
	public UserEntity findOneById(String id) {
		Optional<UserEntity> userEntity = userRepo.findById(UUID.fromString(id));
		if(userEntity.isPresent()) {
			return userEntity.get();
		} else {
			return null;
		}
	}

	@Override
	public UserEntity save(User dto) {		
		UserEntity user = UserConverter.getInstance().dtoToEntity(dto);
		String pwd = encoder.encode(dto.getPassword());
		user.setPassword(pwd);
		user.setUserRoles(getRoles(dto.getRoles()));
		UserEntity uEn = userRepo.save(user);
		return uEn;
	}

	private Set<UserRoleEntity> getRoles(Set<String> strRoles) {
		Set<UserRoleEntity> roles = new HashSet<UserRoleEntity>();

		if (strRoles == null) {
			UserRoleEntity userRole = roleRepo.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					UserRoleEntity adminRole = roleRepo.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(adminRole);

					break;
				case "mod":
					UserRoleEntity modRole = roleRepo.findByName(ERole.ROLE_MODERATOR)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(modRole);

					break;
				default:
					UserRoleEntity userRole = roleRepo.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(userRole);
				}
			});
		}
		return roles;
	}

	@Override
	public void delete(String id) {
		userRepo.deleteById(UUID.fromString(id));
	}

	@Override
	public void deleteAll() {
		userRepo.deleteAll();
	}

	private void removeOldPins() {
		Predicate<UserPin> isOld = p -> !UserPin.isValid(p);
		userIdAndPinMap.values().removeIf(isOld);
	}

	@Override
	public UserEntity register(User user, String code) throws AppException {
		
		UserPin pin = userIdAndPinMap.get(user.getUsername());
		if(pin!=null) {
			if(pin.isValid() && pin.getPin().equals(code)) {
				userIdAndPinMap.remove(user.getUsername());
				//override role, do not expose admin role for new registered user
				user.setRoles(new HashSet<String>(Arrays.asList("user")));
				return save(user);
			} else {
				if(!pin.isValid()) {
					userIdAndPinMap.remove(user.getUsername());
				}
				throw AppExceptionCode.USER_PIN_INVALID_400_4002;
			}
			
		} else {
			throw AppExceptionCode.USER_PIN_NOTFOUND_400_4001;
		}
	}

	@Override
	public UserEntity resetPassword(String username, String code, String password) throws AppException {
		UserPin pin = userIdAndPinMap.get(username);
		if(pin!=null) {
			if(pin.isValid() && pin.getPin().equals(code)) {
				UserEntity u = userRepo.findByUsername(username);
				String pwd = encoder.encode(password);
				u.setPassword(pwd);
				return userRepo.save(u);
			} else {
				if(!pin.isValid()) {
					userIdAndPinMap.remove(username);
				}
				throw AppExceptionCode.USER_PIN_INVALID_400_4002;
			}
			
		} else {
			throw AppExceptionCode.USER_PIN_NOTFOUND_400_4001;
		}
	}

	
	@Override
	public boolean checkPIN(String userId, String code) {
		UserPin pin = userIdAndPinMap.get(userId);
		if(pin!=null) {
			if(pin.isValid() && pin.getPin().equals(code)) {
				return true;
			} else {
				if(!pin.isValid()) {
					userIdAndPinMap.remove(userId);
				}
				return false;
			}
			
		} else {
			return false;
		}
	}


	@Override
	public UserEntity find(String username) {
		return userRepo.findByUsername(username);
	}


	@Override
	public Optional<UserImageEntity> getUserImage(String fileId) {
		return userImageRepo.findById(UUID.fromString(fileId));
	}


	@Override
	public UserImageEntity saveUserImage(UserImageEntity img) {
		return userImageRepo.save(img);
	}

	public UserPreferenceEntity getUserPreference(String type, String userId){
		return preferenceRepo.findByUserIdAndPreferenceType(UUID.fromString(userId), type);
	}
	
	public List<UserPreferenceEntity> getUserPreference(String userId){
		return preferenceRepo.findByUserId(UUID.fromString(userId));
	}
	
	public UserPreferenceEntity savePreference(UserPreferenceEntity en) {
		return preferenceRepo.save(en);
	}
	
	@Override
	public void initDummyData() throws JsonProcessingException {
		
		User dto = new User();
		dto.setUsername("tester@devteamvietnam.com");
		dto.setEmail("tester@devteamvietnam.com");
		dto.setPassword("devteam@tester");
		dto.setFullname("User tester");
		dto.setFirstname("tester");
		dto.setLastname("User");
		dto.setAddress("Tan Binh District");
		dto.setBirthdate("22/06/1997");
		dto.setCity("HCM");
		dto.setGender("Male");
		dto.setPhonenumber("0907777777");
		dto.setJob("Manager");
		dto.setRoles(new HashSet<String>(Arrays.asList("user")));
		userIdAndPinMap.put(dto.getUsername(), new UserPin("999999", System.currentTimeMillis(), 1));
		save(dto);
		
		//registered for the test
		//UserControllerTest.testResetPassword_whenExistsAndPinValid_thenOk()
		userIdAndPinMap.put("dummy@gmail.com", new UserPin("888888", System.currentTimeMillis(), 1));
		
		
		//register user preference
		UserEntity user1 = userRepo.findByUsername("ivanlucas@devteamvietnam.com");
		UserEntity user2 = userRepo.findByUsername("tester@devteamvietnam.com");
				
	}

	@Override
	public void init() {
		if(roleRepo.count()==0) {
			UserRoleEntity role = new UserRoleEntity();		
			role = new UserRoleEntity();
			role.setName(ERole.ROLE_USER);
			roleRepo.save(role);
			role = new UserRoleEntity();
			role.setName(ERole.ROLE_ADMIN);
			roleRepo.save(role);
			role = new UserRoleEntity();
			role.setName(ERole.ROLE_MODERATOR);
			roleRepo.save(role);
		}
		
		User dto = new User();
		dto.setUsername(config.USERNAME);
		dto.setPassword(config.PASSWORD);
		dto.setEmail("devteamvietnam@gmail.com");
		dto.setFullname("User admin");
		dto.setFirstname("admin");
		dto.setLastname("User");
		dto.setAddress("Tan Binh District");
		dto.setBirthdate("22/06/1997");
		dto.setCity("HCM");
		dto.setGender("Male");
		dto.setPhonenumber("0907777777");
		dto.setJob("Manager");
		dto.setRoles(new HashSet<String>(Arrays.asList("user", "admin")));
		userIdAndPinMap.put(dto.getUsername(), new UserPin("999999", System.currentTimeMillis(), 1));
		save(dto);

	}

}
