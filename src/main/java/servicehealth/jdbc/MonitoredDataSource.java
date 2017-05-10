package servicehealth.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import javax.sql.DataSource;

import servicehealth.Status;

public class MonitoredDataSource implements DataSource {

	private final DataSource datasource;
	private final String name;

	private final Status status = new Status();
	
	public MonitoredDataSource(String name, DataSource datasource) {
		this.name = Objects.requireNonNull(name);
		this.datasource = Objects.requireNonNull(datasource);
	}

	public PrintWriter getLogWriter() throws SQLException {
		return datasource.getLogWriter();
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return datasource.unwrap(iface);
	}

	public void setLogWriter(PrintWriter out) throws SQLException {
		datasource.setLogWriter(out);
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return datasource.isWrapperFor(iface);
	}

	public Connection getConnection() throws SQLException {
		return status.execute(new Callable<Connection>() {

			@Override
			public Connection call() throws Exception {
				return datasource.getConnection();
			}
		}, SQLException.class);
	}

	public void setLoginTimeout(int seconds) throws SQLException {
		datasource.setLoginTimeout(seconds);
	}

	public Connection getConnection(String username, String password) throws SQLException {
		return status.execute(new Callable<Connection>() {

			@Override
			public Connection call() throws Exception {
				return datasource.getConnection(username, password);
			}
		}, SQLException.class);
	}

	public int getLoginTimeout() throws SQLException {
		return datasource.getLoginTimeout();
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return datasource.getParentLogger();
	}

	public void init()  {
		status.registerJMX(name);
		
	}

	public void destroy() {
		status.unregisterJMX(name);
	}
}


