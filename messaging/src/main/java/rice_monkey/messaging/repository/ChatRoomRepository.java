package rice_monkey.messaging.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rice_monkey.messaging.domain.ChatRoom;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findAllByUserId(Long userId);
    Optional<ChatRoom> findByIdAndUserId(Long roomId, Long userId);
}
