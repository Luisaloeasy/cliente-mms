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
public class SchedulerTimerWindowsSample2 implements SchedulerBusyInterface 
{

  
  public void run (Map parameters) throws Exception, InterruptedException
  {
  		try{
		
	  		Log.info( this.getClass(), "--0> On Windows Sample 2  INI : " + new Date().toString() );
	  		Thread.sleep(1000);
			Log.info( this.getClass(), "--1> On Windows Sample 2  END : " + new Date().toString() );
			Thread.sleep(1000);
			Log.info( this.getClass(), "--2> On Windows Sample 2  END : " + new Date().toString() );
	  		Thread.sleep(1000);
			Log.info( this.getClass(), "--3> On Windows Sample 2  END : " + new Date().toString() );
	  		Thread.sleep(1000);
			Log.info( this.getClass(), "--4> On Windows Sample 2  END : " + new Date().toString() );
	  		Thread.sleep(1000);
			Log.info( this.getClass(), "--5> On Windows Sample 2  END : " + new Date().toString() );
	  		Thread.sleep(1000);
			Log.info( this.getClass(), "--6> On Windows Sample 2  END : " + new Date().toString() );
	  		Thread.sleep(1000);
			Log.info( this.getClass(), "--7> On Windows Sample 2  END : " + new Date().toString() );
	  		Thread.sleep(1000);
			Log.info( this.getClass(), "--8> On Windows Sample 2  END : " + new Date().toString() );
	  		Thread.sleep(1000);
			Log.info( this.getClass(), "--9> On Windows Sample 2  END : " + new Date().toString() );
	  		Thread.sleep(1000);
			Log.info( this.getClass(), "--10> On Windows Sample 2  END : " + new Date().toString() );


  		}
		catch(Exception e)
		{
		}
		finally
		{
		}
  }
  
 
  
}
