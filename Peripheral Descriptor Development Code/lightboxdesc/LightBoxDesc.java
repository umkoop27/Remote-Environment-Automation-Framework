
    package lightboxdesc;
    
    import lightboxdesc.gen.CustomDescriptor;
    
    public class LightBoxDesc extends CustomDescriptor
    {
    	boolean redState = false;
    	boolean whiteState = false;
    	boolean yellowState = false;
    	boolean blueState = false;
    	boolean greenState = false;
    	

		@Override
		public void setRedAction(String newState) {
			redState = Boolean.parseBoolean(newState);
			if (redState) 
			{
				redLightOnRemote();
			} else {
				redLightOffRemote();
			}			
		}

		@Override
		public void setWhiteAction(String newState) {
			whiteState = Boolean.parseBoolean(newState);
			if (whiteState) 
			{
				whiteLightOnRemote();
			} else {
				whiteLightOffRemote();
			}	
		}

		@Override
		public void blinkAllAction(String timesToBlink) {			
			blinkLightsRemote((byte) Integer.parseInt(timesToBlink));
		}

		@Override
		public String getLightStateStatus() 
		{
			return "Blue: " + blueState + " Green: " + greenState + " Yellow: " + yellowState + "Red: " + redState + " White: " + whiteState;
		}

		@Override
		protected boolean validateAction(int actionID, String... arguments) {
			if (SET_RED == actionID ||
					SET_WHITE == actionID ||
					SET_YELLOW == actionID ||
					SET_GREEN_INTENSITY == actionID ||
					SET_GREEN == actionID ||
					SET_BLUE == actionID ||
					BLINK == actionID)
			{
				if (arguments.length == 1)
				{
					return true;
				}
			} else if (ALL_ON == actionID || ALL_OFF == actionID)
			{
				if (arguments.length == 0)
				{
					return true;
				}
			}
			
			return false;
		}

		@Override
		protected boolean validateTrigger(int triggerID, String... arguments) 
		{
			return false;
		}

		@Override
		public void setYellowAction(String newState) 
		{
			yellowState = Boolean.parseBoolean(newState);
			if (yellowState)
			{
				yellowLightOnRemote();
			} else {
				yellowLightOffRemote();
			}
		}

		@Override
		public void setGreenAction(String newState)
		{
			greenState = Boolean.parseBoolean(newState);
			if (greenState)
			{
				greenLightOnRemote();
			} else {
				greenLightOffRemote();
			}
		}

		@Override
		public void setBlueAction(String newState) 
		{
			blueState = Boolean.parseBoolean(newState);
			if (blueState)
			{
				blueLightOnRemote();
			} else {
				blueLightOffRemote();
			}
		}

		@Override
		public void allOnAction() 
		{
			blueState = true;
			redState = true;
			yellowState = true;
			whiteState = true;
			greenState = true;
			turnAllOnRemote();
		}

		@Override
		public void allOffAction()
		{
			blueState = false;
			redState = false;
			yellowState = false;
			whiteState = false;
			greenState = false;
			turnAllOffRemote();
		}

		@Override
		public void setGreenIntensityAction(String brightness) 
		{
			int intensity = Integer.parseInt(brightness);
			
			if (0 == intensity)
			{
				greenState = false;
			} else {
				greenState = true;
			}
			
			setGreenBrightnessRemote((short) intensity);			
		}
    	    
    
    }
    	