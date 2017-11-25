import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author QuyLD
 * Both implement IServer and Register Naming registry
 */
public class Server  extends java.rmi.server.UnicastRemoteObject
implements IServer, Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	private LoadBalancer balancer;
	private  boolean isAvailable = true;
	private String ID;
	private int priority = -1;
	private String BalancerIP;
	private int registryport = 1099;
	
	//TODO: declare a queue to accept request
	
	//default constructor
	protected Server() throws RemoteException {
		super();
		
	}
	
	public Server(String balancerip, int _registryport,String ID, int _priority)  throws RemoteException, MalformedURLException, NotBoundException{
		this.BalancerIP = balancerip;
		this.registryport = _registryport;
		this.ID = ID;
		this.priority = _priority;
       
        
	}
	public boolean isAvailable(  ) { return isAvailable;}

	public synchronized String addPatient(String patientName, int seconds) throws RemoteException {
		
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

	public String getID() throws RemoteException {
		return this.ID;
	}

	public int getPriority() throws RemoteException {
		return this.priority;
	}

	@Override
	public void run() {
		
		try {
			if ( System.getSecurityManager() == null ) {
			
				
			    System.setProperty("java.security.policy", System.class.getResource("/resources/java.policy").toString());
			    System.setSecurityManager( new SecurityManager() );
			}

			this.balancer = (LoadBalancer) Naming.lookup("rmi://" + BalancerIP + ":" + registryport + "/LoadBalancer");
			this.balancer.RegisterServer((Server) this);
			LOGGER.setLevel(Level.INFO);
			LOGGER.info("Server :" + ID + " is available:");
			
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
