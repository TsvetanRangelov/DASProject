import java.rmi.Naming;	//Import naming classes to bind to rmiregistry

public class hubServer {
	static int port = 1099;
   public hubServer() {
     
     //Construct a new CalculatorImpl object and bind it to the local rmiregistry
     //N.b. it is possible to host multiple objects on a server by repeating the
     //following method. 

     try {
       	node c = new nodeimpl();
       	Naming.rebind("rmi://localhost:" + port + "/CalculatorService", c);
     } 
     catch (Exception e) {
       System.out.println("Server Error: " + e);
     }
   }

   public static void main(String args[]) {
	if (args.length == 1)
		port = Integer.parseInt(args[0]);
	
	new hubServer();
   }
}
