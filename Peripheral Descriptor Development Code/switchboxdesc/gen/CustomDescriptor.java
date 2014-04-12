
package switchboxdesc.gen;


import peripherals.*;

//import java.security.InvalidParameterException; // used for testing if remote function ID is within permissible range

import java.util.Arrays;

import java.io.ByteArrayInputStream;import java.io.ByteArrayOutputStream;import java.io.IOException;import org.msgpack.MessagePack;import org.msgpack.packer.Packer;import org.msgpack.unpacker.Unpacker;

import automationNetwork.Network;

import switchboxdesc.SwitchBoxDesc;
    
/**
* WARNING: DO NOT MODIFY THIS CODE AS IT IS AUTO-GENERATED
*/    
public abstract class CustomDescriptor implements PeripheralDescriptor {
	
	private static final boolean DEBUG = false;
	/** return the type ID of this peripheral. */
	public static final int typeID = 6;
	public static final String xmlFileAddr = "SwitchBoxDescriptor.xml";
	
	public static CustomDescriptor getInstance()
	{
		return new SwitchBoxDesc();
	}
	
	/////////////
	// Triggers
	/////////////
	
	private static final String[] triggerStrings = new String[]
		{
			
			"Switch 1",
			"Switch 2",
			"Switch 3",
			"Dial" 
		};
	
	protected static final int SWITCH_1 = 0;
	protected static final int SWITCH_2 = 1;
	protected static final int SWITCH_3 = 2;
	protected static final int DIAL = 3;
	
	/////////////
	// Actions
	/////////////
	
	private static final String[] actionStrings = new String[]
		{
			 
		};
	
	
	////////////////
	// Callback IDs
	////////////////
	
	/**Received some data from the physical peripheral. Call 'switch1ChangedToCallBack()'*/
	public static final int SWITCH1CHANGEDTO = 1;
	
	/**Received some data from the physical peripheral. Call 'switch2ChangedToCallBack()'*/
	public static final int SWITCH2CHANGEDTO = 2;
	
	/**Received some data from the physical peripheral. Call 'switch3ChangedToCallBack()'*/
	public static final int SWITCH3CHANGEDTO = 3;
	
