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
	
	Serializable s = this.leServeur.jvnLockRead(this.id);
	if(s != null)
	    this.theObject = s;
    }

    public void jvnLockWrite() throws JvnException {
	
	Serializable s = this.leServeur.jvnLockWrite(this.id);
	if(s != null)
	    this.theObject = s;
    }

    public synchronized void jvnUnLock() throws JvnException {
	this.leServeur.jvnUnlock(this.id);
	notifyAll();
    }

    public int jvnGetObjectId() throws JvnException {
	return id;
    }

    public Serializable jvnGetObjectState() throws JvnException {
	return theObject;
    }

    public void jvnInvalidateReader() throws JvnException {
    }

    public Serializable jvnInvalidateWriter() throws JvnException {

	return theObject;
    }

    public Serializable jvnInvalidateWriterForReader() throws JvnException {
	
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
