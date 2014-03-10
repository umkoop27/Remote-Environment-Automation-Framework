package peripherals;


public interface TriggerHandler 
{
	public void triggerOccurred(boolean value, Peripheral p, String trigger, String...arguments);
}
