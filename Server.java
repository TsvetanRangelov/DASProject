import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Server extends java.rmi.server.UnicastRemoteObject implements IServer, Runnable {

	private LoadBalancer balancer;
	private  boolean isAvailable = true;
	private String ID;
	private int processingSpeed = 1;
	private String BalancerIP;
	private int registryport = 1099;
	private int totalRequestsProcessed = 0;
	private int totalProcessingTime = 0;
	private int totalUptime = 0;
	private int uptimeBeforeFirstRequest = 0;
	private int totalDynamicDowntime = 0;
	private int capacity = 1;
	private ConcurrentLinkedQueue<IRequest> requests = null;

	private boolean debug = false;

	protected Server() throws RemoteException {
		super();

	}

	public Server(String balancerip, int _registryport, String ID, int speed, int capacity)  throws RemoteException, MalformedURLException, NotBoundException{
		this.BalancerIP = balancerip;
		this.registryport = _registryport;
		this.ID = ID;
		this.processingSpeed = speed;
       	this.requests = new ConcurrentLinkedQueue<>();
        this.capacity = capacity;

		if (System.getSecurityManager() == null) {
			System.class.getResource("/resources/java.policy").toString();
			System.setProperty("java.security.policy", System.class.getResource("/resources/java.policy").toString());
			System.setSecurityManager(new SecurityManager());
		}
		try {
			String url = String.format("rmi://localhost:%d/%s", registryport, ID);
			System.out.println(url);
			Naming.rebind(url, this);
			System.out.printf("Establishing server %s successfully\n", ID);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isAvailable() {
		return isAvailable;
	}

	@Override
	public synchronized void addPatient(IRequest request) throws RemoteException {
		requests.add(request);
		if (requests.size() == capacity)
			isAvailable = false;
	}

	@Override
	public String getID() throws RemoteException {
		return ID;
	}

	@Override
	public int getProcessingSpeed() throws RemoteException {
		return processingSpeed;
	}

	// simple way of turning debug on/off
	public void toggleDebug() {
		this.debug = !this.debug;
	}

	private boolean processRequest(IRequest request) throws RemoteException {
		int tts = (int) Math.ceil( (double) request.getProcessingTime() / (double) processingSpeed);
		try {
			Thread.sleep(tts);
			totalProcessingTime += tts;
			totalUptime += tts;
			totalRequestsProcessed += 1;
			return true;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	private void changeCapacity(double change) throws RemoteException {
		int oldCapacity = capacity;
		capacity = (int) Math.ceil(change) + capacity;

		if (capacity <= 0)
			capacity = 1;

		System.out.printf("Server %s changing capacity from %d to %d (%d processed)\n", ID, oldCapacity, capacity, totalRequestsProcessed);

		// Return requests over capacity back to load balancer
		if (requests.size() > capacity) {
			isAvailable = false;
			ConcurrentLinkedQueue<IRequest> remainingReqs = new ConcurrentLinkedQueue<>();
			int counter = 0;
			for (IRequest req : requests) {
				if (counter >= capacity)
					remainingReqs.add(req);
				else
					balancer.addRequest(req);
				counter++;
			}

			System.out.println("Total waiting: "+counter);

			requests = remainingReqs;
		}
	}

	private void makeUnavailable(int time) throws InterruptedException {
		System.out.printf("Server %s randomly made unavailable for %d ms (%d processed)\n", ID, time, totalRequestsProcessed);
		Thread.sleep(time);
		totalDynamicDowntime += time;
	}

	private void changeProcessingSpeed(double factor) throws RemoteException {
		int oldProcessingSpeed = processingSpeed;
		processingSpeed = (int) Math.ceil(processingSpeed * factor);

		if (processingSpeed <= 0)
			processingSpeed = 1;
		balancer.changeWeight(this);

		System.out.printf("Server %s changing processing speed from %d to %d (%d processed)\n", ID, oldProcessingSpeed, processingSpeed, totalRequestsProcessed);
	}

	private void randomDynamicEffect() throws RemoteException, InterruptedException {
		Random random = new Random();
		if (random.nextInt(100) == 0)
			changeCapacity(random.nextGaussian() * 4);

//		if (random.nextInt(100) == 0)
//			makeUnavailable((int) (Math.abs(random.nextGaussian() + 1) * 5000));

		if (random.nextInt(100) == 0)
			changeProcessingSpeed(Math.abs(random.nextGaussian() + 1));
	}

	@Override
	public void run() {
		try {
			if (System.getSecurityManager() == null) {
			    System.setProperty("java.security.policy", System.class.getResource("/resources/java.policy").toString());
			    System.setSecurityManager( new SecurityManager() );
			}
			this.balancer = (LoadBalancer) Naming.lookup("rmi://" + BalancerIP + ":" + registryport + "/LoadBalancer");
			this.balancer.RegisterServer(this);
			System.out.printf("Registered server %s\n", ID);
			while (true) {
				if (!requests.isEmpty()) {
					IRequest curRequest = requests.peek();
					if(debug){
						System.out.println(ID + " req "+requests.size()+" proc "+totalRequestsProcessed);
					}
					if (this.processRequest(curRequest)) {
						requests.poll();
						isAvailable = true;
						if(this.balancer.getClass()==WrapperQuasiBalancer.class) {
							((WrapperQuasiBalancer)this.balancer).RegisterServer(this);
						}
						this.randomDynamicEffect();
//						System.out.printf("Server %s processed %s with processing time %d, total processed %d\n", ID, curRequest.getID(), curRequest.getProcessingTime(), totalRequestsProcessed);
//						System.out.printf("Total server %s processing time: %d/(%d + %d)\n", ID, totalProcessingTime, totalUptime, totalDynamicDowntime);
					}
				}
				else {
					Thread.sleep(100);
					if (totalProcessingTime > 0)
						totalUptime += 100;
					else
						uptimeBeforeFirstRequest += 100;
				}
				if (totalUptime > 100000 || uptimeBeforeFirstRequest > 150000)
					break;
			}
			System.out.printf("Server %s total working time %d, uptime %d, downtime %d, total processed %d\n", ID, totalProcessingTime, totalUptime, totalDynamicDowntime, totalRequestsProcessed);
			System.out.printf("Shutting down server %s\n", ID);
			this.balancer.UnregisterServer(this);
		}
		catch (MalformedURLException|RemoteException|SecurityException|NotBoundException|InterruptedException e) {
			e.printStackTrace();
		}
	}


}
