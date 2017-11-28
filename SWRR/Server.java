import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends java.rmi.server.UnicastRemoteObject 
implements IServer,Runnable {

	
	private static final long serialVersionUID = 1L;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	private IBalancer balancer;
	private  boolean isAvailable = true;
	private String serverID;
	private String BalancerIP = "127.0.0.1";
	private int registryport = 1099;
	private int CAPACITY =10;
	private int numberOfRequest = 0;
	private RequestProcessor rp = null;
	
		
	//default constructorf
	protected Server() throws RemoteException {
		super();
		
	}
	
	public Server(String balancerip, int _registryport,String _serverName, int capacity)  throws RemoteException, MalformedURLException, NotBoundException{
		this.BalancerIP = balancerip;
		this.registryport = _registryport;
		this.serverID = _serverName;
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
	public  String addPatient(Request client) throws RemoteException {
	   
		addRequest();
		rp = new RequestProcessor(client);
		Thread t = new Thread(rp);
		t.start();
		while (t.isAlive()) {
			//waiting for processing
		}
		//out of thread is stop
		releaseRequest();
		return "Client:" + client.RequestName() + "has been treated successfully from " + this.getServerID();
	}
	
	@Override
	public String getServerID() throws RemoteException {
		
		return this.serverID;
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

	@Override
	public  void run() {
		try {
			if ( System.getSecurityManager() == null ) {
								
			    System.setProperty("java.security.policy", System.class.getResource("/java.policy").toString());
			    System.setSecurityManager( new SecurityManager() );
			}
			
			//Server server = new Server("127.0.0.1", 1099,args[0].toString(),Integer.parseInt(args[1]));
			this.balancer = (IBalancer) Naming.lookup("rmi://" + this.BalancerIP + ":" + this.registryport + "/WRRBalancer");
			this.balancer.RegisterServer(this);
			LOGGER.setLevel(Level.INFO);
			LOGGER.info("Server :" + this.getServerID()+ " is available with capacity:" + this.getCapacity());
			
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
	

}
