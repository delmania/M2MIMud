 package m2mimud.game;
 import java.util.Vector; 
 import javax.swing.AbstractListModel;
 import javax.swing.JList;
 import javax.swing.event.ListSelectionEvent;
 import javax.swing.event.ListSelectionListener;
 import m2mimud.communications.GameDiscoveryListener;
 import m2mimud.communications.Game;
 import m2mimud.communications.SessionAd;
 import m2mimud.state.GameState;

 /**
  * The Gamechooser object is the one that maintains the list available
  * games sessions.
  *
  * @author Robert Whitcomb
  * @version $Id: GameChooser.java,v 1.4 2005/01/12 14:03:17 rjw2183 Exp $
  *
  */
 
 public class GameChooser
 extends AbstractListModel
 implements GameDiscoveryListener, ListSelectionListener
 {
 
 	// Vector of Game data objects
	private Vector sessions;
	
	// Vector of multihandles to the sessions
	private Vector handles;
	
	// The currently selected session
	private Game currentSelected;
	
	private JList list;
	
	//The listener
	private GameChooserListener myListener;
	
	/**                                                                                              

	 * Create a new GameChooser Object
	 */
	public GameChooser()
	{
		sessions = new Vector();
		handles = new Vector();
		currentSelected = null;
		list = null;
		
		handles.add( null );
		sessions.add( null );
	}
	
	/**
	 * Sets the listener for this object
	 * @param list The listener
	 */
	public void setListener( GameChooserListener list )
	{
		myListener = list;
	}
	
	/**
	 * Associates a Jlist with this object
	 * @param nlist the Jlist object used to store data
	 */
	public synchronized void setJList( JList nlist )
	{
		list = nlist;
		if( list != null )
		{
			list = nlist;
			list.setModel( this );
			list.addListSelectionListener( this );
			list.setSelectedIndex( 0 );
		}
	}

	/**
	 * Notify this that a sesson has been added
	 *
	 * @param theSession The SessionAd that contains information
	 * about the session to add.
	 */
	public void newSessionAdded( SessionAd theSession )                                                                                              
	{		
		int i = handles.size();		
		handles.add( theSession.sessionHandle );		
		sessions.add( theSession );				
		fireIntervalAdded( this, i, i );
	}
	
	/**
	 * Notify that a session has left.
	 *
	 * @param handle The handle of the session which left.
	 */
	public void sessionLeft( Game handle )
	{
	 	int i = handles.indexOf( handle );
		if( i >= 0 )
		{
			handles.removeElementAt( i );
			sessions.removeElementAt( i );
			fireIntervalRemoved( this, i, i );
		}
	}
		
	/**
	 * Notify that the session's count has changed
	 *
	 * @param handle The handle to the game
	 * @param newCount The new count
	 */
	 public void sessionCountChanged( Game handle, int newCount )
	 {
	 	int i = handles.indexOf( handle );
		if( i >= 0 )
		{
			SessionAd temp = (SessionAd)sessions.elementAt( i );
			temp.sessionCount = newCount;
			sessions.setElementAt( temp, i );
			fireContentsChanged( this, i, i );
		}
	}
	
	/**
	 * Informs the Jlist that a session's state has changed
	 * @param handle The handle to the session whose state has changed
	 * @param newState The new state of the session
	 */
	public void sessionStateChanged( Game handle, GameState newState )
	{
		int i = handles.indexOf( handle );
		if( i >= 0 )
		{
			SessionAd temp = (SessionAd)sessions.elementAt( i );
			temp.sessionState = newState;
			sessions.setElementAt( temp, i );
			fireContentsChanged( this, i, i );
		}
	}
	
	/**
	 * Return the count
	 */
	 public synchronized int getSize()
	 {
	 	return sessions.size();
	 }
	
	 /**
	  * Return the element at the given index
	  * @param i The index
	  */
	  public synchronized Object getElementAt( int i )
	  {
	  	return sessions.elementAt( i );
	  } 
		
		
	/**
	 * Report that the selection has changed
	 */
	public synchronized void valueChanged( ListSelectionEvent e)
	{
		String name =  null;
		Game handle = null;
		int count = 0;
		int i = 0;
		if( list != null )
		{
			i = list.getSelectedIndex();			
			if( i == -1 )
			{
				list.ensureIndexIsVisible( 0 );
				list.setSelectedIndex( 0 );
			}
			else if( i == 0 )
			{
				name = null;
				handle = null;
			}
			else
			{
				if( i < sessions.size() )
				{
					SessionAd temp = (SessionAd)sessions.elementAt( i );
					name = temp.sessionName;
					count = temp.sessionCount;				
					handle = (Game)handles.elementAt( i );
				}
			}
			
			if( (currentSelected == null && handle != null ) ||
			    (currentSelected != null && !currentSelected.equals( handle ) ) )
			{
				currentSelected = handle;	
				int j = handles.indexOf( currentSelected );
				if( j >= 0 )
				{
					SessionAd temp = (SessionAd) sessions.elementAt( j );					
					if( myListener != null )
						myListener.gameSelected( temp );
				}		
			}
						  
		}
	} 
	
		
	/**
	 * Set the given session.
	 * @param session The session to select
	 */
	 public synchronized void selectSession( Game session )
	 {
	 	int i = 0;
		if( session != null )
			i = handles.indexOf( session );
		
		if( i >= 0 )
		{
			list.ensureIndexIsVisible( i );
			list.setSelectedIndex( i );
		}
	}
	
	/**
	 * Purges the lists.
	 */
	public void purge()
	{
		sessions.clear();
		handles.clear();
	}
}
		
			
		
	
