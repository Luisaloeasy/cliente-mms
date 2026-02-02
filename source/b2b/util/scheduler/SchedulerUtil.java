/*
 * CREADO EL 22/05/2006 POR: ncv
 */
package b2b.util.scheduler;

/**
 * @author Nagel Camacho
 *  @Fecha = 22/05/2006
 **/
public class SchedulerUtil {

	  /**
	    *replace string
	    *@param string: string that contains the text to be replaced
	    *@param from: text to replace
	    *@param to: replacement
	    **/
	    public static String replace(String string, String from, String to) 
	    {
	        if (from.equals("")) 
	            return string;
	        StringBuffer buf = new StringBuffer(2*string.length());

	        int previndex=0;
	        int index=0;
	        int flen = from.length();
	        while (true) { 
	            index = string.indexOf(from, previndex);
	            if (index == -1) {
	                buf.append(string.substring(previndex));
	                break;
	            }
	            buf.append( string.substring(previndex, index) + to );
	            previndex = index + flen;
	        }
	        return buf.toString();
	    }
	    
	    /**
	     * Split a strings delimited by token in several strings 
	     * @param String source 
	     * @param char token
	     * @return String[] array of strings
	     */
	    public static String[] split( String source, char token )
	    {
	            String[] ndest;
	            String[] dest = new String[ 10 ];
	            int i1 = 0, i2 = 0, i = 0; 
	            while ( ( i2 = source.indexOf( token, i1 ) ) != -1 )
	            {
	                    dest[ i ] = source.substring( i1, i2 );
	                    i++;
	                    i1 = ++i2;
	                    if ( i == dest.length )
	                    {
	                            ndest = new String[ i + 10 ];
	                            System.arraycopy( dest, 0, ndest, 0, i );
	                            dest = ndest;
	                    }
	            }
	            dest[ i++ ] = source.substring( i1 );

	            ndest = new String[ i ];
	            System.arraycopy( dest, 0, ndest, 0, i );
	            return ndest;
	    }}
