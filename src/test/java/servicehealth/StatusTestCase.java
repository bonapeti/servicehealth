package servicehealth;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StatusTestCase {
	
	private static final String ERROR_MESSAGE = "Test SQL";
	private Status status;

	@Before
	public void create() throws Exception {
		status = new Status();
	}

	@After
	public void destroy() throws Exception {
		status = null;
	}

	@Test
	public void noError() {
		status.execute(new Callable<String>() {

			@Override
			public String call() throws Exception {
				return "boo";
			}
		}, RuntimeException.class);
		assertEquals("", status.getMessage());

	}

	@Test
	public void catchCheckedExpectedException() {
		try {

			status.execute(new Callable<String>() {

				@Override
				public String call() throws Exception {
					throwSQLException(ERROR_MESSAGE);
					return null;
				}
			}, SQLException.class);
			fail("Should have thrown SQLException");
		} catch (SQLException e) {
			assertEquals(ERROR_MESSAGE, e.getMessage());
			assertEquals(ERROR_MESSAGE, status.getMessage());
		}

	}
	
	
	
	public void throwSQLException(String message) throws SQLException {
		throw new SQLException(message);
	}
	
	public void throwRuntimeException(String message) {
		throw new RuntimeException(message);
	}
	
	@Test
	public void catchExpectedRuntimeExceptionute() {
		try {
			status.execute(new Callable<String>() {

				@Override
				public String call() throws Exception {
					throwRuntimeException(ERROR_MESSAGE);
					return null;
				}
			}, RuntimeException.class);
			fail("Should have thrown RuntimeException");
		} catch (RuntimeException e) {
			assertEquals(ERROR_MESSAGE, e.getMessage());
			assertEquals(ERROR_MESSAGE, status.getMessage());
		}

	}

	@Test
	public void catchUnexpectedRuntimeException() {
		try {

			status.execute(new Callable<String>() {

				@Override
				public String call() throws Exception {
					throwRuntimeException(ERROR_MESSAGE);
					return null;
				}
			}, SQLException.class);
			fail("Should have thrown SQLException");
		} catch (Exception e) {
			assertTrue(RuntimeException.class.isInstance(e));
			assertEquals(ERROR_MESSAGE, e.getMessage());
			assertEquals(ERROR_MESSAGE, status.getMessage());
		}

	}
}
