import 'dart:io';
import 'dart:math';

import 'package:web_socket_channel/io.dart';
import 'package:stomp_dart_client/stomp.dart';
import 'package:stomp_dart_client/stomp_config.dart';
import 'package:stomp_dart_client/stomp_frame.dart';
import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

import 'package:android/models/draw_action.dart';
import 'package:android/models/draw_shape.dart';

class WebSocketService {
  late StompClient stompClient;
  var url;
  var headers;

  final Function(DrawingShape)
      onUpdateDrawing; // Callback to handle drawing updates

  WebSocketService(this.onUpdateDrawing) {
    var serverAddress = dotenv.env['SERVER_ADDR'] ?? "74.211.111.168:8088";
    url = 'ws://$serverAddress/ws'; // 确保连接到 /ws 端点
    var authUser = dotenv.env['USER'] ?? "admin";
    var authPwd = dotenv.env['PASSWORD'] ?? "admin";
    String basicAuth =
        'Basic ' + base64Encode(utf8.encode('$authUser:$authPwd'));

    headers = {
      'Authorization': basicAuth,
    };

    stompClient = StompClient(
      config: StompConfig(
        url: url,
        onConnect: (StompFrame frame) {
          // 默认的 onConnect 实现
        },
      ),
    );
  }

  Future<void> connect(String roomId) async {
    print("Connecting to WebSocket at $url");

    stompClient = StompClient(
      config: StompConfig(
        url: url,
        stompConnectHeaders: headers,
        webSocketConnectHeaders: headers,
        onConnect: (StompFrame frame) {
          print('Connected to WebSocket');
          // 订阅欢迎消息
          stompClient.subscribe(
            destination: '/app/welcome',
            callback: (StompFrame frame) {
              if (frame.body != null) {
                print("Welcome message received: ${frame.body}");
              }
            },
          );

          stompClient.subscribe(
            destination: '/board/room/$roomId',
            callback: (StompFrame frame) {
              if (frame.body != null) {
                print("Received message: ${frame.body}");
                var decoded = json.decode(frame.body!);
                DrawingAction action = DrawingAction.fromJson(decoded);
                try {
                  var shape = action.toDrawingShape();
                  onUpdateDrawing(shape);
                } catch (e) {
                  print('Error converting to drawing shape: $e');
                }
              }
            },
          );
        },
        onWebSocketError: (dynamic error) => print('WebSocket error: $error'),
        onStompError: (StompFrame frame) => print('STOMP error: ${frame.body}'),
        onDisconnect: (StompFrame frame) =>
            print('Disconnected from WebSocket'),
      ),
    );

    stompClient.activate();
  }

  void sendDrawing(DrawingAction action, String roomId) {
    if (stompClient.connected) {
      var message = json.encode(action.toJson());
      stompClient.send(
        destination: '/app/draw/$roomId', // 确保路径包含 roomId
        body: message,
      );
      print("Sending drawing action: $message");
    } else {
      print("WebSocket is not connected. Sending drawing failed");
    }
  }

  void disconnect() {
    stompClient.deactivate();
    print("WebSocket connection closed");
  }

  // void updateDrawingFromServer(DrawingAction action) {
  //   // Convert the DrawingAction to a DrawingShape
  //   DrawingShape shape;
  //   Paint paint = Paint()
  //     ..color = Color(int.parse(action.color, radix: 16))
  //     ..strokeWidth = action.strokeWidth
  //     ..style = PaintingStyle.stroke;

  //   switch (action.type) {
  //     case 'line':
  //       shape = DrawingLine([
  //         Offset(action.startX, action.startY),
  //         Offset(action.endX, action.endY)
  //       ], paint);
  //       break;
  //     case 'rectangle':
  //       shape = DrawingRectangle(Offset(action.startX, action.startY),
  //           Offset(action.endX, action.endY), paint);
  //       break;
  //     case 'circle':
  //       double radius = ((Offset(action.startX, action.startY) -
  //                   Offset(action.endX, action.endY))
  //               .distance /
  //           2);
  //       shape = DrawingCircle(
  //           Offset((action.startX + action.endX) / 2,
  //               (action.startY + action.endY) / 2),
  //           radius,
  //           paint);
  //       break;
  //     default:
  //       shape = DrawingLine([], paint); // Default or unsupported type
  //   }

  //   // Call the callback to update the UI
  //   onUpdateDrawing(shape);
  //   print("flutter ws updateDrawingFromServer...");
  // }

  void updateDrawingFromServer(dynamic jsonMessage) {
    // Deserialize the incoming message
    DrawingAction action = DrawingAction.fromJson(json.decode(jsonMessage));

    // Convert the DrawingAction to a DrawingShape
    DrawingShape shape = action.toDrawingShape();

    // Call the callback to update the UI
    onUpdateDrawing(shape);
    print("flutter ws updateDrawingFromServer: $shape");
  }
}
