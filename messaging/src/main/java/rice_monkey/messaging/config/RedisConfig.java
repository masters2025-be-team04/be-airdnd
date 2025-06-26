package rice_monkey.messaging.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import rice_monkey.messaging.redis.RedisSubscriber;

@RequiredArgsConstructor
@Configuration
public class RedisConfig {

    // application.yml에 설정된 Redis 정보 주입
    private final RedisProperties redisProperties;

    /**
     * Redis Pub/Sub을 위한 채널 토픽 설정
     */
    @Bean
    public ChannelTopic channelTopic() {
        return new ChannelTopic("chatroom");
    }

    /**
     * Redis 접속 팩토리 설정 (Lettuce 사용)
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        LettuceConnectionFactory factory = new LettuceConnectionFactory();
        factory.setHostName(redisProperties.getHost());
        factory.setPort(redisProperties.getPort());
        return factory;
    }

    /**
     * Redis 메시지를 수신하기 위한 리스너 컨테이너 설정
     * 두 개의 리스너(sendMessage, sendRoomList)를 같은 채널에 등록
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapterChatMessage,
            MessageListenerAdapter listenerAdapterChatRoomList,
            ChannelTopic channelTopic
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapterChatMessage, channelTopic);
        container.addMessageListener(listenerAdapterChatRoomList, channelTopic);
        return container;
    }

    /**
     * RedisSubscriber의 sendMessage 메서드를 처리할 어댑터
     */
    @Bean
    public MessageListenerAdapter listenerAdapterChatMessage(RedisSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "sendMessage");
    }

    /**
     * RedisSubscriber의 sendRoomList 메서드를 처리할 어댑터
     */
    @Bean
    public MessageListenerAdapter listenerAdapterChatRoomList(RedisSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "sendRoomList");
    }

    /**
     * RedisTemplate 설정 (Key: String, Value: JSON 직렬화)
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));

        return redisTemplate;
    }
}
