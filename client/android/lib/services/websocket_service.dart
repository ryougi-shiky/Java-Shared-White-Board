import 'dart:convert';
import 'package:web_socket_channel/io.dart';

void initState() {
  super.initState();
  channel = IOWebSocketChannel.connect('ws://yourserver.com/path');

  channel.stream.listen((message) {
    var data = json.decode(message);
    // 更新画布
    updateCanvas(data);
  });
}

void sendDrawing(List<Offset?> points) {
  channel.sink.add(json.encode(points));
}