	/**Received some data from the physical peripheral. Call 'dialChanged2CallBack()'*/
	public static final int DIALCHANGED2 = 4;
	
	
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
		return "Switch Box";
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
		
			
		
		
		
	}
	
	/**
	 * Call this function to set the flag for a particular trigger. This
	 * means that actions that rely on this trigger in the service manager
	 * will be eligible to be called.
	 * @param triggerID the constant representing the trigger. One of: <code>SWITCH_1</code>, <code>SWITCH_2</code>, <code>SWITCH_3</code>, <code>DIAL</code>.
    
	 * @param arguments the ',' separated <code>String</code> arguments. Since this parameter 
	 * is a <code>varargs</code>, it is possible to simply call 
	 * <p><code>didTrigger("someString")</code></p> <p>if you are sending a trigger
	 * that has no arguments. 
	 */
	public void didTrigger(int triggerID, String...arguments) 
	{
		switch (triggerID) {
		
		case SWITCH_1:
			if (DEBUG) 
			{
				System.out.println("Switch 1-------");
			}						
			delegate.triggerOccurred(true, mPeripheral, triggerStrings[triggerID], arguments);
			break;
		
		case SWITCH_2:
			if (DEBUG) 
			{
				System.out.println("Switch 2-------");
			}						
			delegate.triggerOccurred(true, mPeripheral, triggerStrings[triggerID], arguments);
			break;
		
		case SWITCH_3:
			if (DEBUG) 
			{
				System.out.println("Switch 3-------");
			}						
			delegate.triggerOccurred(true, mPeripheral, triggerStrings[triggerID], arguments);
			break;
		
		case DIAL:
			if (DEBUG) 
			{
				System.out.println("Dial-------");
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
	 * @param triggerID the constant representing the trigger. One of:<code>SWITCH_1</code>, <code>SWITCH_2</code>, <code>SWITCH_3</code>, <code>DIAL</code>.
     
	 * @param arguments the ',' separated <code>String</code> arguments. Since this parameter 
	 * is a <code>varargs</code>, it is possible to simply call 
	 * <p><code>didTrigger("someString")</code></p> <p>if you are sending a trigger</p> 
	 * that has no arguments. 
	 */
	public void cancelTrigger(int triggerID, String...arguments)
	{
		switch (triggerID) {
		
		case SWITCH_1:
			if (DEBUG) 
			{
				System.out.println("cancel ------Switch 1-------");
			}						
			delegate.triggerOccurred(false, mPeripheral, triggerStrings[triggerID], arguments);
			break;
		
		case SWITCH_2:
			if (DEBUG) 
			{
				System.out.println("cancel ------Switch 2-------");
			}						
			delegate.triggerOccurred(false, mPeripheral, triggerStrings[triggerID], arguments);
			break;
		
		case SWITCH_3:
			if (DEBUG) 
			{
				System.out.println("cancel ------Switch 3-------");
			}						
			delegate.triggerOccurred(false, mPeripheral, triggerStrings[triggerID], arguments);
			break;
		
		case DIAL:
			if (DEBUG) 
			{
				System.out.println("cancel ------Dial-------");
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
				
				if (SWITCH1CHANGEDTO == data[0])
				{
					if (null != unpacker)
					{
						Byte  someData0 = unpacker.readByte(); // parse parameters
						switch1ChangedToCallBack(someData0);
					}
					return true;
				} else
				if (SWITCH2CHANGEDTO == data[0])
				{
					if (null != unpacker)
					{
						Byte  someData0 = unpacker.readByte(); // parse parameters
						switch2ChangedToCallBack(someData0);
					}
					return true;
				} else
				if (SWITCH3CHANGEDTO == data[0])
				{
					if (null != unpacker)
					{
						Byte  someData0 = unpacker.readByte(); // parse parameters
						switch3ChangedToCallBack(someData0);
					}
					return true;
				} else
				if (DIALCHANGED2 == data[0])
				{
					if (null != unpacker)
					{
						Byte  someData0 = unpacker.readByte(); // parse parameters
						dialChanged2CallBack(someData0);
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
	
		
	
	/////////////////////
	// Status Functions
	/////////////////////
	
	
		
	/////////////////////
	// Remote functions
	/////////////////////
	
	/**
	 * Execute a function on the physical peripheral.
	 */
	public void setEnabledRemote(Byte set)
	{
		int id = mPeripheral.getPeripheralID();
		
		// get a reference to the network we want to use
		Network mNetwork = mPeripheral.getNetwork();
		
		// package up the parameters
		MessagePack pack = new MessagePack();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Packer mPacker = pack.createPacker(outputStream);
	try { 
			mPacker.write(set);
			byte[] params = outputStream.toByteArray();
			// build the byte array to send to the remote peripheral
			if (params.length > 255) 
			{
				throw new ArrayIndexOutOfBoundsException("too much data: cannot send a payload larger than 255 bytes.");
			}
			
			int functionID = 1;
			//if (functionID < 128 || functionID > 255)
			//{
			//	throw new InvalidParameterException("functionID must be between 128 and 255. Please correct it in file:/C:/Users/Paul/Workspaces/Java/CapstoneProject/MyCoolPeripheral/SwitchBoxDescriptor.xml.");
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
	 * Received some data from the physical peripheral.
	 */
	public abstract void switch1ChangedToCallBack(Byte switchState);
	
	/**
	 * Received some data from the physical peripheral.
	 */
	public abstract void switch2ChangedToCallBack(Byte switchState);
	
	/**
	 * Received some data from the physical peripheral.
	 */
	public abstract void switch3ChangedToCallBack(Byte switchState);
	
	/**
	 * Received some data from the physical peripheral.
	 */
	public abstract void dialChanged2CallBack(Byte dialState);
	
	
	/**
	 * Test that <code>arguments</code> is a valid set of arguments for the action
	 * <code>actionID</code>.
	 * @param actionID an <code>int</code> representing one of: .
    
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
	 * @param triggerID an <code>int</code> representing one of <code>SWITCH_1</code>, <code>SWITCH_2</code>, <code>SWITCH_3</code>, <code>DIAL</code>.
    
	 * @param arguments the ',' separated <code>String</code> arguments that are being tested.
	 * @return <code>true</code> if the combination of <code>triggerID</code> and 
	 * <code>arguments</code> is valid, <code>false</code> otherwise.
	 */
	protected abstract boolean validateTrigger(int triggerID, String... arguments);
   
}
    