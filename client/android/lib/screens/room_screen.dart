import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:android/models/room.dart'; // 确保 Room 模型已正确定义
import 'package:android/models/user.dart'; // 确保 User 模型已正确定义

class RoomScreen extends StatefulWidget {
  final Room room;

  const RoomScreen({Key? key, required this.room}) : super(key: key);

  @override
  _RoomScreenState createState() => _RoomScreenState();
}

class _RoomScreenState extends State<RoomScreen> {
  List<User> participants = [];
  Color selectedColor = Colors.black;
  String selectedTool = 'pen';

  @override
  void initState() {
    super.initState();
    fetchParticipants();
  }

  void fetchParticipants() async {
    var serverUrl = dotenv.env['SERVER_URL'] ?? "http://defaultserver";
    var url =
        Uri.parse('$serverUrl/rooms/getparticipants?roomId=${widget.room.id}');

    var authUser = dotenv.env['USER'] ?? "admin";
    var authPwd = dotenv.env['PASSWORD'] ?? "admin";
    String basicAuth =
        'Basic ' + base64Encode(utf8.encode('$authUser:$authPwd'));

    try {
      var response = await http.get(
        url,
        headers: <String, String>{'authorization': basicAuth},
      );
      if (response.statusCode == 200) {
        setState(() {
          participants = (jsonDecode(response.body) as List)
              .map((data) => User.fromJson(data))
              .toList();
        });
      } else {
        print(
            'Failed to load participants with status code: ${response.statusCode}');
        print('Response body: ${response.body}');
        throw Exception(
            'Failed to load participants with status code: ${response.statusCode}');
      }
    } catch (e) {
      print('Error: $e');
      ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Failed to load participants: $e')));
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Room: ${widget.room.id}'),
        actions: [
          IconButton(
            icon: const Icon(Icons.color_lens),
            onPressed: () => _showToolOptions(context),
          ),
          IconButton(
            icon: const Icon(Icons.people),
            onPressed: () => _showParticipants(context),
          ),
        ],
      ),
      body: Column(
        children: [
          Expanded(
            child: Center(
              child: Text('这里将是画板区域'), // 这里应该是画板的 Widget
            ),
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => _leaveRoom(context),
        child: Icon(Icons.exit_to_app),
        tooltip: 'Leave Room',
      ),
    );
  }

  void _showToolOptions(BuildContext context) {
    showModalBottomSheet(
      context: context,
      builder: (BuildContext context) {
        return Wrap(
          children: <Widget>[
            ListTile(
              leading: Icon(Icons.brush),
              title: Text('Pen'),
              onTap: () => _selectTool('pen'),
            ),
            ListTile(
              leading: Icon(Icons.check_box_outline_blank),
              title: Text('Rectangle'),
              onTap: () => _selectTool('rectangle'),
            ),
            // 添加更多工具选项
          ],
        );
      },
    );
  }

  void _showParticipants(BuildContext context) async {
    try {
      fetchParticipants(); // Wait for participants to be fetched
      showModalBottomSheet(
        context: context,
        builder: (BuildContext context) {
          return ListView.builder(
            itemCount: participants.length,
            itemBuilder: (context, index) {
              return ListTile(
                title: Text(participants[index].username),
              );
            },
          );
        },
      );
    } catch (error) {
      ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text("Error fetching participants: $error")));
    }
  }

  void _leaveRoom(BuildContext context) {
    // 添加退出房间的逻辑
    Navigator.pop(context);
  }

  void _selectTool(String tool) {
    setState(() {
      selectedTool = tool;
    });
    Navigator.pop(context); // 关闭工具选择器
  }
}
