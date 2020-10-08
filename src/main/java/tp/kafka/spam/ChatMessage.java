package tp.kafka.spam;

import lombok.Data;

@Data
public class ChatMessage {
    private String userId;
    private String message;
}