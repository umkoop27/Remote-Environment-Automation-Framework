import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;

import automationNetwork.Network;
import automationNetwork.NetworkAdapter;
import automationNetwork.ValerieNetwork;
import peripherals.DescriptorFactory;
import peripherals.Peripheral;
import peripherals.TriggerHandler;

/**
 * Test out plugins, this is to demonstrate how to use Peripherals and handle 
 * their behaviour
 * @author Paul
 *
 */
public class TestPeripherals
{
	
	/**
	 * Main method that tests plugins
	 * @param args
	 * @throws MalformedURLException
	 */
	public static void main(String[] args) throws MalformedURLException
	{	
		ArrayList<Peripheral> peripherals = new ArrayList<Peripheral>();
		
		String separationString = ";";
		
		// this should be implemented in Jarvis.java
		TriggerHandler triggerHandler = new TriggerHandler() {

			@Override
			public void triggerOccurred(boolean value, Peripheral p,
					String trigger, String... arguments) 
			{
				System.out.println("Peripheral <" + p.getName() + "> set trigger <" + trigger + "> to " + value + ". args: ");
				for (String string : arguments) {
					System.out.println(string);
				}
			}
		};
		System.out.println("Main Network Adapter");
		
		NetworkAdapter adapter = new NetworkAdapter("COM7", 115200, peripherals, new Object());
		Network mNetwork;
		
		// handle user interface (copied from Shawn)
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		
		String actionString;
		int peripheralId;
		boolean keepGoing = true;
		while (keepGoing)
		{
			String line;
			try {
				line = input.readLine();			
				String[] splitLine = line.split(separationString);
				
				int peripheralIndex = 0;
				int peripheralType = 0;
				
				Peripheral selectedPeripheral;
				
				switch (splitLine[0]) {
				case "listen":
					int timeout = 15;
					if (splitLine.length > 1) 
					{
						timeout = Integer.parseInt(splitLine[1]);
					}
					
					int[] newData;
					try {
						newData = adapter.addNewDevice(timeout);
						if (null != newData)
						{
							Peripheral peripheral = new Peripheral(newData[1], newData[0], "p", triggerHandler, adapter.getNetwork());
							peripheral.setName(peripheral.getTypeString() + peripherals.size());
							
							peripherals.add(peripheral);
						} else {
							System.out.println("No new devices were added.");
						}
						
					} catch (InterruptedException e) {
						System.out.println("listening interrupted. no peripheral added");
					}
					break;

				case "remove":
					peripheralId = 0;
					if (splitLine.length > 1) 
					{
						peripheralId = Integer.parseInt(splitLine[1]);
					}

					adapter.removeDevice(peripheralId);
					break;
					
				case "close":
					mNetwork = adapter.getNetwork();
					if (mNetwork instanceof ValerieNetwork) {
						((ValerieNetwork)mNetwork).close();
						keepGoing = false;
					} else {
						System.out.println("Could not close");
					}
					
					break;
					
				case "action":
					if (splitLine.length > 1) 
					{
						peripheralIndex = Integer.parseInt(splitLine[1]);
					}
					if (splitLine.length > 2)
					{
						actionString = splitLine[2];
						
						selectedPeripheral = peripherals.get(peripheralIndex);
						
						if (splitLine.length > 3)
						{
							String[] actionArguments = new String[splitLine.length - 3]; // ignore action, index and action ID
							System.arraycopy(splitLine, 3, actionArguments, 0, splitLine.length-3);
							selectedPeripheral.doAction(actionString, actionArguments);
						} else {
							selectedPeripheral.doAction(actionString);
						}
					} else
					{
						System.out.println("Usage: <action>" + separationString + "<peripheral index>" + separationString + "<action string>" + separationString + "<'" + separationString + "' separated arguments>");
					}				
					
					break;
				
				case "add":
					if (splitLine.length > 1) {
						peripheralType = Integer.parseInt(splitLine[1]);
					}
					
					Peripheral peripheral = new Peripheral(peripheralType, peripherals.size(), "Timer1", triggerHandler);
					peripheral.setName(peripheral.getTypeString() + peripherals.size());
					
					peripherals.add(peripheral);
					break;
					
				case "print":
					System.out.println("Peripherals:");
					String peripheralsDataString = "";
					for (int i = 0; i < peripherals.size(); i++)
					{
						peripheralsDataString += peripherals.get(i) + "\n";
					}
					System.out.println(peripheralsDataString);
					break;
					
				case "refresh":
					System.out.println("Refreshing plugins. some warnings from net.xeoh.plugins.base.impl.classpath.loader.AbstractLoader about known plugins are to be expected.");
					DescriptorFactory.refreshPlugins();
					break;
					
				case "status":
					if (splitLine.length > 1) {
						peripheralIndex = Integer.parseInt(splitLine[1]);
					}
					
					selectedPeripheral = peripherals.get(peripheralIndex);
					
					System.out.println(selectedPeripheral.getStatus("Paul is cool"));
					break;
					
				case "displayStatus":
					peripheralIndex = 0;
					if (splitLine.length > 1) {
						peripheralIndex = Integer.parseInt(splitLine[1]);
					}
					
					selectedPeripheral = peripherals.get(peripheralIndex);
					
					System.out.println(selectedPeripheral.getStatus("Display Text"));
					break;
					
				case "xml":
					peripheralIndex = 0;
					if (splitLine.length > 1) {
						peripheralIndex = Integer.parseInt(splitLine[1]);
					}
					
					selectedPeripheral = peripherals.get(peripheralIndex);
					
					System.out.println("XML:\n" + selectedPeripheral.getXML());
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
	
}


