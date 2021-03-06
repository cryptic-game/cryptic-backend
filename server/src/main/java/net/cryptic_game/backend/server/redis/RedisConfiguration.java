package net.cryptic_game.backend.server.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import java.util.Collections;
import java.util.Map;

@Configuration
public class RedisConfiguration {

    @Bean("notificationListenerAdapter")
    MessageListenerAdapter messageListenerAdapter(final NotificationReceiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer(
            final RedisConnectionFactory connectionFactory,
            @Qualifier("notificationListenerAdapter") final MessageListenerAdapter listenerAdapter
    ) {
        final RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();
        listenerContainer.setConnectionFactory(connectionFactory);
        listenerContainer.setMessageListeners(Map.of(listenerAdapter, Collections.singleton(new PatternTopic("notifications"))));
        return listenerContainer;
    }
}
