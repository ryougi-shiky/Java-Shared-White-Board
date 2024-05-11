from websocket import create_connection
import base64

url = "ws://127.0.0.1:8088/ws"
username = "admin"
password = "admin"
basic_auth = base64.b64encode(f"{username}:{password}".encode()).decode("ascii")
headers = {
    "Authorization": f"Basic {basic_auth}"
}

try:
    ws = create_connection(url, header=headers)
    print("Connection established")
    ws.close()
except Exception as e:
    print(f"Failed to connect: {e}")
