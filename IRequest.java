/**
 *
 */


import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRequest extends Remote {
    public String getID () throws RemoteException;
    //get hospital Priority: higher priority 1,2,3,4,5
    public int getProcessingTime() throws RemoteException;
}
