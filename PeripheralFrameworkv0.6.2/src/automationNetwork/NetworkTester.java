package automationNetwork;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class NetworkTester 
{
	public static void main(String[] args) 
	{
		String port = "COM7";
		int baudRate = 115200;
		if (args.length > 0) 
		{
			port = args[0];
			
			if (args.length > 1) 
			{
				baudRate = Integer.parseInt(args[1]);
			}
		}
		
		testValerieNetwork(port, baudRate);;		
	}
	
	private static void testValerieNetwork(String port, int baudRate)
	{
		NetworkListener listener = new NetworkListener() {

			@Override
			public void didReceivePacket(byte deviceID, byte[] data)
			{
				System.out.println("Device " + deviceID + " recieved: " + ByteArrayHelpers.getByteArrayString(data));
			}

			@Override
			public void didAddDevice(int id, int typeId)
			{
				System.out.println("Device " + id + " of type " + typeId + " was added to the network.");
			}

			@Override
			public void deviceNotResponding(int id) 
			{
				System.out.println("Device " + id + " is not responding.");
			}
		};
		ValerieNetwork network = new ValerieNetwork(port, baudRate);	
		network.addListener(listener);

		// interface with the user:
		// http://imgur.com/UVMV7ZB
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

		int id;
		byte[] data;
		
		boolean keepGoing = true;

		while (keepGoing)
		{
			String line;
			try {
				line = input.readLine();			
				String[] splitLine = line.split(" ");

				switch (splitLine[0]) {
				case "listen":
					network.startListening();
					break;

				case "stop":
					network.stopListening();
					break;

				case "packet":
					id = 0;
					data = new byte[]  // send a data packet with command code 1 and some random data
							{
								(byte)0x01, 
								(byte) 0xFF
							};

					if (splitLine.length > 1) 
					{
						id = Integer.parseInt(splitLine[1]);

						if (splitLine.length > 2) 
						{
							data = splitLine[2].getBytes();
						}
					}

					network.sendPacket(id, data);
					break;

				case "remove":
					id = 0;
					if (splitLine.length > 1) 
					{
						id = Integer.parseInt(splitLine[1]);
					}

					network.removePeripheral(id);
					break;
					
				case "close":
					if (network instanceof ValerieNetwork) {
						network.close();
						keepGoing = false;
					} else {
						System.out.println("Could not close");
					}
					
					break;

				default:
					System.out.println("unrecognized Command");
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private static void testNetworkAdapter()
	{
		NetworkAdapter adapter = new NetworkAdapter(null);
		try {
			int[] data = adapter.addNewDevice(1);
			if (data != null) {
				System.out.println("ID " + data[0] + "\nType: " + data[1]);
			} else {
				System.out.println("data is null!");
			}
			
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
