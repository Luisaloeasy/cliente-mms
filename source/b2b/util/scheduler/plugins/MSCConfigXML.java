/*
 * CREADO EL 16/05/2006 POR: ncv
 */
package b2b.util.scheduler.plugins;

import java.io.FileReader;

import b2b.util.scheduler.Log;
import b2b.util.scheduler.MSCTaskConfig;

/**
 * @author Nagel Camacho
 *  @Fecha = 16/05/2006
 **/
public class MSCConfigXML implements MSCTaskConfig 

{

	
	public String getConfig()
	{
		String strReturn = null;
		try
			{
				strReturn = getFileContent("mscscheduler.xml");
			}
		catch (Exception err)
			{
				Log.error(this.getClass(), "Error in Internal Pluging FILEXML ", err);
				
			}
		return strReturn;
	}

	
	
	/**
	 * Retorn el Contenido del archivo
	 * @param FileName
	 * @return
	 * @throws Exception
	 */
	
	public String getFileContent(String FileName) throws Exception
	{
			FileReader fread = new FileReader(FileName);
			StringBuffer strBuf  = new StringBuffer(500);
			
			int c;
		       while((c=fread.read())!=-1){
		           strBuf.append((char)c);
		       }
			
		     fread.close(); 
		     return strBuf.toString();
	}
}

