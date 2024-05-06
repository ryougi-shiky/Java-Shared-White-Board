import 'package:flutter/material.dart';
import 'drawing_painter.dart'; // 引入绘画逻辑

class Painter extends StatefulWidget {
  final List<Offset?> points; // 注意类型改为 List<Offset?>
  final Color color;
  final Function(List<Offset?>) onNewPoints; // 注意类型改为 List<Offset?>

  Painter(
      {Key? key,
      required this.points,
      required this.color,
      required this.onNewPoints})
      : super(key: key);

  @override
  _PainterState createState() => _PainterState();
}

class _PainterState extends State<Painter> {
  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onPanUpdate: (details) {
        RenderBox renderBox = context.findRenderObject() as RenderBox;
        Offset localPosition = renderBox.globalToLocal(details.globalPosition);
        widget.onNewPoints([...widget.points, localPosition]);
      },
      onPanEnd: (details) =>
          widget.onNewPoints([...widget.points, null]), // 现在这里添加 null 是合法的
      child: CustomPaint(
        painter: DrawingPainter(widget.points.whereType<Offset>().toList(),
            widget.color), // 过滤 null 值
        child: Container(),
      ),
    );
  }
}
