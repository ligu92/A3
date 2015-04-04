/******************************************************************************************************************
* File:SecurityConsole.java
* Course: 17655
* Project: Assignment 3
*
* Description: This class is the console for the museum security control system. 
* This process consists of two threads. The SecurityMonitor object is a thread 
* that is started that is responsible for the monitoring and control of the museum 
* security systems. The main thread provides a text interface for the user to  
* arm/disarm the three different alarms (window, door, motion), as well as shut down the system.
* In addition, to simulate actual intrusions, this console allows the user to
* manually trigger the alarms (window, door, motion).
*
* Parameters: None
*
* Internal Methods: None
*
******************************************************************************************************************/
import TermioPackage.*;
import MessagePackage.*;

public class SecurityConsole
{
	public static void main(String args[])
	{
    	Termio UserInput = new Termio();	// Termio IO Object
		boolean Done = false;				// Main loop flag
		String Option = null;				// Menu choice from user
		Message Msg = null;					// Message object
		boolean Error = false;				// Error flag
		SecurityMonitor Monitor = null;		// The environmental control system monitor
		
		// These parameters allow the console to internally keep track of
		// whether the system is armed and which alarms have been triggered
		boolean alarms_armed = false;
		boolean window_break = false;
		boolean door_break = false;
		boolean motion_detected = false;

		/////////////////////////////////////////////////////////////////////////////////
		// Get the IP address of the message manager
		/////////////////////////////////////////////////////////////////////////////////

 		if ( args.length != 0 )
 		{
			// message manager is not on the local system

			Monitor = new SecurityMonitor( args[0] );

		} else {

			Monitor = new SecurityMonitor();

		} // if


		// Here we check to see if registration worked. If ef is null then the
		// message manager interface was not properly created.

		if (Monitor.IsRegistered() )
		{
			Monitor.start(); // Here we start the monitoring and control thread

			while (!Done)
			{
				// Here, the main thread continues and provides the main menu

				System.out.println( "\n\n\n\n" );
				System.out.println( "Security Monitoring Command Console: \n" );

				if (args.length != 0)
					System.out.println( "Using message manger at: " + args[0] + "\n" );
				else
					System.out.println( "Using local message manger \n" );

				// Gives the user options to interact with the system
				System.out.println( "Select an Option: \n" );
				System.out.println( "1: Arm security alarms" );
				System.out.println( "2: Disarm security alarms" );
				System.out.println( "3: Trigger a window break" );
				System.out.println( "4: Trigger a door break" );
				System.out.println( "5: Trigger a motion detecton" );
				System.out.println( "X: Stop System\n" );
				System.out.print( "\n>>>> " );
				Option = UserInput.KeyboardReadString();

				//////////// option 1 ////////////

				if ( Option.equals( "1" ) ) {
					// Here we arm the system if it's not armed, otherwise
					// we just notify the user and do nothing.
					if (!alarms_armed) {
						alarms_armed = true;
						Monitor.SetArmedStatus(true);
					}
					else {
						System.out.println("Alarms are already armed.");
					}
				} // if

				//////////// option 2 ////////////

				if ( Option.equals( "2" ) ) {
					// Here we disarm the system if it's armed, otherwise
					// we just notify the user and do nothing.
					if (alarms_armed) {
						alarms_armed = false;
						Monitor.SetArmedStatus(false);
					}
					else {
						System.out.println("Alarms are already disarmed.");
					}
				} // if

				//////////// option X ////////////

				if ( Option.equalsIgnoreCase( "X" ) )
				{
					// Here the user is done, so we set the Done flag and halt
					// the environmental control system. The monitor provides a method
					// to do this. Its important to have processes release their queues
					// with the message manager. If these queues are not released these
					// become dead queues and they collect messages and will eventually
					// cause problems for the message manager.

					Monitor.Halt();
					Done = true;
					System.out.println( "\nConsole Stopped... Exit monitor mindow to return to command prompt." );
					Monitor.Halt();

				} // if

			} // while

		} else {

			System.out.println("\n\nUnable to start the monitor.\n\n" );

		} // if

  	} // main

} // ECSConsole
