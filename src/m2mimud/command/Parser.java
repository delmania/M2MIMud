package m2mimud.command;
import m2mimud.command.special.*;
import java.io.*;
import java.util.regex.*;
import m2mimud.state.PlayerCharacter;

 /**
  * The Parser is the object that takes in the string value typed in by the user
  * and creates a Command object that can be given to the GameSystem to tell
  * it what to do to perform the user's action
  *
  * @author Robert Whitcomb
  * @version $Id: Parser.java,v 1.20 2005/01/06 13:46:11 rjw2183 Exp rjw2183 $
  */

public class Parser
{
    private String [] theCommands;
    
    /**
     * Constructor
     */
    public Parser()
    {
        try
        {
        	ObjectInputStream oStream = 
	        	new ObjectInputStream
			( new FileInputStream( "data/commands.data" ) );
	        theCommands = (String[])oStream.readObject();
        	oStream.close();
        }
        catch( Exception e )
        {
            e.printStackTrace();
            System.exit( 1 );
        }
    }
    
    /**
     * The parse command takes in the user's input and transforms it into a 
     * Command object
     * @param command The user's text command
     */
    public Command parse( String command )
    {
    	Command retVal = null;      
    	int commandCode = getCommandCode( command );               
    	if( isBasicCommand( commandCode ) )
        	retVal = new Command( commandCode );
    	else
       		retVal = processCommand( commandCode, command.trim() );
    	return retVal;
    }
    
    /**
     * Given a string command, this determines what the integer code
     * is for the appropriate Command object
     * @param theCommand The string command
     */
    private int getCommandCode( String theCommand )
    {
        int retVal = Command.INVALID_COMMAND;
        boolean found = false;
        for( int i = 0; i < theCommands.length && !found ; i++)
        {
	    if( theCommands[i] != null )	        
            {
	    	Pattern commPattern = Pattern.compile( theCommands[i].trim() );		
		if( commPattern.matcher( theCommand.trim() ).matches() )
		{			
			retVal = i;
	                found = true;
		}
            }
        }
        return retVal;
    }
    
    /**
     * Returns whether or not a command code is for
     * a basic command.  A basic command is a command that
     * has no paramaters.  Examples of this type of command
     * would be the look and list wares commands.  
     * @param commandCode The command code to look at
     */
    private boolean isBasicCommand( int commandCode )
    {
        return ( commandCode == Command.QUIT ||
                 commandCode == Command.LIST_WARES ||
		 commandCode == Command.LIST ||
                 commandCode == Command.LOOK ||
                 commandCode == Command.TIME ||
                 commandCode == Command.WHO ||
                 commandCode == Command.LEAVE_SESSION ||
                 commandCode == Command.FIND_GAMES ||
                 commandCode == Command.SAVE_QUICK ||
                 commandCode == Command.DIG_POND ||              
                 commandCode == Command.CHECK_FRIENDS ||
                 commandCode == Command.INVENTORY ||
                 commandCode == Command.LEAVE_HOUSE ||
                 commandCode == Command.DISPLAY_PART ||
                 commandCode == Command.CLEAR_SCREEN ||
                 commandCode == Command.MAP ||
                 commandCode == Command.LLIST ||                
		 commandCode == Command.INVALID_COMMAND ||
		 commandCode == Command.DO_REPORT_NOW ||
		 commandCode == Command.SURRENDER ||
                 commandCode == Command.PRINT_MOBS ||
		 commandCode == Command.PRINT_STATS );                                
    }
    
