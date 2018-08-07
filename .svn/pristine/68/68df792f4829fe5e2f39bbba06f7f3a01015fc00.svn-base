package com.ydzq.hq.prod.config;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "kafka.server")
public class KafkaConfig {
	private String urls;
	private String key;
	private String value;
	private String topic;

	public void setUrls(String urls) {
		this.urls = urls;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	@Bean
	public KafkaProducer<LongSerializer, ByteArraySerializer> initKafka() {
		Properties props = new Properties();
		props.put("bootstrap.servers", urls);
		props.put("key.serializer", key);
		props.put("value.serializer", value);
		return new KafkaProducer<LongSerializer, ByteArraySerializer>(props);
	}
}
