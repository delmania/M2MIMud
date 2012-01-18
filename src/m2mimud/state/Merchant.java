 package m2mimud.state;
 import edu.rit.util.Timer;
 import edu.rit.util.TimerTask;
 import edu.rit.util.TimerThread;
 import java.io.*;
 import java.util.Random;
 import java.util.Vector;
 import java.util.Iterator;

 /**
  * The merchant class is really the manager for the merchant.  Basically, 
  * this keeps track of the timer which is used to periodically broadcast
  * out a message to the players which advertises to the player to check 
  * the merchants wares.  This merchant is non consigment type merchant, 
  * meaning they only work themselves and all the money they keep is 
  * their own.  While merchants do not mind other merchants, they do 
  * not like them to be nearby, so there can only be one merchant per room.
  * Merchants also have a warding effect on their area, so that no MOB will
  * enter a room where a merchant is.  There are 3 types of merchants: armor, 
  * weapon, and items.  To prevent players from setting up a world with no 
  * MOBs, there can be a max of 3 types of each merchant in the world.  Because
  * of this, when state healing and joining, the incoming ad's merchants take
  * precedence over any merchants the game has locally.
  * 
  * @author Robert Whitcomb
  *
  * @version $Id: Merchant.java,v 1.3 2005/01/06 17:38:24 rjw2183 Exp $
  */

 public class Merchant
 implements Externalizable
 {
 	// Mechant Types
	public static final int ARMOR = 0;
	public static  final int WEAPONS = 1;
	public static  final int ITEM  = 2;
	
	//  Private data members
	private int myType; // the type of merchant this is
	private XYloc myLoc; // the location of this merchant
	private Random intervalPRNG; // a prng used to randomize the merchant's spam
	private Timer broadcastTimer; // the timer used to determine when the merchant
			              // spams his message
	private MerchantListener myListener; // the listener for the merchant		
	
	/** 
	 * Private class that holds the broadcast timer
	 */
	private class ReportTimerTask
	implements TimerTask
	{
		public ReportTimerTask()
		{
		}
		
		public void action( Timer theTimer )
		{
			broadcastMerchantMessage( theTimer );
		}
	}
	
	/**
	 * Constructor
	 * @param type The type of merchant this is
	 * @param theLoc The location of the merchant
	 * @param theListener The listener who is waiting for notification for when the merchant spams
	 */
	public Merchant( int type, XYloc theLoc, MerchantListener theListener ) 				      
	{
		myType = type;
		myLoc = theLoc;
		myListener = theListener;
		intervalPRNG = new Random();
		broadcastTimer = TimerThread.getDefault().createTimer( new ReportTimerTask() );				
	}
	
	/**
	 * Constructor for externalizable
	 */
	public Merchant()
	{
		myLoc = new XYloc( 0, 0 );
		intervalPRNG = new Random();		
		broadcastTimer = TimerThread.getDefault().createTimer( new ReportTimerTask() );
	}
	/** 
	 * Schedule the next merchant message.
	 */
	public void scheduleNextMessage()
	{				
		broadcastTimer.start( 60000 + intervalPRNG.nextInt( 30000 ) );	
	}
	
	/**
	 * Sets the listener
	 * @param listener The listener for this merchant
	 */
	public void setListener( MerchantListener listener )
	{
		myListener = listener;
	}
	
	/**
	 * Returns the location of this merchant
	 */
	public XYloc getLocation()
	{
		return myLoc;
	}
	
	/**
	 * Returns the type of the merchant
	 */
	public int getType()
	{
		return myType;
	}
	
	/**
	 * Private function which is used to tell the listener that
	 * the merchant's time to broadcast out a message
	 * @param theTimer The timer that went off.
	 */
	private void broadcastMerchantMessage( Timer theTimer )
	{		
		if( theTimer.isTriggered() && myListener != null )
			myListener.sendMessage( myType, myLoc );
	}
	
	/** 
	 * Stops the merchant from boradcasting
	 */
	 public void stop()
	 {
	 	broadcastTimer.stop();
	 }
	
	 
	/** 
	 * Determines if this merchant object is equal to the given
	 * @param other The other object to compare to
	 */
	public boolean equals( Object other )
	{
		boolean retVal = false;
		if( this.getClass().isInstance( other ) )
		{
			Merchant otherMech = (Merchant)other;
			retVal = ( (myType == otherMech.myType) && (myLoc.equals( otherMech.myLoc ) ) );
		}
		return retVal;
	}
		
      	
	/**
	 * Writes te merchant to output 
	 * @param out The output object to write to.
	 */
	public void writeExternal( ObjectOutput out )
	throws IOException
	{		
		out.writeInt( myType );
		out.writeObject( myLoc );		
	}
	
	/**
	 * Reads a merchant in from input
	 * @param in The input object to read from.
	 */
	public void readExternal( ObjectInput in )
	throws IOException, ClassNotFoundException
	{
		myType = in.readInt();
		myLoc = (XYloc)in.readObject();
	} 
}				     
