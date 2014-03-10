<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
 	xmlns:java="http://www.java.com/"
 	xmlns:s="http://states.data"
 	exclude-result-prefixes="java xs">

	<xsl:function name="java:file-exists" xmlns:file="java.io.File" as="xs:boolean">
	  <xsl:param name="file" as="xs:string"/>
	  <xsl:param name="base-uri" as="xs:string"/>
	
	  <xsl:variable name="absolute-uri" select="resolve-uri($file, $base-uri)" as="xs:anyURI"/>
	  <xsl:sequence select="file:exists(file:new($absolute-uri))"/>
	</xsl:function>
	
	
    <xsl:output method="text" />
    
    <xsl:key name="type-lookup" match="s:type" use="s:abbr"/>
    <xsl:key name="func-lookup" match="s:func" use="s:abbr"/>
    
    <xsl:variable name="types-top" select="document('')/*/s:types"/>
    <xsl:variable name="funcs-top" select="document('')/*/s:funcs"/>
    
    <xsl:variable name="fileName" select="/peripheral/@class"/>
    <xsl:variable name="packageName" select="lower-case($fileName)"/>
    <xsl:variable name="triggerIDs">
    	<xsl:for-each select="peripheral/triggers/trigger">&lt;code><xsl:value-of select="@id" />&lt;/code><xsl:if test="following-sibling::trigger">, </xsl:if></xsl:for-each>.
    </xsl:variable>
    
    <xsl:variable name="actionIDs">
    	<xsl:for-each select="peripheral/actions/action">&lt;code><xsl:value-of select="@id" />&lt;/code><xsl:if test="following-sibling::action">, </xsl:if></xsl:for-each>.
    </xsl:variable>
    
    
    <xsl:template name ="CustomDescriptor" match="/" priority="100">
    
    <xsl:if test="not(java:file-exists(concat('src/', $packageName, '/', $fileName, '.java'), base-uri()))">
    	<xsl:result-document href="src/{$packageName}/{$fileName}.java">
    package <xsl:value-of select="$packageName" />;
    
    import <xsl:value-of select="$packageName" />.gen.CustomDescriptor;
    
    public class <xsl:value-of select="$fileName" /> extends CustomDescriptor
    {
    	    
    
    }
    	</xsl:result-document>
    </xsl:if>
    
    <xsl:result-document href="src/{$packageName}/gen/ConcreteDescriptorPlugin.java">
package <xsl:value-of select="$packageName" />.gen;

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
    </xsl:result-document>
    
    <xsl:result-document href="src/{$packageName}/gen/CustomDescriptor.java">
package <xsl:value-of select="$packageName" />.gen;


import peripherals.*;

//import java.security.InvalidParameterException; // used for testing if remote function ID is within permissible range

<xsl:if test="count(/peripheral/callback) &gt; 0">import java.util.Arrays;

import java.io.ByteArrayInputStream;</xsl:if>
<xsl:if test="count(/peripheral/remote/argument) &gt; 0">import java.io.ByteArrayOutputStream;</xsl:if>
<xsl:if test="count(/peripheral/remote) &gt; 0">import java.io.IOException;</xsl:if>

<xsl:if test="count(/peripheral/callback) + count(/peripheral/remote) &gt; 0">import org.msgpack.MessagePack;</xsl:if>
<xsl:if test="count(/peripheral/remote) &gt; 0">import org.msgpack.packer.Packer;</xsl:if>
<xsl:if test="count(/peripheral/callback) &gt; 0">import org.msgpack.unpacker.Unpacker;</xsl:if>

import automationNetwork.Network;

import <xsl:value-of select="$packageName" />.<xsl:value-of select="$fileName" />;
    
/**
* WARNING: DO NOT MODIFY THIS CODE AS IT IS AUTO-GENERATED
*/    
public abstract class CustomDescriptor implements PeripheralDescriptor {
	
	private static final boolean DEBUG = false;
	/** return the type ID of this peripheral. */
	public static final int typeID = <xsl:value-of select="peripheral/@typeID" />;
	public static final String xmlFileAddr = "<xsl:value-of select = "tokenize(base-uri(/), '/')[last()]" />";
	
	public static CustomDescriptor getInstance()
	{
		return new <xsl:value-of select="/peripheral/@class" />();
	}
	
	/////////////
	// Triggers
	/////////////
	
