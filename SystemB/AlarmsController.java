/******************************************************************************************************************
* File:AlarmsController.java
* Course: 17655
* Project: Assignment A3
*
* Description:
*
* This class simulates a multiple alarm that has a window break detector, a door break
* detector, and a motion detector. 
* 
* It polls the message bus for message ID 6, which tells it to arm/disarm itself. 
*	Arm = arm alarms
*	Disarm = disarm alarms

* It also polls for the ID 7, which tells it that a certain alarm is triggered
*	Window = window break
*	Door = door break
*	Motion = motion detected
*	
* It sends an alarm status message with ID -6. This is a four-character message. 
* The first char is 0(disarmed)/1(armed), the second char is 0(window ok)/1(window break),
* the third char is 0(door ok)/1(door break), the fourth char is 0(no motion)/1(motion).
* This message also serves as a heartbeat message so the monitoring console can use it
* to check if the alarms on online/offline
*
* Parameters: IP address of the message manager (on command line). If blank, it is assumed that the message manager is
* on the local machine.
*
* Internal Methods:
*	static private void ConfirmMessage(MessageManagerInterface ei, String m )
*
******************************************************************************************************************/
import InstrumentationPackage.*;
import MessagePackage.*;
import java.util.*;

class AlarmsController
{
	public static void main(String args[])
	{
		String MsgMgrIP;					// Message Manager IP address
		Message Msg = null;					// Message object
		MessageQueue eq = null;				// Message Queue
		int MsgId = 0;						// User specified message ID
		MessageManagerInterface em = null;	// Interface object to the message manager

		boolean ArmedState = false;			// Alarms armed: false == disarmed, true == armed
		boolean WindowState = false;		// Windows: false == ok, true == broken
		boolean DoorState = false;			// Doors: false == ok, true == broken
		boolean MotionState = false;		// Motion: false == no motion, true == motion

		StringBuilder sendMsg = new StringBuilder("0000");

		int	Delay = 1000;					// The loop delay (1 second)
		boolean Done = false;				// Loop termination flag

		/////////////////////////////////////////////////////////////////////////////////
		// Get the IP address of the message manager
		/////////////////////////////////////////////////////////////////////////////////

 		if ( args.length == 0 )
 		{
			// message manager is on the local system

			System.out.println("\n\nAttempting to register on the local machine..." );

			try
			{
				// Here we create an message manager interface object. This assumes
				// that the message manager is on the local machine

				em = new MessageManagerInterface();
			}

			catch (Exception e)
			{
				System.out.println("Error instantiating message manager interface: " + e);

			} // catch

		} else {

			// message manager is not on the local system

			MsgMgrIP = args[0];

			System.out.println("\n\nAttempting to register on the machine:: " + MsgMgrIP );

			try
			{
				// Here we create an message manager interface object. This assumes
				// that the message manager is NOT on the local machine

				em = new MessageManagerInterface( MsgMgrIP );
			}

			catch (Exception e)
			{
				System.out.println("Error instantiating message manager interface: " + e);

			} // catch

		} // if

		// Here we check to see if registration worked. If em is null then the
		// message manager interface was not properly created.

		if (em != null)
		{
			System.out.println("Registered with the message manager." );

			/* Now we create the alarm control status and message panel
			** We put this panel about 1/3 the way down the terminal, aligned to the left
			** of the terminal. */

			float WinPosX = 0.0f; 	//This is the X position of the message window in terms
									//of a percentage of the screen height
			float WinPosY = 0.33f;	//This is the Y position of the message window in terms
								 	//of a percentage of the screen height

			MessageWindow mw = new MessageWindow("Alarm Controller Status", WinPosX, WinPosY);
			mw.WriteMessage("Registered with the message manager." );

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
				try
				{
					eq = em.GetMessageQueue();

				} // try

				catch( Exception e )
				{
					mw.WriteMessage("Error getting message queue::" + e );

				} // catch

				// If there are messages in the queue, we read through them.
				// We are looking for MessageIDs = 6 or 7, see the top of the code
				// for details
				int qlen = eq.GetSize();

				for ( int i = 0; i < qlen; i++ )
				{
					Msg = eq.GetMessage();

					if ( Msg.GetMessageId() == 6 )
					{
						if (Msg.GetMessage().equalsIgnoreCase("Arm")) // arm yourself
						{
							ArmedState = true;
							mw.WriteMessage("Received arm message" );

							// Confirm that the message was recieved and acted on
							sendMsg.setCharAt(0, '1');
							ConfirmMessage( em, sendMsg.toString() );

						} // if

						if (Msg.GetMessage().equalsIgnoreCase("Disarm")) // disarm in peace
						{
							ArmedState = false;
							mw.WriteMessage("Received disarm message" );

							// Confirm that the message was recieved and acted on
							sendMsg.setCharAt(0, '0');
							sendMsg.setCharAt(1, '0');
							sendMsg.setCharAt(2, '0');
							sendMsg.setCharAt(3, '0');
							ConfirmMessage( em, sendMsg.toString() );

						} // if
					} // if

					if ( Msg.GetMessageId() == 7 )
					{
						if (Msg.GetMessage().equalsIgnoreCase("Window")) // broken window
						{
							WindowState = true;
							mw.WriteMessage("Received window break" );

							// Confirm that the message was recieved and acted on
							sendMsg.setCharAt(1, '1');
							ConfirmMessage( em, sendMsg.toString() );

						} // if

						if (Msg.GetMessage().equalsIgnoreCase("Door")) // broken door
						{
							DoorState = false;
							mw.WriteMessage("Received door break" );

							// Confirm that the message was recieved and acted on
							sendMsg.setCharAt(2, '1');
							ConfirmMessage( em, sendMsg.toString() );

						} // if

						if (Msg.GetMessage().equalsIgnoreCase("Motion")) // motion detected
						{
							ArmedState = false;
							mw.WriteMessage("Received motion detection" );

							// Confirm that the message was recieved and acted on
							sendMsg.setCharAt(3, '1');
							ConfirmMessage( em, sendMsg.toString() );

						} // if
					} // if

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

					// Send hearbeat even if nothing
					ConfirmMessage( em, sendMsg.toString() );

				} // for
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
	* CONCRETE METHOD:: ConfirmMessage
	* Purpose: This method posts the specified message to the specified message
	* manager. This method assumes an message ID of -4 which indicates a confirma-
	* tion of a command.
	*
	* Arguments: MessageManagerInterface ei - this is the messagemanger interface
	*			 where the message will be posted.
	*
	*			 string m - this is the received command.
	*
	* Returns: none
	*
	* Exceptions: None
	*
	***************************************************************************/

	static private void ConfirmMessage(MessageManagerInterface ei, String m )
	{
		// Here we create the message.

		Message msg = new Message( -6, m );

		// Here we send the message to the message manager.

		try
		{
			ei.SendMessage( msg );

		} // try

		catch (Exception e)
		{
			System.out.println("Error Confirming Message:: " + e);

		} // catch

	} // PostMessage

} // HumidityControllers