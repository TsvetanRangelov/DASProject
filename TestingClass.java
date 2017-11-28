import java.io.FileReader;
import java.io.BufferedReader;

public class TestingClass    {
	
	public static void main(String[] args) {
		 
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
				default:
					DynamicSimpleRRBalancer dsrrb = new DynamicSimpleRRBalancer(1099);
					new Thread(dsrrb).start();
					break;
			}
			Thread.sleep(5000);

			String [] hospitalName = {"Glasgow","Edinburgh","London"};
			int [] speed = {2, 4, 6};
			int [] caps = {10, 10, 10};
			Server hospital = null;
			for (int i = 0; i < speed.length; i++) {
				hospital = new Server("127.0.0.1", 1099, hospitalName[i], speed[i], caps[i]);
				new Thread(hospital).start();
			}
			Thread.sleep(5000);

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

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
