
public class RequestProcessor implements Runnable {

	private ClientRequest client = null;
	public  RequestProcessor(ClientRequest client) {
		this.client = client;
	}
	@Override
	
	public void run() {
		try {
			Thread.sleep(client.getProcessingTime()*1000);
		
		
		}catch(InterruptedException e) {
			
		}
	
		
	}
}
