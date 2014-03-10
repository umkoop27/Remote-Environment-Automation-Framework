
    package messageboard;
    
    import java.util.ArrayList;

import messageboard.gen.CustomDescriptor;
    
    public class MessageBoard extends CustomDescriptor
    {
    	private ArrayList<String> messages;
    	private int maxNumMessages;
    	private final int defaultMaxNumMessages = 30;
    	
    	public MessageBoard()
    	{
    		messages = new ArrayList<String>();
    		maxNumMessages = defaultMaxNumMessages;
    	}
    	
		@Override
		public void addMessageAction(String message) 
		{
			if(messages.size() == maxNumMessages)	//if the list of messages is full
			{
				messages.remove(messages.size() - 1);
			}
			messages.add(0, message);
			didTrigger(NEW_MESSAGE);
		}

		@Override
		public void setMaxMessagesAction(String maxNumMessages) 
		{
			System.out.println("Set max messages to: " + maxNumMessages);
			try
			{
				int newMaxNumMessages = Integer.parseInt(maxNumMessages);
				this.maxNumMessages = newMaxNumMessages;
				System.out.println("Current size = " + messages.size() + ", New Max Size = " + newMaxNumMessages);
				while(messages.size() > this.maxNumMessages)
				{
					messages.remove(messages.size() - 1);
					System.out.println("Current size = " + messages.size());
				}
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
			}
		}

		@Override
		public String getMessagesStatus() 
		{
			String result = "";
			
			for(int messageIndex = 0; messageIndex < messages.size(); messageIndex++)
			{
				result = result + messages.get(messageIndex);
				if(messageIndex != messages.size()-1)
					result = result + "|";
			}
			
			return result;
		}

		@Override
		public String getMaximumNumberofMessagesStatus() 
		{
			return "" + maxNumMessages;
		}

		@Override
		protected boolean validateAction(int actionID, String... arguments) 
		{
			boolean result = false;
			
			if(actionID == POST_TO_MESSAGE_BOARD && arguments.length == 1 && arguments[0] != null)
				result = true;
			else if(actionID == SET_MAX_MESSAGES && arguments.length == 1 && arguments[0] != null)
			{
				try
				{
					int newMax = Integer.parseInt(arguments[0]);
					System.out.println("New max = " + newMax);
					if(newMax > 0 && newMax < 500)
						result = true;
				}
				catch(NumberFormatException e)
				{
					result = false;
				}
				
			}
			
			return result;
		}

		@Override
		protected boolean validateTrigger(int triggerID, String... arguments) 
		{
			boolean result = false;
			if(triggerID == NEW_MESSAGE && (arguments == null || arguments.length == 0))
					result = true;
			return result;
		}
    	    
    
    }
    