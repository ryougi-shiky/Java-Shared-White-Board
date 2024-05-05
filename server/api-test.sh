#!/bin/bash

# 定义服务器地址
SERVER_URL="http://localhost:8088"

# 测试注册用户接口
echo "Testing user registration..."
curl -X POST "${SERVER_URL}/register" -d "username=testuser" -H "Content-Type: application/x-www-form-urlencoded"
echo -e "\n"

# 测试创建房间接口
echo "Testing room creation..."
curl -X POST "${SERVER_URL}/rooms/create" -d "username=testuser" -H "Content-Type: application/x-www-form-urlencoded"
echo -e "\n"

# 测试加入房间接口
echo "Testing join room..."
curl -X POST "${SERVER_URL}/rooms/join" -d "roomId=room1&username=testuser" -H "Content-Type: application/x-www-form-urlencoded"
echo -e "\n"

# 测试离开房间接口
echo "Testing leave room..."
curl -X POST "${SERVER_URL}/rooms/leave" -d "roomId=room1&username=testuser" -H "Content-Type: application/x-www-form-urlencoded"
echo -e "\n"

# 测试获取所有房间接口
echo "Testing get all rooms..."
curl -X GET "${SERVER_URL}/listrooms"
echo -e "\n"

# 测试获取房间内所有参与者接口
echo "Testing get participants..."
curl -X GET "${SERVER_URL}/rooms/getparticipants" -d "roomId=room1"
echo -e "\n"
