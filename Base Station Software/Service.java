import java.util.ArrayList;

import peripherals.Peripheral;

/**
 * A <code>Service</code> consists of a set of triggers and actions that are connected with Boolean algebraic statements. This algebraic relationship is represented by a set of {@link LogicBlock}
 * objects that each represent a piece of the logic of a <code>Service</code>. Each <code>Service</code> also has a <code>name</code> and <code>serviceID</code>. 
 * <p>
 * A <code>Service</code> does not run in its own <code>Thread</code> because <code>LogicBlock</code> objects used in one <code>Service</code> can be used in others as well. Therefore another class
 * handles the updating of appropriate <code>LogicBlock</code> objects as triggers occur in the system. This class handles the creation, editing, and removal of a <code>Service</code> from the system.
 * 
 * @author Shawn Koop
 */
public class Service
{
	/**
	 * The name of the <code>Service</code>.
	 */
	private String name;
	/**
	 * A <code>String</code> representation of the logic of the <code>Service</code> that follows the following instructions.
	 * <p>
	 * <h1>Triggers and Actions</h1>
	 * Triggers and actions are both represented by a name associated with the peripheral whose trigger or action is being represented, followed by a ':::', followed by the name of the trigger or action itself, followed by zero or more arguments, all separated by a ':::'. For example, a trigger named "trig" occuring on peripheral "ed" is represented as "ed:::trig". Or, a trigger named "tempGreaterThan" occurring on peripheral "thermometer" with one argument, the threshold temperature, is represented as "thermometer:::tempGreaterThan:::21".
	 * <p>
	 * <h1>Implications</h1>
	 * All services follow the format "A::->B" where B is an action and A is a combination of various triggers. B follows the format described under the Triggers and Actions section. For example, an action named "doStuff" with no arguments to be done on peripheral "c" is represented as "c:::doStuff".
	 * <p>
	 * <h1>Combinations of Triggers</h1>
	 * The combination of triggers that precedes the implication sign in the String representation of the service can make use of none, one, or both of the logical operators OR and AND. The logical operator OR is represented as a '+' and the logical operator AND is represented as a '&'. Each layer of operations is to be enclosed in parentheses (preceded by '::'), including the case with no operations (a trigger). For example, the combination of the triggers "1:::a" and "2:::b" using the OR operator is denoted as "::(::(1:::a::)+::(2:::b::)::)". It is important that NO SPACES are used in the representation, except in the actual names of peripherals, triggers, and arguments. For another example, consider "::(::(::(1:::a::)+::(2:::b::)::)&::(3:::c::)::)", taking the AND of the result of the previous example and the trigger "3:::c". Both the AND and OR operations support more than two terms. For example, ::(::(1:::a::)+::(2:::b::)+::(3:::c::)::) is a valid combination. Lastly, for clarity, a service whose combination consists of only one trigger, say "1:::a", has a trigger combination "::(1:::a::)".
	 * <p>
	 * <h1>Services of Multiple Parts</h1>
	 * A service may be comprised of multiple parts. Each part follows the "A::->B" format mentioned earlier and is separated from other parts by a '::,'. For example, "::(1:::a::)::->4:::doStuff::,::(::(1:::a::)&::(2:::b::)::)::->3:::doOtherStuff" is a valid service representation.
	 */
	private String representation;
	/**
	 * The ID number of this <code>Service</code>.
	 */
	private int serviceID;
	/**
	 * An <code>ArrayList</code> containing all of the <code>LogicBlock</code> objects that are used in this <code>Service</code>.
	 */
	private ArrayList<LogicBlock> logicBlocks;

	/**
	 * Creates a new <code>Service</code> object and adds it to the system of <code>LogicBlock</code> objects.
	 * 
	 * @param name					A <code>String</code> containing the name of the new <code>Service</code>
	 * @param serviceID				An <code>int</code> containing the ID number of the new <code>Service</code>
	 * @param serviceRepresentation	A <code>String</code> representation of the logic of the new <code>Service</code>
	 * @param triggers				An <code>ArrayList</code> of <code>TriggerLogicBlock</code> objects that gives access to all <code>LogicBlock</code> objects in the entire system.
	 * @param peripherals			An <code>ArrayList</code> of all <code>Peripheral</code> objects in the system.
	 * @throws Exception			An exception is thrown when there is an error creating the service.
	 */
	public Service(String name, int serviceID, String serviceRepresentation, ArrayList<TriggerLogicBlock> triggers, ArrayList<Peripheral> peripherals) throws Exception
	{
		this.name = name;
		this.serviceID = serviceID;
		representation = serviceRepresentation;	//store a copy of the representation
		logicBlocks = addServiceToSystem(serviceRepresentation, triggers, peripherals);	//add the service to the system
	}
	
