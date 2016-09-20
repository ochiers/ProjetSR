package jvn;

import java.io.Serializable;

public class JvnObjectImpl implements JvnObject {

    Serializable theObject;
    int id;
    
    public Lock readLock;
    public Lock writeLock;
    
    public JvnLocalServer leServeur;
    
    public JvnObjectImpl(Serializable o, int id, JvnLocalServer js){
	
	this.theObject = o;
	this.id = id;
	this.leServeur = js;
	this.readLock = new Lock();
	this.writeLock = new Lock();
    }
    
    public void jvnLockRead() throws JvnException {
	this.readLock.setLocked(true);

    }

    public void jvnLockWrite() throws JvnException {
	this.writeLock.setLocked(true);
	
    }

    public void jvnUnLock() throws JvnException {
	this.readLock.setLocked(false);
	this.writeLock.setLocked(false);
	//this.leServeur.jvnRegisterObject(jon, jo);

    }

    public int jvnGetObjectId() throws JvnException {
	// TODO Auto-generated method stub
	return id;
    }

    public Serializable jvnGetObjectState() throws JvnException {
	return theObject;
    }

    public void jvnInvalidateReader() throws JvnException {
	
	while(this.readLock.isUsed())
	    try {
		wait();
	    } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	
	this.readLock.setLocked(false);

    }

    public Serializable jvnInvalidateWriter() throws JvnException {

	this.writeLock.setLocked(false);
	return theObject;
    }

    public Serializable jvnInvalidateWriterForReader() throws JvnException {
	
	this.readLock.setLocked(true);
	this.writeLock.setLocked(false);
	
	return theObject;
    }

}
