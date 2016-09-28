/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import irc.Sentence;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.io.*;

public class JvnServerImpl extends UnicastRemoteObject implements JvnLocalServer, JvnRemoteServer {

    // A JVN server is managed as a singleton
    private static JvnServerImpl js = null;
    private HashMap<Integer, JvnObject> cache;
    private HashMap<String, Integer> serviceNommage;

    private JvnRemoteCoord coordinateur;

    /**
     * Default constructor
     * 
     * @throws JvnException
     **/
    private JvnServerImpl() throws Exception {
	super();
	this.cache = new HashMap<Integer, JvnObject>();
	this.serviceNommage = new HashMap<String, Integer>();

    }

    /**
     * Static method allowing an application to get a reference to a JVN server instance
     * 
     * @throws JvnException
     **/
    /*
     * public static JvnServerImpl jvnGetServer() { if (js == null) { try { js = new JvnServerImpl(); } catch (Exception e) { return null; } } return js; }
     */

    public static JvnServerImpl jvnGetServer(JvnRemoteCoord coord) {
	if (js == null) {
	    try {
		js = new JvnServerImpl();
		js.setCoordinateur(coord);
	    } catch (Exception e) {
		return null;
	    }
	}
	return js;
    }

    /**
     * The JVN service is not used anymore
     * 
     * @throws JvnException
     **/
    public void jvnTerminate() throws jvn.JvnException {
	// to be completed
    }

    /**
     * creation of a JVN object
     * 
     * @param o
     *            : the JVN object state
     * @throws JvnException
     **/
    public JvnObject jvnCreateObject(Serializable o) throws jvn.JvnException {

	try {
	    int id = this.coordinateur.jvnGetObjectId();
	    JvnObject object = new JvnObjectImpl(o, id,this);
	    object.jvnLockWrite();

	    Tools.println("<CLIENT %date>Creation d'un objet d'id=" + id);
	    return object;
	} catch (RemoteException e) {
	    e.printStackTrace();
	    return null;
	}
    }

    /**
     * Associate a symbolic name with a JVN object
     * 
     * @param jon
     *            : the JVN object name
     * @param jo
     *            : the JVN object
     * @throws JvnException
     **/
    public void jvnRegisterObject(String jon, JvnObject jo) throws jvn.JvnException {

	Tools.println("<CLIENT %date>Register de " + jon);
	jo.setRegisterInfo(this, jon);
	this.cache.put(jo.jvnGetObjectId(), jo);
	this.serviceNommage.put(jon, jo.jvnGetObjectId());
	try {
	    coordinateur.jvnRegisterObject(jon, jo, this);
	} catch (RemoteException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Provide the reference of a JVN object beeing given its symbolic name
     * 
     * @param jon
     *            : the JVN object name
     * @return the JVN object
     * @throws JvnException
     **/
    public JvnObject jvnLookupObject(String jon) throws jvn.JvnException {

	Tools.print("<CLIENT %d>lookup ... ");
	Integer id = this.serviceNommage.get(jon);
	if (!cache.containsKey(id))
	    try {
		Tools.println("... demande au coordinateur");
		JvnObject o = coordinateur.jvnLookupObject(jon, this);
		if (o != null) {
		    o.setRegisterInfo(this, jon);
		    Tools.print("Sentence : " + ((Sentence) o.jvnGetObjectState()).read());
		    this.cache.put(o.jvnGetObjectId(), o);
		this.serviceNommage.put(jon, o.jvnGetObjectId());
		}
		
		return o;
	    } catch (RemoteException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	    }
	else {
	    Tools.println("... dans le cache " + this.cache.get(jon));
	    return this.cache.get(id);
	}
    }

    /**
     * Get a Read lock on a JVN object
     * 
     * @param joi
     *            : the JVN object identification
     * @return the current JVN object state
     * @throws JvnException
     **/
    public Serializable jvnLockRead(int joi) throws JvnException {
	try {
	    return this.coordinateur.jvnLockRead(joi, this);
	} catch (RemoteException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return null;

    }

    /**
     * Get a Write lock on a JVN object
     * 
     * @param joi
     *            : the JVN object identification
     * @return the current JVN object state
     * @throws JvnException
     **/
    public Serializable jvnLockWrite(int joi) throws JvnException {
	try {
	    Tools.println("<ServeurLocal %date>Demande de lock write au coordianteur");
	    return this.coordinateur.jvnLockWrite(joi, this);
	} catch (RemoteException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return null;
    }

    /**
     * Invalidate the Read lock of the JVN object identified by id called by the JvnCoord
     * 
     * @param joi
     *            : the JVN object id
     * @return void
     * @throws java.rmi.RemoteException
     *             ,JvnException
     **/
    public void jvnInvalidateReader(int joi) throws java.rmi.RemoteException, jvn.JvnException {
	// to be completed
	this.cache.get(joi).jvnInvalidateReader();
    };

    /**
     * Invalidate the Write lock of the JVN object identified by id
     * 
     * @param joi
     *            : the JVN object id
     * @return the current JVN object state
     * @throws java.rmi.RemoteException
     *             ,JvnException
     **/
    public Serializable jvnInvalidateWriter(int joi) throws java.rmi.RemoteException, jvn.JvnException {
	// to be completed
	return this.cache.get(joi).jvnInvalidateWriter();
    };

    /**
     * Reduce the Write lock of the JVN object identified by id
     * 
     * @param joi
     *            : the JVN object id
     * @return the current JVN object state
     * @throws java.rmi.RemoteException
     *             ,JvnException
     **/
    public Serializable jvnInvalidateWriterForReader(int joi) throws java.rmi.RemoteException, jvn.JvnException {
	// to be completed
	return this.cache.get(joi).jvnInvalidateWriterForReader();
    }

    public JvnRemoteCoord getCoordinateur() {
	return coordinateur;
    }

    public void setCoordinateur(JvnRemoteCoord coordinateur) {
	this.coordinateur = coordinateur;
    };

}
