import java.rmi.RemoteException;

public interface node extends java.rmi.Remote {

    public String startElection(String senderName) throws RemoteException;
    public void leader(String leaderName);
    public String receiveMsg(String message);
}

