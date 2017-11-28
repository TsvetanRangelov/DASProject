package Static;



import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author QuyLD
 *
 */
public interface IHospital extends Remote {
       
	//Can server know PatientClient object - implement Serializable
	public abstract String addPatient(PatientClient client) throws RemoteException;
	
	public boolean getServerStatus() throws RemoteException;
	
	//get hospital ID
    public String getHospitalID () throws RemoteException;
   
    public int getCapacity() throws RemoteException;
 
}