	private static final String[] triggerStrings = new String[]
		{
			<xsl:for-each select="peripheral/triggers/trigger">
			"<xsl:value-of select="name" />"<xsl:if test="following-sibling::trigger">,</xsl:if></xsl:for-each> 
		};
	<xsl:for-each select="peripheral/triggers/trigger">
	protected static final int <xsl:value-of select="@id" /> = <xsl:value-of select="position() - 1" />;</xsl:for-each>
	
	/////////////
	// Actions
	/////////////
	
	private static final String[] actionStrings = new String[]
		{
			<xsl:for-each select="peripheral/actions/action">
			"<xsl:value-of select="name" />"<xsl:if test="following-sibling::action">,</xsl:if></xsl:for-each> 
		};
	<xsl:for-each select="peripheral/actions/action">
	protected static final int <xsl:value-of select="@id" /> = <xsl:value-of select="position() - 1" />;</xsl:for-each>
	
	////////////////
	// Callback IDs
	////////////////
	<xsl:for-each select="peripheral/callback">
	/**Received some data from the physical peripheral. Call '<xsl:value-of select="translate(@function-id,' ','')" />CallBack()'*/
	public static final int <xsl:value-of select="upper-case(translate(@function-id,' ',''))" /> = <xsl:value-of select="@id" />;
	</xsl:for-each>
	
	//////////////////////
	// Instance Variables
	//////////////////////
	
	private TriggerHandler delegate;
	protected Peripheral mPeripheral;    
	
	/////////////
	// Overrides
	/////////////
	
	@Override
	public int getTypeID() 
	{
		return typeID;
	}
	
	@Override
	public String getTypeString()
	{
		return "<xsl:value-of select="/peripheral/peripheral-type" />";
	}
	
	@Override
	public String getXMLString()
	{
		return xmlFileAddr;
//		String xmlAsString = "XML NOT FOUND";
//		
//		ClassLoader cl = this.getClass().getClassLoader();
//		InputStream stream = cl.getResourceAsStream(xmlFileAddr);
//		if (null != stream)
//		{
//			xmlAsString = ""; // the XML file is found
//			BufferedReader mBufferedReader = new BufferedReader(new InputStreamReader(stream));
//			try {
//				while (mBufferedReader.ready())
//				{
//					xmlAsString += mBufferedReader.readLine();
//				}
//			} catch (IOException e) 
//			{
//				e.printStackTrace();
//			}
//		}
//		
//		return xmlAsString;
	}
	
	@Override
	public String getStatus(String statusString)
	{
		String outputStatus = "";
		switch (statusString) {
		<xsl:for-each select="peripheral/statuses/status">
		case "<xsl:value-of select="name" />":
			outputStatus = get<xsl:value-of select="translate(name,' ','')" />Status();
			break;
		</xsl:for-each>

		default:
			break;
		}
		
		
		return outputStatus;			
	}
	
	@Override
	public void doAction(String action, String...arguments) 
	{
		int actionIndex = actionStrings.length;
					
		// linear search for the index of action
		for (int index = 0; index &lt; actionStrings.length; index++)
		{
			if (actionStrings[index].equals(action))
			{
				actionIndex = index;
				break;
			}
		}
		
		<xsl:for-each select="peripheral/actions/action">
		if (<xsl:value-of select="@id" /> == actionIndex)
		{
			<xsl:for-each select="function/argument">
			String actionArgument<xsl:value-of select="position() - 1" /> = arguments[<xsl:value-of select="position() - 1" />];
			</xsl:for-each>
			<xsl:value-of select="function/@id" />Action(<xsl:for-each select="function/argument">actionArgument<xsl:value-of select="position() - 1" /><xsl:if test="following-sibling::argument">, </xsl:if></xsl:for-each>);
		} <xsl:if test="following-sibling::action">else</xsl:if>
		</xsl:for-each>	
		
		
		
	}
	
	/**
	 * Call this function to set the flag for a particular trigger. This
	 * means that actions that rely on this trigger in the service manager
	 * will be eligible to be called.
	 * @param triggerID the constant representing the trigger. One of: <xsl:value-of select="$triggerIDs"/>
	 * @param arguments the ',' separated &lt;code>String&lt;/code> arguments. Since this parameter 
	 * is a &lt;code>varargs&lt;/code>, it is possible to simply call 
	 * &lt;p>&lt;code>didTrigger("someString")&lt;/code>&lt;/p> &lt;p>if you are sending a trigger
	 * that has no arguments. 
	 */
	public void didTrigger(int triggerID, String...arguments) 
	{
		switch (triggerID) {
		<xsl:for-each select="peripheral/triggers/trigger">
		case <xsl:value-of select="@id" />:
			if (DEBUG) 
			{
				System.out.println("<xsl:value-of select="name" />-------");
			}						
			delegate.triggerOccurred(true, mPeripheral, triggerStrings[triggerID], arguments);
			break;
		</xsl:for-each>		
		default:
			break;
		}
	}
	
