
    package compoundperipheral;
    
    import compoundperipheral.gen.CustomDescriptor;
    
    public class CompoundPeripheral extends CustomDescriptor
    {
    	//Final Constants
    	private static final Byte ON_BYTE= 0x5;
    	private static final Byte OFF_BYTE = 0x0;
    	private static final Byte OPEN_BYTE = 0x5;
    	private static final Byte CLOSED_BYTE = 0x0;
    	
    	private static final boolean ON_STATE = true;
    	private static final boolean OFF_STATE = false;
    	private static final boolean OPEN_STATE = true;
    	private static final boolean CLOSED_STATE = false;
    	
    	private static final int callbackWaitTime = 2000; //msec
    	
    	//Note: False is off/open, True is on/closed
    	private Boolean magneticContactState = ON_STATE;
    	private Boolean buzzerState = OFF_STATE;
    	private Boolean appliance1State = OFF_STATE;
    	private Boolean appliance2State = OFF_STATE;
    	private Boolean appliance3State = OFF_STATE;

		@Override
		public void setBuzzerOnAction() {
			setBuzzerOnRemote();
		}

		@Override
		public void setBuzzerOffAction() {
			setBuzzerOffRemote();	
		}

		@Override
		public void setAppliance1OnAction() {
			setAppliance1OnRemote();
		}

		@Override
		public void setAppliance1OffAction() {
			setAppliance1OffRemote();
		}

		@Override
		public void setAppliance2OnAction() {
			setAppliance2OnRemote();
		}

		@Override
		public void setAppliance2OffAction() {
			setAppliance2OffRemote();
		}

		@Override
		public void setAppliance3OnAction() {
			setAppliance3OnRemote();
		}

		@Override
		public void setAppliance3OffAction() {
			setAppliance3OffRemote();
		}

		@Override
		public String getMagneticContactStateStatus() {
			requestMagneticContactStateRemote();
			String returnValue = "";
			synchronized(this.magneticContactState)
			{
				try {
					this.magneticContactState.wait(callbackWaitTime);
				
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					System.out.println("No response when querying for Magnetic Contact State: returning last known state");	
				}
				if(OPEN_STATE == magneticContactState)
				{
					returnValue = "Open";
				}
				else //CLOSED_STATE == magneticContactState
				{
					returnValue = "Closed";
				}
				return returnValue;
			}
		}

		@Override
		public String getBuzzerStateStatus() {
			requestBuzzerStateRemote();
			String returnValue = "";
			synchronized(this.buzzerState)
			{
				try {
					this.buzzerState.wait(callbackWaitTime);
				
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					System.out.println("No response when querying for Buzzer State: returning last known state");	
				}
				if(ON_STATE == buzzerState)
				{
					returnValue = "On";
				}
				else //OFF_STATE == buzzerState
				{
					returnValue = "Off";
				}
				return returnValue;
			}
		}

		@Override
		public String getAppliance1StateStatus() {
			requestAppliance1StateRemote();
			String returnValue = "";
			synchronized(this.appliance1State)
			{
				try {
					this.appliance1State.wait(callbackWaitTime);
				
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					System.out.println("No response when querying for Appliance 1 State: returning last known state");	
				}
				if(ON_STATE == appliance1State)
				{
					returnValue = "On";
				}
				else //OFF_STATE == appliance1State
				{
					returnValue = "Off";
				}
				return returnValue;
			}
		}

		@Override
		public String getAppliance2StateStatus() {
			requestAppliance2StateRemote();
			String returnValue = "";
			synchronized(this.appliance2State)
			{
				try {
					this.appliance2State.wait(callbackWaitTime);
				
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					System.out.println("No response when querying for Appliance 2 State: returning last known state");	
				}
				if(ON_STATE == appliance2State)
				{
					returnValue = "On";
				}
				else //OFF_STATE == appliance2State
				{
					returnValue = "Off";
				}
				return returnValue;
			}
		}

		@Override
		public String getAppliance3StateStatus() {
			requestAppliance3StateRemote();
			String returnValue = "";
			synchronized(this.appliance3State)
			{
				try {
					this.appliance3State.wait(callbackWaitTime);
				
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					System.out.println("No response when querying for Appliance 3 State: returning last known state");	
				}
				if(ON_STATE == appliance3State)
				{
					returnValue = "On";
				}
				else //OFF_STATE == appliance3State
				{
					returnValue = "Off";
				}
				return returnValue;
			}
		}

		@Override
		public synchronized void receiveMagneticContactStateCallBack(Byte state) {
			boolean newState;
			if(OPEN_BYTE == state)
			{
				newState = OPEN_STATE;
				didTrigger(MAGNETIC_CONTACT_OPEN);
			}
			else //CLOSED_BYTE == state
			{
				newState = CLOSED_STATE;
				didTrigger(MAGNETIC_CONTACT_CLOSED);
			}
			//First check if the contact state has toggled
			if(magneticContactState != newState)
			{
				didTrigger(MAGNETIC_CONTACT_TOGGLE);
				//Then change the state to the current 'new' state
				magneticContactState = newState;
			}
			synchronized(this.magneticContactState)
			{
				this.magneticContactState.notifyAll();
			}
		}

		@Override
		public void receiveBuzzerStateCallBack(Byte state) {
			if(ON_BYTE == state)
			{
				buzzerState = ON_STATE;
			}
			else //OFF_BYTE == state
			{
				buzzerState = OFF_STATE;
			}
			synchronized(this.buzzerState)
			{
				this.buzzerState.notifyAll();
			}
		}

		@Override
		public void receiveAppliance1StateCallBack(Byte state) {
			if(ON_BYTE == state)
			{
				appliance1State = ON_STATE;
			}
			else //OFF_BYTE == state
			{
				appliance1State = OFF_STATE;
			}
			synchronized(this.appliance1State)
			{
				this.appliance1State.notifyAll();
			}
		}

		@Override
		public void receiveAppliance2StateCallBack(Byte state) {
			if(ON_BYTE == state)
			{
				appliance2State = ON_STATE;
			}
			else //OFF_BYTE == state
			{
				appliance2State = OFF_STATE;
			}
			synchronized(this.appliance2State)
			{
				this.appliance2State.notifyAll();
			}
		}

		@Override
		public void receiveAppliance3StateCallBack(Byte state) {
			if(ON_BYTE == state)
			{
				appliance3State = ON_STATE;
			}
			else //OFF_BYTE == state
			{
				appliance3State = OFF_STATE;
			}
			synchronized(this.appliance3State)
			{
				this.appliance3State.notifyAll();
			}
		}

		@Override
		protected boolean validateAction(int actionID, String... arguments) 
		{
			//based (roughly) on doAction in CustomDescriptor.java
			
			boolean validAction = true;

			switch( actionID )
    		{
    			case SET_BUZZER_ON:  
			        break;
			        
    			case SET_BUZZER_OFF:
    				break;
    				
    			case SET_APPL1_ON:
			        break;
			        
    			case SET_APPL1_OFF:
    				break;
    				
    			case SET_APPL2_ON:
					break;
    			
    			case SET_APPL2_OFF:
    				break;
        		
    			case SET_APPL3_ON:
					break;
					
    			case SET_APPL3_OFF:
    				break;
   			
    			default:
    				validAction = false;
    				break;
    		}
    		return validAction;
		}

		@Override
		protected boolean validateTrigger(int triggerID, String... arguments) {
			boolean validTrigger = true;
			
			switch(triggerID)
			{
				case MAGNETIC_CONTACT_TOGGLE:
					break;
				case MAGNETIC_CONTACT_OPEN:
					break;
				case MAGNETIC_CONTACT_CLOSED:
					break;
				
				default:
					validTrigger = false;
					break;
			}	
			return validTrigger;
		}
    	    
    
    }
    	