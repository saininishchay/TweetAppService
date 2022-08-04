package com.tweetapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import lombok.Generated;

@SpringBootApplication
@EnableMongoRepositories
@Generated
@EnableAspectJAutoProxy
public class TweetAppServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TweetAppServiceApplication.class, args);
	}

}
