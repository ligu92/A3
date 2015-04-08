/******************************************************************************************************************
* File:MaintenanceMonitor.java
* Course: 17655
* Project: Assignment A3
*
* Description:
*
* This class monitors components connected to the message bus. It discovers who is connected by the messages they pass
* and displays their status. Components which do not respond in 2 seconds are labeled as offline, and in need of
* maintenance.
*
* Parameters: IP address of the message manager (on command line). If blank, it is assumed that the message manager is
* on the local machine.
*
* Internal Methods:
*	static private void 
*
******************************************************************************************************************/
import InstrumentationPackage.*;
import MessagePackage.*;
import java.util.*;

class MaintenanceMonitor extends Thread
{
	private final int UNRESPONSIVE_THRESHOLD = 6000;

	private MessageManagerInterface em = null;	// Interface object to the message manager
	private String MsgMgrIP = null;				// Message Manager IP address

	long startTime = System.currentTimeMillis();
	
	boolean Registered = true;					// Signifies that this class is registered with an message manager.
	MessageWindow mw = null;					// This is the message window
	
	ArrayList<Client> participants;				// Internal state used to track the participants we have witnessed.
	ArrayList<Indicator> indicators;			// Internal state used to keep track of indicators for the participants.

	public MaintenanceMonitor()
	{
		// message manager is on the local system

		try
		{
			// Here we create an message manager interface object. This assumes
			// that the message manager is on the local machine

			em = new MessageManagerInterface();

		}

		catch (Exception e)
		{
			System.out.println("MaintenanceMonitor::Error instantiating message manager interface: " + e);
			Registered = false;

		} // catch

	} //Constructor

	public MaintenanceMonitor( String MsgIpAddress )
	{
		// message manager is not on the local system

		MsgMgrIP = MsgIpAddress;

		try
		{
			// Here we create an message manager interface object. This assumes
			// that the message manager is NOT on the local machine

			em = new MessageManagerInterface( MsgMgrIP );
		}

		catch (Exception e)
		{
			System.out.println("MaintenanceMonitor::Error instantiating message manager interface: " + e);
			Registered = false;

		} // catch

	} // Constructor

	public void run()
	{
		Message Msg = null;				// Message object
		MessageQueue eq = null;			// Message Queue
		int MsgId = 0;					// User specified message ID
		
		//Initialize the internal state - we're tracking which participants we've seen,
		//and we have an indicator for each of them to display on the GUI.
		participants = new ArrayList<Client>();
		indicators = new ArrayList<Indicator>();
		
		int	Delay = 500;				// The loop delay (0.5 seconds)
		boolean Done = false;			// Loop termination flag

		if (em != null)
		{
			// Now we create the Maintenance status and message panel.
			// We will dynamically create indicators for devices connected to the
			// message bus as we discover them, and put them on the panel.
			// This panel is placed in the upper right hand corner and the status
			// indicators are placed directly below it.

			mw = new MessageWindow("Maintenance Monitoring Console", 0.5f, 0);

			mw.WriteMessage( "Registered with the message manager." );

	    	try
	    	{
				mw.WriteMessage("   Participant id: " + em.GetMyId() );
				mw.WriteMessage("   Registration Time: " + em.GetRegistrationTime() );

			} // try

	    	catch (Exception e)
			{
				System.out.println("Error:: " + e);

			} // catch

			/********************************************************************
			** Here we start the main simulation loop
			*********************************************************************/

			while ( !Done )
			{
				// Here we get our message queue from the message manager

				try
				{
					eq = em.GetMessageQueue();

				} // try

				catch( Exception e )
				{
					mw.WriteMessage("Error getting message queue::" + e );

				} // catch

				// If there are messages in the queue, we read through them.
				int qlen = eq.GetSize();

				for ( int i = 0; i < qlen; i++ )
				{
					Msg = eq.GetMessage();

					//Check the message's sender. 
					boolean newSender = true;
					for (int c = 0; c <= participants.size() - 1; c++){
						if (participants.get(c).getID() == Msg.GetSenderId()){
							//If they're not a new sender, update the corresponding array element's last time seen field.
							participants.get(c).updateTime();
							//Mark their indicator as OK, with a green color.
							participants.get(c).getIndicator().SetLampColor(1);
							newSender = false;
						}
					}
					if (newSender){
						//Add the new participant to the array. Assign the sender ID to be the component's ID.			
						participants.add(new Client(System.currentTimeMillis(), Msg.GetSenderId(), Msg.GetMessageId()));
						
						//Create a new indicator for the new participant, and assign it to that participant.
						indicators.add(new Indicator(participants.get(participants.size() - 1).getComponentType() + "\nID:" +  Msg.GetSenderId(), (50 * (indicators.size() % (3))), (20 * indicators.size()) % 500, 1));
						participants.get(participants.size() - 1).setIndicator(indicators.get(indicators.size() - 1));
					}
									
					// If the message ID == 99 then this is a signal that the simulation
					// is to end. At this point, the loop termination flag is set to
					// true and this process unregisters from the message manager.

					if ( Msg.GetMessageId() == 99 )
					{
						Done = true;

						try
						{
							em.UnRegister();

				    	} // try

				    	catch (Exception e)
				    	{
							mw.WriteMessage("Error unregistering: " + e);

				    	} // catch

				    	mw.WriteMessage( "\n\nSimulation Stopped. \n");

					} // if

				} // for

				//Check if any participants have timed out, since we won't have updated their status.
				//Iterate through all components in the array.
				for (int i = 0; i <= participants.size() - 1; i++){
					//Check when we last saw them. If it's been longer than 2 seconds, make an alert and change their indicator to reflect an error.
					if (System.currentTimeMillis() - participants.get(i).getLastMessageTime() > UNRESPONSIVE_THRESHOLD){
						mw.WriteMessage(participants.get(i).getComponentType() 
						+ " with ID: " 
						+ participants.get(i).getID() 
						+ " has not responded for " 
						+ Math.abs((participants.get(i).getLastMessageTime() - System.currentTimeMillis())) 
						+ "ms and is in need of maintenance.");
						
						participants.get(i).getIndicator().SetLampColor(3);
					}
				}
				
				// This delay slows down the sample rate to Delay mi(lliseconds
				try
				{
					Thread.sleep( Delay );

				} // try

				catch( Exception e )
				{
					System.out.println( "Sleep error:: " + e );

				} // catch

			} // while

		} else {

			System.out.println("Unable to register with the message manager.\n\n" );

		} // if

	} // main

	/***************************************************************************
	* CONCRETE METHOD:: IsRegistered
	* Purpose: This method returns the registered status
	*
	* Arguments: none
	*
	* Returns: boolean true if registered, false if not registered
	*
	* Exceptions: None
	*
	***************************************************************************/

	public boolean IsRegistered()
	{
		return( Registered );

	} // IsRegistered


	/***************************************************************************
	* CONCRETE METHOD:: Halt
	* Purpose: This method posts an message that stops the environmental control
	*		   system.
	*
	* Arguments: none
	*
	* Returns: none
	*
	* Exceptions: Posting to message manager exception
	*
	***************************************************************************/

	public void Halt()
	{
		mw.WriteMessage( "***HALT MESSAGE RECEIVED - SHUTTING DOWN SYSTEM***" );

		// Here we create the stop message.

		Message msg;

		msg = new Message( (int) 99, "XXX" );

		// Here we send the message to the message manager.

		try
		{
			em.SendMessage( msg );

		} // try

		catch (Exception e)
		{
			System.out.println("Error sending halt message:: " + e);

		} // catch

	} // Halt


} // MaintenanceMonitor