
    package switchboxdesc;
    
    import switchboxdesc.gen.CustomDescriptor;
    
    public class SwitchBoxDesc extends CustomDescriptor
    {

		@Override
		public void setEnabledAction(String isEnabled) 
		{
			boolean enabled = Boolean.valueOf(isEnabled);
			if (enabled) {
				setEnabledRemote((byte)1);
			} else {
				setEnabledRemote((byte)0);
			}
			
		}

		@Override
		public String getDisplaySwitchesStatus() 
		{
			return "hello world";
		}

		@Override
		public void switch1ChangedToCallBack(Byte switchState)
		{
			if (switchState < 1)
			{
				cancelTrigger(SWITCH_1, switchState.toString());
			} else if (switchState > 0)
			{
				didTrigger(SWITCH_1, switchState.toString());
			}			
		}

		@Override
		public void switch2ChangedToCallBack(Byte switchState) 
		{
			if (switchState < 1)
			{
				cancelTrigger(SWITCH_2, switchState.toString());
			} else if (switchState > 0)
			{
				didTrigger(SWITCH_2, switchState.toString());
			}		
		}

		@Override
		public void switch3ChangedToCallBack(Byte switchState) 
		{
			if (switchState < 1)
			{
				cancelTrigger(SWITCH_3, switchState.toString());
			} else if (switchState > 0)
			{
				didTrigger(SWITCH_3, switchState.toString());
			}		
		}

		@Override
		public void dialChanged2CallBack(Byte dialState) 
		{
			didTrigger(DIAL, dialState.toString());
		}

		@Override
		protected boolean validateAction(int actionID, String... arguments) 
		{
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		protected boolean validateTrigger(int triggerID, String... arguments) {
			// TODO Auto-generated method stub
			return true;
		}
    	    
    
    }
    	