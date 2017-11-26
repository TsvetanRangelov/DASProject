import java.util.Random;
import java.lang.Math;

public class TestingClass    {
	
	public static void main(String[] args) {
		 
		try {
			switch (args[0]) {
				case "wrr":
					DynamicWeightedRRBalancer dwrrb = new DynamicWeightedRRBalancer(1099);
					new Thread(dwrrb).start();
					Thread.sleep(5000);
					break;
				default:
					DynamicSimpleRRBalancer dsrrb = new DynamicSimpleRRBalancer(1099);
					new Thread(dsrrb).start();
					Thread.sleep(5000);
					break;
			}
			String [] hospitalName = {"Glasgow","Edinburgh","London"};
			int [] speed = {2, 4, 6};
			int [] caps = {10, 10, 10};
			Server hospital = null;
			for (int i = 0; i < speed.length; i++) {
				hospital = new Server("127.0.0.1", 1099, hospitalName[i], speed[i], caps[i]);
				new Thread(hospital).start();
			}
			Thread.sleep(5000);

			Random random = new Random();
			System.out.println("Creating requests");
			for (int i = 0; i < 1000; i++) {
				int reqTime = (int) Math.ceil(Math.abs(random.nextGaussian()*1000));
				int waitTime = (int) Math.ceil(Math.abs(random.nextGaussian()*100));
				new Request("Patient " + i, "127.0.0.1", 1099, reqTime);
				Thread.sleep(waitTime);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