	/**
	 * Call this function to clear the flag for a particular trigger. This
	 * means that actions that rely on this trigger in the service manager
	 * will no longer be eligible to be called.
	 * @param triggerID the constant representing the trigger. One of:<xsl:value-of select="$triggerIDs"/> 
	 * @param arguments the ',' separated &lt;code>String&lt;/code> arguments. Since this parameter 
	 * is a &lt;code>varargs&lt;/code>, it is possible to simply call 
	 * &lt;p>&lt;code>didTrigger("someString")&lt;/code>&lt;/p> &lt;p>if you are sending a trigger&lt;/p> 
	 * that has no arguments. 
	 */
	public void cancelTrigger(int triggerID, String...arguments)
	{
		switch (triggerID) {
		<xsl:for-each select="peripheral/triggers/trigger">
		case <xsl:value-of select="@id" />:
			if (DEBUG) 
			{
				System.out.println("cancel ------<xsl:value-of select="name" />-------");
			}						
			delegate.triggerOccurred(false, mPeripheral, triggerStrings[triggerID], arguments);
			break;
		</xsl:for-each>		
		default:
			break;
		}
	}
	@Override
	public void setDelegate(TriggerHandler delegate)
	{
		this.delegate = delegate;		
	}
	
	@Override
	public void setPeripheral(Peripheral peripheral)
	{
		mPeripheral = peripheral;
	}
	
	@Override
	public boolean validateTrigger(String triggerID, String... arguments)
	{
		for (int index = 0; index &lt; triggerStrings.length; index++)
		{
			if (triggerStrings[index].equals(triggerID)) 
			{
				return validateTrigger(index, arguments); // delegate to peripheral descriptor
			}
		}
		
		return false; // triggerID does not match a trigger string.
	}
	
	@Override
	public boolean validateAction(String actionID, String... arguments)
	{
		for (int index = 0; index &lt; actionStrings.length; index++)
		{
			if (actionStrings[index].equals(actionID)) 
			{
				return validateAction(index, arguments); // delegate to peripheral descriptor
			}
		}
		
		return false; // actionID does not match an action string.
	}
	
	@Override
	public boolean handlePacket(byte[] data)
	{
	<xsl:if test="count(/peripheral/callback) &gt; 0">
		try 
		{
			byte[] parameters = new byte[0];
			if (data.length > 0)
			{
				Unpacker unpacker = null;
				
				// get the parameters from data
				if (data.length > 1) 
				{
					parameters = Arrays.copyOfRange(data, 1, data.length);
					ByteArrayInputStream in = new ByteArrayInputStream(parameters);
					MessagePack pack = new MessagePack();
					unpacker = pack.createUnpacker(in);
				}			
				<xsl:for-each select="peripheral/callback">
				if (<xsl:value-of select="upper-case(translate(@function-id,' ',''))" /> == data[0])
				{
					<xsl:choose><xsl:when test="count(argument) > 0">if (null != unpacker)
					{
						<xsl:for-each select="argument">
						<xsl:choose><xsl:when test="@type = 'Integer'">
						Integer someData<xsl:value-of select="position() - 1" /> = unpacker.readInt(); // parse parameters
						</xsl:when>
						<xsl:otherwise>
						<xsl:value-of select="@type"/><xsl:text> </xsl:text> someData<xsl:value-of select="position() - 1" /> = unpacker.read<xsl:value-of select="@type"/>(); // parse parameters
						</xsl:otherwise>
						</xsl:choose>
						</xsl:for-each>
						<xsl:value-of select="translate(@function-id,' ','')" />CallBack(<xsl:for-each select="argument">someData<xsl:value-of select="position() - 1" /><xsl:if test="following-sibling::argument">, </xsl:if></xsl:for-each>);
					}</xsl:when>
					<xsl:otherwise><xsl:value-of select="translate(@function-id,' ','')" />CallBack(); // No need to check the unpacker since this function accepts no arguments</xsl:otherwise>
					</xsl:choose>
					return true;
				} <xsl:if test="following-sibling::callback">else</xsl:if>
				</xsl:for-each>
				
			}
		} catch (IOException exception)
		{
			if (DEBUG) {
				System.out.println("Could not read parameters. not calling callback.");
			}
		}
		</xsl:if>
		return false;
	}

