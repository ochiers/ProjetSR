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
	/*try {
	    Thread.sleep(20000);
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}*/
    }

    public String read() {
	return data;
    }

}