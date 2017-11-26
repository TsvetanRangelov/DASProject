/**
 * 
 */


import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IServer extends Remote {
	void addPatient(IRequest request) throws RemoteException;
	boolean isAvailable() throws RemoteException;
    String getID () throws RemoteException;
    int getProcessingSpeed() throws RemoteException;
}
