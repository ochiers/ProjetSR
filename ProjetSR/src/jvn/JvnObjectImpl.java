package jvn;

import java.io.Serializable;

public class JvnObjectImpl implements JvnObject {

    Serializable theObject;

    int id;

    public transient JvnLocalServer leServeur;
    public String nameGiven;

    private StateLock state;

    public JvnObjectImpl(Serializable o, int id) {
	this.theObject = o;
	this.id = id;
    }

    public void jvnLockRead() throws JvnException {
	if (this.leServeur != null)
	    this.theObject = this.leServeur.jvnLookupObject(nameGiven).jvnGetObjectState();

	switch (state) {
	case NL:
	case RC:
	    this.state = StateLock.R;
	    this.theObject = this.leServeur.jvnLockRead(id);
	    break;
	case W:
	    /* Cas à redéfinir */
	    break;
	case WC:
	    this.state = StateLock.RWC;
	    this.theObject = this.leServeur.jvnLockRead(id);
	    break;
	default: // state = R ou state = RWC
	    break;
	}

    }

    public void jvnLockWrite() throws JvnException {
	switch (state) {
	case NL:
	case RC:
	    this.state = StateLock.W;
	    this.theObject = this.leServeur.jvnLockWrite(id);
	    break;
	case R:
	case RWC:
	    break;
	case WC:
	    this.state = StateLock.W;
	    break;
	default: // state = W
	    break;
	}

    }

    public void jvnUnLock() throws JvnException {
	// if (this.leServeur != null)
	// this.leServeur.jvnRegisterObject(nameGiven, this);

	this.state = StateLock.NL;

    }

    public int jvnGetObjectId() throws JvnException {
	return id;
    }

    public Serializable jvnGetObjectState() throws JvnException {
	return theObject;
    }

    public void jvnInvalidateReader() throws JvnException {
	this.state = StateLock.NL;
    }

    public Serializable jvnInvalidateWriter() throws JvnException {
	this.state = StateLock.NL;
//	this.wait();
	return theObject;
    }

    public Serializable jvnInvalidateWriterForReader() throws JvnException {
	this.state = StateLock.NL;
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
    
}
