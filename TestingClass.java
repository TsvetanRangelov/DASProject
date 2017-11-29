import java.io.FileReader;



import java.io.BufferedReader;

public class TestingClass    {

	public static void main(String[] args) {
		boolean debug = false;

		try {
			switch (args[0]) {
				case "wrr":
					DynamicWeightedRRBalancer dwrrb = new DynamicWeightedRRBalancer(1099);
					new Thread(dwrrb).start();
					break;
				case "p":
					DynamicPriorityBalancer dpb = new DynamicPriorityBalancer(1099);
					new Thread(dpb).start();
					break;
				case "q":
					WrapperQuasiBalancer wqb = new WrapperQuasiBalancer(1099);
					new Thread(wqb).start();
					break;
				case "swrr":
					new WRRBalancer(1099);
					break;
				default:
					DynamicSimpleRRBalancer dsrrb = new DynamicSimpleRRBalancer(1099);
					new Thread(dsrrb).start();
					break;
			}
			//SWRR diffrent implementation
			if ("swrr".equals(args[0])){
				//create server

				String [] serverName = {"London","Glasgow","Edinburgh"};
				int [] capacity = {10,5,5};
				SWRRServer server = null;
				for (int i = 0; i < capacity.length; i++) {
					server = new SWRRServer ("127.0.0.1",1099,serverName[i],capacity[i]);
					new Thread(server).start();
				}
				System.out.println("Server created sucessfully");

				/*ClientRequest client = null;
				int numberOfPatients = 20;
				for (int i = 0; i < numberOfPatients; i++) {
					client = new ClientRequest("Request " + i, "127.0.0.1", 1099, i+5);
					new Thread(client).start();
				}*/

				FileReader file = new FileReader("requests.csv");
				BufferedReader in = new BufferedReader(file);
				System.out.println("Creating requests");
				String line;
				int i = 0;
				ClientRequest client = null;
				while ((line = in.readLine())!=null){
					String[] data = line.split(",");
					client = new ClientRequest("Request " + i, "127.0.0.1", 1099, Integer.parseInt(data[0]));
					new Thread(client).start();
					i++;
				}

				System.out.println("Client Request created sucessfully. Check Logging.html file to see what's going on !");
			}
			else {

				if(args.length > 1 && args[1].equals("debug")){
					System.out.println("Servers will be loud.");
					debug = true;
				}

				Thread.sleep(2000);

				String [] hospitalName = {"Glasgow","Edinburgh","London"};
				int [] speed = {2, 4, 6};
				int [] caps = {10, 10, 10};
				Server hospital = null;
				for (int i = 0; i < speed.length; i++) {
					hospital = new Server("127.0.0.1", 1099, hospitalName[i], speed[i], caps[i]);
					if(debug){
						hospital.toggleDebug();
						System.out.println("Server "+hospitalName[i]+" changing capacity from "+caps[i]+" to "+caps[i]);
						System.out.println("Server "+hospitalName[i]+" changing processing speed from "+speed[i]+" to "+speed[i]);
					}
					new Thread(hospital).start();
				}
				Thread.sleep(2000);

				FileReader file = new FileReader("requests.csv");
				BufferedReader in = new BufferedReader(file);
				System.out.println("Creating requests");
				String line;
				int i = 0;
				while ((line = in.readLine())!=null){
					String[] data = line.split(",");
					new Request("Request " + i, "127.0.0.1", 1099, Integer.parseInt(data[0]), Integer.parseInt(data[2]));
					Thread.sleep(Integer.parseInt(data[1]));
					i++;
				}
			}
			} catch (Exception e) {
				e.printStackTrace();
			}


	}
}
