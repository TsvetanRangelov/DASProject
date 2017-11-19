import java.rmi.Naming;			//Import the rmi naming - so you can lookup remote object
import java.rmi.RemoteException;	//Import the RemoteException class so you can catch it
import java.net.MalformedURLException;	//Import the MalformedURLException class so you can catch it
import java.rmi.NotBoundException;	//Import the NotBoundException class so you can catch it

public class nodeclient {

    public static void main(String[] args) {
        
       String reg_host = "localhost";
       int reg_port = 1099;
       
       if (args.length == 1) {
       	reg_port = Integer.parseInt(args[0]);
      } else if (args.length == 2) {
      	reg_host = args[0];
      	reg_port = Integer.parseInt(args[1]);
      }
        
	try {			
            Node c = (Node)
                           Naming.lookup("rmi://" + reg_host + ":" + reg_port + "/NodeService");
 
        }
	catch (MalformedURLException murle) {
            System.out.println();
            System.out.println("MalformedURLException");
            System.out.println(murle);
        }
        catch (RemoteException re) {
            System.out.println();
            System.out.println("RemoteException");
            System.out.println(re);
        }
        catch (NotBoundException nbe) {
            System.out.println();
            System.out.println("NotBoundException");
            System.out.println(nbe);
        }

    }
}

