package automationNetwork;

/**
 * Defines how to communicate with the attached network. Default implementation is
 * {@link automationNetwork.ValerieNetwork}. This allows communication with an 
 * ArduIMU-based network of peripherals running Valerie's custom network layer.
 * this is because the communication messages may be application-sensitive (i.e.
 * using a ZibBee-based network layer may require different message types).
 * @author Paul White
 * @version 0.1
 *
 */
public interface Network 
{
	/**
	 * Instruct the network to start listening for new peripherals.
	 * @return <code>true</code> if successfully started listening, <code>false</code> 
	 * otherwise.
	 * @Note This occurs on a separate thread so you are free to {@link #wait()} after calling
	 * this.
	 */
	public boolean startListening();
	
	/**
	 * Instruct the network to stop listening for new peripherals.
	 * @return <code>true</code> if successfully stopped listening, <code>false</code> 
	 * otherwise.
	 */
	public boolean stopListening();
	
	/**
	 * Instruct the network to stop communicating with the peripheral having ID 
	 * <code>peripheralID</code>.
	 * @param peripheralID the peripheral to delete
	 * @return <code>true</code> if successful, <code>false</code> otherwise.
	 */
	public boolean removePeripheral(int peripheralID);
	
	/**
	 * Add a delegate to listen to network events. Be careful, consult your 
	 * implementation to ensure that it supports more than one listener.
	 * @param newListener the new delegate
	 */
	public void addListener(NetworkListener newListener);
	
	/**
	 * Send a packet to a particular peripheral in the network.
	 * @param peripheralID the ID of the peripheral to send the message to
	 * @param data the data to send
	 * @return <code>true</code> if successfully sent, <code>false</code> if not. 
	 * Also return <code>false</code> if there is no peripheral with ID <code>peripheralID</code>.
	 */
	public boolean sendPacket(int peripheralID, Object data);	
}
