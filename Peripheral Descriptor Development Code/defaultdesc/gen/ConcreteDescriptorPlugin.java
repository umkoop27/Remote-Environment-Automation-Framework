
package defaultdesc.gen;

import peripherals.DescriptorPlugin;
import peripherals.PeripheralDescriptor;
import net.xeoh.plugins.base.annotations.Capabilities;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.events.Init;   

/**
* WARNING: DO NOT MODIFY THIS CODE AS IT IS AUTO-GENERATED
*/
@PluginImplementation // this implements the PeripheralDescriptor Plugin
public class ConcreteDescriptorPlugin implements DescriptorPlugin
{	
	@Init
	@Override
	public void onInit()
	{
	}

	@Capabilities
	@Override
	public String[] capabilities()
	{ 
		return new String[] {"id:" + CustomDescriptor.typeID}; 
	}
	
	@Override
	public PeripheralDescriptor getNewDescriptor() 
	{
		return CustomDescriptor.getInstance();
	}
	
}
    