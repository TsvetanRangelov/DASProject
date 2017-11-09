import java.rmi.RemoteException;


public class NodeImpl extends java.rmi.server.UnicastRemoteObject implements Node {

	private String name;
	private String leaderName = "";
	private String host;

	private boolean leaderFound = false;
	
	public Node(String name, String hostname){
		
	}

    
}

