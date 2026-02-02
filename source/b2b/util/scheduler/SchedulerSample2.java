/*
 * Created on 08/08/2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package b2b.util.scheduler;


import java.util.*;

/**
 * @author Gabriell Calatrava 
 *
 * Scheduler Class Sample
 */
public class SchedulerSample2 extends MSCSchedulerTask {

    public static int counter = 0;

	public void run()
	{
		try{
			Log.info( this.getClass(), counter  + " TAREA  INI!!!! " + new Date().toString() + "***" + getInitParameter("CancelOnBusy"));
			
			//Thread.sleep(20000);
			

			Log.info( this.getClass(), counter  + "TAREA  END!!!! " + new Date().toString());
		}
		catch(Exception e)
		{}

	}
	
	

}
