/**
 * @author Gabriell Calatrava 
 *
 * Scheduler Task Caller
 */
package b2b.util.scheduler;


import java.util.*;



/**
 * A task that can be scheduled for one-time or repeated execution by a
 * <code>Scheduler</code>.   The inherited run() method is called by a
 * <code>Timer</code> and must be overridden.
 * 
 * @author Gabriell Calatrava
 */
public abstract class MSCSchedulerTask extends TimerTask {

    /** Parameters from scheduler configuration file */
    private Map initParameters = new Hashtable();
    
    
    
    /**
     * Constructor
     */
    public MSCSchedulerTask() {
        super();
    }

    /**
     * Gets parameter value from scheduler configuration file, matching 
     * supplied parameter name.
     * @param paramName name of parameter to get
     * @return parameter value
     * @since 1.1
     */
    public String getInitParameter(String paramName) {
        return (String) initParameters.get(paramName);
    }
    
    /**
     * Gets all parameter names defined in scheduler configuration file.
     * @return parameter names
     * @since 1.1
     */
    public Enumeration getInitParameterNames() {
        return ((Hashtable) initParameters).keys();
    }
    
    /**
     * Sets initialization parameters for this scheduler task.
     * @param initParameters initialization parameters
     * @since 1.1
     */
    protected void setInitParameters(Hashtable initParameters) 
    {
            this.initParameters=initParameters;
		
    }
    

	/**
	 * Get parameters for this scheduler task.
	 * @since 1.1
	 */
    public Map getInitParameters()
    {
    	return initParameters; 
    }

}