package b2b.util.scheduler;
    
    
import java.util.Hashtable;
import org.w3c.dom.NamedNodeMap;
    

import electric.xml.Document;
import electric.xml.Element;
import electric.xml.Node;
import electric.xml.XPath;
    

/**
 * @author ncv
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MSCTasks 

	{
		
		public Hashtable getTaskList(String strXML) throws Exception
		{
			XPath xpath2 =null;
			Hashtable vcReturn =new Hashtable();

			Document doc = new Document(strXML);
			
			Element el = null;
			Node nod = null;
			NamedNodeMap listAtt = null;
			
			
			
			xpath2 = new XPath("/scheduler/task");
			Element elRoot= doc.getRoot();
			int intCantTask = elRoot.getElements(xpath2).size();
			Log.info( this.getClass(), "Found Task's:" + intCantTask);
			
				
					for (int i=1; i <= intCantTask; i++)
						{
						
							xpath2 = new XPath  ("/scheduler/task[" + i + "]");
							Log.info( this.getClass(), " TASK [" +i + "]");
							el = (Element)doc.getElement(xpath2 );
							
							if (el!=null)
								{
									 listAtt = el.getAttributes();
									MSCTaskInfo tInfo = new MSCTaskInfo();
									for (int j=0; j< listAtt.getLength();j++)
										{

											nod = (Node) listAtt.item(j);
											Log.info( this.getClass(), "  - Attribute:[" + nod.getNodeName() + " = "+ el.getAttribute(nod.getNodeName())  + "]" );	
											tInfo.setPamameter(nod.getNodeName(),el.getAttribute(nod.getNodeName()));
											
										}
									xpath2 = new XPath ("/scheduler/task[" + i + "]/task-param");
									int intCantParam = elRoot.getElements(xpath2).size();
									
									Log.info( this.getClass(), "  - Parameters:[" + intCantParam + "]");

									for (int f =1; f <= intCantParam; f ++)
										{
												
										xpath2 = new XPath  ( "/scheduler/task[" + i + "]/task-param[" + f  +"]/param-name");
										el = (Element)doc.getElement(xpath2);		
										String strName = el.getFirstChild().getNodeValue();
										
										xpath2 = new XPath  ("/scheduler/task[" + i + "]/task-param[" + f + "]/param-value");
										el = (Element)doc.getElement(xpath2);		
										el.getFirstChild().getNodeValue();
										String strValue = el.getFirstChild().getNodeValue();
										
										Log.info( this.getClass(), "    - " + strName + " = "+ strValue);
										tInfo.setPamameter(strName,strValue);
												
									}
						
									vcReturn.put(tInfo.getName(),tInfo);
									tInfo=null;
									
								}
						}
				
						
			return vcReturn;
			
		
			
	}

	}