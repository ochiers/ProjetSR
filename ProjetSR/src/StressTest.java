import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import jvn.JvnCoordImpl;


public class StressTest {

    
    public static void main(String args[]) throws InterruptedException{
	
	
	Runnable coord = new Runnable() {
	    
	    public void run() {
		JvnCoordImpl.main(null);
		
	    }
	};
	
	
	
	
	Thread tcoord = new Thread(coord);
	

	
	tcoord.start();
	Thread.sleep(2000);
	
	
	Thread[] t = new Thread[20];
	for(int i=0; i<t.length;i+=2){
	    t[i] = new Thread(new StressClient("Reader"+i, "reader"));
	    t[i+1] = new Thread(new StressClient("Writer"+(i+1), "writer"));
	}
	
	
	for(int i=0; i<t.length;i++){
	    t[i].start();
	}
	
	System.out.println("join");
	try {
	    for(int i=0; i<t.length;i++){
		    t[i].join();
	}
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	
	Thread lastReader = new Thread(new StressClient("LastReader", "last"));
	lastReader.start();
	lastReader.join();
	
	System.exit(0);
    }
    
}
