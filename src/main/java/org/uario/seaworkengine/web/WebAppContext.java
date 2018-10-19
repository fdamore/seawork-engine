package org.uario.seaworkengine.web;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

/**
 * Define App context... Application Lifecycle Listener implementation class
 * WebAppContext
 *
 */
public class WebAppContext implements ServletContextListener {

	private final Logger logger = Logger.getLogger(WebAppContext.class);

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(final ServletContextEvent arg0) {
		this.logger.info("Context destroyed");
		// deregister driver...
		final Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			final Driver driver = drivers.nextElement();
			try {
				DriverManager.deregisterDriver(driver);

			} catch (final SQLException e) {
				this.logger.error(e);

			}
		}
	}

	@Override
	public void contextInitialized(final ServletContextEvent arg0) {
		this.logger.info("Context Initialized");

		// set the logger system for ibatis
		// org.apache.ibatis.logging.LogFactory.useStdOutLogging();

	}

}
