package pro.sky.telegrambot.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity(name = "notification_task")
public class NotificationEntity {
    @Id
    @GeneratedValue
    private Long id;
    private Long chatId;
    private String message;
    private LocalDateTime time;

    public NotificationEntity(Long id, String message, LocalDateTime dateAndTime) {
        this.chatId = id;
        this.message = message;
        this.time = dateAndTime;
    }

    public NotificationEntity() {
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationEntity that = (NotificationEntity) o;
        return Objects.equals(chatId, that.chatId) && Objects.equals(message, that.message) && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, message, time);
    }

    @Override
    public String toString() {
        return "NotificationEntity{" +
                "id=" + chatId +
                ", message='" + message + '\'' +
                ", dateAndTime=" + time +
                '}';
    }
}
