
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Request extends java.rmi.server.UnicastRemoteObject implements IRequest {

	private String ID;
	private String toIP;
	private int toPort = 1099;
	private String url = null;
	private int processingTime = 0;
	private LoadBalancer balancer;
	public Request(String ID, String _toIP, int port, int _pTime) throws RemoteException {
		this.ID = ID;
		this.toIP = _toIP;
		this.toPort = port;
		url = String.format("rmi://%s:%d/LoadBalancer", toIP, toPort);
		this.processingTime = _pTime;

		if ( System.getSecurityManager() == null ) {
			System.setProperty("java.security.policy", System.class.getResource("/resources/java.policy").toString());
			System.setSecurityManager( new SecurityManager() );
		}
		try {
			this.balancer = (LoadBalancer) Naming.lookup(url);
			this.balancer.addRequest(this);
		}
		catch (RemoteException|NotBoundException|MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public int getProcessingTime() throws RemoteException {
		return processingTime;
	}

	public String getID() throws RemoteException {
		return ID;
	}
}
