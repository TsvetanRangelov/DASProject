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

public class HospitalBalancer extends UnicastRemoteObject implements IHospitalBalancer {

	private static final long serialVersionUID = 1L;
	// balancer contain list of Server
	private int lastId = -1;

	protected HospitalBalancer() throws RemoteException {
		super();
	}

	public HospitalBalancer(int port) throws RemoteException,IOException {
		MyLogger.setup();
		if (System.getSecurityManager() == null) {
			String abc = System.class.getResource("/resources/java.policy").toString();
			System.out.println(abc);
			System.setProperty("java.security.policy", System.class.getResource("/resources/java.policy").toString());
			System.setSecurityManager(new SecurityManager());
		}

		try {
			Naming.rebind("rmi://localhost:" + port + "/HospitalBalancer", this);

			System.out.println("Establishing load balancer successfully");

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public String assignPatient(String name, int time) throws RemoteException {
		System.out.println("received patient " + name);
		Registry reg = null;
		String msg = null;
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
		
			System.out.println(lastId);
			try {
				IHospital server = (IHospital) reg.lookup(Integer.toString(lastId));
				msg = server.addPatient(name, time,lastId);
			} catch (Exception e) {}
		}
		return msg;
	}

	public static void main(String[] args){
		int port = 1099;

		try {
			new HospitalBalancer(port);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
