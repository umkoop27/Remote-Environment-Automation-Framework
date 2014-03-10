package peripherals;



/**
 * Classes that implement this interface are used to provide custom behaviour to a {@link peripherals.Peripheral}.
 * Hook methods are included that allow the custom behaviours to be discovered, including {@link #getTriggers()}
 * and {@link #getActions()}. These will be called by a higher level to discover the triggers and actions and 
 * display them to the user. As a developer, if you want to add an action or trigger, you have to make sure that 
 * it appears in  these lists.
 * @author Paul
 */
public interface PeripheralDescriptor
{
	/**
	 * @return a group of <code>String</code>s that represent the various
	 * triggers that this <code>PeripheralDescriptor</code> can handle.
	 * These <code>String</code>s will be passed to the Service Manager
	 * as arguments when a particular trigger happens. Each <code>String</code>
	 * is mapped to a list of <code>Argument</code>s that can be applied to
	 * the triggers.
	 */
	//public Map<String, ArrayList<Argument>> getTriggers();
		
	/**
	 * @return a group of <code>String</code>s that represent the various
	 * actions that this <code>PeripheralDescriptor</code> can do.
	 * These <code>String</code>s will be sent from the Service Manager
	 * when it wants a particular sction to take place, so the <code>
	 * PeripheralDescriptor</code> will have to be able to parse them. Each 
	 * <code>String</code> is mapped to a list of <code>Argument</code>s 
	 * that can be applied to the triggers.
	 */
	//public Map<String, ArrayList<Argument>> getActions();
	
	/**
	 * Parse <code>action</code> and do something. This is where you can define
	 * custom behaviour!
	 * @param action the action to do. Compare this to the list of supported actions.
	 * @param arguments Any extra data that may be needed.
	 */
	public void doAction(String action, String... arguments);
	
	/** 
	 * @return this <code>PeripheralDescriptor</code>s type code.
	 */
	public int getTypeID();
	
	/** 
	 * @return this <code>PeripheralDescriptor</code>s type as a String that could be displayed in the UI.
	 */
	public String getTypeString();
	
	public String getXMLString();
	
	/**
	 * Ask the peripheral for a status string to display in the user interface. The 
	 * contents of this string will be determined by the developer.
	 * @param statusString
	 * @return
	 */
	public String getStatus(String statusString);

	
	/**
	 * Set a delegate to receive trigger events
	 * @param delegate a class that implements {@link peripherals.TriggerHandler}.
	 */
	public void setDelegate(TriggerHandler delegate);
	
	/**
	 * Associate a <code>Peripheral</code> to this <code>PeripheralDescriptor</code>. 
	 * @param peripheral a {@link peripherals.Peripheral}
	 */
	public void setPeripheral(Peripheral peripheral);
	
	/**
	 * Ensure that a particular set of <code>Argument</code>s is valid for a particular
	 * trigger string. If the arguments are valid, but not yet in the set of enabled 
	 * triggers, they will be added.
	 * @param triggerID the ID for the particular trigger that is being checked
	 * @param arguments A ',' separated list of <code>String</code>s that are being 
	 * checked. Since this parameter is a <code>varargs</code>, it is possible to simply 
	 * call <p><code>validateTrigger("someString")</code> <p>if you are checking a trigger 
	 * that has no arguments.
	 * @return <code>true</code> if the combination of <code>triggerID</code> and 
	 * <code>arguments</code> exists already, or was successfully added. Returns
	 * </code>false</code> if the combination of <code>triggerID</code> and 
	 * <code>arguments</code> is not valid.
	 */
	public boolean validateTrigger(String triggerID, String... arguments);
	
	/**
	 * Ensure that a particular set of arguments is valid for a particular
	 * action string.
	 * @param actionID the ID for the particular action that is being checked
	 * @param arguments the ',' separated <code>String</code> objects that are being 
	 * checked. Since this parameter is a <code>varargs</code>, it is possible to simply 
	 * call <p><code>validateAction("someString")</code> <p>if you are checking an action 
	 * that has no arguments.
	 * @return <code>true</code> if the combination of <code>actionID</code> and 
	 * <code>arguments</code> can be called. Otherwise, returns <code>false</code>.
	 */
	public boolean validateAction(String actionID, String... arguments);
	
	/**
	 * Handle a bundle of data that was sent from the physical peripheral. This will often
	 * result in sending a callback to the developer-supplied peripheral descriptor.
	 * @param data
	 * @return <code>true</code> if the data was successfully processed.
	 */
	public boolean handlePacket(byte[] data);

}
