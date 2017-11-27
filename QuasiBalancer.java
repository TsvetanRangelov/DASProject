import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;


public class QuasiBalancer extends UnicastRemoteObject{

	private static final long serialVersionUID = 1L;
	private HashMap<Integer,BlockingQueue<UnknownServer>> priorityTable;
	private ArrayList<Float> frequencyTable;
	private ArrayList<Float> speedTable;
	private WrapperQuasiBalancer parent;

	public QuasiBalancer(int port, WrapperQuasiBalancer parent) throws RemoteException,IOException {
		this.parent=parent;
		//0 stores unallocated servers
		priorityTable = new HashMap<Integer, BlockingQueue<UnknownServer>>();
		this.frequencyTable = new ArrayList<Float>();
		this.speedTable = new ArrayList<Float>();
		for(int i=0;i<11;++i) {
			speedTable.add((float) 0);
			frequencyTable.add((float) 1);
			priorityTable.put(i, new PriorityBlockingQueue<UnknownServer>());
		}
		if (System.getSecurityManager() == null) {
			String abc = System.class.getResource("/resources/java.policy").toString();
			System.out.println(abc);
			System.setProperty("java.security.policy", System.class.getResource("/resources/java.policy").toString());
			System.setSecurityManager(new SecurityManager());
		}

		try {
			System.out.println("rmi://localhost:" + port + "/QuasiBalancer");
			Naming.rebind("rmi://localhost:" + port + "/QuasiBalancer", this);

			System.out.println("Establishing load balancer successfully");

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}

	public void addServer(String id) throws RemoteException {
		try {
			insertServer(new UnknownServer(id));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	public String processRequest(int priority, String requestID) throws RemoteException {
		frequencyTable.set(priority,frequencyTable.get(priority)+1);
		try {
			UnknownServer server = priorityTable.get(priority).poll();
			speedTable.set(priority,speedTable.get(priority)-server.speed);
			float startTime = System.nanoTime();
			parent.dispatch(requestID, server.id);
			float endTime = System.nanoTime();
			server.updateSpeed(endTime - startTime);
			insertServer(server);
			
		}catch(Exception e) {
			frequencyTable.set(priority,frequencyTable.get(priority)-1);
			processRequest(priority-1, requestID);
		}
		return null;
	}


	private void insertServer(UnknownServer server) throws InterruptedException {
		for(int i = 1; i<10;++i) {
			synchronized(priorityTable){
				if(priorityTable.get(i).peek()==null) {
					speedTable.set(i,speedTable.get(i)+server.speed);
					priorityTable.get(i).put(server);
				}
				if(getAverageRatio()<speedTable.get(i)/frequencyTable.get(i)) {
					speedTable.set(i,speedTable.get(i)+server.speed);
					priorityTable.get(i).put(server);
				}
			}
		}
		
	}


	private float getAverageRatio() {
		float sum=0, sumFreq=0;
		for(float speed: speedTable) {
			sum+=speed;
		}
		for(float freq: frequencyTable) {
			sumFreq+=freq;
		}
		return sum/sumFreq;
	}


	private class UnknownServer implements Comparable<UnknownServer>{

		private float speed=0;
		public String id;
		private int totalCalls=0;

		public UnknownServer(String id) {
			this.id=id;
		}

		public void updateSpeed(float duration) {
			speed=(speed*totalCalls+duration)/(totalCalls+1);
			totalCalls++;
		}

		@Override
		public int compareTo(UnknownServer server) {
			return (int)(this.speed-server.getSpeed());
		}

		private float getSpeed() {
			return speed;
		}
	}
	
}