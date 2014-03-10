/**
 * An <code>AndLogicBlock</code> is a {@link LogicBlock} that has an <code>output</code> values of 
 * <code>true</code> when the <code>output</code> values of all <code>LogicBlock</code> objects in 
 * <code>prevBlocks</code> are <code>true</code>.
 * The <code>update</code> methods of the next blocks are called by the <code>update</code> method if the 
 * new <code>output</code> value is <code>true</code> or if the <code>output</code> value is changing from 
 * <code>true</code> to <code>false</code>.
 * 
 * @author Shawn Koop
 */
public class AndLogicBlock extends LogicBlock
{
	/**
	 * The <code>output</code> value of this <code>AndLogicBlock</code> is set to <code>true</code> if
	 * the <code>output</code> values of all <code>LogicBlock</code> objects in 
	 * <code>prevBlocks</code> are <code>true</code>. Otherwise the <code>output</code> value is set to <code>false</code>.
	 * <p>
	 * The <code>update</code> methods of the next blocks are called if the 
	 * new <code>output</code> value is <code>true</code> or if the <code>output</code> value is changing from 
	 * <code>true</code> to <code>false</code>.
	 */
	public void update(boolean callerOutput)
	{
		boolean output = true;
		if(callerOutput == false)
		{
			output = false;
		}
		else 
		{
			for(int prevBlocksIndex = 0; prevBlocksIndex < prevBlocks.size(); prevBlocksIndex++)	//iterate all previous blocks
			{
				if(prevBlocks.get(prevBlocksIndex).getOutput() == false)		//if a block has a false output, set output to false and break
				{
					output = false;
					break;
				}
			}
		}
		
		if((getOutput() == true && output == false) || output == true) //if the output is changing from true to false or is changing to true
		{
			setOutput(output);
			updateNext();
		}
	}
}
