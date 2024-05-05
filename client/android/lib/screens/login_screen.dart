import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  _LoginScreenState createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final TextEditingController _usernameController = TextEditingController();

  Future<void> registerUser() async {
    var authUser = dotenv.env['USER'] ?? "admin";
    var authPwd = dotenv.env['PASSWORD'] ?? "admin";
    var serverUrl = dotenv.env['SERVER_URL'] ?? "http://defaultserver/";
    var url = Uri.parse(
        '$serverUrl/register?username=${_usernameController.text}');

    String basicAuth =
        'Basic ' + base64Encode(utf8.encode('$authUser:$authPwd'));

    try {
      var response = await http.post(
        url,
        headers: <String, String>{
          'authorization': basicAuth,
        },
      );

      // 打印响应状态码和响应体
      print('Response status: ${response.statusCode}');
      print('Response body: ${response.body}');

      if (response.statusCode == 200) {
        // If registration is successful, navigate to the next screen
        Navigator.pushReplacementNamed(context, '/mainScreen',
            arguments: _usernameController.text);
      } else {
        // Handling errors, parse the error message from the response
        var errorData = jsonDecode(response.body);
        showErrorDialog(errorData['message'] ?? 'Registration failed');
      }
    } catch (e) {
      print('Exception caught: $e');
      showErrorDialog('Failed to connect to the server.');
    }
  }

  void showErrorDialog(String message) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Error'),
        content: Text(message),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text('OK'),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      // appBar: AppBar(
      //   title: const Text('Enter Your Username'),
      // ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            const Text(
              'Drawing with Your Friends Anywhere',
              textAlign: TextAlign.center,
              style: TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
                color: Colors.blue,
              ),
            ),
            const SizedBox(height: 20),
            TextField(
              controller: _usernameController,
              decoration: const InputDecoration(
                labelText: 'Username',
              ),
            ),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: registerUser,
              child: const Text('Start'),
            ),
          ],
        ),
      ),
    );
  }
}
