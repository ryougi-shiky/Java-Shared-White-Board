import 'package:flutter/material.dart';
import 'drawing_painter.dart';
import 'package:android/models/draw_shape.dart';
import 'package:android/models/draw_action.dart';

class Painter extends StatefulWidget {
  final List<DrawingShape> shapes;
  final Color color;
  final String selectedTool;
  final double strokeWidth;
  final Function(List<DrawingShape>) onNewShapes;
  final Function(DrawingAction) onDrawUpdate;
  final Function(DrawingAction) onDrawEnd;

  Painter({
    Key? key,
    required this.shapes,
    required this.color,
    required this.selectedTool,
    required this.onNewShapes,
    required this.strokeWidth,
    required this.onDrawUpdate,
    required this.onDrawEnd,
  }) : super(key: key);

  @override
  _PainterState createState() => _PainterState();
}

class _PainterState extends State<Painter> {
  Paint paint = Paint()
    ..strokeCap = StrokeCap.round
    ..strokeWidth = 3.0
    ..style = PaintingStyle.stroke;

  DrawingShape? currentShape;
  Offset startPoint = Offset.zero;
  DrawingAction? currentAction;

  @override
  void initState() {
    super.initState();
    paint.color = widget.color;
  }

  void _startShape(Offset position) {
    startPoint = position;
    Paint paint = Paint()
      ..color = widget.color
      ..strokeWidth = widget.strokeWidth
      ..style = PaintingStyle.stroke;

    switch (widget.selectedTool) {
      case 'line':
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
      currentAction = DrawingAction(
        type: widget.selectedTool,
        startX: startPoint.dx,
        startY: startPoint.dy,
        endX: startPoint.dx,
        endY: startPoint.dy,
        color: widget.color.value.toRadixString(16),
        strokeWidth: widget.strokeWidth,
      );
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

    if (currentAction != null) {
      currentAction!.endX = position.dx;
      currentAction!.endY = position.dy;
      widget.onDrawUpdate(
          currentAction!); // Continuously update the drawing action
    }
  }

  void _endShape() {
    if (currentAction != null) {
      widget.onDrawEnd(currentAction!); // Finalize the drawing action
      currentAction = null;
    }
    currentShape = null; // Reset the current shape when drawing is finished
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
        _endShape();
      },
      child: CustomPaint(
        painter: DrawingPainter(widget.shapes),
        child: Container(),
      ),
    );
  }
}
