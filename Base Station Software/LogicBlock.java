import java.util.ArrayList;
/**
 * A <code>LogicBlock</code> represents one piece of the logical representation of a {@link Service} in the system.
 * <p>A <code>LogicBlock</code> contains a list of <code>prevBlocks</code> and <code>nextBlocks</code>.
 * <p>A <code>LogicBlock</code> has a <code>boolean</code> <code>output</code> value that is determined by the <code>update</code> method
 * to be implemented in subclasses of <code>LogicBlock</code>.
 * <p>Each <code>LogicBlock</code> also contains an <code>int</code>, <code>numServices</code>, that reflects the number of <code>Service</code> objects making use of this <code>LogicBlock</code>.
 * 
 * @author Shawn Koop
 *
 */
public abstract class LogicBlock 
{
	/**
	 * An <code>ArrayList</code> of previous <code>LogicBlock</code> objects.
	 */
	protected ArrayList<LogicBlock> prevBlocks;
	/**
	 * An <code>ArrayList</code> of next <code>LogicBlock</code> objects.
	 */
	protected ArrayList<LogicBlock> nextBlocks;
	/**
	 * A <code>boolean</code> value that may be updated through the <code>update</code> method.
	 */
	private boolean output;
	/**
	 * An <code>int</code> representing the number of {@link Service} objects making use of this <code>LogicBlock</code>.
	 */
	private int numServices;
	
	/**
	 * Creates a <code>LogicBlock</code> object that has no next or previous <code>LogicBlock</code>s,
	 * has an <code>output</code> of <code>false</code>, and initializes <code>numServices</code> to <code>0</code>.
	 */
	public LogicBlock()
	{
		prevBlocks = new ArrayList<LogicBlock>();
		nextBlocks = new ArrayList<LogicBlock>();
		output = false;
		numServices = 0;
	}
	
	/**
	 * Returns the <code>output</code> value of this <code>LogicBlock</code>.
	 * 
	 * @return	The <code>output</code> value of this <code>LogicBlock</code>.
	 */
	public boolean getOutput()
	{
		return output;
	}
	
	/**
	 * Sets the <code>output</code> value of this <code>LogicBlock</code> to the specified <code>value</code>.
	 * 
	 * @param newOutput A <code>boolean</code> containing the value to set the output of this <code>LogicBlock</code> to.
	 */
	public void setOutput(boolean newOutput)
	{
		output = newOutput;
	}
	
	/**
	 * Add a <code>LogicBlock</code> to the list of previous <code>LogicBlock</code> objects, <code>prevBlocks</code>.
	 * 
	 * @param block A <code>LogicBlock</code> to be added to the list of previous <code>LogicBlock</code> objects.
	 */
	public void addPrevBlock(LogicBlock block)
	{
		if(!prevBlocks.contains(block))
			prevBlocks.add(block);
	}
	
	/**
	 * Add a <code>LogicBlock</code> to the list of next <code>LogicBlock</code> objects, <code>nextBlocks</code>.
	 * 
	 * @param block A <code>LogicBlock</code> to be added to the list of next <code>LogicBlock</code> objects.
	 */
	public void addNextBlock(LogicBlock block)
	{
		if(!nextBlocks.contains(block))
			nextBlocks.add(block);
	}
	
	/**
	 * Remove a specified <code>LogicBlock</code> from the list of previous <code>LogicBlock</code> objects, <code>prevBlocks</code>.
	 * 
	 * @param block A <code>LogicBlock</code> to be removed from the list of previous <code>LogicBlock</code> objects.
	 */
	private void removePrevBlock(LogicBlock block)
	{
		if(prevBlocks.contains(block))
			prevBlocks.remove(block);
	}
	
	/**
	 * Remove a specified <code>LogicBlock</code> from the list of next <code>LogicBlock</code> objects, <code>nextBlocks</code>.
	 * 
	 * @param block A <code>LogicBlock</code> to be removed from the list of next <code>LogicBlock</code> objects.
	 */
	private void removeNextBlock(LogicBlock block)
	{
		if(nextBlocks.contains(block))
			nextBlocks.remove(block);
	}
	
	/**
	 * Removes this <code>LogicBlock</code> from a service, decrementing <code>numServices</code>.
	 * <p>
	 * If <code>numServices</code> was <code>1</code>, then this <code>LogicBlock</code> is removed from the 
	 * <code>nextBlocks</code> list of all <code>LogicBlock</code> objects in <code>prevBlocks</code> and from the
	 * <code>prevBlocks</code> list of all <code>LogicBlock</code> objects in <code>nextBlocks</code>. This
	 * <code>LogicBlock</code> will have been removed from the system since it is no longer used by any services.
	 */
	public void removeFromService()
	{
		numServices--;
		if(numServices == 0)
		{
			for(int prevBlocksIndex = 0; prevBlocksIndex < prevBlocks.size(); prevBlocksIndex++)
			{
				prevBlocks.get(prevBlocksIndex).removeNextBlock(this);
			}
			for(int nextBlocksIndex = 0; nextBlocksIndex < nextBlocks.size(); nextBlocksIndex++)
			{
				nextBlocks.get(nextBlocksIndex).removePrevBlock(this);
			}
			prevBlocks = null;
			nextBlocks = null;
		}
	}
	
	/**
	 * Returns <code>numServices</code>.
	 * 
	 * @return The number of services using this <code>LogicBlock</code>.
	 */
	public int getNumServices()
	{
		return numServices;
	}
	
	/**
	 * Increments <code>numServices</code> by <code>1</code>.
	 */
	public void incrementNumServices()
	{
		numServices++;
	}
	
	/**
	 * Calls the <code>update</code> method of all <code>LogicBlock</code> objects in <code>nextBlocks</code>,
	 * passing the <code>output</code> of this <code>LogicBlock</code> as the <code>callerOutput</code>.
	 */
	public void updateNext()
	{
		for(int i = 0; i < nextBlocks.size(); i++)
		{
			nextBlocks.get(i).update(output);
		}
	}
	
	/**
	 * The <code>output</code> value of this <code>LogicBlock</code> is updated based on the <code>output</code>
	 * value of the <code>LogicBlock</code> that called <code>update</code> on this <code>LogicBlock</code>.
	 * 
	 * @param callerOutput The output value of the <code>LogicBlock</code> that called <code>update</code> on this <code>LogicBlock</code>.
	 */
	public abstract void update(boolean callerOutput);
}
