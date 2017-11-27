import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.math.BigInteger;

public class DynamicWeightedRRBalancer extends UnicastRemoteObject implements LoadBalancer, Runnable {

	private List<IServer> servers = null;
	private List<Integer> weights;
	private HashMap<IServer, Integer> serverIndexMap;
	private ConcurrentLinkedQueue<IRequest> requests = null;
	private int stepSize = 0;
	private boolean serversStarted = false;

	protected DynamicWeightedRRBalancer() throws RemoteException {
		super();
	}

	public DynamicWeightedRRBalancer(int port) throws RemoteException,IOException {
		this.servers = new ArrayList<>();
		this.weights = new ArrayList<>();
		this.serverIndexMap = new HashMap<>();
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

	private static int gcd(int a, int b) {
		BigInteger b1 = new BigInteger(""+a);
		BigInteger b2 = new BigInteger(""+b);
		BigInteger gcd = b1.gcd(b2);
		return gcd.intValue();
	}

	private void calculateStepSize() {
		int tempStepSize = weights.get(0);
		for (int i = 1; i < weights.size(); i++)
			tempStepSize = gcd(tempStepSize, weights.get(i));
		stepSize = tempStepSize;
	}

	@Override
	public void RegisterServer(IServer server) throws RemoteException {
		servers.add(server);
		weights.add(server.getProcessingSpeed());
		//Map the server to its index in the array for dynamic capacity changes
		serverIndexMap.put(server, servers.size() - 1);
		if (servers.size() == 1)
			stepSize = server.getProcessingSpeed();
		else
			stepSize = gcd(stepSize, server.getProcessingSpeed());
		serversStarted = true;
	}

	@Override
	public synchronized void UnregisterServer(IServer server) throws RemoteException {
		servers.remove(server);
		int index = serverIndexMap.get(server);
		weights.set(index, -1);
		serverIndexMap.remove(server);
	}

	public void addRequest(IRequest request) throws RemoteException {
		requests.add(request);
	}

	public void changeWeight(IServer server) throws RemoteException {
		int index = serverIndexMap.get(server);
		weights.set(index, server.getProcessingSpeed());
		calculateStepSize();
	}

	@Override
	public void run() {
		int serverTurn = 0;
		int curServerWeight = 0;
		while (servers.size() > 0 || !serversStarted) {
			if (!requests.isEmpty()) {
//				System.out.println(curServerWeight);
				if (serverTurn >= servers.size())
					serverTurn = serverTurn % servers.size();
				IServer curServer = servers.get(serverTurn);
				IRequest curRequest = requests.peek();
				try {
					if (curServer.isAvailable()) {
						requests.poll();
						servers.get(serverTurn).addPatient(curRequest);
					}
					else {
//						System.out.printf("Server %s at full capacity, cannot store %s\n", curServer.getID(), curRequest.getID());
						curServerWeight = Integer.MIN_VALUE;
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				curServerWeight += stepSize;
				if ((curServerWeight >= weights.get(serverTurn % servers.size())) || (curServerWeight < 0)) {
					curServerWeight = 0;
					serverTurn++;
				}
			}
		}
		System.out.println("Load balancer shutting down");
	}
}
