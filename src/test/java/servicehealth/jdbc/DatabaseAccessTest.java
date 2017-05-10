package servicehealth.jdbc;

import static org.junit.Assert.assertEquals;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLTransientException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import servicehealth.JMX;

public class DatabaseAccessTest {

	private static final String SQL_ERROR_MESSAGE = "SQLErrorMessage";
	
	private static final String DATABASE_NAME = "BusinessDatabase";
	private MonitoredDataSource monitoredDataSource = null;
	
	@Before
	public void setUp() throws Exception {
		monitoredDataSource = new MonitoredDataSource(DATABASE_NAME, new FailingDataSource(new SQLTransientException(SQL_ERROR_MESSAGE)));
		monitoredDataSource.init();
	}
	
	@After
	public void after() throws Exception {
		monitoredDataSource.destroy();
		monitoredDataSource = null;
	}
	
	
	

	@Test
	public void failingConnectionShouldShowErrorMessage() throws Exception {
		assertEquals("", JMX.errorMessage(DATABASE_NAME));		
		try {
			monitoredDataSource.getConnection();
		} catch (Exception e) {
			assertEquals(SQL_ERROR_MESSAGE, JMX.errorMessage(DATABASE_NAME));
		}
	}
	
	
}

class FailingDataSource implements DataSource {

	private final SQLTransientException failure;
	
	public FailingDataSource(SQLTransientException failure) {
		super();
		this.failure = failure;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

	@Override
	public void setLogWriter(PrintWriter arg0) throws SQLException {
	
	}

	@Override
	public void setLoginTimeout(int arg0) throws SQLException {
	}

	@Override
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		return null;
	}

	@Override
	public Connection getConnection() throws SQLException {
		throw failure;
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		throw failure;
	}
	
}