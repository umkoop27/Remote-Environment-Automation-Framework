
package compoundperipheral.gen;


import peripherals.*;

//import java.security.InvalidParameterException; // used for testing if remote function ID is within permissible range

import java.util.Arrays;

import java.io.ByteArrayInputStream;import java.io.IOException;import org.msgpack.MessagePack;import org.msgpack.packer.Packer;import org.msgpack.unpacker.Unpacker;

import automationNetwork.Network;

import compoundperipheral.CompoundPeripheral;
    
/**
* WARNING: DO NOT MODIFY THIS CODE AS IT IS AUTO-GENERATED
*/    
public abstract class CustomDescriptor implements PeripheralDescriptor {
	
	private static final boolean DEBUG = false;
	/** return the type ID of this peripheral. */
	public static final int typeID = 49;
	public static final String xmlFileAddr = "Compound_Peripheral_Descriptor.xml";
	
	public static CustomDescriptor getInstance()
	{
		return new CompoundPeripheral();
	}
	
	/////////////
	// Triggers
	/////////////
	
	private static final String[] triggerStrings = new String[]
		{
			
			"Magnetic Contact Toggle",
			"Magnetic Contact Closed",
			"Magnetic Contact Open" 
		};
	
	protected static final int MAGNETIC_CONTACT_TOGGLE = 0;
	protected static final int MAGNETIC_CONTACT_CLOSED = 1;
	protected static final int MAGNETIC_CONTACT_OPEN = 2;
	
	/////////////
	// Actions
	/////////////
	
	private static final String[] actionStrings = new String[]
		{
			
			"Activate Buzzer",
			"Deactivate Buzzer",
			"Activate Appliance 1",
			"Deactivate Appliance 1",
			"Activate Appliance 2",
			"Deactivate Appliance 2",
			"Activate Appliance 3",
			"Deactivate Appliance 3" 
		};
	
	protected static final int SET_BUZZER_ON = 0;
	protected static final int SET_BUZZER_OFF = 1;
	protected static final int SET_APPL1_ON = 2;
	protected static final int SET_APPL1_OFF = 3;
	protected static final int SET_APPL2_ON = 4;
	protected static final int SET_APPL2_OFF = 5;
	protected static final int SET_APPL3_ON = 6;
	protected static final int SET_APPL3_OFF = 7;
	
	////////////////
	// Callback IDs
	////////////////
	
	/**Received some data from the physical peripheral. Call 'receiveMagneticContactStateCallBack()'*/
	public static final int RECEIVEMAGNETICCONTACTSTATE = 14;
	
	/**Received some data from the physical peripheral. Call 'receiveBuzzerStateCallBack()'*/
	public static final int RECEIVEBUZZERSTATE = 15;
	
	/**Received some data from the physical peripheral. Call 'receiveAppliance1StateCallBack()'*/
	public static final int RECEIVEAPPLIANCE1STATE = 16;
	
	/**Received some data from the physical peripheral. Call 'receiveAppliance2StateCallBack()'*/
	public static final int RECEIVEAPPLIANCE2STATE = 17;
	
	/**Received some data from the physical peripheral. Call 'receiveAppliance3StateCallBack()'*/
	public static final int RECEIVEAPPLIANCE3STATE = 18;
	
	
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
		return "Compound Peripheral";
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
		
		case "Magnetic Contact State":
			outputStatus = getMagneticContactStateStatus();
			break;
		
		case "Buzzer State":
			outputStatus = getBuzzerStateStatus();
			break;
		
		case "Appliance 1 State":
			outputStatus = getAppliance1StateStatus();
			break;
		
		case "Appliance 2 State":
			outputStatus = getAppliance2StateStatus();
			break;
		
