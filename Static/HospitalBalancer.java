package Static;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



public class HospitalBalancer extends UnicastRemoteObject implements  IHospitalBalancer,IHospital {

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
	 * Weighted Round robin 
	 * 1. Choose to forward request to server which has highest capacity and avaiable
	 * 
	 */
	@Override
	public  String addPatient(PatientClient patient) throws RemoteException {

		if (servers.size() > 0) {

			ArrayList<IHospital> lstHospital = new ArrayList<>();
			IHospital chosenHospital = null;
			IHospital hospital = null;
			int maxCapacity = 0;
			
			   synchronized (servers) {
				   //iterate all server
				   for (int i = 0; i < servers.size(); i++) {
					  hospital = (IHospital)servers.get(i);
					  LOGGER.setLevel(Level.INFO);
					  LOGGER.info("Hospital :" + hospital.getHospitalID() + " is available:" + hospital.getServerStatus());
					   if (hospital.getServerStatus()) {
						  //get available server	
						   lstHospital.add(hospital);
						   //get max capacity for hospital
							if (maxCapacity < hospital.getCapacity()) 
							{
								maxCapacity = hospital.getCapacity();
							}
						}
					   				   
					}
				   //iterate all avaialbe server - get highest priority hospital 
				   for (IHospital iHospital : lstHospital) {
					if (iHospital.getCapacity() == maxCapacity)
					{
						chosenHospital = iHospital;
					}
				   }
				  				
			  }
			   
			   if (chosenHospital != null) {
					
					LOGGER.setLevel(Level.INFO);
					LOGGER.info("Request from client  " + patient.PatientName() + " is transfered to hospital: " + chosenHospital.getHospitalID());
					String result = chosenHospital.addPatient(patient);
					LOGGER.setLevel(Level.INFO);
					LOGGER.info(result);
					return result;
				}
	
			else {
				LOGGER.setLevel(Level.WARNING);
				LOGGER.info("All server are busy now. Please wait...");
				return null;
			}
	    } else 
		{
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

	@Override
	public boolean getServerStatus() throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getHospitalID() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCapacity() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String checkAlive() throws RemoteException {
		return "I'm still alive";
	}

	

	

}
