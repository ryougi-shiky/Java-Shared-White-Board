import 'package:flutter/material.dart';
import 'drawing_painter.dart'; // Import your custom painter for drawing the shapes
import 'package:android/models/draw_shape.dart'; // Ensure your model definitions are correct
import 'package:android/models/draw_action.dart';

class Painter extends StatefulWidget {
  final List<DrawingShape> shapes;
  final Color color;
  final String selectedTool; // 新增选中工具的属性
  final double strokeWidth;
  final Function(List<DrawingShape>) onNewShapes;
  final Function(DrawingAction) onDrawUpdate;

  Painter({
    Key? key,
    required this.shapes,
    required this.color,
    required this.selectedTool,
    required this.onNewShapes,
    required this.strokeWidth,
    required this.onDrawUpdate,
  }) : super(key: key);

  @override
  _PainterState createState() => _PainterState();
}

class _PainterState extends State<Painter> {
  Paint paint = Paint()
    ..strokeCap = StrokeCap.round
    ..strokeWidth = 3.0
    ..style = PaintingStyle.stroke; // Stroke style for hollow shapes

  DrawingShape? currentShape;
  Offset startPoint = Offset.zero;

  @override
  void initState() {
    super.initState();
    paint.color = widget.color; // 初始化时设置颜色
  }

  void _startShape(Offset position) {
    startPoint = position;
    Paint paint = Paint()
      ..color = widget.color
      ..strokeWidth = widget.strokeWidth
      ..style = PaintingStyle.stroke; // 设置为stroke绘制空心图形

    switch (widget.selectedTool) {
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

    if (currentShape != null) {
      DrawingAction action = DrawingAction(
        type: widget.selectedTool,
        startX: startPoint.dx,
        startY: startPoint.dy,
        endX: position.dx,
        endY: position.dy,
        color: widget.color.value.toRadixString(16),
        strokeWidth: widget.strokeWidth,
      );
      widget.onDrawUpdate(action); // 持续更新绘图数据
    }
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
        currentShape = null; // Reset the current shape when drawing is finished
      },
      // onTapDown: (details) {
      //   if (widget.onSelectPosition != null) {
      //     RenderBox renderBox = context.findRenderObject() as RenderBox;
      //     Offset localPosition =
      //         renderBox.globalToLocal(details.globalPosition);
      //     widget.onSelectPosition!(
      //         localPosition); // Use widget's callback if not null
      //   }
      // },
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
