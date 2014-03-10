package peripherals;

import net.xeoh.plugins.base.Plugin;
import net.xeoh.plugins.base.annotations.Capabilities;
import net.xeoh.plugins.base.annotations.events.Init;

public interface DescriptorPlugin extends Plugin
{	
	@Init
	public void onInit();
	
	@Capabilities
	public String[] capabilities();
	
	public PeripheralDescriptor getNewDescriptor();
}
