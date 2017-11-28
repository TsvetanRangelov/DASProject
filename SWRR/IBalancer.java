

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IBalancer extends Remote {
	public void RegisterServer(IServer server) throws RemoteException;
	public void UnregisterServer(IServer server) throws RemoteException;
    public String checkAlive() throws RemoteException;
}
