import java.util.Random;
import java.lang.Math;

public class PatientClient {
	
	public static void main(String[] args){

		// Creates a bunch of clients that are added to HospitalBalancer
		double mean = 10;
		double std = 3;
		Random rand = new Random(System.currentTimeMillis());

		String nameTemplate = "patient";

		for (int i=0; i<5; i++){
			Patient p = new Patient(nameTemplate+i,(int)Math.ceil(rand.nextGaussian()*std+mean));
			new Thread(p).start();
		}
	}

}
