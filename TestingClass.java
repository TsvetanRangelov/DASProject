

public class TestingClass    {
	
	public static void main(String[] args) {
		 
		try {
			String typeProgram = "";
			if (args.length > 0)
				typeProgram = args[0].toString();

			if ("Balancer".equals(typeProgram)) {
				new SimplePriorityBalancer(1099);
			}
			
			else if ("Server".equals(typeProgram)) {
				String [] hospitalName = {"Glasgow","Edinburgh","London"};
				int [] priority = {10,20,30};
				Server hospital = null;
				for (int i = 0; i < priority.length; i++) {
					hospital = new Server("127.0.0.1", 1099, hospitalName[i], priority[i]);
					new Thread(hospital).start();
				}
			}
			
		
			else if ("Client".equals(typeProgram)) {
				//String [] pName = {"John","Beckham","David","Terry","Simeone","Alan","York"};
				//int [] processTime = {10,20,30,40,20,30,50};
				Request request = null;
				for (int i = 0; i < 100; i++) {
					request = new Request("Patient " + i, "127.0.0.1", 1099, i+5);
					new Thread(request).start();
				}
			}

			else {
				new SimplePriorityBalancer(1099);

				Thread.sleep(5000);
				String [] hospitalName = {"Glasgow","Edinburgh","London"};
				int [] priority = {10,20,30};
				Server hospital = null;
				for (int i = 0; i < priority.length; i++) {
					hospital = new Server("127.0.0.1", 1099, hospitalName[i], priority[i]);
					new Thread(hospital).start();
				}

				Thread.sleep(5000);
				Request client = null;
				for (int i = 0; i < 100; i++) {
					client = new Request("Patient " + i, "127.0.0.1", 1099, 1);
					new Thread(client).start();
				}
			}
		
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
