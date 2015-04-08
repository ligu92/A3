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
import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class SecurityConsole
{
	static String Option = null;				// Menu choice from user
	//timer for sprinkler
	static Thread t=null;
	static Thread t1=null;
	static Thread tSprinklerTurnoff=null;
	static long promptStartTime=-1;
	static long elapsedTime=0;
	static String selection="No selection";
	static JOptionPane jp=new JOptionPane("Start sprinkler or Stop the Fire alarm if it is false alarm\n Default sprinkle action in 10sec", JOptionPane.QUESTION_MESSAGE,JOptionPane.YES_NO_OPTION);
	static JDialog dialog = jp.createDialog("Sprinkler!");
	//Stop sprinkler option also stops fire
	static JOptionPane sprinklerStopAlert=new JOptionPane("\n Stop Sprinkler\n This will also stop fire", JOptionPane.QUESTION_MESSAGE,JOptionPane.OK_CANCEL_OPTION);
	static JDialog sprinklerStopDialog = sprinklerStopAlert.createDialog("Sprinkle Stop!");
	//static String sprinkleOffSelection="No selection";
	static boolean sprinklerTurnoffPrompt=false;
	static boolean startprompting=false;
	static boolean promptingStarted=false;
	static boolean fire_detect = false;
	static boolean sprinkler_start = false;
	static boolean Done = false;

	static SecurityMonitor Monitor = null;		// The environmental control system monitor

	public static void main(String args[])
	{
    	Termio UserInput = new Termio();	// Termio IO Object
		boolean Done = false;				// Main loop flag
		String Option = null;				// Menu choice from user
		Message Msg = null;					// Message object
		boolean Error = false;				// Error flag
		
		
		// These parameters allow the console to internally keep track of
		// whether the system is armed and which alarms have been triggered
		boolean alarms_armed = false;
		boolean window_break = false;
		boolean door_break = false;
		boolean motion_detected = false;

		// These parameters allow the console to internally keep track of
		// whether the system is armed and which alarms have been triggered
		//boolean alarms_armed = false;
		boolean reset = false;

		sprinklerStopDialog.setVisible(false);

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
				System.out.println( "6: Fire Detected" );
				System.out.println( "7: Reset and Stop sprinkler" );
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
					// Disarming the alarm also sets everything intrusion
					// detector to be status ok
					if (alarms_armed) {
						alarms_armed = false;
						window_break = false;
						door_break = false;
						motion_detected = false;
						Monitor.SetArmedStatus(false);
						Monitor.SetWindowStatus(false);
						Monitor.SetDoorStatus(false);
						Monitor.SetMotionStatus(false);
					}
					else {
						System.out.println("Alarms are already disarmed.");
					}
				} // if

				//////////// option 3 ////////////

				if ( Option.equals( "3" ) ) {
					// Here we trigger the window break alarm for testing
					if (!window_break) {
						window_break = true;
						Monitor.SetWindowStatus(true);
					}
					else {
						System.out.println("Window already broken.");
					}
				} // if

				//////////// option 4 ////////////

				if ( Option.equals( "4" ) ) {
					// Here we trigger the window break alarm for testing
					if (!door_break) {
						door_break = true;
						Monitor.SetDoorStatus(true);
					}
					else {
						System.out.println("Door already broken.");
					}
				} // if

				//////////// option 5 ////////////

				if ( Option.equals( "5" ) ) {
					// Here we trigger the window break alarm for testing
					if (!motion_detected) {
						motion_detected = true;
						Monitor.SetMotionStatus(true);
					}
					else {
						System.out.println("Motion already detected.");
					}
				} // if

				//////////// option 6 ////////////

				if ( Option.equals( "6" ) ) {
					// Here we trigger the fire detect alarm for testing
					if (!fire_detect) {
						fire_detect = true;
						Monitor.SetFireStatus(true);
						if(!promptingStarted){
						    startPrompt();
						    promptingStarted=false;
						}
						startprompting=true;
					}
					else {
						System.out.println("Fire already detected.");
					}
				} // if
				
				if ( Option.equals( "7" ) ) {
					// Resetting
					if (fire_detect) {
						fire_detect = false;
						Monitor.SetFireStatus(false);
					}
					else {
						System.out.println("Fire already Off.");
					}
					if (sprinkler_start) {
						sprinkler_start = false;
						Monitor.SetSprinklerStatus(false);
					}
					else {
						System.out.println("Sprinkler already Off.");
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

  	public static void startPrompt(){
		t = new Thread(new Runnable() {
			public void run() {
				if(promptStartTime==-1 && !promptingStarted){
					promptStartTime=System.currentTimeMillis();
				}
				while(true){
					if(selection!=null && selection.equalsIgnoreCase("0")){//Sprinkle start yes
						if (!sprinkler_start) {
							sprinkler_start = true;
							Monitor.SetSprinklerStatus(true);
						}
						else {
							//System.out.println("Sprinkler already on.");
						}
						dialog.setVisible(false);
						sprinklerTurnoffPrompt=true;
						selection="No selection";
						break;
					}
					if(selection!=null && selection.equalsIgnoreCase("1")){//Sprinkle start no indicating false alarm
						if (sprinkler_start) {
							sprinkler_start = false;
							Monitor.SetSprinklerStatus(false);
						}
						else {
							//System.out.println("Sprinkler already off.");
						}
						
						dialog.setVisible(false);
						selection="No selection";
						break;
					}
					elapsedTime=(System.currentTimeMillis()-promptStartTime);
					if(elapsedTime>=10000){//Make default sprinkle action
						if (!sprinkler_start && jp.getValue()!=null) {//ensures close button press
							sprinkler_start = true;
							Monitor.SetSprinklerStatus(true);
						}
						else {
							//System.out.println("Sprinkler already on.");
						}
						dialog.setVisible(false);
						selection="No selection";
						//sprinklerTurnoffPrompt=true;
						break;
					}
				}
				promptStartTime=-1;
			}
		});
		t.start();
		//create thread for input taking
		t1 = new Thread(new Runnable() {
			public void run() {
				dialog.setVisible(true);
				while(true){
				 if(jp.getValue() != null)
		          selection=jp.getValue().toString();
				 
		          if(selection!=null && selection.equalsIgnoreCase("0")){//Sprinkle start yes
						break;
					}
					if(selection!=null && selection.equalsIgnoreCase("1")){//Sprinkle start no indicating false alarm
						break;
					}
					if(elapsedTime>=10000){
						//dialog.setVisible(false);
						break;
					}	          
		 		    }//while
			}
		});
		t1.start();
	}

} // ECSConsole
