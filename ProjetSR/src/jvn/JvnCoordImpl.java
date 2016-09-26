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
import java.util.LinkedList;
import java.util.List;
import java.io.Serializable;

public class JvnCoordImpl extends UnicastRemoteObject implements JvnRemoteCoord {

    private int counter;
    private HashMap<String, Integer> serviceNommage;
    private HashMap<Integer, JvnObject> cache;
    private HashMap<Integer, CoupleVerrou> verrous;
    
    /**
     * Default constructor
     * 
     * @throws JvnException
     **/
    private JvnCoordImpl() throws Exception {
	super();
	this.counter = 0;
	this.serviceNommage = new HashMap<String, Integer>();
	this.cache = new HashMap<Integer, JvnObject>();
	this.verrous = new HashMap<Integer, CoupleVerrou>();
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
	this.serviceNommage.put(jon, jo.jvnGetObjectId());
	this.cache.put(jo.jvnGetObjectId(), jo);
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
	
	Integer id = this.serviceNommage.get(jon);
	return this.cache.get(id);
	
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
