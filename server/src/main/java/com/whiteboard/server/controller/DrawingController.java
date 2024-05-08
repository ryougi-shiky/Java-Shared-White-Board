import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import com.whiteboard.server.model.DrawingAction;

@Controller
public class DrawingController {
    @MessageMapping("/draw")
    @SendTo("/topic/room/{roomId}")
    public DrawingAction broadcastDrawing(DrawingAction action) throws Exception {
        // 这里可以添加一些逻辑，比如保存动作到数据库
        return action;
    }
}
