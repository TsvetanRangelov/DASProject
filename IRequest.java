/**
 *
 */


import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRequest extends Remote {
    String getID () throws RemoteException;
    int getProcessingTime() throws RemoteException;
    int getPriority() throws RemoteException;
}
