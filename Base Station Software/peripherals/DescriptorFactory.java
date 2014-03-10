package peripherals;
import java.io.File;
import java.net.URI;

import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.options.GetPluginOption;
import net.xeoh.plugins.base.options.getplugin.OptionCapabilities;

/**
 * Use this class to instantiate Descriptors when adding a new Peripheral. Call
 * the static method {@link #getDescriptor(int)}.
 * @author Paul
 *
 */
public class DescriptorFactory
{
	private static PluginManager pluginManager = null;
	private static URI pluginFolderURL = (new File("plugins/")).toURI();
	public static final boolean DEBUG = true;
	
	protected DescriptorFactory()
	{
		// defeat instantiation
	}
	
	/**
	 * approved way to get a <code>PeripheralDescriptor</code>.
	 * @param typeID the ID of the desired PeripheralDescriptor. The 
	 * peripheral hardware will be initialized with this ID.
	 * @return one of any of the <code>PeripheralDescriptor</code>s that
	 * have an ID matching <code>typeID</code>. Returns <code>null</code>
	 * if no such <code>PeripheralDescriptor</code> exists.
	 */
	public static PeripheralDescriptor getDescriptor(int typeID)
	{
		if (null == pluginManager) {
			pluginManager = PluginManagerFactory.createPluginManager();
			refreshPlugins();	
		}
		GetPluginOption option = new OptionCapabilities("id:" + typeID);
		DescriptorPlugin newDescriptorPlugin = pluginManager.getPlugin(DescriptorPlugin.class, option);
		if (null == newDescriptorPlugin) 
		{
			return null;
		} else 
		{
			return newDescriptorPlugin.getNewDescriptor();
		}
	}
//TODO: I don't think that this is necessary, just add Plugins to the /plugins folder and they will be found.
//	public boolean addNewDescriptor(String fileURL)
//	{
//		return false;
//	}
	/**
	 * Search for Plugins in the <code>/plugins</code> folder of the filesystem.
	 * If any Plugins are found, they are instantiated.
	 * @return true if successful
	 */
	//TODO: Make sure that plugins match DescriptorPlugin.class
	public static boolean refreshPlugins()
	{
		if (null != pluginManager) {
			pluginManager.addPluginsFrom(pluginFolderURL);
			
			if (DEBUG) {
				System.out.println("Adding plugins from " + pluginFolderURL);
			}
			
			return true;
		} else {
			return false;
		}
		
	}
}
