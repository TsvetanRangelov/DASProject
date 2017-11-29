import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ClientRequest implements Runnable,Serializable {
    
/**
* 
*/
private static final long serialVersionUID = 1L;


private String requestName;
private String toIP;
private int toPort = 1099;
private String url = null;
private int processingTime = 0;

public ClientRequest() throws RemoteException{
	super();
}
public String RequestName(){
	return requestName;
}
public int ProcessingTime() {
	return this.processingTime;
}

public ClientRequest(String pName, String _toIP, int port, int _pTime) throws RemoteException {
	this.requestName = pName;
	this.toIP = _toIP;
	this.toPort = port;
	url = "rmi://" + toIP + ":" + toPort + "/WRRBalancer";
	this.processingTime = _pTime;
}

@Override
public void run() {
	if ( System.getSecurityManager() == null ) {
		System.setProperty("java.security.policy", System.class.getResource("/java.policy").toString());
		System.setSecurityManager( new SecurityManager() );
	}
	try {
		    String message = null;
		    //if message is null, repeat 
		    while(message==null) {
		    	   //TODO: Create a thread here, instead of running many windows
				ISWRRServer server = (ISWRRServer) Naming.lookup(url);
				message = server.addPatient(this);
					
			
				Thread.sleep(5000);
			
		    }
		 
		
	} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (RemoteException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (NotBoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}


public int getProcessingTime() {
	
	return this.processingTime;
}

}
