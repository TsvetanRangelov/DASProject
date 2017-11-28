
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class ReadyProcessesList {
	
    private List<PatientClient> readyProcessList = new ArrayList<PatientClient>();
	
	public ReadyProcessesList(){};  
	
	// Adds a process to the list
	public void addProcess(PatientClient process){  
		readyProcessList.add(process);
	}
	
	// Removes a process from the list
    public void removeReadyProcess(){  
    	readyProcessList.remove(0);
    }
		
    // Returns the process that its time has come to run in CPU	
	public PatientClient getProcessToRunInCpu(){  
		return readyProcessList.get(0);	
	}
	
	// Prints to the screen the contents of the list
	public void printList() throws RemoteException{  
		
		if(readyProcessList.size() == 0){
			
			System.out.println("List of ready processes is empty");
			
		}else{
			
			for(PatientClient pro : readyProcessList){
				
				System.out.println("Process ID : " + pro.PatientName());
			}	
		}
	}
	
	// Checks if the list is empty
	public boolean isEmpty(){  
         
	    boolean empty = false;
	         
	    if (readyProcessList.isEmpty()){        
	         empty = true;         
	    }     
	    return empty;         
	}
	
	// Returns the size of the list
	public int getNumberOfProcesses (){     
        return this.readyProcessList.size();      
    }

}