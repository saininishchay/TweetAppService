package com.tweetapp.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.tweetapp.model.Tweet;
import com.tweetapp.model.User;
import com.tweetapp.repository.TweetRepository;
import com.tweetapp.repository.UserRepository;
import com.tweetapp.util.Envelope;
import com.tweetapp.util.TweetConstant;

@Service
public class TweetService {

	private static final Logger log = LoggerFactory.getLogger(TweetService.class);

	@Autowired
	TweetRepository tweetRepository;

	@Autowired
	MongoOperations mongoOperations;

	@Autowired
	UserRepository userRepository;

	@Autowired
	private KafkaProducer producer;

	public ResponseEntity<Envelope<String>> postTweet(String userName, String tweetString) {
		log.info(TweetConstant.IN_REQUEST_LOG, "postTweet", tweetString);
		Optional<User> findByUserName = userRepository.findByUserName(userName);
		if (!findByUserName.isPresent()) {
			log.info(TweetConstant.EXITING_RESPONSE_LOG, "postTweet", TweetConstant.USER_NAME_NOT_PRESENT);
			throw new TweetAppException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST,
					TweetConstant.USER_NAME_NOT_PRESENT);
		}
		long count = tweetRepository.count();
		log.info("total tweets " + count);
		Tweet tweet = new Tweet((int) count + 1, userName, tweetString, new Date(System.currentTimeMillis()), null,
				null);
		tweetRepository.save(tweet);
		log.info(TweetConstant.EXITING_RESPONSE_LOG, "postTweet", tweet);
		return ResponseEntity.ok(new Envelope<String>(HttpStatus.OK.value(), HttpStatus.OK, "Saved"));
	}

	public ResponseEntity<Envelope<List<Tweet>>> getAllTweet() {
		log.info(TweetConstant.IN_REQUEST_LOG, "getAllTweet", "getting All Tweets");
		List<Tweet> findAll = tweetRepository.findAll();
		if (findAll.isEmpty())
			throw new TweetAppException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST,
					TweetConstant.NO_TWEETS_FOUND);
		log.info(TweetConstant.EXITING_RESPONSE_LOG, "getAllTweet", findAll);
		return ResponseEntity.ok(new Envelope<List<Tweet>>(HttpStatus.OK.value(), HttpStatus.OK, findAll));
	}

	public ResponseEntity<Envelope<List<Tweet>>> getAllUserTweet(String userName) {
		log.info(TweetConstant.IN_REQUEST_LOG, "getAllUserTweet", userName);
		List<Tweet> findByUserName = tweetRepository.findByUserName(userName);
		if (findByUserName.isEmpty())
			throw new TweetAppException(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR,
					TweetConstant.NO_TWEETS_FOUND);
		log.info(TweetConstant.EXITING_RESPONSE_LOG, "getAllUserTweet", findByUserName);
		return ResponseEntity.ok(new Envelope<List<Tweet>>(HttpStatus.OK.value(), HttpStatus.OK, findByUserName));
	}

	public ResponseEntity<Envelope<String>> updateTweet(String userName, int tweetId, String updateTweet) {
		log.info(TweetConstant.IN_REQUEST_LOG, "updateTweet", updateTweet);
		tweetAndUserValidation(userName, tweetId);
		Tweet tweet = new Tweet(tweetId, userName, updateTweet,
				new Date(System.currentTimeMillis()), null, null);
		Query query = new Query();
		query.addCriteria(Criteria.where("userName").is(userName));
		Update update = new Update();
		update.set(TweetConstant.TWEET, tweet.getTweet());
		tweet = mongoOperations.findAndModify(query, update, Tweet.class);
		if (tweet == null)
			throw new TweetAppException(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR,
					"Error While Updating Tweet");
		producer.sendMessage("Updated Tweet :: " + tweet.toString().concat(" by ::" + userName));
		log.info(TweetConstant.EXITING_RESPONSE_LOG, "updateTweet", tweet);
		return ResponseEntity
				.ok(new Envelope<String>(HttpStatus.OK.value(), HttpStatus.OK, TweetConstant.TWEET_UPDATED));
	}

	public ResponseEntity<Envelope<String>> deleteTweet(String userName, int tweetId) {
		log.info(TweetConstant.IN_REQUEST_LOG, "deleteTweet", tweetId);
		tweetAndUserValidation(userName, tweetId);
		tweetRepository.deleteById(tweetId);
		log.info(TweetConstant.EXITING_RESPONSE_LOG, "deleteTweet", TweetConstant.TWEET_DELETED);
		return ResponseEntity
				.ok(new Envelope<String>(HttpStatus.OK.value(), HttpStatus.OK, TweetConstant.TWEET_DELETED));
	}

	public ResponseEntity<Envelope<String>> likeTweet(String userName, int tweetId) {
		log.info(TweetConstant.IN_REQUEST_LOG, "likeTweet", tweetId);
		tweetAndUserValidation(userName, tweetId);
		Optional<Tweet> findById = tweetRepository.findById(tweetId);
		Tweet tweet = findById.get();
		Map<String, Integer> OldlikesMap = tweet.getLikes();
		Map<String, Integer> updatedLikesMap = new HashMap<>();
		if (OldlikesMap != null)
			updatedLikesMap.putAll(OldlikesMap);
		updatedLikesMap.put(userName, 1);
		tweet.setLikes(updatedLikesMap);
		Query query = new Query();
		query.addCriteria(Criteria.where(TweetConstant.TWEET_ID).is(tweetId));
		Update update = new Update();
		update.set(TweetConstant.LIKES, tweet.getLikes());
		log.info(TweetConstant.EXITING_RESPONSE_LOG, "likeTweet", tweet.getLikes());
		tweet = mongoOperations.findAndModify(query, update, Tweet.class);
		if (tweet == null)
			throw new TweetAppException(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR,
					"Error While Liking");
		return ResponseEntity.ok(new Envelope<String>(HttpStatus.OK.value(), HttpStatus.OK, TweetConstant.LIKED_TWEET));
	}

	public ResponseEntity<Envelope<String>> replyTweet(String userName, int tweetId, String reply) {
		log.info(TweetConstant.IN_REQUEST_LOG, "replyTweet", tweetId + " " + reply);
		tweetAndUserValidation(userName, tweetId);
		Optional<Tweet> findById = tweetRepository.findById(tweetId);
		Tweet tweet = findById.get();
		Map<String, List<String>> newReplyList = new HashMap<>();
		Map<String, List<String>> oldReplies = tweet.getReplies();
		if (oldReplies == null) {
			newReplyList.put(userName, Arrays.asList(reply));
		} else {
			if (oldReplies.containsKey(userName)) {
				List<String> list = new ArrayList<>(oldReplies.get(userName));
				list.add(reply);
				newReplyList.putAll(oldReplies);
				newReplyList.put(userName, list);
			} else {
				newReplyList.putAll(oldReplies);
				newReplyList.put(userName, Arrays.asList(reply));
			}
		}
		tweet.setReplies(newReplyList);
		tweet.setReplies(newReplyList);
		Query query = new Query();
		query.addCriteria(Criteria.where(TweetConstant.TWEET_ID).is(tweetId));
		Update update = new Update();
		update.set(TweetConstant.REPLIES, newReplyList);

		tweet = mongoOperations.findAndModify(query, update, Tweet.class);
		log.info(TweetConstant.EXITING_RESPONSE_LOG, "replyTweet", tweet);
		if (tweet == null)
			throw new TweetAppException(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR,
					"Error While replying");
		return ResponseEntity
				.ok(new Envelope<String>(HttpStatus.OK.value(), HttpStatus.OK, TweetConstant.REPLIED_TO_TWEET));
	}

	private void tweetAndUserValidation(String userName, int tweetId) {
		log.info(TweetConstant.IN_REQUEST_LOG, "tweetAndUserValidation :: Validating User", userName);
		Optional<Tweet> findById = tweetRepository.findById(tweetId);
		Optional<User> findByUserName = userRepository.findByUserName(userName);
		if (!findByUserName.isPresent())
			throw new TweetAppException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST,
					TweetConstant.USER_NAME_NOT_PRESENT);
		log.info(TweetConstant.IN_REQUEST_LOG, "tweetAndUserValidation :: Validating Tweet", tweetId);
		if (!findById.isPresent())
			throw new TweetAppException(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR,
					TweetConstant.NO_TWEETS_FOUND);
		log.info(TweetConstant.EXITING_RESPONSE_LOG, "tweetAndUserValidation", "User And Tweet Validated");
	}

}
