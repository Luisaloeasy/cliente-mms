/*
 * Created on 08/08/2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package b2b.util.scheduler;

/**
 * @author Nagel Camacho 
 *
 * Scheduler Class Sample
 */
public class SchedulerRefresh extends MSCSchedulerTask {

    public static int counter = 0;

	public void run()
	{
		try{
		//	debug("refreshing Scerhduler Config!!!! " + new Date().toString() + "***" + getInitParameter("CancelOnBusy"));
			
			MSCScheduler.doRefresh();
			
		}
		
		catch(Exception e)
		{}

	}
	
	

}
