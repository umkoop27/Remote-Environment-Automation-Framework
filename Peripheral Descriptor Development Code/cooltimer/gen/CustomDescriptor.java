
package cooltimer.gen;


import peripherals.*;

//import java.security.InvalidParameterException; // used for testing if remote function ID is within permissible range



import automationNetwork.Network;

import cooltimer.CoolTimer;
    
/**
* WARNING: DO NOT MODIFY THIS CODE AS IT IS AUTO-GENERATED
*/    
public abstract class CustomDescriptor implements PeripheralDescriptor {
	
	private static final boolean DEBUG = false;
	/** return the type ID of this peripheral. */
	public static final int typeID = 54;
	public static final String xmlFileAddr = "MyTimer.xml";
	
	public static CustomDescriptor getInstance()
	{
		return new CoolTimer();
	}
	
	/////////////
	// Triggers
	/////////////
	
	private static final String[] triggerStrings = new String[]
		{
			
			"Timer Expired",
			"Current Date is",
			"Timer was Stopped" 
		};
	
	protected static final int TIMER_EXPIRED = 0;
	protected static final int CURRENT_DATE = 1;
	protected static final int TIMER_STOPPED = 2;
	
	/////////////
	// Actions
	/////////////
	
	private static final String[] actionStrings = new String[]
		{
			
			"Stop Timer",
			"Set Timer" 
		};
	
	protected static final int STOP_TIMER = 0;
	protected static final int SET_TIMER = 1;
	
	////////////////
	// Callback IDs
	////////////////
	
	
	//////////////////////
	// Instance Variables
	//////////////////////
	
	private TriggerHandler delegate;
	protected Peripheral mPeripheral;    
	
	/////////////
	// Overrides
	/////////////
	
	@Override
	public int getTypeID() 
	{
		return typeID;
	}
	
	@Override
	public String getTypeString()
	{
		return "My Timer";
	}
	
	@Override
	public String getXMLString()
	{
		return xmlFileAddr;
//		String xmlAsString = "XML NOT FOUND";
//		
//		ClassLoader cl = this.getClass().getClassLoader();
//		InputStream stream = cl.getResourceAsStream(xmlFileAddr);
//		if (null != stream)
//		{
//			xmlAsString = ""; // the XML file is found
//			BufferedReader mBufferedReader = new BufferedReader(new InputStreamReader(stream));
//			try {
//				while (mBufferedReader.ready())
//				{
//					xmlAsString += mBufferedReader.readLine();
//				}
//			} catch (IOException e) 
//			{
//				e.printStackTrace();
//			}
//		}
//		
//		return xmlAsString;
	}
	
	@Override
	public String getStatus(String statusString)
	{
		String outputStatus = "";
		switch (statusString) {
		
		case "Timer Value":
			outputStatus = getTimerValueStatus();
			break;
		
		case "Paul is cool":
			outputStatus = getPauliscoolStatus();
			break;
		

		default:
			break;
		}
		
		
		return outputStatus;			
	}
	
	@Override
	public void doAction(String action, String...arguments) 
	{
		int actionIndex = actionStrings.length;
					
		// linear search for the index of action
		for (int index = 0; index < actionStrings.length; index++)
		{
			if (actionStrings[index].equals(action))
			{
				actionIndex = index;
				break;
			}
		}
		
		
		if (STOP_TIMER == actionIndex)
		{
			stopTimerAction();
		} else
		if (SET_TIMER == actionIndex)
		{
			
			String actionArgument0 = arguments[0];
			setTimerAction(actionArgument0);
		} 	
		
		
		
	}
	
	/**
	 * Call this function to set the flag for a particular trigger. This
	 * means that actions that rely on this trigger in the service manager
	 * will be eligible to be called.
	 * @param triggerID the constant representing the trigger. One of: <code>TIMER_EXPIRED</code>, <code>CURRENT_DATE</code>, <code>TIMER_STOPPED</code>.
    
	 * @param arguments the ',' separated <code>String</code> arguments. Since this parameter 
	 * is a <code>varargs</code>, it is possible to simply call 
	 * <p><code>didTrigger("someString")</code></p> <p>if you are sending a trigger
	 * that has no arguments. 
	 */
	public void didTrigger(int triggerID, String...arguments) 
	{
		switch (triggerID) {
		
		case TIMER_EXPIRED:
			if (DEBUG) 
			{
				System.out.println("Timer Expired-------");
			}						
			delegate.triggerOccurred(true, mPeripheral, triggerStrings[triggerID], arguments);
			break;
		
		case CURRENT_DATE:
			if (DEBUG) 
			{
				System.out.println("Current Date is-------");
			}						
			delegate.triggerOccurred(true, mPeripheral, triggerStrings[triggerID], arguments);
			break;
		
		case TIMER_STOPPED:
			if (DEBUG) 
			{
				System.out.println("Timer was Stopped-------");
			}						
			delegate.triggerOccurred(true, mPeripheral, triggerStrings[triggerID], arguments);
			break;
				
		default:
			break;
		}
	}
	