	/**
	 * Returns a <code>String</code> containing the <code>name</code> of this <code>Service</code> object.
	 * 
	 * @return The name of this <code>Service</code>
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Returns a <code>String</code> representation of the logic of this <code>Service</code> object.
	 * 
	 * @return The <code>String</code> representation of the logic of this <code>Service</code>
	 */
	public String getServiceRepresentation()
	{
		return representation;
	}
	
	/**
	 * Returns an <code>int</code> containing the ID number of this <code>Service</code>.
	 * 
	 * @return The ID number of this <code>Service</code>
	 */
	public int getServiceID()
	{
		return serviceID;
	}
	
	/**
	 * Returns an <code>ArrayList</code> containing all of the <code>LogicBlock</code> objects that make up this <code>Service</code>.
	 * 
	 * @return An <code>ArrayList</code> containing all of the <code>LogicBlock</code> objects that make up this <code>Service</code>
	 */
	public ArrayList<LogicBlock> getLogicBlocks()
	{
		return logicBlocks;
	}
	
	/**
	 * Changes the <code>name</code> of this <code>Service</code> to the name provided.
	 * 
	 * @param newName A <code>String</code> containing a new name for this <code>Service</code>
	 */
	public void setName(String newName)
	{
		name = newName;
	}
	
	/**
	 * Removes this <code>Service</code> from the system which is given as an <code>ArrayList</code> of <code>TriggerLogicBlock</code> objects.
	 * 
	 * @param triggers An <code>ArrayList</code> of <code>TriggerLogicBlock</code> objects representing the system to delete this <code>Service</code> from
	 */
	public void deleteFromSystem(ArrayList<TriggerLogicBlock> triggers)
	{
		Service.removeBlocks(triggers, logicBlocks);
	}
	
	/**
	 * Removes a specified list of logic blocks from a <code>Service</code> by calling the <code>removeFromService</code> method in the <code>LogicBlock</code> class.
	 * <p>
	 * If a <code>TriggerLogicBlock</code> object is included in <code>blocksToDelete</code> and was only a part of one <code>Service</code>, it is removed from the list of all <code>TriggerLogicBlock</code> objects in the system.
	 * 
	 * @param triggers An <code>ArrayList</code> containing all of the <code>TriggerLogicBlock</code> objects in the system
	 * @param blocksToDelete An <code>ArrayList</code> containing all of the <code>LogicBlock</code> objects to be removed from a <code>Service</code>
	 */
	private static void removeBlocks(ArrayList<TriggerLogicBlock> triggers, ArrayList<LogicBlock> blocksToDelete)
	{
		for(int blocksToDeleteIndex = 0; blocksToDeleteIndex < blocksToDelete.size(); blocksToDeleteIndex++)
		{
			LogicBlock block = blocksToDelete.get(blocksToDeleteIndex);
			if(block != null)
			{
				block.removeFromService();
				if(block instanceof TriggerLogicBlock && block.getNumServices() == 0)
					triggers.remove(block);
			}
		}
	}
	
