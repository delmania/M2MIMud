 package m2mimud.command;  

 /** 
  * The command class is an object representation the command which the user 
  * entered.  It is created by the parser and passed to the system.
  *
  * @author Robert Whitcomb
  * @version $Id: Command.java,v 1.13 2004/12/10 02:29:38 rjw2183 Exp $ 
  */

 public class Command
 {
 	
	
	// The list of command available to the user
	public static final int QUIT            = 0;
	public static final int CREATE          = 1;
	public static final int LIST            = 2;
	public static final int LOOK            = 3;
	public static final int MOVE            = 4;
	public static final int YELL            = 5;
	public static final int CREATE_GAME     = 6;
	public static final int FIND_GAMES      = 7;
	public static final int CANCEL_SESSION  = 8;
	public static final int LEAVE_SESSION   = 9;
	public static final int WHO             = 10;  
	public static final int SAY             = 11; 
	public static final int DIG_POND        = 12;
	public static final int HOUSE_CREATE    = 13;
	public static final int SESSION_COMMAND = 14;
	public static final int TIME            = 15;
	public static final int GOTO            = 16;
	public static final int LIST_WARES      = 17;
	public static final int BUY             = 18;	
	public static final int INVENTORY       = 19;	
	public static final int CHECK_FRIENDS   = 20;
	public static final int LOOKUP          = 21;
	public static final int REMOVE          = 22;
	public static final int HOUSE_ENTER     = 23;
	public static final int EQUIP           = 24;
	public static final int ATTACK 	        = 25;
	public static final int LLIST           = 26;
	public static final int LSEND           = 27;
	public static final int LADD            = 28;
	public static final int LCHECK          = 29;		
	public static final int ADD             = 30;
	public static final int SEND            = 31;
	public static final int SELL            = 32;
	public static final int ATTACK_NO_TARG  = 33;
	public static final int CHALLENGE       = 34;
	public static final int ACCEPT          = 35;
	public static final int DECLINE         = 36;
	public static final int MAP             = 37;
	public static final int LOAD            = 38;
	public static final int SAVE            = 39;
	public static final int STATE_REMOVE    = 40;
	public static final int SAVE_QUICK      = 41;
	public static final int USE             = 42;
	public static final int PARTITION       = 43;
	public static final int ENTER           = 44;
	public static final int CAST            = 45;
	public static final int CAST_NO_TARG    = 46;
	public static final int LEAVE_HOUSE     = 47;
	public static final int CLEAR_SCREEN    = 48;
	public static final int DISPLAY_PART    = 49;
	public static final int DO_REPORT_NOW   = 50;
	public static final int SURRENDER       = 51;
	public static final int MOB_COMMAND     = 52;
        public static final int PRINT_MOBS      = 53;
	public static final int PRINT_STATS     = 54;
        public static final int INVALID_COMMAND = 55;	
	
	private int type;	// the type of command this is

	/**
	 * Constructor
	 * @param type The type of command this is
	 */
	public Command( int type )
	{
		this.type = type;		
	}
	
	/**
	 * Returns the type of command this is
	 */
	public int getType()
	{
		return type;
	}
}
	
