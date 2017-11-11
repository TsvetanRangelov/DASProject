import java.rmi.RemoteException;

public interface Node extends java.rmi.Remote {

    public String startElection(String senderName) throws RemoteException;
    public void leader(String leaderName) throws RemoteException;
    //public String receiveMsg(String message);
}

