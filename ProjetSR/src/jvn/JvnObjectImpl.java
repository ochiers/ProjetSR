package jvn;

import java.io.Serializable;

public class JvnObjectImpl implements JvnObject {

    Serializable theObject;

    int id;

    public transient JvnLocalServer leServeur;
    public String nameGiven;

    private StateLock state;

    public JvnObjectImpl(Serializable o, int id, JvnLocalServer js) {
	this.theObject = o;
	this.id = id;
	this.state = StateLock.NL;
	this.leServeur = js;
    }
    

    public void jvnLockRead() throws JvnException {
	/*
	 * if (this.leServeur != null) this.theObject = this.leServeur.jvnLookupObject(nameGiven).jvnGetObjectState();
	 */
	System.out.println("<jvnObject id=" + this.id + ">Demande verrou read, etat actuel : " + this.state);
	switch (state) {
	case NL:
	case RC:
	    this.state = StateLock.R;
	    System.out.println("<jvnObject id=" + this.id + ">Verrou : NL|RC->R");
	    // this.theObject = this.leServeur.jvnLockRead(id);
	    this.theObject = this.leServeur.jvnLookupObject(this.nameGiven).getTheObject();
	    System.out.println("<jvnObject id=" + this.id + ">Serializable = " + this.theObject);

	    break;
	case W:
	    System.out.println("<jvnObject id=" + this.id + ">Verrou : W->????");
	    break;
	case WC:
	    this.state = StateLock.RWC;
	    System.out.println("<jvnObject id=" + this.id + ">Verrou : WC->RWC");
	    this.theObject = this.leServeur.jvnLockRead(id);
	    System.out.println("<jvnObject id=" + this.id + ">Serializable = " + this.theObject);
	    break;
	default: // state = R ou state = RWC
	    break;
	}
	System.out.println("<jvnObject id=" + this.id + ">Demande verrou read, etat a la sortie : " + this.state);
    }

    public void jvnLockWrite() throws JvnException {
	System.out.println("<jvnObject id=" + this.id + ">Demande verrou write, etat actuel : " + this.state);
	switch (state) {
	case NL:
	case RC:
	    this.state = StateLock.W;
	    Serializable s = this.leServeur.jvnLockWrite(id);
	    if (s != null)// Si dans le coordinateur
		this.theObject = s;
	    System.out.println("<jvnObject id=" + this.id + ">Verrou : NL|RC->W");
	    break;
	case R:
	case RWC:
	    break;
	case WC:
	    this.state = StateLock.W;
	    System.out.println("<jvnObject id=" + this.id + ">Verrou : WC->W");
	    break;
	default: // state = W
	    break;
	}
	System.out.println("<jvnObject id=" + this.id + ">Demande verrou write, etat a la sortie : " + this.state);
    }

    public void jvnUnLock() throws JvnException {
	// if (this.leServeur != null)
	// this.leServeur.jvnRegisterObject(nameGiven, this);

	System.out.println("Etat avant le unlock : " + this.state);

	switch (this.state) {
	case W:
	    this.leServeur.jvnRegisterObject(nameGiven, this);
	    this.state = StateLock.WC;
	    break;
	case R:
	    this.state = StateLock.RC;
	    break;
	case RC:
	    break;
	case RWC:
	    this.state = StateLock.WC;
	    break;
	case WC:
	    break;
	case NL:
	    break;
	}

	System.out.println("Etat apres le unlock : " + this.state);
	/*
	 * if(this.state == StateLock.W) this.leServeur.jvnRegisterObject(nameGiven, this); this.state = StateLock.NL;
	 */

    }

    public int jvnGetObjectId() throws JvnException {
	return id;
    }

    public Serializable jvnGetObjectState() throws JvnException {
	return theObject;
    }

    public void jvnInvalidateReader() throws JvnException {
	System.out.println("<jvnObject id=" + this.id + ">InvalidateReader " + this.state + "->NL");
	this.state = StateLock.NL;
    }

    public Serializable jvnInvalidateWriter() throws JvnException {
	System.out.println("<jvnObject id=" + this.id + ">InvalidateWriter " + this.state + "->NL");

	switch (this.state) {
	case W:
	    this.state = StateLock.NL;
	    break;
	case R:
	    break;
	case RC:
	    break;
	case RWC:
	    break;
	case WC:
	    break;
	case NL:
	    break;
	}
	// this.wait();
	return theObject;
    }

    public Serializable jvnInvalidateWriterForReader() throws JvnException {
	System.out.println("<jvnObject id=" + this.id + ">InvalidateWriterForReader " + this.state + "->RC");
	switch (this.state) {
	case W:
	    this.state = StateLock.RC;
	    break;
	case R:
	    break;
	case RC:
	    break;
	case RWC:
	    this.state = StateLock.R;
	    break;
	case WC:
	    break;
	case NL:
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
