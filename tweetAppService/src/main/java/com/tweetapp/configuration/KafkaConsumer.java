package com.tweetapp.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.tweetapp.util.TweetConstant;

import lombok.Generated;

@Generated
@Service
public class KafkaConsumer {

	private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

	@KafkaListener(topics = "message", groupId = TweetConstant.GROUP_ID)
	public void consume(String message) {
		log.info(String.format("Message received -> %s", message));
	}
}