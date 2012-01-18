 package m2mimud.game;
 import edu.rit.util.Timer;
 import edu.rit.util.TimerTask;
 import edu.rit.util.TimerThread;
 import java.util.Random;
 import edu.rit.m2mi.Eoid;
 import m2mimud.communications.Game;

 /** 
  * The PlayerRefresher object is one which serves as
  * the heartbeat of the player.  It will periodically boradcast out 
  * a message essentially stating that "yes, my player is still here."
  * One could argue that there are several ways that this could be done.
  * For example, every action the player takes could refresh the timer.  
  * The problem is that certain actions, like combat, are not broadcasted to
  * everyone.  So, if 2 people are fighting for more than 5 minutes they will
  * both be disconnected.  One could also make the case that these message should
  * be bundled in with te state broadcasts.  Again, this can not
  * be the case since there isn no guarantee that the system will transmit 
  * it's broadcast before the timeout occurs.  So, in esscence, this is probably
  * the best idea.  
  * 
  * @author Robert Whitcomb
  * @version $Id: PlayerRefresher.java,v 1.4 2005/01/12 14:03:17 rjw2183 Exp $
  */

 public class PlayerRefresher
 {
 	private static int TWO_MINUTES = 12000;
	private Game sessionHandle; // the handle for the session
	private Eoid myPlayerId; // the Id of the player
	private Timer reportTimer; // the timer to use to broadcast
	private Random delayPRNG; // used to randomize whenborad cast are done
	private int myPartNum; // the number of the partition.
	
	private class ReportTimerTask
	implements TimerTask
	{
		public ReportTimerTask()
		{
		}
		
		public void action( Timer theTimer )
		{
			if( theTimer.isTriggered() && sessionHandle != null )			
				sessionHandle.refreshPlayer( myPlayerId, myPartNum );			
		}
	}
	
	/**
	 * Constructor
	 * @param playerId The id of the player
	 * @param sessHand the handle of the session to broadcast to.
	 */
	public PlayerRefresher( Eoid playerId, Game sessHand )
	{
		sessionHandle = sessHand;
		myPlayerId = playerId;
		delayPRNG = new Random();
		reportTimer = TimerThread.getDefault().createTimer( new ReportTimerTask() );
		myPartNum = 0;
	}
	
	/**
	 * Starts the report timer
	 */
	public void start()
	{
		reportTimer.startFixedIntervalTimeout( 0, TWO_MINUTES + delayPRNG.nextInt( TWO_MINUTES ) );
	}
	
	/** 
	 * Stops the report timer
	 */
	public void stop()
	{
		reportTimer.stop();
	}
	
	/**
	 * Sets the partition number
	 * @param partNum  The partition number
	 */
        public void setPartition( int partNum )
	{
	 	myPartNum = partNum;
	}	 
 }
