package pro.sky.telegrambot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class NotificationTask {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long key;
    private Long chat_id;
    private String message;
    private LocalDateTime dateTime;

    public NotificationTask(Long chat_id, String message, LocalDateTime dateTime) {
        this.chat_id = chat_id;
        this.message = message;
        this.dateTime = dateTime;
    }

    public NotificationTask() {

    }

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    public Long getChat_id() {
        return chat_id;
    }

    public void setChat_id(Long chat_id) {
        this.chat_id = chat_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
