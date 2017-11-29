import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class WRRBalancer extends UnicastRemoteObject implements
ISWRRBalancer,ISWRRServer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	 
	// balancer contain list of Server

	private List<ISWRRServer> servers = null;


	protected WRRBalancer() throws RemoteException {
		super();
	}

	public WRRBalancer(int port) throws RemoteException,IOException {
		this.servers = new ArrayList<>();
	
		MyLogger.setup();
		if (System.getSecurityManager() == null) {
			String abc = System.class.getResource("/resources/java.policy").toString();
			System.out.println(abc);
			System.setProperty("java.security.policy", System.class.getResource("/resources/java.policy").toString());
			System.setSecurityManager(new SecurityManager());
		}

		try {
			Naming.rebind("rmi://localhost:" + port + "/WRRBalancer", this);

			System.out.println("Establishing load balancer successfully");

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * Weighted Round robin 
	 * 1. Choose to forward request to server which has highest capacity and avaiable
	 * 
	 */
	@Override
	public  String addPatient(ClientRequest request) throws RemoteException {

		if (servers.size() > 0) {

			ArrayList<ISWRRServer> lstServer = new ArrayList<>();
			ISWRRServer chosenServer = null;
			ISWRRServer server = null;
			int maxCapacity = 0;
			
			   synchronized (servers) {
				   //iterate all server
				   for (int i = 0; i < servers.size(); i++) {
					   server = (ISWRRServer)servers.get(i);
					  LOGGER.setLevel(Level.INFO);
					  LOGGER.info("Hospital :" + server.getServerID() + " is available:" + server.getServerStatus());
					   if (server.getServerStatus()) {
						  //get available server	
						   lstServer.add(server);
						   //get max capacity for hospital
							if (maxCapacity < server.getCapacity()) 
							{
								maxCapacity = server.getCapacity();
							}
						}
					   				   
					}
				   //iterate all avaialbe server - get highest priority hospital 
				   for (ISWRRServer iServer : lstServer) {
					if (iServer.getCapacity() == maxCapacity)
					{
						chosenServer = iServer;
					}
				   }
				  				
			  }
			   
			   if (chosenServer != null) {
					
					LOGGER.setLevel(Level.INFO);
					LOGGER.info("Request from client  " + request.RequestName() + " is transfered to server: " + chosenServer.getServerID());
						
					String result = chosenServer.addPatient(request);
					LOGGER.setLevel(Level.INFO);
					LOGGER.info(result);
					return result;
				}
	
			else {
				LOGGER.setLevel(Level.WARNING);
				LOGGER.info("All server are busy now. Please wait...");
				return null;
			}
	    } else 
		{
			LOGGER.setLevel(Level.INFO);
			LOGGER.info("No server is available ");
			
		}
		return null;
	}

	@Override
	public void RegisterServer(ISWRRServer server) throws RemoteException {
		servers.add(server);
		LOGGER.setLevel(Level.INFO);
		LOGGER.info("Server with the ID " + server.getServerID() + " registered at the registry");
	}

	@Override
	public void UnregisterServer(ISWRRServer server) throws RemoteException {
		synchronized (servers) {
			servers.remove(server);
			LOGGER.setLevel(Level.INFO);
			LOGGER.info("Remove server " + server.getServerID() + " from registry");
			
		}

	}

	@Override
	public boolean getServerStatus() throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getServerID() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCapacity() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String checkAlive() throws RemoteException {
		return "I'm still alive";
	}

}
