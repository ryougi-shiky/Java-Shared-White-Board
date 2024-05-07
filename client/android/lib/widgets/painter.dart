import 'package:flutter/material.dart';
import 'drawing_painter.dart'; // 引入绘画逻辑
import 'package:android/models/draw_shape.dart';

class Painter extends StatefulWidget {
  final List<DrawingShape> shapes;
  final Color color;
  final String selectedTool; // 新增选中工具的属性
  final double strokeWidth;
  final Function(List<DrawingShape>) onNewShapes;

  Painter({
    Key? key,
    required this.shapes,
    required this.color,
    required this.selectedTool, // 新增参数
    required this.onNewShapes,
    required this.strokeWidth,
  }) : super(key: key);

  @override
  _PainterState createState() => _PainterState();
}

class _PainterState extends State<Painter> {
  DrawingShape? currentShape;

  void _startShape(Offset position) {
    Paint paint = Paint()
      ..color = widget.color
      ..strokeWidth = widget.strokeWidth
      ..style = PaintingStyle.stroke; // 设置为stroke绘制空心图形

    switch (widget.selectedTool) {
      // 使用当前选中的工具创建形状
      case 'pen':
        currentShape = DrawingLine([position], paint);
        break;
      case 'rectangle':
        currentShape = DrawingRectangle(position, position, paint);
        break;
      case 'circle':
        currentShape = DrawingCircle(position, 0, paint);
        break;
      default:
        break;
    }
    if (currentShape != null) {
      widget.onNewShapes([...widget.shapes, currentShape!]);
    }
  }

  void _updateShape(Offset position) {
    if (currentShape is DrawingLine) {
      (currentShape as DrawingLine).points.add(position);
    } else if (currentShape is DrawingRectangle) {
      (currentShape as DrawingRectangle).endPoint = position;
    } else if (currentShape is DrawingCircle) {
      double radius =
          ((currentShape as DrawingCircle).center - position).distance;
      (currentShape as DrawingCircle).radius = radius;
    }
    widget.onNewShapes(List.from(widget.shapes));
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onPanStart: (details) {
        RenderBox renderBox = context.findRenderObject() as RenderBox;
        Offset localPosition = renderBox.globalToLocal(details.globalPosition);
        _startShape(localPosition);
      },
      onPanUpdate: (details) {
        RenderBox renderBox = context.findRenderObject() as RenderBox;
        Offset localPosition = renderBox.globalToLocal(details.globalPosition);
        _updateShape(localPosition);
      },
      onPanEnd: (details) {
        currentShape = null; // End the current drawing
      },
      child: CustomPaint(
        painter: DrawingPainter(widget.shapes),
        child: Container(),
      ),
    );
  }

  void _handleDrawing(DragUpdateDetails details) {
    RenderBox renderBox = context.findRenderObject() as RenderBox;
    Offset localPosition = renderBox.globalToLocal(details.globalPosition);
    _updateShape(localPosition);
  }

  void _finishDrawing() {
    setState(() => currentShape = null);
  }
}
