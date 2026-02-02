/**
 * @author Gabriell Calatrava 
 *
 * Scheduler Simple Sample
 */
package b2b.util.scheduler;

import java.util.*;

/**
 * @author Gabriell Calatrava
 *
 * Declaracion de la interfaz
 */
public class SchedulerBusySample implements SchedulerBusyInterface 
{

  
  public void run (Map parameters) throws Exception, InterruptedException
  {
  		try{
		
	  		Log.info( this.getClass(), "--> On Busy Sample INI : " + new Date().toString() );
	  		Thread.sleep(25000);
			Log.info( this.getClass(), "--> On Busy Sample END : " + new Date().toString() );
		}
		catch(Exception e)
		{
		}
		finally
		{
		}
  }
  
  
}
