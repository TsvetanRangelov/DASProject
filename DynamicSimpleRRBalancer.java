import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DynamicSimpleRRBalancer extends UnicastRemoteObject implements LoadBalancer, Runnable {

	private List<IServer> servers = null;
	private ConcurrentLinkedQueue<IRequest> requests = null;
	private boolean serversStarted = false;

	protected DynamicSimpleRRBalancer() throws RemoteException {
		super();
	}

	public DynamicSimpleRRBalancer(int port) throws RemoteException,IOException {
		this.servers = new ArrayList<>();
		this.requests = new ConcurrentLinkedQueue<>();
		if (System.getSecurityManager() == null) {
			System.class.getResource("/resources/java.policy").toString();
			System.setProperty("java.security.policy", System.class.getResource("/resources/java.policy").toString());
			System.setSecurityManager(new SecurityManager());
		}
		try {
			System.out.println("rmi://localhost:" + port + "/LoadBalancer");
			Naming.rebind("rmi://localhost:" + port + "/LoadBalancer", this);
			System.out.println("Establishing load balancer successfully");

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void RegisterServer(IServer server) throws RemoteException {
		servers.add(server);
		serversStarted = true;
	}

	@Override
	public synchronized void UnregisterServer(IServer server) throws RemoteException {
		servers.remove(server);
	}

	public void addRequest(IRequest request) throws RemoteException {
		requests.add(request);
	}

	public void changeWeight(IServer server) throws RemoteException {
		return;
	}

	@Override
	public void run() {
		int serverTurn = 0;
		while (servers.size() > 0 || !serversStarted) {
			if (!requests.isEmpty()) {
				if (serverTurn >= servers.size())
					serverTurn = serverTurn % servers.size();
				IServer curServer = servers.get(serverTurn);
				IRequest curRequest = requests.peek();
				try {
					if (curServer.isAvailable()) {
						requests.poll();
						servers.get(serverTurn).addPatient(curRequest);
					}
//					else
//						System.out.printf("Server %s at full capacity, cannot store %s\n", curServer.getID(), curRequest.getID());
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				serverTurn++;
			}
		}
		System.out.println("Load balancer shutting down");
	}
}
