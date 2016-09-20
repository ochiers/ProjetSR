package jvn;

import java.io.Serializable;

public class Lock implements Serializable{

    
    private boolean locked;
    private boolean used;
    
    public boolean isLocked() {
        return locked;
    }
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
    public boolean isUsed() {
        return used;
    }
    public void setUsed(boolean used) {
        this.used = used;
    }
    
}
