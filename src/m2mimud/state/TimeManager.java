 package m2mimud.state;
 import edu.rit.util.Timer;
 import edu.rit.util.TimerTask;
 import edu.rit.util.TimerThread;
 import java.util.Random;

 /**
  * The time manager class is responsible for keeping track of 
  * the game time.  It maintains the current time and informs
  * its listener when time has passed.
  * 
  * @author Robert Whitcomb
  * @version $Id: TimeManager.java,v 1.4 2005/01/06 17:38:24 rjw2183 Exp $
  */
  
 public class TimeManager
 {
 	/**
	 * Intege codes for the time of day
	 */
	public static final int EARLY_MORNING = 0;
	public static final int MID_MORNING = 1;
	public static final int EARLY_AFTERNOON = 2;
	public static final int LATE_AFTERNOON = 3;
	public static final int EARLY_EVENING = 4;
	public static final int EVENING = 5;
	
	private int myTime; // the current time
	private Random timerPRNG; // PRNG used to reduce the chance that 2 timers for off at once
	private Timer reportTimer; // timer used to change times
	private TimeListener listener; // the listener who is notified when times change
	
	/** 
	 * Private class that holds the timer that counts down
	 * to when the time changes
	 */
	private class ReportTimerTask
	implements TimerTask
	{
		public ReportTimerTask()
		{
		}
		
		public void action( Timer theTimer )
		{
			changeTimes( theTimer );
		}
	}
	
	/**
	 * Constructor
	 * @param theListener The listener who is notifed when time changes
	 */
	public TimeManager( TimeListener theListener )
	{
		timerPRNG = new Random();
		myTime = EARLY_MORNING;
		reportTimer = TimerThread.getDefault().createTimer( new ReportTimerTask() );
		listener = theListener;
	}
	
	/**
	 * Returns the current time.
	 */
	public int getTime()
	{
		return myTime;
	}
	
	/** 
	 * Starts the timer.
	 */
	public void start()
	{
		reportTimer.start( 300000 + timerPRNG.nextInt( 30000 ) );
	}
	
	/**
	 * Synchronizes this time manager by setting its time
	 * to the specified time and restarting its timer
	 * @param newTime The new time to set to.
	 */
	public void synchronize( int newTime )
	{
		if( 0 <= newTime && newTime <= 5 )
			myTime = newTime;
		start();
	}
	/**
	 * Private function which happens when the timer goes off.
	 * @param theTimer The timer which went off.
	 */
	private void changeTimes( Timer theTimer )
	{
		if( theTimer.isTriggered() && listener != null )
		{
			myTime++;
			if( myTime > 5 )
				myTime = 0;
			listener.timeChanged( myTime );
		}
	}
	
	/** 
	 * When then the time of day changes, this function is called
	 * to get a string that informs the user of that event
	 */
	public String getTimePassageString( int theTime )
	{
		String retVal = null;
		switch( theTime )
		{
			case EARLY_MORNING:
				retVal = "The sun begins to rise on the horizons, " +	
				"it's early rays piercing the morning mists.";
			break;
			case MID_MORNING:			
				retVal = "Having burned the few remaining traces of the night's mist, " +
				"the sun continues its journey upwards.";
			break;
			case EARLY_AFTERNOON:
				retVal = "The sun has risen to its highest point in the sky, signaling " +
				"that the day is half over.";
			break;
			case LATE_AFTERNOON:
				retVal = "The sun begins to set in the sky.";
			break;
			case EARLY_EVENING:
				retVal = "The sun begins to dip below the horizon, and the shadows around you " +
				"grow longer.";
			break;
			case EVENING:
				retVal = "With the sun now gone below the horizon, the sky appear in the sky " +
				"and merrily twinkle as the moon begins its rounds.";
			break;
		}
		return retVal;
	}
}	

