package peripherals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import automationNetwork.Network;

/**
 * Represent a Peripheral inside the Nexus. Custom functionality is added by means of a 
 * {@link peripherals.PeripheralDescriptor}. This class should not be extended. Each Peripheral
 * has its own threading to prevent blocking calls from tying up the main Jarvis thread.
 * @author Paul
 */
public final class Peripheral
{
	private String name;
	private String location;
	private int peripheralID;
	
	// synchronize on this object
	private PeripheralDescriptor descriptor;
	
	// allow remote communication
	private Network network;
	public final Network getNetwork()
	{
		return network;
	}
	
	/**
	 * Initialize a new instance of a Peripheral because a new device has been added to the 
	 * network.
	 * @param peripheralType This parameter determines which <code>PeripheralDescriptor</code> to use
	 * with this <code>Peripheral</code>. If <code>peripheralType</code> does not correspond to an actual
	 * <code>PeripheralDescriptor</code>, the user will be prompted to manually set a Descriptor.
	 * @param peripheralID This parameter sets the unique ID that will be associated to this <code>Peripheral</code>
	 * while it is in the network. This parameter should be determined by the base class.
	 * @param name A <code>String</code> that will be used to identify this Peripheral to the 
	 * user. //TODO: This might be stored in a lookup table instead and not ever directly associated to the Peripheral.
	 * @param delegate an object that implements the <code>triggerOccurred()</code> function.
	 */
	public Peripheral(int peripheralType, int peripheralID, String name, TriggerHandler delegate)
	{
		this(peripheralType, peripheralID, name, delegate, null);
	}
	
	/**
	 * Initialize a new instance of a Peripheral because a new device has been added to the 
	 * network.
	 * @param peripheralType This parameter determines which <code>PeripheralDescriptor</code> to use
	 * with this <code>Peripheral</code>. If <code>peripheralType</code> does not correspond to an actual
	 * <code>PeripheralDescriptor</code>, the user will be prompted to manually set a Descriptor.
	 * @param peripheralID This parameter sets the unique ID that will be associated to this <code>Peripheral</code>
	 * while it is in the network. This parameter should be determined by the base class.
	 * @param name A <code>String</code> that will be used to identify this Peripheral to the 
	 * user. //TODO: This might be stored in a lookup table instead and not ever directly associated to the Peripheral.
	 * @param delegate an object that implements the <code>triggerOccurred()</code> function.
	 * @param network a <code>Network</code> that contains the physical peripheral represented by this
	 * <code>Peripheral</code> object.
	 */
	public Peripheral(int peripheralType, int peripheralID, String name, TriggerHandler delegate, Network network)
	{
		this.peripheralID = peripheralID;
		this.name = name;
		this.network = network;
		
		// get a descriptor
		descriptor = DescriptorFactory.getDescriptor(peripheralType);
		
		if (null == descriptor) {
			//TODO: Prompt user to set the descriptor manually
			descriptor = DescriptorFactory.getDescriptor(0); // default for now
			if (null == descriptor)
			{
				System.out.println("default descriptor not loaded");
				return;
			}
			System.out.println("Descriptor " + peripheralType + " not found. Got descriptor: " + descriptor.getTypeID());
		} else {
			System.out.println("got descriptor: " + descriptor.getTypeID());
		}
		
		descriptor.setDelegate(delegate);
		descriptor.setPeripheral(this);
	}
	
	@Override
	public String toString()
	{
//		String actions = "";
//		for (String action : getActions().keySet())
//		{
//			actions += "  " + action + "\n";
//		}
//		
//		String triggers = "";
//		for (String trigger : getTriggers().keySet())
//		{
//			triggers += "  " + trigger + "\n";
//		}
		
		return getName() + " (" + getPeripheralID() + ")";
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getTypeString()
	{
		return descriptor.getTypeString();
	}
	
	public String getStatus(String statusString) 
	{
		return descriptor.getStatus(statusString);
	}
	
	public String getLocation()
	{
		return location;
	}
	
	public void setLocation(String newLocation)
	{
		location = newLocation;
	}
	
	public void setName(String newName)
	{
		this.name = newName;
	}
	
	/**
	 * Execute the action <code>action</code>. <code>action</code> runs in a separate
	 * thread where <code>descriptor</code> is used to provide synchronization.
	 * This means that calls to <code>descriptor</code> will not interleave.
	 * @param action the action to execute
	 * @param arguments the ',' separated <code>String</code> arguments. Since this 
	 * parameter is a <code>varargs</code>, it is possible to simply call 
	 * <p><code>doAction("someString")</code> <p>if you are calling an action 
	 * that has no arguments.
	 */
	public void doAction(final String action, final String...arguments)
	{		
		//TODO: either do this on a brand new (synchronized) thread or just delegate it to a PeripheralThread.
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() 
			{
				synchronized (descriptor) 
				{
					descriptor.doAction(action, arguments);
				}
				
			}
		});
		
