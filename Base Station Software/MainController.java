import java.io.FileInputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import peripherals.Peripheral;
import peripherals.TriggerHandler;
import peripherals.DescriptorFactory;
import automationNetwork.NetworkAdapter;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;


/**
 * The <code>MainController</code> is responsible for connecting all components of the automation framework.
 * The main controller contains a list of all of the <code>Service</code> objects in the system, a list of all of the 
 * <code>Peripheral</code> objects in the system, a <code>NetworkAdapter</code> that handles communication to peripherals, and a list 
 * of all of the <code>TriggerLogicBlock</code> objects in the system, which gives access to every logic block in the system.
 * The main controller also starts the HTTPS server that handles user information requests and handles all database communication for the system.
 * 
 * @author Shawn Koop
 */
public class MainController implements TriggerHandler
{
	/**
	 * An <code>ArrayList</code> containing all of the <code>Service</code> objects in the automation system.
	 */
	private ArrayList<Service> services;
	/**
	 * An <code>ArrayList</code> containing all of the <code>Peripheral</code> objects in the automation system.
	 */
	private ArrayList<Peripheral> peripherals;
	/**
	 * An <code>ArrayList</code> containing all of the <code>TriggerLogicBlock</code> objects in the automation system.
	 * Because logic blocks are all attached, this list provides access to every <code>LogicBlock</code> object in the system.
	 */
	private ArrayList<TriggerLogicBlock> triggers;
	/**
	 * A <code>LinkedBlockingQueue</code> that holds {@link TriggerRequest} objects waiting to be acted upon.
	 */
	private LinkedBlockingQueue<TriggerRequest> triggerQueue;
	/**
	 * A <code>NetworkAdapter</code> that handles all communication with physical peripherals.
	 */
	private NetworkAdapter mAdapter;
	/**
	 * A <code>String</code> containing the URL of the database.
	 */
	private final String databaseURL = "jdbc:mysql://localhost:3306/jarvis";
	/**
	 * A <code>String</code> containing the database user name.
	 */
	private final String databaseUser = "Jarvis";
	/**
	 * A <code>String</code> containing the database password. A future version of this software should store the database user name and password in an encrypted file.
	 */
	private final String databasePassword = "Jarvis"; //in the future this should be changed so that the user sets the password somehow
	
