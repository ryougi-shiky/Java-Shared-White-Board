import 'dart:convert';
import 'package:flutter/material.dart';

import 'draw_shape.dart';

class DrawingAction {
  String type; // Types: "line", "rectangle", "circle", "text", etc.
  double startX;
  double startY;
  double endX;
  double endY;
  String color; // Color in hex string format, e.g., "#FFFFFF"
  double strokeWidth;

  DrawingAction({
    required this.type,
    required this.startX,
    required this.startY,
    required this.endX,
    required this.endY,
    required this.color,
    required this.strokeWidth,
  });

  Map<String, dynamic> toJson() {
    return {
      'type': type,
      'startX': startX,
      'startY': startY,
      'endX': endX,
      'endY': endY,
      'color': color,
      'strokeWidth': strokeWidth,
    };
  }

  factory DrawingAction.fromJson(Map<String, dynamic> json) {
    return DrawingAction(
      type: json['type'],
      startX: json['startX'].toDouble(),
      startY: json['startY'].toDouble(),
      endX: json['endX'].toDouble(),
      endY: json['endY'].toDouble(),
      color: json['color'],
      strokeWidth: json['strokeWidth'].toDouble(),
    );
  }

  DrawingShape toDrawingShape() {
    Paint paint = Paint()
      ..color = Color(int.parse(color, radix: 16) |
          0xFF000000) // Ensures opacity byte is added
      ..strokeWidth = strokeWidth
      ..style = PaintingStyle.stroke;

    switch (type) {
      case 'line':
        return DrawingLine([Offset(startX, startY), Offset(endX, endY)], paint);
      case 'rectangle':
        return DrawingRectangle(
            Offset(startX, startY), Offset(endX, endY), paint);
      case 'circle':
        double radius =
            ((Offset(startX, startY) - Offset(endX, endY)).distance / 2);
        Offset center = Offset((startX + endX) / 2, (startY + endY) / 2);
        return DrawingCircle(center, radius, paint);
      default:
        throw Exception('Unsupported shape type');
    }
  }

  DrawingAction createDrawingActionFromCurrentState(
      String type, Offset start, Offset end, String color, double strokeWidth) {
    return DrawingAction(
        type: type,
        startX: start.dx,
        startY: start.dy,
        endX: end.dx,
        endY: end.dy,
        color: color, // This should be a hex string like 'FFFFFF'
        strokeWidth: strokeWidth);
  }
}
