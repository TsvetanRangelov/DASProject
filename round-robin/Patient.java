import java.rmi.Naming;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Patient implements Runnable {

    public String pName;
    public int procTime;

    public Patient(String name, int time){
        pName = name;
        procTime = time;
    }

    @Override
    public void run(){
        int port = 1099;
		String url = "rmi://127.0.0.1:" + port + "/HospitalBalancer";

        try {		
			IHospitalBalancer balancer = (IHospitalBalancer) Naming.lookup(url);
            String msg = balancer.assignPatient(this.pName,this.procTime);
            System.out.println(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
        
    }

}