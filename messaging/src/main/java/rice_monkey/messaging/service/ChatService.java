package rice_monkey.messaging.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rice_monkey.messaging.dto.ChatMessageDto;
import rice_monkey.messaging.dto.ChatRoomListGetResponse;
import rice_monkey.messaging.dto.MessageSubDto;
import rice_monkey.messaging.dto.MessageType;
import rice_monkey.messaging.redis.RedisPublisher;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChatService {

    private final RedisPublisher redisPublisher;
    private final ChatRoomService chatRoomService;

    /**
     * 채팅방에 메시지 발송
     */
    public void sendChatMessage(ChatMessageDto chatMessage, String accessToken) {
        Long userId = chatMessage.getUserId();
        Long roomId = chatMessage.getRoomId();

        // 1. 메시지 타입이 DELETE라면 채팅방 삭제 처리 후 종료
        if (chatMessage.getType() == MessageType.DELETE) {
            chatRoomService.deleteChatRoom(accessToken, roomId, userId);
            return;
        }

        // 2. Redis에 채팅방이 존재하지 않으면 Member 서비스에서 받아와 저장
        ChatRoomListGetResponse roomInfo = chatRoomService.getOrFetchChatRoom(userId, roomId, accessToken);
        Long partnerId = getPartnerId(chatMessage, roomInfo);

        // 3. 마지막 메시지 내용을 Redis에 저장 (양측 모두)
        chatRoomService.updateChatRoomLastMessage(userId, partnerId, roomId, chatMessage, roomInfo);

        // 4. 내 채팅방 목록 불러오기 및 정렬
        List<ChatRoomListGetResponse> userList = chatRoomService.getSortedChatRoomList(userId, accessToken);
        // 5. 상대방 채팅방 목록 불러오기 및 정렬 (accessToken 없이 가능)
        List<ChatRoomListGetResponse> partnerList = chatRoomService.getSortedChatRoomList(partnerId, null);

        // 6. 전송할 최종 메시지 DTO 구성
        MessageSubDto dto = MessageSubDto.builder()
                .userId(userId)
                .partnerId(partnerId)
                .chatMessageDto(chatMessage)
                .list(userList)
                .partnerList(partnerList)
                .build();

        // 7. Redis pub/sub 발행 → STOMP 브로드캐스트 대상
        redisPublisher.publish(dto);
    }

    /**
     * 채팅 메시지 내에서 상대방 ID를 추출 (나와 다른 유저)
     */
    private Long getPartnerId(ChatMessageDto chatMessage, ChatRoomListGetResponse chatRoomInfo) {
        Long userId = chatMessage.getUserId();
        Long partnerId = chatRoomInfo.getPartnerId();

        if (partnerId == null || partnerId.equals(userId)) {
            throw new IllegalStateException("잘못된 파트너 ID입니다.");
        }
        return partnerId;
    }
}