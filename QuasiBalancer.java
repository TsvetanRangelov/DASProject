import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import jdk.nashorn.internal.ir.RuntimeNode.Request;


public class QuasiBalancer extends UnicastRemoteObject{

	private static final long serialVersionUID = 1L;
	private HashMap<Integer,BlockingQueue<UnknownServer>> priorityTable;
	private ArrayList<UnknownServer> serverList;
	private ArrayList<Float> frequencyTable;
	private ArrayList<Float> speedTable;
	private WrapperQuasiBalancer parent;

	public QuasiBalancer(int port, WrapperQuasiBalancer parent) throws RemoteException,IOException {
		this.parent=parent;
		//0 stores unallocated servers
		priorityTable = new HashMap<Integer, BlockingQueue<UnknownServer>>();
		this.frequencyTable = new ArrayList<Float>();
		this.serverList = new ArrayList<UnknownServer>();
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
			for(UnknownServer s: serverList) {
				if(s.id==id) {
					insertServer(s);
					break;
				}
			}
			insertServer(new UnknownServer(id));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	public String processRequest(int priority, String requestID) throws RemoteException {
		//If there is no server available to process the request, wait 100ms and try again
		if(priority == -1) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return processRequest(10,requestID);
		}
		//Update the frequency table to remember that a priority of that type has entered
		frequencyTable.set(priority,frequencyTable.get(priority)+1);
		try {
			UnknownServer server = priorityTable.get(priority).poll();
			speedTable.set(priority,speedTable.get(priority)-server.speed);
			float startTime = System.nanoTime();
			boolean available = parent.dispatch(requestID, server.id);
			float endTime = System.nanoTime();
			server.updateSpeed(endTime - startTime);
			if(available)
			insertServer(server);
			
		}catch(Exception e) {	//No server at that slot, check next priority
			frequencyTable.set(priority,frequencyTable.get(priority)-1);
			processRequest(priority-1, requestID);
		}
		return null;
	}

	//Find a new place for a freed server based on speed
	private void insertServer(UnknownServer server) throws InterruptedException {
		for(int i = 1; i<10;++i) {
			synchronized(priorityTable){
				//Check if the priority slot is empty
				if(priorityTable.get((3*i)%10).size()==0 ) {
					speedTable.set((3*i)%10,speedTable.get((3*i)%10)+server.speed);
					priorityTable.get((3*i)%10).put(server);
					return;
				}
				//Insert the server at a below average slot
				if(speedTable.get((3*i)%10)<getAverageRatio()) {
					speedTable.set((3*i)%10,speedTable.get((3*i)%10)+server.speed);
					priorityTable.get((3*i)%10).put(server);
					return;
				}
			}
		}
		
	}

	//Check the average measurements of the rows
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

	//A representation for an unknown server
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
		public boolean equals(Object o) {
			
			return id==((UnknownServer)o).id;
			
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
