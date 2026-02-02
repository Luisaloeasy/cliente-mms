package com.megasoft.merchant.mms.configuracion;

import java.util.*;

import com.megasoft.merchant.mms.configuracion.exception.*;
 
/**
 * 
 * @author Christian De Sousa
 *
 */
public class FachadaConf {

	public static LinkedHashMap cargarConfEntidadDeGeneral(String pathArch, String entidad) throws EntityConfigurationLoadException{
		return CargarConf.cargarConfGeneral(pathArch, entidad);
	}
	

	public static LinkedHashMap cargarEntidad(String directorio, String nomArchivo) throws EntityConfigurationLoadException{
		String pathArchivo;
		if (directorio.equals("") || directorio.equals("./"))
			pathArchivo = nomArchivo;
		else
			pathArchivo = directorio+"/"+nomArchivo;
		
		return CargarConf.cargarConfEntidad(pathArchivo);
	}
	
	public static void guardarConfGeneral(LinkedHashMap elementosConf, String directorio, String nomArchivo) throws EntityConfigurationSaveException, FileNotFoundMSConfException {
		String pathArchivo;
		if (directorio.equals("") || directorio.equals("./"))
			pathArchivo = nomArchivo;
		else
			pathArchivo = directorio+"/"+nomArchivo;
		
		GuardarConf.guardarConfiguracionEntidadXML(elementosConf, pathArchivo);
		
	}

}