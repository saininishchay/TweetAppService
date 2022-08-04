package com.tweetapp.controller;

import static com.tweetapp.util.TweetConstant.ROOT_URL;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.model.Tweet;
import com.tweetapp.service.TweetService;
import com.tweetapp.util.Envelope;

import io.micrometer.core.annotation.Timed;
import lombok.Generated;

@RestController
@Generated
@RequestMapping(value = ROOT_URL)
@CrossOrigin(origins = "${client.url}")
public class TweetController {

	private static final Logger log = LoggerFactory.getLogger(TweetController.class);

	@Autowired
	TweetService tweetService;

	@PostMapping("/{userName}/add")
	public ResponseEntity<Envelope<String>> postTweet(@PathVariable("userName") String userName,
			@RequestParam("tweet") String tweet) {
		log.info("In {} UserName {} ", "postTweet", userName);
		return tweetService.postTweet(userName, tweet);
	}

	@GetMapping("/all")
	public ResponseEntity<Envelope<List<Tweet>>> getAllTweet() {
		log.info("In {}", "getAllTweet");
		return tweetService.getAllTweet();
	}

	@GetMapping("/{userName}")
	@Timed(value = "getAllUserTweet.time", description = "Time taken to return getAllUserTweet")
	public ResponseEntity<Envelope<List<Tweet>>> getAllUserTweet(@PathVariable String userName) {
		log.info("In {} UserName {} ", "getAllUserTweet", userName);
		return tweetService.getAllUserTweet(userName);
	}

	@PutMapping("/{userName}/update/{tweetId}")
	public ResponseEntity<Envelope<String>> updateTweet(@PathVariable("userName") String userName,
			@PathVariable("tweetId") int tweetId, @RequestParam("tweet") String tweet) {
		log.info("In {} UserName {} ", "updateTweet", userName);
		return tweetService.updateTweet(userName, tweetId, tweet);
	}

	@DeleteMapping("/{userName}/delete/{tweetId}")
	public ResponseEntity<Envelope<String>> deleteTweet(@PathVariable("userName") String userName,
			@PathVariable("tweetId") int tweetId) {
		log.info("In {} UserName {} ", "deleteTweet", userName);
		return tweetService.deleteTweet(userName, tweetId);
	}

	@PutMapping("/{userName}/like/{tweetId}")
	public ResponseEntity<Envelope<String>> likeTweet(@PathVariable("userName") String userName,
			@PathVariable("tweetId") int tweetId) {
		log.info("In {} UserName {} ", "likeTweet", userName);
		return tweetService.likeTweet(userName, tweetId);
	}

	@PostMapping("/{userName}/reply/{tweetId}")
	public ResponseEntity<Envelope<String>> replyTweet(@PathVariable("userName") String userName,
			@PathVariable("tweetId") int tweetId, @RequestParam("reply") String reply) {
		log.info("In {} UserName {} ", "replyTweet", userName);
		return tweetService.replyTweet(userName, tweetId, reply);
	}

}
