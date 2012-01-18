 package m2mimud.state; 
 import edu.rit.m2mi.Eoid;
 import java.io.*;
 import java.util.Iterator;
 import java.util.HashMap;
 import java.util.Arrays;
 import m2mimud.command.special.PlayerCreationCommand;
 import m2mimud.communications.Game;

/** 
 * The PlayerCharacter class is the objeect representation of the player's
 * character, or toon.  This is the character with whom they play the game.
 * 
 * @author Robert Whitcomb
 * @version $Id: PlayerCharacter.java,v 1.17 2005/01/13 15:47:06 rjw2183 Exp rjw2183 $
 */
 
 
 public class PlayerCharacter
 implements Externalizable
 {
 
 	private String name; // character's name
	private int[] stats; // the character's stats 
	private String[] equipment;
	private int sex; // The character's sec 0 is male, 1 is female
	private int clas; // character's classm, f is for figher, m is for mage
	private int level; // the current level of the character
	private Eoid myId; // A unique id for this player
	private HashMap inventory;  // the player's inventory
	private int gold; // the amount of gold a player has
	private Game myHandle; // the handle to the game which controls this player
	private MobKey myMobTarget; // the monster target for this player
	private boolean inCombat; // whether or no this player is in combat
	private Eoid myPlayerTarget; // the player target for this player
	private int currentExp; // the current amount of exp the player has
	private int currentHP; // thw current amount of hp a player has
	private int maxHP; // the maximum amount
	private boolean alive; // whether or not this oplayer has been killed
	private XYloc currentLoc; // the current location of the player
	private String clsName; // a string that holds the name of the class
	private boolean inHouse; // a boolean variable to indicate that the user has entered a house
	private Eoid houseId; // the id of the house the player is in
	
	// Used to access the character's stats
	public static final int STR = 0;
	public static final int CON = 1;
	public static final int DEX = 2;
	public static final int INT = 3;
	
	/**
	 * Used to access the equipment
	 */
	public static final int BODY = 0;
	public static final int TWO_HAND = 1;
	public static final int RIGHT_HAND = 2;
	public static final int LEFT_HAND = 3;
	
	/** 
	 * These variables are used to store data about the character's sex and class
	 */
	public static final int MALE = 0;
	public static final int FEMALE = 1;
	public static final int FIGHTER = 0;
	public static final int MAGE = 1;
	
	/**
	 * Dummy constructor needed for externalizable
	 * DO NOT CALL EVER!
	 */
	public PlayerCharacter()
	{
		name = new String();
		stats = new int[4];
		equipment = new String[4];
		myId = new Eoid();
		inventory = new HashMap();
		alive = true;
		currentLoc = new XYloc( 0, 0 );
		clsName = new String();
		houseId = new Eoid();
	}	
	/* 
	 * Creates a new character. Cannot be directly accessed
	 */
	private PlayerCharacter( PlayerCreationCommand comm, PlayerClass data, Game handle )
	{
		this.name = comm.name;
		this.sex = comm.sex;
		this.clas = data.classCode;
		this.clsName = new String( data.className );
		
		stats = new int[4];
		stats[STR] = data.str;
		stats[CON] = data.cons;
		stats[DEX] = data.dex;
		stats[INT] = data.intg;

		level = 1;
		myId = Eoid.next();
		inventory = new HashMap();
		equipment = new String[4];
		gold = 1000;
		myHandle = handle;
		currentExp = 0;
		maxHP = data.hp;
		currentHP = data.hp;
		alive = true;
		currentLoc = new XYloc( 0, 0 );
	}
	
	/**
	 * Constructor - private
	 * This creates a playerCharacter object from another 
	 * playerCharacter object
	 */
	private PlayerCharacter( PlayerCharacter other, Game handle )
        {
            this.name = new String( other.name );
            this.sex = other.sex;
            this.clas = other.clas;
	    this.clsName = other.clsName;	    
            
            stats = new int[4];
            stats[STR] = other.stats[STR];
            stats[CON] = other.stats[CON];
            stats[DEX] = other.stats[DEX];
            stats[INT] = other.stats[INT];
            
            level = other.level;
            myId = other.myId;
            inventory = new HashMap( other.inventory );
            equipment = new String[4];
            for( int i = 0; i < equipment.length; i++ )
            {
                if( other.equipment[i] != null )
                    equipment[i] = new String( other.equipment[i] );
                else
                    equipment[i] = null;
            }
            gold = other.gold;
            myHandle = handle;
            currentExp = other.currentExp;
            maxHP = other.maxHP;
            currentHP = other.currentHP;
            alive = true;
            currentLoc = new XYloc( other.currentLoc );
        }
                
	/** 
	 * Creates a new character.
	 * @param comm The PlayerCreationCommand that contains
	 *  information (gender, name, and class) of the new player.
	 * 
	 * @param handle The handle associated with the Game unit that controls the player
	 */
	public static PlayerCharacter createNewCharacter( PlayerCreationCommand comm, 
	Game handle)
	{
		PlayerCharacter retVal = null;
		PlayerClass data = PlayerClass.load( comm.clas );		
		if( data != null )		
			retVal = new PlayerCharacter( comm, data, handle );		
		return retVal;					   
	}	
        
	/** 
	 * Creates a new playercharacter object from the given one
	 */
        public static PlayerCharacter createNewCharacter( PlayerCharacter other, Game handle )	
        {
            return new PlayerCharacter( other, handle );
        }
	
	/**
	 * Returns a string representation of the character
	 */
	public String toString()
	{
		String s_sex, s_clas;
		if( sex == MALE )
			s_sex = "male";
		else
			s_sex = "female";
		
		return name + ": " + s_sex + " " + clsName + "\n" +
		       "(player id: " + myId + ")" + " " + currentLoc + ", inCombat " +
		       inCombat + ", fighting: " + getTarget() + ", inHouse " + inHouse + 
		       " houseId " + houseId;
	}
      
      	/**
	 * Returns a string to display the stats of the character
	 */
	public String getStatString()
	{
		return 
		"\nYour character stats are: \n" + 
		"Class: " + clsName + "\n" +
		"Level: " + level + "\n" +
		"Maximum HP: " + maxHP + "\n" +
		"str: " + stats[STR] + "\n" + 
		"cons: " + stats[CON] + "\n" +
		"dex: " + stats[DEX] + "\n" +
		"int: " + stats[INT] + "\n" +
		"EXP: " + currentExp;
	}
		
		
	/**
         * Returns the current location of the player
	 */
	public XYloc getLocation()
	{
		return currentLoc;
	}
	
       
	/**
	 * Returns the name of the character
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Returns the sex of the player
	 */
	public int getSex()
	{
		return sex;
	}
	
	/**
	 * Returns the class of the player
	 */
	public int getPlayerClass()
	{
		return clas;
	}
	
	/**
	 * Returns the given stat
	 * @param which The stat to look up
	 */
	public int getStat( int which )
	{
		int retVal = -1;
		if( 0 <= which && which <= 3)
			retVal = stats[which];
		return retVal;
	}
	
	/**
	 * Returns the level of the character
	 */
	public int getLevel()
	{
		return level;
	}
	
	/**
	 * Returns the amount of gold the player has
	 */
	public int getCurrentGold()
	{
		return gold;
	}

	/**
	 * Returns the Id of this character
	 */
	public Eoid getId()
	{
		return myId;
	}
	/**
	 * Returns whether or not this object is equal to the other one
	 * @param other The other character to check 
	 */
	public boolean equals( Object other )
	{
		boolean retVal = false;
		if( other != null && this.getClass().isInstance( other ) )
		{
			PlayerCharacter otherPlayer = (PlayerCharacter)other;
			if( myId.equals( otherPlayer.myId ) )
			{
				retVal = ( currentLoc.equals(otherPlayer.currentLoc ) &&
				inHouse == otherPlayer.inHouse  &&
				inCombat == otherPlayer.inCombat );
                
				if( retVal == true && inCombat == true )
					retVal = this.getTarget().equals( otherPlayer.getTarget());		
		
			if( retVal == true )
				retVal = checkHouseId( otherPlayer.getHouse() );                                               
			}
		}
		return retVal;
	}
       
	
	/**
	 * This determines if the two houses ideas are the same
	 * @param otherId The other house id.
	 */
	private boolean checkHouseId( Eoid otherId )
	{
		boolean retVal = true;
		if(  ( houseId == null && otherId != null ) ||
		     ( houseId != null && otherId == null ) )
		     	retVal = false;
		else
		{
			if( houseId != null && otherId != null )
				retVal = houseId.equals( otherId );
		}
		return retVal;
	}
	
	/**
	 * This updates this player with the other, but it will only do
	 * so is the players have the same Id.  Also, the information about the 
	 * the player's target is no copied.  That must be done manually
	 * @param other The player to copy
	 */
	public void copyData( PlayerCharacter other )
	{
		if( other != null && myId.equals( other.myId ) )
		{
			name = new String( other.name );
			sex = other.sex;
			clas = other.clas;
			gold = other.gold;
			level = other.level;
			maxHP = other.maxHP;
			inCombat = other.inCombat;
			alive = other.alive;
			for( int i = 0; i < stats.length; i++ )
				stats[i] = other.stats[i];
			for( int i = 0; i < equipment.length; i++ )
				equipment[i] = new String( other.equipment[i] );
			currentLoc = new XYloc( other.currentLoc );
		}
	}
	
	/**
	 * Private function used to determine if the two objects, 
	 * which represent the targets, are equal.  This is used
	 * because one of the values may be null while the other isn't.
	 */
	private boolean checkTargets( Object target1, Object target2 )
	{
		boolean retVal = false;
		if( target1 != null && target1.equals( target2 ) ||
		    target1 == null && target2 == null )
		    retVal = true;
		return retVal;
	}
		
		
	/**
	 * Adds an item to the player's inventory
	 * @param id The id of the item
	 * @param amount the amount of the item to add
	 */
	public void addItem( String id, int amount )
	{
		if( inventory.containsKey( id ) )
		{
			int i = ((Integer)inventory.get( id ) ).intValue();
			i += amount;
			inventory.put( id, new Integer( i ) );
		}
		else
			inventory.put( id, new Integer( amount ) );
	}
	
	
	
	/**
	 * Decrements the players's gold by the given amount
	 * iff the player has at least as much gold as the amount
	 * Otherwise, no action is taken
	 * @param amount The amount to decrement by
	 */
	public void decrementGold( int amount )
	{
		if( gold >= amount )
			gold -= amount;
	}
	
	/**
	 * Returns whether or not the user has at least the given amount
	 * @param amount The amoiunt to check
	 */
	public boolean canAfford( int amount )
	{
		return gold >= amount;
	}
	
	/** 
	 * Increments the player gold by the given amount
	 * @param amount The amount to increment by
	 */
	public void incrementGold( int amount )
	{	
		gold += amount;
	}
	
	/**
	 * Adjusts the player's hit point by the given amount.
	 * To decrement, just give a negative value
	 */
	public void adjustHP( int amount )
	{
		currentHP += amount;
		if( currentHP <= 0 )
			alive = false;
		else if( currentHP > maxHP )
			currentHP = maxHP;
	}
	
	/**
	 * Returns if thisplayer is currently alive
	 */
	public boolean isAlive()
	{
		return alive;
	}
	
	/**
	 * Saves this character object to a file.
	 */
	public void save()
	{
		try
		{
			leaveHouse();
			String fileName = new String( "players/" + name + ".dat" );			
			File theFile = new File( fileName );
			FileOutputStream outStream = new FileOutputStream( theFile );
			ObjectOutputStream objStream = new ObjectOutputStream( outStream );
			objStream.writeObject( this );
			objStream.flush();
			objStream.close();
			outStream.close();
		}
		catch( Exception e )
		{
			e.printStackTrace();
			System.exit( 1 );
		}
	 }
	 
	 /**
	  * Returns the handle to the game unit to which this character belongs
	  */
	 public Game getHandle()
	 {
		return myHandle;
	 }
	 
	 /** 
	  * Returns an interator of the ids of
	  * the items in the user's inventory
	  */
	 public Iterator getInventoryKeys()
	 {
		return inventory.keySet().iterator();
	 }
	 
	 /**
	  * Return how much of the item the user has
	  * @param id The item to look at
	  */
	 public int getItemAmount( String id )
	 {
	 	int retVal = 0;
		if( inventory.containsKey( id ) )
			retVal = ((Integer)inventory.get( id )).intValue();
		return retVal;
	 }
	 
	 /**
	  * Returns the id of the item at the given slot
	  * @param slot The slot to look at
	  */
	 public String getGear( int slot )
	 {
	 	return equipment[slot];
	 }
	
	 /**
	  * Equips the item into the given slot
	  * If the item is going into any of the hand slots
	  * (right, left, or two hand), then the items in the other
	  * hand slot are adjust accordingly.  If the item
	  * is a weapn for the left or right hand, then the 
	  * two handed slot is emptied and vice versa
	  * @param id The id of the item to equip
	  * @param slot The slot to use
	  */
	 public void equipItem( String id, int slot )
	 {
	 	transferItem( slot );
		equipment[slot] = id;
		int i = ((Integer)inventory.get( id )).intValue();
		i--;
		inventory.put( id, new Integer( i ) );
		
		// if this is a two handed weapon, sheathe the items in the 
		// right and left hand.		 
		if( slot == TWO_HAND )
		{
			transferItem( RIGHT_HAND );
			transferItem( LEFT_HAND );
		}
		
		// if this is right or left handed item, sheathe the 2 handed slot
		if( slot == RIGHT_HAND || slot == LEFT_HAND )
			transferItem( TWO_HAND );		
	 }
	 
	 /** 
	  * Private function which transfer the item from the slot back into
	  * the user's inventory
	  * @param slot The slot to transfer
	  */
	 private void transferItem( int slot )
	 {
	 	if( equipment[slot] != null )
		{
			int value = ((Integer)inventory.get( equipment[slot] )).intValue();
			value++;
			inventory.put( equipment[slot], new Integer( value ) );
			equipment[slot] = null;
		}
	} 
	
	 /**
	  * Returns the currently equiped weapon, if any.
	  */
	 public String getEquippedWeapon()
	 {
	 	String retVal = null;
		if( equipment[RIGHT_HAND] == null )
			retVal = equipment[TWO_HAND];
		else
			retVal = equipment[RIGHT_HAND];
		return retVal;
	 }
	 /**
	  * Registers the target with playe
	  * @param theTarget The target of the player
	  */
	 public void registerTarget( Object theTarget )	 	 
	 {
	 	if( myMobTarget == null && myPlayerTarget == null )
		{
			inCombat = true;
			if( theTarget.getClass().equals( MobKey.class ) )
				myMobTarget = (MobKey)theTarget;
			else if( theTarget.getClass().equals( Eoid.class ) )
				myPlayerTarget = (Eoid)theTarget;
			else
				clearTargetData();							
		}
	 }
	 	 
	 /**
	  * Returns the target of the player
	  */
	 public Object getTarget()
	 {
	 	Object retVal = null;
		if( myMobTarget != null )
			retVal = myMobTarget;
		else if( myPlayerTarget != null )
			retVal = myPlayerTarget;
		return retVal;
	}
	
	/**
	 * Updates the player's location
	 * @param loc The new location of the player
	 */
	public void updateLocation( XYloc loc )
	{
		currentLoc = new XYloc( loc );
	}
	
	/**
	 * Return whether or not this player is in combat
	 */
	public boolean isInCombat()
	{
		return inCombat;
	}
	 
	/**
	 * Returns whether or not the player has a mob for a target
	 */
	public boolean hasMobTarget() 
	{
		return myMobTarget != null;
	}
	 
	/**
	 * Clears the target data 
	 */
	public void clearTargetData()
	{		
		myMobTarget = null;
		myPlayerTarget = null;
		inCombat = false;
	}
	 
	/** 
	 * Adjusts the player's exp by the given amount
	 */
	public void incrementExp( int amount )
	{		
		currentExp += amount;
		if( currentExp >= 100 )
			levelUp();
			
		if( currentExp < 0 )
			currentExp = 0;
	}
	
	/**
	 * Private function called when the player levels up
	 */
	private void levelUp()
	{
		level += 1;
		for( int i = 0; i < stats.length; i++ )
			stats[i] += 2;
		PlayerClass myClass = PlayerClass.load( clsName );
		stats[STR] += myClass.inc_str;
		stats[DEX] += myClass.inc_dex;
		stats[CON] += myClass.inc_cons;
		stats[INT] += myClass.inc_int;
		maxHP += myClass.inc_hp;
		currentHP = maxHP;
		currentExp = 0;
	}	 	
	
	/**
	 * Returns the amount of hp the player currently has
	 */
	public int getHP()
	{
	 	return currentHP;
	}
	 
	/**
	 * Return the player's maximum hp
	 */
	public int getMaxHP()
	{
		return maxHP;
	}
	 
	/**
	 * Restores the player to life
	 */
	public void ressurect()
	{
	 	currentHP = maxHP;
		alive = true;
		clearTargetData();		
	}
	 	
	 /**
	  * Causes a user to enter a house.
	  * Unlike a room, the user maintains no information
	  * about which house he or she is in.  That is because the
	  * system is capable of looking at the location of the player, 
	  * and then asking all the houses in that area if the player is in 
	  * them.
	  */
	 public void enterHouse( Eoid theHouse )
	 {
	 	inHouse = true;
		houseId = theHouse;
	 }
	 
	 /**
	  * Removes a player from the house.
	  */
	 public void leaveHouse()
	 {
	 	inHouse = false;
		houseId = null;
	 }
	 
	 /**
	  * Returns the id of the house that the user is in
	  */
	 public Eoid getHouse()
	 {
	 	return houseId;
	 }
	 
	 /**
	  * Returnd true if the player is in a house
	  */
	 public boolean isInHouse()
	 {
	 	return inHouse;
	 }
	 
	 /**
	  * Writes the character out to output.
	  * @param out The object to write to.
	  */
	 public void writeExternal( ObjectOutput out ) 
	 throws IOException
	 {	 			
		out.writeObject( myId );
		out.writeObject( name );
		out.writeObject( clsName );		
		out.writeInt( sex );
		out.writeInt( clas );
		out.writeInt( level );
		out.writeInt( gold );
		out.writeObject( myHandle );
		for( int i = 0; i < 4; i++ )
			out.writeInt( stats[i] );
		out.writeObject( inventory );
		out.writeObject( equipment[BODY] );
		out.writeObject( equipment[TWO_HAND] );
		out.writeObject( equipment[RIGHT_HAND] );
		out.writeObject( equipment[LEFT_HAND] );
		out.writeObject( myMobTarget );
		out.writeObject( myPlayerTarget );
		out.writeObject( new Boolean( inCombat ) );
		out.writeInt( currentExp );
		out.writeInt( currentHP );
		out.writeInt( maxHP );
		out.writeObject( currentLoc );		
		out.writeObject( new Boolean( inHouse ) );
		out.writeObject( houseId );		
	 }
	 
	 /**
	  * Reads the character in from input
	  * @param in The object to read in from.
	  */
	 public void readExternal( ObjectInput in )
	 throws IOException, ClassNotFoundException
	 {	 			
		myId = (Eoid)in.readObject();
		name = (String) in.readObject();		
		clsName = (String)in.readObject();		
		sex = in.readInt();
		clas = in.readInt();
		level = in.readInt();
		gold = in.readInt();
		myHandle = (Game)in.readObject();
		for( int i = 0; i < 4; i++ )
			stats[i] = in.readInt();
		inventory = (HashMap)in.readObject();
		equipment[BODY] = (String)in.readObject();
		equipment[TWO_HAND] = (String)in.readObject();
		equipment[RIGHT_HAND] = (String)in.readObject();
		equipment[LEFT_HAND] = (String)in.readObject();	
		myMobTarget = (MobKey)in.readObject();
		myPlayerTarget = (Eoid)in.readObject();
		inCombat = ((Boolean)in.readObject()).booleanValue();
		currentExp = in.readInt();
		currentHP = in.readInt();
		maxHP = in.readInt();	
		currentLoc = (XYloc)in.readObject();	
		inHouse = ((Boolean)in.readObject()).booleanValue();
		houseId = (Eoid)in.readObject();		
	 }
	
	/** 
	 * Loads the player character from a file into memory.  If the player data
	 * doesn't exist, it is set to null to indicate that no character exists
	 * @param handle The handle to associate with the player
	 * @param name The name of the player
	 */
	public static PlayerCharacter loadCharacterDataFromFile( Game handle, String name )
	{
		String fileName = new String( "players/" + name + ".dat" );
		File characterFile = new File( fileName );
		PlayerCharacter retVal = null;
		if( characterFile.exists() )
		{
			try
		   	{		   	
				FileInputStream inStream = new FileInputStream( characterFile );
				ObjectInputStream objStream = new ObjectInputStream( inStream );
				retVal = (PlayerCharacter) objStream.readObject();	
				retVal.myHandle = handle;
				retVal.clearTargetData();	
				inStream.close();
				objStream.close();			
		  	}
			catch( Exception e )
			{
		    		e.printStackTrace();
				System.exit( 1 );
			}
		}
		return retVal;
	}			
 }
