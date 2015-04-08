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
			//If the message code is -8, then they're a FireController.
			return "Fire Controller";
		}
		if (messageCode == -9){
			//If the message code is -9, then they're a SprinklerController.
			return "Sprinkler Controller";
		}
		if (messageCode == 8 || messageCode == 7 || messageCode == 6){
			//If the message code is 8, 7, 6, then they're a SecurityMonitor.
			return "Security Monitor";
		}
		if (messageCode == 10){
			//If the message code is 10, then they're a FireMonitor.
			return "Fire Monitor";
		}
		if (messageCode == -6){
			//If the message code is -6, then they're a AlarmsController.
			return "Alarm Controller";
		}
		if (messageCode == 4 || messageCode == 5){
			//If the message code is 4, 5, then they're a ECSConsole.
			return "ECS Console";
		}
		if (messageCode == 1){
			//If the message code is 1, then they're a TemperatureSensor.
			return "Temperature Sensor";
		}
		if (messageCode == 2){
			//If the message code is 1, then they're a HumiditySensor.
			return "Humidity Sensor";
		}
		if (messageCode == -4){
			//If the message code is -4, then they're a HumidityController.
			return "Humidity Controller";
		}
		if (messageCode == -5){
			//If the message code is -4, then they're a TemperatureController.
			return "Temperature Controller";
		}
		else{
			//Unidentified component. Give it a generic name.
			return "Unknown Device";
		}
	}
}