import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class HospitalBalancer extends UnicastRemoteObject implements IHospital, IHospitalBalancer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	 
	// balancer contain list of Server

	private List<IHospital> servers = null;

	protected HospitalBalancer() throws RemoteException {
		super();
	}

	public HospitalBalancer(int port) throws RemoteException,IOException {
		this.servers = new ArrayList<>();
		MyLogger.setup();
		if (System.getSecurityManager() == null) {
			String abc = System.class.getResource("/java.policy").toString();
			System.out.println(abc);
			System.setProperty("java.security.policy", System.class.getResource("/java.policy").toString());
			System.setSecurityManager(new SecurityManager());
		}

		try {
			Naming.rebind("rmi://localhost:" + port + "/HospitalBalancer", this);

			System.out.println("Establishing load balancer successfully");

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * Weighted Round robin 1. Check server available or not 2. If there are more
	 * than 2 server avaialble, request will be redirected to server which has higher priority
	 */
	@Override
	public String addPatient(String patientName, int processedTime) throws RemoteException {

		if (servers.size() > 0) {

			ArrayList<IHospital> lstHospital = new ArrayList<>();
			IHospital chosenHospital = null;
			int maxPriority = 0;
			synchronized (servers) {

				// iterate servers
				IHospital hospital = null;

				for (int i = 0; i < servers.size(); i++) {
					hospital = servers.get(i);
					LOGGER.setLevel(Level.INFO);
					LOGGER.info("Hospital :" + hospital.getHospitalID() + " is available:" + hospital.isAvailable());
					
					
					// Weighted Round robin - if hospital isAvaialble, it will accept request
					if (hospital.isAvailable()) {
						lstHospital.add(hospital);
						if (maxPriority < hospital.getPriority()) {
							maxPriority = hospital.getPriority();
						}
					}
				}

				for (IHospital iHospital : lstHospital) {
					if (iHospital.getPriority() == maxPriority) {
						chosenHospital = iHospital;
					}
				}

				if (chosenHospital != null) {
					
					LOGGER.setLevel(Level.INFO);
					LOGGER.info("Request from client  " + patientName + " is transfered to hospital: " + chosenHospital.getHospitalID());
				}

			}

			if (chosenHospital != null)
				return chosenHospital.addPatient(patientName, processedTime);
			else {
				LOGGER.setLevel(Level.WARNING);
				LOGGER.info("All server are busy now. Please wait...");
				return null;
			}
				

		} else {
			LOGGER.setLevel(Level.INFO);
			LOGGER.info("No hospital is available ");
			
		}
		return null;
	}

	@Override
	public void RegisterHospitalServer(IHospital hospital) throws RemoteException {
		servers.add(hospital);
		LOGGER.setLevel(Level.INFO);
		LOGGER.info("Hospital with the ID " + hospital.getHospitalID() + " registered at the registry");
	}

	@Override
	public void UnregisterHospitalServer(IHospital hospital) throws RemoteException {
		synchronized (servers) {
			servers.remove(hospital);
			LOGGER.setLevel(Level.INFO);
			LOGGER.info("Remove hospital " + hospital.getHospitalID() + " from registry");
			
		}

	}


	/*public static void main(String[] args) {
		try {
			new HospitalBalancer(1099);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/
	

	@Override
	public boolean isAvailable() throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getHospitalID() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPriority() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

}
