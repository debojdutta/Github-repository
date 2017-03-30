package com.baeldung.spring.data.redis.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

import com.baeldung.spring.data.redis.queue.MessagePublisher;
import com.baeldung.spring.data.redis.queue.RedisMessagePublisher;
import com.baeldung.spring.data.redis.queue.RedisMessageSubscriber;

@Configuration
@ComponentScan("com.baeldung.spring.data.redis")
public class RedisConfig {

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
    	List<String> nodes = new ArrayList<String>();
    	nodes.add("10.201.1.131:30001");
    	nodes.add("10.201.1.131:30002");
    	nodes.add("10.201.1.131:30003");
    	
    	 return new JedisConnectionFactory(
    	            new RedisClusterConfiguration(nodes));
    	 

        
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        final RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));
        return template;
    }

    @Bean
    MessageListenerAdapter messageListener() {
        return new MessageListenerAdapter(new RedisMessageSubscriber());
    }

    @Bean
    RedisMessageListenerContainer redisContainer() {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory());
        container.addMessageListener(messageListener(), topic());
        return container;
    }

    @Bean
    MessagePublisher redisPublisher() {
        return new RedisMessagePublisher(redisTemplate(), topic());
    }

    @Bean
    ChannelTopic topic() {
        return new ChannelTopic("pubsub:queue");
    }
}
