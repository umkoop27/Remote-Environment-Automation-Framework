import peripherals.Peripheral;

/**
 * A <code>TriggerLogicBlock</code> is a {@link LogicBlock} that represents a trigger occurring in the system.
 * <p>
 * Each <code>TriggerLogicBlock</code> consists of the {@link Peripheral} on which the trigger occurred and the <code>triggerString</code>
 * that represents the trigger on the <code>Peripheral</code>.
 * <p>
 * When the trigger represented by this block occurs in the system, the <code>update</code> method of this block
 * will be called and the <code>boolean</code> value of the trigger value will be passed to the method. The update
 * method updates the <code>output</code> value of this <code>TriggerLogicBlock</code> to the <code>newTriggerValue</code>
 * and then calls the <code>update</code> method of all <code>LogicBlock</code> objects in <code>nextBlocks</code>.
 * <p>
 * The <code>prevBlocks</code> list of a <code>TriggerLogicBlock</code> is always empty.
 * 
 * @author Shawn Koop
 */
public class TriggerLogicBlock extends LogicBlock
{
	/**
	 * The {@link Peripheral} on which the trigger represented by this <code>TriggerLogicBlock</code> occurs.
	 */
	private Peripheral peripheral;
	/**
	 * A <code>String</code> that represents the trigger occurring on <code>peripheral</code>.
	 */
	private String triggerString;
	/**
	 * An array of </code>String</code> objects that represent arguments of the trigger.
	 */
	private String[] arguments;
	
	/**
	 * Creates a new <code>TriggerLogicBlock</code> object with the specified <code>triggerString</code> and <code>peripheral</code>
	 * that represent a trigger occurring in the system.
	 * 
	 * @param peripheral 		The {@link Peripheral} on which the trigger occurs.
	 * @param triggerString 	A <code>String</code> that represents the trigger that occurs on <code>peripheral</code>.
	 * @param arguments			<code>String</code> objects that represent arguments of the trigger that occurs on <code>peripheral</code>.
	 */
	public TriggerLogicBlock(Peripheral peripheral, String triggerString, String... arguments)
	{
		super();
		this.peripheral = peripheral;
		this.triggerString = triggerString;
		this.arguments = arguments;
	}
	
	/**
	 * Returns the <code>Peripheral</code> object on which the trigger represented by this <code>TriggerLogicBlock</code> occurs.
	 * 
	 * @return The <code>Peripheral</code> object on which the trigger represented by this <code>TriggerLogicBlock</code> occurs.
	 */
	public Peripheral getPeripheral()
	{
		return peripheral;
	}
	
	/**
	 * Returns the <code>String</code> that represents the trigger that occurs on <code>peripheral</code> that is represented by this <code>TriggerLogicBlock</code>.
	 *
	 * @return The <code>String</code> that represents the trigger that occurs on <code>peripheral</code> that is represented by this <code>TriggerLogicBlock</code>.
	 */
	public String getTriggerString()
	{
		return triggerString;
	}
	
	/**
	 * Returns the array of <code>String</code> objects that represent the arguments of the trigger that occurs on <code>peripheral</code> that is represented by this <code>TriggerLogicBlock</code>.
	 * 
	 * @return The array of <code>String</code> objects that represent the arguments of the trigger that occurs on <code>peripheral</code> that is represented by this <code>TriggerLogicBlock</code>.
	 */
	public String[] getArguments()
	{
		return arguments;
	}
	
	/**
	 * This method overrides the <code>addPrevBlock</code> method in {@link LogicBlock} so that the method does nothing because <code>TriggerLogicBlock</code> objects have no previous blocks.
	 * <p>
	 * The <code>addPrevBlock</code> method of a <code>TriggerLogicBlock</code> should never be called but this is implemented as a safeguard in case someone tries.
	 * 
	 * @param block This field is not used and can be left <code>null</code>.
	 */
	public void addPrevBlock(LogicBlock block)
	{
		//should override the addPrevBlock function in LogicBlock to do nothing since a TriggerLogicBlock should not have any previous blocks
	}
	
	/**
	 * This method updates the <code>output</code> value of this <code>TriggerLogicBlock</code> to the <code>newTriggerValue</code>
	 * and then calls the <code>update</code> method of all <code>LogicBlock</code> objects in <code>nextBlocks</code>.
	 * 
	 * @param newTriggerValue The <code>boolean</code> value to update this <code>TriggerLogicBlock</code> to.
	 */
	public void update(boolean newTriggerValue) 
	{
		setOutput(newTriggerValue);
		updateNext();
	}

}
