/***
 * Sentence class : used for representing the text exchanged between users
 * during a chat application
 * Contact: 
 *
 * Authors: 
 */

package irc;

public class Sentence implements java.io.Serializable {
    String data;

    public Sentence() {
	data = new String("");
    }

    public void write(String text) {
	data = text;
	
	/*for(int i = 0; i<10000000;i++){
	    
	    double x = Math.sqrt(Double.MAX_VALUE - Math.pow(i, i)); //grosse opération (en theorie ...)
	    
	}*/
	
	
    }

    public String read() {
	return data;
    }

}