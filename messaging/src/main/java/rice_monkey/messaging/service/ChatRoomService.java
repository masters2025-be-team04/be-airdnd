package rice_monkey.messaging.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rice_monkey.messaging.dto.ChatMessageDto;
import rice_monkey.messaging.dto.ChatRoomListGetResponse;
import rice_monkey.messaging.fegin.MemberClient;
import rice_monkey.messaging.handler.JwtTokenProvider;
import rice_monkey.messaging.repository.ChatRoomRedisRepository;
import rice_monkey.messaging.repository.ChatRoomRepository;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberClient memberClient;
    private final ChatRoomRedisRepository chatRoomRedisRepository;
    private final ChatRoomRepository chatRoomRepository;

    /**
     * Redis에 채팅방이 없으면 Member 서비스에서 조회 후 Redis에 저장
     */
    public ChatRoomListGetResponse getOrFetchChatRoom(Long userId, Long roomId, String accessToken) {
        if (chatRoomRedisRepository.existChatRoom(userId, roomId)) {
            return chatRoomRedisRepository.getChatRoom(userId, roomId);
        }
        ChatRoomListGetResponse chatRoom = memberClient.getChatRoomInfo(userId, roomId);
        chatRoomRedisRepository.setChatRoom(userId, roomId, chatRoom);
        return chatRoom;
    }

    /**
     * 마지막 메시지 정보와 시간 Redis에 업데이트 (내 정보와 상대방 정보 모두)
     */
    public void updateChatRoomLastMessage(Long userId, Long partnerId, Long roomId,
                                          ChatMessageDto message, ChatRoomListGetResponse roomInfo) {
        roomInfo.setLastMessage(message.getMessage());
        roomInfo.setLastMessageTime(java.time.LocalDateTime.now());
        chatRoomRedisRepository.setChatRoom(userId, roomId, roomInfo);
        chatRoomRedisRepository.setChatRoom(partnerId, roomId, roomInfo);
        chatRoomRedisRepository.setLastChatMessage(roomId, message);
    }

    /**
     * 채팅방 목록 조회 후 최신 메시지 기준으로 정렬
     */
    public List<ChatRoomListGetResponse> getSortedChatRoomList(Long userId, String accessToken) {
        return sortChatRoomListLatest(getChatRoomList(userId, accessToken));
    }

    /**
     * 채팅방 목록 조회 (Redis → 없으면 Member 서비스 → Redis 초기화)
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
     * 채팅방 삭제 처리 (accessToken 확인 후 내 Redis,memberDb에서도 삭제)
     */
    public void deleteChatRoom(String accessToken, Long roomId, Long userId) {
        Long tokenUserId = jwtTokenProvider.getUserIdFromToken(accessToken);
        if (!tokenUserId.equals(userId)) {
            throw new IllegalArgumentException("본인의 채팅방만 삭제할 수 있습니다.");
        }
        memberClient.deleteChatRoom(userId, roomId);
        chatRoomRedisRepository.deleteChatRoom(userId, roomId);
    }

    /**
     * 마지막 메시지 시간 기준 내림차순 정렬
     */
    private List<ChatRoomListGetResponse> sortChatRoomListLatest(List<ChatRoomListGetResponse> list) {
        list.sort(Comparator.comparing(ChatRoomListGetResponse::getLastMessageTime).reversed());
        return list;
    }
}