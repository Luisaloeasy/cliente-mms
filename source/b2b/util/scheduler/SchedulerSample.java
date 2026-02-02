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
public class SchedulerSample extends MSCSchedulerTask {

    public static int counter = 0;

	public void run()
	{
		try{
			Log.info( this.getClass(), "TAREA  INI!!!! " + new Date().toString() + "***" + getInitParameter("CancelOnBusy"));
			
			Thread.sleep(1000);
			if (counter==0)
			{
				Thread.sleep(0);
				counter++;
			}

			Log.info( this.getClass(), "TAREA  END!!!! " + new Date().toString());
		}
		catch(InterruptedException ie)
		{}
		catch(Exception e)
		{}

	}
	
	
	
}