		case "Appliance 3 State":
			outputStatus = getAppliance3StateStatus();
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
		
		
		if (SET_BUZZER_ON == actionIndex)
		{
			setBuzzerOnAction();
		} else
		if (SET_BUZZER_OFF == actionIndex)
		{
			setBuzzerOffAction();
		} else
		if (SET_APPL1_ON == actionIndex)
		{
			setAppliance1OnAction();
		} else
		if (SET_APPL1_OFF == actionIndex)
		{
			setAppliance1OffAction();
		} else
		if (SET_APPL2_ON == actionIndex)
		{
			setAppliance2OnAction();
		} else
		if (SET_APPL2_OFF == actionIndex)
		{
			setAppliance2OffAction();
		} else
		if (SET_APPL3_ON == actionIndex)
		{
			setAppliance3OnAction();
		} else
		if (SET_APPL3_OFF == actionIndex)
		{
			setAppliance3OffAction();
		} 	
		
		
		
	}
	
	/**
	 * Call this function to set the flag for a particular trigger. This
	 * means that actions that rely on this trigger in the service manager
	 * will be eligible to be called.
	 * @param triggerID the constant representing the trigger. One of: <code>MAGNETIC_CONTACT_TOGGLE</code>, <code>MAGNETIC_CONTACT_CLOSED</code>, <code>MAGNETIC_CONTACT_OPEN</code>.
    
	 * @param arguments the ',' separated <code>String</code> arguments. Since this parameter 
	 * is a <code>varargs</code>, it is possible to simply call 
	 * <p><code>didTrigger("someString")</code></p> <p>if you are sending a trigger
	 * that has no arguments. 
	 */
	public void didTrigger(int triggerID, String...arguments) 
	{
		switch (triggerID) {
		
		case MAGNETIC_CONTACT_TOGGLE:
			if (DEBUG) 
			{
				System.out.println("Magnetic Contact Toggle-------");
			}						
			delegate.triggerOccurred(true, mPeripheral, triggerStrings[triggerID], arguments);
			break;
		
		case MAGNETIC_CONTACT_CLOSED:
			if (DEBUG) 
			{
				System.out.println("Magnetic Contact Closed-------");
			}						
			delegate.triggerOccurred(true, mPeripheral, triggerStrings[triggerID], arguments);
			break;
		
		case MAGNETIC_CONTACT_OPEN:
			if (DEBUG) 
			{
				System.out.println("Magnetic Contact Open-------");
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
	 * @param triggerID the constant representing the trigger. One of:<code>MAGNETIC_CONTACT_TOGGLE</code>, <code>MAGNETIC_CONTACT_CLOSED</code>, <code>MAGNETIC_CONTACT_OPEN</code>.
     
	 * @param arguments the ',' separated <code>String</code> arguments. Since this parameter 
	 * is a <code>varargs</code>, it is possible to simply call 
	 * <p><code>didTrigger("someString")</code></p> <p>if you are sending a trigger</p> 
	 * that has no arguments. 
	 */
	public void cancelTrigger(int triggerID, String...arguments)
	{
		switch (triggerID) {
		
		case MAGNETIC_CONTACT_TOGGLE:
			if (DEBUG) 
			{
				System.out.println("cancel ------Magnetic Contact Toggle-------");
			}						
			delegate.triggerOccurred(false, mPeripheral, triggerStrings[triggerID], arguments);
			break;
		
		case MAGNETIC_CONTACT_CLOSED:
			if (DEBUG) 
			{
				System.out.println("cancel ------Magnetic Contact Closed-------");
			}						
			delegate.triggerOccurred(false, mPeripheral, triggerStrings[triggerID], arguments);
			break;
		
		case MAGNETIC_CONTACT_OPEN:
			if (DEBUG) 
			{
				System.out.println("cancel ------Magnetic Contact Open-------");
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
	
		try 
		{
			byte[] parameters = new byte[0];
			if (data.length > 0)
			{
				Unpacker unpacker = null;
				
				// get the parameters from data
				if (data.length > 1) 
				{
					parameters = Arrays.copyOfRange(data, 1, data.length);
					ByteArrayInputStream in = new ByteArrayInputStream(parameters);
					MessagePack pack = new MessagePack();
					unpacker = pack.createUnpacker(in);
				}			
				
				if (RECEIVEMAGNETICCONTACTSTATE == data[0])
				{
					if (null != unpacker)
					{
						Byte  someData0 = unpacker.readByte(); // parse parameters
						receiveMagneticContactStateCallBack(someData0);
					}
					return true;
				} else
				if (RECEIVEBUZZERSTATE == data[0])
				{
					if (null != unpacker)
					{
						Byte  someData0 = unpacker.readByte(); // parse parameters
						receiveBuzzerStateCallBack(someData0);
					}
					return true;
				} else
				if (RECEIVEAPPLIANCE1STATE == data[0])
				{
					if (null != unpacker)
					{
						Byte  someData0 = unpacker.readByte(); // parse parameters
						receiveAppliance1StateCallBack(someData0);
					}
					return true;
				} else
				if (RECEIVEAPPLIANCE2STATE == data[0])
				{
					if (null != unpacker)
					{
						Byte  someData0 = unpacker.readByte(); // parse parameters
						receiveAppliance2StateCallBack(someData0);
					}
					return true;
				} else
				if (RECEIVEAPPLIANCE3STATE == data[0])
				{
					if (null != unpacker)
					{
						Byte  someData0 = unpacker.readByte(); // parse parameters
						receiveAppliance3StateCallBack(someData0);
					}
					return true;
				} 
				
			}
		} catch (IOException exception)
		{
			if (DEBUG) {
				System.out.println("Could not read parameters. not calling callback.");
			}
		}
		
		return false;
	}

	/////////////////////
	// Action Functions 
	/////////////////////
	
	
	public abstract void setBuzzerOnAction();
	
	public abstract void setBuzzerOffAction();
	
	public abstract void setAppliance1OnAction();
	
	public abstract void setAppliance1OffAction();
	
	public abstract void setAppliance2OnAction();
	
	public abstract void setAppliance2OffAction();
	
	public abstract void setAppliance3OnAction();
	
	public abstract void setAppliance3OffAction();
		
	
	/////////////////////
	// Status Functions
	/////////////////////
	
	/**
	 * @return a <code>String</code> that describes the status field 'Magnetic Contact State'.
	 */
	public abstract String getMagneticContactStateStatus();
	
	/**
	 * @return a <code>String</code> that describes the status field 'Buzzer State'.
	 */
	public abstract String getBuzzerStateStatus();
	
	/**
	 * @return a <code>String</code> that describes the status field 'Appliance 1 State'.
	 */
	public abstract String getAppliance1StateStatus();
	
	/**
	 * @return a <code>String</code> that describes the status field 'Appliance 2 State'.
	 */
	public abstract String getAppliance2StateStatus();
	
	/**
	 * @return a <code>String</code> that describes the status field 'Appliance 3 State'.
	 */
	public abstract String getAppliance3StateStatus();
	
	
		
	/////////////////////
	// Remote functions
	/////////////////////
	
	/**
	 * Execute a function on the physical peripheral.
	 */
	public void requestMagneticContactStateRemote()
	{
		int id = mPeripheral.getPeripheralID();
		
		// get a reference to the network we want to use
		Network mNetwork = mPeripheral.getNetwork();
		
		
			
			int functionID = 1;
			//if (functionID < 128 || functionID > 255)
			//{
			//	throw new InvalidParameterException("functionID must be between 128 and 255. Please correct it in file:/C:/Users/Paul/Workspaces/Java/CapstoneProject/MyCoolPeripheral/Compound_Peripheral_Descriptor.xml.");
			//}
			
			mNetwork.sendPacket(id, new byte[]{(byte)functionID});
			
	}
	
	
	/**
	 * Execute a function on the physical peripheral.
	 */
	public void requestBuzzerStateRemote()
	{
		int id = mPeripheral.getPeripheralID();
		
		// get a reference to the network we want to use
		Network mNetwork = mPeripheral.getNetwork();
		
		
			
			int functionID = 2;
			//if (functionID < 128 || functionID > 255)
			//{
			//	throw new InvalidParameterException("functionID must be between 128 and 255. Please correct it in file:/C:/Users/Paul/Workspaces/Java/CapstoneProject/MyCoolPeripheral/Compound_Peripheral_Descriptor.xml.");
			//}
			
			mNetwork.sendPacket(id, new byte[]{(byte)functionID});
			
	}
	
	
	/**
	 * Execute a function on the physical peripheral.
	 */
	public void setBuzzerOnRemote()
	{
		int id = mPeripheral.getPeripheralID();
		
		// get a reference to the network we want to use
		Network mNetwork = mPeripheral.getNetwork();
		
		
			
			int functionID = 3;
			//if (functionID < 128 || functionID > 255)
			//{
			//	throw new InvalidParameterException("functionID must be between 128 and 255. Please correct it in file:/C:/Users/Paul/Workspaces/Java/CapstoneProject/MyCoolPeripheral/Compound_Peripheral_Descriptor.xml.");
			//}
			
			mNetwork.sendPacket(id, new byte[]{(byte)functionID});
			
	}
	
	
	/**
	 * Execute a function on the physical peripheral.
	 */
	public void setBuzzerOffRemote()
	{
		int id = mPeripheral.getPeripheralID();
		
		// get a reference to the network we want to use
		Network mNetwork = mPeripheral.getNetwork();
		
		
			
			int functionID = 4;
			//if (functionID < 128 || functionID > 255)
			//{
			//	throw new InvalidParameterException("functionID must be between 128 and 255. Please correct it in file:/C:/Users/Paul/Workspaces/Java/CapstoneProject/MyCoolPeripheral/Compound_Peripheral_Descriptor.xml.");
			//}
			
			mNetwork.sendPacket(id, new byte[]{(byte)functionID});
			
	}
	
	
	/**
	 * Execute a function on the physical peripheral.
	 */
	public void requestAppliance1StateRemote()
	{
		int id = mPeripheral.getPeripheralID();
		
		// get a reference to the network we want to use
		Network mNetwork = mPeripheral.getNetwork();
		
		
			
			int functionID = 5;
			//if (functionID < 128 || functionID > 255)
			//{
			//	throw new InvalidParameterException("functionID must be between 128 and 255. Please correct it in file:/C:/Users/Paul/Workspaces/Java/CapstoneProject/MyCoolPeripheral/Compound_Peripheral_Descriptor.xml.");
			//}
			
			mNetwork.sendPacket(id, new byte[]{(byte)functionID});
			
	}
	
	
	/**
	 * Execute a function on the physical peripheral.
	 */
	public void setAppliance1OnRemote()
	{
		int id = mPeripheral.getPeripheralID();
		
		// get a reference to the network we want to use
		Network mNetwork = mPeripheral.getNetwork();
		
		
			
			int functionID = 6;
			//if (functionID < 128 || functionID > 255)
			//{
			//	throw new InvalidParameterException("functionID must be between 128 and 255. Please correct it in file:/C:/Users/Paul/Workspaces/Java/CapstoneProject/MyCoolPeripheral/Compound_Peripheral_Descriptor.xml.");
			//}
			
			mNetwork.sendPacket(id, new byte[]{(byte)functionID});
			
	}
	
	
	/**
	 * Execute a function on the physical peripheral.
	 */
	public void setAppliance1OffRemote()
	{
		int id = mPeripheral.getPeripheralID();
		
		// get a reference to the network we want to use
		Network mNetwork = mPeripheral.getNetwork();
		
		
			
			int functionID = 7;
			//if (functionID < 128 || functionID > 255)
			//{
			//	throw new InvalidParameterException("functionID must be between 128 and 255. Please correct it in file:/C:/Users/Paul/Workspaces/Java/CapstoneProject/MyCoolPeripheral/Compound_Peripheral_Descriptor.xml.");
			//}
			
			mNetwork.sendPacket(id, new byte[]{(byte)functionID});
			
	}
	
	
	/**
	 * Execute a function on the physical peripheral.
	 */
	public void requestAppliance2StateRemote()
	{
		int id = mPeripheral.getPeripheralID();
		
		// get a reference to the network we want to use
		Network mNetwork = mPeripheral.getNetwork();
		
		
			
			int functionID = 8;
			//if (functionID < 128 || functionID > 255)
			//{
			//	throw new InvalidParameterException("functionID must be between 128 and 255. Please correct it in file:/C:/Users/Paul/Workspaces/Java/CapstoneProject/MyCoolPeripheral/Compound_Peripheral_Descriptor.xml.");
			//}
			
			mNetwork.sendPacket(id, new byte[]{(byte)functionID});
			
	}
	
	
	/**
	 * Execute a function on the physical peripheral.
	 */
	public void setAppliance2OnRemote()
	{
		int id = mPeripheral.getPeripheralID();
		
		// get a reference to the network we want to use
		Network mNetwork = mPeripheral.getNetwork();
		
		
			
			int functionID = 9;
			//if (functionID < 128 || functionID > 255)
			//{
			//	throw new InvalidParameterException("functionID must be between 128 and 255. Please correct it in file:/C:/Users/Paul/Workspaces/Java/CapstoneProject/MyCoolPeripheral/Compound_Peripheral_Descriptor.xml.");
			//}
			
			mNetwork.sendPacket(id, new byte[]{(byte)functionID});
			
	}
	
	
	/**
	 * Execute a function on the physical peripheral.
	 */
	public void setAppliance2OffRemote()
	{
		int id = mPeripheral.getPeripheralID();
		
		// get a reference to the network we want to use
		Network mNetwork = mPeripheral.getNetwork();
		
		
			
			int functionID = 10;
			//if (functionID < 128 || functionID > 255)
			//{
			//	throw new InvalidParameterException("functionID must be between 128 and 255. Please correct it in file:/C:/Users/Paul/Workspaces/Java/CapstoneProject/MyCoolPeripheral/Compound_Peripheral_Descriptor.xml.");
			//}
			
			mNetwork.sendPacket(id, new byte[]{(byte)functionID});
			
	}
	
	
	/**
	 * Execute a function on the physical peripheral.
	 */
	public void requestAppliance3StateRemote()
	{
		int id = mPeripheral.getPeripheralID();
		
		// get a reference to the network we want to use
		Network mNetwork = mPeripheral.getNetwork();
		
		
			
			int functionID = 11;
			//if (functionID < 128 || functionID > 255)
			//{
			//	throw new InvalidParameterException("functionID must be between 128 and 255. Please correct it in file:/C:/Users/Paul/Workspaces/Java/CapstoneProject/MyCoolPeripheral/Compound_Peripheral_Descriptor.xml.");
			//}
			
			mNetwork.sendPacket(id, new byte[]{(byte)functionID});
			
	}
	
	
	/**
	 * Execute a function on the physical peripheral.
	 */
	public void setAppliance3OnRemote()
	{
		int id = mPeripheral.getPeripheralID();
		
		// get a reference to the network we want to use
		Network mNetwork = mPeripheral.getNetwork();
		
		
			
			int functionID = 12;
			//if (functionID < 128 || functionID > 255)
			//{
			//	throw new InvalidParameterException("functionID must be between 128 and 255. Please correct it in file:/C:/Users/Paul/Workspaces/Java/CapstoneProject/MyCoolPeripheral/Compound_Peripheral_Descriptor.xml.");
			//}
			
			mNetwork.sendPacket(id, new byte[]{(byte)functionID});
			
	}
	
	
	/**
	 * Execute a function on the physical peripheral.
	 */
	public void setAppliance3OffRemote()
	{
		int id = mPeripheral.getPeripheralID();
		
		// get a reference to the network we want to use
		Network mNetwork = mPeripheral.getNetwork();
		
		
			
			int functionID = 13;
			//if (functionID < 128 || functionID > 255)
			//{
			//	throw new InvalidParameterException("functionID must be between 128 and 255. Please correct it in file:/C:/Users/Paul/Workspaces/Java/CapstoneProject/MyCoolPeripheral/Compound_Peripheral_Descriptor.xml.");
			//}
			
			mNetwork.sendPacket(id, new byte[]{(byte)functionID});
			
	}
	
	
	/**
	 * Received some data from the physical peripheral.
	 */
	public abstract void receiveMagneticContactStateCallBack(Byte state);
	
	/**
	 * Received some data from the physical peripheral.
	 */
	public abstract void receiveBuzzerStateCallBack(Byte state);
	
	/**
	 * Received some data from the physical peripheral.
	 */
	public abstract void receiveAppliance1StateCallBack(Byte state);
	
	/**
	 * Received some data from the physical peripheral.
	 */
	public abstract void receiveAppliance2StateCallBack(Byte state);
	
	/**
	 * Received some data from the physical peripheral.
	 */
	public abstract void receiveAppliance3StateCallBack(Byte state);
	
	
	/**
	 * Test that <code>arguments</code> is a valid set of arguments for the action
	 * <code>actionID</code>.
	 * @param actionID an <code>int</code> representing one of: <code>SET_BUZZER_ON</code>, <code>SET_BUZZER_OFF</code>, <code>SET_APPL1_ON</code>, <code>SET_APPL1_OFF</code>, <code>SET_APPL2_ON</code>, <code>SET_APPL2_OFF</code>, <code>SET_APPL3_ON</code>, <code>SET_APPL3_OFF</code>.
    
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
	 * @param triggerID an <code>int</code> representing one of <code>MAGNETIC_CONTACT_TOGGLE</code>, <code>MAGNETIC_CONTACT_CLOSED</code>, <code>MAGNETIC_CONTACT_OPEN</code>.
    
	 * @param arguments the ',' separated <code>String</code> arguments that are being tested.
	 * @return <code>true</code> if the combination of <code>triggerID</code> and 
	 * <code>arguments</code> is valid, <code>false</code> otherwise.
	 */
	protected abstract boolean validateTrigger(int triggerID, String... arguments);
   
}
    