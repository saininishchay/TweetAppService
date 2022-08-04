package com.tweetapp.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.tweetapp.model.User;

@Repository
public interface UserRepository extends MongoRepository<User, Long> {

	@Query("{ userName : ?0,password: ?1 }")
	Optional<User> findByUserNameAndPassword(String userName, String password);

	@Query("{ emailId : ?0}")
	Optional<User> findByEmailIdName(String emailId);

	@Query("{ userName : ?0}")
	Optional<User> findByUserName(String userName);

}
