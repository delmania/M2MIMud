 package m2mimud.state;

 /** 
 * The interface for the listener who is notified when time
 * goes off.
 * @author Robert Whitcomb
 * @version $Id: TimeListener.java,v 1.2 2005/01/06 17:38:24 rjw2183 Exp $
 */
 public interface TimeListener
 {
 	/**
	 * Informs the listener that the time of day has changed
	 */
	public void timeChanged( int newTime );
 }
