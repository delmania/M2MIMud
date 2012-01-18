 package m2mimud.command;

 /**
  * The CommandExec interface is used to designate a class which can accept
  * command for processing.
  * 
  * @author Robert Whitcomb
  * @version $Id: CommandExec.java,v 1.2 2005/01/06 13:46:11 rjw2183 Exp rjw2183 $ 
  */

 public interface CommandExec
 {
 	/** 
	 * Accept a command for processing
	 * 
	 * @param command The command for processing.
	 */
	 public void executeCommand( String command );	
 }
 
