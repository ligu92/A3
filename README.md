################################ 
A3
Team Rainbow
################################ 
WARNING: The indicators may not appear to work correctly in Mac OSX, they are in fact working
but don't render as expected in this environment. They may take some time to appear normal, but
they may also constantly flash or update intermittently. We have verified that
they work correctly on Windows platforms, which is the intended environment for these systems, as
the writeup suggests.


*************
System A
*************
1) Open up a terminal/command prompt
2) Chanege directory to folder labeled "SystemA"
3) Run "javac *.java" 
4) Run "rmic MessageManaer"
5) Run "rmiregistry"
6) In a new terminal/cmd tab/window, run "java MessageManager"
7) In a third terminal/cmd tab/window, run "java AlarmsController"
8) In a fourth terminal/cmd tab/window, run "java SecurityConsole"

To test the system:
1) Once all components have been started in the order given, you can see in the Message Window
   that the alarm is initially disarmed and that no intrusions are being reported.
2) Arm the alarm using option "1" in the SecurityConsole. 
   Note that all lights are now green.
3) Trigger a window break using "3" in the SecurityConsole, note that the
   alarm is being reported in the message window and the corresponding
   indicator is blinking red.
   You can also test the door break alarm ("4") and the motion detector ("5") as well.
4) Disarm the alarm using option "2", note that the arm/disarm indicator
   is red again and the triggered alarms are cleared.
5) While the alarm is still disarmed, try to trigger a window break by entering 
   "3" in the SecurityConsole (or any other break). Observe that the alarms are
   not blinking. 
6) Shut down only the AlarmsController by going to the terminal tab/window
   that ran the AlarmsController and press Ctrl+C. Note that all the alarm
   indicators turn to black and the SecurityMonitor reports that the alarms
   are offline. At this point, you should see the message that tells you to
   alert the police. 


*************
System B
*************
1) Open up a terminal/command prompt
2) Change directory to folder labeled "SystemB"
3) Run "javac *.java" 
4) Run "rmic MessageManager"
5) Run "rmiregistry"
6) In a new terminal/cmd tab/window, run "java MessageManager"
7) In a third terminal/cmd tab/window, run "java FireController"
8) In a fourth terminal/cmd tab/window, run "java SprinklerController"
9) In a fifth terminal/cmd tab/window, run "java SecurityConsole"

To test the system:
1) Once all components have been started in the order given, you can see in 
   the Message Window that there is no fire and the sprinklers are off.
2) Trigger a fire with option "6". You can see a prompt pop up, asking you
   to engage the sprinklers or to cancel them.
   Note the system message in SecurityMonitor also reports that a fire is detected.
3) Choose to start the sprinklers, you can see both the sprinklers are engaged
   and that fire is still detected. 
4) To simulate the fire being put out, use option "7" to reset the fire and stop the sprinklers.
5) Start another fire using "6" and do not take any action at the prompt. Note that the
   sprinklers automatically engage after 10 seconds.
6) Reset the fire using "7" and start a fire again with "6". 
   This time, choose to not engage the sprinklers. Note that the fire is still
   detected but the sprinklers are off.
7) Reset the fires using "7". Shut down only the FireController or 
   the SprinklerController by going to their respective terminal windows and using
   Ctrl+C. Note that all the fire and sprinkler indicators turn to black and the 
   SecurityMonitor reports that the fire alarm or the sprinkler is offline. This
   means that you should alert the fire department.


*************
System C
*************
1) Open up a terminal/command prompt
2) Chanege directory to folder labeled "SystemC"
3) Run "javac *.java" 
4) Run "rmic MessageManager"
5) Run "rmiregistry"
6) In a new terminal/cmd tab/window, run "java MessageManager"
7) In a new terminal/cmd tab/window, run "java FireController"
8) In a new terminal/cmd tab/window, run "java SprinklerController"
9) In a new terminal/cmd tab/window, run "java AlarmsController"
10) In a new terminal/cmd tab/window, run "java SecurityConsole"
11) In a new terminal/cmd tab/window, run "java HumiditySensor"
12) In a new terminal/cmd tab/window, run "java HumidityController"
13) In a new terminal/cmd tab/window, run "java TemperatureSensor"
14) In a new terminal/cmd tab/window, run "java TemperatureController"
15) In a new terminal/cmd tab/window, run "java ECSConsole"
16) In a new terminal/cmd tab/window, run "java MaintenanceConsole"

=========
Optional
=========
17) The MaintenanceConsole has the ability to dynamically detect any participants
	that were connected to the message manager before it was started. It can also 
	detect any participants that are connected after it has been started. To test this,
	you can start some of the above components before starting the MaintenanceConsole,
	start the MaintenanceConsole, and then start some of the other components.


To test the system:
1) You should be able to see a new indicator for each participant that is registered
   with the message manager.
2) Shut down any participant that you started above by going to their terminal window
   and entering Ctrl+C. Note that the MaintenanceMonitor message window reports
   that the participant is offline in 6 seconds and that the indicator changes color.
Note: The MaintenanceConsole tracks the state of every participant that is connected. It has the capability to keep track of participants that have gone offline and then are reconnected. If you
terminate a participant and then start a new participant of the same type, it will be 
considered a new participant by the MaintenanceMonitor. The MaintenanceMonitor can only keep track of participants if they have the same ID on the message bus. This can only be simulated by
suspending the participant's process rather than terminating it completely. This is part
of the message bus behavior and cannot be modified. 