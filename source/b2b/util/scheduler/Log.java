package b2b.util.scheduler;

 // Import log4j classes.

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

 public class Log {

  
	 
	 public static void init(String strFileName)
      {
    	  PropertyConfigurator.configure(strFileName);
      }
      
	 public static void info (Class cl, String text)
     {
		 //= Logger.getLogger(cl).info(text);
     }
	 public static void info (Class cl, String text, Throwable th )
     {
		 //= Logger.getLogger(cl).info(text, th);
     }

	 
	 public static void debug (Class cl, String text)
     {
		 //= Logger.getLogger(cl).debug(text);
     }

	 public static void debug (Class cl, String text, Throwable th )
     {
		 //= Logger.getLogger(cl).debug(text, th);
     }
	 
	 public static void warn (Class cl, String text)
     {
		 //= Logger.getLogger(cl).warn(text);
     }

	 public static void warn (Class cl, String text, Throwable th )
     {
		 //= Logger.getLogger(cl).warn(text, th);
     }


	 
	 public static void error (Class cl, String text)
     {
		 //= Logger.getLogger(cl).error(text);
     }

	 public static void error (Class cl, String text, Throwable th )
     {
		 //= Logger.getLogger(cl).error(text, th);
     }

	 public static void fatal (Class cl, String text)
     {
		 //= Logger.getLogger(cl).error(text);
     }

	 public static void fatal (Class cl, String text, Throwable th )
     {
		 //= Logger.getLogger(cl).error(text, th);
     }

 }