
    package switchboxdesc;
    
    import switchboxdesc.gen.CustomDescriptor;
    
    public class SwitchBoxDesc extends CustomDescriptor
    {

		@Override
		public void switch1ChangedToCallBack(Byte switchState)
		{
			if (switchState < 1)
			{
				cancelTrigger(SWITCH_1, "true");
				didTrigger(SWITCH_1, "false");
			} 
			else if (switchState > 0)
			{
				cancelTrigger(SWITCH_1, "false");
				didTrigger(SWITCH_1, "true");
			}			
		}

		@Override
		public void switch2ChangedToCallBack(Byte switchState) 
		{
			if (switchState < 1)
			{
				cancelTrigger(SWITCH_2, "true");
				didTrigger(SWITCH_2, "false");
			} else if (switchState > 0)
			{
				cancelTrigger(SWITCH_2, "false");
				didTrigger(SWITCH_2, "true");
			}	
		}

		@Override
		public void switch3ChangedToCallBack(Byte switchState) 
		{
			if (switchState < 1)
			{
				cancelTrigger(SWITCH_3, "true");
				didTrigger(SWITCH_3, "false");
			} else if (switchState > 0)
			{
				cancelTrigger(SWITCH_3, "false");
				didTrigger(SWITCH_3, "true");
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
    	