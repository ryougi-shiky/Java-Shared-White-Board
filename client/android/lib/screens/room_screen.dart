import 'package:flutter/material.dart';
import 'package:android/models/room.dart'; // 确保 Room 模型已正确定义

class RoomScreen extends StatefulWidget {
  final Room room;

  const RoomScreen({Key? key, required this.room}) : super(key: key);

  @override
  _RoomScreenState createState() => _RoomScreenState();
}

class _RoomScreenState extends State<RoomScreen> {
  Color selectedColor = Colors.black; // 默认颜色
  String selectedTool = 'pen'; // 默认工具

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

  void _showParticipants(BuildContext context) {
    showModalBottomSheet(
      context: context,
      builder: (BuildContext context) {
        return ListView.builder(
          itemCount: widget.room.participants.length,
          itemBuilder: (context, index) {
            return ListTile(
              title: Text(widget.room.participants[index].username),
            );
          },
        );
      },
    );
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
