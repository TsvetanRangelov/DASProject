public class PatientProcessor implements Runnable {

	private PatientClient client = null;
	public  PatientProcessor(PatientClient client) {
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
