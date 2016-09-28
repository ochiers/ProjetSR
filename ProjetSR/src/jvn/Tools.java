package jvn;

import java.time.Instant;
import java.util.Date;

public class Tools {

    
    public static void println(String str){
	
	Date d = Date.from(Instant.now());
	String strDate = d.getHours()+":"+d.getMinutes()+":"+d.getSeconds();
	System.out.println(str.replaceAll("%date", strDate));
    }
    
    public static void print(String str){
	
	Date d = Date.from(Instant.now());
	String strDate = d.getHours()+":"+d.getMinutes()+":"+d.getSeconds();
	System.out.print(str.replaceAll("%date", strDate));
	
    }   
}
