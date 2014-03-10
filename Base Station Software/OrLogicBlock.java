/**
 * An <code>OrLogicBlock</code> is a {@link LogicBlock} that has an <code>output</code> value of <code>true</code> 
 * when the <code>output</code> value of one or more <code>LogicBlock</code> objects in <code>prevBlocks</code> 
 * is <code>true</code>.
 * The <code>update</code> methods of the next blocks are only called if the <code>LogicBlock</code> that called <code>update</code> on this <code>OrLogicBlock</code>
 * had an <code>output</code> value of <code>true</code> or if the <code>output</code> value of this block is changing from <code>true</code> to <code>false</code>.
 * 
 * @author Shawn Koop
 */
public class OrLogicBlock extends LogicBlock
{	
	/**
	 * The <code>output</code> value of this <code>OrLogicBlock</code> is set to <code>true</code> if
	 * <code>callerOutput</code> is <code>true</code>. If <code>callerOutput</code> is <code>false</code>
	 * all of the <code>prevBlocks</code> are checked. If none of their <code>output</code> values are <code>true</code>,
	 * the <code>output</code> value of this <code>OrLogicBlock</code> is set to <code>false</code>. 
	 * <p>
	 * The <code>update</code> methods of the next blocks are called if <code>callerOutput</code> is <code>true</code> or if the <code>output</code> value of this <code>OrLogicBlock</code> is changing from <code>true</code> to <code>false</code>.
	 */
	public void update(boolean callerOutput)
	{
		boolean newOutput = false;
		if(callerOutput == true)
		{
			newOutput = true;
		}
		else
		{
			for(int prevBlocksIndex = 0; prevBlocksIndex < prevBlocks.size(); prevBlocksIndex++)	//iterate all of the previous blocks
			{
				if(prevBlocks.get(prevBlocksIndex).getOutput() == true)			//if a block has a true output set newOutput to true and break
				{
					newOutput = true;
					break;
				}
			}
		}
		if(callerOutput == true || (newOutput == false && getOutput() == true))	//if the caller is true or the output is changing from true to false
		{
			setOutput(newOutput);
			updateNext();
		}
	}
}
