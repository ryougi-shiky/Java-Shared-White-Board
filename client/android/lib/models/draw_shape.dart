import 'package:flutter/material.dart';

abstract class DrawingShape {
  Paint paint;
  DrawingShape(this.paint);
}

class DrawingPoint extends DrawingShape {
  Offset point;
  DrawingPoint(this.point, Paint paint) : super(paint);
}

class DrawingLine extends DrawingShape {
  List<Offset> points;
  DrawingLine(this.points, Paint paint) : super(paint);
}

class DrawingRectangle extends DrawingShape {
  Offset startPoint;
  Offset endPoint;
  DrawingRectangle(this.startPoint, this.endPoint, Paint paint) : super(paint);
}

class DrawingCircle extends DrawingShape {
  Offset center;
  double radius;
  DrawingCircle(this.center, this.radius, Paint paint) : super(paint);
}

class DrawingText extends DrawingShape {
  String text;
  Offset position;
  DrawingText(this.text, this.position, Paint paint) : super(paint);
}