	/**
	 * If the provided <code>editedServiceRepresentation</code> is not the same as the <code>String</code> representation of the logic of this <code>Service</code>,
	 * <code>LogicBlock</code> objects are added to or removed from this <code>Service</code> as necessary such that when this method completes this <code>Service</code>
	 * will be representable by <code>editedServiceString</code>. Upon completion of this method, <code>representation</code> and <code>editedServiceRepresentation</code> are the same.
	 * 
	 * @param editedServiceRepresentation The new <code>String</code> representation of the logic of this <code>Service</code>.
	 * @param triggers An <code>ArrayList</code> containing all of the <code>TriggerLogicBlock</code> objects in the system
	 * @param peripherals An <code>ArrayList</code> of all <code>Peripheral</code> objects in the system.
	 * @return A boolean stating whether or not the service was successfully edited.
	 */
	public boolean editService(String editedServiceRepresentation, ArrayList<TriggerLogicBlock> triggers, ArrayList<Peripheral> peripherals)
	{	
		boolean result = true;
		if(!representation.equals(editedServiceRepresentation))	//if the service is being edited
		{
			try 
			{
				ArrayList<LogicBlock> newServiceBlocks = addServiceToSystem(editedServiceRepresentation, triggers, peripherals);	//add the edited representation to the system
				removeBlocks(triggers, logicBlocks);																				//remove the old representation from the system
				logicBlocks = newServiceBlocks;
				representation = editedServiceRepresentation;
			} 
			catch (Exception e) 
			{
				result = false;
			}
		}
		return result;
	}
	
	/**
	 * This method adds a service that follows a given representation to the system represented by a given <code>ArrayList</code> of <code>TriggerLogicBlock</code> objects.
	 * If a required <code>LogicBlock</code> is not already in the system, it is created and added to the system. 
	 * <p>
	 * An <code>ArrayList</code> of all <code>LogicBlock</code> objects required to fulfill the <code>representation</code> is returned. 
	 * <p>
	 * The <code>numServices</code> values of each <code>LogicBlock</code> are updated as appropriate.
	 * 
	 * @param representation 	A <code>String</code> representation of the logic of the service to be added to the system.
	 * @param triggerBlocks		An <code>ArrayList</code> of <code>TriggerLogicBlock</code> objects that gives access to all <code>LogicBlock</code> objects in the entire system.
	 * @param peripherals		An <code>ArrayList</code> of all <code>Peripheral</code> objects in the system.
	 * @return					Returns an <code>ArrayList</code> containing all of the <code>LogicBlock</code> objects that are part of the service that was added to the system, null if an error occurred.
	 * @throws Exception		An exception is thrown when there is an error adding the service to the system.
	 */
	private static ArrayList<LogicBlock> addServiceToSystem(String representation, ArrayList<TriggerLogicBlock> triggerBlocks, ArrayList<Peripheral> peripherals) throws Exception
	{
		ArrayList<LogicBlock> serviceBlocks = new ArrayList<LogicBlock>();			//an ArrayList to hold the LogicBlock objects of the service being added to the system
		String[] splitRepresentations = representation.split("::,");					//split the representation into component parts
		for(int splitRepresentationsIndex = 0; splitRepresentationsIndex < splitRepresentations.length; splitRepresentationsIndex++)//for each part
		{
			String partRepresentation = splitRepresentations[splitRepresentationsIndex];//get the representation of this part
			int implicationIndex = partRepresentation.indexOf("::->");				//find the index of the implication
			String triggerCombinations = partRepresentation.substring(0, implicationIndex);	//get the combination of triggers, which occurs before the implication


			LogicBlock triggerCombinationOutput = null;//get the last logic block of the trigger combination, which will point to the action block
			try 
			{
				triggerCombinationOutput = addTriggerCombinationToSystem(triggerCombinations, triggerBlocks, peripherals, serviceBlocks);
			} 
			catch (Exception e) 
			{
				removeBlocks(triggerBlocks, serviceBlocks);
				throw new Exception("Error creating service");
			}	
			
			String actionString = partRepresentation.substring(implicationIndex+4);	//get the representation of the action
			String[] actionStringData = actionString.split(":::");					//split the action representation into its parts
			String peripheralName = actionStringData[0];							//get the name of the peripheral on which the action will occur
			String actionName = actionStringData[1];								//get the name of the action
			String[] arguments = new String[actionStringData.length - 2];			//create an array to hold the arguments of the action
			for(int actionStringDataIndex = 2; actionStringDataIndex < actionStringData.length; actionStringDataIndex++)
			{
				arguments[actionStringDataIndex - 2] = actionStringData[actionStringDataIndex]; //get the arguments of the action
			}
			
			ArrayList<LogicBlock> nextBlocks = triggerCombinationOutput.nextBlocks;	//get the next blocks of the last logic block of the trigger combination
																					//check to see if the required action block already exists in the system
			boolean actionBlockExists = false;										//holds whether or not the action block exists
			LogicBlock actionBlock = null;											//holds the action block to be added to the service
			for(int nextBlocksIndex = 0; nextBlocksIndex < nextBlocks.size(); nextBlocksIndex++)//iterate all of the next blocks
			{
				LogicBlock nextBlock = nextBlocks.get(nextBlocksIndex);				//get the current block
				if(nextBlock instanceof ActionLogicBlock)							//if the block is an action block
				{																	//if the block is the action block we are looking for
					if (((ActionLogicBlock) nextBlock).getPeripheral().getName().equals(peripheralName) && ((ActionLogicBlock) nextBlock).getActionString().equals(actionName))
					{
						String[] nextBlockArguments = ((ActionLogicBlock) nextBlock).getArguments();
						if(nextBlockArguments.length == arguments.length)
						{
							boolean argumentsMatch = true;
							for(int argumentsIndex = 0; argumentsIndex < arguments.length; argumentsIndex++)
							{
								if(!arguments[argumentsIndex].equals(nextBlockArguments[argumentsIndex]))
								{
									argumentsMatch = false;
									break;
								}
							}
							if(argumentsMatch)
							{
								actionBlock = nextBlock;									//update block
								actionBlockExists = true;									//the next block exists
								break;														//no need to keep looking
							}
						}
					}
				}
			}
			
			if(!actionBlockExists) 													//if the action block doesn't exist, create it
			{
				Peripheral peripheral = null;										//the peripheral object to be used to create the action block
				for(int peripheralsIndex = 0; peripheralsIndex < peripherals.size(); peripheralsIndex++)//iterate through all peripherals
				{
					Peripheral tempPeripheral = peripherals.get(peripheralsIndex);	//get the current peripheral
																					//if this peripheral has the right ID and supports the action
					if(tempPeripheral.getName().equals(peripheralName) && tempPeripheral.validateAction(actionName, arguments))
					{
						peripheral = tempPeripheral;								//found it
						break;														//no need to keep looking
					}
				}
				if(peripheral == null)												//if the peripheral was not found
				{
					removeBlocks(triggerBlocks, serviceBlocks);						//take the blocks back out
					throw new Exception("Error creating service");
				}
				actionBlock = new ActionLogicBlock(peripheral, actionName, arguments);			//create the new action block
			}
																					//connect the combination of triggers to the action
			triggerCombinationOutput.addNextBlock(actionBlock);							
			actionBlock.addPrevBlock(triggerCombinationOutput);
			
			actionBlock.update(triggerCombinationOutput.getOutput());				//update the action block to fire the action if the triggers have already occurred in the system
			
			if(!serviceBlocks.contains(actionBlock))										//necessary since two parts of a service could lead to the same action, even though an OR would make more sense in that situation
			{
				serviceBlocks.add(actionBlock);
				actionBlock.incrementNumServices();
			}
		}
		return serviceBlocks;														//return the logic blocks of the service
	}

