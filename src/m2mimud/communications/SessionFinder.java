 package m2mimud.communications; 
 import edu.rit.m2mi.M2MI;
 import edu.rit.util.Timer;
 import edu.rit.util.TimerThread;
 import edu.rit.util.TimerTask;
 import java.util.Vector;
 import java.util.HashMap;
 import java.util.Iterator;

 /**
  * The session finder object is responsible for listening to and reporting
  * when games are found and when they leave or are changed
  *
  * @author Robert Whitcomb
  * @version $Id: SessionFinder.java,v 1.5 2005/01/06 14:01:48 rjw2183 Exp rjw2183 $
  */
 public class SessionFinder
 implements GameDiscovery
 {
 
 	// This is a hash map that goes from the session's multihandle to the
	// the record of that session.  The record is a private class
	private HashMap sessionMap;
	
	// The listener which is updated whenever soemthing happens
 	private GameDiscoveryListener listener;
	
	/**
	 * The info class is one which stores data about the session
	 */
	 private class InfoRecord
	 {
	 	
		// The ad for the session
		public SessionAd mySession;
		
		// Lease timer used to ensure that sessions which no longer
		// exist are removed
		public Timer leaseTimer;
		
		/**
		 * Constructor
		 * @param theSession The SessoinAd object that contains
		 * the state for the session this record holds information
		 * about.
		 */
		public InfoRecord( SessionAd theSession )
		{
			mySession = theSession;
			leaseTimer = TimerThread.getDefault().createTimer
			( new LeaseTimerTask( theSession.sessionHandle ) );
		}
	}
	
	/**
	 * This private class is used to remove session which have timed out.
	 */
	 private class LeaseTimerTask
	 implements TimerTask
	 {
	 	// Session multihandle
		private Game handle;
		
		public LeaseTimerTask( Game handle )
		{
			this.handle = handle;
		}
		
		// If the timer has run out, remove the session
		public void action( Timer theTimer )
		{
			leaseTimeout( theTimer, handle );
		}
	 }
	 
	 /**
	  * Constructor: initialize the local variables and export the object
	  * @param listener The listener for ths object, which is notifed when things happen
	  */
	 public SessionFinder(  GameDiscoveryListener listener )
	 {
	 	sessionMap = new HashMap();
		this.listener = listener; 		
	 }
	
	/**
	 * The report function, which reports when a session enters or its name hasbeen changed
	 */
	 public synchronized void report( SessionAd theAd )
	 {		
		// Look up to see if the record exists
		InfoRecord data = (InfoRecord)sessionMap.get( theAd.sessionHandle );		
		// It doesn't, create a new record
		if( data == null )
		{
			data = new InfoRecord( theAd );
			sessionMap.put( theAd.sessionHandle, data );
			listener.newSessionAdded( theAd ); 
			
		}
		else
		{			
			// It does, check to see if the count or state has changed.
			// If so, then update it.			
			
			if( data.mySession.sessionCount != theAd.sessionCount ) 
			{
				listener.sessionCountChanged( data.mySession.sessionHandle, theAd.sessionCount );
				data.mySession.sessionCount = theAd.sessionCount;
			}
			
			if( !data.mySession.sessionState.equals( theAd.sessionState ) )
			{
				listener.sessionStateChanged( data.mySession.sessionHandle, theAd.sessionState );
				data.mySession.sessionState = theAd.sessionState;
			}
		}
		data.leaseTimer.start( 30000 );
		
	 }
	 
	 /**
	  * Removes the record from the hashmap, since a session has timed out
	  * @param theTimer The timer which  timed out
	  * @param handle Multihandle for the session which timed out
	  */
	  
	  private synchronized void leaseTimeout( Timer theTimer, Game handle )
	  {
	  	if( theTimer.isTriggered() )
		{
			InfoRecord data = (InfoRecord)sessionMap.remove( handle );
	  		if( data != null )			
				listener.sessionLeft( data.mySession.sessionHandle );			
		}
	 
	 }
	 
	 /**
	  * Purges the data held in this sessionf finder object
	  */
	 public void purge()
	 {
	 	sessionMap.clear();
	 }
	
	 /** 
	  * Exports this object to the M2MI layer
	  */ 
	 public void export()
	 {
	 	M2MI.export( this, GameDiscovery.class );
	 }
	 
	 /**
	  * Removes tis object from the M2MI layer
	  */
	 public void unexport()
	 {
	 	M2MI.unexport( this );
	 }
}	

 
