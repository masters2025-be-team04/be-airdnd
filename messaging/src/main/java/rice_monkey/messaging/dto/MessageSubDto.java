package rice_monkey.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MessageSubDto {
    private Long userId;
    private Long partnerId;
    private ChatMessageDto chatMessageDto;
    private List<ChatRoomListGetResponse> list;
    private List<ChatRoomListGetResponse> partnerList;
}
