import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.Remote;
import java.util.concurrent.ConcurrentLinkedQueue;

public class StaticRRBalancer extends UnicastRemoteObject implements LoadBalancer, Runnable {

	// balancer contain list of Server
	private int lastId = -1;
	private ConcurrentLinkedQueue<IRequest> requests = null;

	protected StaticRRBalancer() throws RemoteException {
		super();
	}

	public StaticRRBalancer(int port) throws RemoteException,IOException {
		this.requests = new ConcurrentLinkedQueue<>();
		if (System.getSecurityManager() == null) {
			String abc = System.class.getResource("/java.policy").toString();
			System.out.println(abc);
			System.setProperty("java.security.policy", System.class.getResource("/java.policy").toString());
			System.setSecurityManager(new SecurityManager());
		}

		try {
			Naming.rebind("rmi://localhost:" + port + "/StaticRRBalancer", this);
			System.out.println("Establishing load balancer successfully");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void addRequest(IRequest request) throws RemoteException {
		requests.add(request);
	}

	public String assignPatient(String name, int time) throws RemoteException {
	
	}

	@Override
	public void run(){
		while (!requests.isEmpty()){
			Registry reg = null;
			synchronized(this){
				try {
					reg = LocateRegistry.getRegistry("localhost");
				} catch (Exception e) {}
				String[] servers = reg.list();
				if (lastId == -1){
					lastId = Integer.parseInt(servers[0]);
				} else {
					lastId = (lastId+1) % (servers.length-1);
				}
				try {
					IHospital server = (IHospital) reg.lookup(Integer.toString(lastId));
					server.addPatient(requests.poll());
				} catch (Exception e) {}
			}
		}	
	}
}