	/**
	 * This method creates a <code>MainController</code> object, initializes its peripherals and services, makes it start managing triggers and sessions, and starts the HTTPS server listening on port 443.
	 * 
	 * @param args This parameter is never used.
	 */
	public static void main(String[] args)
	{
		try 
		{
			Class.forName("com.mysql.jdbc.Driver");
		} 
		catch (ClassNotFoundException e1) 
		{
			e1.printStackTrace();
		}
		
		MainController mainController = new MainController();
		
		mainController.initializePeripherals();
		mainController.initializeServices();
		
		mainController.manageTriggers();
		mainController.manageSessions();
		
		//start the web server
		WebRequestHandler webRequestHandler = new WebRequestHandler(mainController);
		InetSocketAddress addr = new InetSocketAddress(443);
		try
		{
			
			SSLContext sslContext = SSLContext.getInstance("TLSv1");
			
			HttpsServer server = HttpsServer.create(addr, 100);
			
			KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load(new FileInputStream(".keystore"), "Jarvis".toCharArray());
			
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
			keyManagerFactory.init(keyStore, "Jarvis".toCharArray());
			
			sslContext.init(keyManagerFactory.getKeyManagers(),null,null);
			
			server.createContext("/", webRequestHandler);
			HttpsConfigurator httpsConfigurator = new HttpsConfigurator(sslContext);
			server.setHttpsConfigurator(httpsConfigurator);
			server.setExecutor(Executors.newCachedThreadPool());
			server.start();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * This constructor creates a new <code>MainController</code> and initializes the lists and queue of the <code>MainController</code> class
	 * and initializes the <code>NetworkAdapter</code> to be located at "/dev/ttyUSB0" using a bit rate of 9600 bits/sec.
	 */
	public MainController()
	{
		services = new ArrayList<Service>();
		peripherals = new ArrayList<Peripheral>();
		triggers = new ArrayList<TriggerLogicBlock>();
		triggerQueue = new LinkedBlockingQueue<TriggerRequest>();
		mAdapter = new NetworkAdapter("/dev/ttyUSB0", 19200, peripherals, this);
	}
	
	/**
	 * This method adds a new <code>Peripheral</code> to the automation system. The <code>Peripheral</code> object is created, passing this
	 * main controller as the trigger handler and <code>mAdapter</code>'s network as the network. Then the new <code>Peripheral</code> is added to <code>peripherals</code>.
	 * 
	 * @param peripheralType	The int type ID of the new <code>Peripheral</code>.
	 * @param peripheralID 		The int peripheral ID of the new <code>Peripheral</code>.
	 * @param name				A <code>String</code> containing the name of the new peripheral.
	 * @return					A <code>String</code> containing "device_added" or "invalid_name" depending on the situation.
	 */
	public synchronized String addPeripheral(int peripheralType, int peripheralID, String name)
	{
		String response = "device_added";
		boolean invalidName = false;
		for(int peripheralIndex = 0; peripheralIndex < peripherals.size(); peripheralIndex ++)
		{
			if(peripherals.get(peripheralIndex).getName().equals(name))
				invalidName = true;
		}
		if(invalidName)
			response = "invalid_name";
		else
		{
			Peripheral peripheral = new Peripheral(peripheralType, peripheralID, name, this, mAdapter.getNetwork());
			peripherals.add(peripheral);
		}
		return response;
	}
	
	/**
	 * This method is used to initialize any known <code>Peripheral</code> objects at startup.
	 */
	public void initializePeripherals()
	{
		addPeripheral(55, 256, "Message Board");
	}
	
	/**
	 * This method is used to initialize any known <code>Service</code> objects at startup.
	 */
	public void initializeServices()
	{
		//uncomment the following block to enable loading services from the database upon startup
		/*try 
		{
			Connection connection = DriverManager.getConnection(databaseURL, databaseUser, databasePassword);
			String query = "SELECT * FROM services";
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			while(resultSet.next())
			{
				int serviceID = resultSet.getInt("id");
				String name = resultSet.getString("name");
				String representation = resultSet.getString("representation");
				//create the Service object
				Service service = new Service(name, serviceID, representation, triggers, peripherals);
				//add the Service object to the list of services
				services.add(service);
			}
		} 
		catch (SQLException e) 
		{
			//System.out.println("Unable to add new service to database.");
			//e.printStackTrace();
		}*/
	}
	
	/**
	 * This method creates a new <code>Service</code> object with the specified service name and representation and adds it to <code>services</code>.
	 * 
	 * @param name A <code>String</code> containing the name of the <code>Service</code> to be created.
	 * @param serviceRepresentation A<code>String</code> containing the service representation of the new <code>Service</code>.
	 * @return A <code>String</code> containing either "service_created", "invalid_name", or "creation_failed" depending on the situation.
	 * @see Service
	 */
	public synchronized String createNewService(String name, String serviceRepresentation)
	{
		String response = "service_created";
		//choose the service ID
		int serviceID = 0;
		boolean idChosen = false;
		while(idChosen == false)
		{
			int serviceIndex;
			for(serviceIndex = 0; serviceIndex < services.size(); serviceIndex++)
			{
				if(services.get(serviceIndex).getServiceID() == serviceID)
				{
					serviceID++;
					break;
				}
			}
			if(serviceIndex == services.size())
				idChosen = true;
		}
		//uncomment this section to store services in the database
		/*
		try 
		{
			Connection connection = DriverManager.getConnection(databaseURL, databaseUser, databasePassword);
			String query = "INSERT INTO services VALUES (" + serviceID + ",'" + name + "','" + serviceRepresentation + "')";
			Statement statement = connection.createStatement();
			statement.executeUpdate(query);
		} 
		catch (SQLException e) 
		{
			response = "creation_failed";
		}
		*/
		for(int serviceIndex = 0; serviceIndex < services.size(); serviceIndex++)
		{
			Service service = services.get(serviceIndex);
			if(service.getName().equals(name))
			{
				response = "invalid_name";
			}
		}
		
		if(response.equals("service_created"))//if no errors have occurred yet
		{
			//create the Service object
			try 
			{
				Service service = new Service(name, serviceID, serviceRepresentation, triggers, peripherals);
				services.add(service);
			} 
			catch (Exception e) 
			{
				response = "creation_failed";
			}
		}
		
		return response;
	}
	
	/**
	 * This method returns a <code>String</code> containing the <code>name</code> of every <code>Service</code> object in the system separated by a '|'.
	 * 
	 * @return A <code>String</code> containing the <code>name</code> of every <code>Service</code> object in the system separated by a '|'.
	 */
	public synchronized String getServiceList()
	{
		String result = "no_services";
		if(services.size() > 0)
		{
			result = services.get(0).getName();
			for(int serviceIndex = 1; serviceIndex < services.size(); serviceIndex++)
			{
				result = result + "|" + services.get(serviceIndex).getName();
			}
		}
		return result;
	}
	
	/**
	 * This method returns a <code>String</code> containing the <code>name</code> of every <code>Peripheral</code> object in the system separated by a '|'.
	 * 
	 * @return A <code>String</code> containing the <code>name</code> of every <code>Peripheral</code> object in the system separated by a '|'.
	 */
	public synchronized String getDeviceList()
	{
		String result = "";
		if(peripherals.size() > 0)
		{
			result = peripherals.get(0).getName();
			for(int peripheralIndex = 1; peripheralIndex < peripherals.size(); peripheralIndex++)
			{
				result = result + "|" + peripherals.get(peripheralIndex).getName();
			}
		}
		return result;
	}
	
	/**
	 * This method returns the service representation of the specified service.
	 * 
	 * @param serviceName A <code>String</code> containing the <code>name</code> of the <code>Service</code> whose representation is desired.
	 * @return A <code>String</code> containing the service representation of the specified service.
	 * @see Service
	 */
	public synchronized String getServiceRepresentation(String serviceName)
	{
		String representation = "";
		for(int serviceIndex = 0; serviceIndex < services.size(); serviceIndex++)
		{
			Service service = services.get(serviceIndex);
			if(service.getName().equals(serviceName))
			{
				representation = service.getServiceRepresentation();
				break;
			}
		}
		return representation;
	}
	

	/**
	 * This method removes a <code>Peripheral</code> from the automation system.
	 * 
	 * @param peripheralName A <code>String</code> containing the name of the <code>Peripheral</code> to remove.
	 * @return A <code>String</code> containing either "device_removed" or "invalid_name" depending on the situation.
	 */
	public synchronized String removePeripheral(String peripheralName)
	{
		String response = "device_removed";
		
		Peripheral peripheral = null;
		for(int peripheralIndex = 0; peripheralIndex < peripherals.size(); peripheralIndex++)
		{
			Peripheral tempPeripheral = peripherals.get(peripheralIndex);
			if(tempPeripheral.getName().equals(peripheralName))
			{
				peripheral = tempPeripheral;
				break;
			}
		}
		if(peripheral != null)
		{
			if(mAdapter.removeDevice(peripheral.getPeripheralID()))
				peripherals.remove(peripheral);
			else
				response = "removal_failed";
		}
		else 
		{
			response = "invalid_name";
		}
		
		return response;
	}
	
	/**
	 * This method removes a <code>Service</code> from the automation system.
	 * 
	 * @param serviceName A <code>String</code> containing the name of the <code>Service</code> to be removed.
	 * @return A <code>String</code> containing either "service_removed", "removal_failed", or "invalid_name" depending on the situation.
	 * @see Service
	 */
	public synchronized String removeService(String serviceName)
	{
		String response = "service_removed";
		Service service = null;
		for(int serviceIndex = 0; serviceIndex < services.size(); serviceIndex++)
		{
			if(services.get(serviceIndex).getName().equals(serviceName))
			{
				service = services.get(serviceIndex);
				break;
			}
		}
		if(service != null)
		{
			//uncomment this section when services are stored in the database
			/*
			try 
			{
				Connection c = DriverManager.getConnection(databaseURL, databaseUser, databasePassword);
				String query = "DELETE FROM services WHERE id=" + s.getServiceID();
				Statement st = c.createStatement();
				st.executeUpdate(query);
			} 
			catch (SQLException e) 
			{
				response = "removal_failed";
			}*/
			if(!response.equals("removal_failed"))
			{
				service.deleteFromSystem(triggers);
				services.remove(service);
			}
		}
		else //service doesn't exist
		{
			response = "invalid_name";
		}
		
		return response;
	}
	
	/**
	 * This method edits a <code>Service</code> so that if this method returns "service_edited", the <code>Service</code> of interest will be representable by the provided service representation.
	 * 
	 * @param serviceName A <code>String</code> containing the name of the <code>Service</code> to be edited.
	 * @param editedServiceString A <code>String</code> representation of what the <code>Service</code> should be changed to.
	 * @return A <code>String</code> containing either "service_edited", "edit_failed", or "invalid_name" depending on the situation.
	 * @see Service
	 */
	public synchronized String editService(String serviceName, String editedServiceString)
	{
		String response = "service_edited";
		Service service = null;
		for(int serviceIndex = 0; serviceIndex < services.size(); serviceIndex++)
		{
			if(services.get(serviceIndex).getName().equals(serviceName))
			{
				service = services.get(serviceIndex);
				break;
			}
		}
		if(service != null)
		{
			boolean editSucceeded = service.editService(editedServiceString, triggers, peripherals);
			String oldServiceString = service.getServiceRepresentation();
			if(!editSucceeded)
				response = "edit_failed";
			else
			{
				//uncomment this section if services are stored in the database
				/*
				try 
				{
					Connection c = DriverManager.getConnection(databaseURL, databaseUser, databasePassword);
					String query = "UPDATE services SET representation='" + editedServiceString + "' WHERE id=" + s.getServiceID();
					Statement st = c.createStatement();
					st.executeUpdate(query);
				} 
				catch (SQLException e) 
				{
					response = "edit_failed";
					boolean fixed = false;
					while(!fixed)
					{
						fixed = s.editService(oldServiceString, triggers, peripherals);	//reverting to old version should work no problem because all of the peripherals were there
					}
				}*/
			}
		}
		else 
		{
			response = "invalid_name";
		}
		return response;
	}
	
	/**
	 * This method edits the name of a <code>Service</code>
	 * 
	 * @param oldServiceName A <code>String</code> containing the old <code>Service</code> name.
	 * @param newServiceName A <code>String</code> containing the new <code>Service</code> name.
	 * @return A <code>String</code> containing either "name_edited", "edit_failed", or "invalid_name" depending on the situation.
	 * @see Service
	 */
	public synchronized String editServiceName(String oldServiceName, String newServiceName)
	{
		String response = "name_edited";
		Service service = null;
		for(int serviceIndex = 0; serviceIndex < services.size(); serviceIndex++)
		{
			if(services.get(serviceIndex).getName().equals(oldServiceName))
			{
				service = services.get(serviceIndex);
				break;
			}
		}
		if(service != null)
		{
			service.setName(newServiceName);
			//uncomment this section if services are stored in the database
			/*
			try 
			{
				Connection c = DriverManager.getConnection(databaseURL, databaseUser, databasePassword);
				String query = "UPDATE services SET name='" + newServiceName + "' WHERE id=" + s.getServiceID();
				Statement st = c.createStatement();
				st.executeUpdate(query);
			} 
			catch (SQLException e) 
			{
				response = "edit_failed";
				s.setName(oldServiceName);
			}*/
		}
		else 
		{
			response = "invalid_name";
		}
		return response;
	}
	
	/**
	 * This method edits the name of a <code>Peripheral</code>
	 * 
	 * @param oldPeripheralName A <code>String</code> containing the old <code>Peripheral</code> name.
	 * @param newPeripheralName A <code>String</code> containing the new <code>Peripheral</code> name.
	 * @return A <code>String</code> containing either "name_edited" or "invalid_name" depending on the situation.
	 * @see Peripheral
	 */
	public synchronized String editPeripheralName(String oldPeripheralName, String newPeripheralName)
	{
		String response = "name_edited";
		Peripheral peripheral = null;
		for(int peripheralIndex = 0; peripheralIndex < peripherals.size(); peripheralIndex++)
		{
			if(peripherals.get(peripheralIndex).getName().equals(oldPeripheralName))
			{
				peripheral = peripherals.get(peripheralIndex);
				break;
			}
		}
		if(peripheral != null)
		{
			peripheral.setName(newPeripheralName);
		}
		else 
		{
			response = "invalid_name";
		}
		return response;
	}
	
	/**
	 * This method asks the <code>mAdapter</code> network to search for a new peripheral and returns the new peripheral information if one was found.
	 * 
	 * @param numSecondsToWait An <code>int</code> containing the number of seconds to wait for the peripheral to respond.
	 * @return A <code>String</code> containing either "no_signal_detected" or "signal_detected|[device type ID]|[device ID]" depending on the situation.
	 * @see NetworkAdapter
	 */
	public synchronized String searchForPeripheral(int numSecondsToWait)
	{
		String response = "no_signal_detected";
		
		try 
		{
			int[] deviceData = mAdapter.addNewDevice(numSecondsToWait);
			if(deviceData != null)
				response = "signal_detected|" + deviceData[1] + "|" + deviceData[0];
			else 
			{
				response = "no_signal_detected";
			}
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		
		return response;
	}
	
	/**
	 * This method returns the information of the triggers and actions in a <code>Service</code> in <code>String</code>
	 * format. The <code>String</code> begins with the name of the <code>Service</code> and then trigger and action information,
	 * where each term is separated by a "|". For a trigger, the format is "t`[device name]`[trigger name]`[arg1]`[arg2]`...".
	 * For a trigger, the format is "a`[device name]`[action name]`[arg1]`[arg2]`...".
	 * 
	 * @param serviceName A <code>String</code> containing the name of the <code>Service</code> whose information should be returned.
	 * @return A <code>String</code> containing the trigger and action information of the <code>Service</code> or "invalid_name", depending on the situation.
	 * @see Service
	 */
	public synchronized String getServiceInfo(String serviceName)
	{
		String response = serviceName;
		
		Service service = null;
		for(int serviceIndex = 0; serviceIndex < services.size(); serviceIndex++)
		{
			if(services.get(serviceIndex).getName().equals(serviceName))
			{
				service = services.get(serviceIndex);
				break;
			}
		}
		if(service!= null)
		{
			ArrayList<LogicBlock> blocks = service.getLogicBlocks();
			for(int blocksIndex = 0; blocksIndex < blocks.size(); blocksIndex++)
			{
				LogicBlock block = blocks.get(blocksIndex);
				if(block instanceof TriggerLogicBlock)
				{
					response = response + "|t`" + ((TriggerLogicBlock)block).getPeripheral().getName() + "`" + ((TriggerLogicBlock)block).getTriggerString();
					String[] arguments = ((TriggerLogicBlock)block).getArguments();
					for(int argumentIndex = 0; argumentIndex < arguments.length; argumentIndex++)
					{
						response = response + "`" + arguments[argumentIndex];
					}
				}
				else if(block instanceof ActionLogicBlock)
				{
					response = response + "|a`" + ((ActionLogicBlock)block).getPeripheral().getName() + "`" + ((ActionLogicBlock)block).getActionString();
					String[] arguments = ((ActionLogicBlock)block).getArguments();
					for(int argumentIndex = 0; argumentIndex < arguments.length; argumentIndex++)
					{
						response = response + "`" + arguments[argumentIndex];
					}
				}
			}
		}
		else 
		{
			response = "invalid_name";
		}
		
		return response;
	}
	
	/**
	 * This method returns the XML information of a <code>Peripheral</code>.
	 * 
	 * @param peripheralName A <code>String</code> containing the name of the <code>Peripheral</code> whose information is requested.
	 * @return A <code>String</code> containing either "invalid_name" or the XML information of the <code>Peripheral</code>.
	 * @see Peripheral
	 */
	public synchronized String getPeripheralInfo(String peripheralName)
	{
		String response = "invalid_name";
		
		Peripheral peripheral = null;
		for(int peripheralIndex = 0; peripheralIndex < peripherals.size(); peripheralIndex++)
		{
			if(peripherals.get(peripheralIndex).getName().equals(peripheralName))
			{
				peripheral = peripherals.get(peripheralIndex);
				break;
			}
		}
		if(peripheral != null)
		{
			response = peripheral.getXML();
			//remove unnecessary whitespace
			response = response.replace("\t", "");
			response = response.replace("\n", "");
		}
		return response;
	}
	
	/**
	 * This method returns the requested <code>status</code> <code>String</code> of the specified <code>Peripheral</code>.
	 * 
	 * @param peripheralName A <code>String</code> containing the name of the <code>Peripheral</code> whose <code>status</code> is being requested.
	 * @param statusName A <code>String</code> containing the name of the <code>status</code> being requested.
	 * @return A <code>String</code> containing the requested <code>status</code> or "invalid_name" or "invalid_status", depending on the situation.
	 * @see Peripheral
	 */
	public synchronized String getStatus(String peripheralName, String statusName)
	{
		String response = "invalid_status";
		
		Peripheral peripheral = null;
		for(int peripheralIndex = 0; peripheralIndex < peripherals.size(); peripheralIndex++)
		{
			Peripheral tempPeripheral = peripherals.get(peripheralIndex);
			if(tempPeripheral.getName().equals(peripheralName))
			{
				peripheral = tempPeripheral;
				break;
			}
		}
		if(peripheral != null)
		{
			response = peripheral.getStatus(statusName);
		}
		else 
		{
			response = "invalid_name";
		}
		return response;
	}
	
	/**
	 * This method calls the <code>doAction</code> method on the specified <code>Peripheral</code> to perform the desired action.
	 * 
	 * @param peripheralName A <code>String</code> containing the name of the <code>Peripheral</code> that should perform the action.
	 * @param actionName A <code>String</code> containing the name of the action to be performed.
	 * @param arguments An array of <code>String</code> objects containing the arguments for the action to be performed.
	 * @return A <code>String</code> containing "action_complete" or "invalid_action" or "invalid_peripheral_name" depending on the situation.
	 * @see Peripheral
	 */
	public synchronized String doAction(String peripheralName, String actionName, String... arguments)
	{
		String response = "action_complete";
		
		Peripheral peripheral = null;
		for(int peripheralIndex = 0; peripheralIndex < peripherals.size(); peripheralIndex++)
		{
			Peripheral tempPeripheral = peripherals.get(peripheralIndex);
			if(tempPeripheral.getName().equals(peripheralName))
			{
				peripheral = tempPeripheral;
				break;
			}
		}
		if(peripheral != null)
		{
			if(peripheral.validateAction(actionName, arguments))
			{
				peripheral.doAction(actionName, arguments);
			}
			else 
			{
				response = "invalid_action";
			}
		}
		else 
		{
			response = "invalid_peripheral_name";
		}
		
		return response;
	}
	
	/**
	 * This method authenticates a user by checking the user name and password in the MySQL database and
	 * returns a new session ID if the combination is valid or null if it is not.
	 * 
	 * @param userName A <code>String</code> containing the user name to be authenticated.
	 * @param password A <code>String</code> containing the password to be authenticated.
	 * @return A <code>String</code> containing a new session ID if the combination is valid or null if it is not.
	 */
	public synchronized String authenticateUser(String userName, String password)
	{
		String sessionID = null;
		try 
		{
			MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
			byte[] passwordBytes = password.getBytes();
			byte[] hashedPasswordBytes = mDigest.digest(passwordBytes);
			String hashedPassword = toHex(hashedPasswordBytes);
			
			Connection connection = DriverManager.getConnection(databaseURL, databaseUser, databasePassword);
			String query = "SELECT password FROM users WHERE userName='" + userName + "'";
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			while(resultSet.next())
			{
				String userPassword = resultSet.getString("password");
				if(userPassword.equals(hashedPassword))
				{
					sessionID = UUID.randomUUID().toString();
					Date currentDate = new Date();
					long time = currentDate.getTime() + 3600000;	//set expiry for one hour from now
					Timestamp timestamp = new Timestamp(time);
					
					Connection connection2 = DriverManager.getConnection(databaseURL, databaseUser, databasePassword);
					String query2 = "INSERT INTO sessions VALUES ('" + sessionID + "','" + timestamp.toString() + "')";
					Statement statement2 = connection2.createStatement();
					statement2.executeUpdate(query2);
					break;
				}
			}
		} 
		catch (Exception e) 
		{
			sessionID = null;
			e.printStackTrace();
		}
		return sessionID;
	}
	
	/**
	 * This method edits the user name of a user by changing the user name in the MySQL database.
	 * 
	 * @param oldName A <code>String</code> containing the old user name.
	 * @param newName A <code>String</code> containing the new user name.
	 * @return A <code>String</code> containing either "name_edited" or "edit_failed" depending on the situation.
	 */
	public synchronized String editUserName(String oldName, String newName)
	{
		String response = "name_edited";
		try 
		{	
			Connection c = DriverManager.getConnection(databaseURL, databaseUser, databasePassword);
			String query = "UPDATE users SET userName='" + newName + "' WHERE userName='" + oldName + "'";
			Statement s = c.createStatement();
			s.executeUpdate(query);
		} 
		catch (Exception e) 
		{
			response = "edit_failed";
		}
		return response;
	}
	
	/**
	 * This method edits the password of a user by changing the password in the MySQL database.
	 * 
	 * @param oldPassword A <code>String</code> containing the old password.
	 * @param newPassword A <code>String</code> containing the new password.
	 * @return A <code>String</code> containing either "password_edited" or "edit_failed" depending on the situation.
	 */
	public synchronized String editPassword(String oldPassword, String newPassword)
	{
		String response = "password_edited";
		
		try 
		{
			MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
			byte[] oldPasswordBytes = oldPassword.getBytes();
			byte[] hashedOldPasswordBytes = mDigest.digest(oldPasswordBytes);
			String hashedOldPassword = toHex(hashedOldPasswordBytes);
			
			byte[] newPasswordBytes = newPassword.getBytes();
			byte[] hashedNewPasswordBytes = mDigest.digest(newPasswordBytes);
			String hashedNewPassword = toHex(hashedNewPasswordBytes);
			
			Connection c = DriverManager.getConnection(databaseURL, databaseUser, databasePassword);
			String query = "UPDATE users SET password='" + hashedNewPassword + "' WHERE password='" + hashedOldPassword + "'";
			Statement s = c.createStatement();
			s.executeUpdate(query);
		} 
		catch (Exception e) 
		{
			response = "edit_failed";
		}
		
		return response;
	}
	
	/**
	 * This method checks whether or not a user account exists in the system or not by checking the MySQL database.
	 * 
	 * @return A <code>boolean</code> indicating whether or not an account exists.
	 */
	public synchronized boolean getUserAccountExists()
	{
		boolean result = false;
		
		try 
		{
			Connection connection = DriverManager.getConnection(databaseURL, databaseUser, databasePassword);
			String query = "SELECT * FROM  users LIMIT 1";
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			if(resultSet.next())
				result = true;
		} 
		catch (Exception e) 
		{
			result = false;
		}	
		
		return result;
	}
	
	/**
	 * This method creates a user account and adds it to the MySQL database and automatically authenticates the new account.
	 * 
	 * @param userName A <code>String</code> containing the user name of the new account.
	 * @param password A <code>String</code> containing the password of the new account.
	 * @return A <code>String</code> containing a new session ID if the account was created or null if it is not.
	 */
	public synchronized String createUserAccount(String userName, String password)
	{
		String sid = null;
		
		try 
		{
			MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
			byte[] passwordBytes = password.getBytes();
			byte[] hashedPasswordBytes = mDigest.digest(passwordBytes);
			String hashedPassword = toHex(hashedPasswordBytes);
			
			if(!getUserAccountExists())
			{
				Connection connection = DriverManager.getConnection(databaseURL, databaseUser, databasePassword);
				String query = "INSERT INTO users VALUES ('" + userName + "','" + hashedPassword + "')";
				Statement statement = connection.createStatement();
				statement.executeUpdate(query);
				
				sid = authenticateUser(userName, password);
			}
		} 
		catch (Exception e) 
		{
		}
		
		return sid;
	}
	
	/**
	 * This method validates a session ID by checking the MySQL database and then updates the session ID expiry time. If the session ID is in the database but has expired, it is deleted.
	 * @param sid A <code>String</code> containing the session ID to be validated.
	 * @return A <code>boolean</code> indicating whether or not the session ID is valid.
	 */
	public synchronized boolean validateSID(String sid)
	{
		boolean validSID = false;
		
		Connection connection;
		try 
		{
			connection = DriverManager.getConnection(databaseURL, databaseUser, databasePassword);
			String query = "SELECT expiryTime FROM sessions WHERE sid='" + sid + "'";
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			
			while(resultSet.next())
			{
				Timestamp expiryTime = resultSet.getTimestamp("expiryTime");
				Date currentDate = new Date();
				Timestamp currentTime = new Timestamp(currentDate.getTime());
				if(currentTime.before(expiryTime))
				{
					Timestamp newExpiryTime = new Timestamp(currentDate.getTime() + 3600000);
					
					Connection connection2 = DriverManager.getConnection(databaseURL, databaseUser, databasePassword);
					String query2 = "UPDATE sessions SET expiryTime='" + newExpiryTime.toString() + "' WHERE sid='" + sid  + "'";
					Statement statement2 = connection2.createStatement();
					statement2.executeUpdate(query2);
					validSID = true;
					break;
				}
				else 
				{
					Connection connection3 = DriverManager.getConnection(databaseURL, databaseUser, databasePassword);
					String query3 = "DELETE FROM sessions WHERE sid='" + sid + "'";
					Statement statement3 = connection3.createStatement();
					statement3.executeUpdate(query3);
				}
			}
		} 
		catch (SQLException e) 
		{
			validSID = false;
			e.printStackTrace();
		}
		
		return validSID;
	}
	
	/**
	 * This method removes the specified session ID from the database.
	 * 
	 * @param sid A <code>String</code> containing the session ID to be removed.
	 */
	public synchronized void destroySID(String sid)
	{
		try 
		{
			Connection connection = DriverManager.getConnection(databaseURL, databaseUser, databasePassword);
			String query = "DELETE FROM sessions WHERE sid='" + sid + "'";
			Statement statement = connection.createStatement();
			statement.executeUpdate(query);
		} 
		catch (Exception e) 
		{
			
		}
	}
	
	/**
	 * This method refreshes the peripheral descriptor plugins.
	 * 
	 * @see DescriptorFactory
	 */
	public synchronized void newDescriptorAdded()
	{
		DescriptorFactory.refreshPlugins();
	}
	
	/**
	 * This method creates and starts a new <code>Thread</code> that takes trigger requests off of the <code>triggerQueue</code> and updates the appropriate trigger.
	 */
	private void manageTriggers()
	{
		new Thread(
				new Runnable()
				{
					public void run()
					{
						while(true)
						{
							try 
							{
								TriggerRequest triggerRequest = triggerQueue.take();
								updateTrigger(triggerRequest.value, triggerRequest.peripheral, triggerRequest.triggerName, triggerRequest.arguments);
							} 
							catch (InterruptedException e) 
							{
							}
						}
					}
				}
				).start();
	}
	
	/**
	 * This method is called to notify the main controller of a trigger occurrence. The trigger request is placed on the <code>triggerQueue</code> to be handled by the trigger manager thread.
	 *
	 * @param value A <code>boolean</code> containing the value of the trigger.
	 * @param peripheral The <code>Peripheral</code> on which the trigger occurred.
	 * @param triggerName A <code>String</code> containing the name of the trigger that occurred.
	 * @param arguments A <code>String</code> array containing the arguments of the trigger that occurred.
	 */
	@Override
	public void triggerOccurred(boolean value, Peripheral peripheral, String triggerName, String... arguments)
	{
		TriggerRequest triggerRequest = new TriggerRequest(value, peripheral, triggerName, arguments);
		try 
		{
			triggerQueue.put(triggerRequest);
		} 
		catch (InterruptedException e) 
		{
		}
	}
	
	/**
	 * This method updates the output value of a trigger, which is represented by a <code>TriggerLogicBlock</code>.
	 * 
	 * @param value 		A <code>boolean</code> containing the new trigger output.
	 * @param peripheral	The <code>Peripheral</code> on which the trigger occurred.
	 * @param triggerName 	A <code>String</code> containing the name of the trigger that occurred.
	 * @param arguments 	A <code>String</code> array containing the arguments of the trigger that occurred.
	 * @see TriggerLogicBlock
	 */
	private synchronized void updateTrigger(boolean value, Peripheral peripheral, String triggerName, String... arguments)
	{
		for(int triggerIndex = 0; triggerIndex < triggers.size(); triggerIndex++)
		{
			TriggerLogicBlock triggerLogicBlock = triggers.get(triggerIndex);
			if(triggerLogicBlock.getPeripheral().equals(peripheral) && triggerLogicBlock.getTriggerString().equals(triggerName))
			{
				String[] triggerLogicBlockArguments = ((TriggerLogicBlock) triggerLogicBlock).getArguments();
				if(triggerLogicBlockArguments.length == arguments.length)
				{
					boolean argumentsMatch = true;
					for(int argumentsIndex = 0; argumentsIndex < arguments.length; argumentsIndex++)
					{
						if(!arguments[argumentsIndex].equals(triggerLogicBlockArguments[argumentsIndex]))
						{
							argumentsMatch = false;
							break;
						}
					}
					if(argumentsMatch)
					{
						triggerLogicBlock.update(value);
						break;
					}
				}	
			}
		}
	}
	
	/**
	 * This method creates and starts a new <code>Thread</code> that checks the database for expired session IDs every hour. Any expired IDs are removed from the database.
	 */
	private void manageSessions()
	{
		new Thread(
				new Runnable()
				{
					public void run()
					{
						while(true)
						{
							try 
							{
								Date currentDate = new Date();
								Timestamp currentTime = new Timestamp(currentDate.getTime());
								
								Connection connection = DriverManager.getConnection(databaseURL, databaseUser, databasePassword);
								String query = "DELETE FROM sessions WHERE expiryTime<'" + currentTime.toString() + "'";
								Statement statement = connection.createStatement();
								statement.executeUpdate(query);
								Thread.sleep(3600000); //only check every hour
							} 
							catch (Exception e) 
							{
							}
						}
					}
				}
				).start();
	}
	
	//taken from http://stackoverflow.com/questions/332079/in-java-how-do-i-convert-a-byte-array-to-a-string-of-hex-digits-while-keeping-l
	/**
	 * This method converts an array of bytes into a <code>String</code> of hexadecimal characters.
	 * 
	 * @param bytes The array of bytes to be converted.
	 * @return A <code>String</code> of hexadecimal numbers representing the array of bytes.
	 * @author Ayman (taken from {@link http://stackoverflow.com/questions/332079/in-java-how-do-i-convert-a-byte-array-to-a-string-of-hex-digits-while-keeping-l} on January 18, 2014)
	 */
	private static String toHex(byte[] bytes)
	{
		BigInteger bigInteger = new BigInteger(1, bytes);
		return String.format("%0" + (bytes.length << 1) + "x", bigInteger);
	}
}