	/**
	 * Call this function to clear the flag for a particular trigger. This
	 * means that actions that rely on this trigger in the service manager
	 * will no longer be eligible to be called.
	 * @param triggerID the constant representing the trigger. One of:<code>TIMER_EXPIRED</code>, <code>CURRENT_DATE</code>, <code>TIMER_STOPPED</code>.
     
	 * @param arguments the ',' separated <code>String</code> arguments. Since this parameter 
	 * is a <code>varargs</code>, it is possible to simply call 
	 * <p><code>didTrigger("someString")</code></p> <p>if you are sending a trigger</p> 
	 * that has no arguments. 
	 */
	public void cancelTrigger(int triggerID, String...arguments)
	{
		switch (triggerID) {
		
		case TIMER_EXPIRED:
			if (DEBUG) 
			{
				System.out.println("cancel ------Timer Expired-------");
			}						
			delegate.triggerOccurred(false, mPeripheral, triggerStrings[triggerID], arguments);
			break;
		
		case CURRENT_DATE:
			if (DEBUG) 
			{
				System.out.println("cancel ------Current Date is-------");
			}						
			delegate.triggerOccurred(false, mPeripheral, triggerStrings[triggerID], arguments);
			break;
		
		case TIMER_STOPPED:
			if (DEBUG) 
			{
				System.out.println("cancel ------Timer was Stopped-------");
			}						
			delegate.triggerOccurred(false, mPeripheral, triggerStrings[triggerID], arguments);
			break;
				
		default:
			break;
		}
	}
	@Override
	public void setDelegate(TriggerHandler delegate)
	{
		this.delegate = delegate;		
	}
	
	@Override
	public void setPeripheral(Peripheral peripheral)
	{
		mPeripheral = peripheral;
	}
	
	@Override
	public boolean validateTrigger(String triggerID, String... arguments)
	{
		for (int index = 0; index < triggerStrings.length; index++)
		{
			if (triggerStrings[index].equals(triggerID)) 
			{
				return validateTrigger(index, arguments); // delegate to peripheral descriptor
			}
		}
		
		return false; // triggerID does not match a trigger string.
	}
	
	@Override
	public boolean validateAction(String actionID, String... arguments)
	{
		for (int index = 0; index < actionStrings.length; index++)
		{
			if (actionStrings[index].equals(actionID)) 
			{
				return validateAction(index, arguments); // delegate to peripheral descriptor
			}
		}
		
		return false; // actionID does not match an action string.
	}
	
	@Override
	public boolean handlePacket(byte[] data)
	{
	
		return false;
	}

	/////////////////////
	// Action Functions 
	/////////////////////
	
	
	public abstract void stopTimerAction();
	
	public abstract void setTimerAction(String seconds);
		
	
	/////////////////////
	// Status Functions
	/////////////////////
	
	/**
	 * @return a <code>String</code> that describes the status field 'Timer Value'.
	 */
	public abstract String getTimerValueStatus();
	
	/**
	 * @return a <code>String</code> that describes the status field 'Paul is cool'.
	 */
	public abstract String getPauliscoolStatus();
	
	
		
	/////////////////////
	// Remote functions
	/////////////////////
	
	
	/**
	 * Test that <code>arguments</code> is a valid set of arguments for the action
	 * <code>actionID</code>.
	 * @param actionID an <code>int</code> representing one of: <code>STOP_TIMER</code>, <code>SET_TIMER</code>.
    
	 * @param arguments the ',' separated <code>String</code> arguments that are being tested.
	 * @return <code>true</code> if the combination of <code>actionID</code> and 
	 * <code>arguments</code> is valid, <code>false</code> otherwise.
	 */
	protected abstract boolean validateAction(int actionID, String... arguments);
	
	/**
	 * Test that <code>arguments</code> is a valid set of arguments for the trigger
	 * <code>triggerID</code>. This method gets called when the user tries to add a
	 * new service, so it should be used to collect the parameters that the user specified.
	 * The parameter(s) will be represented by <code>arguments</code>.
	 * @param triggerID an <code>int</code> representing one of <code>TIMER_EXPIRED</code>, <code>CURRENT_DATE</code>, <code>TIMER_STOPPED</code>.
    
	 * @param arguments the ',' separated <code>String</code> arguments that are being tested.
	 * @return <code>true</code> if the combination of <code>triggerID</code> and 
	 * <code>arguments</code> is valid, <code>false</code> otherwise.
	 */
	protected abstract boolean validateTrigger(int triggerID, String... arguments);
   
}
    