package rice_monkey.messaging.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat_room")
@SQLDelete(sql = "UPDATE chat_room SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDateTime lastMessageTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean deleted;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoomUser> participants = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        lastMessageTime = LocalDateTime.now();
        deleted = false;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void updateLastMessageTime(LocalDateTime time) {
        this.lastMessageTime = time;
    }

    public void addParticipant(ChatRoomUser user) {
        participants.add(user);
        user.setChatRoom(this);
    }
}
