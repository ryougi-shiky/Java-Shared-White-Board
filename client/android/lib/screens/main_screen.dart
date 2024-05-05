import 'package:android/models/room.dart';

import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;

class MainScreen extends StatefulWidget {
  final String username;

  const MainScreen({Key? key, required this.username}) : super(key: key);

  @override
  _MainScreenState createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  List<dynamic> rooms = [];

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
      var response = await http
          .get(url, headers: <String, String>{'authorization': basicAuth});
      if (response.statusCode == 200) {
        var jsonData = jsonDecode(response.body);
        if (jsonData != null) {
          var roomsData = jsonData as List;
          setState(() {
            rooms = roomsData.map((data) => Room.fromJson(data)).toList();
          });
        } else {
          setState(() {
            rooms = [];
          });
        }
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
                Room room = rooms[index];
                String username =
                    room.owner.username; // 直接访问 Room 对象的 owner 属性的 username
                return ListTile(
                  title: Text(username),
                  onTap: () {
                    // Join the room
                  },
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
                  onPressed: () {
                    // 假设你有一个方法来加入房间
                  },
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
    var url =
        Uri.parse('$serverUrl/rooms/create?username=${widget.username}');
    var authUser = dotenv.env['USER'] ?? "admin";
    var authPwd = dotenv.env['PASSWORD'] ?? "admin";
    String basicAuth =
        'Basic ' + base64Encode(utf8.encode('$authUser:$authPwd'));

    try {
      var response = await http.post(
        url,
        headers: <String, String>{
          'authorization': basicAuth,
        },
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
}
