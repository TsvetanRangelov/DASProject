import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.HashMap;

public class DynamicPriorityBalancer extends UnicastRemoteObject implements LoadBalancer, Runnable {

	private class ServerHelper {
		private String ID;
		private int speed;

		public ServerHelper(String ID, int speed) {
			this.ID = ID;
			this.speed = speed;
		}

		public String getID() {
			return ID;
		}

		public int getSpeed() {
			return speed;
		}

		public void setSpeed(int s) {
			speed = s;
		}
	}

	private List<String> servers = null;
	private TreeSet<ServerHelper> serverPriorities = null;
	private HashMap<String, ServerHelper> IDToHelper = null;
	private ConcurrentLinkedQueue<IRequest> requests = null;
	private boolean serversStarted = false;

	protected DynamicPriorityBalancer() throws RemoteException {
		super();
	}

	public DynamicPriorityBalancer(int port) throws RemoteException,IOException {
		this.servers = new ArrayList<>();
		this.requests = new ConcurrentLinkedQueue<>();
		this.IDToHelper = new HashMap<>();
		Comparator<ServerHelper> serverComparator = new Comparator<ServerHelper>() {
			@Override
			public int compare(ServerHelper a, ServerHelper b) {
				return b.getSpeed() - a.getSpeed();
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
		String sID = server.getID();
		servers.add(sID);
		ServerHelper sh = new ServerHelper(server.getID(), server.getProcessingSpeed());
		serverPriorities.add(sh);
		IDToHelper.put(sID, sh);
		serversStarted = true;
	}

	@Override
	public synchronized void UnregisterServer(IServer server) throws RemoteException {
		String sID = server.getID();
		servers.remove(sID);
		ServerHelper toBeRemoved = IDToHelper.get(sID);
		serverPriorities.remove(toBeRemoved);
		IDToHelper.remove(sID);
	}

	public void addRequest(IRequest request) throws RemoteException {
		requests.add(request);
	}

	public void changeWeight(IServer server) throws RemoteException {
		ServerHelper toBeChanged = IDToHelper.get(server.getID());
		serverPriorities.remove(toBeChanged);
		toBeChanged.setSpeed(server.getProcessingSpeed());
		serverPriorities.add(toBeChanged);
	}

	@Override
	public void run() {
		while (servers.size() > 0 || !serversStarted) {
			if (!requests.isEmpty()) {
				try {
					IServer curServer = null;
					for (ServerHelper s : serverPriorities) {
						String curServerURL = String.format("rmi://localhost:1099/%s", s.getID());
						IServer tempServer = (IServer) Naming.lookup(curServerURL);
						if (tempServer.isAvailable()){
							curServer = tempServer;
							break;
						}
					}
					if (curServer != null) {
						IRequest curRequest = requests.poll();
						curServer.addPatient(curRequest);
					}
					else {
						Thread.sleep(50);
//						System.out.printf("Server %s at full capacity, cannot store %s\n", curServer.getID(), curRequest.getID());
					}
				} catch (NotBoundException|MalformedURLException|RemoteException|InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("Load balancer shutting down");
	}
}
