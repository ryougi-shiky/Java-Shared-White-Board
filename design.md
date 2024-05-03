# Requirements
用户可以开启一个房间，让其他用户加入房间，开启房间的用户是房主。
用户名字不能重复
房主可以踢出用户
共享画板上可以画基础图形，比如线条，长方形，圆形，三角形。
可以选择不同的线条颜色。
可以添加文本，文本也是支持线条颜色的。
画板数据不会保存在服务器上，它只会在房间开启的时候暂时性存储在服务器上，用户必须手动点击save来把画板数据保存为json文件。
用户在开启新房间的时候可以选择从本地导入画板，这个时候就可以导入之前保存的画板数据。
应用不需要复杂的登陆逻辑，只需要输入一个不重复的用户名即可

# Server Design
## User
### Attributes
- String username
### Methods
- User(String username)
- String getUsername()

## Room
### Attributes
- String id
- User owner
- Set<User> participants
- Map<String, Object> boardData
### Methods
- boolean addUser(User user)
- boolean removeUser(User user)
- String getId()
- String getOwnerName()

## RoomService
### Attributes
- ConcurrentHashMap<String, Room> rooms
- ConcurrentHashMap<String, User> users
- User registerUser(String username)
- Room createRoom(String username)
- Error joinRoom(String roomId, String username)
- Error leaveRoom(String roomId, String username)












要有效处理你描述的画板应用，需要详细规划数据结构、实时同步机制以及并发处理。这里是一个分步指南，涵盖了从客户端到服务器的各个方面。

### 数据结构设计

#### 1. **画板数据结构**
画板的数据可以包含多种元素，比如线条、矩形、圆形、三角形和文本。每个元素可以定义为一个对象，具有颜色、位置、大小等属性：

```java
class DrawingItem {
    String type; // "line", "rectangle", "circle", "triangle", "text"
    Map<String, String> attributes; // Includes color, start point, end point, text content, etc.
}
```

#### 2. **画板数据存储**
画板数据可以存储为`Room`类中的一个集合，每个`Room`对象管理其自身的绘图数据：

```java
class Room {
    ...
    List<DrawingItem> drawingItems = new ArrayList<>();
    ...
}
```

### 实时数据同步

#### 1. **使用WebSocket**
WebSocket 提供了一种在单个连接上进行全双工通信的方式，非常适合实现实时数据同步。Spring Boot 支持集成 WebSocket：

- **配置WebSocket**
  在Spring中配置WebSocket消息代理，使用`@EnableWebSocketMessageBroker`注解启动：

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // Topic used for broadcasting updates
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/whiteboard-websocket").withSockJS();
    }
}
```

- **广播更新**
  每当房间的画板数据更新时，服务器可以将这些更新广播到所有订阅了相应房间主题的客户端：

```java
@Service
public class RoomService {
    ...
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void addDrawingItem(String roomId, DrawingItem item) {
        rooms.get(roomId).getDrawingItems().add(item);
        messagingTemplate.convertAndSend("/topic/" + roomId, item);
    }
}
```

### 客户端开发（Flutter）

在Flutter中，你可以使用`websocket`库来与服务器建立WebSocket连接，并实时接收更新：

- **建立WebSocket连接**
  使用`web_socket_channel`包来连接服务器：

```dart
import 'package:web_socket_channel/io.dart';

final channel = IOWebSocketChannel.connect('ws://server_address/whiteboard-websocket');

channel.stream.listen((message) {
  // 处理接收到的消息
});
```

- **画板实现**
  Flutter中可以使用`CustomPainter`和`GestureDetector`来绘制画板并处理用户输入。

### 处理高并发场景

在高并发场景下，确保服务器能够处理多个客户端的请求非常重要：

- **使用缓存**
  对频繁访问但不常修改的数据使用缓存，减少数据库访问压力。

- **限流**
  使用限流策略防止API被过度请求。

- **负载均衡**
  如果使用多个服务器实例，可以通过负载均衡分散请求压力。

通过上述设计和实现策略，你的画板应用应能有效处理客户端的实时交互，同时保持良好的性能和可扩展性。