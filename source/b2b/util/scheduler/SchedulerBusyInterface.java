/**
 * @author Gabriell Calatrava 
 *
 * Scheduler Interface
 */
package b2b.util.scheduler;

import java.util.*;

public interface SchedulerBusyInterface 
{
  
  public void run (Map parameters) throws Exception, InterruptedException;
  
}
