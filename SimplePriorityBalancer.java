import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SimplePriorityBalancer extends UnicastRemoteObject implements LoadBalancer {

	private static final long serialVersionUID = 1L;
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	 
	// balancer contain list of Server

	private List<IServer> servers = null;

	protected SimplePriorityBalancer() throws RemoteException {
		super();
	}

	public SimplePriorityBalancer(int port) throws RemoteException,IOException {
		this.servers = new ArrayList<>();
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

			System.out.println("Establishing load balancer successfully");

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * Weighted Round robin 1. Check server available or not 2. If there are more
	 * than 2 server avaialble, request will be redirected to server which has higher priority
	 */
	public String processRequest(String patientName, int processedTime) throws RemoteException {

		if (servers.size() > 0) {

			ArrayList<IServer> availableServers = new ArrayList<>();
			IServer chosenServer = null;
			int maxPriority = 0;
			synchronized (servers) {
				
				IServer server = null;

				for (int i = 0; i < servers.size(); i++) {
					server = servers.get(i);
					LOGGER.setLevel(Level.INFO);
					LOGGER.info("Server :" + server.getID() + " is available:" + server.isAvailable());
					
					// Weighted Round robin - if server isAvaialble, it will accept request
					if (server.isAvailable()) {
						availableServers.add(server);
						if (maxPriority < server.getPriority()) {
							maxPriority = server.getPriority();
						}
					}
				}

				for (IServer avServer : availableServers) {
					if (avServer.getPriority() == maxPriority) {
						chosenServer = avServer;
					}
				}

				if (chosenServer != null) {
					LOGGER.setLevel(Level.INFO);
					LOGGER.info("Request from client  " + patientName + " is transfered to server: " + chosenServer.getID());
				}

			}

			if (chosenServer != null)
				return chosenServer.addPatient(patientName, processedTime);
			else {
				LOGGER.setLevel(Level.WARNING);
				LOGGER.info("All server are busy now. Please wait...");
				return null;
			}
				

		} else {
			LOGGER.setLevel(Level.INFO);
			LOGGER.info("No server is available ");
			
		}
		return null;
	}

	@Override
	public void RegisterServer(IServer server) throws RemoteException {
		servers.add(server);
		LOGGER.setLevel(Level.INFO);
		LOGGER.info("Hospital with the ID " + server.getID() + " registered at the registry");
	}

	@Override
	public void UnregisterServer(IServer server) throws RemoteException {
		synchronized (servers) {
			servers.remove(server);
			LOGGER.setLevel(Level.INFO);
			LOGGER.info("Remove server " + server.getID() + " from registry");
			
		}
	}
}
