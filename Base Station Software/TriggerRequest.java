import peripherals.Peripheral;

/**
 * A data structure used for storing trigger requests from peripherals on the request queue.
 * 
 * @author Shawn Koop
 *
 */
public class TriggerRequest 
{
	/**
	 * The {@link Peripheral} on which the trigger occurred.
	 */
	public Peripheral peripheral;
	/**
	 * A <code>String</code> representing the trigger that occurred.
	 */
	public String triggerName;
	/**
	 * An array of </code>String</code> objects that represent arguments of the trigger.
	 */
	public String[] arguments;
	/**
	 * The <code>boolean</code> value of the trigger.
	 */
	public boolean value;
	
	/**
	 * Creates a TriggerRequest object.
	 * 
	 * @param value				The <code>boolean</code> value of the trigger.
	 * @param peripheral		The {@link Peripheral} object making the trigger request.
	 * @param triggerName		The <code>String</code> representation of the trigger that has occurred.
	 * @param arguments			<code>String</code> objects that represent arguments of the trigger that has occurred.
	 */
	public TriggerRequest(boolean value, Peripheral peripheral, String triggerName, String... arguments)
	{
		this.peripheral = peripheral;
		this.triggerName = triggerName;
		this.value = value;
		this.arguments = arguments;
	}
}
