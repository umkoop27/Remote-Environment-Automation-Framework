package automationNetwork;

/**
 * This interface defines the callbacks that must be implemented to respond to events in the
 * peripheral network.
 * @author Paul
 *
 */
public interface NetworkListener 
{
	/**
	 * Called when a new device has been discovered and assigned an ID.
	 * @param id an integer representing the unique ID of this new peripheral in the network.
	 * @param typeId an integer representing the type of this peripheral. That indicates which
	 * <code>PeripheralDescriptor</code> to use with it.
	 */
	public void didAddDevice(int id, int typeId);
	
	/**
	 * Called when a new packet has been received from the network.
	 * @param deviceID the ID of the peripheral that sent this packet
	 * @param data The array of bytes that was received. This should be passed to a peripheral to
	 * be deciphered.
	 */
	public void didReceivePacket(byte deviceID, byte[] data);
	
	/**
	 * Called when the <code>Network</code> has determined that a peripheral has stopped responding
	 * to messages. This should notify the user in some way or take action to reconnect with the 
	 * peripheral or disconnect with it altogether.
	 * @param id the ID of the device that is not responding.
	 */
	public void deviceNotResponding(int id);

}
