/**
 * 
 */


import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author QuyLD
 *
 */
public interface IServer extends Remote {
    //process patient request from client
	public abstract String addPatient(String patientName, int seconds) throws RemoteException;
    //if server is processing a request, it will not available
	public boolean isAvailable() throws RemoteException;
	//get hospital ID
    public String getID () throws RemoteException;
    //get hospital Priority: higher priority 1,2,3,4,5
    public int getPriority() throws RemoteException;
}
