import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;

import sun.tools.tree.ThisExpression;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * The <code>WebRequestHandler</code> is responsible for handling and parsing requests from the user interface and calling the appropriate methods in the <code>MainController</code>.
 * 
 * @author Shawn Koop
 */
public class WebRequestHandler implements HttpHandler
{
	/**
	 * The main controller of the automation system whose requests are handled by this <code>WebRequestHandler</code>.
	 */
	private MainController mainController;
	
	/**
	 * A <code>String</code> that contains the path to the folder containing peripheral descriptors
	 */
	private String pluginsFolder = "plugins";//"/home/pi/Jarvis/plugins";
	
	/**
	 * Create a new <code>WebRequestHandler</code> to handle user interface requests for the <code>MainController</code> of the automation system.
	 * 
	 * @param mainController The main controller of the automation system whose requests are handled by this <code>WebRequestHandler</code>.
	 */
	public WebRequestHandler(MainController mainController)
	{
		this.mainController = mainController;
	}
	
	/**
	 * This method handles HTTP requests and responses for the user interface. Many requests are delegated to the <code>MainController</code> class.
	 * 
	 * @param exchange The <code>HttpExchange</code> containing the HTTP request to be handled and the HTTP response to be sent.
	 */
	public void handle(HttpExchange exchange) throws IOException 
	{
		String requestMethod = exchange.getRequestMethod();
		String response = "";
		int responseCode = 200;
		
		if(requestMethod.equalsIgnoreCase("GET"))
		{
			URI uri = exchange.getRequestURI();
			System.out.println("Received GET: " + uri.toString());
			String path = uri.getPath();
			if((!validateSID(exchange) && !path.equals("/script.js") && !path.equals("/style.css")) || path.equals("/newAccount.html") || path.equals("/logout.html"))
			{
				response = getLoginPage();
				if(path.equals("/logout.html"))
				{
					destroySID(exchange);
				}
			}
			else
			{	
				response = getFileData(path);
			}
			if(response.equals("<h1>404 Not Found</h1>"))
				responseCode = 404;
		}
		else if(requestMethod.equalsIgnoreCase("POST"))
		{
			URI uri = exchange.getRequestURI();
			String uriString = uri.toString().substring(1);	// take out the '/'
			System.out.println("Received POST: " + uriString);
			
			if(uriString.equals("login"))
			{
				InputStream is = exchange.getRequestBody();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String loginRequest = br.readLine();
				if(loginRequest != null)
				{
					String[] formData = loginRequest.split("&");
					if(formData.length == 2)
					{
						String userName = "";
						String password = "";
						for(int i = 0; i < formData.length; i++)
						{
							String[] inputData = formData[i].split("=");
							if(inputData[0].equalsIgnoreCase("userName"))
								userName = inputData[1];
							else if(inputData[0].equalsIgnoreCase("password"))
								password = inputData[1];
						}
						
						
						String sid = mainController.authenticateUser(userName, password);
						
						if(sid != null)
						{
							Headers responseHeaders = exchange.getResponseHeaders();
							responseHeaders.add("Set-Cookie", "sid=" + sid + "; Secure; HttpOnly");
							response = getFileData("/index.html");
							if(response.equals("<h1>404 Not Found</h1>"))
								responseCode = 404;
						}
						else 
						{
							response = getLoginPage();
							if(response.equals("<h1>404 Not Found</h1>"))
								responseCode = 404;
						}	
					}
					else 
					{
						response = getLoginPage();
						if(response.equals("<h1>404 Not Found</h1>"))
							responseCode = 404;
					}
				}
				else 
				{
					response = getLoginPage();
					if(response.equals("<h1>404 Not Found</h1>"))
						responseCode = 404;
				}
			}
			else if(uriString.equals("new_account"))
			{
				InputStream is = exchange.getRequestBody();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String newAccountRequest = br.readLine();
				if(newAccountRequest != null)
				{
					String[] formData = newAccountRequest.split("&");
					if(formData.length == 2)
					{
						String userName = "";
						String password = "";
						for(int formDataIndex = 0; formDataIndex < formData.length; formDataIndex++)
						{
							String[] inputData = formData[formDataIndex].split("=");
							if(inputData[0].equalsIgnoreCase("userName"))
								userName = inputData[1];
							else if(inputData[0].equalsIgnoreCase("password"))
								password = inputData[1];
						}
						
						
						String sid = mainController.createUserAccount(userName, password);
						
						if(sid != null)
						{
							Headers responseHeaders = exchange.getResponseHeaders();
							responseHeaders.add("Set-Cookie", "sid=" + sid + "; Secure; HttpOnly");
							response = getFileData("/index.html");
							if(response.equals("<h1>404 Not Found</h1>"))
								responseCode = 404;
						}
						else 
						{
							response = getLoginPage();
							if(response.equals("<h1>404 Not Found</h1>"))
								responseCode = 404;
						}	
					}
					else 
					{
						response = getLoginPage();
						if(response.equals("<h1>404 Not Found</h1>"))
							responseCode = 404;
					}
				}
				else 
				{
					response = getLoginPage();
					if(response.equals("<h1>404 Not Found</h1>"))
						responseCode = 404;
				}
			}
			else if(uriString.equals("uploadDescriptor"))
			{
				downloadDescriptor(exchange);
				
			}
			else if(uriString.equals("program_data"))
			{
				if(!validateSID(exchange))
					response = "not_logged_in";
				else
				{
					InputStream inputStream = exchange.getRequestBody();
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
					String line = null;
					while((line = bufferedReader.readLine()) != null)
					{
						String[] splitLine = line.split("\\|");
						if(splitLine.length >= 1 && !splitLine[0].equals(""))
						{
							String command = splitLine[0];
							if(command.equals("message_board"))
							{
								//return message board as a String of messages separated by |
								response = mainController.getStatus("Message Board", "Messages");
							}
							else if(command.equals("device_list"))
							{
								//return list of names of all peripheral devices in the system separated by |
								response = mainController.getDeviceList();
							}
							else if(command.equals("device_info"))
							{
								if(splitLine.length == 2 && !splitLine[1].equals(""))
								{
									String peripheralName = splitLine[1];
									response = mainController.getPeripheralInfo(peripheralName);
									//return information of peripheral with name deviceName
								}
								else 
								{
									response = "invalid_num_inputs";
								}
							}
							else if(command.equals("service_list"))
							{
								//return list of names of all services separated by |
								response = mainController.getServiceList();
							}
							else if(command.equals("service_representation"))
							{
								//return service representation of service with provided name
								if(splitLine.length == 2 && !splitLine[1].equals(""))
								{
									String serviceName = splitLine[1];
									response = mainController.getServiceRepresentation(serviceName);
								}
								else 
								{
									response = "invalid_num_inputs";
								}
							}
							else if(command.equals("service_info"))
							{
								//return trigger and action information of service with provided name
								if(splitLine.length == 2 && !splitLine[1].equals(""))
								{
									String serviceName = splitLine[1];
									response = mainController.getServiceInfo(serviceName);
								}
								else 
								{
									response = "invalid_num_inputs";
								}
							}
							else if (command.equals("create_service"))
							{
								if(splitLine.length == 3 && !splitLine[1].equals("") && !splitLine[2].equals(""))
								{
									String serviceName = splitLine[1];
									String serviceRepresentation = splitLine[2];
									response = mainController.createNewService(serviceName, serviceRepresentation);
								}
								else 
								{
									response = "invalid_num_inputs";
								}
								//response = service_created, invalid_name, invalid_representation, creation_failed
							}
							/*
							 * Request:	remove_service|[service name]
								Response:	service_removed
											invalid_name
											removal_failed
											missing_input
							 */
							else if(command.equals("remove_service"))
							{
								if(splitLine.length == 2 && !splitLine[1].equals(""))
								{
									String serviceName = splitLine[1];
									response = mainController.removeService(serviceName);
								}
								else 
								{
									response = "invalid_num_inputs";
								}
							}
							/*
							 * 	Request:	edit_service|[service name]|[new representation]
								Response:	service_edited
											edit_failed
											invalid_num_inputs
							 */
							else if(command.equals("edit_service"))
							{
								if(splitLine.length == 3 && !splitLine[1].equals("") && !splitLine[2].equals(""))
								{
									String serviceName = splitLine[1];
									String newRepresentation = splitLine[2];
									response = mainController.editService(serviceName, newRepresentation);
								}
								else
								{
									response = "invalid_num_inputs";
								}
							}
							/*
							  	name_edited
								invalid_name
								edit_failed
								invalid_num_inputs

							*/
							else if(command.equals("edit_service_name"))
							{
								if(splitLine.length == 3 && !splitLine[1].equals("") && !splitLine[2].equals(""))
								{
									String oldServiceName = splitLine[1];
									String newServiceName = splitLine[2];
									response = mainController.editServiceName(oldServiceName, newServiceName);
								}
								else 
								{
									response = "invalid_num_inputs";
								}
							}
							else if(command.equals("wait_for_signal"))
							{
								if(splitLine.length == 2 && !splitLine[1].equals(""))
								{
									int secondsToWait = Integer.parseInt(splitLine[1]);
									response = mainController.searchForPeripheral(secondsToWait);
								}
								
							}
							else if(command.equals("add_device"))
							{
								if(splitLine.length == 4 && !splitLine[1].equals("") && !splitLine[2].equals("") && !splitLine[3].equals(""))
								{
									int peripheraltypeID = Integer.parseInt(splitLine[1]);
									int peripheralID = Integer.parseInt(splitLine[2]);
									String peripheralName = splitLine[3];
									response = mainController.addPeripheral(peripheraltypeID, peripheralID, peripheralName);
								}
								else 
								{
									response = "invalid_num_inputs";
								}
							}
							else if(command.equals("remove_device"))
							{
								if(splitLine.length == 2 && !splitLine[1].equals(""))
								{
									String peripheralName = splitLine[1];
									response = mainController.removePeripheral(peripheralName);
								}
							}
							else if(command.equals("edit_device_name"))
							{
								if(splitLine.length == 3 && !splitLine[1].equals("") && !splitLine[2].equals(""))
								{
									String oldPeripheralName = splitLine[1];
									String newPerpiheralName = splitLine[2];
									response = mainController.editPeripheralName(oldPeripheralName, newPerpiheralName);
								}
								else 
								{
									response = "invalid_num_inputs";
								}
							}
							else if(command.equals("get_status"))
							{
								if(splitLine.length >= 3 && !splitLine[1].equals("") && !splitLine[2].equals(""))
								{
									String peripheralName = splitLine[1];
									for(int splitLineIndex = 2; splitLineIndex < splitLine.length; splitLineIndex++)
									{
										String statusName = splitLine[splitLineIndex];
										response = response + mainController.getStatus(peripheralName, statusName);
										if(splitLineIndex < splitLine.length - 1)
											response = response + "|";
									}
								}
								else 
								{
									response = "invalid_num_inputs";
								}
							}
							else if(command.equals("do_action"))
							{
								if(splitLine.length >= 3 && !splitLine[1].equals("") && !splitLine[2].equals(""))	//blank arguments are allowed
								{
									String peripheralName = splitLine[1];
									String actionName = splitLine[2];
									String[] arguments = new String[splitLine.length - 3];			//create an array to hold the arguments of the action
									for(int splitLineIndex = 3; splitLineIndex < splitLine.length; splitLineIndex++)
									{
										arguments[splitLineIndex - 3] = splitLine[splitLineIndex]; //get the arguments of the action
									}
									response = mainController.doAction(peripheralName, actionName, arguments);
								}
								else 
								{
									response = "invalid_num_inputs";
								}
								System.out.println("Do action response = " + response);
							}
							else if(command.equals("edit_user_name"))
							{
								if(splitLine.length == 3 && !splitLine[1].equals("") && !splitLine[2].equals(""))
								{
									String oldUserName = splitLine[1];
									String newUserName = splitLine[2];
									response = mainController.editUserName(oldUserName, newUserName);
								}
								else 
								{
									response = "invalid_num_inputs";
								}
							}
							else if(command.equals("edit_password"))
							{
								if(splitLine.length == 3 && !splitLine[1].equals("") && !splitLine[2].equals(""))
								{
									String oldPassword = splitLine[1];
									String newPassword = splitLine[2];
									response = mainController.editPassword(oldPassword, newPassword);
								}
								else 
								{
									response = "invalid_num_inputs";
								}
							}
							else
							{
								response = "invalid_command";
							}
						}
						else
						{
							response = "invalid_command";
						}
					}
				}
				
			}
		}
		if(requestMethod.equalsIgnoreCase("GET"))
		{
			URI uri = exchange.getRequestURI();
			String path = uri.getPath();
			String[] pathSplit = path.split("\\.");
			String extension = pathSplit[1];
			String contentType = "text/plain";
			if(extension.equals("html"))
				contentType = "text/html";
			else if(extension.equals("js"))
				contentType = "application/javascript";
			else if(extension.equals("css"))
				contentType = "text/css";
			
			Headers responseHeaders = exchange.getResponseHeaders();
			responseHeaders.add("Content-Type", contentType);
		}
		exchange.sendResponseHeaders(responseCode, response.getBytes().length);
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
	
	/**
	 * This method returns the contents of the login page (login.html) if a user account exists in the system and returns the page for creating a new account (newAccount.html) if a user account does not exist.
	 *
	 * @return A String containing the HTML data for either the login page or the account creation page.
	 */
	private String getLoginPage()
	{
		if(mainController.getUserAccountExists())
		{
			return getFileData("/login.html");
		}
		else 
		{	
			return getFileData("/newAccount.html");
		}
	}
	
	/**
	 * This method validates the session ID of an HTTP request by parsing the cookie containing the session ID and calling the <code>validateSID</code> method in the <code>mainController</code>. 
	 * 
	 * @param exchange The <code>HttpExchange</code> representing the HTTP request to be validated.
	 * @return A <code>boolean</code> indicating whether the session ID is valid or not.
	 * @see MainController
	 */
	private boolean validateSID(HttpExchange exchange)
	{
		boolean loggedIn = false;
		Headers requestHeaders = exchange.getRequestHeaders();
		String cookieString = requestHeaders.getFirst("Cookie");
		if(cookieString != null)
		{
			String[] cookies = cookieString.split("; ");
			for(int cookieIndex = 0; cookieIndex < cookies.length; cookieIndex++)
			{
				String[] cookieData = cookies[cookieIndex].split("=");
				String cookieName = cookieData[0];
				String cookieValue = cookieData[1];
				if(cookieName.equals("sid"))
				{
					loggedIn = mainController.validateSID(cookieValue);
					break;
				}
			}
		}
		return loggedIn;
	}
	
	/**
	 * This method destroys the session ID of an HTTP request by parsing the cookie containing the session ID and calling the <code>destroySID</code> method in the <code>mainController</code>.
	 * 
	 * @param exchange The <code>HttpExchange</code> representing the HTTP request whose session ID should be destroyed.
	 * @see MainController
	 */
	private void destroySID(HttpExchange exchange)
	{
		Headers requestHeaders = exchange.getRequestHeaders();
		String cookieString = requestHeaders.getFirst("Cookie");
		if(cookieString != null)
		{
			String[] cookies = cookieString.split("; ");
			for(int cookieIndex = 0; cookieIndex < cookies.length; cookieIndex++)
			{
				String[] cookieData = cookies[cookieIndex].split("=");
				String cookieName = cookieData[0];
				String cookieValue = cookieData[1];
				if(cookieName.equals("sid"))
				{
					mainController.destroySID(cookieValue);
					break;
				}
			}
		}
	}
	
	/**
	 * This method returns a <code>String</code> containing the contents of the file with the specified <code>fileName</code> located in the /Website directory relative to the project's main folder.
	 * <p>
	 * If the file is not found, the response is "{@literal <h1>}404 Not Found{@literal </h1>}".
	 * 
	 * @param fileName The name of the file whose content is to be retrieved.
	 * @return A <code>String</code> containing the contents of the file.
	 */
	private String getFileData(String fileName)
	{
		String response = "";
		String fullPath = "/Website" + fileName;
		URL fileURL = getClass().getResource(fullPath);
		
		if(fileName.indexOf('/', 1) != -1 || fileURL == null)	//deal with directory tree crawl attack and nonexistent files
		{
			response = "<h1>404 Not Found</h1>";
		}
		else
		{
			File file = null;
			try 
			{
				file = new File(fileURL.toURI());
				FileReader fileReader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				String line = null;
				while((line = bufferedReader.readLine()) != null)
				{
					response = response + line + "\n";
				}
				bufferedReader.close();
				fileReader.close();
			} 
			catch (Exception e) 
			{
				response = "<h1>404 Not Found</h1>";
			}
		}
		if(response.equals(""))
			response = "<h1>404 Not Found</h1>";
		return response;
	}
	
	/**
	 * This method parses an HTTP request body containing a peripheral descriptor file and stores the contents of the file in a file in the <code>pluginsFolder</code> directory.
	 * The main controller is notified of the new descriptor by calling the <code>newDescriptorAdded</code> method in the <code>mainController</code>.
	 * 
	 * @param exchange The <code>HttpExchange</code> representing the HTTP request containing the file to be downloaded.
	 * @see MainController
	 */
	private void downloadDescriptor(HttpExchange exchange)
	{
		try 
		{
			InputStream inputStream = exchange.getRequestBody();
			byte[] buffer = new byte[1000];
			int bytesRead = 0;
			File tempFile = new File(pluginsFolder, "download.tmp");
			tempFile.createNewFile();
			FileOutputStream tempFileOutputStream = new FileOutputStream(tempFile);
			while((bytesRead = inputStream.read(buffer)) != -1)
			{
				tempFileOutputStream.write(buffer, 0, bytesRead);
			}
			inputStream.close();
			tempFileOutputStream.close();
			
			
			FileReader fileReader = new FileReader(tempFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String firstLine = bufferedReader.readLine() + "\n";
			int numTrailingBytes = firstLine.getBytes().length + "--".getBytes().length + 2;
			int numLeadingBytes = numTrailingBytes;
			numTrailingBytes++;
			String secondLine = bufferedReader.readLine() + "\n";
			int fileNameStartIndex = secondLine.indexOf("filename=\"") + 10;
			int fileNameEndIndex = secondLine.indexOf("\"", fileNameStartIndex);
			String fileName = secondLine.substring(fileNameStartIndex, fileNameEndIndex);
			numLeadingBytes += secondLine.length();
			String thirdLine = bufferedReader.readLine() + "\n";
			numLeadingBytes += thirdLine.length();
			String fourthLine = bufferedReader.readLine() + "\n";
			numLeadingBytes += fourthLine.length();
			bufferedReader.close();
			fileReader.close();
			
			FileInputStream fileInputStream = new FileInputStream(tempFile);
			File file = new File(pluginsFolder, fileName);
			tempFile.createNewFile();
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			
			fileInputStream.skip(numLeadingBytes);
			int bytesToRead = (int) (tempFile.length() - numLeadingBytes - numTrailingBytes);
			int bytesToWrite = 0;
			bytesRead = 0;
			while(bytesToRead > 0 && (bytesRead = fileInputStream.read(buffer)) != -1)
			{
				bytesToRead -= bytesRead;
				if(bytesToRead < 0)
					bytesToWrite = bytesRead + bytesToRead;
				else
					bytesToWrite = bytesRead;
				fileOutputStream.write(buffer, 0, bytesToWrite);
			}
			
			fileOutputStream.close();
			fileInputStream.close();
			tempFile.delete();
			mainController.newDescriptorAdded();
		} 
		catch (Exception e)
		{
//			e.printStackTrace();
		}
		
	}
}
