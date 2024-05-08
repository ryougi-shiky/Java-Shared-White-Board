import 'dart:convert';

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
}
