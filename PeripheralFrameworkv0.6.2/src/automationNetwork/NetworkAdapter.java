/**
 * 
 */
package automationNetwork;

import java.util.List;

import peripherals.Peripheral;

/**
 * @author Paul
 *
 */
public class NetworkAdapter implements NetworkListener 
{
	public static final boolean DEBUG = true;
	ValerieNetwork mNetwork;
	int[] deviceData;
	List<Peripheral> peripheralList; //TODO: This should maybe be a hashset to improve access times in didReceivePacket()
	Object semaphoreObject = null;
	
	/**
	 * Default constructor
	 * @throws Exception if used
	 * @deprecated use {@link #NetworkAdapter(List)} instead.
	 */
	@Deprecated
	public NetworkAdapter() throws Exception
	{
		this(null);
		throw new Exception("don't use this constructor!!!");
	}
	
	/**
	 * Initializes a <code>NetworkAdapter</code> to let the Executive software issue commands
	 * to the network.
	 * @param peripherals a <code>List</code> of registered <code>Peripherals</code> that can
	 * receive calls from remote peripherals. {@link #didReceivePacket(byte, byte[])} will pass
	 * packets on to peripherals from this list.
	 */
	public NetworkAdapter(List<Peripheral> peripherals)
	{
		mNetwork = new ValerieNetwork();
		mNetwork.addListener(this);
		peripheralList = peripherals;
		
		deviceData = null;
	}
	
	/**
	 * Initializes a <code>NetworkAdapter</code> to let the Executive software issue commands
	 * to the network.
	 * @param peripherals a <code>List</code> of registered <code>Peripherals</code> that can
	 * receive calls from remote peripherals. {@link #didReceivePacket(byte, byte[])} will pass
	 * packets on to peripherals from this list.
	 * @param semaphore use this object to synchronize operations on <code>peripherals</code>
	 */
	public NetworkAdapter(List<Peripheral> peripherals, Object semaphoreObject)
	{
		this("COM7", 115200, peripherals, semaphoreObject);
	}
	
	/**
	 * Initializes a <code>NetworkAdapter</code> to let the Executive software issue commands
	 * to the network.
	 * @param comPort the serial com port to use to communicate with the transceiver. this will vary if you are using *nix or windows.
	 * @param bitrate the bitrate to use to communicate with the transceiver.
	 * @param peripherals a <code>List</code> of registered <code>Peripherals</code> that can
	 * receive calls from remote peripherals. {@link #didReceivePacket(byte, byte[])} will pass
	 * packets on to peripherals from this list.
	 * @param semaphoreObject use this object to synchronize operations on <code>peripherals</code>
	 */
	public NetworkAdapter(String comPort, int bitrate, List<Peripheral> peripherals, Object semaphoreObject)
	{
		mNetwork = new ValerieNetwork(comPort, bitrate);
		mNetwork.addListener(this);
		peripheralList = peripherals;
		this.semaphoreObject = semaphoreObject;
		deviceData = null;
	}
	
	public Network getNetwork()
	{
		return mNetwork;
	}
	
	/**
	 * Tells the network to start listening for new device and return the first new device
	 * to respond. Once this device has responded, tell the network to stop listening. If 
	 * the timeout expires, the network will automatically stop listening and return 
	 * <code>null</code>.
	 * @param timeout the number of seconds to wait before abandoning hope of establishing a connection. This 
	 * method will block so be careful not to call this on your UI thread!!!
	 * @return a 2-element array of the form [ <code>New Device ID</code>, <code>New Device Type</code> ]. Return
	 * null if no device connected and the timeout occurred.
	 * @throws InterruptedException lol Shawn I can't do everything for you n00b
	 */
	public synchronized int[] addNewDevice(int timeout) throws InterruptedException
	{
		deviceData = null;		
		mNetwork.startListening();
		this.wait(timeout * 1000); // wait for up to timeout seconds
		mNetwork.stopListening();
		return deviceData;
	}
	
	/**
	 * Tells the network to remove a device. Successive calls to that peripheral will
	 * result in 'Device Not Responding' messages.
	 * @param id the ID of the peripheral to remove.
	 * @return <code>true</code> if the peripheral was successfully removed from the network.
	 */
	public synchronized boolean removeDevice(int id)
	{
		return mNetwork.removePeripheral(id);
	}

	@Override
	public synchronized void didAddDevice(int id, int typeId) 
	{
		deviceData = new int[] { id, typeId }; // TODO: this screams danger but maybe I'm just paranoid about threads
		this.notify(); // tell the waiting thread to go ahead and return the new device data
	}

	@Override
	public void deviceNotResponding(int id) 
	{
		// TODO: Pass to Executive software/user interface somehow.
	}

	@Override
	public void didReceivePacket(byte deviceID, byte[] data) 
	{
		if (null != peripheralList) 
		{
			if (null != semaphoreObject)
			{
				synchronized (semaphoreObject) 
				{
					for (Peripheral mPeripheral : peripheralList) //TODO: Blech linear search is greasy
					{
						if (deviceID == mPeripheral.getPeripheralID()) 
						{
							mPeripheral.receivePacket(data);
							break;
						}
					}
				} 
			}else synchronized (peripheralList)
			{
				if (DEBUG)
				{
					System.out.println("synchronizing on peripheral list!");
				}
				for (Peripheral mPeripheral : peripheralList) //TODO: Blech linear search is greasy
				{
					if (deviceID == mPeripheral.getPeripheralID()) 
					{
						mPeripheral.receivePacket(data);
						break;
					}
				}
			}

		}
		else if (DEBUG)
		{
			System.out.println("No peripheral list has been set!");
		}
		
	}

}
