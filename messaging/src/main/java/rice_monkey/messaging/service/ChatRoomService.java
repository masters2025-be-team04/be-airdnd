package rice_monkey.messaging.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rice_monkey.messaging.dto.ChatMessageDto;
import rice_monkey.messaging.dto.ChatRoomListGetResponse;
import rice_monkey.messaging.fegin.MemberClient;
import rice_monkey.messaging.handler.JwtTokenProvider;
import rice_monkey.messaging.repository.ChatRoomRedisRepository;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberClient memberClient;
    private final ChatRoomRedisRepository chatRoomRedisRepository;

    /**
     * 특정 채팅방 정보 조회 (Redis → 없으면 Member Service → Redis 저장)
     */
    public ChatRoomListGetResponse getChatRoomInfo(String accessToken, Long roomId) {
        Long userId = jwtTokenProvider.getUserIdFromToken(accessToken);

        if (chatRoomRedisRepository.existChatRoom(userId, roomId)) {
            return chatRoomRedisRepository.getChatRoom(userId, roomId);
        }

        ChatRoomListGetResponse chatRoom = memberClient.getChatRoomInfo(userId, roomId);
        chatRoomRedisRepository.setChatRoom(userId, roomId, chatRoom);

        return chatRoom;
    }

    /**
     * 사용자의 모든 채팅방 목록 조회 (Redis → 없으면 Member Service → Redis 저장)
     */
    public List<ChatRoomListGetResponse> getChatRoomList(Long userId, String accessToken) {
        List<ChatRoomListGetResponse> list = chatRoomRedisRepository.getChatRoomList(userId);

        if (list == null || list.isEmpty()) {
            list = memberClient.getChatRoomList(userId);
            chatRoomRedisRepository.initChatRoomList(userId, list);
        }

        return list;
    }

    /**
     * 채팅방 정렬 (마지막 메시지 기준 내림차순)
     */
    public List<ChatRoomListGetResponse> sortChatRoomListLatest(List<ChatRoomListGetResponse> list) {
        list.sort(Comparator.comparing(ChatRoomListGetResponse::getLastMessageTime).reversed());
        return list;
    }

    /**
     * 채팅방 삭제 (내 Redis 데이터만 삭제)
     */
    public void deleteChatRoom(String accessToken, Long roomId, Long userId) {
        Long tokenUserId = jwtTokenProvider.getUserIdFromToken(accessToken);
        if (!tokenUserId.equals(userId)) {
            throw new IllegalArgumentException("본인의 채팅방만 삭제할 수 있습니다.");
        }

        chatRoomRedisRepository.deleteChatRoom(userId, roomId);
    }

    /**
     * 마지막 메시지 업데이트
     */
    public void updateLastChatMessage(Long roomId, ChatMessageDto message) {
        chatRoomRedisRepository.setLastChatMessage(roomId, message);
    }
}
