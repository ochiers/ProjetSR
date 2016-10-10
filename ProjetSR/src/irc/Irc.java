/***
 * Irc class : simple implementation of a chat using JAVANAISE
 * Contact: 
 *
 * Authors: 
 */

package irc;

import java.awt.*;
import java.awt.event.*;
import jvn.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Irc {
    public TextArea text;
    public TextField data;
    Frame frame;
    ISentence sentence;

    /**
     * main method create a JVN object nammed IRC for representing the Chat application
     **/
    public static void main(String argv[]) {
	try {

	    Registry registre = LocateRegistry.getRegistry("localhost");
	    JvnRemoteCoord coordinateur = (JvnRemoteCoord) registre.lookup("serveur");

	    // initialize JVN
	    JvnServerImpl js = JvnServerImpl.jvnGetServer(coordinateur);

	    ISentence jo = (ISentence) MyProxy.newInstance(js, "IRC", Sentence.class);

	    new Irc(jo);

	} catch (Exception e) {
	    e.printStackTrace();
	    System.out.println("IRC problem : " + e.getMessage());
	}
    }

    /**
     * IRC Constructor
     * 
     * @param jo
     *            the JVN object representing the Chat
     **/
    public Irc(ISentence jo) {
	sentence = jo;
	frame = new Frame();
	frame.setLayout(new GridLayout(1, 1));
	text = new TextArea(10, 60);
	text.setEditable(false);
	text.setForeground(Color.red);
	frame.add(text);
	data = new TextField(40);
	frame.add(data);
	Button read_button = new Button("read");
	read_button.addActionListener(new readListener(this));
	frame.add(read_button);
	Button write_button = new Button("write");
	write_button.addActionListener(new writeListener(this));
	frame.add(write_button);

	Button unlock_button = new Button("unlock");
	unlock_button.addActionListener(new unlockListener(this));
	frame.add(unlock_button);

	frame.setSize(545, 201);
	text.setBackground(Color.black);
	frame.setVisible(true);

	frame.addWindowListener(new WindowAdapter() {
	    public void windowClosing(WindowEvent we) {
		System.exit(0);
	    }
	});

    }
}

/**
 * Internal class to manage user events (read) on the CHAT application
 **/
class readListener implements ActionListener {
    Irc irc;

    public readListener(Irc i) {
	irc = i;
    }

    /**
     * Management of user events
     **/
    public void actionPerformed(ActionEvent e) {
	try {
	    // lock the object in read mode
	    // irc.sentence.jvnLockRead();

	    // invoke the method
	    String s = irc.sentence.read();

	    // display the read value
	    irc.data.setText(s);
	    irc.text.append(s + "\n");
	} catch (Exception je) {
	    System.out.println("IRC problem : " + je.getMessage());
	}
    }
}

/**
 * Internal class to manage user events (write) on the CHAT application
 **/
class writeListener implements ActionListener {
    Irc irc;

    public writeListener(Irc i) {
	irc = i;
    }

    /**
     * Management of user events
     **/
    public void actionPerformed(ActionEvent e) {
	try {
	    // get the value to be written from the buffer
	    String s = irc.data.getText();

	    // invoke the method
	    irc.sentence.write(s);

	} catch (Exception je) {
	    System.out.println("IRC problem  : " + je.getMessage());
	}
    }
}

class unlockListener implements ActionListener {
    Irc irc;

    public unlockListener(Irc i) {
	irc = i;
    }

    /**
     * Management of user events
     **/
    public void actionPerformed(ActionEvent e) {
	/*
	 * try { // unlock the object irc.sentence.jvnUnLock(); } catch (JvnException je) { System.out.println("IRC problem  : " + je.getMessage()); }
	 */
    }
}
