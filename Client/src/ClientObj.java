import java.rmi.server.UnicastRemoteObject;

public class ClientObj extends UnicastRemoteObject implements ClientInterface{
    // Constructor, GUI, and event listeners

    @Override
    public void receiveMessage(String message) {
        // Display the message in the chat window
    }

    @Override
    public void updateWhiteboard(WhiteboardItem item) {
        // Update the local whiteboard state
    }

    // Additional methods for advanced features
}
