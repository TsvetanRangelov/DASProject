import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ISWRRBalancer extends Remote {
	public void RegisterServer(ISWRRServer server) throws RemoteException;
	public void UnregisterServer(ISWRRServer server) throws RemoteException;
    public String checkAlive() throws RemoteException;
}

