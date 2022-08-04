package com.tweetapp.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tweetapp.configuration.KafkaProducer;
import com.tweetapp.exception.TweetAppException;
import com.tweetapp.model.User;
import com.tweetapp.repository.UserRepository;
import com.tweetapp.util.Envelope;
import com.tweetapp.util.TweetConstant;

@Service
public class UserService {

	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	@Autowired
	UserRepository userRepository;

	@Autowired
	MongoOperations mongoperation;

	@Autowired
	private KafkaProducer producer;

	public ResponseEntity<Envelope<String>> register(User user) {
		log.info(TweetConstant.IN_REQUEST_LOG, "register", user.toString());
		Optional<User> emailId = userRepository.findByEmailIdName(user.getEmailId());
		Optional<User> userName = userRepository.findByUserName(user.getUserName());
		String userRegister = emailId.isPresent() || userName.isPresent() ? TweetConstant.USER_NAME_ALREADY_EXIST
				: TweetConstant.USER_NAME_REGISTERED_SUCCESSFULLY;
		log.info(TweetConstant.EXITING_RESPONSE_LOG, "register", userRegister);
		if (emailId.isPresent()) {
			throw new TweetAppException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST,
					"User already present with the same Email Id");
		} else if (userName.isPresent()) {
			throw new TweetAppException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST,
					"User already present with the same Username");
		}
		userRepository.save(user);
		return ResponseEntity
				.ok(new Envelope<String>(HttpStatus.OK.value(), HttpStatus.OK, user.getEmailId() + " " + userRegister));
	}

	public ResponseEntity<Envelope<String>> login(String userName, String password) throws TweetAppException {
		log.info(TweetConstant.IN_REQUEST_LOG, "login", userName.concat(" " + password));
		Optional<User> isValid = userRepository.findByUserNameAndPassword(userName, password);
		String userValid = isValid.isPresent() ? TweetConstant.LOGIN_SUCCESS : TweetConstant.LOGIN_FAILED;
		log.info(TweetConstant.EXITING_RESPONSE_LOG, "login", userValid);
		if (!isValid.isPresent())
			throw new TweetAppException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, userValid);
		return ResponseEntity.ok(new Envelope<String>(HttpStatus.OK.value(), HttpStatus.OK, userValid));
	}

	public ResponseEntity<Envelope<String>> forgotPassword(String userName, String password) {
		log.info(TweetConstant.IN_REQUEST_LOG, "forgotPassword", userName.concat(" " + password));
		Optional<User> findByUserName = userRepository.findByUserName(userName);
		if (!findByUserName.isPresent()) {
			log.info(TweetConstant.EXITING_RESPONSE_LOG, "forgotPassword", TweetConstant.NO_USERS_FOUND);
			throw new TweetAppException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST,
					TweetConstant.USER_NAME_NOT_PRESENT);
		}
		producer.sendMessage("Forgot Password for :: " + userName.concat(" password: " + password));
		Query query = new Query();
		query.addCriteria(Criteria.where(TweetConstant.USER_NAME).is(userName));

		Update update = new Update();
		update.set(TweetConstant.PASSWORD, password);

		User user = mongoperation.findAndModify(query, update, User.class);

		if (user == null)
			throw new TweetAppException(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR,
					TweetConstant.ERROR_WHILE_UPDATING_PASSWORD);
		log.info(TweetConstant.EXITING_RESPONSE_LOG, "forgotPassword", "password updated for " + user.toString());
		return ResponseEntity.ok(new Envelope<>(HttpStatus.OK.value(), HttpStatus.OK, TweetConstant.PASSWORD_UPDATED));
	}

	public ResponseEntity<Envelope<List<User>>> getAllusers() {
		log.info(TweetConstant.IN_REQUEST_LOG, "getAllusers", "Getting All Users");
		List<User> findAll = userRepository.findAll();
		log.debug("allUsers from list {}", findAll);
		if (findAll.isEmpty()) {
			log.debug(TweetConstant.NO_USERS_FOUND);
			throw new TweetAppException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST,
					TweetConstant.NO_USERS_FOUND);
		}
		return ResponseEntity.ok(new Envelope<>(HttpStatus.OK.value(), HttpStatus.OK, findAll));
	}

	public ResponseEntity<Envelope<User>> username(String userName) {
		log.info(TweetConstant.IN_REQUEST_LOG, "username", userName);
		Optional<User> userPresent = userRepository.findByUserName(userName);
		log.info(TweetConstant.EXITING_RESPONSE_LOG, "username",
				userPresent.isPresent() ? "Present " + userPresent.get() : "User Not Present");
		if (!userPresent.isPresent())
			throw new TweetAppException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, "userName not Present");
		return ResponseEntity.ok(new Envelope(HttpStatus.OK.value(), HttpStatus.OK, userPresent));
	}

}
