package com.awizen.gangehi.test;

import static org.jbpm.test.JBPMHelper.txStateName;

import java.sql.SQLException;

import javax.transaction.Status;
import javax.transaction.Transaction;

import org.h2.tools.DeleteDbFiles;
import org.h2.tools.Server;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.After;
import org.junit.Before;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

import com.awizen.gangehi.util.MyUserGroupCallback;

public class MyJbpmJUnitBaseTestCase extends JbpmJUnitBaseTestCase {

	private H2Server server = new H2Server();

	public MyJbpmJUnitBaseTestCase(boolean arg0, boolean arg1) {
		super(arg0, arg1);
		userGroupCallback = new MyUserGroupCallback();
	}

	@Override
	@Before
	public void setUp() throws Exception{
		server.start();
		super.setUp();
	}

	@Override
	protected PoolingDataSource setupPoolingDataSource() {
		PoolingDataSource pds = new PoolingDataSource();
		pds.setUniqueName("jdbc/jbpm-ds");
		pds.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");
		pds.setMaxPoolSize(5);
		pds.setAllowLocalTransactions(true);
		pds.getDriverProperties().put("user", "sa");
		pds.getDriverProperties().put("password", "");
		//		pds.getDriverProperties().put("url", "jdbc:h2:tcp://localhost/~/jbpm-db;MVCC=true");
		pds.getDriverProperties().put("url", "jdbc:h2:tcp://localhost/~/jbpm-db");
		pds.getDriverProperties().put("driverClassName", "org.h2.Driver");
		pds.init();
		return pds;
	}

	private static class H2Server {

		private Server server;

		public synchronized void start() {
			if (server == null || !server.isRunning(false)) {
				try {
					DeleteDbFiles.execute("~", "jbpm-db", true);
					server = Server.createTcpServer(new String[0]);
					server.start();
				} catch (SQLException e) {
					throw new RuntimeException("Cannot start h2 server database", e);
				}
			}
		}

		public void stop() {
			if (server != null) {
				server.stop();
				server.shutdown();
				DeleteDbFiles.execute("~", "jbpm-db", true);
				server = null;
			}
		}
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();

		// Clean up possible transactions
		Transaction tx = TransactionManagerServices.getTransactionManager().getCurrentTransaction();
		if (tx != null) {
			int testTxState = tx.getStatus();
			if (testTxState != Status.STATUS_NO_TRANSACTION
					&& testTxState != Status.STATUS_ROLLEDBACK
					&& testTxState != Status.STATUS_COMMITTED) {
				try {
					tx.rollback();
				} catch (Throwable t) {
					// do nothing..
				}
				fail("Transaction had status " + txStateName[testTxState] + " at the end of the test.");
			}
		}
		server.stop();
	}

}
