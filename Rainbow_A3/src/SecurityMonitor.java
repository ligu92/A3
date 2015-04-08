/******************************************************************************************************************
 * File:SecurityMonitor.java
 * Course: 17655
 * Project: Assignment A3
 *
 * Description:
 *
 * This class monitors the alarms that detect window breaks, door breaks, and motion. 
 * The monitor also allows the user to disarm/arm the alarm. When the alarms are
 * disarmed, any intrusions are still detected, but are ignored by the security console.
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

import javax.swing.JDialog;
import javax.swing.JOptionPane;

class SecurityMonitor extends Thread {
	private MessageManagerInterface em = null; // Interface object to the
												// message manager
	private String MsgMgrIP = null; // Message Manager IP address
	JOptionPane pane = new JOptionPane("Sprinkles within 10sec",
			JOptionPane.YES_NO_OPTION);
	// These parameters allow the console to internally keep track of
	// whether the system is armed and which alarms have been triggered
	boolean alarms_armed = false;
	boolean window_break = false;
	boolean door_break = false;
	boolean motion_detected = false;
	boolean fire_detect = false;
	boolean sprinkler_start = false;

	// Sprinkler user action prompting thread and the selection
	Thread t = null;
	int selection = 11;
	// Sprinkler prompt start time
	long promptStartTime = -1;

	long startTime = System.currentTimeMillis();

	boolean Registered = true; // Signifies that this class is registered with
								// an message manager.
	MessageWindow mw = null; // This is the message window
	Indicator ai; // Armed indicator
	Indicator wi; // Window break indicator
	Indicator di; // Door break indicator
	Indicator mi; // Motion detector indicator
	Indicator fi; // fire indicator
	Indicator si; // sprinkler indicator

	public SecurityMonitor() {
		// message manager is on the local system

		try {
			// Here we create an message manager interface object. This assumes
			// that the message manager is on the local machine

			em = new MessageManagerInterface();

		}

		catch (Exception e) {
			System.out
					.println("SecurityMonitor::Error instantiating message manager interface: "
							+ e);
			Registered = false;

		} // catch

	} // Constructor

	public SecurityMonitor(String MsgIpAddress) {
		// message manager is not on the local system

		MsgMgrIP = MsgIpAddress;

		try {
			// Here we create an message manager interface object. This assumes
			// that the message manager is NOT on the local machine

			em = new MessageManagerInterface(MsgMgrIP);
		}

		catch (Exception e) {
			System.out
					.println("SecurityMonitor::Error instantiating message manager interface: "
							+ e);
			Registered = false;
		} // catch

	} // Constructor

	public void run() {
		Message Msg = null; // Message object
		MessageQueue eq = null; // Message Queue
		int MsgId = 0; // User specified message ID

		int Delay = 1000; // The loop delay (1 seconds)
		boolean Done = false; // Loop termination flag
		boolean ON = true; // Used to turn on heaters, chillers, humidifiers,
							// and dehumidifiers
		boolean OFF = false; // Used to turn off heaters, chillers, humidifiers,
								// and dehumidifiers

		boolean alarmControllerAlive = true;

		if (em != null) {
			// Now we create the Security status and message panel
			// Note that we set up three indicators that are initially green.
			// This is
			// because the alarms are initialized to be disarmed. The exception
			// is the indicator that shows armed status, it is initially red
			// because the alarms are initially disarmed.
			// This panel is placed in the upper right hand corner and the
			// status
			// indicators are placed directly below it.

			mw = new MessageWindow("Security Monitoring Console", 0.5f, 0);
			ai = new Indicator("DISARMED", mw.GetX(), mw.GetY() + mw.Height(),
					3);
			wi = new Indicator("WIN OK", ai.GetX() + ai.Width() + 1, mw.GetY()
					+ mw.Height(), 1);
			di = new Indicator("DOOR OK", wi.GetX() + wi.Width() + 1, mw.GetY()
					+ mw.Height(), 1);
			mi = new Indicator("NO MOTION", di.GetX() + di.Width() + 1,
					mw.GetY() + mw.Height(), 1);
			fi = new Indicator("NoFire", mi.GetX() + mi.Width() + 1, mw.GetY()
					+ mw.Height(), 1);
			si = new Indicator(" SprinklerOFF", fi.GetX() + fi.Width() + 1,
					mw.GetY() + mw.Height(), 3);
			mw.WriteMessage("Registered with the message manager.");

			try {
				mw.WriteMessage("   Participant id: " + em.GetMyId());
				mw.WriteMessage("   Registration Time: "
						+ em.GetRegistrationTime());

			} // try

			catch (Exception e) {
				System.out.println("Error:: " + e);

			} // catch

			/********************************************************************
			 ** Here we start the main simulation loop
			 *********************************************************************/

			while (!Done) {
				// Here we get our message queue from the message manager

				try {
					eq = em.GetMessageQueue();

				} // try

				catch (Exception e) {
					mw.WriteMessage("Error getting message queue::" + e);

				} // catch

				// If there are messages in the queue, we read through them.
				// We are looking for MessageIDs = 6. Message IDs of 6 are
				// alarm status messages

				int qlen = eq.GetSize();

				for (int i = 0; i < qlen; i++) {
					Msg = eq.GetMessage();

					if (Msg.GetMessageId() == -6) // Alarm status reading
					{
						startTime = System.currentTimeMillis();
						try {
							// Parse the alarm status message to determine if
							// the alarms are armed and to determine if the
							// individual
							// intrusions have been detected
							String alarm_msg = Msg.GetMessage();
							if (!alarms_armed && alarm_msg.charAt(0) == '1') {
								Arm(false);
							} else if (alarms_armed
									&& alarm_msg.charAt(0) == '0') {
								Arm(true);
							}
							if (alarm_msg.charAt(1) == '1') {
								window_break = true;
							} else {
								window_break = false;
							}
							if (alarm_msg.charAt(2) == '1') {
								door_break = true;
							} else {
								door_break = false;
							}
							if (alarm_msg.charAt(3) == '1') {
								motion_detected = true;
							} else {
								motion_detected = false;
							}

						} // try

						catch (Exception e) {
							mw.WriteMessage("Error reading alarm status: " + e);

						} // catch

					} // if

					if (Msg.GetMessageId() == -8) // Fire Alarm status reading
					{
						startTime = System.currentTimeMillis();
						try {
							// Parse the alarm status message to determine if
							// the alarms are armed and to determine if the
							// individual
							// intrusions have been detected
							String alarm_msg = Msg.GetMessage();
							if (!alarms_armed && alarm_msg.charAt(0) == '1') {
								Arm(false);
							} else if (alarms_armed
									&& alarm_msg.charAt(0) == '0') {
								Arm(true);
							}
							if (alarm_msg.charAt(1) == '1') {
								fire_detect = true;
							} else if (alarm_msg.charAt(1) == '0') {
								fire_detect = false;
							}
						} // try

						catch (Exception e) {
							mw.WriteMessage("Error reading alarm status: " + e);

						} // catch

					} // if

					if (Msg.GetMessageId() == -9) // Sprinkler Alarm status
													// reading
					{
						startTime = System.currentTimeMillis();
						try {
							// Parse the alarm status message to determine if
							// the alarms are armed and to determine if the
							// individual
							// intrusions have been detected
							String alarm_msg = Msg.GetMessage();

							if (!alarms_armed && alarm_msg.charAt(0) == '1') {
								Arm(false);
							} else if (alarms_armed
									&& alarm_msg.charAt(0) == '0') {
								Arm(true);
							}
							if (alarm_msg.charAt(1) == '1') {
								sprinkler_start = true;
							} else if (alarm_msg.charAt(1) == '0') {
								sprinkler_start = false;
							}
						} // try

						catch (Exception e) {
							mw.WriteMessage("Error reading alarm status: " + e);

						} // catch

					} // if

					// If the message ID == 99 then this is a signal that the
					// simulation
					// is to end. At this point, the loop termination flag is
					// set to
					// true and this process unregisters from the message
					// manager.

					if (Msg.GetMessageId() == 99) {
						Done = true;

						try {
							em.UnRegister();

						} // try

						catch (Exception e) {
							mw.WriteMessage("Error unregistering: " + e);

						} // catch

						mw.WriteMessage("\n\nSimulation Stopped. \n");

						// Get rid of the indicators. The message panel is left
						// for the
						// user to exit so they can see the last message posted.

						ai.dispose();
						fi.dispose();
						si.dispose();
						wi.dispose();
						di.dispose();
						mi.dispose();

					} // if

				} // for

				mw.WriteMessage("Alarms armed: " + alarms_armed
						+ " Window break: " + window_break + " Door break: "
						+ door_break + " Motion detected: " + motion_detected
						+ " fire detected: " + fire_detect + " Sprinkle: "
						+ sprinkler_start);

				// Check alarm status and change indicators if necessary
				// Only show alarms as triggered if the alarm is actually armed
				// If the alarms are disarmed, the three intrusion indicators
				// show green no matter what
				if (alarms_armed) {
					ai.SetLampColorAndMessage("ARMED", 1);
					if (window_break) {
						wi.SetLampColorAndMessage("WIN BRK!", 3);
					} else {
						wi.SetLampColorAndMessage("WIN OK", 1);
					}
					if (door_break) {
						di.SetLampColorAndMessage("DOOR BRK!", 3);
					} else {
						di.SetLampColorAndMessage("DOOR OK", 1);
					}
					if (motion_detected) {
						mi.SetLampColorAndMessage("MOTION!", 3);
					} else {
						mi.SetLampColorAndMessage("NO MOTION", 1);
					}
					if (fire_detect) {
						fi.SetLampColorAndMessage("FIRE!", 3);
					}// if
					else {
						fi.SetLampColorAndMessage("NO FIRE", 1);
					}
					if (sprinkler_start) {
						si.SetLampColorAndMessage("SPRINKLER ON", 1);
					} else {
						si.SetLampColorAndMessage("SPRINKLER OFF", 3);
					}
				} else {
					ai.SetLampColorAndMessage("DISARMED", 3);
					fi.SetLampColorAndMessage("NO FIRE", 1);
					si.SetLampColorAndMessage("SPRINKLER OFF", 3);
					wi.SetLampColorAndMessage("WIN OK", 1);
					di.SetLampColorAndMessage("DOOR OK", 1);
					mi.SetLampColorAndMessage("NO MOTION", 1);
				} // if

				long endTime = System.currentTimeMillis();
				if (endTime - startTime > 2000) {
					mw.WriteMessage("Fire controller has not repsonded for more than 2 seconds, please alert the Fire Department.");
					mw.WriteMessage("Alarm has not responded for:"
							+ (endTime - startTime));
					ai.SetLampColorAndMessage("OFFLINE", 0);
					fi.SetLampColorAndMessage("OFFLINE", 0);
					si.SetLampColorAndMessage("OFFLINE", 0);
					wi.SetLampColorAndMessage("OFFLINE", 0);
					di.SetLampColorAndMessage("OFFLINE", 0);
					mi.SetLampColorAndMessage("OFFLINE", 0);
				}

				// Sends the heart beat on ID 10 so SystemC works
				Message heartbeat = new Message((int) 10, "I am alive");
				try {
					em.SendMessage(heartbeat);

				} // try

				catch (Exception e) {
					System.out
							.println("Error sending heartbeat message:: " + e);

				} // catch

				// This delay slows down the sample rate to Delay milliseconds
				try {
					Thread.sleep(Delay);

				} // try

				catch (Exception e) {
					System.out.println("Sleep error:: " + e);

				} // catch

			} // while

		} else {

			System.out
					.println("Unable to register with the message manager.\n\n");

		} // if

	} // main

	/***************************************************************************
	 * CONCRETE METHOD:: IsRegistered Purpose: This method returns the
	 * registered status
	 *
	 * Arguments: none
	 *
	 * Returns: boolean true if registered, false if not registered
	 *
	 * Exceptions: None
	 *
	 ***************************************************************************/

	public boolean IsRegistered() {
		return (Registered);

	} // SetTemperatureRange

	/***************************************************************************
	 * CONCRETE METHOD:: SetArmedStatus Purpose: This method sets the armed
	 * status
	 ***************************************************************************/
	public void SetArmedStatus(boolean armed) {
		alarms_armed = armed;
		Arm(armed);
		mw.WriteMessage("*** Security alarms status set to: " + armed + " ***");
	} // SetArmedStatus

	/***************************************************************************
	 * CONCRETE METHOD:: SetFireStatus Purpose: This method sets the fire status
	 ***************************************************************************/
	public void SetFireStatus(boolean detected) {
		// Fire status = detected;
		if (detected) {
			Trigger("Fire");
		} else {// If false it is asking for stopping fire
			Trigger("FireOff");
		}
		mw.WriteMessage("*** Fire  alarm set to: " + detected + " ***");
	} // SetFireStatus

	/***************************************************************************
	 * CONCRETE METHOD:: SetSprinklerStatus Purpose: This method sets the
	 * sprinkler status
	 ***************************************************************************/
	public void SetSprinklerStatus(boolean start) {
		// Sprinkle status = broken;
		if (start) {
			Trigger("Sprinkle");
		} else {// If false it is asking for stopping fire
			Trigger("SprinkleOff");
		}
		mw.WriteMessage("*** Sprinkler operation status: " + start + " ***");
	} // SetSprinkleStatus

	/***************************************************************************
	 * CONCRETE METHOD:: SetWindowStatus Purpose: This method sets the window
	 * status
	 ***************************************************************************/
	public void SetWindowStatus(boolean broken) {
		// window_break = broken;
		if (broken) {
			Trigger("Window");
		}
		mw.WriteMessage("*** Window break set to: " + broken + " ***");
	} // SetWindowStatus

	/***************************************************************************
	 * CONCRETE METHOD:: SetDoorStatus Purpose: This method sets the door status
	 ***************************************************************************/
	public void SetDoorStatus(boolean broken) {
		// door_break = broken;
		if (broken) {
			Trigger("Door");
		}
		mw.WriteMessage("*** Door break set to: " + broken + " ***");
	} // SetDoorStatus

	/***************************************************************************
	 * CONCRETE METHOD:: SetMotionStatus Purpose: This method sets the window
	 * status
	 ***************************************************************************/
	public void SetMotionStatus(boolean motion) {
		// motion_detected = motion;
		if (motion) {
			Trigger("Motion");
		}
		mw.WriteMessage("*** Motion detect set to: " + motion + " ***");
	} // SetMotionStatus

	/***************************************************************************
	 * CONCRETE METHOD:: Halt Purpose: This method posts an message that stops
	 * the environmental control system.
	 *
	 * Arguments: none
	 *
	 * Returns: none
	 *
	 * Exceptions: Posting to message manager exception
	 *
	 ***************************************************************************/

	public void Halt() {
		mw.WriteMessage("***HALT MESSAGE RECEIVED - SHUTTING DOWN SYSTEM***");

		// Here we create the stop message.

		Message msg;

		msg = new Message((int) 99, "XXX");

		// Here we send the message to the message manager.

		try {
			em.SendMessage(msg);

		} // try

		catch (Exception e) {
			System.out.println("Error sending halt message:: " + e);

		} // catch

	} // Halt

	/***************************************************************************
	 * CONCRETE METHOD:: Arm Purpose: This method posts messages that will
	 * signal the alarm controller to be armed/disarmed
	 *
	 * Arguments: boolean ON(true)/OFF(false) - indicates whether to turn the
	 * alarm on or off.
	 *
	 * Returns: none
	 *
	 * Exceptions: Posting to message manager exception
	 *
	 ***************************************************************************/

	private void Arm(boolean ON) {
		// Here we create the message.

		Message msg;

		if (ON) {
			msg = new Message((int) 6, "Arm");

		} else {

			msg = new Message((int) 6, "Disarm");

		} // if

		// Here we send the message to the message manager.

		try {
			em.SendMessage(msg);

		} // try

		catch (Exception e) {
			System.out.println("Error sending arm/disarm alarm message:: " + e);

		} // catch

	} // Alarm

	/***************************************************************************
	 * CONCRETE METHOD:: Trigger Purpose: This method posts messages that will
	 * signal the alarm controller to have some sort of intrusion alarm
	 *
	 * Arguments: String: "Window", "Door", "Motion"
	 * 
	 * Returns: none
	 *
	 * Exceptions: Posting to message manager exception
	 *
	 ***************************************************************************/

	private void Trigger(String intrusion) {
		// Here we create the message.

		Message msg;
		if (intrusion.equals("Window")) {
			msg = new Message((int) 7, "Window");
		} else if (intrusion.equals("Door")) {
			msg = new Message((int) 7, "Door");
		} else if (intrusion.equals("Motion")) {
			msg = new Message((int) 7, "Motion");
		} else if (intrusion.equals("Fire")) {
			msg = new Message((int) 8, "Fire");
		} else if (intrusion.equals("Sprinkle")) {
			msg = new Message((int) 9, "Sprinkle");
		} else if (intrusion.equals("FireOff")) {
			msg = new Message((int) 8, "FireOff");
		} else {
			msg = new Message((int) 9, "SprinkleOff");
		}
		// Here we send the message to the message manager.

		try {
			em.SendMessage(msg);

		} // try

		catch (Exception e) {
			System.out.println("Error sending arm/disarm alarm message:: " + e);

		} // catch

	} // Alarm

} // SecurityMonitor