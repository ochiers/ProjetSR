package jvn;

import java.io.Serializable;

public class JvnObjectImpl implements JvnObject {

    Serializable theObject;

    int id;

    public transient JvnLocalServer leServeur;
    public String nameGiven;

    public JvnObjectImpl(Serializable o, int id, JvnLocalServer js) {
	this.theObject = o;
	this.id = id;
	this.leServeur = js;
    }

    public void jvnLockRead() throws JvnException {

	System.out.println(this.leServeur.getStateLock(this.id));
	
	switch (this.leServeur.getStateLock(this.id)) {
	case NL:
	    this.leServeur.setStateLock(this.id, StateLock.R);
	    this.theObject = this.leServeur.jvnLockRead(this.id);
	    break;
	case RC:
	    this.leServeur.setStateLock(this.id, StateLock.R);
	    this.theObject = this.leServeur.readCache(this.id);
	    break;
	case W:
	    break;
	case WC:
	    this.leServeur.setStateLock(this.id, StateLock.RWC);
	    this.theObject = this.leServeur.readCache(this.id);
	    break;
	default: // state = R ou state = RWC
	    break;
	}

    }

    public void jvnLockWrite() throws JvnException {

	switch (this.leServeur.getStateLock(this.id)) {
	case NL:
	case RC:
	    this.leServeur.setStateLock(this.id, StateLock.W);
	    Serializable s = this.leServeur.jvnLockWrite(this.id);
	    if (s != null)
		this.theObject = s;
	    break;
	case R:
	case RWC:
	    break;
	case WC:
	    this.leServeur.setStateLock(this.id, StateLock.W);
	    break;
	default: // state = W
	    break;
	}

    }

    public synchronized void jvnUnLock() throws JvnException {

	switch (this.leServeur.getStateLock(this.id)) {
	case W:
	    // this.leServeur.jvnRegisterObject(nameGiven, this);
	    this.leServeur.setStateLock(this.id, StateLock.WC);
	    break;
	case R:
	    this.leServeur.setStateLock(this.id, StateLock.RC);
	    break;
	case RWC:
	    this.leServeur.setStateLock(this.id, StateLock.WC);
	    break;
	default:
	    break;
	}

	notifyAll();
    }

    public int jvnGetObjectId() throws JvnException {
	return id;
    }

    public Serializable jvnGetObjectState() throws JvnException {
	return theObject;
    }

    public synchronized void jvnInvalidateReader() throws JvnException {

	if(this.leServeur.getStateLock(this.id) == StateLock.R)
	    try {
		wait();
	    } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	
	this.leServeur.setStateLock(this.id,  StateLock.NL);
	
	
    }

    public synchronized Serializable jvnInvalidateWriter() throws JvnException {
	
	StateLock stateCour = this.leServeur.getStateLock(this.id);
	
	switch (stateCour) {
	case W:
	case R:  
	    try {
		this.wait();
		this.leServeur.setStateLock(this.id,  StateLock.NL);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}break;
	case RC:
	case WC:
	case RWC:
	    this.leServeur.setStateLock(this.id,  StateLock.NL);
	    break;
	default:
	    break; 
	}
	
	
	return theObject;
    }

    public synchronized Serializable jvnInvalidateWriterForReader() throws JvnException {
	
	StateLock stateCour = this.leServeur.getStateLock(this.id);
	switch (stateCour) {
	case W:
	    try {
		wait();
	    } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    this.leServeur.setStateLock(this.id,  StateLock.RC);
	    break;
	case WC:
	    this.leServeur.setStateLock(this.id,  StateLock.NL);
	    break;
	case RWC:
	    this.leServeur.setStateLock(this.id,  StateLock.R);
	    break;
	default:
	    break;
	}
	
	return theObject;
    }

    public void setRegisterInfo(JvnLocalServer js, String name) {
	this.leServeur = js;
	this.nameGiven = name;
    }

    public Serializable getTheObject() {
	return theObject;
    }

    public void setTheObject(Serializable theObject) {
	this.theObject = theObject;
    }

    @Override
    public String toString() {

	return "Object id = " + this.id + ", nom : " + this.nameGiven;

    }

}
