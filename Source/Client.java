import InstrumentationPackage.*;
import MessagePackage.*;

class Client {
	Indicator myIndicator;
	long lastMessageTime;
	String id;
	String componentType;
	
	public Client (Indicator _indicator, long _msgtime, String _id, int _mcode){
		this.myIndicator = _indicator;
		this.lastMessageTime = _msgtime;
		this.id = _id;		
		this.componentType = getDeviceType(_mcode);			
	}
	
	private String getDeviceType (int messageCode){
		if (messageCode == -8){
			//If the message code is -8, X, X, then they're a FireController.
			return "Fire Controller";
		}
		if (messageCode == -9){
			//If the message code is -9, X, X, then they're a SprinklerController.
			return "Sprinkler Controller";
		}
		if (messageCode == 10 || messageCode == 7 || messageCode == 6){
			//If the message code is 10, 7, 6, then they're a SecurityMonitor.
			return "Security Monitor";
		}
		if (messageCode == -6){
			//If the message code is -6, X, X, then they're a AlarmsController.
			return "Alarm Controller";
		}
		else{
			//Unidentified component. Give it a generic name.
			return "Unknown Device";
		}
	}
}