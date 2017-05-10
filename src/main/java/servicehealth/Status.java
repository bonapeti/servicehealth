package servicehealth;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import javax.management.ObjectName;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ObjectNameFactory;
import com.codahale.metrics.Timer;

/**
 * Status captures the healthiness of a service by measuring execution time  
 * and recording errors of a service call
 * - the caught exception's message is exposed in JMX bean "Message" attribute
 * - it maintains statistics about the execution time and exposese in "Timers" bean
 * 
 *
 */
public class Status implements StatusMBean {

	private static final String DEFAULT_EMPTY_VALUE = "";
	
	private String okMessage = DEFAULT_EMPTY_VALUE;
	private final AtomicReference<String> message = new AtomicReference<>(okMessage);
	
	private MetricRegistry metricRegistry = new MetricRegistry();
	
	
	private JmxReporter jmxReporter = JmxReporter
			.forRegistry(metricRegistry)
			.inDomain(JMX.DOMAIN_NAME)
			.createsObjectNamesWith(new StatusObjectNameFactory())
			.build();
	
	private final Timer timer;
	
	public Status() {
		timer = new Timer();
	}
	
	@Override
	public String getMessage() {
		return message.get();
	}
	
	/**
	 * Measures the time of execution and monitors errors of a service call.
	 * The service should be called in argument of type {@link java.util.concurrent.Callable#}.
	 * The class of monitored exception should passed as an argument.
	 * 
	 * @param callable
	 * @param errorClass
	 * @return
	 * @throws E
	 */
	public <R, E extends Exception> R execute(Callable<R> callable, Class<E> errorClass) throws E {
		try {
			Timer.Context timerContext = timer.time();
			R result = callable.call();
			ok();
			timerContext.stop();
			return result;
		} catch (Exception e) {
			error(e);
			if (errorClass.isInstance(e)) {
				throw errorClass.cast(e);
			}
			if (RuntimeException.class.isInstance(e)) {
				throw RuntimeException.class.cast(e);
			}
			throw new UnsupportedOperationException("Unexpected exception",e);
		}
	}
	
	private void ok() {
		message.lazySet(okMessage);
	}
	
	private void error(Throwable exception) {
		Throwable rootCause = rootCause(exception);
		if (rootCause.getMessage() != null) {
			message.lazySet(rootCause.getMessage());
		} else {
			message.lazySet(exception.getClass().getName());
		}
	}
	
	private Throwable rootCause(Throwable error) {
		Throwable previous = error;
		Throwable cause = previous.getCause();
		while (cause != null) {
			previous = cause;
			cause = previous.getCause();
		}
		return previous;
	}

	
	
	public String getOkMessage() {
		return okMessage;
	}

	public void setOkMessage(String okMessage) {
		this.okMessage = okMessage;
	}

	public void registerJMX(String name) {
		JMX.register("errors", name, this);
		metricRegistry.register(name, timer);
		jmxReporter.start();
	}

	public void unregisterJMX(String name) {
		jmxReporter.stop();
		metricRegistry.remove(name);
		JMX.unregister("errors",name);
	}
	
	
}

class StatusObjectNameFactory implements ObjectNameFactory {

	@Override
	public ObjectName createName(String type, String domain, String name) {
		return JMX.createName(type, name);
	}
	
}