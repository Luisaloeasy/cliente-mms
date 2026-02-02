package b2b.util.scheduler;



import java.util.HashMap;
import java.util.Map;


/**
 * Utility methods used by this scheduler library.
 * @author Pascal Essiembre
 * @since 1.1
 */
public final class MSCSchedulerUtil {

    /** Number of milliseconds in a second */
    public final static long SECOND = 1000L;
    /** Number of milliseconds in a minute */
    public final static long MINUTE = 60 * SECOND;
    /** Number of milliseconds in a hour */
    public final static long HOUR = 60 * MINUTE;
    /** Number of milliseconds in a day */
    public final static long DAY = 24 * HOUR;
    /** Map between time unit type and milliseconds */
    public final static Map TIME_UNIT_MAP = new HashMap();
    static {
        TIME_UNIT_MAP.put(new Character('s'), new Long(SECOND));
        TIME_UNIT_MAP.put(new Character('m'), new Long(MINUTE));
        TIME_UNIT_MAP.put(new Character('h'), new Long(HOUR));
        TIME_UNIT_MAP.put(new Character('d'), new Long(DAY));
    }
    
    /**
     * Constructor.
     */
	public MSCSchedulerUtil() 
	{
	
    	super();
    }

    /**
     * Parses a <code>String</code> represntation of time period or delay,
     * in milliseconds.
     * If the time only contains digits, it is simply converted to a long.
     * However, the value can be made of one or several numbers followed with
     * an alpha character representing a time unit, all merged together.  The
     * supported time units are:
     * <ul>
     *   <li><strong>d</strong>: a day
     *   <li><strong>h</strong>: an hour
     *   <li><strong>m</strong>: a minute
     *   <li><strong>s</strong>: a second
     * </ul>
     * No time unit for a number always means milliseconds.  Some examples are:
     * <blockquote>
     *   <strong>1d</strong>: 1 day (or 86,400,000 milliseconds)<br>
     *   <strong>2h30m</strong>: 2 hours and 30 minutes<br>
     *   <strong>30s500</strong>: 30 seconds and 500 milliseconds<br>
     * </blockquote>
     * @param time period or delay to parse
     * @return a <code>long</code> representation of a delay or period
     */
    public static long parseTime(String time){

        long longTime = 0L;
        StringBuffer number = new StringBuffer("0");
        for(int i = 0; i < time.length(); i++) {
            char ch = time.charAt(i);
            if (Character.isDigit(ch)) {
                number.append(ch);
            } else {
                Character unitType = new Character(ch);
                if (TIME_UNIT_MAP.containsKey(unitType)) {
                    longTime += Long.parseLong(number.toString())
                             * ((Long) TIME_UNIT_MAP.get(unitType)).longValue(); 
                    number = new StringBuffer("0");
                } else {
                     throw new NumberFormatException(
                            "\"" + ch + "\" is not a valid time unit type.");
                }
            }
        }
        // Add remaining milliseconds, if any
        longTime += Long.parseLong(number.toString());
        return longTime;
    }
    public static Long getUnit(String time){

        //long longTime = 0L;
        //StringBuffer number = new StringBuffer("0");
        for(int i = 0; i < time.length(); i++) {
            char ch = time.charAt(i);
            if (Character.isDigit(ch)) {
                //number.append(ch);
            } 
            else {
                
            	Character unitType = new Character(ch);
                
                if (TIME_UNIT_MAP.containsKey(unitType)){
                	
                	return (Long)TIME_UNIT_MAP.get(unitType); 
                } 
                else{
                	throw new NumberFormatException("\"" + ch + "\" is not a valid time unit type.");
                }
            }
        }
        return new Long(0);
    }
}
