
public class RequestProcessor implements Runnable {

	private Request client = null;
	public  RequestProcessor(Request client) {
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