	/////////////////////
	// Action Functions 
	/////////////////////
	
	<xsl:for-each select="peripheral/actions/action">
	public abstract void <xsl:value-of select="function/@id" />Action(<xsl:for-each select="function/argument">String <xsl:value-of select="@id" /><xsl:if test="following-sibling::argument">, </xsl:if></xsl:for-each>);
	</xsl:for-each>	
	
	/////////////////////
	// Status Functions
	/////////////////////
	<xsl:for-each select="peripheral/statuses/status">
	/**
	 * @return a &lt;code>String&lt;/code> that describes the status field '<xsl:value-of select="name" />'.
	 */
	public abstract String get<xsl:value-of select="translate(name,' ','')" />Status();
	</xsl:for-each>
	
		
	/////////////////////
	// Remote functions
	/////////////////////
	<xsl:for-each select="peripheral/remote">
	/**
	 * Execute a function on the physical peripheral.
	 */
	public void <xsl:value-of select="translate(@function-id,' ','')" />Remote(<xsl:for-each select="argument"><xsl:value-of select="@type" /><xsl:text> </xsl:text> <xsl:value-of select="@id" /><xsl:if test="following-sibling::argument">, </xsl:if></xsl:for-each>)
	{
		int id = mPeripheral.getPeripheralID();
		
		// get a reference to the network we want to use
		Network mNetwork = mPeripheral.getNetwork();
		
		<xsl:if test="count(argument) &gt; 0">// package up the parameters
		MessagePack pack = new MessagePack();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Packer mPacker = pack.createPacker(outputStream);
	try { </xsl:if>
			<xsl:for-each select="argument">
			mPacker.write(<xsl:value-of select="@id" />);
			</xsl:for-each>
			<xsl:if test="count(argument) &gt; 0">byte[] params = outputStream.toByteArray();
			// build the byte array to send to the remote peripheral
			if (params.length > 255) 
			{
				throw new ArrayIndexOutOfBoundsException("too much data: cannot send a payload larger than 255 bytes.");
			}</xsl:if>
			
			int functionID = <xsl:value-of select="@id" />;
			//if (functionID &lt; 128 || functionID &gt; 255)
			//{
			//	throw new InvalidParameterException("functionID must be between 128 and 255. Please correct it in <xsl:value-of select="base-uri()" />.");
			//}
			<xsl:choose><xsl:when test="count(argument) > 0">
			byte[] remoteFunctionData = new byte[params.length + 1];
			remoteFunctionData[0] = (byte)functionID;
			System.arraycopy(params, 0, remoteFunctionData, 1, params.length);	
			
			// send it off! :)
			mNetwork.sendPacket(id, remoteFunctionData);</xsl:when>
			<xsl:otherwise>
			mNetwork.sendPacket(id, new byte[]{(byte)functionID});
			</xsl:otherwise></xsl:choose>
<xsl:if test="count(argument) &gt; 0">	} catch (IOException exception)
		{
			System.out.println("Could not output parameters. data not sent.");
		}</xsl:if>
	}
	<!--<xsl:if test="@blocking='false'">
	/**
	 * Callback for a function on the physical peripheral.
	 */
	public abstract void <xsl:value-of select="translate(@function-id,' ','')" />RemoteCallback(<xsl:for-each select="peripheral/remote/argument"><xsl:value-of select="@type" /> <xsl:value-of select="@id" /><xsl:if test="following-sibling::argument">, </xsl:if></xsl:for-each>);
	</xsl:if> -->
	</xsl:for-each>
	
	
	<xsl:for-each select="peripheral/callback">
	/**
	 * Received some data from the physical peripheral.
	 */
	public abstract void <xsl:value-of select="translate(@function-id,' ','')" />CallBack(<xsl:for-each select="argument"><xsl:value-of select="@type" /><xsl:text> </xsl:text> <xsl:value-of select="@id" /><xsl:if test="following-sibling::argument">, </xsl:if></xsl:for-each>);
	</xsl:for-each>
	
