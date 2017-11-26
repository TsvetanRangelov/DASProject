//package Balancer;

import java.rmi.Remote;
import java.rmi.RemoteException;



public interface LoadBalancer extends Remote {

	public void RegisterServer(IServer server) throws RemoteException;
	public void UnregisterServer(IServer server) throws RemoteException;
	public void addRequest(IRequest request) throws RemoteException;
	public void changeWeight(IServer server) throws RemoteException;
}
