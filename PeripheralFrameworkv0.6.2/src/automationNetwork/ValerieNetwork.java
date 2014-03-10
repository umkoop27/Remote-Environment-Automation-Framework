/**
 * 
 */
package automationNetwork;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This is the default implementation of the {@link automationNetwork.Network} interface.
 * It supports commuincation with a network of ArduIMUs running Valerie's custom network
 * layer by communicating with the root node over a serial port.
 * @author Paul
 *
 */
public class ValerieNetwork implements Network 
{
	private static final boolean DEBUG = true;
	
	///////////////
	// EVENT TYPES
	///////////////
	private static final byte EVENT_ADD_DEVICE = 'P';
	private static final byte EVENT_PERIPHERAL_PACKET = 'E';
	private static final byte EVENT_DEVICE_NOT_RESPONDING = 'X';
	
	private static final byte COMMAND_SEARCH_FOR_DEVICES = 'N';
	private static final byte COMMAND_STOP_LISTENING = 'S';
	private static final byte COMMAND_SEND_PACKET = 'T';
	private static final byte COMMAND_REMOVE_DEVICE = 'R';	
	
	boolean initialized = false;
	ArrayList<NetworkListener> mListeners;
	SerialConnection serialConnection;
		
	public ValerieNetwork() 
	{
		this("COM8", 9600);
	}

	public ValerieNetwork(String comPort, int bitRate)
	{
		if (!initialized)
		{
			mListeners = new ArrayList<NetworkListener>();
			serialConnection = new SerialConnection(comPort, bitRate, this); // watch for bugs here
			initialized = true;
		} else {
			if (DEBUG) 
			{
				System.out.println("ValerieNetwork already initialized!");
			}
			
		}
	}
	
	public void close()
	{
		serialConnection.close();
	}
	
	/**
	 * deal with a packet from the attached transceiver
	 * @param data
	 */
	public void handlePacket(byte[] data)
	{
		if (data.length >= 3) {
			byte eventType = data[0];
			byte deviceID = data[1];
			byte len = data[2];	
			int start = data.length - len;
			if (start < 0)
			{
				if (DEBUG) {
					System.out.println("start = " + data.length + " - " + len + " = " + start);
				}
				start = 0;
			}
			byte[] eventData = Arrays.copyOfRange(data, start, data.length);
			
			switch (eventType) 
			{
			case EVENT_ADD_DEVICE:
				if (4 == eventData.length) 
				{
					int newTypeID = ByteArrayHelpers.fromByteArray(eventData);
					
					for (NetworkListener listener : mListeners)
					{
						listener.didAddDevice(deviceID, newTypeID);
					}
				}
				else if (DEBUG) 
				{
					System.out.println("Received invalid 'Add Device' packet: " + ByteArrayHelpers.getByteArrayString(data));
				}
				break;
				
			case EVENT_DEVICE_NOT_RESPONDING:
				if (0 == eventData.length)
				{
					int notRespondingDevice = data[1];
					for (NetworkListener listener : mListeners)
					{
						listener.deviceNotResponding(notRespondingDevice);
					}
				}
				else if (DEBUG) 
				{
					System.out.println("Received invalid 'Not Responding' packet: " + ByteArrayHelpers.getByteArrayString(data));
				}
				break;
				
			case EVENT_PERIPHERAL_PACKET:
				if (len == eventData.length)
				{
					for (NetworkListener listener : mListeners)
					{
						listener.didReceivePacket(deviceID, eventData);
					}
				}
				else if (DEBUG) 
				{
					System.out.println("Received invalid 'peripheral data' packet: " + ByteArrayHelpers.getByteArrayString(data));
				}
				break;

			default:
				if (DEBUG) 
				{
					System.out.println("Received invalid 'Unknown' packet: " + ByteArrayHelpers.getByteArrayString(data));
				}
				break;
			}
			
		}
		
	}

	@Override
	/**
	 * Send a message to the attached transciever and instruct it to listen for new devices
	 */
	public boolean startListening() 
	{
		final byte[] dataPacket = new byte[]
				{
					COMMAND_SEARCH_FOR_DEVICES, (byte) 0xff, 0
				};
		
		// write out to serial port
		return serialConnection.writeBytes(dataPacket);
		// do it on another thread
//		Thread testThread = new Thread(new Runnable() {
//			
//			final byte[] dataPacket = new byte[]
//					{
//						COMMAND_SEARCH_FOR_DEVICES, 0x00, 0
//					};
//			
//			@Override
//			public void run() 
//			{
//				// write out to serial port
//				serialConnection.writeBytes(dataPacket);
//			}
//		} );		
//		testThread.start();
//		return true;
	}

	@Override
	public boolean stopListening() 
	{
		final byte[] dataPacket = new byte[]
				{
					COMMAND_STOP_LISTENING, 0x00, 0
				};
		
		// write out to serial port
		return serialConnection.writeBytes(dataPacket);
	}

	@Override
	public boolean removePeripheral(int peripheralID) 
	{
		if (peripheralID < 0xFF) 
		{
			final byte[] dataPacket = new byte[]
				{
					COMMAND_REMOVE_DEVICE, (byte)peripheralID, 0
				};
		
			// write out to serial port
			return serialConnection.writeBytes(dataPacket);
		}
		else if (DEBUG) 
		{
			System.out.println("Peripheral ID " + peripheralID + " exceeds 0xFF");
		}
		return false;		
	}

	@Override
	public void addListener(NetworkListener newListener) 
	{
		mListeners.add(newListener);
	}

	@Override
	public boolean sendPacket(int peripheralID, Object data) 
	{
		if (peripheralID < 0xff) 
		{
			if (data instanceof byte[])
			{
				byte[] dataBytes = (byte[]) data;
				if (0xFF > dataBytes.length) 
				{
					final byte[] dataHeader = new byte[]
							{
								COMMAND_SEND_PACKET, (byte)peripheralID, (byte)dataBytes.length
							};
					final byte[] dataPacket = ByteArrayHelpers.concat(dataHeader, dataBytes);
					
					if (DEBUG) {
						System.out.println("Sending: " + ByteArrayHelpers.getByteArrayString(dataPacket));
					}
					
					// write out to serial port
					return serialConnection.writeBytes(dataPacket);
				}
				else if(DEBUG) 
				{
					System.out.println("data has too many bytes: " + dataBytes.length);
				}	
			}
			else if(DEBUG) 
			{
				System.out.println("data is not a byte array: " + data);
			}		
		}
		else if (DEBUG) 
		{
			System.out.println("Peripheral ID " + peripheralID + " exceeds 0xFF");
		}
		return false;	
	}

}
