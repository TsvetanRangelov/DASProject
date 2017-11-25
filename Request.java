
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Request implements Runnable {
           
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    
	private String ID;
	private String toIP;
	private int toPort = 1099;
	private String url = null;
	private int processingTime = 0;
	public Request(String ID, String _toIP, int port, int _pTime) {
		this.ID = ID;
		this.toIP = _toIP;
		this.toPort = port;
		url = "rmi://" + toIP + ":" + toPort + "/LoadBalancer";
		this.processingTime = _pTime;
	}
	
	@Override
	public void run() {
		if ( System.getSecurityManager() == null ) {
			System.setProperty("java.security.policy", System.class.getResource("/resources/java.policy").toString());
			System.setSecurityManager( new SecurityManager() );
		}
		try {
			    String message = null;
			    //if message is null, repeat 
			    while(message==null) {
			    	   //TODO: Create a thread here, instead of running many windows
					LoadBalancer balancer = (LoadBalancer) Naming.lookup(url);
					message = balancer.processRequest(ID,processingTime);
						
					LOGGER.setLevel(Level.INFO);
					LOGGER.info(message);
					
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

}
