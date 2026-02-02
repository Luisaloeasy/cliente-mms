/**
 * @author Gabriell Calatrava 
 *
 * Scheduler Class Model. This Class prevent calling a task if it's still
 * busy of the last call. 
 */
package b2b.util.scheduler;



import java.text.SimpleDateFormat;
import java.util.*;

import electric.xml.Document;
import electric.xml.XPath;

public class SchedulerTimerWindowsTask extends MSCSchedulerTask {

	SchedulerBusyMonitor monitor = null; 
	int[] inicio = null;
	int[] fin = null;

	/**
	 * 
	 * @param horaFin Hora en formato hh:mm:ss
	 * @return true si esa hora es menor a la actual
	 * @throws Exception
	 */
	public boolean isInHour() throws Exception
	{
		Calendar actual = Calendar.getInstance();
	
		SimpleDateFormat sd = new SimpleDateFormat("HHmmss");
		int horaActual = Integer.parseInt(sd.format(actual.getTime()));
		boolean isInHour = false;
		
		// Verifica se esta en la hora programada
		for (int v =1; v < inicio.length; v++ )
		{

			if (horaActual >= inicio[v]  &&  horaActual<=fin[v] )
				{
					
					isInHour =  true;
					break;
				}
			}
		
		return isInHour;
		
	}
	/**
	 * Carga del xml de configuracion del pluging las horas a las cuales se ejecutara la Tarea
	 * @throws Exception
	 */
	public void setTimes() throws Exception
	{
		if (getInitParameter("times")==null)
			throw new Exception ("No found the parameter for windows times, for this Task");
		
		Document doc = null; 
		try
			{
				doc = new Document(getInitParameter("times"));
			}
		catch (Exception eDoc)
			{
				throw new Exception ("invalid XML format, for parameter \"times\" ");
			}
		Log.info( this.getClass(), "Loading Windows Times");
		
		// Obtiene la cantidad de ventanas Definbidas
		int cant =  doc.getElements(new XPath("/times/time")).size(); 
		
		if (cant==0)
			throw new Exception ("No found windows times defined, for this Task");

		
		//Dimensiona los arreglos segun la cantidad de ventana definidas
		fin = new int[cant+1];
		inicio = new int[cant+1];
		
		String horaini, horafin = "";
		
		for (int i =1; i <= cant; i++)
		{
			// verifica la existencia  de los tag from, to
				if (doc.getElement(new XPath ("/times/time[" + i + "]/from"))==null)
					throw new Exception("tag /times/time/from not found in the window times configuration");
				if (doc.getElement(new XPath ("/times/time[" + i + "]/to"))==null)
					throw new Exception("tag /times/time/to not found in the window times configuration");
					

				horaini = doc.getElement(new XPath ("/times/time[" + i + "]/from")).getText().toString();
				horafin = doc.getElement(new XPath ("/times/time[" + i + "]/to")).getText().toString();
			
			// Valida el formato de la hora
			if (isValidTime(horaini)  && isValidTime(horafin) )
			{					
					inicio[i] = Integer.parseInt(SchedulerUtil.replace(horaini,":", ""));
					fin[i] = Integer.parseInt(SchedulerUtil.replace(horafin,":",""));
			}
			else
				{
					
					// Envia una exception con el formato de la hora
					throw new Exception("Invalid time format, should be hh:mm:ss in 24 hours format \r\n Start time: " + horaini + "\r\n End time: " + horafin);
				}
		}
				
		
	}
	
	/**
	 * Valida el formatoi de la hora
	 * @param time
	 * @return
	 */
	public boolean isValidTime (String time )
	{
		try 
			{
				SimpleDateFormat sd = new SimpleDateFormat("HH:mm:ss");
				if (!sd.format(sd.parse(time)).equals(time)) 
					throw new Exception ("Invalid time format");
				return true;
			} 
		catch (Exception e) 
			{
				return false;
			}
		
	}

	
	
	public void run()
	{
		try
		
			{
				// Carga las ventanas de tiempo
				if (inicio==null)
				{
					try
						{
							setTimes();	
						}
					catch (Exception e)
						{
							
							Log.info( this.getClass(), "The Task won't be started");
							Log.error( this.getClass(), "Error: " + e.getMessage(), e);
							if (e.getMessage()==null)
								e.printStackTrace();
							inicio=null;
							fin=null;
							return;
						}
				}
				Log.info( this.getClass(), "Checker: Cheking at " + new Date().toString() );

			// Verifica que la tarea este en la Hora
			if( isInHour())
				{
					Log.info( this.getClass(), "Checker: Task In Hour.  Cheking BusyTask." );
				
					// Si esta ocupado el unico hilo no se invoca la tarea.
					if ( (monitor==null || !monitor.isBusy())  )
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
	
				}
			else
				{
					Log.info( this.getClass(), "Checker Task NOT In Hour.!");
				}

			Log.info( this.getClass(), "Checker: End");
		}
		
		catch(Exception e)
		{ 
			Log.error(this.getClass(), "", e);
			 
		}

	}
	
	
	
}
