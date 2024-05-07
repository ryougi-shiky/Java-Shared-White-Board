import 'package:flutter/material.dart';
import 'package:android/models/draw_shape.dart';

class DrawingPainter extends CustomPainter {
  final List<DrawingShape> shapes;
  final TextPainter textPainter = TextPainter(
    textAlign: TextAlign.left,
    textDirection: TextDirection.ltr,
  );

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
        DrawingText textShape = shape as DrawingText;
        textPainter.text = TextSpan(
            style: TextStyle(color: textShape.paint.color),
            text: textShape.text);
        textPainter.layout();
        textPainter.paint(canvas, textShape.position);
      }
    }
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => true;
}
