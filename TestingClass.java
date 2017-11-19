

public class TestingClass    {
	
	public static void main(String[] args) {
		 
		try {
			
			String typeProgram = args[0].toString();
			if ("Balancer".equals(typeProgram)) {
				new HospitalBalancer(1099);
			}
			
			else if ("Server".equals(typeProgram)) {
				String [] hospitalName = {"Glasgow","Edinburgh","London"};
				int [] priority = {10,20,30};
				HospitalServer hospital = null;
				for (int i = 0; i < priority.length; i++) {
					hospital = new HospitalServer("127.0.0.1", 1099, hospitalName[i], priority[i]);
					new Thread(hospital).start();
				}
			}
			
		
			else if ("Client".equals(typeProgram)) {
				//String [] pName = {"John","Beckham","David","Terry","Simeone","Alan","York"};
				//int [] processTime = {10,20,30,40,20,30,50};
				PatientClient client = null;
				for (int i = 0; i < 100; i++) {
					client = new PatientClient("Patient " + i, "127.0.0.1", 1099, i+5);
					new Thread(client).start();
				}
			}
		
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
