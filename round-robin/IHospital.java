/**
 * 
 */


import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author QuyLD
 *
 */
public interface IHospital extends Remote {
    //process patient request from client
	public abstract String addPatient(String patientName, int seconds, int numId) throws RemoteException;
    //if server is processing a request, it will not available
	public boolean isAvailable() throws RemoteException;
}
