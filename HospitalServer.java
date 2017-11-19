import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author QuyLD
 * Both implement IHospital and Register Naming registry
 */
public class HospitalServer  extends java.rmi.server.UnicastRemoteObject 
implements IHospital,Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	private IHospitalBalancer balancer;
	private  boolean isAvailable = true;
	private String hospitalID;
	private int priority = -1;
	private String BalancerIP;
	private int registryport = 1099;
	
	//TODO: declare a queue to accept request
	
	//default constructorf
	protected HospitalServer() throws RemoteException {
		super();
		
	}
	
	public HospitalServer(String balancerip, int _registryport,String hospitalName, int _priority)  throws RemoteException, MalformedURLException, NotBoundException{
		this.BalancerIP = balancerip;
		this.registryport = _registryport;
		this.hospitalID = hospitalName;
		this.priority = _priority;
       
        
	}
	@Override
	public boolean isAvailable(  ) { return isAvailable;}
	 
	@Override
	public  String addPatient(String patientName, int seconds) throws RemoteException {
		
		try {
			isAvailable = false;
			//wait for processing
			Thread.sleep(seconds*1000);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		isAvailable = true;
		return "Patient :" + patientName + " is treated successfully !";
	}
	
	/*
	public static void main(String []args) {
		try {
			//get user input
			String serverName = args[0];
			int priority = Integer.parseInt(args[1]);
			new HospitalServer("localhost", 1099,serverName,priority);
		} catch (RemoteException | MalformedURLException | NotBoundException e) {
			
			e.printStackTrace();
		}
	}
	*/

	@Override
	public String getHospitalID() throws RemoteException {
		
		return this.hospitalID;
	}

	@Override
	public int getPriority() throws RemoteException {
		return this.priority;
	}

	@Override
	public void run() {
		
		try {
			if ( System.getSecurityManager() == null ) {
			
				
			    System.setProperty("java.security.policy", System.class.getResource("/java.policy").toString());
			    System.setSecurityManager( new SecurityManager() );
			}

			this.balancer = (IHospitalBalancer) Naming.lookup("rmi://" + BalancerIP + ":" + registryport + "/HospitalBalancer");
			this.balancer.RegisterHospitalServer(this);
			LOGGER.setLevel(Level.INFO);
			LOGGER.info("Hospital :" + hospitalID+ " is available:");
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
}