    /**
     * This function deals with the none basic commands, the one that do have
     * parameters and therefore need special processing
     * @param code The command code to indicate what kind of command this is
     * @param command The string value of the command
     */
    private Command processCommand( int commandCode, String command )
    {
        Command retVal = null;
        if( commandCode ==  Command.YELL || commandCode == Command.SAY )
        {
            // Say or yell commands
	    String message =command.substring( command.indexOf( "") + 1,
                            command.length() ).trim();
            retVal = new StringCommand( commandCode, message );
        }
        else if( commandCode == Command.MOVE )
        {
            // Movement commands
	    String direction = command.substring( command.indexOf( " " ) + 1,  
                               command.length() ).trim();
            int dirCode = -99;	             
	    
	    if( direction.equals( "north" ) )
                dirCode = IntCommand.NORTH;
            
	    else if( direction.equals( "south" ) )
                dirCode = IntCommand.SOUTH;
            
	    else if( direction.equals( "east" ) )
                dirCode = IntCommand.EAST;
            
	    else if( direction.equals( "west" ) )
                dirCode = IntCommand.WEST;
		
            retVal = new IntCommand( Command.MOVE, dirCode );                
        }
        else if( commandCode == Command.CREATE_GAME )
        {
            // Game creation command  
	    retVal = new StringCommand
            ( commandCode, command.substring( command.indexOf( " ") + 1, 
            command.length() ).trim() );
        }
        else if( commandCode == Command.CREATE )
        {
            // Character Creation command
	    String[] charData  = command.split( "\\s" );
            String name = charData[2];
            String clas = charData[3];
            int sex = PlayerCharacter.FEMALE;
            if( charData[1].equals( "male" ) )
                sex = PlayerCharacter.MALE;
            retVal = new PlayerCreationCommand( name, sex, clas );  
        }
        else if( commandCode == Command.HOUSE_CREATE )
        {
            // House creation commands
	    retVal = new HouseCreationCommand
            ( command.substring( command.indexOf( " ") + 1, 
              command.length() ), true );
        }
        else if( commandCode == Command.GOTO )
        {
            // goto command
	    String[] warpData = command.split( "\\s" );
            int x = Integer.parseInt( warpData[1] );
            int y = Integer.parseInt( warpData[2] );
            retVal = new GotoCommand( x, y );
        }
        else if( commandCode == Command.BUY )
        {
               // Buy or Sell commands
	       String[] transactionData = command.split( "\\s" );
               if( transactionData[0].equals( "sell" ) )
                   commandCode = Command.SELL;
               String itemId = transactionData[1];
               int amount = Integer.parseInt( transactionData[2] );
               retVal = new StringIntCommand( commandCode, amount, itemId );
        }
        else if( commandCode == Command.SEND || commandCode == Command.LSEND )
        {
            // Send (including lsend) command
	    int firstSpace = command.indexOf( " " );
            int secondSpace = command.indexOf( " ", firstSpace + 1 );
            String msg = command.substring( secondSpace + 1, command.length() ).trim();
            
            String name = command.substring( firstSpace + 1, secondSpace ).trim();            
            if( commandCode == Command.LSEND )
            {
                int id = Integer.parseInt( name );
                retVal = new SendCommand( id, msg );
            }
            else
                retVal = new SendCommand( name, msg );
        }
        else if( commandCode == Command.ADD || commandCode == Command.CHALLENGE )
        {            
            // Add/lookup commands
	    String comm = command.substring( 0, command.indexOf( " " ) ).trim();
            String name = command.substring( command.indexOf( " " ) + 1,  
                                             command.length() ).trim();
            if( commandCode == Command.CHALLENGE )
	    	retVal = new StringCommand( Command.CHALLENGE, name );
	    else
	    {
	    	if( comm.equals( "lookup" ) )                                   
        		commandCode = Command.LOOKUP;
	        retVal = new StringCommand( commandCode, name );
	    }
        }
        else if( commandCode == Command.LADD )
        {
            // ladd command
	    int index = Integer.parseInt( command.substring(
            command.indexOf( " ") + 1, command.length() ).trim() );
            String comm = command.substring( 
            0, command.indexOf( " " ) ).trim();
            
            if( comm.equals( "lcheck ") ) 
                commandCode = Command.LCHECK;
            retVal = new IntCommand( commandCode, index );
        }
        else if( commandCode == Command.EQUIP || commandCode == Command.USE )
        {
            // Equip Command
	    String itemId = command.substring( 
            command.indexOf( " ") + 1, command.length() );
            retVal = new StringCommand( commandCode, itemId );
        }
	else if( commandCode == Command.REMOVE )
	{
		String name = command.substring(
		0, command.indexOf( " " ) ).trim();
		retVal = new StringCommand( commandCode, name );
	}
	else if( commandCode == Command.ATTACK || commandCode == Command.ATTACK_NO_TARG )
	{
		int spacePos = command.indexOf( " " );
		String attackName = null;
		
		if( commandCode == Command.ATTACK_NO_TARG )
			attackName = command.trim();
		else
			attackName = command.substring( 0, spacePos ).trim();
			
		int atkType = -1;
		if( attackName.equals( "punch" ) || attackName.equals( "pu" ) )
			atkType = StringIntCommand.PUNCH;
		else if( attackName.equals( "kick" ) || attackName.equals( "ki" ) )
			atkType = StringIntCommand.KICK;
		else if( attackName.equals( "slash" ) || attackName.equals( "sl" ) )
			atkType = StringIntCommand.SLASH;
		else if( attackName.equals( "thrust" ) || attackName.equals( "th" ) )
			atkType = StringIntCommand.THRUST;
		
		if( commandCode == Command.ATTACK )
		{
			String tName = command.substring( spacePos + 1, command.length() );
			retVal = new StringIntCommand( commandCode, atkType, tName );
		}
		else
			retVal = new IntCommand( commandCode, atkType );
	}
	else if( commandCode == Command.CAST || commandCode == Command.CAST_NO_TARG )
	{
		String[] castData = command.split( "\\s" );
		String splId = castData[1];
		if( commandCode == Command.CAST )
		{
			String name = castData[2];
			retVal = new CastCommand( splId, name );
		}
		else
			retVal = new StringCommand( Command.CAST_NO_TARG, splId );
	}
	else if( commandCode == Command.SAVE || commandCode == Command.LOAD )
	{
		String[] data = command.split( "\\s" );
		int dataType = -1;
		String which = data[1];
		String name = data[2];
		
		if( which.equals( "player" ) )
			dataType = StringIntCommand.PLAYER;
		else if( which.equals( "map" ) )
			dataType = StringIntCommand.MAP;
		else
			dataType = StringIntCommand.WORLD;
		retVal = new StringIntCommand( commandCode, dataType, name );
		
	}
	else if( commandCode == Command.STATE_REMOVE )
	{
		int houseNum = -1;
		String[] removeData = command.split( "\\s" );
		if( removeData[1].equals( "house" ) )
			houseNum = Integer.parseInt( removeData[2] );
		retVal = new IntCommand( commandCode, houseNum );
	}
	else if( commandCode == Command.PARTITION || commandCode == Command.ENTER
	         || commandCode == Command.HOUSE_ENTER )
	{

		int firstPos = command.indexOf( " " );
		int secondPos = command.indexOf( " ", firstPos + 1 );
		String which = command.substring( secondPos,
						  command.length() ).trim();
		int data = Integer.parseInt( which );
		retVal = new IntCommand( commandCode, data );
	}
	else if( commandCode == Command.MOB_COMMAND )
	{
		String[] cmdData = command.split( "\\s" );
		
		int key1 = Integer.parseInt( cmdData[1] );
		int key2 = Integer.parseInt( cmdData[2] );
		int which = -999;
		
		if( cmdData[0].equals( "kill" ) )
			which = MobCommand.KILL;
		else if( cmdData[0].equals( "move" ) )
			which = MobCommand.MOVE;
		else if( cmdData[0].equals( "respawn" ) )
			which = MobCommand.RESPAWN;
		else if( cmdData[0].equals( "find" ) )
			which = MobCommand.FIND;
		
		retVal = new MobCommand( which, key1, key2 );
			
		
	}
	else if( commandCode == Command.ACCEPT )
	{
		int which = commandCode;
		if( command.trim().equals( "decline" ) )
			which = Command.DECLINE;
		retVal = new Command( which );
	}
	else	
		retVal = new Command( Command.INVALID_COMMAND );

        return retVal;
    }
}
