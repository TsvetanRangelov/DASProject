import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;

public class Main {
    public static void main(String[] args){
        try{
            LocateRegistry.createRegistry(1099);
            System.out.println("Created reg");
            while(true){}
        } catch (RemoteException e){
            e.printStackTrace();
        }
    }
}