/******************************************************************************************************************
* File:SecurityConsole.java
* Course: 17655
* Project: Assignment 3
*
* Description: This class is the console for the museum maintenance control system. 
* This process consists of two threads. The MaintenanceMonitor object is a thread 
* that is started that is responsible for the monitoring the museum 
* systems. The main thread provides a text interface for the user to  
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
		MaintenanceMonitor Monitor = null;		// The environmental control system monitor

		/////////////////////////////////////////////////////////////////////////////////
		// Get the IP address of the message manager
		/////////////////////////////////////////////////////////////////////////////////

 		if ( args.length != 0 )
 		{
			// message manager is not on the local system

			Monitor = new MaintenanceMonitor( args[0] );

		} else {

			Monitor = new MaintenanceMonitor();

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
				System.out.println( "Maintenance Monitor Command Console: \n" );

				if (args.length != 0)
					System.out.println( "Using message manger at: " + args[0] + "\n" );
				else
					System.out.println( "Using local message manger \n" );

				// Gives the user options to interact with the system
				System.out.println( "Select an Option: \n" );
				System.out.println( "X: Stop System\n" );
				System.out.print( "\n>>>> " );
				Option = UserInput.KeyboardReadString();


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
