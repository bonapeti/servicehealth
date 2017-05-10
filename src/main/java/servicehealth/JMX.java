package servicehealth;

import java.lang.management.ManagementFactory;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class JMX {

	public static String DOMAIN_NAME = "bonapeti.status";
	
	
	private JMX() {};
	
	public static ObjectName createName(String type, String name) {
		try {
			return new ObjectName(DOMAIN_NAME + ":type=" + type + ",name=" + name);
		} catch (MalformedObjectNameException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public static void register(String type, String name, Object object) {
		try {
			ManagementFactory.getPlatformMBeanServer().registerMBean(object, createName(type, name));
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		} 
	}
	
	
	
	public static boolean isRegistered(String type, String name) {
		try {
			return ManagementFactory.getPlatformMBeanServer().isRegistered(createName(type, name));
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		} 
	}
	
	public static boolean isRegistered(String name) {
		try {
			return ManagementFactory.getPlatformMBeanServer().isRegistered(new ObjectName(name));
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		} 
	}
	
	public static void unregister(String type, String name) {
		try {
			ManagementFactory.getPlatformMBeanServer().unregisterMBean(createName(type, name));
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		} 
	}
	
	public static void unregister(String name) {
		try {
			ManagementFactory.getPlatformMBeanServer().unregisterMBean(new ObjectName(name));
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		} 
	}
	
	public static Object attributeValue(String objectName, String attributeName) {
		try {
			return ManagementFactory.getPlatformMBeanServer().getAttribute(new ObjectName(objectName), attributeName);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		} 
	}
	
	public static String errorMessage(String name) {
		return (String)attributeValue(DOMAIN_NAME + ":type=errors,name=" + name, "Message");
	}
}
