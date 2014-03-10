
package lpd8806.gen;


import peripherals.*;

//import java.security.InvalidParameterException; // used for testing if remote function ID is within permissible range

import java.io.IOException;import org.msgpack.MessagePack;import org.msgpack.packer.Packer;

import automationNetwork.Network;

import lpd8806.LPD8806;
    
/**
* WARNING: DO NOT MODIFY THIS CODE AS IT IS AUTO-GENERATED
*/    
public abstract class CustomDescriptor implements PeripheralDescriptor {
	
	private static final boolean DEBUG = false;
	/** return the type ID of this peripheral. */
	public static final int typeID = 47;
	public static final String xmlFileAddr = "LPD8806_LightStrip_Descriptor.xml";
	
	public static CustomDescriptor getInstance()
	{
		return new LPD8806();
	}
	
	/////////////
	// Triggers
	/////////////
	
	private static final String[] triggerStrings = new String[]
		{
			 
		};
	
	
	/////////////
	// Actions
	/////////////
	
	private static final String[] actionStrings = new String[]
		{
			
			"Color Chase",
			"Stop Color Chase" 
		};
	
	protected static final int COLORCHASE = 0;
	protected static final int COLORCHASESTOP = 1;
	
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
		return "LPD8806 LightStrip";
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
		
		
		if (COLORCHASE == actionIndex)
		{
			doColorChaseAction();
		} else
		if (COLORCHASESTOP == actionIndex)
		{
			stopColorChaseAction();
		} 	
		
		
		
	}
	
	/**
	 * Call this function to set the flag for a particular trigger. This
	 * means that actions that rely on this trigger in the service manager
	 * will be eligible to be called.
	 * @param triggerID the constant representing the trigger. One of: .
    
	 * @param arguments the ',' separated <code>String</code> arguments. Since this parameter 
	 * is a <code>varargs</code>, it is possible to simply call 
	 * <p><code>didTrigger("someString")</code></p> <p>if you are sending a trigger
	 * that has no arguments. 
	 */
	public void didTrigger(int triggerID, String...arguments) 
	{
		switch (triggerID) {
				
		default:
			break;
		}
	}
	
	/**
	 * Call this function to clear the flag for a particular trigger. This
	 * means that actions that rely on this trigger in the service manager
	 * will no longer be eligible to be called.
	 * @param triggerID the constant representing the trigger. One of:.
     
	 * @param arguments the ',' separated <code>String</code> arguments. Since this parameter 
	 * is a <code>varargs</code>, it is possible to simply call 
	 * <p><code>didTrigger("someString")</code></p> <p>if you are sending a trigger</p> 
	 * that has no arguments. 
	 */
	public void cancelTrigger(int triggerID, String...arguments)
	{
		switch (triggerID) {
				
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
	
	
	public abstract void doColorChaseAction();
	
	public abstract void stopColorChaseAction();
		
	
	/////////////////////
	// Status Functions
	/////////////////////
	
	
		
	/////////////////////
	// Remote functions
	/////////////////////
	
	/**
	 * Execute a function on the physical peripheral.
	 */
	public void colorChaseStartRemote()
	{
		int id = mPeripheral.getPeripheralID();
		
		// get a reference to the network we want to use
		Network mNetwork = mPeripheral.getNetwork();
		
		
			
			int functionID = 1;
			//if (functionID < 128 || functionID > 255)
			//{
			//	throw new InvalidParameterException("functionID must be between 128 and 255. Please correct it in file:/C:/Users/Paul/Workspaces/Java/CapstoneProject/MyCoolPeripheral/LPD8806_LightStrip_Descriptor.xml.");
			//}
			
			mNetwork.sendPacket(id, new byte[]{(byte)functionID});
			
	}
	
	
	/**
	 * Execute a function on the physical peripheral.
	 */
	public void colorChaseStopRemote()
	{
		int id = mPeripheral.getPeripheralID();
		
		// get a reference to the network we want to use
		Network mNetwork = mPeripheral.getNetwork();
		
		
			
			int functionID = 2;
			//if (functionID < 128 || functionID > 255)
			//{
			//	throw new InvalidParameterException("functionID must be between 128 and 255. Please correct it in file:/C:/Users/Paul/Workspaces/Java/CapstoneProject/MyCoolPeripheral/LPD8806_LightStrip_Descriptor.xml.");
			//}
			
			mNetwork.sendPacket(id, new byte[]{(byte)functionID});
			
	}
	
	
	
	/**
	 * Test that <code>arguments</code> is a valid set of arguments for the action
	 * <code>actionID</code>.
	 * @param actionID an <code>int</code> representing one of: <code>COLORCHASE</code>, <code>COLORCHASESTOP</code>.
    
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
	 * @param triggerID an <code>int</code> representing one of .
    
	 * @param arguments the ',' separated <code>String</code> arguments that are being tested.
	 * @return <code>true</code> if the combination of <code>triggerID</code> and 
	 * <code>arguments</code> is valid, <code>false</code> otherwise.
	 */
	protected abstract boolean validateTrigger(int triggerID, String... arguments);
   
}
    