package tp.kafka.spam;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatTuple {

   ChatMessage msg;
   Long c;

   public boolean isSpam(){
       return c != null;
   }

}