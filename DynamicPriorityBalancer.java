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
import java.util.TreeSet;
import java.util.Comparator;

public class DynamicPriorityBalancer extends UnicastRemoteObject implements LoadBalancer, Runnable {

	private List<IServer> servers = null;
	private TreeSet<IServer> serverPriorities = null;
	private ConcurrentLinkedQueue<IRequest> requests = null;
	private boolean serversStarted = false;

	protected DynamicPriorityBalancer() throws RemoteException {
		super();
	}

	public DynamicPriorityBalancer(int port) throws RemoteException,IOException {
		this.servers = new ArrayList<>();
		this.requests = new ConcurrentLinkedQueue<>();
		Comparator<IServer> serverComparator = new Comparator<IServer>() {
			@Override
			public int compare(IServer a, IServer b) {
				try {
					return a.getProcessingSpeed() - b.getProcessingSpeed();
				}
				catch (RemoteException e) {
					e.printStackTrace();
				}
				return 0;
			}
		};
		this.serverPriorities = new TreeSet<>(serverComparator);
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
		serverPriorities.add(server);
		serversStarted = true;
	}

	@Override
	public synchronized void UnregisterServer(IServer server) throws RemoteException {
		servers.remove(server);
		serverPriorities.remove(server);
	}

	public void addRequest(IRequest request) throws RemoteException {
		requests.add(request);
	}

	public void changeWeight(IServer server) throws RemoteException {
		serverPriorities.remove(server);
		serverPriorities.add(server);
	}

	@Override
	public void run() {
		while (servers.size() > 0 || !serversStarted) {
			if (!requests.isEmpty()) {
				try {
					IServer curServer = null;
					for (IServer s : serverPriorities) {
						if (s.isAvailable()){
							curServer = s;
							break;
						}
					}
					IRequest curRequest = requests.peek();
					if (curServer != null) {
						requests.poll();
						curServer.addPatient(curRequest);
					}
					else {
						Thread.sleep(50);
//						System.out.printf("Server %s at full capacity, cannot store %s\n", curServer.getID(), curRequest.getID());
					}
				} catch (RemoteException|InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("Load balancer shutting down");
	}
}
