import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public class WrapperQuasiBalancer extends UnicastRemoteObject implements LoadBalancer, Runnable {
	
	
	private QuasiBalancer QuasiBalancer;
	private static final long serialVersionUID = 1L;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	 
	// balancer contain list of Server

	private HashMap<String, IServer> servers = null;
	private HashMap<String, IRequest> requests = null;

	protected WrapperQuasiBalancer() throws RemoteException {
		super();
	}

	public WrapperQuasiBalancer(int port) throws RemoteException,IOException {
		this.QuasiBalancer = new QuasiBalancer(port, this);
		this.servers = new HashMap<String, IServer>();
		this.requests = new HashMap<String, IRequest>();
		MyLogger.setup();
		if (System.getSecurityManager() == null) {
			String abc = System.class.getResource("/resources/java.policy").toString();
			System.out.println(abc);
			System.setProperty("java.security.policy", System.class.getResource("/resources/java.policy").toString());
			System.setSecurityManager(new SecurityManager());
		}

		try {
			System.out.println("rmi://localhost:" + port + "/LoadBalancer");
			Naming.rebind("rmi://localhost:" + port + "/LoadBalancer", this);

			System.out.println("Establishing wrapper balancer successfully");

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * Weighted Round robin 1. Check server available or not 2. If there are more
	 * than 2 server avaialble, request will be redirected to server which has higher priority
	 */
	public void addRequest(IRequest request) throws RemoteException {
		requests.put(request.getID(), request);
		QuasiBalancer.processRequest(request.getPriority(),request.getID());
	}
	
	@Override
	public void RegisterServer(IServer server) throws RemoteException {
		synchronized(servers) {	//Is synchronized so that the logs are always properly reflect the order of events
			servers.put(server.getID(),server);
			QuasiBalancer.addServer(server.getID());
		}
	}

	@Override
	public void UnregisterServer(IServer server) throws RemoteException {
		synchronized (servers) {
			servers.remove(server.getID());
			LOGGER.setLevel(Level.INFO);
			LOGGER.info("Remove server " + server.getID() + " from registry");
			
		}
	}

	public void dispatch(String requestID, String id) throws RemoteException {
		System.out.println(id+" "+requestID);
		servers.get(id).addPatient(requests.get(requestID));
		requests.remove(requestID);
	}

	@Override
	public void changeWeight(IServer server) throws RemoteException {
		// Unapplicable
		
	}

	@Override
	public void run() {
		
	}
}
