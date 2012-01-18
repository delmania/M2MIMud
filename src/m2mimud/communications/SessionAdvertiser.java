 package m2mimud.communications;
 import edu.rit.util.Timer;
 import edu.rit.util.TimerTask;
 import edu.rit.util.TimerThread;
 import edu.rit.m2mi.M2MI;
 import java.util.Random;

 /**
  * The session advertiser is responsible for advertising out the session's
  * existence to all listening game units.  All members of the sessions use
  * this class to advertise the session they are in.  
  * 
  * @author Robert Whitcomb
  * @version $Id: SessionAdvertiser.java,v 1.6 2005/01/06 14:01:48 rjw2183 Exp rjw2183 $
  * 
  */

 public abstract class SessionAdvertiser
 implements DiscoverableGame
 {
 	// Timer for doing report() calls
	protected Timer reportTimer;
	
	// PRNG for choosing report intervals
	protected Random intervalPRNG;
	
	// The omnihandle for all other games
	protected GameDiscovery allGameUnits;
	
	/**
	 * This private class is used to repeatedly invoke the report()
	 * function on all other games in the world
	 */
	private class ReportTimerTask
	implements TimerTask
	{
		/**
		 * Constructor
		 */
		public ReportTimerTask()
		{
		}
		
		public void action( Timer theTimer )
		{
			invokeReport( theTimer );
		}
	}
	
	/** 
	 * Constructor
	 */
	public SessionAdvertiser()
	{
		// Initialize all the private data members and export this object to the M2MI layer				
		allGameUnits = (GameDiscovery)M2MI.getOmnihandle( GameDiscovery.class );		
		intervalPRNG = new Random();
		reportTimer = TimerThread.getDefault().createTimer( new ReportTimerTask() );		
	}
			
	/**
	 * Stops the timer from reporting
	 */
	public synchronized void stopReporting()
	{
		reportTimer.stop();
	}

	/**
	 * Reports the existence of a sessions.
	 * For the purposes of this class, this causes the report timer to reset itself so it doesn't
	 * flood the network.
	 * @param theAd The SessionAd object of a session being played.
	 */
	public abstract void report( SessionAd theAd );
	  
	  
	/** 
	 * The request function causes the unit to speed up it advertisements.
	 */
	public void request()
	{
		scheduleFastReport();
	}
	
	
	/** 
	 * Call report on everyone.
	 */
	protected abstract void invokeReport( Timer theTimer );	   
	   
	   
	/**
	 * Schedule a normal timed report
	 */
	protected void scheduleNormalReport()
	{
		// These go off every 5 minutes.
		restartTimer( 60000 );
	}
	    
	/**
	 * Schedule a fast report
	 */	     	
	protected void scheduleFastReport()
	{
		// These go off after 10 seconds
		restartTimer( 10000 );
	}
	     
	/**
	 * Schedule an emergency report
	 */
	protected void scheduleEmergencyReport()
	{		
		int delay = intervalPRNG.nextInt( 10 );
		restartTimer( delay * 1000 );
	}
	     
	/**
	 * This causes the report timer to be restarted using
	 * the specificed interval	
	 * @param theInterval the interval to use.
	 */
	private void restartTimer( long theInterval )
	{
		reportTimer.stop();
		int randomTime = intervalPRNG.nextInt( 10000 );
		reportTimer.start( theInterval + randomTime );
	}	     	   
 }
 
