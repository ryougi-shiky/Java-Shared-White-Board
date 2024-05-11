import 'package:web_socket_channel/io.dart';
import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

import 'package:android/models/draw_action.dart';
import 'package:android/models/draw_shape.dart';

class WebSocketService {
  late IOWebSocketChannel channel;
  final Function(DrawingShape)
      onUpdateDrawing; // Callback to handle drawing updates

  WebSocketService(this.onUpdateDrawing);

  void connect(String roomId) {
    dotenv.load(); // 确保.env文件已加载
    var serverAddress = dotenv.env['SERVER_ADDR'] ?? "74.211.111.168:8088";
    var url = Uri.parse('ws://$serverAddress/ws/'); // 确保连接到 /ws 端点
    print("Connecting to WebSocket at $url");

    channel = IOWebSocketChannel.connect(url);

    channel.stream.listen((message) {
      print("Received message: $message");
      var decoded = json.decode(message);
      DrawingAction action = DrawingAction.fromJson(decoded);
      var shape = action.toDrawingShape();
      onUpdateDrawing(shape);
    });

    // 订阅房间
    channel.sink.add(json
        .encode({'type': 'subscribe', 'destination': '/board/room/$roomId'}));
  }

  void sendDrawing(DrawingAction action) {
    channel.sink.add(json.encode(
        {'type': 'send', 'destination': '/app/draw', 'body': action.toJson()}));
    print("Sending drawing action...");
  }

  void disconnect() {
    channel.sink.close();
  }

  void updateDrawingFromServer(DrawingAction action) {
    // Convert the DrawingAction to a DrawingShape
    DrawingShape shape;
    Paint paint = Paint()
      ..color = Color(int.parse(action.color, radix: 16))
      ..strokeWidth = action.strokeWidth
      ..style = PaintingStyle.stroke;

    switch (action.type) {
      case 'line':
        shape = DrawingLine([
          Offset(action.startX, action.startY),
          Offset(action.endX, action.endY)
        ], paint);
        break;
      case 'rectangle':
        shape = DrawingRectangle(Offset(action.startX, action.startY),
            Offset(action.endX, action.endY), paint);
        break;
      case 'circle':
        double radius = ((Offset(action.startX, action.startY) -
                    Offset(action.endX, action.endY))
                .distance /
            2);
        shape = DrawingCircle(
            Offset((action.startX + action.endX) / 2,
                (action.startY + action.endY) / 2),
            radius,
            paint);
        break;
      default:
        shape = DrawingLine([], paint); // Default or unsupported type
    }

    // Call the callback to update the UI
    onUpdateDrawing(shape);
    print("flutter ws updateDrawingFromServer...");
  }
}
