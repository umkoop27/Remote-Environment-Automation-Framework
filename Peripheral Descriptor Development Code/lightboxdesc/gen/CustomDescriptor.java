
package lightboxdesc.gen;


import peripherals.*;

//import java.security.InvalidParameterException; // used for testing if remote function ID is within permissible range

import java.io.ByteArrayOutputStream;import java.io.IOException;import org.msgpack.MessagePack;import org.msgpack.packer.Packer;

import automationNetwork.Network;

import lightboxdesc.LightBoxDesc;
    
/**
* WARNING: DO NOT MODIFY THIS CODE AS IT IS AUTO-GENERATED
*/    
public abstract class CustomDescriptor implements PeripheralDescriptor {
	
	private static final boolean DEBUG = false;
	/** return the type ID of this peripheral. */
	public static final int typeID = 19;
	public static final String xmlFileAddr = "LightBoxDesc.xml";
	
	public static CustomDescriptor getInstance()
	{
		return new LightBoxDesc();
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
			
			"Set Red",
			"Set White",
			"Set Yellow",
			"Set Green",
			"Set Green Intensity",
			"Set Blue",
			"Blink All",
			"All On",
			"All Off" 
		};
	
	protected static final int SET_RED = 0;
	protected static final int SET_WHITE = 1;
	protected static final int SET_YELLOW = 2;
	protected static final int SET_GREEN = 3;
	protected static final int SET_GREEN_INTENSITY = 4;
	protected static final int SET_BLUE = 5;
	protected static final int BLINK = 6;
	protected static final int ALL_ON = 7;
	protected static final int ALL_OFF = 8;
	
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
		return "Jonathan's super awesome light box";
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
		
