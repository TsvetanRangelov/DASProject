

public class TestingClass    {
	
	public static void main(String[] args) {
		 
		try {
			//create WRRBalancer
			String typeProgram = args[0];
			if ("Balancer".equals(typeProgram)) {
				 new WRRBalancer(1099);
			}
			else if ("Server".equals(typeProgram)) {
				String [] serverName = {"London","Glasgow","Edinburgh"};
				int [] capacity = {10,5,5};
				Server server = null;
				for (int i = 0; i < capacity.length; i++) {
					server = new Server("127.0.0.1",1099,serverName[i],capacity[i]);
					new Thread(server).start();
				}
			}
		   
			else if ("Client".equals(typeProgram)) {
				Request client = null;
				int numberOfPatients = 20;
				for (int i = 0; i < numberOfPatients; i++) {
					client = new Request("Request " + i, "127.0.0.1", 1099, i+5);
					new Thread(client).start();
				}
			}
		    
		 	
			
			
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
