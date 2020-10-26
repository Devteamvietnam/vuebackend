package com.devteamvietnam.vuebackend.controller.base;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.devteamvietnam.vuebackend.config.GeneralConfig;
import com.devteamvietnam.vuebackend.config.security.JwtUtils;
import com.devteamvietnam.vuebackend.config.security.UserDetailsImpl;
import com.devteamvietnam.vuebackend.converter.base.UserConverter;
import com.devteamvietnam.vuebackend.dto.base.LoginRequest;
import com.devteamvietnam.vuebackend.dto.base.User;
import com.devteamvietnam.vuebackend.entity.base.UserEntity;
import com.devteamvietnam.vuebackend.entity.base.UserImageEntity;
import com.devteamvietnam.vuebackend.exceptionhandler.AppException;
import com.devteamvietnam.vuebackend.exceptionhandler.AppExceptionCode;
import com.devteamvietnam.vuebackend.response.JwtResponse;
import com.devteamvietnam.vuebackend.response.MessageResponse;
import com.devteamvietnam.vuebackend.service.base.UserService;



@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

	@Autowired
	GeneralConfig config;
	
	public static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	@Qualifier("userService")
	UserService service;
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	JwtUtils jwtUtils;

	@RequestMapping(value = "/register/{pin}", method = RequestMethod.POST)
	public ResponseEntity<?> register(@PathVariable("pin") String pin, @RequestBody User user, UriComponentsBuilder ucBuilder) throws AppException {
		logger.info("Creating user : {}", user);
		if(!service.exists(user.getEmail())) {
			UserEntity result = service.register(user, pin);
			if(result!=null) {
				return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
			} else {
				throw AppExceptionCode.USER_REGISTRATION_FAILED_500_4006;
			}
		} else {
			throw AppExceptionCode.USER_ALREADY_REGISTERED_400_4000;
		}
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<?> register(@RequestBody User user, UriComponentsBuilder ucBuilder) throws AppException {
		logger.info("Creating user : {}", user);
		if(!service.exists(user.getEmail())) {
			UserEntity result = service.register(user, user.getPin());
			if(result!=null) {
				return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
			} else {
				throw AppExceptionCode.USER_REGISTRATION_FAILED_500_4006;
			}
		} else {
			throw AppExceptionCode.USER_ALREADY_REGISTERED_400_4000;
		}
	}
	
	@RequestMapping(value = "/registerWithUserImage/{pin}", method = RequestMethod.POST)
	public ResponseEntity<?> registerWithImage(@PathVariable("pin") String pin, @Valid @RequestPart("data") User user, @RequestPart("file") MultipartFile file) throws AppException {
		logger.info("Creating user : {}", user);
		if(!service.exists(user.getEmail())) {
			
			UserEntity result = service.register(user, pin);
			String fileName = StringUtils.cleanPath(file.getOriginalFilename());
			try {
				UserImageEntity img = new UserImageEntity();   
				img.setFileName(fileName);
				img.setFileType(file.getContentType());
				img.setData(file.getBytes());
				img.setUser(result);
				service.saveUserImage(img);
			} catch(Exception ex) {
				throw AppExceptionCode.COMMON_FILE_IOEXCEPTION_500_9000.addMessageParams(fileName);
			}

			if(result!=null) {
				return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
			} else {
				throw AppExceptionCode.USER_REGISTRATION_FAILED_500_4006;
			}
		} else {
			throw AppExceptionCode.USER_ALREADY_REGISTERED_400_4000;
		}
	}
	
	@RequestMapping(value = "/verify/{userId}/{pin}", method = RequestMethod.POST)
	public ResponseEntity<?> checkPin(@PathVariable("userId") String userId, @PathVariable("pin") String pin) throws AppException {
		logger.info("Checking user : {}/ pin: {}", userId, pin);	
		boolean result = service.checkPIN(userId, pin);
		if(result) {
			return ResponseEntity.ok(new MessageResponse("PIN is valid!"));
		} else {
			throw AppExceptionCode.USER_PIN_INVALID_400_4002;
		}
	}
	
	@RequestMapping(value = "/verify/{userId}", method = RequestMethod.POST)
	public DeferredResult<ResponseEntity<?>> sendVerificationCode(@PathVariable("userId") String userId) throws AppException {
		logger.info("Request verification code for userId : {}", userId );
		DeferredResult<ResponseEntity<?>> result = new DeferredResult<>();
		new Thread(() -> {

			if(service.sendVerificationCode(userId)) {
				ResponseEntity<?> r = ResponseEntity.ok(new MessageResponse("Verification code sent!"));
				result.setResult(r);
			} else {
				result.setErrorResult(AppExceptionCode.USER_SENDING_VERIFICATION_FAILED_400_4004);
			}
    		
    	}).start();
		
		return result;
		
	}
		
	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) throws AppException {
		logger.info("Authenticating user : {}", loginRequest.getUsername());
		try {

			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

			SecurityContextHolder.getContext().setAuthentication(authentication);
			String jwt = jwtUtils.generateJwtToken(authentication);
			
			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
			List<String> roles = userDetails.getAuthorities().stream()
					.map(item -> item.getAuthority())
					.collect(Collectors.toList());

			return ResponseEntity.ok(new JwtResponse(jwt, 
							userDetails.getUsername(), userDetails.getFullname()));

		} catch(AuthenticationException ex) {
			throw AppExceptionCode.USER_LOGIN_FAILED_400_4003;
		}
	}
	
	@RequestMapping(value = "/resetpassword", method = RequestMethod.POST)
	public ResponseEntity<?> resetPassword(@RequestParam String pin,  @RequestParam String username, @RequestParam String password) throws AppException {
		logger.info("Reset password for : {}", username);
		
		if(service.exists(username)) {
			User result = UserConverter.getInstance().entityToDto(service.resetPassword(username, pin, password));
			if(result!=null) {
				return ResponseEntity.ok(new MessageResponse("Password changed successfully!"));
			} else {
				throw AppExceptionCode.USER_RESET_PASSWORD_FAILED_500_4007;
			}
		} else {
			throw AppExceptionCode.USER_NOTFOUND_400_4005;
		}
	}
	
	@RequestMapping(value = "/viewJPEG/{fileId}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	public void getJPEGImage(HttpServletResponse response, @PathVariable("fileId") String fileId) throws IOException {
		UserImageEntity dbFileEntity = service.getUserImage(fileId).get();
		InputStream imgStream = new ByteArrayInputStream(dbFileEntity.getData());
		response.setContentType(MediaType.IMAGE_JPEG_VALUE);
		StreamUtils.copy(imgStream, response.getOutputStream());
	}

	@RequestMapping(value = "/viewPNG/{fileId}", method = RequestMethod.GET,
			produces = MediaType.IMAGE_PNG_VALUE)
	public void getPNGImage(HttpServletResponse response, @PathVariable("fileId") String fileId) throws IOException {
		UserImageEntity dbFileEntity = service.getUserImage(fileId).get();
		InputStream imgStream = new ByteArrayInputStream(dbFileEntity.getData());
		response.setContentType(MediaType.IMAGE_PNG_VALUE);
		StreamUtils.copy(imgStream, response.getOutputStream());
	}

	@RequestMapping(value = "/viewGIF/{fileId}", method = RequestMethod.GET,
			produces = MediaType.IMAGE_GIF_VALUE)
	public void getGIFImage(HttpServletResponse response, @PathVariable("fileId") String fileId) throws IOException {
		UserImageEntity dbFileEntity = service.getUserImage(fileId).get();
		InputStream imgStream = new ByteArrayInputStream(dbFileEntity.getData());
		response.setContentType(MediaType.IMAGE_GIF_VALUE);
		StreamUtils.copy(imgStream, response.getOutputStream());
	}

}
