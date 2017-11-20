import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IHospitalBalancer extends Remote {
    
	public String assignPatient(String name, int time) throws RemoteException;
}