		thread.start();
			
	}
	
	public int getPeripheralID()
	{
		return peripheralID;
	}
	
	/**
	 * Called from a lower layer when a packet is received. 
	 * Fork a thread to actually process the data and return.
	 * @param data
	 */
	public void receivePacket(final byte[] data)
	{
		//TODO: either do this on a brand new (synchronized) thread or just delegate it to a PeripheralThread.
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() 
			{
				synchronized (descriptor) 
				{
					descriptor.handlePacket(data);
				}
				
			}
		});
		
		thread.start();
	}
	
	/**
	 * Ensure that a particular set of <code>String</code>s is valid for a particular
	 * trigger string. If the arguments are valid, but not yet in the set of enabled 
	 * triggers, they will be added.
	 * @param triggerID the ID for the particular trigger that is being checked
	 * @param arguments the ',' separated <code>String</code> objects that are being 
	 * checked. Since this parameter is a <code>varargs</code>, it is possible to simply 
	 * call <p><code>validateTrigger("someString")</code> <p>if you are checking a trigger 
	 * that has no arguments.
	 * @return <code>true</code> if the combination of <code>triggerID</code> and 
	 * <code>arguments</code> exists already, or was successfully added. Returns
	 * </code>false</code> if the combination of <code>triggerID</code> and 
	 * <code>arguments</code> is not valid.
	 */
	public boolean validateTrigger(String triggerID, String...arguments)
	{
		return descriptor.validateTrigger(triggerID, arguments);
	}
	
	/**
	 * Ensure that a particular set of <code>String</code>s is valid for a particular
	 * action string.
	 * @param actionID the ID for the particular action that is being checked
	 * @param arguments the ',' separated <code>String</code> objects that are being 
	 * checked. Since this parameter is a <code>varargs</code>, it is possible to simply 
	 * call <p><code>validateAction("someString")</code> <p>if you are checking an action 
	 * that has no arguments.
	 * @return <code>true</code> if the combination of <code>actionID</code> and 
	 * <code>arguments</code> can be called. Otherwise, returns <code>false</code>.
	 */
	public boolean validateAction(String actionID, String...arguments)
	{
		return descriptor.validateAction(actionID, arguments);
	}
	
	/**
	 * @return the XML data that describes this peripheral. This is the XML that was
	 * provided by the developer.
	 */
	public String getXML()
	{
		String xmlAsString = "";
		
		ClassLoader cl = descriptor.getClass().getClassLoader();
		String target = descriptor.getXMLString();
		System.out.println(target);
		InputStream stream = cl.getResourceAsStream(target);
		if (null != stream)
		{
			xmlAsString = ""; // the XML file is found
			BufferedReader mBufferedReader = new BufferedReader(new InputStreamReader(stream));
			try {
				while (mBufferedReader.ready())
				{
					xmlAsString += mBufferedReader.readLine();
				}
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		
		return xmlAsString;
	}
	
//	/**
//	 * @return a group of <code>String</code>s that represent the various triggers that 
//	 * this <code>Peripheral</code> can send. Each <code>String</code> is mapped to a 
//	 * list of <code>Strings</code> that may be applied to that trigger. When a user
//	 * wants to use that trigger in a service, they should be prompted for values
//	 * for each of the arguments.
//	 * @deprecated use XML file instead
//	 */
//	@Deprecated
//	public Map<String, ArrayList<String>> getTriggers()
//	{
//		return descriptor.getTriggers();
//	}
//	
//	/**
//	 * @return a group of <code>String</code>s that represent the various actions that 
//	 * this <code>Peripheral</code> can handle. Each <code>String</code> is mapped to a 
//	 * list of <code>Strings</code> that may be applied to that action. When a user
//	 * wants to use that action in a service, they should be prompted for values for each
//	 * of the arguments.
//	 * @deprecated use XML file instead
//	 */
//	@Deprecated
//	public Map<String, ArrayList<String>> getActions()
//	{
//		return descriptor.getActions();
//	}	
}
