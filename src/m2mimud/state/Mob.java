 package m2mimud.state;
 import java.io.*;
 import java.lang.Integer;
 import java.util.Random;
 import edu.rit.util.Timer;
 import edu.rit.util.TimerTask;
 import edu.rit.util.TimerThread;
 import edu.rit.m2mi.Eoid;
 import java.util.Date;
 import m2mimud.command.special.MobCommand;

 /**
  * A mob in an online game does  not refer to a large collection of people (which is usually called
  * a zerg).  It is actually shorthand for "mobile enemy" which refers to any moving NPC 
  * which (a) may attack the player ot (b) can be attacked by the player.  It is called mobile
  * because it moves from time to time.  In this case, mobs will move only into rooms which do not have
  * water and do not have a merechant in it.
  * @author Robert Whitcomb
  * @version $Id: Mob.java,v 1.17 2005/01/13 15:47:06 rjw2183 Exp rjw2183 $
  */
 public class Mob
 implements Externalizable
 {
 	private static int TWO_MINUTES = 120000;
	private static int THREE_MINUTES = 180000;
	private MobKey myKey; // the key of this mob	
	private XYloc currentLocation; // the current location of the mob
	private boolean alive; // whether or not the mob is alive
	private Timer movementTimer; // timer used to move the mob
	private Timer attackTimer; // the timer used to perform an attack on its target
	private Timer respawnTimer; // timer used to respawn the mob
	private Timer regenTimer; // a timer used to regen a monster's hit points
	private Random movementPRNG; // prng used to randomize when the mob moves		
	private World myWorld; // a reference to the world this mob is in 	
	private int maxHitPoints; // the total number of hit points a mob can have.
	private long myAttackSpeed; // the attack speed of the mob
	private int currentHitPoints; // the current number of hit points a mob has
	private int myDamage; // the damage the mob causes to targets
	private String  myName; // The name of the mob		
	private boolean underAttack; // a boolean used to indicate if the mob is under attack by something
	private MobListener myListener; // the listener for this mob
	int myGold; // the amount of gold this mob drops
	String myDrop; // the id of the item this mob drops
	int myExp; // the amount of exp this mobs drops
	boolean tempMob; // indicates if this is a temporary mob.
	private Eoid myCurrentTarget; // this is the current target 
	private Date timeStamp; // a timestamp used for combat purposes.
	
	
	/*
	 * This timer is used to move the mob.  Basically,
	 * when it goes off, the mob informs its listener that it needs to be moved,
	 * passing along the current location of the mob.
	 */
	private class MovementTimerTask
	implements TimerTask
	{
		public MovementTimerTask()
		{
		}
		
		public void action( Timer theTimer )
		{
			moveMob( theTimer );
		}
	}
	
	/**
	 * Thisfunction calculates a random direction for the mob to move in.
	 * It them passes this direction to the world to see if a move in that
	 * direction is valud for the mob.  If so, the mob informs the listener
	 * that it needs to be moved.
	 * 
	 * @param  theTimer The movement timer indicated that the mob needs to move
	 * now
	 */
	private void moveMob( Timer theTimer )
	{				
		if( theTimer.isTriggered() && myListener != null )					
		{												
			int newDirection = new Random().nextInt( 4 );		
			MobMove myMove = myWorld.checkMobMove( myKey, newDirection );			
			if( myMove != null )
				myListener.moveMob( myMove );
			else
				restartMovementTimer();
			
		}
		
	}
	
	/**
	 * This timer is used to inform the listener that the mob has attacked its target.
	 * When it goes off, it informs its listener who it is attack and how much damage it is 
	 * going to hit the target with (base damage, this value is adjusted by the the target's
	 * armor.
	 */
	private class AttackTimerTask
	implements TimerTask
	{		
		public AttackTimerTask()
		{
		}
		
		public void action( Timer theTimer )
		{
			attackTarget( theTimer );
		}
	}
	
	/**
	 * Informs the listener that the mob is attacking its target
	 */ 
	private void attackTarget( Timer theTimer )
	{
		if( ( theTimer.isTriggered() && !theTimer.isStopped() ) &&
		    myListener != null )					
			myListener.attackTarget( myDamage, myName ); 			
	}
	
	
	/**
	 * Private class to hold the respawn timer
	 */		
	private class RespawnTimerTask
	implements TimerTask
	{
		public RespawnTimerTask()
		{
		}
		
		public void action( Timer theTimer )
		{			
			respawnMob( theTimer );
		}
	}
	
	/** 
	 * Informs the listener to respawn its mob
	 */
	private void respawnMob( Timer theTimer )
	{		
		if( theTimer.isTriggered() && myListener != null )					
			myListener.respawnMob( myKey );
		
	}
		
	/** 
	 * Constructor
	 */ 
	public Mob( MobKey theKey, String theName, XYloc initialLoc,
	            int maxHP, int damage, long atkSpd, World theWorld, MobListener theListener, 
		    int gold, int exp, String drop )
	{
		myWorld = theWorld;
		myListener = theListener;
		maxHitPoints = maxHP;
		currentHitPoints = maxHP;
		myDamage = damage;
		alive = true;	
		underAttack = false;		
		myGold = gold;
		myExp = exp;
		myDrop = new String( drop );
		currentLocation = new XYloc( initialLoc );
		movementPRNG = new Random();
		myKey = new MobKey( theKey );
		myAttackSpeed = atkSpd;
		myName = theName;
		movementTimer = TimerThread.getDefault().createTimer( new MovementTimerTask() );
		attackTimer = TimerThread.getDefault().createTimer( new AttackTimerTask() );		
		movementTimer.start( TWO_MINUTES +  movementPRNG.nextInt( THREE_MINUTES ) );
		respawnTimer = TimerThread.getDefault().createTimer( new RespawnTimerTask() );
		timeStamp = null;	
	}
	
	/**
	 * Empty constructor used by readExternal
	 */ 
	public Mob()
	{
		myName = new String();
		myKey = new MobKey();		
		currentLocation = new XYloc( 0, 0 );
		myListener = null;
		movementPRNG = new Random();
		myDrop = null;
		movementTimer = TimerThread.getDefault().createTimer( new MovementTimerTask() );
		attackTimer = TimerThread.getDefault().createTimer( new AttackTimerTask() );	
		respawnTimer = TimerThread.getDefault().createTimer( new RespawnTimerTask() );			
		timeStamp = null;
	}
	
	/**
         * Registers an attacker with this mob.
	 * This places it in attack mode, stops the movement timer
	 * and starts the attack timer.
	 * @param target The id of the player who is the target
	 * @param location This
	 */
	public Date startAttack( Eoid target, XYloc location )
	{
		movementTimer.stop();
		attackTimer.start( 0, myAttackSpeed );
		underAttack = true;
		myCurrentTarget = target;
		timeStamp = new Date();		
		return timeStamp;				
				
	}
       
    /**
    * Places the mob in silent combat mode, which means it will not inform its
	* listener when it attack.
	* @param target the Target of the mob
	* @param theTimeStamp The timestamp of then fight was started
	*/
       public void startAttackSilent( Eoid target,  Date theTimeStamp )
       {
		movementTimer.stop();
		underAttack = true;
		myCurrentTarget = target;
		timeStamp = theTimeStamp;		
       }
       
	/**
	 * Stops the mob from attacking	 
	 */
	public void stopAttack()
	{       				
		myCurrentTarget = null;
		attackTimer.stop();
		underAttack = false;				
		if( !alive )
			respawnTimer.start( TWO_MINUTES +  movementPRNG.nextInt( TWO_MINUTES ) );			
		else
		{
			// retore the monster to full health
			currentHitPoints = maxHitPoints;
			movementTimer.start( TWO_MINUTES +  movementPRNG.nextInt( THREE_MINUTES) );
		}		
	}

	/**
         * Returns the time the mob start combat
         */
	public Date getCombatTimeStamp()
	{	
		return timeStamp;
	}
       
	/**
     * Damages the mob.  Returns true idf the mob is dead.
	 * @param damage The amount of damage the mob takes.
	 */
	public void damageMob( int damage )
	{       		
		currentHitPoints -= damage;
		if( currentHitPoints <= 0 )
			kill();
	}
       
	/**
	 * restarts the movement timer
	 */
	public void restartMovementTimer()
	{
       		movementTimer.start( TWO_MINUTES +  movementPRNG.nextInt( THREE_MINUTES ) );
	}
	
	/** 
	 * Returns the current location of this mob
	 */
	public XYloc getCurrentLocation()
	{
		return currentLocation;
	}
	
	/**
	 * Returns the type of monster this is
	 */
	public int getType()
	{
		return myKey.getType();
	}
	
	/**
	 * Returns the ID of this mob
	 */
	public MobKey getKey()
	{
		return myKey;
	}
	
	/**
	 * Get the name of the mob
	 */
	public String getName()
	{
		return myName;
	}
	
	/** 
	 * Returns whether or not this mob is alive
	 */
	public boolean isAlive()
	{
		return alive;
	}
	
	/**
	 * Update the mob's location
	 * @param location The direction the monster moves in
	 */
	public void updateLocation( XYloc location )
	{		
		currentLocation = new XYloc( location );
	}
	
	/**
	 * Returns whether or not other is equal to this object
	 * @param other The object to comapre to.
	 */
	public boolean equals( Object other )
	{
		boolean retVal = false;
		if( other != null && other.getClass().equals( Mob.class ) )
		{
			Mob otherMob =(Mob)other;			
			
			if( myKey.equals( otherMob.myKey ) )
			{
				retVal = 
				currentLocation.equals( otherMob.currentLocation ) &&
				alive == otherMob.alive && 
				underAttack == otherMob.underAttack;
				
				if( retVal == true && underAttack )
					retVal = myCurrentTarget.equals( otherMob.myCurrentTarget );
			}			
			
		}
		return retVal;
	}
	
	/**
	 * Sets the listener for this mob
	 * @param list The listener
	 */
	public void setList( MobListener list, World theWorld )
	{
		myListener  = list;
		myWorld = theWorld;
	}
	
	/**
	 * Returns whether or not this mob is in combat
	 */
	public boolean isUnderAttack()
	{
		return underAttack;
	}
	
	/**
	 * Returns the current health of the mob
	 */
	public int getCurrentHealth()
	{
		return currentHitPoints;
	}
	
	
	/**
	 * Returns the target of the mob
	 */
	public Eoid getTarget()
	{
		return myCurrentTarget;
	}
	/**
	 * Kills the mob
	 */
	public void kill()
	{
		alive = false;
		currentHitPoints = 0;
		stopAttack();
	}
	
	/**
	 * Respawns the monster at the location
	 * where it died.
	 */
	public void respawn()
	{
		alive = true;
		currentHitPoints = maxHitPoints;
		respawnTimer.stop();
		restartMovementTimer();
	}
	
	/**
	 * Returns the amount of gold this mob
	 * drops when it is killed.
	 */
	public int getGold() 
	{
		return myGold;
	}
	
	/** 
	 * Returns the amount of EXP this mob
	 * drops when it is killed.
	 */
	public int getExp()
	{
		return myExp;
	}
	
	/**
	 * Returns the item id for the item
	 * that this mob drops upon death
	 */
	public String getDrop()
	{
		return myDrop;
	}
	
	/**
	 * This causes either the mob's movement or respawn timers to 
	 * go off immediately.
	 * @param which Which timer to go off
	 */
	public void doNow( int which )
	{
		if( which == MobCommand.MOVE )
			movementTimer.start( 0 );
		else if( which == MobCommand.RESPAWN )
			respawnTimer.start( 0 );
	}
	
	/**
	 * If this is a temporary mob, this returns back the key which is 
	/** 
     	 * Writes the object to output.
	 * @param out The output object to write to
	 */
	public void writeExternal( ObjectOutput out )
	throws IOException
	{
		out.writeObject( myKey );		
		out.writeObject( currentLocation );
		out.writeObject( new Boolean( alive ) );
		out.writeObject( new Boolean( underAttack ) );		
		out.writeInt( currentHitPoints );
		out.writeInt( maxHitPoints );
		out.writeInt( myDamage );
		out.writeObject( new Long( myAttackSpeed ) );
		out.writeObject( myName );
		out.writeInt( myExp );
		out.writeInt( myGold );
		out.writeObject( myDrop );				
		out.writeObject( timeStamp );
		out.writeObject( myCurrentTarget );
	} 
		
	/**
	 * Read the object in from input
	 * @param in The object to read from
	 */
	public void readExternal( ObjectInput in )
	throws IOException, ClassNotFoundException
	{
		myKey = (MobKey)in.readObject();		
		currentLocation = (XYloc)in.readObject();
		alive = ((Boolean)in.readObject()).booleanValue();
		underAttack = ((Boolean)in.readObject()).booleanValue();	
		currentHitPoints = in.readInt();
		maxHitPoints = in.readInt();
		myDamage = in.readInt();
		myAttackSpeed = ((Long)in.readObject()).longValue();
		myName = (String)in.readObject();	
		myGold = in.readInt();
		myExp = in.readInt();
		myDrop = (String)in.readObject();		
		timeStamp = (Date)in.readObject();	
		myCurrentTarget = (Eoid)in.readObject();	
	}	
 }