	/**
	 * Test that &lt;code>arguments&lt;/code> is a valid set of arguments for the action
	 * &lt;code>actionID&lt;/code>.
	 * @param actionID an &lt;code>int&lt;/code> representing one of: <xsl:value-of select="$actionIDs"/>
	 * @param arguments the ',' separated &lt;code>String&lt;/code> arguments that are being tested.
	 * @return &lt;code>true&lt;/code> if the combination of &lt;code>actionID&lt;/code> and 
	 * &lt;code>arguments&lt;/code> is valid, &lt;code>false&lt;/code> otherwise.
	 */
	protected abstract boolean validateAction(int actionID, String... arguments);
	
	/**
	 * Test that &lt;code>arguments&lt;/code> is a valid set of arguments for the trigger
	 * &lt;code>triggerID&lt;/code>. This method gets called when the user tries to add a
	 * new service, so it should be used to collect the parameters that the user specified.
	 * The parameter(s) will be represented by &lt;code>arguments&lt;/code>.
	 * @param triggerID an &lt;code>int&lt;/code> representing one of <xsl:value-of select="$triggerIDs"/>
	 * @param arguments the ',' separated &lt;code>String&lt;/code> arguments that are being tested.
	 * @return &lt;code>true&lt;/code> if the combination of &lt;code>triggerID&lt;/code> and 
	 * &lt;code>arguments&lt;/code> is valid, &lt;code>false&lt;/code> otherwise.
	 */
	protected abstract boolean validateTrigger(int triggerID, String... arguments);
   
}
    </xsl:result-document>
    
    
    <xsl:result-document href="arduino-{$packageName}/PeripheralGen.h">
#ifndef GEN
#define GEN


const unsigned char MAX_FUNCTIONS = 255;
/* Send data to the base station. assign something like Serial.write to this. */
void sendResult(const uint8_t * data, uint8_t len, uint8_t cmd);


/* FUNCTION IDs */
<xsl:for-each select="peripheral/remote">
const unsigned char <xsl:value-of select="upper-case(translate(@function-id,' ',''))" /> = <xsl:value-of select="@id"/>;
</xsl:for-each>

<xsl:for-each select="peripheral/callback">
const unsigned char <xsl:value-of select="upper-case(translate(@function-id,' ',''))" /> = <xsl:value-of select="@id"/>;
</xsl:for-each>

/** CALLBACKS AVAILABLE ON NEXUS **/
<xsl:for-each select="peripheral/callback">
void <xsl:value-of select="translate(@function-id,' ','')" />Nexus(<xsl:for-each select="argument"><xsl:apply-templates select="$types-top"><xsl:with-param name="type" select="@type"/></xsl:apply-templates><xsl:text> </xsl:text> <xsl:value-of select="@id" /><xsl:if test="following-sibling::argument">, </xsl:if></xsl:for-each>);
</xsl:for-each>


/** FUNCTIONS AVAILABLE ON PERIPHERAL **/
<xsl:for-each select="peripheral/remote">
extern void <xsl:value-of select="@function-id"/>(<xsl:for-each select="argument"><xsl:apply-templates select="$types-top"><xsl:with-param name="type" select="@type"/></xsl:apply-templates><xsl:text> </xsl:text><xsl:value-of select="@id" /><xsl:if test="following-sibling::argument">, </xsl:if></xsl:for-each>);
</xsl:for-each>

/** OTHER FUNCTIONS **/
void doFunction(unsigned char rpc_cmd, char *packet, int16_t packetLen);

#endif /* GEN */
    </xsl:result-document>
    
    <xsl:result-document href="arduino-{$packageName}/PeripheralGen.ino">
#include "PeripheralGen.h"
#include "msgpackalt.h"

<xsl:for-each select="peripheral/callback">
void <xsl:value-of select="translate(@function-id,' ','')" />Nexus(<xsl:for-each select="argument"><xsl:apply-templates select="$types-top"><xsl:with-param name="type" select="@type"/></xsl:apply-templates><xsl:text> </xsl:text> <xsl:value-of select="@id" /><xsl:if test="following-sibling::argument">, </xsl:if></xsl:for-each>)
{
  <xsl:choose><xsl:when test = "count(argument)>0">const uint8_t *buffer = NULL;
  uint32_t len;
  msgpack_p *p = msgpack_pack_init();
  
  <xsl:for-each select="argument">msgpack_pack_<xsl:apply-templates select="$funcs-top"><xsl:with-param name="func" select="@type"/></xsl:apply-templates>( p, <xsl:value-of select="@id" /> );
  </xsl:for-each>
  
  msgpack_get_buffer( p, &amp;buffer, &amp;len );
  sendResult(buffer, len, <xsl:value-of select="@id"/>);
  msgpack_pack_free( p );</xsl:when>
  <xsl:otherwise>sendResult(NULL, 0, <xsl:value-of select="@id"/>);</xsl:otherwise></xsl:choose>
}
</xsl:for-each>

