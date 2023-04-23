import java.rmi.Naming;

public class Client {
    public static void main(String[] args) {
        try {
            ServerInterface remoteObject = (ServerInterface) Naming.lookup("rmi://localhost/ServerRemoteObj");
            String response = remoteObject.sayHello();
            System.out.println("Response from the remote object: " + response);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