	/**
	 * This method adds <code>LogicBlock</code> objects that follow the interpretation of a set of <code>triggerCombinations</code> 
	 * to the system represented by a given <code>ArrayList</code> of <code>TriggerLogicBlock</code> objects.
	 * <p>
	 * If a required <code>LogicBlock</code> is not already in the system, it is created and added to the system. 
	 * <p>
	 * The <code>LogicBlock</code> whose output is logically equivalent to the given <code>triggerCombinations</code> is returned.
	 * <p>
	 * The <code>numServices</code> values of each <code>LogicBlock</code> are updated as appropriate.
	 * <p>
	 * An <code>ArrayList</code> of <code>LogicBlock</code> objects already in the <code>Service</code> that the <code>triggerCombinations</code> are a part of is updated as 
	 * new <code>LogicBlock</code> objects are found or created. 
	 * 
	 * @param triggerCombinations	A <code>String</code> containing logical combinations of triggers
	 * @param triggerBlocks			An <code>ArrayList</code> of <code>TriggerLogicBlock</code> objects that gives access to all <code>LogicBlock</code> objects in the entire system
	 * @param peripherals			An <code>ArrayList</code> of all <code>Peripheral</code> objects in the system
	 * @param serviceBlocks			An <code>ArrayList</code> of <code>LogicBlock</code> objects already in the <code>Service</code> that the <code>triggerCombinations</code> are a part of
	 * @return						The <code>LogicBlock</code> whose output is logically equivalent to the given <code>triggerCombinations</code>, null if a problem occurred
	 * @throws Exception			An exception is thrown when there is an error adding the trigger combination to the system.
	 */
	private static LogicBlock addTriggerCombinationToSystem(String triggerCombinations, ArrayList<TriggerLogicBlock> triggerBlocks, ArrayList<Peripheral> peripherals, ArrayList<LogicBlock> serviceBlocks) throws Exception
	{
		String combinations = triggerCombinations.substring(3, triggerCombinations.length() - 3); //remove outer braces from triggerCombinations
		LogicBlock result = null;
		if(!combinations.substring(0, 3).equals("::("))												//if there are no more outer braces combinations is a trigger
		{
			String[] triggerData = combinations.split(":::");								//split the trigger data
			String peripheralName = triggerData[0];										//get the peripheral ID
			String triggerName = triggerData[1];										//get the trigger name
			String[] arguments = new String[triggerData.length - 2];			//create an array to hold the arguments of the trigger
			for(int triggerDataIndex = 2; triggerDataIndex < triggerData.length; triggerDataIndex++)
			{
				arguments[triggerDataIndex - 2] = triggerData[triggerDataIndex]; //get the arguments of the trigger
			}
			for(int triggersIndex = 0; triggersIndex < triggerBlocks.size(); triggersIndex++)//iterate through all of the system triggers
			{
				TriggerLogicBlock tempBlock = triggerBlocks.get(triggersIndex);
				if(tempBlock.getPeripheral().getName().equals(peripheralName) && tempBlock.getTriggerString().equals(triggerName)) //if there already exists a trigger block for this trigger
				{
					String[] tempBlockArguments = ((TriggerLogicBlock) tempBlock).getArguments();
					if(tempBlockArguments.length == arguments.length)
					{
						boolean argumentsMatch = true;
						for(int argumentsIndex = 0; argumentsIndex < arguments.length; argumentsIndex++)
						{
							if(!arguments[argumentsIndex].equals(tempBlockArguments[argumentsIndex]))
							{
								argumentsMatch = false;
								break;
							}
						}
						if(argumentsMatch)
						{
							result = tempBlock;											//the result has been found
							break;														//no need to look further
						}
					}												
				}
			}
			if(result == null)															//if there is not a trigger block for this trigger, make one
			{
				Peripheral peripheral = null;											//look for the right peripheral
				for(int k = 0; k < peripherals.size(); k++)								//iterate all peripherals
				{
					Peripheral tempPeripheral = peripherals.get(k);						//get the current peripheral
					if(tempPeripheral.getName().equals(peripheralName) && tempPeripheral.validateTrigger(triggerName, arguments))	//if the appropriate peripheral has been found
					{
						peripheral = tempPeripheral;									//store it
						break;															//no need to keep looking
					}
				}
				if(peripheral == null)													//if there is no peripheral with the given ID in the system
				{
					removeBlocks(triggerBlocks, serviceBlocks);							//remove any blocks added to the system. 
					throw new Exception("Error creating service");
				}
				result = new TriggerLogicBlock(peripheral, triggerName, arguments);		//create the new TriggerLogicBlock
				triggerBlocks.add((TriggerLogicBlock)result);							//add to the list of system trigger blocks
			}
		}
		else																			//combinations is not a trigger
		{
			int count = 0;																//used to count brackets: +1 for '(', -1 for ')'
																						//when 0 is hit the start and end index of a term of the logical statement are known
			int startIndex = 0;
			int endBeforeIndex = 0;
			ArrayList<LogicBlock> previousOutputBlocks = new ArrayList<LogicBlock>();	//holds all of the blocks that represent each term of the current logical statement
			char operation = 'n';														//to be used to determine which operation is being used: '+' or '&'
			for(int index = 0; index < combinations.length(); index++)					//iterate through all characters of combinations
			{
				if(combinations.substring(index, index+3).equals("::("))									//if current char is '(', increment count
					count++;
				else if(combinations.substring(index, index+3).equals("::)"))								//if current char is ')', decrement count
				{
					count--;
					if(count == 0)														//if count == 0, then index is at the close bracket of a term of the current logical statement
					{
						endBeforeIndex = index + 3;										//the end before index for the term is the next index
						String subCombinations = combinations.substring(startIndex, endBeforeIndex); //get the term
						try 
						{
							LogicBlock prevOutput = addTriggerCombinationToSystem(subCombinations, triggerBlocks, peripherals, serviceBlocks); //get the LogicBlock that represents the logical statement of the term
							previousOutputBlocks.add(prevOutput);							//add to the list blocks that represent the different terms
						} 
						catch (Exception e) 
						{
							removeBlocks(triggerBlocks, serviceBlocks);					//remove any blocks added to the system
							throw new Exception("Error creating service");
						}
		
						if(endBeforeIndex != combinations.length())						//if this is not the end of the combinations String, the next char is the logical operator
						{
							if(operation == 'n')										//if the operator hasn't been determined yet
								operation = combinations.charAt(endBeforeIndex);		//set it
							else if(operation != combinations.charAt(endBeforeIndex))	//if it has, double check to ensure that operations aren't being mixed
							{
								removeBlocks(triggerBlocks, serviceBlocks);							//remove any blocks added to the system. 
								throw new Exception("Error creating service");
							}
						}
						else 
						{
							break;
						}

						startIndex = endBeforeIndex + 1;								//the start index for the next term is right after the operator
					}
				}
			}
			
			boolean blockExists = false;
																						//look for a block that works
			LogicBlock prevBlock = previousOutputBlocks.get(0);							//any of the previous blocks can be checked, but for there to already be a block in the system only one would have to be searched since all previous blocks would be connected to the needed block
			ArrayList<LogicBlock> candidateBlocks = prevBlock.nextBlocks; 				//all next blocks of the chosen previous block are candidates to be the appropriate block
			for(int candidateBlocksIndex = 0; candidateBlocksIndex < candidateBlocks.size(); candidateBlocksIndex++) //iterate all candidate blocks
			{
				LogicBlock candidateBlock = candidateBlocks.get(candidateBlocksIndex);	//get the current candidate block
																						//if the operation of the candidate block is correct
				if((operation == '+' && candidateBlock instanceof OrLogicBlock)||(operation == '&' && candidateBlock instanceof AndLogicBlock))
				{
					ArrayList<LogicBlock> candidatePrevBlocks = candidateBlock.prevBlocks; 	//get the previous blocks of the candidate block
																							//the previous blocks of the candidate block must be the same as previousOutputBlocks
					boolean foundBlock = true;												//Check this. If true, foundBlock will be true.
					for(int candidatePrevBlocksIndex = 0; candidatePrevBlocksIndex < candidatePrevBlocks.size(); candidatePrevBlocksIndex++)
					{
						if(!previousOutputBlocks.contains(candidatePrevBlocks.get(candidatePrevBlocksIndex)))
						{
							foundBlock = false;
							break;
						}
					}
					
					if(foundBlock)														//if the candidate block is a successful match
					{
						result = candidateBlock;										//update the result to be the candidate
						blockExists = true;												
					}
				
				}
			}
			
			if(!blockExists)															//if the block doesn't exist it must be created
			{																			//create according to appropriate operation
				if(operation == '+')
					result = new OrLogicBlock();
				else
					result = new AndLogicBlock();
			}
																						//connect all of the logic blocks representing the different terms with the result block
			for(int previousOutputBlocksIndex = 0; previousOutputBlocksIndex < previousOutputBlocks.size(); previousOutputBlocksIndex++)
			{
				LogicBlock prevOutputBlock = previousOutputBlocks.get(previousOutputBlocksIndex);
				prevOutputBlock.addNextBlock(result);
				result.addPrevBlock(prevOutputBlock);
			}
			
			if(!blockExists)															//if result is a newly created block, update its output
			{																			//update must be called passing each previous block to ensure accurate results
				for(int previousOutputBlocksIndex = 0; previousOutputBlocksIndex < previousOutputBlocks.size(); previousOutputBlocksIndex++)
					result.update(previousOutputBlocks.get(previousOutputBlocksIndex).getOutput());
			}
		}
		if(!serviceBlocks.contains(result))												//if the result block is not already in the service
		{
			serviceBlocks.add(result);													//add it to the list of blocks in the service
			result.incrementNumServices();												//increment numServices
		}
		return result;
	}
}
