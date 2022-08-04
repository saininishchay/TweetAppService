package com.tweetapp.unit.service;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tweetapp.configuration.KafkaProducer;
import com.tweetapp.exception.TweetAppException;
import com.tweetapp.model.Tweet;
import com.tweetapp.model.User;
import com.tweetapp.repository.TweetRepository;
import com.tweetapp.repository.UserRepository;
import com.tweetapp.service.TweetService;
import com.tweetapp.util.Envelope;
import com.tweetapp.util.TweetConstant;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class TweetServiceTest {

	@InjectMocks
	TweetService tweetService;

	@Mock
	UserRepository userRepository;

	@Mock
	TweetRepository tweetRepository;

	@Mock
	MongoOperations mongoperation;

	@Mock
	KafkaProducer kafkaProducer;

	@Test
	void postTweet() {
		Mockito.when(userRepository.findByUserName("test")).thenReturn(Optional.of(new User()));
		ResponseEntity<Envelope<String>> postTweet = tweetService.postTweet("test", "Hello tweet");
		Assertions.assertNotNull(postTweet);
	}

	@Test
	void postTweetUserNameException() {
		Mockito.when(userRepository.findByUserName("test")).thenReturn(Optional.empty());
		TweetAppException exceptionResponse = Assertions.assertThrows(TweetAppException.class,
				() -> tweetService.postTweet("test", "Hello tweet"));
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, exceptionResponse.getStatus());
		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), exceptionResponse.getStatusCode());
		Assertions.assertEquals("userName not Present", exceptionResponse.getData());
	}

	@Test
	void getAllTweets() {
		List<Tweet> tweetList = Arrays.asList(new Tweet(1, "Hello", "test", null, null, null),
				new Tweet(2, "World", "test1", null, null, null));
		Mockito.when(tweetRepository.findAll()).thenReturn(tweetList);
		ResponseEntity<Envelope<List<Tweet>>> allTweet = tweetService.getAllTweet();
		Assertions.assertNotNull(allTweet);
	}

	@Test
	void getAllTweetsThrowsException() {
		TweetAppException exceptionResponse = Assertions.assertThrows(TweetAppException.class,
				() -> tweetService.getAllTweet());
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, exceptionResponse.getStatus());
		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), exceptionResponse.getStatusCode());
		Assertions.assertEquals("No Tweets Found", exceptionResponse.getData());
	}

	@Test
	void getAllUserTweet() {
		Mockito.when(tweetRepository.findByUserName("test")).thenReturn(Arrays.asList(
				new Tweet(1, "test", "Hello", null, null, null), new Tweet(1, "test2", "World", null, null, null)));
		ResponseEntity<Envelope<List<Tweet>>> allUserTweet = tweetService.getAllUserTweet("test");
		Assertions.assertNotNull(allUserTweet);
	}

	@Test
	void getAllUserTweetThrowsException() {
		TweetAppException exceptionResponse = Assertions.assertThrows(TweetAppException.class,
				() -> tweetService.getAllUserTweet("test"));
		Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionResponse.getStatus());
		Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), exceptionResponse.getStatusCode());
		Assertions.assertEquals("No Tweets Found", exceptionResponse.getData());
	}

	@Test
	void updateTweet() {
		Tweet tweet = new Tweet();
		tweet.setTweetId(1);
		tweet.setTweet("Hello");
		Mockito.when(tweetRepository.findById(1)).thenReturn(Optional.of(tweet));
		Mockito.when(userRepository.findByUserName("test")).thenReturn(Optional.of(new User()));
		Query query = new Query();
		query.addCriteria(Criteria.where("userName").is("test"));
		Update update = new Update();
		update.set(TweetConstant.TWEET, "Hello All");
		Mockito.when(mongoperation.findAndModify(query, update, Tweet.class)).thenReturn(new Tweet());
		ResponseEntity<Envelope<String>> updateTweet = tweetService.updateTweet("test", 1, "Hello All");
		Assertions.assertNotNull(updateTweet);
	}

	@Test
	void updateTweetThrowsException() {
		Tweet tweet = new Tweet();
		tweet.setTweetId(1);
		tweet.setTweet("Hello");
		Mockito.when(tweetRepository.findById(1)).thenReturn(Optional.of(tweet));
		Mockito.when(userRepository.findByUserName("test")).thenReturn(Optional.of(new User()));
		Query query = new Query();
		query.addCriteria(Criteria.where("userName").is("test"));
		Update update = new Update();
		update.set(TweetConstant.TWEET, "Hello All");
		Mockito.when(mongoperation.findAndModify(query, update, Tweet.class)).thenReturn(null);
		kafkaProducer.sendMessage("this is a message");
		TweetAppException exceptionResponse = Assertions.assertThrows(TweetAppException.class,
				() -> tweetService.updateTweet("test", 1, "Hello All"));
		Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionResponse.getStatus());
		Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), exceptionResponse.getStatusCode());
		Assertions.assertEquals("Error While Updating Tweet", exceptionResponse.getData());
	}
	
	@Test
	void updateTweetNoUserFoundException() {
		Mockito.when(userRepository.findByUserName("test")).thenReturn(Optional.empty());
		TweetAppException exceptionResponse = Assertions.assertThrows(TweetAppException.class,
				() -> tweetService.updateTweet("test", 1, "Hello All"));
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, exceptionResponse.getStatus());
		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), exceptionResponse.getStatusCode());
		Assertions.assertEquals("userName not Present", exceptionResponse.getData());
	}
	
	@Test
	void updateTweetNoTweetFoundException() {
		Mockito.when(tweetRepository.findById(1)).thenReturn(Optional.empty());
		Mockito.when(userRepository.findByUserName("test")).thenReturn(Optional.of(new User()));
		TweetAppException exceptionResponse = Assertions.assertThrows(TweetAppException.class,
				() -> tweetService.updateTweet("test", 1, "Hello All"));
		Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionResponse.getStatus());
		Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), exceptionResponse.getStatusCode());
		Assertions.assertEquals("No Tweets Found", exceptionResponse.getData());
	}

	@Test
	void deleteTweet() {
		Mockito.when(tweetRepository.findById(1)).thenReturn(Optional.of(new Tweet()));
		Mockito.when(userRepository.findByUserName("test")).thenReturn(Optional.of(new User()));
		ResponseEntity<Envelope<String>> deleteTweet = tweetService.deleteTweet("test", 1);
		Assertions.assertNotNull(deleteTweet);
	}

	@Test
	void replyTweet() {
		Tweet tweet = new Tweet();
		tweet.setTweetId(1);
		tweet.setReplies(null);
		Mockito.when(tweetRepository.findById(1)).thenReturn(Optional.of(tweet));
		Mockito.when(userRepository.findByUserName("test")).thenReturn(Optional.of(new User()));
		Map<String, List<String>> newReplyList = new HashMap<>();
		newReplyList.put("test", Arrays.asList("Hello all"));
		Query query = new Query();
		query.addCriteria(Criteria.where(TweetConstant.TWEET_ID).is(1));
		Update update = new Update();
		update.set("replies", newReplyList);
		Mockito.when(mongoperation.findAndModify(query, update, Tweet.class)).thenReturn(new Tweet());
		ResponseEntity<Envelope<String>> replyTweet = tweetService.replyTweet("test", 1, "Hello all");
		Assertions.assertNotNull(replyTweet);
	}

	@Test
	void replyOldTweet() {
		Map<String, List<String>> oldTweet = new HashMap<>();
		oldTweet.put("test", Arrays.asList("Hi"));
		Mockito.when(tweetRepository.findById(1)).thenReturn(
				Optional.of(new Tweet(1, "test", "reply", new Date(System.currentTimeMillis()), null, oldTweet)));
		Mockito.when(userRepository.findByUserName("test")).thenReturn(Optional.of(new User()));
		Map<String, List<String>> newReplyList = new HashMap<>();
		newReplyList.put("test", Arrays.asList("Hi", "Hello all"));
		Query query = new Query();
		query.addCriteria(Criteria.where(TweetConstant.TWEET_ID).is(1));
		Update update = new Update();
		update.set("replies", newReplyList);
		Mockito.when(mongoperation.findAndModify(query, update, Tweet.class)).thenReturn(new Tweet());
		ResponseEntity<Envelope<String>> replyTweet = tweetService.replyTweet("test", 1, "Hello all");
		Assertions.assertNotNull(replyTweet);
	}

	@Test
	void replyOldTweetWithOtherUserName() {
		Map<String, List<String>> oldTweet = new HashMap<>();
		oldTweet.put("test1", Arrays.asList("Hi"));
		Mockito.when(tweetRepository.findById(1)).thenReturn(
				Optional.of(new Tweet(1, "test", "reply", new Date(System.currentTimeMillis()), null, oldTweet)));
		Mockito.when(userRepository.findByUserName("test")).thenReturn(Optional.of(new User()));
		Map<String, List<String>> newReplyList = new HashMap<>();
		newReplyList.put("test", Arrays.asList("Hello"));
		newReplyList.putAll(oldTweet);
		Query query = new Query();
		query.addCriteria(Criteria.where(TweetConstant.TWEET_ID).is(1));
		Update update = new Update();
		update.set("replies", newReplyList);
		Mockito.when(mongoperation.findAndModify(query, update, Tweet.class)).thenReturn(new Tweet());
		ResponseEntity<Envelope<String>> replyTweet = tweetService.replyTweet("test", 1, "Hello");
		Assertions.assertNotNull(replyTweet);
	}

	@Test
	void replyTweetException() {
		Mockito.when(tweetRepository.findById(1)).thenReturn(Optional.of(new Tweet()));
		Mockito.when(userRepository.findByUserName("test")).thenReturn(Optional.of(new User()));
		TweetAppException exceptionResponse = Assertions.assertThrows(TweetAppException.class,
				() -> tweetService.replyTweet("test", 1, "Hello all"));
		Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionResponse.getStatus());
		Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), exceptionResponse.getStatusCode());
		Assertions.assertEquals("Error While replying", exceptionResponse.getData());

	}

	@Test
	void likeTweetException() {
		Mockito.when(tweetRepository.findById(1)).thenReturn(Optional.of(new Tweet()));
		Mockito.when(userRepository.findByUserName("test")).thenReturn(Optional.of(new User()));
		TweetAppException exceptionResponse = Assertions.assertThrows(TweetAppException.class,
				() -> tweetService.likeTweet("test", 1));
		Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionResponse.getStatus());
		Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), exceptionResponse.getStatusCode());
		Assertions.assertEquals("Error While Liking", exceptionResponse.getData());
	}

	@Test
	void likeTweet() {
		Mockito.when(tweetRepository.findById(1)).thenReturn(Optional.of(new Tweet()));
		Mockito.when(userRepository.findByUserName("test")).thenReturn(Optional.of(new User()));
		Map<String, Integer> updatedLikesMap = new HashMap<>();
		updatedLikesMap.put("test", 1);
		Query query = new Query();
		query.addCriteria(Criteria.where("tweetId").is(1));
		Update update = new Update();
		update.set("likes", updatedLikesMap);
		Mockito.when(mongoperation.findAndModify(query, update, Tweet.class)).thenReturn(new Tweet());
		ResponseEntity<Envelope<String>> likeTweet = tweetService.likeTweet("test", 1);
		Assertions.assertNotNull(likeTweet);
	}

	@Test
	void likeMultipleTweet() {
		Map<String, Integer> oldLikesMap = new HashMap<>();
		oldLikesMap.put("test", 1);
		Mockito.when(tweetRepository.findById(1)).thenReturn(
				Optional.of(new Tweet(1, "test", "like", new Date(System.currentTimeMillis()), oldLikesMap, null)));
		Mockito.when(userRepository.findByUserName("test")).thenReturn(Optional.of(new User()));
		Map<String, Integer> updatedLikesMap = new HashMap<>();
		updatedLikesMap.put("test", 1);
		Query query = new Query();
		query.addCriteria(Criteria.where("tweetId").is(1));
		Update update = new Update();
		update.set("likes", updatedLikesMap);
		Mockito.when(mongoperation.findAndModify(query, update, Tweet.class)).thenReturn(new Tweet());
		ResponseEntity<Envelope<String>> likeTweet = tweetService.likeTweet("test", 1);
		Assertions.assertNotNull(likeTweet);
	}

}
