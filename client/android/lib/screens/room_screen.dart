import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:android/models/room.dart'; // 确保 Room 模型已正确定义
import 'package:android/models/user.dart'; // 确保 User 模型已正确定义
import 'package:android/widgets/painter.dart';
import 'package:android/models/draw_shape.dart';

class RoomScreen extends StatefulWidget {
  final Room room;
  final String username; // 添加用户名字段

  const RoomScreen({Key? key, required this.room, required this.username})
      : super(key: key);

  @override
  _RoomScreenState createState() => _RoomScreenState();
}

class _RoomScreenState extends State<RoomScreen> {
  List<User> participants = [];
  Color selectedColor = Colors.black;
  String selectedTool = 'pen';
  List<DrawingShape> shapes = []; // Initialize an empty list of shapes
  double strokeWidth = 3.0;

  @override
  void initState() {
    super.initState();
    fetchParticipants();
  }

  void updateShapes(List<DrawingShape> newShapes) {
    setState(() {
      shapes = newShapes;
    });
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
      body: Painter(
        shapes: shapes, // Pass the shapes list
        color: selectedColor,
        selectedTool: selectedTool, // Pass the selected tool
        onNewShapes: updateShapes, // Pass the callback to update shapes
        strokeWidth: strokeWidth,
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
      isScrollControlled: true, // 允许弹窗内容滚动
      builder: (BuildContext context) {
        return SingleChildScrollView(
          // 添加滚动视图
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: <Widget>[
              Wrap(
                children: <Widget>[
                  ListTile(
                    leading: Icon(Icons.brush),
                    title: Text('Pen'),
                    onTap: () {
                      _selectTool('pen');
                    },
                  ),
                  ListTile(
                    leading: Icon(Icons.check_box_outline_blank),
                    title: Text('Rectangle'),
                    onTap: () {
                      _selectTool('rectangle');
                    },
                  ),
                  ListTile(
                    leading: Icon(Icons.radio_button_unchecked),
                    title: Text('Circle'),
                    onTap: () {
                      _selectTool('circle');
                    },
                  ),
                ],
              ),
              Divider(),
              // Padding(
              //   padding: const EdgeInsets.symmetric(
              //       horizontal: 20.0, vertical: 10.0),
              //   child: Row(
              //     children: <Widget>[
              //       Text("Stroke Size"),
              //       Expanded(
              //         child: Slider(
              //           value: strokeWidth,
              //           min: 1.0,
              //           max: 10.0,
              //           divisions: 9,
              //           label: strokeWidth.round().toString(),
              //           onChanged: (double value) {
              //             setState(() {
              //               strokeWidth = value;
              //             });
              //           },
              //         ),
              //       ),
              //     ],
              //   ),
              // ),
              Padding(
                padding: const EdgeInsets.symmetric(vertical: 20.0), // 增加垂直间距
                child: Wrap(
                  spacing: 7.0, // 每个颜色按钮之间的间距
                  runSpacing: 10.0, // 新增行间距
                  children: List.generate(Colors.primaries.length, (index) {
                    return GestureDetector(
                      onTap: () {
                        setState(() {
                          selectedColor = Colors.primaries[index];
                        });
                        Navigator.pop(context); // 选择颜色后关闭底部弹窗
                      },
                      child: CircleAvatar(
                        backgroundColor: Colors.primaries[index],
                        radius: 15,
                      ),
                    );
                  }),
                ),
              )
            ],
          ),
        );
      },
    );
  }

  void _selectTool(String tool) {
    setState(() {
      selectedTool = tool;
    });
    Navigator.pop(context); // 关闭工具选择器
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

  void _leaveRoom(BuildContext context) async {
    var serverUrl = dotenv.env['SERVER_URL'] ?? "http://defaultserver";
    var url = Uri.parse(
        '$serverUrl/rooms/leave?roomId=${widget.room.id}&username=${widget.username}');

    var authUser = dotenv.env['USER'] ?? "admin";
    var authPwd = dotenv.env['PASSWORD'] ?? "admin";
    String basicAuth =
        'Basic ' + base64Encode(utf8.encode('$authUser:$authPwd'));

    try {
      var response = await http
          .post(url, headers: <String, String>{'authorization': basicAuth});

      if (response.statusCode == 200) {
        Navigator.pop(context); // 成功退出后返回上一页
        ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('You have successfully left the room')));
      } else {
        print('Failed to leave room with status code: ${response.statusCode}');
        print('Response body: ${response.body}');
        throw Exception(
            'Failed to leave room with status code: ${response.statusCode}');
      }
    } catch (e) {
      print('Error: $e');
      ScaffoldMessenger.of(context)
          .showSnackBar(SnackBar(content: Text('Failed to leave room: $e')));
    }
  }
}
