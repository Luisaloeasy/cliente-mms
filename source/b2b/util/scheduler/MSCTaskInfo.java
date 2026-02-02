package b2b.util.scheduler;

import java.util.Hashtable;
import java.util.Vector;

/*
 * Created on 10/08/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


/**
 * @author ncv
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MSCTaskInfo 
	

	{
		private Hashtable htParam; 
		private Vector vcParam;
		
		public MSCTaskInfo()
		{
			htParam = new Hashtable();
		}
	
		
		public String getDelay()  
		{
			return (String)htParam.get("delay");
		}

		public void setDelay(String strParam)  
		{
			htParam.put("delay", strParam);
		}

		
		public String getVersion()  
		{
			return (String)htParam.get("version");
		}

		public void setVersion(String strParam)  
		{
			htParam.put("version", strParam);
		}
		
		public String getName()  
		{
			return (String)htParam.get("name");
		}

		public void setName(String strParam)  
		{
			htParam.put("name", strParam);
		}
		
		
		public String getPeriod()  
		{
			return (String)htParam.get("period");
		}

		public void setPeriod(String strParam)  
		{
			htParam.put("period", strParam);
		}

		
		public String getTask() 
		{
			return (String)htParam.get("class");
		}
		public void setTask(String strParam) 
		{
			htParam.put("class", strParam);
		}
		
		
		
		public String getTime() 
		{
			return (String)htParam.get("time");
		}
		
		public void setTime(String strParam)  
		{
			htParam.put("time", strParam);
		}
		
	
		public String  getEnabled()  
		{
			return (String)htParam.get("enabled");
		}
		
		public void setEnabled(String strParam)  
		{
			htParam.put("enabled", strParam);
		}
		
		public Hashtable getParameters()  
		{
			return htParam;
		}

		public void setParameters(Vector vcPara)  
		{

			vcParam= vcPara;
		}

		public void setPamameter(String strKey, String strParam)  
		{
			htParam.put(strKey.toLowerCase(), strParam);
		}
		public String  getPamameter(String strParam)  
		{
			return (String)htParam.get(strParam.toLowerCase());
		}

		public String getRunForced(){
			
			return (String) htParam.get("run-forced");
		}
		
		public void setRunForced(String strParam){
			htParam.put("run-forced", strParam);
			
		}


	}