################################ 
A3
Team Rainbow
################################ 

*************
System A
*************
1) Open up a terminal/command prompt
2) Chanege directory to folder labeled "System A"
3) Run "javac *.java" 
4) Run "rmic MessageManaer"
5) Run "rmigistry"
6) In a new terminal/cmd tab/window, run "java MessageManager"
7) In a third terminal/cmd tab/window, run "java SecurityConsole"
8) In a fourth terminal/cmd tab/window, run "java AlarmsController"

To test the system:
1) Once all components have been started, you can see in the Message Window
   that the alarm is initially disarmed and that no breaks are being reported.
2) The indicator lights take about five minutes before they start to blink in
   the correct color consistently. Please be patient. The status of the system
   is reported correctly in the message window, the indicators are just slow.
3) Arm the alarm using option "1" in the SecurityConsole. 
   Note that all lights are now green.
4) Trigger a window break using "3" in the SecurityConsole, note that the
   alarm is being reported in the message window and the corresponding
   indicator is blinking red.
5) Disarm the alarm using option "2", note that the arm/disarm indicator
   is red again and the triggered alarms are cleared.
6) While the alarm is still disarmed, try to trigger a window break by entering 
   "3" in the SecurityConsole (or any other break). Observe that the alarms are
   not blinking. 
7) Shut down only the AlarmsController by going to the terminal tab/window
   that ran the AlarmsController and press Ctrl+C. Note that all the alarm
   indicators turn to black and the SecurityMonitor reports that the alarms
   are offline. 


*************
System B
*************



*************
System C
*************