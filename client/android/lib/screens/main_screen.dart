import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

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
    var url = Uri.parse('$serverUrl/api/listrooms');
    try {
      var response = await http.get(url);
      if (response.statusCode == 200) {
        setState(() {
          rooms = jsonDecode(response.body);
        });
      } else {
        throw Exception('Failed to load rooms');
      }
    } catch (e) {
      print('Error: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Available Rooms'),
      ),
      body: ListView.builder(
        itemCount: rooms.length,
        itemBuilder: (context, index) {
          return ListTile(
            title: Text(rooms[index]['owner'][
                'username']), // Assuming 'owner' is a map that contains 'username'
            onTap: () {
              // Join the room
            },
          );
        },
      ),
    );
  }
}
