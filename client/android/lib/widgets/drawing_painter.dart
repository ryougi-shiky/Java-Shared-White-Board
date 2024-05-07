import 'package:flutter/material.dart';
import 'package:android/models/draw_shape.dart';

class DrawingPainter extends CustomPainter {
  final List<DrawingShape> shapes;

  DrawingPainter(this.shapes);

  @override
  void paint(Canvas canvas, Size size) {
    for (DrawingShape shape in shapes) {
      shape.paint.strokeCap = StrokeCap.round;
      if (shape is DrawingLine) {
        for (int i = 0; i < shape.points.length - 1; i++) {
          canvas.drawLine(shape.points[i], shape.points[i + 1], shape.paint);
        }
      } else if (shape is DrawingRectangle) {
        Rect rect = Rect.fromPoints(shape.startPoint, shape.endPoint);
        canvas.drawRect(rect, shape.paint);
      } else if (shape is DrawingCircle) {
        canvas.drawCircle(shape.center, shape.radius, shape.paint);
      } else if (shape is DrawingText) {
        TextSpan span = TextSpan(style: TextStyle(color: shape.paint.color), text: shape.text);
        TextPainter tp = TextPainter(text: span, textAlign: TextAlign.left, textDirection: TextDirection.ltr);
        tp.layout();
        tp.paint(canvas, shape.position);
      }
    }
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => true;
}
