package irc;

import jvn.Mode;


public interface ISentence {

    @Mode(mode=jvn.Mode.ModeType.Write)
    public void write(String text) ;
    
    @Mode(mode=jvn.Mode.ModeType.Read)
    public String read() ;
}
