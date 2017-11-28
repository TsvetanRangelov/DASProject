package Static;
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
implements IHospital {

	
	private static final long serialVersionUID = 1L;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	private IHospitalBalancer balancer;
	private  boolean isAvailable = true;
	private String hospitalID;
	private String BalancerIP = "127.0.0.1";
	private int registryport = 1099;
	private int CAPACITY =10;
	private int numberOfRequest = 0;
	private PatientProcessor pp = null;
		
	//default constructorf
	protected HospitalServer() throws RemoteException {
		super();
		
	}
	
	public HospitalServer(String balancerip, int _registryport,String hospitalName, int capacity)  throws RemoteException, MalformedURLException, NotBoundException{
		this.BalancerIP = balancerip;
		this.registryport = _registryport;
		this.hospitalID = hospitalName;
        this.CAPACITY = capacity;
     
        
	}
	
	public synchronized void addRequest(){
		try {
			if (numberOfRequest < CAPACITY) {
				numberOfRequest ++;
				
			}
			else {
				wait();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void releaseRequest() {
		try {
			if (numberOfRequest > 0) {
				numberOfRequest--;
				notify();
			}
			else {
				wait();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 
	@Override
	public  String addPatient(PatientClient client) throws RemoteException {
	   
		addRequest();
		pp = new PatientProcessor(client);
		Thread t = new Thread(pp);
		t.start();
		while (t.isAlive()) {
			//waiting for processing
		}
		//out of thread is stop
		releaseRequest();
		return "Client:" + client.PatientName() + "has been treated successfully from " + this.getHospitalID();
	}
	
	@Override
	public String getHospitalID() throws RemoteException {
		
		return this.hospitalID;
	}

	

	  public static void main(String[] args) {
		  try {
				if ( System.getSecurityManager() == null ) {
									
				    System.setProperty("java.security.policy", System.class.getResource("/java.policy").toString());
				    System.setSecurityManager( new SecurityManager() );
				}
				//arg0: name,arg: capacity
				
				HospitalServer server = new HospitalServer("127.0.0.1", 1099, args[0].toString(), Integer.parseInt(args[1].toString()));	
				server.balancer = (IHospitalBalancer) Naming.lookup("rmi://" + server.BalancerIP + ":" + server.registryport + "/HospitalBalancer");
				server.balancer.RegisterHospitalServer(server);
				LOGGER.setLevel(Level.INFO);
				LOGGER.info("Hospital :" + server.hospitalID+ " is available with capacity:" + server.getCapacity());
				
			} catch (MalformedURLException e) {
				
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotBoundException e) 
		  {
				
			}
	  }
	  
	@Override
	public boolean getServerStatus() throws RemoteException {
		 isAvailable =numberOfRequest< CAPACITY ? true:false;
		 return isAvailable;
	}

	@Override
	 public int getCapacity() throws RemoteException{
		return this.CAPACITY;
	}
	

	

	

	

	
}
