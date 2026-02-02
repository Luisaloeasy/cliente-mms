/*
 * CREADO EL 16/05/2006 POR: ncv
 */
package b2b.util.scheduler.plugins;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import java.io.*;
import electric.xml.Document;
import electric.xml.Element;
import b2b.util.scheduler.Log;
import b2b.util.scheduler.MSCTaskConfig;

/**
 * @author Nagel Camacho
 *  @Fecha = 16/05/2006
 **/
public class MSCConfigDB implements MSCTaskConfig 

{

	
	Connection con = null;
	Statement st  = null;
	ResultSet rs  = null;
	ResultSet rs2 = null;
	Statement st2 = null;
	ResultSet rs3  = null;
	Statement st3 = null;	

	
	public String getConfig()
	{
		String strReturn = null;
		try 
			{
				strReturn = getDBContent("schedulerDB.properties");
			}
		catch (Exception err)
			{
				Log.fatal(this.getClass(), "Error in Internal Pluging ConfigDB " + err.getMessage(), err);
			}
		finally
			{
				if (rs3!=null)
				try {	rs3.close();}
				catch (SQLException e) {}

				if (rs2!=null)
					try {	rs2.close();}
					catch (SQLException e) {}

				if (rs!=null)
					try {rs.close();}
					catch (SQLException e) {}

				if (st!=null)
					try {st.close();}
					catch (SQLException e) {}
				
				if (st2!=null)
					try {st2.close();}
					catch (SQLException e) {}

				if (st3!=null)
					try {st3.close();}
					catch (SQLException e) {}
					
				if (con!=null)
					try {st.close();}
					catch (SQLException e) {}
						
			}
		return strReturn;
	}

	
	
	public String getDBContent(String FileName) throws Exception
	{
		
		// Crea el Doc xml en Memoria

		Document doc = new Document();
		
		//Agrega el Root
		doc.setRoot("scheduler");
		
		
		// Maneja las Propiedades de la Conexión
		Properties dbProperties = new Properties();
		FileInputStream fin = new FileInputStream(FileName);
		dbProperties.load(fin);
		

		Log.info(this.getClass(), "Property content " + dbProperties.toString());
		
		Class.forName(dbProperties.getProperty("driver"));
		
		
		con = DriverManager.getConnection(dbProperties.getProperty("url"), dbProperties);  
		st = con.createStatement();
		
		// Selecciona todas las tareas con sus tipos de periodad y delay
		String sql= " select id_task, enabled, time," +
					" t.name," +
					" class_name," +
					" t.delay delay, dt.name delay_name," +
					" t.period period , pt.name period_name," +
					" t.version version	" +
					" from " +
					" tasks t, " +
					" delay_types dt, " +
					" period_types pt " +
					" where " +
					" t.id_period_types = pt.id_period_types " +
					" and " +
					" t.id_delay_types = dt.id_delay_types";
		
			rs = st.executeQuery(sql);
		
		// Recorre todos los registros Agendados
		while (rs.next())
			{
				// Crea los elementos <task>
				Element e = doc.getRoot().addElement("task");
				e.setAttribute("name",rs.getString("name"));
				e.setAttribute("class",rs.getString("class_name"));
				e.setAttribute("description",rs.getString("class_name"));
				e.setAttribute("version",rs.getString("version"));
				e.setAttribute("enabled",rs.getString("enabled"));
				e.setAttribute("to",System.currentTimeMillis()+"");
				Thread.sleep(100);
				if (rs.getString("time")==null || rs.getString("time").trim().equals(""))
					{
						e.setAttribute("delay",rs.getString("delay") + rs.getString("delay_name"));
						e.setAttribute("period",rs.getString("period") + rs.getString("period_name"));
					}
				else
					e.setAttribute("time",rs.getString("time"));

				// Le agrega al Elemento los Param si existen
			
				addTaskParam(e, rs.getString("id_task"));
				addWindowTimes(e, rs.getString("id_task"));
			}
				
		
		Log.info(this.getClass(), "XML Generado " +doc.toString());
		
		return doc.toString();
			
	}
		
	/**
	 * Agrega los parametros a cada uno de las Propiedad
	 * @param e Element
	 * @param id_task id de la tarea
	 * @throws Exception
	 */
	public void addTaskParam(Element e, String id_task) throws Exception
		{
			
			// Busca las Proopiedades del task
			st2 = con.createStatement();
			rs2 = st2.executeQuery("select * from task_properties where id_task = '" + id_task +"' and enable = 1");
			
			//Agrega cada una de las Propiedades a los Task que tengan 
			while (rs2.next())
			{
				Element elem = e.addElement("task-param");
				elem.addElement("param-name").setText(rs2.getString("name"));
				elem.addElement("param-value").setText(rs2.getString("value"));
			}
		}
	public void addWindowTimes(Element e, String id_task)  throws Exception
	{
		// Busca las ventanas de tiempo del task
		st3 = con.createStatement();
		rs3 = st3.executeQuery("select * from task_windows_times where id_task = '" + id_task +"' and enable = 1");
		
		
		Document docTimes = new Document();
		Element elTimes = docTimes.setRoot("times");
		
		boolean changed = false;
		//Agrega cada una de las ventanas de tiempo a los Task que tengan 
		while (rs3.next())
		{
			changed = true;
			elTimes.addElement("time").addElement("from").setText(rs3.getString("from")).getParent().addElement("to").setText(rs3.getString("to"));
		}

		rs3.first();
		
		if (changed)
			{
				Element elem = e.addElement("task-param");
				elem.addElement("param-name").setText("times");
				elem.addElement("param-value").setText(docTimes.createCDATASection(docTimes.toString()).getData());
			}
		
		
		
	}

}

