 package m2mimud.game;
 import javax.swing.*;
 import java.awt.*;
 import java.awt.event.*;
 import m2mimud.communications.Game;
 import m2mimud.communications.SessionAd;

 /**
  * The game chooser window is the window which displays the available session.
  *
  * @author Robert Whitcomb
  * @version $Id: GameChooserWindow.java,v 1.3 2005/01/12 14:03:17 rjw2183 Exp $
  *
  *
  */

 public class GameChooserWindow
 extends JFrame
 implements GameChooserListener
 {
 	private JScrollPane scrollPane;
	private JButton joinButton;
	private JButton cancelButton;
	private JList sessionList;
	private GameChooser myGC;
	private GCWindowListener myListener;
	private SessionAd myAd;
	
	/**
	 * Creates a new GameChooser window
	 * @param gc The GameChooser object that uses this window to display its data
	 * @param listener  A listener that waits for the window to inform what session was selected
	 */
	 public GameChooserWindow( GameChooser gc, GCWindowListener listener )
	 {
	 	super( "Select a session to join." );
		myGC = gc;
		myListener = listener;
		sessionList = new JList();
		myGC.setJList( sessionList );
		sessionList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder( BorderFactory.createEmptyBorder
		 (10, 10, 10, 10 ) );
		mainPanel.setLayout( new BoxLayout( mainPanel, BoxLayout.Y_AXIS ) );
		scrollPane = new JScrollPane( sessionList, 
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		scrollPane.setAlignmentX( 0.0f );
		mainPanel.add( scrollPane );
		mainPanel.add( Box.createVerticalStrut( 10 ) );
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout( new BoxLayout( buttonPanel, BoxLayout.X_AXIS ) );
		buttonPanel.setAlignmentX( 0.0f );
		joinButton = new JButton( "Join" );
		joinButton.addActionListener(
			new ActionListener()
			{
				public void actionPerformed( ActionEvent e )
				{	
					setVisible( false );
					myListener.sessionSelected( myAd );
					myGC.purge();
					dispose();				
				}
			}
		);
		cancelButton = new JButton( "Cancel" );
		cancelButton.addActionListener(
			new ActionListener()
			{
				public void actionPerformed( ActionEvent e )
				{
					setVisible( false );
					myListener.sessionSelected( null );
					myGC.purge();
					dispose();
				}
			}
		);
				
			
		buttonPanel.add( joinButton );
		buttonPanel.add( cancelButton );
		
		mainPanel.add( buttonPanel );
		
		getContentPane().add( mainPanel );
		setSize( 400, 400 );
		myGC.setListener( this );
	 }
	 
	 /**
	  * When a session has been selected in the GameChooser, this is called
	  * so that the window can report back the correct session
	  * @param theAd The sessionad that was selected
	  */
	 public void gameSelected( SessionAd theAd )
	 {
	 	myAd = theAd;
	 }
 }
