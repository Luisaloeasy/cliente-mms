package com.megasoft.merchant.mms.configuracion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

import com.megasoft.merchant.mms.configuracion.exception.*;

/**
 * 
 * @author Christian De Sousa
 *
 */
public class GuardarConf {

	static Logger logger = Logger.getLogger(GuardarConf.class.getName());
	
	public static void guardarConfiguracionEntidadXML(LinkedHashMap elementosConf, 
											String pathArch) 
				throws EntityConfigurationSaveException, FileNotFoundMSConfException {		
		
		String contenido = generarContenidoConfiguracionEntidad(elementosConf);	
		guardarArchivo(contenido, pathArch);
	}
	

	private static String generarContenidoConfiguracionEntidad(LinkedHashMap elementosConf) 
														throws EntityConfigurationSaveException {
		String contenido = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"+
					generarContenidoConfiguracionEntidadNivelado(elementosConf, 0);
		
		return contenido;
	}
	
	private static String generarContenidoConfiguracionEntidadNivelado(LinkedHashMap elementosConf, int nivel){
		Iterator iterador = elementosConf.keySet().iterator();
		String contenido = "";
		String espacio = "";
		for (int i = 0; i < nivel; i++) {
			espacio = espacio + "\t";
		}
		while(iterador.hasNext()){
			String key = (String)iterador.next();
			Object obj = elementosConf.get(key);
			if (obj instanceof LinkedHashMap) {
				contenido = contenido + espacio+"<"+key+">"+"\n"+
										espacio+"\t"+"<params>\n"+
										generarContenidoConfiguracionEntidadNivelado((LinkedHashMap)obj, nivel + 2)+
										espacio+"\t"+"</params>\n"+
										espacio+"</"+key+">\n";
			}else {
				contenido = contenido + espacio+"<param>\n";
				contenido = contenido + espacio+"\t"+"<param-name>"+key+"</param-name>\n";
				contenido = contenido + espacio+"\t"+"<param-value>"+obj.toString()+"</param-value>\n";
				contenido = contenido + espacio+"</param>\n";
			}
		}
		return contenido;
	}
	
	private static void guardarArchivo(String contenido, String pathArch) throws EntityConfigurationSaveException, FileNotFoundMSConfException{		
		if (logger.isDebugEnabled()){
			logger.debug("A guardar en "+pathArch+" contenido: '"+contenido+"'");
		}
		File miFichero;
		FileOutputStream miFicheroSt;
		miFichero = new File(pathArch);
		
	    try {
	    	miFicheroSt = new FileOutputStream( miFichero );
			miFicheroSt.write(contenido.getBytes());
			miFicheroSt.close();
		
			if (logger.isDebugEnabled()){
				logger.debug("Se guardo el archivo "+pathArch);
			}
	    } catch (FileNotFoundException e) {
			logger.error("El archivo "+pathArch+" (para guardar) no fue encontrado", e);
			throw new FileNotFoundMSConfException("El archivo "+pathArch+
										" (para guardar) no fue encontrado",e);
		
		} catch (IOException e) {
			logger.error("No se pudo guardar la configuracion en el archivo "+pathArch,e);
			throw new EntityConfigurationSaveException("No se pudo guardar la configuracion en el archivo "+pathArch,e);
		}  
	}

}
