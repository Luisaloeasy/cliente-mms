/**
 * @author Gabriell Calatrava 
 *
 * Scheduler Class Utility for Ignore Busy Tasks 
 */
package b2b.util.scheduler;


import java.util.*;


public class SchedulerBusyMonitor extends Thread {

	private Map initParameters = new Hashtable();
	public  boolean isBusy = false;
	public  Date   timeCallingTask = new Date();

	/**
	 * metodo run del Thread
	 */
	public void run() 
	{

		// Nombre de la tarea a invocar
		String classTask = getInitParameter("task-class");
		
		try{
	
			if (classTask==null){
				throw new Exception ("Parámetro 'task-class' no encontrado para la Tarea");
			}

	
			if ( !isBusy() )
			{
				//Marca este hilo como Ocupado hasta que la clase finalice su procesamiento
				//Esto evita que el timer reposicione su timepo de inicio al finalizar la tarea 
				isBusy = true;
				
				// Hora de inicio de la tarea
				timeCallingTask = new Date();
				//Retorna la Clase
				Class taskObject = Class.forName(classTask);
				Log.info( this.getClass(), "Run(): Monitor calling " + classTask + " at " + timeCallingTask.toString() );
				//Instancia la Tarea
				SchedulerBusyInterface task = (SchedulerBusyInterface) taskObject.newInstance();

				//Inicia la Tarea
				task.run( getInitParameters() );
			}
			else
				Log.info( this.getClass(), "Run(): Monitor Canceled. Busy Task [" + classTask + "] Since " + timeCallingTask.toString() );

  	
			Log.info( this.getClass(), "Run() End");	
		}
		catch(Exception e)
		{
			Log.error( this.getClass(), "Run() Exception: " + e.getMessage(), e ) ;	
		}
		finally
		{
			isBusy=false;
			Log.info( this.getClass(), "Run(): Monitor Ended. Busy Task [" + classTask + "] Since " + timeCallingTask.toString() );	
		}
	}

	/**
	 * Set parameters for this scheduler task.
	 * @since 1.1
	 */
	public synchronized boolean isBusy()
	{
		return isBusy; 
	}

	/**
	 * Set parameters for this scheduler task.
	 * @since 1.1
	 */
	public void setInitParameters( Map parameters )
	{
		initParameters=parameters; 
	}
	
	/**
	 * Get parameters for this scheduler task.
	 * @since 1.1
	 */
	public Map getInitParameters()
	{
		return initParameters; 
	}
	
	/**
	 * Gets parameter value from scheduler configuration file, matching 
	 * supplied parameter name.
	 * @param paramName name of parameter to get
	 * @return parameter value
	 * @since 1.1
	 */
	public String getInitParameter(String paramName) {
		return (String) initParameters.get(paramName);
	}
	
	
	/*
	 * Debug Print Utility
	 * */	

}
