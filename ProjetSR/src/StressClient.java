import irc.ISentence;
import irc.Sentence;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import jvn.JvnRemoteCoord;
import jvn.JvnServerImpl;
import jvn.MyProxy;

public class StressClient implements Runnable {

    ISentence jo;
    String id;
    String role;

    public StressClient(String id, String role) {

	Registry registre;
	JvnRemoteCoord coordinateur = null;
	try {
	    registre = LocateRegistry.getRegistry("localhost");
	    coordinateur = (JvnRemoteCoord) registre.lookup("serveur");
	} catch (Exception e) {
	    e.printStackTrace();
	}

	JvnServerImpl js = JvnServerImpl.jvnGetServer(coordinateur);

	jo = (ISentence) MyProxy.newInstance(js, "IRC", Sentence.class);
	this.id = id;
	this.role = role;
    }

    public void doWriteLoop() {

	for (int i = 0; i < 1000000; i++) {
	    jo.write(i + id);
	}
	//System.out.println(this.id + " " + jo.read());

    }

    public void doReadLoop() {
	for (int i = 0; i < 1000000; i++) {
	    jo.read();
	}
    }

    public void doRead() {

	System.out.println(this.id + " " + jo.read());
    }

    public void run() {

	if (role.equals("writer")) {

	    long t = System.nanoTime();
	    this.doWriteLoop();
	    long t2 = System.nanoTime();
	    System.out.println("Temps d'execution de " + id + " : " +  (t2-t)/1000000 + " ms");
	    this.doRead();

	} else if(role.equals("reader")) {

	    long t = System.nanoTime();
	    this.doReadLoop(); 
	    long t2 = System.nanoTime();
	    System.out.println("Temps d'execution de " + id + " : " +  (t2-t)/1000000 + " ms");
	    this.doRead();
	}
	else
	    this.doRead();

    }

}
