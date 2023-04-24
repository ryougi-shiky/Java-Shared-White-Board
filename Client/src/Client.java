import java.rmi.Naming;
import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;

public class Client {
    public static void main(String[] args) {
//        String serverIPAddress = args[0];
//        int serverPort = Integer.parseInt(args[1]);
//        String username = args[2];

        try {
            ServerInterface client = (ServerInterface) Naming.lookup("rmi://localhost/ServerRemoteObj");
            String response = client.sayHello();
            System.out.println("Response from the remote object: " + response);
            response = client.join("client 1");
            System.out.println("Response from the remote object: " + response);
        } catch (RemoteException e){
            e.printStackTrace();
        } catch (MalformedURLException e){
            System.err.println("MalformedURLException: " + e.getMessage());
        } catch (NotBoundException e){
            System.err.println("Remote object name is not currently bound: " + e.getMessage());
        }
    }
}