void doFunction(unsigned char rpc_cmd, char *packet, int packetLen)
{
  if (rpc_cmd &lt;= MAX_FUNCTIONS)
  {
  <xsl:for-each select="peripheral/remote">
    if (<xsl:value-of select="upper-case(translate(@function-id,' ',''))" /> == rpc_cmd)
    {
      <xsl:if test="count(argument) > 0">msgpack_u *u;
      u = msgpack_unpack_init( packet, packetLen, 1 ); // could set to 0
      </xsl:if>
    
      <xsl:for-each select="argument">
      <xsl:choose>
      <xsl:when test="@type='String'">char arg<xsl:value-of select="position() - 1"/>[50];
      msgpack_unpack_str( u, arg<xsl:value-of select="position() - 1"/>, 50 );
      </xsl:when>
      <xsl:otherwise><xsl:apply-templates select="$types-top"><xsl:with-param name="type" select="@type"/></xsl:apply-templates> arg<xsl:value-of select="position() - 1"/>;
      msgpack_unpack_<xsl:apply-templates select="$funcs-top"><xsl:with-param name="func" select="@type"/></xsl:apply-templates>( u, &amp;arg<xsl:value-of select="position() - 1"/> );
      </xsl:otherwise>
      </xsl:choose>
      </xsl:for-each>
     
      <xsl:value-of select="@function-id"/>(<xsl:for-each select="argument">arg<xsl:value-of select="position() - 1" /><xsl:if test="following-sibling::argument">, </xsl:if></xsl:for-each>);
      <xsl:if test="count(argument) > 0">msgpack_unpack_free( u );</xsl:if>
  	}
    <xsl:if test="following-sibling::remote">else</xsl:if>
    </xsl:for-each>
  }
}
    </xsl:result-document>
    
    
    
    </xsl:template>
    <xsl:template match="s:types">
	  <xsl:param name="type"/>
	  <xsl:value-of select="key('type-lookup', $type)/s:name"/>
	</xsl:template>
    
    <xsl:template match="s:funcs">
	  <xsl:param name="func"/>
	  <xsl:value-of select="key('func-lookup', $func)/s:name"/>
	</xsl:template>
    
    <s:types>
	 <s:type><s:abbr>String</s:abbr><s:name>char *</s:name><s:msgpackfunc>str</s:msgpackfunc></s:type>
	 <s:type><s:abbr>Long</s:abbr><s:name>int64_t</s:name><s:msgpackfunc>int64</s:msgpackfunc></s:type>
	 <s:type><s:abbr>Integer</s:abbr><s:name>int32_t</s:name><s:msgpackfunc>int32</s:msgpackfunc></s:type>
	 <s:type><s:abbr>Short</s:abbr><s:name>int16_t</s:name><s:msgpackfunc>int16</s:msgpackfunc></s:type>
	 <s:type><s:abbr>Byte</s:abbr><s:name>int8_t</s:name><s:msgpackfunc>byte</s:msgpackfunc></s:type>
	 <s:type><s:abbr>Char</s:abbr><s:name>char</s:name><s:msgpackfunc>char</s:msgpackfunc></s:type>
	 <s:type><s:abbr>Float</s:abbr><s:name>float</s:name><s:msgpackfunc>float</s:msgpackfunc></s:type>
	 <s:type><s:abbr>Boolean</s:abbr><s:name>int8_t</s:name><s:msgpackfunc>int</s:msgpackfunc></s:type>
	</s:types>
	
	<s:funcs>
	 <s:func><s:abbr>String</s:abbr><s:name>str</s:name></s:func>
	 <s:func><s:abbr>Long</s:abbr><s:name>int64</s:name></s:func>
	 <s:func><s:abbr>Integer</s:abbr><s:name>int32</s:name></s:func>
	 <s:func><s:abbr>Short</s:abbr><s:name>int16</s:name></s:func>
	 <s:func><s:abbr>Byte</s:abbr><s:name>int8</s:name></s:func>
	 <s:func><s:abbr>Char</s:abbr><s:name>char</s:name></s:func>
	 <s:func><s:abbr>Float</s:abbr><s:name>float</s:name></s:func>
	 <s:func><s:abbr>Boolean</s:abbr><s:name>bool</s:name></s:func>
	</s:funcs>
</xsl:stylesheet>