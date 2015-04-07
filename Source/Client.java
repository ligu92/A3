import InstrumentationPackage.*;
import MessagePackage.*;

class Client {
	private Indicator myIndicator;
	private long lastMessageTime;
	private long id;
	private String componentType;
	
	public Client (Indicator _indicator, long _msgtime, long _id, int _mcode){
		this.myIndicator = _indicator;
		this.lastMessageTime = _msgtime;
		this.id = _id;		
		this.componentType = getDeviceType(_mcode);			
	}
	
	public Client (long _msgtime, long _id, int _mcode){
		this.lastMessageTime = _msgtime;
		this.id = _id;		
		this.componentType = getDeviceType(_mcode);			
	}
	
	public void updateTime(){
		this.lastMessageTime = System.currentTimeMillis();
	}
	
	public void updateTime(long _t){
		this.lastMessageTime = _t;
	}
	
	public Indicator getIndicator(){
		return this.myIndicator;
	}
	
	public void setIndicator(Indicator _i){
		this.myIndicator = _i;
	}
	
	public long getID(){
		return this.id;
	}
	
	public long getLastMessageTime(){
		return this.lastMessageTime;
	}
	
	public String getComponentType(){
		return this.componentType;
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