		case "Light State":
			outputStatus = getLightStateStatus();
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
		
		
		if (SET_RED == actionIndex)
		{
			
			String actionArgument0 = arguments[0];
			setRedAction(actionArgument0);
		} else
		if (SET_WHITE == actionIndex)
		{
			
			String actionArgument0 = arguments[0];
			setWhiteAction(actionArgument0);
		} else
		if (SET_YELLOW == actionIndex)
		{
			
			String actionArgument0 = arguments[0];
			setYellowAction(actionArgument0);
		} else
		if (SET_GREEN == actionIndex)
		{
			
			String actionArgument0 = arguments[0];
			setGreenAction(actionArgument0);
		} else
		if (SET_GREEN_INTENSITY == actionIndex)
		{
			
			String actionArgument0 = arguments[0];
			setGreenIntensityAction(actionArgument0);
		} else
		if (SET_BLUE == actionIndex)
		{
			
			String actionArgument0 = arguments[0];
			setBlueAction(actionArgument0);
		} else
		if (BLINK == actionIndex)
		{
			
			String actionArgument0 = arguments[0];
			blinkAllAction(actionArgument0);
		} else
		if (ALL_ON == actionIndex)
		{
			allOnAction();
		} else
		if (ALL_OFF == actionIndex)
		{
			allOffAction();
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
	
	
	public abstract void setRedAction(String newState);
	
	public abstract void setWhiteAction(String newState);
	
	public abstract void setYellowAction(String newState);
	
	public abstract void setGreenAction(String newState);
	
	public abstract void setGreenIntensityAction(String brightness);
	
	public abstract void setBlueAction(String newState);
	
	public abstract void blinkAllAction(String timesToBlink);
	
	public abstract void allOnAction();
	
	public abstract void allOffAction();
		
	
	/////////////////////
	// Status Functions
	/////////////////////
	
	/**
	 * @return a <code>String</code> that describes the status field 'Light State'.
	 */
	public abstract String getLightStateStatus();
	
	
		
	/////////////////////
	// Remote functions
	/////////////////////
	
	/**
	 * Execute a function on the physical peripheral.
	 */
	public void redLightOnRemote()
	{
		int id = mPeripheral.getPeripheralID();
		
		// get a reference to the network we want to use
		Network mNetwork = mPeripheral.getNetwork();
		
		
			
			int functionID = 1;
			//if (functionID < 128 || functionID > 255)
			//{
			//	throw new InvalidParameterException("functionID must be between 128 and 255. Please correct it in file:/C:/Users/Paul/Workspaces/Java/CapstoneProject/MyCoolPeripheral/LightBoxDesc.xml.");
			//}
			
			mNetwork.sendPacket(id, new byte[]{(byte)functionID});
			
	}
	
	
	/**
	 * Execute a function on the physical peripheral.
	 */
	public void redLightOffRemote()
	{
		int id = mPeripheral.getPeripheralID();
		
		// get a reference to the network we want to use
		Network mNetwork = mPeripheral.getNetwork();
		
		
			
			int functionID = 2;
			//if (functionID < 128 || functionID > 255)
			//{
			//	throw new InvalidParameterException("functionID must be between 128 and 255. Please correct it in file:/C:/Users/Paul/Workspaces/Java/CapstoneProject/MyCoolPeripheral/LightBoxDesc.xml.");
			//}
			
			mNetwork.sendPacket(id, new byte[]{(byte)functionID});
			
	}
	
	
	/**
	 * Execute a function on the physical peripheral.
	 */
	public void whiteLightOnRemote()
	{
		int id = mPeripheral.getPeripheralID();
		
		// get a reference to the network we want to use
		Network mNetwork = mPeripheral.getNetwork();
		
		
			
			int functionID = 3;
			//if (functionID < 128 || functionID > 255)
			//{
			//	throw new InvalidParameterException("functionID must be between 128 and 255. Please correct it in file:/C:/Users/Paul/Workspaces/Java/CapstoneProject/MyCoolPeripheral/LightBoxDesc.xml.");
			//}
			
			mNetwork.sendPacket(id, new byte[]{(byte)functionID});
			
	}
	
	
	/**
	 * Execute a function on the physical peripheral.
	 */
	public void whiteLightOffRemote()
	{
		int id = mPeripheral.getPeripheralID();
		
		// get a reference to the network we want to use
		Network mNetwork = mPeripheral.getNetwork();
		
		
			
			int functionID = 4;
			//if (functionID < 128 || functionID > 255)
			//{
			//	throw new InvalidParameterException("functionID must be between 128 and 255. Please correct it in file:/C:/Users/Paul/Workspaces/Java/CapstoneProject/MyCoolPeripheral/LightBoxDesc.xml.");
			//}
			
			mNetwork.sendPacket(id, new byte[]{(byte)functionID});
			
	}
	
	
	/**
	 * Execute a function on the physical peripheral.
	 */
	public void yellowLightOnRemote()
	{
		int id = mPeripheral.getPeripheralID();
		
		// get a reference to the network we want to use
		Network mNetwork = mPeripheral.getNetwork();
		
		
			
			int functionID = 5;
			//if (functionID < 128 || functionID > 255)
			//{
			//	throw new InvalidParameterException("functionID must be between 128 and 255. Please correct it in file:/C:/Users/Paul/Workspaces/Java/CapstoneProject/MyCoolPeripheral/LightBoxDesc.xml.");
			//}
			
			mNetwork.sendPacket(id, new byte[]{(byte)functionID});
			
	}
	
	
	/**
	 * Execute a function on the physical peripheral.
	 */
	public void yellowLightOffRemote()
	{
		int id = mPeripheral.getPeripheralID();
		
		// get a reference to the network we want to use
		Network mNetwork = mPeripheral.getNetwork();
		
		
			
			int functionID = 6;
			//if (functionID < 128 || functionID > 255)
			//{
			//	throw new InvalidParameterException("functionID must be between 128 and 255. Please correct it in file:/C:/Users/Paul/Workspaces/Java/CapstoneProject/MyCoolPeripheral/LightBoxDesc.xml.");
			//}
			
			mNetwork.sendPacket(id, new byte[]{(byte)functionID});
			
	}
	
	
	/**
	 * Execute a function on the physical peripheral.
	 */
	public void greenLightOnRemote()
	{
		int id = mPeripheral.getPeripheralID();
		
		// get a reference to the network we want to use
		Network mNetwork = mPeripheral.getNetwork();
		
		
			
			int functionID = 7;
			//if (functionID < 128 || functionID > 255)
			//{
			//	throw new InvalidParameterException("functionID must be between 128 and 255. Please correct it in file:/C:/Users/Paul/Workspaces/Java/CapstoneProject/MyCoolPeripheral/LightBoxDesc.xml.");
			//}
			
			mNetwork.sendPacket(id, new byte[]{(byte)functionID});
			
	}
	
	
	/**
	 * Execute a function on the physical peripheral.
	 */
	public void greenLightOffRemote()
	{
		int id = mPeripheral.getPeripheralID();
		
		// get a reference to the network we want to use
		Network mNetwork = mPeripheral.getNetwork();
		
		
			
			int functionID = 8;
			//if (functionID < 128 || functionID > 255)
			//{
			//	throw new InvalidParameterException("functionID must be between 128 and 255. Please correct it in file:/C:/Users/Paul/Workspaces/Java/CapstoneProject/MyCoolPeripheral/LightBoxDesc.xml.");
			//}
			
			mNetwork.sendPacket(id, new byte[]{(byte)functionID});
			
	}
	
	
	/**
	 * Execute a function on the physical peripheral.
	 */
	public void blueLightOnRemote()
	{
		int id = mPeripheral.getPeripheralID();
		
		// get a reference to the network we want to use
		Network mNetwork = mPeripheral.getNetwork();
		
		
			
			int functionID = 9;
			//if (functionID < 128 || functionID > 255)
			//{
			//	throw new InvalidParameterException("functionID must be between 128 and 255. Please correct it in file:/C:/Users/Paul/Workspaces/Java/CapstoneProject/MyCoolPeripheral/LightBoxDesc.xml.");
			//}
			
			mNetwork.sendPacket(id, new byte[]{(byte)functionID});
			
	}
	
	
	/**
	 * Execute a function on the physical peripheral.
	 */
	public void blueLightOffRemote()
	{
		int id = mPeripheral.getPeripheralID();
		
		// get a reference to the network we want to use
		Network mNetwork = mPeripheral.getNetwork();
		
		
			
			int functionID = 10;
			//if (functionID < 128 || functionID > 255)
			//{
			//	throw new InvalidParameterException("functionID must be between 128 and 255. Please correct it in file:/C:/Users/Paul/Workspaces/Java/CapstoneProject/MyCoolPeripheral/LightBoxDesc.xml.");
			//}
			
			mNetwork.sendPacket(id, new byte[]{(byte)functionID});
			
	}
	
	
	/**
	 * Execute a function on the physical peripheral.
	 */
	public void blinkLightsRemote(Byte num)
	{
		int id = mPeripheral.getPeripheralID();
		
		// get a reference to the network we want to use
		Network mNetwork = mPeripheral.getNetwork();
		
		// package up the parameters
		MessagePack pack = new MessagePack();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Packer mPacker = pack.createPacker(outputStream);
	try { 
			mPacker.write(num);
			byte[] params = outputStream.toByteArray();
			// build the byte array to send to the remote peripheral
			if (params.length > 255) 
			{
				throw new ArrayIndexOutOfBoundsException("too much data: cannot send a payload larger than 255 bytes.");
			}
			
			int functionID = 11;
			//if (functionID < 128 || functionID > 255)
			//{
			//	throw new InvalidParameterException("functionID must be between 128 and 255. Please correct it in file:/C:/Users/Paul/Workspaces/Java/CapstoneProject/MyCoolPeripheral/LightBoxDesc.xml.");
			//}
			
			byte[] remoteFunctionData = new byte[params.length + 1];
			remoteFunctionData[0] = (byte)functionID;
			System.arraycopy(params, 0, remoteFunctionData, 1, params.length);	
			
			// send it off! :)
			mNetwork.sendPacket(id, remoteFunctionData);	} catch (IOException exception)
		{
			System.out.println("Could not output parameters. data not sent.");
		}
	}
	
	
	/**
	 * Execute a function on the physical peripheral.
	 */
	public void turnAllOnRemote()
	{
		int id = mPeripheral.getPeripheralID();
		
		// get a reference to the network we want to use
		Network mNetwork = mPeripheral.getNetwork();
		
		
			
			int functionID = 12;
			//if (functionID < 128 || functionID > 255)
			//{
			//	throw new InvalidParameterException("functionID must be between 128 and 255. Please correct it in file:/C:/Users/Paul/Workspaces/Java/CapstoneProject/MyCoolPeripheral/LightBoxDesc.xml.");
			//}
			
			mNetwork.sendPacket(id, new byte[]{(byte)functionID});
			
	}
	
	
	/**
	 * Execute a function on the physical peripheral.
	 */
	public void turnAllOffRemote()
	{
		int id = mPeripheral.getPeripheralID();
		
		// get a reference to the network we want to use
		Network mNetwork = mPeripheral.getNetwork();
		
		
			
			int functionID = 13;
			//if (functionID < 128 || functionID > 255)
			//{
			//	throw new InvalidParameterException("functionID must be between 128 and 255. Please correct it in file:/C:/Users/Paul/Workspaces/Java/CapstoneProject/MyCoolPeripheral/LightBoxDesc.xml.");
			//}
			
			mNetwork.sendPacket(id, new byte[]{(byte)functionID});
			
	}
	
	
	/**
	 * Execute a function on the physical peripheral.
	 */
	public void setGreenBrightnessRemote(Short value)
	{
		int id = mPeripheral.getPeripheralID();
		
		// get a reference to the network we want to use
		Network mNetwork = mPeripheral.getNetwork();
		
		// package up the parameters
		MessagePack pack = new MessagePack();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Packer mPacker = pack.createPacker(outputStream);
	try { 
			mPacker.write(value);
			byte[] params = outputStream.toByteArray();
			// build the byte array to send to the remote peripheral
			if (params.length > 255) 
			{
				throw new ArrayIndexOutOfBoundsException("too much data: cannot send a payload larger than 255 bytes.");
			}
			
			int functionID = 14;
			//if (functionID < 128 || functionID > 255)
			//{
			//	throw new InvalidParameterException("functionID must be between 128 and 255. Please correct it in file:/C:/Users/Paul/Workspaces/Java/CapstoneProject/MyCoolPeripheral/LightBoxDesc.xml.");
			//}
			
			byte[] remoteFunctionData = new byte[params.length + 1];
			remoteFunctionData[0] = (byte)functionID;
			System.arraycopy(params, 0, remoteFunctionData, 1, params.length);	
			
			// send it off! :)
			mNetwork.sendPacket(id, remoteFunctionData);	} catch (IOException exception)
		{
			System.out.println("Could not output parameters. data not sent.");
		}
	}
	
	
	
	/**
	 * Test that <code>arguments</code> is a valid set of arguments for the action
	 * <code>actionID</code>.
	 * @param actionID an <code>int</code> representing one of: <code>SET_RED</code>, <code>SET_WHITE</code>, <code>SET_YELLOW</code>, <code>SET_GREEN</code>, <code>SET_GREEN_INTENSITY</code>, <code>SET_BLUE</code>, <code>BLINK</code>, <code>ALL_ON</code>, <code>ALL_OFF</code>.
    
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
    