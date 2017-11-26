import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Server extends java.rmi.server.UnicastRemoteObject implements IServer, Runnable {

	private LoadBalancer balancer;
	private  boolean isAvailable = true;
	private String ID;
	private int processingSpeed = 1;
	private String BalancerIP;
	private int registryport = 1099;
	private int totalProcessingTime = 0;
	private int capacity = 1;
	private Queue<IRequest> requests = null;

	protected Server() throws RemoteException {
		super();
		
	}
	
	public Server(String balancerip, int _registryport, String ID, int speed, int capacity)  throws RemoteException, MalformedURLException, NotBoundException{
		this.BalancerIP = balancerip;
		this.registryport = _registryport;
		this.ID = ID;
		this.processingSpeed = speed;
       	this.requests = new LinkedList<>();
        this.capacity = capacity;
	}

	@Override
	public boolean isAvailable() {
		return isAvailable;
	}

	@Override
	public synchronized void addPatient(IRequest request) throws RemoteException {
		requests.add(request);
		if (requests.size() == capacity)
			isAvailable = false;
	}

	@Override
	public String getID() throws RemoteException {
		return this.ID;
	}

	@Override
	public int getProcessingSpeed() throws RemoteException {
		return this.processingSpeed;
	}

	private boolean processRequest(IRequest request) throws RemoteException {
		int tts = (int) Math.ceil( (double) request.getProcessingTime() / (double) processingSpeed);
		try {
			Thread.sleep(tts);
			totalProcessingTime += tts;
			return true;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void run() {
		try {
			if (System.getSecurityManager() == null) {
			    System.setProperty("java.security.policy", System.class.getResource("/resources/java.policy").toString());
			    System.setSecurityManager( new SecurityManager() );
			}
			this.balancer = (LoadBalancer) Naming.lookup("rmi://" + BalancerIP + ":" + registryport + "/LoadBalancer");
			this.balancer.RegisterServer(this);
			System.out.printf("Registered server %s\n", ID);
			while (true) {
				if (!requests.isEmpty()) {
					IRequest curRequest = requests.peek();
					if (this.processRequest(curRequest)) {
						requests.poll();
						isAvailable = true;
						System.out.printf("Server %s processed %s with processing time %d\n", ID, curRequest.getID(), curRequest.getProcessingTime());
						System.out.printf("Total server %s processing time: %d\n", ID, totalProcessingTime);
					}
				}
				else {
					Thread.sleep(100);
				}
			}
		}
		catch (MalformedURLException|RemoteException|SecurityException|NotBoundException|InterruptedException e) {
			e.printStackTrace();
		}
	}

	
}
