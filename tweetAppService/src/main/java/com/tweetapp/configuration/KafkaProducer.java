package com.tweetapp.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.tweetapp.util.TweetConstant;

@Service
public class KafkaProducer {

	private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	public void sendMessage(String message) {
		log.info(String.format("Message sent-> %s", message));
		this.kafkaTemplate.send(TweetConstant.TOPIC_NAME, TweetConstant.TOPIC_NAME, message);
	}

	@Bean
	public NewTopic createTopic() {
		return new NewTopic(TweetConstant.TOPIC_NAME, 3, (short) 1);
	}

}