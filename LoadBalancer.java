//package Balancer;

import java.rmi.Remote;
import java.rmi.RemoteException;



public interface LoadBalancer extends Remote {

	public void RegisterServer(IServer server) throws RemoteException;
	public void UnregisterServer(IServer server) throws RemoteException;
	public String processRequest(String patientName, int processedTime) throws RemoteException;
}
