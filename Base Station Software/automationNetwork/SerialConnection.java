package automationNetwork;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

public class SerialConnection
{
	public static final boolean DEBUG_SERIAL = true;
	public static final boolean DEBUG_MSGS = true ;
	
	
	private SerialPort serialPort;
	
	public SerialConnection(String comPort, int bitRate, ValerieNetwork network)
	{
		serialPort = new SerialPort(comPort);
		
		try {
            serialPort.openPort();//Open port
            if (serialPort.setParams(bitRate, 8, 1, 0))//Set params
            {
	            int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;//Prepare mask
	            serialPort.setEventsMask(mask);//Set mask
	            serialPort.addEventListener(new SerialListener(serialPort, network));//Add SerialPortEventListener
            } else  if (DEBUG_MSGS) {
            	System.out.println("Invalid Serial Port parameters");
			}
        }
        catch (SerialPortException ex) {
        	if (DEBUG_MSGS) {
				System.err.println("Error opening serial port.");
				ex.printStackTrace();
			}
            silentClose();
            System.exit(1);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                silentClose();
            }
        });
	}
	
	private void silentClose() 
	{
        try 
        {
            serialPort.closePort();
        } catch (SerialPortException ex) 
        {
        	if (DEBUG_MSGS) {
				ex.printStackTrace();
			}
        }
    }
	
	public void close()
	{
		silentClose();
		if (DEBUG_MSGS) 
		{
			System.out.println("Closing port.");
		}
	}
	
	public boolean writeBytes(byte[] bytes)
	{
		try
		{
			if (DEBUG_SERIAL) {
				System.out.println("Network Sending: " + ByteArrayHelpers.getByteArrayString(bytes));
			}
			
			return serialPort.writeBytes(bytes);
		} catch (SerialPortException e) 
		{	
			if (DEBUG_MSGS)
			{
				e.printStackTrace();
			}			
			return false;
		}
	}
}

enum ListenerState {
	DATA, PREAMBLE
}

class SerialListener implements SerialPortEventListener
{
	SerialPort mPort;
	ListenerState state;
	int dataSize;
	public static final int PREAMBLE_SIZE = 3;
	ValerieNetwork networkDelegate;
	
	public SerialListener(SerialPort port, ValerieNetwork delegate)
	{
		mPort = port;
		state = ListenerState.PREAMBLE;
		networkDelegate = delegate;
	}

	@Override
	public void serialEvent(SerialPortEvent serialPortEvent) 
	{
		int val = serialPortEvent.getEventValue();
		if (serialPortEvent.isRXCHAR() && val > 0) 
		{
			if (SerialConnection.DEBUG_SERIAL)
			{
				System.out.println("Received " + val + " bytes");
			}
			try {
				
				byte[] preambleData = new byte[0];
				byte[] payloadData = new byte[0];
				
				preambleData = mPort.readBytes(PREAMBLE_SIZE, 500);
				
				if (SerialConnection.DEBUG_SERIAL) {
					System.out.println("Preamble: " + ByteArrayHelpers.getByteArrayString(preambleData) + "(" + new String(preambleData) + ")");
				}
				
				if (preambleData.length == PREAMBLE_SIZE)
				{
					dataSize = preambleData[2];
					if (dataSize > 0) 
					{
						payloadData = mPort.readBytes(dataSize, 500);
						
						if (SerialConnection.DEBUG_SERIAL) {
							System.out.println("Data: " + ByteArrayHelpers.getByteArrayString(payloadData) + "(" + new String(payloadData) + ")");
						}
						
						networkDelegate.handlePacket(ByteArrayHelpers.concat(preambleData, payloadData));
						
					} else if (0 == dataSize)
					{
						networkDelegate.handlePacket(preambleData);
					}
				} else
				{
					if (SerialConnection.DEBUG_MSGS) 
					{
						System.out.println("preamble data length: " + preambleData.length + " does not match correct size: " + PREAMBLE_SIZE);
					}
				}
				
				
				
//				switch (state) {
//				case PREAMBLE:
//					preambleData = mPort.readBytes(PREAMBLE_SIZE);
//					
//					if (SerialConnection.DEBUG_SERIAL) {
//						System.out.println("Preamble: " + ByteArrayHelpers.getByteArrayString(preambleData) + "(" + new String(payloadData) + ")");
//					}
//					
//					if (preambleData.length == PREAMBLE_SIZE)
//					{
//						dataSize = preambleData[PREAMBLE_SIZE - 1];
//						if (dataSize > 0) 
//						{
//							state = ListenerState.DATA;
//							payloadData = mPort.readBytes(dataSize, 50);
//							
//							if (SerialConnection.DEBUG_SERIAL) {
//								System.out.println("Data: " + ByteArrayHelpers.getByteArrayString(payloadData) + "(" + new String(payloadData) + ")");
//							}
//							state = ListenerState.PREAMBLE;
//							
//						} else if (0 == dataSize)
//						{
//							networkDelegate.handlePacket(preambleData);
//						}
//					} else
//					{
//						if (SerialConnection.DEBUG_MSGS) 
//						{
//							System.out.println("preamble data length: " + preambleData.length + " does not match correct size: " + PREAMBLE_SIZE);
//						}
//					}
//					break;
//					
//				case DATA:
//					payloadData = mPort.readBytes(dataSize);
//					
//					if (SerialConnection.DEBUG_SERIAL) {
//						System.out.println("Data: " + ByteArrayHelpers.getByteArrayString(payloadData));
//					}
//					
//					if (dataSize == payloadData.length)
//					{
//						byte[] concatData = ByteArrayHelpers.concat(preambleData, payloadData);
//						networkDelegate.handlePacket(concatData);
//					}
//					state = ListenerState.PREAMBLE;
//					break;
//
//				default:
//					break;
//				}
				
			} catch (SerialPortException e) 
			{
				if (SerialConnection.DEBUG_MSGS) 
				{
					e.printStackTrace();
				}
				
			} catch (SerialPortTimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}	
}