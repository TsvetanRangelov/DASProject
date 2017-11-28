

public class TestingClass    {
	
	public static void main(String[] args) {
		 
		try {
			
			String typeProgram = args[0].toString();
			if ("Balancer".equals(typeProgram)) {
				new HospitalBalancer(1099);
			}
			
		
		
			else if ("Client".equals(typeProgram)) {
				
				PatientClient client = null;
				int numberOfPatients = Integer.parseInt(args[1]);
				for (int i = 0; i < numberOfPatients; i++) {
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
