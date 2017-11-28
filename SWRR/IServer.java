import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IServer extends Remote {
	//Can server know PatientClient object - implement Serializable
		public abstract String addPatient(Request client) throws RemoteException;
		
		public boolean getServerStatus() throws RemoteException;
		
		//get hospital ID
	    public String getServerID () throws RemoteException;
	   
	    public int getCapacity() throws RemoteException;
}
