
    package lpd8806;

import lpd8806.gen.CustomDescriptor;
    
    public class LPD8806 extends CustomDescriptor
    {

		@Override
		public void doColorChaseAction() 
		{		
			colorChaseStartRemote();		
			
		}
		
		@Override
		public void stopColorChaseAction() 
		{		
			colorChaseStopRemote();
		}

		@Override
		protected boolean validateAction(int actionID, String... arguments) {
			return actionID == COLORCHASE;
		}

		@Override
		protected boolean validateTrigger(int triggerID, String... arguments) {
			// TODO Auto-generated method stub
			return false;
		}
    	    
    
    }
    	