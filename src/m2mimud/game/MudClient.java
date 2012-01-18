package m2mimud.game;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.*;
import javax.swing.text.rtf.RTFEditorKit;
import m2mimud.command.CommandExec;
import m2mimud.state.PlayerCharacter;

/**
 * The MudClient class is the GUI window for the game.
 * 
 * @author Robert Whitcomb
 * @author Jangho Hwang
 * @version $Id: MudClient.java,v 1.12 2005/01/12 14:03:17 rjw2183 Exp $
 *
 */

public class MudClient
extends JFrame
{
	private JScrollPane chatPane, inputPane, combatPane;
	private JTextArea inputArea;	
	private CommandExec commExec;
	private MudDisplayWindow myDisplayWindow, combatWindow;
	private Font FONT_10; 
	JLabel statusLabel, nameLabel, classLabel;
	JLabel levelLabel, goldLabel, idLabel, equilLabel, balanceLabel;
	JLabel hpLabel, stateNameLabel, targetLabel;
	
	/** 
	 * Creates a new client.
	 *
	 * @param title The title of the window
	 * @param cE The CommandExec object associated with this client that process
	 *           commands from the user.
	 *          
	 */
	public MudClient( String title, CommandExec cE )
	{
		// For the most part, this window is based on the jMSN
		// chat window jMSN is a java based MSN Messenger client developed
		// by Jango Hwang, and availabe at http://jmsn.sourceforge.net/
		super( title );
		commExec = cE;
		FONT_10 = new Font( "Dialog", Font.PLAIN, 12 );
		
		setSize( 500, 700 );
		JPanel mainPanel = (JPanel)getContentPane();
		
		myDisplayWindow = new MudDisplayWindow();
		myDisplayWindow.setFont( FONT_10 );
		
		combatWindow = new MudDisplayWindow();
		combatWindow.setFont( FONT_10 );
		
		JPanel propertyPane = new JPanel();
		propertyPane.setLayout( new BorderLayout() );
		
		JPanel propertyPanel = new JPanel();
		propertyPanel.setPreferredSize( new Dimension( Short.MAX_VALUE, 32 ) );
		propertyPanel.setLayout( new FlowLayout( FlowLayout.LEFT, 5, 2 ) );
	 	propertyPane.add( propertyPanel,"West" ); 
		
		inputArea = new JTextArea();
		inputArea.setFont( FONT_10 );
		inputArea.setLineWrap( true );
		
		equilLabel = new JLabel( "Mental Equilibrium" );		
		equilLabel.setFont( FONT_10 );
		equilLabel.setPreferredSize( new Dimension( 150, 24 ) );
		
		balanceLabel = new JLabel( "Physical Balance" );
		balanceLabel.setFont( FONT_10 );
		balanceLabel.setPreferredSize( new Dimension( 150, 24 ) );
		
		JPanel statusPanel = new JPanel( new FlowLayout() );
		statusPanel.add( equilLabel );
		statusPanel.add( balanceLabel );
		
		chatPane = new JScrollPane( myDisplayWindow );
		myDisplayWindow.setScrollPane( chatPane );		
		
		
		combatPane = new JScrollPane( combatWindow );
		combatWindow.setScrollPane( combatPane );
		combatPane.setPreferredSize( new Dimension( 100, 50 ) );
		inputPane = new JScrollPane( inputArea );
		inputPane.setPreferredSize( new Dimension( 100, 50 ) );
		
		JPanel outputPanel = new JPanel( new BorderLayout() );
		outputPanel.add( combatPane, "North" );
		outputPanel.add( new JLabel( "Please enter in a command:" ), "Center" );
		outputPanel.add( inputPane, "South" );
		
	 
		JPanel bottomPanel = new JPanel( new BorderLayout() );
		bottomPanel.add( propertyPane, "North" );
		bottomPanel.add( outputPanel, "Center" );
		bottomPanel.add( statusPanel, "South" );
		
		JPanel topPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 4, 4 ) );
		topPanel.setPreferredSize( new Dimension( 100, 45 ) );
		topPanel.setBorder( BorderFactory.createEtchedBorder() );
		nameLabel = new JLabel( "Name: " );
		levelLabel = new JLabel( "Level: " );
		classLabel = new JLabel( "Class: " );
		goldLabel = new JLabel( "Gold: " );
		idLabel = new JLabel( "Id: " );
		hpLabel = new JLabel( "Hp: / " );
		stateNameLabel = new JLabel();
		targetLabel = new JLabel( "Target:  " );
		topPanel.add( nameLabel );
		topPanel.add( levelLabel );
		topPanel.add( classLabel );
		topPanel.add( goldLabel );
		topPanel.add( hpLabel );
		topPanel.add( idLabel );
		topPanel.add( stateNameLabel );
		topPanel.add( targetLabel );
		
		
		mainPanel.add( topPanel, "North" );
		mainPanel.add( chatPane, "Center" );
		mainPanel.add( bottomPanel, "South" );

		
		InputMap im = inputArea.getInputMap();
		ActionMap am = inputArea.getActionMap();
		im.put( KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "execute" );
		am.put( "execute", 
			new AbstractAction() 
			{
				public void actionPerformed( ActionEvent e )
				{
					commExec.executeCommand( inputArea.getText().trim() );
					inputArea.setText( "" );
				}
			}
		);
		
		addWindowListener( new WindowAdapter()
			{
				public void windowClosing( WindowEvent e )
				{
					dispose();
				}
				
				public void windowClosed( WindowEvent e )
				{
					System.exit( 0 );
				}
			}
		);		
		setVisible( true );		
		inputArea.requestFocusInWindow();		
	} 
		
	/**
	 * Add new text to the window using the given color	 
	 * @param line The text to add.
	 * @param theColor The color of the text to add
	 */
	public void displayOutput( String line, Color theColor, boolean error )
	throws Exception
	{
		myDisplayWindow.addText( line, theColor, error );	
		repaint();	
	}
	
	/** 
	 * Prints a combat message to the combatWindw window
	 * @param line The message to print
	 * @param theColor The color of the message to print
	 * @param player A boolean value to indicate if this message was generated
	 * by the local player
	 */
	public void printCombatMessage( String line, Color theColor, boolean player )
	throws Exception
	{
		combatWindow.addText( line, theColor, player );
		repaint();
	}
	
	/**
	 * Clears the combat window
	 */
	public void clearCombatWindow()
	{
		combatWindow.setText( "" );
	}	
	
	/**
	 * Clears the primary display window
	 */
	public void clearOutput()
	{
		myDisplayWindow.setText( "" );
	}
	
	/**
	 * Sets the state name label
	 * @param name The name of the state
	 */
	public void setStateName( String name )
	{
		stateNameLabel.setText( name );
		repaint();
	}
	
	/**
	 * Sets this as the active player
	 * @param thePlayer The player to display
	 */
	public void setPlayer( PlayerCharacter thePlayer )
	{
		String[] classes = { "Fighter",  "Mage" };	
		nameLabel.setText( "Name: " + thePlayer.getName() );				
		levelLabel.setText( "Level: " + thePlayer.getLevel() );
		classLabel.setText( "Class: " + classes[thePlayer.getPlayerClass()] );
		goldLabel.setText( "Gold: " + thePlayer.getCurrentGold() );
		idLabel.setText( "Id: " + thePlayer.getId().toString() );
		hpLabel.setText( "HP: " + thePlayer.getHP() + "/" + thePlayer.getMaxHP() );
	}
	
	/**
	 * Sets the target label
	 */
	public void setTarget( String targetName )
	{
		targetLabel.setText( "Target: " + targetName );
	}
	
	/**
	 * Sets the player's status
	 * @param equilibrium True if the player has mental equilbrium
	 * @param balance True if the player has physical balance
	 */
	public void setPlayerStatus( boolean equilibrium, boolean balance )
	{
		if( equilibrium )
			equilLabel.setText( "Mental Equilbrium" );
		else
			equilLabel.setText( "No Mental Equilbrium" );
		
		if( balance )
			balanceLabel.setText( "Physical Balance" );
		else
			balanceLabel.setText( "No Physical Balance" );
	}
	
	/**
	 * This is a private class that define a window that displays text.  It allows
	 * the text to change colors and style
	 */
	private class MudDisplayWindow
 	extends JTextPane
	{
 		private SimpleAttributeSet style;
		private JTextArea myTextArea;
		private JScrollPane myScrollPane;
		
		/**
		 * Constructor
		 */	
		public MudDisplayWindow()
		{
			style = new SimpleAttributeSet();
			StyleConstants.setForeground( style, Color.black );		
			StyleConstants.setFontSize( style, 12 );
			this.setContentType( "text/rtf" );
			this.setEditorKit( new RTFEditorKit() );
			this.setEditable( false );
			setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );	 		
		}
		
		/**
		 * Sets the scroll pane so the addText function can force it
		 * to autoscroll to the bottom when adding new text.
		 * @param theScrollPane The scroll pane associated with the 
		 * window that the addText function adds text to
		 */
		public void setScrollPane( JScrollPane theScrollPane )
		{
			myScrollPane  = theScrollPane;
		}
		
		/**
		 * Adds a string of text to the output using the given color.
		 * @param theMessage The message to display
		 * @param theColor The color of the message to display.
		 */
		public void addText( String theMessage, Color theColor, boolean error )
		throws Exception
		{
			// Save the current style and color values
			Color a3 = StyleConstants.getForeground( style );
			String a2 = StyleConstants.getFontFamily( style );
			
			// Set the style and color values
			StyleConstants.setFontFamily( style, a2 );
			StyleConstants.setForeground( style, theColor );
			
			// This finds the end of the text
			String s = getDocument().getText( 0, getDocument().getLength() );
			int d1 = s.length();
			
			// If this is an error message, bold it
			if( error )
				StyleConstants.setBold( style, true );
			
			// Insert the new text at the end of the current text
			getDocument().insertString( this.getDocument().getLength(),
					    theMessage + "\n", style );
			getDocument().insertString( this.getDocument().getLength(),
					     "", style );
					     
			// Restore the old style and color values, turn off bold text					   
		        StyleConstants.setFontFamily( style, a2);
			StyleConstants.setForeground( style, a3 );
			if( error )
				StyleConstants.setBold( style, false );
			
			// Auto scroll the window to the bottom
			JScrollBar sb =  myScrollPane.getVerticalScrollBar();
			sb.setValue( sb.getMaximum() );
			repaint();
		}
	}	 
}
