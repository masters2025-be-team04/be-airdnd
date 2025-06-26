package rice_monkey.messaging.fegin;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import rice_monkey.messaging.dto.ChatRoomListGetResponse;

import java.util.List;

@FeignClient(name = "member-service")
public interface MemberClient {

    @GetMapping("/api/members/{userId}/chatrooms")
    List<ChatRoomListGetResponse> getChatRoomList(@PathVariable Long userId);

    @GetMapping("/api/members/{userId}/chatrooms/{roomId}")
    ChatRoomListGetResponse getChatRoomInfo(@PathVariable Long userId, @PathVariable Long roomId);

    @DeleteMapping("/chatrooms/{userId}/{roomId}")
    ResponseEntity<Void> deleteChatRoom(@PathVariable Long userId, @PathVariable Long roomId);

}
