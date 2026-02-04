package b2b.util.scheduler;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.text.*;
import java.util.*;




/**
 * @author Gabriell Calatrava 
 *
 * Scheduler Class
 */
public class MSCScheduler extends Thread {

	private static  int TASK_ADD = 1;
	private static int TASK_EDIT = 2;
	

	//Mantiene este thread activo.
	private boolean endSchedule = false;
    
    //XML de tareas agendadas 
	private static String  config 		=	null;

	// Propiedades del Scheduler
	private static Properties SchedulerPro = new Properties();
	
	// Almacena las referencias a las tareas ejecutandose
	private static Hashtable       tasksRef = null;

	// Almacena las nuevas tareas 
	private static Hashtable 		taskUpdate  = null;

	// Referencia de los Timer Corriendo
	private static Hashtable       TimerHash = new Hashtable();
	
	// Almacena los TaskInfo Corriendo
	private static Hashtable  infoRunningTask = new Hashtable();
	
	/**
	 * Metodo Invocado desde la Clase Task para refrecar la configuraci�n
	 * @throws Exception
	 */
	public static void doRefresh() throws Exception
	{
		try
		{
		refresh();
		}
		catch (Exception e2)
		{
			Log.error(MSCScheduler.class, "Excepcion Refrescando " , e2 );
			
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		//log.debug("text");
		
		
		 
		//log.info(this, "Sample Stand Alone Application Starting..");
		 
		MSCScheduler app = new MSCScheduler("mscscheduler.properties");
		
		Log.init(SchedulerPro.getProperty("log4jsetting"));
		
		app.start();
		Thread.sleep(1600000000);
		app.endSheduler();
		Thread.sleep(30000);
		Log.info(MSCScheduler.class, "Sample Stand Alone Application Ending..");
	}
	
	/**
	 * metodo run del Thread
	 */
	
	
	/**
	 * Compara las Versiones de los task para determinar si hubo cambio o se agrego una nueva tarea
	 */
	private static int comparateVersion(String taskKey) throws Exception
	{

		int returnValue =0;
		// Verifica que no exista para agregarla
		if (!TimerHash.containsKey(taskKey))
			{
				
				Log.debug(MSCScheduler.class, " tarea Agregada " + taskKey);
				returnValue = TASK_ADD;
			
			}
			else
				{
						
					// Compara las versiones para determinar los cambios
					MSCTaskInfo taskInfoRunning =	(MSCTaskInfo) infoRunningTask.get(taskKey);
					MSCTaskInfo taskInfoUpdated =	(MSCTaskInfo) taskUpdate.get(taskKey);
					if 	(taskInfoRunning.getVersion()==null || taskInfoRunning.getVersion()==null )
						return 10;
					if (!taskInfoRunning.getVersion().equals(taskInfoUpdated.getVersion()))
						{
							Log.debug(MSCScheduler.class, " tarea modificada " + taskKey);
							returnValue =  TASK_EDIT;
						}
				}
		return returnValue;
	
	}
		
	/**
	 * Elimina las Tareas que se estan ejecutando y no estan en la actualizaci�n
	 * @throws Exception
	 */
	private static  void deleteNotInUpdated() throws Exception
	{
		Enumeration enDel = TimerHash.keys();
		
		while (enDel.hasMoreElements())
		{
			
			String keyDel =  (String)enDel.nextElement();
			if (!taskUpdate.containsKey(keyDel))
				{
					Log.debug(MSCScheduler.class, " Cancelando Tarea  " + keyDel);
					cancelTask(keyDel);
				}
		}
				
				
		
	}
		
		
	/**
	 * Agenda la Tarea nueva
	 * @param taskKey
	 */	
	private static void addTasksUpdated(String taskKey)
	{
	
	MSCTaskInfo taskInfo =	(MSCTaskInfo) taskUpdate.get(taskKey);
	 
	
	if (taskInfo.getEnabled()!=null)
		{
			if (taskInfo.getEnabled().equalsIgnoreCase("true"))
			{
				Log.info (MSCScheduler.class, taskKey +" - Task Loading:" + taskInfo.getTask()   );
				
				Log.info( MSCScheduler.class,taskKey +" - Description: " + taskInfo.getPamameter("Description"));
				MSCSchedulerTask task = createSchedulerTask(taskInfo);
				
				TimerHash.put(taskInfo.getName(),launchTimer(task, taskInfo) );
				infoRunningTask.put(taskInfo.getName(), taskInfo);
			}
			else 
			{
				Log.info( MSCScheduler.class, taskKey +" - Task Disabled: " + taskInfo.getTask());
				Log.info( MSCScheduler.class, taskKey +" - Description: " + taskInfo.getPamameter("Description"));
			}
	
		}
	else 
	
		{
			Log.info( MSCScheduler.class, "** Task Disabled: " + taskInfo.getTask());
			Log.info( MSCScheduler.class, "** Task Description: " + taskInfo.getPamameter("Description"));
		}
	}
	/**
	 * Metodo para Refrescar la configuracion
	 * @throws Exception
	 */
	private static void  refresh () throws Exception
	{
		loadConfig();
		
		Enumeration enUpdated = taskUpdate.keys();
		while (enUpdated.hasMoreElements())
		{
			
			String key = (String) enUpdated.nextElement(); 
			Log.info( MSCScheduler.class,  key + " - Looking changes in " );	
			
			int resultCompara = comparateVersion(key);
			
			
			if (resultCompara== TASK_ADD)
			{
				Log.info( MSCScheduler.class, key  +" - Task New ");
				addTasksUpdated(key);
			}
				
			else if (resultCompara== TASK_EDIT)
			{
				Log.info( MSCScheduler.class, key + " - Task Modified");
				cancelTask(key);	
				addTasksUpdated(key);
			}
			else
				Log.info( MSCScheduler.class, key  + " - Task not Modified or Add");
		}
		deleteNotInUpdated();
		
		

	}
	
	/**
	 * Obtiene el Timer y lo cancela
	 * @param key
	 */
	private  static  void cancelTask(String key )
	{
		Timer tDelete = (Timer)TimerHash.get(key);
		Log.info( MSCScheduler.class, key + " - Canceling Task " );
		
		tDelete.cancel();
		tasksRef.remove(key);
		TimerHash.remove(key);
	}
	/**
	 * Carga la configuracion desde el pluging
	 * @throws Exception
	 */
	private  static void loadConfig() throws Exception
	{
		// Obtiene el nombre de la clase Pliging la cual obtendra la configuraci�n de las tareas
		String clase = SchedulerPro.getProperty("class.config.pluging");
		Log.info( MSCScheduler.class, "Updating Scheduler looking config using Class = " + clase);
        MSCTaskConfig configPluging = (MSCTaskConfig) Class.forName(clase).getDeclaredConstructor().newInstance();
		config = configPluging.getConfig();
		
		MSCTasks Tasks = new MSCTasks();
		
		taskUpdate = Tasks.getTaskList(config);
		
		
		
		
		
		
	}
	public void run() 
	{
		
		try
		{
			loadConfig();
			
			init();
			asleep();

		}
		catch(Exception e)
		{
			
			Log.fatal( MSCScheduler.class, "MSCScheduler.Run() Exception: " + e.getMessage(), e ) ;	
			return;
		}
		finally
		{
			Log.info( MSCScheduler.class, "MSCScheduler.Run Finally");
		}
	}
	
	
	/**
	 * Constructor. Parameter specify the schedule file
	 */
	public MSCScheduler ( String schedulerFile )
	{
		
		try
		{
			
			FileInputStream fin = new FileInputStream(new File(schedulerFile));
			
			SchedulerPro.load(fin);
			
		}
		catch (Exception err)
		{
			
			Log.fatal( MSCScheduler.class, "Can`t Start Scheduler Properties ERROR: " + err.getMessage(), err);
			
		}
	}
	
	/**
	 * End the scheduler.
	 */
	private  void endSheduler ()
	{
		Log.info( MSCScheduler.class, "Notifiying Scheduler End");
		endSchedule = true;
	}

	/**
	 * Keep the thread in memory but sleeping.
	 * 
	 * @throws Exception, InterruptedException If something goes wrong
	 *         
	 */
	public void asleep() throws Exception, InterruptedException
	{
		while ( !endSchedule )
		{
			Thread.sleep(5000);
		}
		
	}

	
	
	/**
	 * Inicializa la Configuraci�n
	 */
	public void init() throws Exception {

		try {

			Log.info( MSCScheduler.class, "Initializing Configuration..");
			

			//MSCSchedulerTask tasks      = null;
			
		

			MSCTasks Tasks = new MSCTasks();
		
			tasksRef = Tasks.getTaskList(config);
           
			 
			
			// Load and run timers
			Enumeration en = tasksRef.keys();
			
			
			
			for (int i = 0; i < tasksRef.size(); i++) 
			while (en.hasMoreElements())
				{   
						String taskKey = (String)en.nextElement();
						Log.info( MSCScheduler.class, "Buscando Task " + taskKey);
						MSCTaskInfo taskInfo =	(MSCTaskInfo) tasksRef.get(taskKey);
					
						
						if (taskInfo.getEnabled()!=null)
							{
								if (taskInfo.getEnabled().equalsIgnoreCase("true"))
								{
									Log.info( MSCScheduler.class, "Task Loading:" + taskInfo.getTask()   );
									Log.info( MSCScheduler.class, "  - Description: " + taskInfo.getPamameter("Description"));
									MSCSchedulerTask task = createSchedulerTask(taskInfo);
									
									//launchTimer(task, taskInfo);
									TimerHash.put(taskInfo.getName(),launchTimer(task, taskInfo) );
									infoRunningTask.put(taskInfo.getName(), taskInfo);
								}
								else 
								{
									Log.info( MSCScheduler.class, "Task Disabled: " + taskInfo.getTask());
									Log.info( MSCScheduler.class, "  - Description: " + taskInfo.getPamameter("Description"));
								}
						
							}
						else 
						
							{
								Log.info( MSCScheduler.class, "** Task Disabled: " + taskInfo.getTask());
								Log.info( MSCScheduler.class, "** Task Description: " + taskInfo.getPamameter("Description"));
							}
					}	
		//	Enumeration enum  = TimerHash.keys();
	
	
	
			
			}
					
				 
			catch (Exception e) 
				{
					Log.error( MSCScheduler.class, "Initializing Configuration Error:" + e.getMessage(), e);
					throw new RuntimeException("ERROR in SchedulerServler.init():" + e);
				}
		
	
	}

	/**
	 * Creates and instantiate a <code>SchedulerTask</code> based on
	 * the provided <code>SchedulerTask</code> object.
	 * 
	 * @param taskInfo a <code>SchedulerTaskInfo</code> object.
	 * @return a <code>SchedulerTask</code> object.
	 * 
	 */
	private static MSCSchedulerTask createSchedulerTask(MSCTaskInfo taskInfo) {

		try {

            Class taskObject = Class.forName(taskInfo.getTask());
            MSCSchedulerTask task = (MSCSchedulerTask) taskObject.getDeclaredConstructor().newInstance();

			
			task.setInitParameters(taskInfo.getParameters());
			return task;

		} catch (java.lang.ClassNotFoundException e) {
			throw new RuntimeException(
					"ERROR in TimerTask.createSchedulerTask(). "
					+ "Provided class name does not exists. "
					+ "Exception is: " + e);
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(
                    "ERROR in TimerTask.createSchedulerTask(). "
                            + "Cannot instantiate provided class name. "
                            + "Exception is: " + e, e);
        }
    }

	/**
	 * Launches a <code>Timer</code> object with the provided task information.
	 * 
	 * @param task a <code>SchedulerTask</code> object.
	 * @param taskInfo a <code>SchedulerTaskInfo</code> object.
	 * 
	 */
	private static Timer launchTimer(MSCSchedulerTask task, MSCTaskInfo taskInfo) {
        
		Timer timer = new Timer(true);
		
		Date  time  = null;
        
		
		/*
		 * Parse the date from taskInfo if provided.
		 */
		if (taskInfo.getTime() != null) {

			// Create the date formert            
			DateFormat formatter = null; 
            
            
			/*
			 * Check for which format was provide yyyy-MM-dd HH:mm:ss,
			 * HH:mm:ss or yyy-MM-dd
			 */
			if (taskInfo.getTime().indexOf("-") > 0  && taskInfo.getTime().indexOf(":") > 0){ 
					// Full Date yyyy-MM-dd HH:mm:ss
					formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				}
			else if (taskInfo.getTime().indexOf("-") > 0 && taskInfo.getTime().indexOf(":") < 0) {
				// Just the date yyyy-MM-dd
				formatter = new SimpleDateFormat("yyyy-MM-dd");
			} 
			else if (taskInfo.getTime().indexOf("-") < 0 && taskInfo.getTime().indexOf(":") > 0) {
				// Just a time HH:mm:ss
				formatter = new SimpleDateFormat("HH:mm:ss");
			}
            
			try {
				time = formatter.parse(taskInfo.getTime());
			} 
			catch (java.text.ParseException e) {
					throw new IllegalArgumentException( "ERROR in SchedulerTask.setTime(String). "
														+ "Could not parse date provided date: '" + time + "'. "
														+ "Exception is: " + e);
			}
            
			// In case of just time, Add the appropriate date 
			// Si no existe Fecha

			// Calendario para la agenda
			Calendar userCal = Calendar.getInstance();

			// Calendario para la Hora actual			
			Calendar sysCal = Calendar.getInstance();

			if (taskInfo.getTime().indexOf("-") < 0	 && taskInfo.getTime().indexOf(":") > 0){

				// Get the User provided Time
				userCal.setTime(time);
                
				// Get System Calendar Date
            
				// Set the date for the time provided
				userCal.set(Calendar.YEAR, sysCal.get(java.util.Calendar.YEAR));
				userCal.set(Calendar.MONTH, sysCal.get(java.util.Calendar.MONTH));
				userCal.set(Calendar.DAY_OF_MONTH, sysCal.get(Calendar.DAY_OF_MONTH));

				 // Compare the two dates.
				if (userCal.getTime().getTime() < sysCal.getTime().getTime() && (taskInfo.getRunForced()==null ||  !taskInfo.getRunForced().equalsIgnoreCase("true"))) {
                    
					// Time has passed. Add one day
					Log.info(MSCScheduler.class, "Se paso la hora, se a�adera un dia para la proxima Ejecucion");
					userCal.add(Calendar.DAY_OF_MONTH, 1);
				}
				// Set the time object
				time = userCal.getTime();	
			}
		
			/**
				Si la Hora ya paso, se agenda sumandole a la hora el period,
				Ejemplos
					Hora de Ejecucion 10:10:00
					Periodo 10m
					
					Hora Actual
						11:43:00
					Hora Reprogramada sera 
						11:50:00
				*/ 

			// Calcula la cantidad de Milisegundos a agregar
            int add = (int) MSCSchedulerUtil.parseTime(taskInfo.getPeriod());
			
			if (taskInfo.getRunForced()!=null &&  taskInfo.getRunForced().equalsIgnoreCase("true"))	{

				// Agrega la cantida de Milisegundos al Calendar Programado hasta que sea mayor que el de sistema	
				while (userCal.getTimeInMillis() < sysCal.getTimeInMillis()){
					userCal.add(Calendar.MILLISECOND, add  );
				}
				time = userCal.getTime();
			}
		}
		
		
        
		/*
		 * Execute the proper Timer method call based on arguments in taskInfo
		 * If not method excactly match the number and type of supplied 
		 * arguments, we throw an error.
		 */
		Log.info( MSCScheduler.class, "Time " + time);
		Log.info( MSCScheduler.class, "Period " + taskInfo.getPeriod());
		Log.info( MSCScheduler.class, "delay " + taskInfo.getDelay());
		
		
		if (time != null && taskInfo.getPeriod() == null && taskInfo.getDelay() == null) 
			{
				timer.schedule(task, time);
			} 
		else if (time != null && taskInfo.getPeriod() != null && taskInfo.getDelay() == null) 
			{
				timer.schedule(task, time, MSCSchedulerUtil.parseTime(taskInfo.getPeriod()));
			} 
		else if (time == null && taskInfo.getPeriod() == null&& taskInfo.getDelay() != null) 
			{
				timer.schedule(task, MSCSchedulerUtil.parseTime(taskInfo.getDelay()));
			} 
		else if (time == null && taskInfo.getPeriod() != null 	&& taskInfo.getDelay() != null) 
			{
				timer.schedule(task, MSCSchedulerUtil.parseTime(taskInfo.getDelay()),MSCSchedulerUtil.parseTime(taskInfo.getPeriod()));
			} 
		else
			{
				throw new RuntimeException(
					 "ERROR in SchedulerServlet.launchTimer(). "
					+ "No Timer.schedule() method could be found matching "
					+ "supplied arguments. ");
			}
		
		return timer;
	}
	
	/*
	 * Debug Print Utility
	 * */	




	/**
	* Detiene todas las Tareas. Si la tarea se esta ejecutando en el momento de la llamada deja que termine
	*/
	 public void stopTasks(){
		 Log.info( MSCScheduler.class, " Canceling All Task " );
		 Iterator keySet = TimerHash.keySet().iterator();
	     
		 LinkedList listaId = new LinkedList();
		 while (keySet.hasNext()){
			 listaId.add((String)keySet.next());
	     }
		 for (int i = 0; i < listaId.size(); i++) {
			 
			 cancelTask((String)listaId.get(i));
		 }

	}

}
