import peripherals.Peripheral;

/**
 * An <code>ActionLogicBlock</code> is a {@link LogicBlock} that represents an action that should occur in the system
 * when the appropriate triggers have occurred.
 * <p>
 * Each <code>ActionLogicBlock</code> consists of the {@link Peripheral} on which the action should occur and the <code>actionString</code>
 * that represents the action on the <code>Peripheral</code>.
 * <p>
 * When the update method of this <code>ActionLogicBlock</code> is called, if the <code>callerOutput</code> field is <code>true</code> then
 * the <code>doAction</code> method of the <code>peripheral</code> will be called and the action represented by <code>actionString</code>
 * will be executed by the <code>peripheral</code>.
 * <p>
 * The <code>nextBlocks</code> list of an <code>ActionLogicBlock</code> is always empty since an action represents the completion of a logical statement.
 * <p>
 * The <code>output</code> value of an <code>ActionLogicBlock</code> is never updated because the output of an <code>ActionLogicBlock</code> is considered
 * to be the execution of an action on a <code>Peripheral</code>.
 * 
 * @author Shawn Koop
 */
public class ActionLogicBlock extends LogicBlock
{
	/**
	 * The {@link Peripheral} on which the action represented by this <code>ActionLogicBlock</code> should occur.
	 */
	private Peripheral peripheral;
	/**
	 * A <code>String</code> that represents the action to occur on <code>peripheral</code>.
	 */
	private String actionString;
	/**
	 * An array of </code>String</code> objects that represent arguments of the action.
	 */
	private String[] arguments;
	
	/**
	 * Creates a new <code>ActionLogicBlock</code> object with the specified <code>actionString</code> and <code>peripheral</code>
	 * that represent an action to occur in the system.
	 * 
	 * @param peripheral 		The {@link Peripheral} on which the action should occur.
	 * @param actionString 		A <code>String</code> that represents the action to occur on <code>peripheral</code>.
	 * @param arguments			<code>String</code> objects that represent arguments of the action.
	 */
	public ActionLogicBlock(Peripheral peripheral, String actionString, String... arguments)
	{
		super();
		this.peripheral = peripheral;
		this.actionString = actionString;
		this.arguments = arguments;
	}
	
	/**
	 * Returns the <code>Peripheral</code> object on which the action represented by this <code>ActionLogicBlock</code> is to occur.
	 * 
	 * @return The <code>Peripheral</code> object on which the action represented by this <code>ActionLogicBlock</code> is to occur.
	 */
	public Peripheral getPeripheral()
	{
		return peripheral;
	}

	/**
	 * Returns the <code>String</code> that represents the action to occur on <code>peripheral</code> that is represented by this <code>ActionLogicBlock</code>.
	 *
	 * @return The <code>String</code> that represents the action to occur on <code>peripheral</code> that is represented by this <code>ActionLogicBlock</code>.
	 */
	public String getActionString()
	{
		return actionString;
	}
	
	/**
	 * Returns the array of <code>String</code> objects that represent the arguments of the action that occurs on <code>peripheral</code> that is represented by this <code>ActionLogicBlock</code>.
	 * 
	 * @return The array of <code>String</code> objects that represent the arguments of the action that occurs on <code>peripheral</code> that is represented by this <code>ActionLogicBlock</code>.
	 */
	public String[] getArguments()
	{
		return arguments;
	}
	
	/**
	 * This method overrides the <code>addNextBlock</code> method in {@link LogicBlock} so that the method does nothing because <code>ActionLogicBlock</code> objects have no next blocks.
	 * <p>
	 * The <code>addNextBlock</code> method of an <code>ActionLogicBlock</code> should never be called but this is implemented as a safeguard in case someone tries.
	 * 
	 * @param block This field is not used but is kept to override this method in {@link LogicBlock}.
	 */
	public void addNextBlock(LogicBlock block)	//should override ability to have next blocks
	{
		
	}
	
	/**
	 * When this method is called, if the <code>callerOutput</code> field is <code>true</code> then
	 * the <code>doAction</code> method of the <code>peripheral</code> will be called and the action represented by <code>actionString</code>
	 * will be executed by the <code>peripheral</code>.
	 */
	public void update(boolean callerOutput)
	{
		if(callerOutput == true)
		{
			peripheral.doAction(actionString, arguments);
		}
	}
}
