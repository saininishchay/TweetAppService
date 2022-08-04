package com.tweetapp.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

@Document(collection = "Tweet")
@Component
public class Tweet {

	@Transient
	public static final String SEQUENCE_NAME = "tweet_sequence";

	@Id
	private int tweetId;

	private String userName;

	private String tweet;

	private Date created;

	private Map<String, Integer> likes;

	private Map<String, List<String>> replies;

	public Tweet(int tweetId, String userName, String tweet, Date created, Map<String, Integer> likes,
			Map<String, List<String>> replies) {
		this.tweetId = tweetId;
		this.userName = userName;
		this.tweet = tweet;
		this.created = created;
		this.likes = likes;
		this.replies = replies;
	}

	public Tweet() {
	}

	public int getTweetId() {
		return tweetId;
	}

	public void setTweetId(int tweetId) {
		this.tweetId = tweetId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTweet() {
		return tweet;
	}

	public void setTweet(String tweet) {
		this.tweet = tweet;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Map<String, Integer> getLikes() {
		return likes;
	}

	public void setLikes(Map<String, Integer> likes) {
		this.likes = likes;
	}

	public Map<String, List<String>> getReplies() {
		return replies;
	}

	public void setReplies(Map<String, List<String>> replies) {
		this.replies = replies;
	}

	@Override
	public String toString() {
		return "Tweet [tweetId=" + tweetId + ", userName=" + userName + ", tweet=" + tweet + ", created=" + created
				+ ", likes=" + likes + ", replies=" + replies + "]";
	}

}
