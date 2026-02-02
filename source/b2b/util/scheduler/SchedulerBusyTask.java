/**
 * @author Gabriell Calatrava 
 *
 * Scheduler Class Model. This Class prevent calling a task if it's still
 * busy of the last call. 
 */
package b2b.util.scheduler;


import java.util.*;

public class SchedulerBusyTask extends MSCSchedulerTask {

	SchedulerBusyMonitor monitor = null; 

	public void run()
	{
		try{
						
			Log.info( this.getClass(), "Checker: Cheking at " + new Date().toString() );
			
	
			// Si esta ocupado el unico hilo no se invoca la tarea.
			if ( monitor==null || !monitor.isBusy() )
			{
				Log.info( this.getClass(), "Checker: Task Not Busy. Calling Task.");
				monitor = new SchedulerBusyMonitor();
				monitor.setInitParameters( getInitParameters() );
				monitor.start();
			}
			else
			{
				Log.info( this.getClass(), "Checker: Still Busy. Calling Canceled!");
			}

			Log.info( this.getClass(), "Checker: End");
		}
		
		catch(Exception e)
		{ 
			Log.error(this.getClass(), "", e);
			 
		}

	}
	
	
}
