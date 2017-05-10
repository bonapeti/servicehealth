package servicehealth.jdbc;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import servicehealth.JMX;
import servicehealth.jdbc.MonitoredDataSource;

public class H2Test {

	private static JdbcConnectionPool nonExistingDatabase = null;
	private MonitoredDataSource nonExistingMonitoredDataSource = null;

	@BeforeClass
	public static void startH2() throws Exception {
		nonExistingDatabase = JdbcConnectionPool.create("jdbc:h2:mem:testdb;IFEXISTS=TRUE", "sa", "sa");
	}

	@AfterClass
	public static void stopH2() throws Exception {
		nonExistingDatabase.dispose();
	}

	@Before
	public void setup() throws Exception {
		nonExistingMonitoredDataSource = new MonitoredDataSource("H2Database", nonExistingDatabase);
		nonExistingMonitoredDataSource.init();
	}

	@After
	public void teardown() throws Exception {
		nonExistingMonitoredDataSource.destroy();
		nonExistingMonitoredDataSource = null;
	}

	@Test
	public void test() throws Exception {
		try {
			try (Connection connection = nonExistingMonitoredDataSource.getConnection()) {
				try (PreparedStatement ps = connection.prepareStatement("select")) {
					ps.executeQuery();
				}
			}
			
		} catch (Exception e) {
			assertEquals("Database \"mem:testdb\" not found [90013-195]", JMX.errorMessage("H2Database"));

		}
	}

}
