import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Timer;
import java.util.TimerTask;

public class NodeImpl extends java.rmi.server.UnicastRemoteObject implements Node {

	private String name;
	private String leaderName = "";
	private String host;

	private boolean heardFromLeader = false;
	private boolean leaderFound = false;
	
	public NodeImpl(String name, String hostName) throws RemoteException {
		super();
		host = hostName;
		this.name = name;
		System.out.println(this.name);

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (!name.equals(leaderName) && !heardFromLeader) {
					try {
						System.out.println("Calling election...");
						Registry reg = LocateRegistry.getRegistry(host);
						for (String nodeName : reg.list()) {
							try {
								if (!nodeName.equals(name) && nodeName.compareTo(name)>0){
									Node otherNode = (Node) reg.lookup(nodeName);
									String res = otherNode.startElection(name);

									if (res.length() > 0){
										leaderFound = true;
										break;
									}
								}
							} catch (Exception e ) {
								try {
									reg.unbind(nodeName);
								} catch (Exception d) {

								}
							}
						}

						if (!leaderFound) {
							try {
								System.out.println("No leader found, electing myself.");
								startElection(name);
								leaderFound = false;
							} catch (Exception e) {
								System.out.println("Node Error: " + e.toString());
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (heardFromLeader){
					heardFromLeader = false;
				}
			}
		}, 5000, 1000);

		System.out.println(name + " ready");
	}

	@Override
	public String startElection(String sendername) throws RemoteException{
		System.out.println("Election started...");
		String str = "";

		try {
			Registry reg = LocateRegistry.getRegistry(host);
			str = "Leader accepted";
			System.out.println(str);
			leaderName = name;
			for (String nodeName : reg.list()){
				if (!nodeName.equals(name)){
					try {
						Node node = (Node) reg.lookup(nodeName);
						node.leader(name);
					} catch (Exception e){
						try {
							reg.unbind(nodeName);
						} catch (Exception d){}
					}
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return str;
	}

	public void leader(String leader) throws RemoteException{
		leaderName = leader;
		System.out.println(leaderName + " is the new leader");
	}

	public static void usage(){
		System.out.println("usage: java Node <node-name> <host-name>");
		System.exit(1);
	}
	public static void main(String[] args){
		if (args.length < 2) usage();

		String name = args[0];
		String hostName = args[1];

		try {
			Node node = new NodeImpl(name,hostName);

			// bind
			Registry reg = LocateRegistry.getRegistry(hostName);
			reg.bind(name,node);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
    
}

