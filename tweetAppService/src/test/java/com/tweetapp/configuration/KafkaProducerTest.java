package com.tweetapp.configuration;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

import com.tweetapp.util.TweetConstant;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class KafkaProducerTest {

	@InjectMocks
	KafkaProducer kafkaProducer;

	@Mock
	private KafkaTemplate<String, String> kafkaTemplate;

	@Test
	void testSendMessage() {
		kafkaProducer.sendMessage("this is a message");
		verify(kafkaTemplate, times(1)).send(TweetConstant.TOPIC_NAME, TweetConstant.TOPIC_NAME, "this is a message");
	}

}
