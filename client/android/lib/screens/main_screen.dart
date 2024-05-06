import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:android/models/room.dart'; // 确保你的 Room 模型已正确定义并可用
import 'room_screen.dart';

class MainScreen extends StatefulWidget {
  final String username;

  const MainScreen({Key? key, required this.username}) : super(key: key);

  @override
  _MainScreenState createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  List<Room> rooms = [];
  Room? selectedRoom;

  @override
  void initState() {
    super.initState();
    fetchRooms();
  }

  void fetchRooms() async {
    var serverUrl = dotenv.env['SERVER_URL'] ?? "http://defaultserver";
    var url = Uri.parse('$serverUrl/listrooms');

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
          rooms = (jsonDecode(response.body) as List)
              .map((data) => Room.fromJson(data))
              .toList();
        });
      } else {
        throw Exception('Failed to load rooms');
      }
    } catch (e) {
      print('Error: $e');
      ScaffoldMessenger.of(context)
          .showSnackBar(SnackBar(content: Text('Failed to load rooms: $e')));
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Welcome, ${widget.username}'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: fetchRooms,
          ),
        ],
      ),
      body: Column(
        children: [
          Expanded(
            child: ListView.builder(
              itemCount: rooms.length,
              itemBuilder: (context, index) {
                bool isSelected = selectedRoom == rooms[index];

                return Container(
                  // color: index % 2 == 0 ? Colors.grey[300] : Colors.white, // 交替颜色
                  color: isSelected
                      ? Colors.blue[100]
                      : (index % 2 == 0
                          ? Colors.grey[300]
                          : Colors.white), // 选中时改变背景色

                  child: ListTile(
                    title: Text(rooms[index].owner.username),
                    onTap: () {
                      setState(() {
                        selectedRoom = rooms[index]; // 更新选中的房间
                      });
                    },
                  ),
                );
              },
            ),
          ),
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                ElevatedButton(
                  onPressed: createRoom,
                  child: const Text('Create Room'),
                ),
                ElevatedButton(
                  onPressed: joinRoom,
                  child: const Text('Join a Room'),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  void createRoom() async {
    var serverUrl = dotenv.env['SERVER_URL'] ?? "http://defaultserver";
    var url = Uri.parse('$serverUrl/rooms/create?username=${widget.username}');
    var authUser = dotenv.env['USER'] ?? "admin";
    var authPwd = dotenv.env['PASSWORD'] ?? "admin";
    String basicAuth =
        'Basic ' + base64Encode(utf8.encode('$authUser:$authPwd'));

    try {
      var response = await http.post(
        url,
        headers: <String, String>{'authorization': basicAuth},
      );
      if (response.statusCode == 200) {
        fetchRooms(); // 刷新列表以显示创建的房间
      } else {
        print('Failed to create room: ${response.body}');
      }
    } catch (e) {
      print('Error creating room: $e');
    }
  }

  void joinRoom() async {
    var serverUrl = dotenv.env['SERVER_URL'] ?? "http://defaultserver";
    var url = Uri.parse(
        '$serverUrl/rooms/join?roomId=${selectedRoom?.id}&username=${widget.username}');
    var authUser = dotenv.env['USER'] ?? "admin";
    var authPwd = dotenv.env['PASSWORD'] ?? "admin";
    String basicAuth =
        'Basic ' + base64Encode(utf8.encode('$authUser:$authPwd'));

    if (selectedRoom != null) {
      print('Joining room: ${selectedRoom!.id}'); // 打印房间 ID，或进行页面跳转
      // 你可以在这里添加逻辑来处理加入房间的请求
      try {
        var response = await http.post(
          url,
          headers: <String, String>{'authorization': basicAuth},
        );
        if (response.statusCode == 200) {
          fetchRooms(); // 刷新列表以显示创建的房间
        } else {
          print('Failed to join room: ${response.body}');
        }
      } catch (e) {
        print('Error joining room: $e');
      }

      Navigator.push(
          context,
          MaterialPageRoute(
              builder: (context) =>
                  RoomScreen(room: selectedRoom!, username: widget.username)));
    } else {
      ScaffoldMessenger.of(context)
          .showSnackBar(SnackBar(content: Text('No room selected')));
    }
  }
}
