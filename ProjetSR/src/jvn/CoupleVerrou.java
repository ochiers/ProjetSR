package jvn;

public class CoupleVerrou {
    
    private JvnRemoteServer js;
    private StateLock state;
    
    public CoupleVerrou(JvnRemoteServer js, StateLock state) {
	this.js = js;
	this.state = state;
    }
    
    public JvnRemoteServer getJs() {
        return js;
    }
    public void setJs(JvnRemoteServer js) {
        this.js = js;
    }
    public StateLock getState() {
        return state;
    }
    public void setState(StateLock state) {
        this.state = state;
    }
    
    

}
