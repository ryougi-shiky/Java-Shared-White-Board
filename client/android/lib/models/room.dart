import 'user.dart';

class Room {
  final String id;
  final User owner;
  final List<User> participants;

  Room({required this.id, required this.owner, required this.participants});

  factory Room.fromJson(Map<String, dynamic> json) {
    var list = json['participants'] as List;
    List<User> participantsList = list.map((i) => User.fromJson(i)).toList();
    return Room(
      id: json['id'],
      owner: User.fromJson(json['owner']),
      participants: participantsList,
    );
  }
}
