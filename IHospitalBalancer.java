//package Balancer;

import java.rmi.Remote;
import java.rmi.RemoteException;



public interface IHospitalBalancer extends Remote {
    
	public void RegisterHospitalServer(IHospital hospital) throws RemoteException;
	public void UnregisterHospitalServer(IHospital hospital) throws RemoteException;
}
