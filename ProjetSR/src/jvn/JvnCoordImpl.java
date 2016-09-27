/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.io.Serializable;

public class JvnCoordImpl extends UnicastRemoteObject implements JvnRemoteCoord {

    private int counter;
    private HashMap<String, Integer> serviceNommage;
    private HashMap<Integer, JvnObject> cache;
    private HashMap<Integer, List<CoupleVerrou>> verrous;

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
	this.verrous = new HashMap<Integer, List<CoupleVerrou>>();
    }

    /**
     * Allocate a NEW JVN object id (usually allocated to a newly created JVN object)
     * 
     * @throws java.rmi.RemoteException
     *             ,JvnException
     **/
    public synchronized int jvnGetObjectId() throws java.rmi.RemoteException, jvn.JvnException {
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
    public synchronized void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js) throws java.rmi.RemoteException, jvn.JvnException {
	System.out.println("<COORDINATEUR>Register demandé de l'objet d'id " + jo.toString());
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
    public synchronized JvnObject jvnLookupObject(String jon, JvnRemoteServer js) throws java.rmi.RemoteException, jvn.JvnException {

	System.out.println("<COORDINATEUR>Lookup demandé sur " + jon);

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
    public synchronized Serializable jvnLockRead(int joi, JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {
	/*
	 * joi doit savoir si on a un lock en write si oui, invalidateWrite sinon verrou lecture + return object
	 */

	JvnObject res = this.cache.get(joi);
	
	System.out.println("<COORDIANTEUR>Demande de lock READ pour l'objet d'id="+joi);
	System.out.println("<COORDIANTEUR "+ Date.from(Instant.now()).toString()+ ">res null ? : " + (res == null));

	System.out.println("<COORDIANTEUR "+ Date.from(Instant.now()).toString()+ ">res.object null ? : " + (res.getTheObject() == null));
	
	List<CoupleVerrou> list = this.verrous.get(joi);

	if (list == null) {
	    list = new LinkedList<CoupleVerrou>();
	    list.add(new CoupleVerrou(js, StateLock.R));
	    this.verrous.put(joi, list);
	} else {

	    Iterator<CoupleVerrou> i = list.iterator();
	    while (i.hasNext()) {
		CoupleVerrou couple = i.next();
		if (couple.getJs().equals(js))
		    couple.setState(StateLock.R);
		else {
		    switch (couple.getState()) {
		    case W:
			System.out.println("<COORDIANTEUR "+ Date.from(Instant.now()).toString()+ ">Case W");
			res.setTheObject(couple.getJs().jvnInvalidateWriterForReader(joi));
			this.cache.put(joi, res);
			couple.setState(StateLock.R);
			break;
		    default:
			break;
		    }
		}
	    }
	}
	return res.getTheObject();
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
    public synchronized Serializable jvnLockWrite(int joi, JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {

	JvnObject res = this.cache.get(joi);
	System.out.println("<COORDIANTEUR>Demande de lock WRITE pour l'objet d'id="+joi);
	System.out.println("<COORDIANTEUR>res null ? : " + (res == null));
	if(res != null)
	System.out.println("<COORDIANTEUR>res.object null ? : " + (res.getTheObject() == null));
	List<CoupleVerrou> list = this.verrous.get(joi);
	if (list == null) {
	    System.out.println("<COORDIANTEUR>Objet nouveau, creation d'une liste de verrous avec un verrou en W");
	    list = new LinkedList<CoupleVerrou>();
	    list.add(new CoupleVerrou(js, StateLock.W));
	    this.verrous.put(joi, list);
	} else {

	    Iterator<CoupleVerrou> i = list.iterator();
	    while (i.hasNext()) {
		CoupleVerrou couple = i.next();
		if (couple.getJs().equals(js))
		    couple.setState(StateLock.W);
		else {
		    switch (couple.getState()) {
		    case W:
			res.setTheObject(couple.getJs().jvnInvalidateWriter(joi));
			this.cache.put(joi, res);
			couple.setState(StateLock.NL);
			break;
		    case R:
			couple.getJs().jvnInvalidateReader(joi);
			couple.setState(StateLock.NL);
			break;
		    default:
			break;
		    }
		}
	    }
	}
	if(res == null)
	    return null;
	return res.getTheObject();
    }

    /**
     * A JVN server terminates
     * 
     * @param js
     *            : the remote reference of the server
     * @throws java.rmi.RemoteException
     *             , JvnException
     **/
    public synchronized void jvnTerminate(JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {

    }

    public static void main(String[] args) {

	try {

	    /*JvnRemoteCoord coordinateur = new JvnCoordImpl();

	    // JvnRemoteCoord h_stub = (JvnRemoteCoord) UnicastRemoteObject.exportObject(coordinateur, 0);
	    Registry registre = LocateRegistry.getRegistry();
	    registre.bind("serveur", coordinateur);*/
	    
	    JvnRemoteCoord coordinateur = new JvnCoordImpl();
	    Registry registre = LocateRegistry.createRegistry(1099);
	    registre.bind("serveur", coordinateur);
	    System.out.println("<COORDINATEUR>Coordinateur lancé !");
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

}
