import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.Remote;
/**
 * @author QuyLD
 * Both implement IHospital and Register Naming registry
 */
public class HospitalServer  extends java.rmi.server.UnicastRemoteObject 
implements IHospital, Remote {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	private String name;
	private int id;
	private boolean isAvailable;
	
	//default constructorf
	protected HospitalServer() throws RemoteException {
		super();
	}
	
	public HospitalServer(String name, int id)  throws RemoteException, MalformedURLException, NotBoundException{
		this.name = name;
		this.id = id;
	}

	@Override
	public boolean isAvailable() { 
		return isAvailable;
	}
	 
	@Override
	public  String addPatient(String patientName, int seconds, int numId) throws RemoteException {
		System.out.println("Obtained patient " + patientName + " on server with id " + numId);		
		try {
			isAvailable = false;
			//wait for processing
			Thread.sleep(seconds*1000);
			isAvailable = true;			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Patient :" + patientName + " is treated successfully on server with id " + numId;
	}
	
	public static void main (String[] args) {

		try {
			Registry reg = LocateRegistry.getRegistry("localhost");
			String nameTemplate = "server";
			for (int i=0; i<3; i++){
				reg.bind(Integer.toString(i),new HospitalServer(nameTemplate+i,i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
