import 'package:web_socket_channel/io.dart';
import 'dart:convert';

import 'package:android/models/draw_action.dart';
import 'package:android/models/draw_shape.dart';
import 'package:flutter/material.dart';

class WebSocketService {
  late IOWebSocketChannel channel;
  final Function(DrawingShape)
      onUpdateDrawing; // Callback to handle drawing updates

  WebSocketService(this.onUpdateDrawing);

  void connect(String roomId) {
    channel = IOWebSocketChannel.connect('ws://yourserver.com/room/$roomId');

    channel.stream.listen((message) {
      // Deserialize the incoming message
      var jsonMessage = json.decode(message);
      DrawingAction action = DrawingAction.fromJson(jsonMessage);
      updateDrawingFromServer(action);
    });
  }

  void sendDrawing(DrawingAction action) {
    // Serialize and send the drawing action
    channel.sink.add(json.encode(action.toJson()));
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
  }
}
