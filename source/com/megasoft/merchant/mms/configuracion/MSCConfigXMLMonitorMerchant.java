package com.megasoft.merchant.mms.configuracion;

import java.io.FileReader;
import java.io.IOException;

import com.megasoft.merchant.mms.configuracion.exception.EntityConfigurationLoadException;

import b2b.util.scheduler.MSCTaskConfig;

/**
 * 
 * @author Christian De Sousa
 *
 */
public class MSCConfigXMLMonitorMerchant implements MSCTaskConfig 
{
	public String getConfig() {
		try {
			String filePath = FachadaConf.cargarConfEntidadDeGeneral("ConfigFiles/GeneralConfig.xml", "MSCScheduler")
										.get("File-Path").toString();
			return getFileContent(filePath);
		} catch (EntityConfigurationLoadException e) {
			System.err.println("Error in Internal Pluging FILEXMLMonitorMerchant " + e.getMessage());
		} catch (IOException e) {
			System.err.println("Error in Internal Pluging FILEXMLMonitorMerchant " + e.getMessage());
		}
		return null;
	}

	/**
	 * Retorn el Contenido del archivo
	 * @param FileName
	 * @return
	 * @throws IOException 
	 * @throws Exception
	 */
	
	public String getFileContent(String FileName) throws IOException 
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

