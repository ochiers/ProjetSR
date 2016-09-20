/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import irc.Sentence;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Iterator;
import java.io.Serializable;

public class JvnCoordImpl extends UnicastRemoteObject implements JvnRemoteCoord {

    private int counter;
    private HashMap<String, JvnObject> storage;

    /**
     * Default constructor
     * 
     * @throws JvnException
     **/
    private JvnCoordImpl() throws Exception {
	super();
	this.counter = 0;
	this.storage = new HashMap<String, JvnObject>();
    }

    /**
     * Allocate a NEW JVN object id (usually allocated to a newly created JVN object)
     * 
     * @throws java.rmi.RemoteException
     *             ,JvnException
     **/
    public int jvnGetObjectId() throws java.rmi.RemoteException, jvn.JvnException {

	return counter++;
    }

    /**
     * Associate a symbolic name with a JVN object
     * 
     * @param jon
     *            : the JVN object name
     * @param jo
     *            : the JVN object
     * @param joi
     *            : the JVN object identification
     * @param js
     *            : the remote reference of the JVNServer
     * @throws java.rmi.RemoteException
     *             ,JvnException
     **/
    public void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js) throws java.rmi.RemoteException, jvn.JvnException {
	System.out.println("Register demandé");
	this.storage.put(jon, jo);

    }

    /**
     * Get the reference of a JVN object managed by a given JVN server
     * 
     * @param jon
     *            : the JVN object name
     * @param js
     *            : the remote reference of the JVNServer
     * @throws java.rmi.RemoteException
     *             ,JvnException
     **/
    public JvnObject jvnLookupObject(String jon, JvnRemoteServer js) throws java.rmi.RemoteException, jvn.JvnException {
	
	System.out.println("Lookup demandé sur " + jon);
	
	Iterator<String> it = this.storage.keySet().iterator();
	while(it.hasNext()){
	    String key = it.next();
	    System.out.println(key + " : " + this.storage.get(key));
	}
	
	if(this.storage.get(jon) != null)
	    System.out.println(((Sentence)this.storage.get(jon).jvnGetObjectState()).read());
	return this.storage.get(jon);
    }

    /**
     * Get a Read lock on a JVN object managed by a given JVN server
     * 
     * @param joi
     *            : the JVN object identification
     * @param js
     *            : the remote reference of the server
     * @return the current JVN object state
     * @throws java.rmi.RemoteException
     *             , JvnException
     **/
    public Serializable jvnLockRead(int joi, JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {
	// to be completed
	return null;
    }

    /**
     * Get a Write lock on a JVN object managed by a given JVN server
     * 
     * @param joi
     *            : the JVN object identification
     * @param js
     *            : the remote reference of the server
     * @return the current JVN object state
     * @throws java.rmi.RemoteException
     *             , JvnException
     **/
    public Serializable jvnLockWrite(int joi, JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {
	// to be completed
	return null;
    }

    /**
     * A JVN server terminates
     * 
     * @param js
     *            : the remote reference of the server
     * @throws java.rmi.RemoteException
     *             , JvnException
     **/
    public void jvnTerminate(JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {

    }

    public static void main(String[] args) {

	try {

	    JvnRemoteCoord coordinateur = new JvnCoordImpl();

	    //JvnRemoteCoord h_stub = (JvnRemoteCoord) UnicastRemoteObject.exportObject(coordinateur, 0);
	    Registry registre = LocateRegistry.getRegistry();
	    registre.bind("serveur", coordinateur);
	    

	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

}
