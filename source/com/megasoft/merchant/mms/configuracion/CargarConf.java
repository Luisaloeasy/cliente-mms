package com.megasoft.merchant.mms.configuracion;

import com.megasoft.merchant.mms.configuracion.exception.EntityConfigurationLoadException;
import electric.xml.Document;
import electric.xml.Element;
import electric.xml.Elements;
import electric.xml.ParseException;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.LinkedHashMap;

/**
 * 
 * @author Christian De Sousa
 *
 */
public class CargarConf {
	
	static Logger logger = Logger.getLogger(CargarConf.class.getName());

	/**
	 * Carga Directamente la configuracion de la entidad pedida
	 * @throws EntityConfigurationLoadException 
	 * 
	 * */
	public static LinkedHashMap cargarConfGeneral(String pathArch, String entidad) throws EntityConfigurationLoadException {
		if (logger.isDebugEnabled()) {
			logger.debug("Cargando configuracion del archivo " + pathArch + " entidad " + entidad);
		}
		
		LinkedHashMap elementosConf = new LinkedHashMap();
		electric.xml.Document doc;

		try {
			doc = new Document(new File(pathArch));
			if (logger.isDebugEnabled())
				logger.debug("Cargo el archivo "+pathArch);

		} catch (ParseException e) {
			logger.error("El archivo "+pathArch+" no posee un patron xml");
			throw new EntityConfigurationLoadException("El archivo "+pathArch+" no posee un patron xml");
		}
		
		Element elemBase = doc.getRoot();
		electric.xml.Elements entidades = elemBase.getElements();
				
		for (int i = 0; i < entidades.size(); i++) {
			Element entidadTemp = entidades.next();
			String nombre = entidadTemp.getElement("name").getFirstChild().getNodeValue();
			if (entidad.equals(nombre)){
				String fuenteConf = entidadTemp.getElement("confsource").getFirstChild().getNodeValue();
				
				if (logger.isDebugEnabled())
					logger.debug("Cargando configuracion del archivo "+pathArch+
									" entidad "+entidad+" de la fuente "+fuenteConf);
				
				elementosConf = cargarFuente(fuenteConf, entidadTemp);				
				
				if (logger.isDebugEnabled())
					logger.debug("Cargo satisfactoriamente configuracion del archivo "+pathArch+
									" entidad "+entidad+" de la fuente "+fuenteConf);
				
				break;			
			}			
		}
		if (logger.isDebugEnabled())
			logger.debug("Cargo satisfactoriamente la configuracion del archivo "+pathArch+" entidad "+entidad);
		
		return elementosConf;
	}
	
	public static LinkedHashMap cargarConfEntidad(String pathArch) throws EntityConfigurationLoadException{
		return obtenerConfXML(pathArch);
	}
	
	private static LinkedHashMap cargarFuente(String fuente, Element entidad) throws EntityConfigurationLoadException {
		if (logger.isDebugEnabled())
			logger.debug("Cargando configuracion de la entidad "+entidad.getName()+" de la fuente "+fuente);
		
		LinkedHashMap arbolConfiguracion = new LinkedHashMap();
		if (fuente.equals("Database")){
			Elements parametros = entidad.getElement("params").getElements();
			LinkedHashMap confBD = new LinkedHashMap();
			
			for (int j = 0; j < parametros.size(); j++) {
				Element prop = parametros.next();
				Element paramValueElement = prop.getElement("param-value");
				String paramValue = (paramValueElement.getFirstChild() != null)
						? paramValueElement.getFirstChild().getNodeValue()
						: "";
				confBD.put(
						prop.getElement("param-name").getFirstChild().getNodeValue(),
						paramValue);
			}

		}else if(fuente.equals("XML")){
			Elements parametros = entidad.getElement("params").getElements();
			for (int j = 0; j < parametros.size(); j++) {
				Element prop = parametros.next();
				Element paramValueElement = prop.getElement("param-value");
				String paramValue = (paramValueElement.getFirstChild() != null)
						? paramValueElement.getFirstChild().getNodeValue()
						: "";
				if (prop.getElement("param-name").getFirstChild().getNodeValue().equals("File-Path")){
					arbolConfiguracion = obtenerConfXML(paramValue);
					arbolConfiguracion.put("File-Path",paramValue);
				}
				
			}
		} else if (fuente.equals("Directo")){
			Elements parametros = entidad.getElement("params").getElements();
			for (int j = 0; j < parametros.size(); j++) {
				Element prop = parametros.next();
				Element paramValueElement = prop.getElement("param-value");
				String paramValue = (paramValueElement.getFirstChild() != null)
						? paramValueElement.getFirstChild().getNodeValue()
						: "";
				arbolConfiguracion.put(
						prop.getElement("param-name").getFirstChild().getNodeValue(),
						paramValue);
			}
		}
		else {
			logger.error("Tipo de Carga de configuracion no soportado "+fuente);
			throw new EntityConfigurationLoadException("Tipo de Carga de configuracion no soportado "+fuente);
		}
		arbolConfiguracion.put("confsource",fuente);
		
		if (logger.isDebugEnabled())
			logger.debug("Cargo exitosamente la configuracion de la entidad "+entidad.getName()+" desde la fuente "+fuente);
		return arbolConfiguracion;	
	}
	
	private static LinkedHashMap obtenerConfXML(String pathArch) throws EntityConfigurationLoadException {
		LinkedHashMap elementosConfTemp = new LinkedHashMap();
		electric.xml.Document doc;
		try {
			doc = new Document(new File(pathArch));
		} catch (ParseException e) {
			logger.error("El archivo "+pathArch+" no posee un patron xml o no existe");
			throw new EntityConfigurationLoadException("El archivo "+pathArch+" no posee un patron xml");
		}
		
		Element elemBase = doc.getRoot();
		if (elemBase.getElement("params") == null){
			throw new EntityConfigurationLoadException("El archivo no posee el formato esperado ", new Exception().fillInStackTrace());
		}
		
		Elements propiedades = elemBase.getElement("params").getElements();
				
		for (int i = 0; i < propiedades.size(); i++) {
			Element prop = propiedades.next();
			Element paramValueElement = prop.getElement("param-value");
			String paramValue = (paramValueElement.getFirstChild() != null)
					? paramValueElement.getFirstChild().getNodeValue()
					: "";
			elementosConfTemp.put(
					prop.getElement("param-name").getFirstChild().getNodeValue(),
					paramValue);
		}
		return elementosConfTemp;
	}

}
