package org.uario.seaworkengine.web;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.uario.seaworkengine.model.UserTask;
import org.uario.seaworkengine.platform.persistence.cache.ITaskCache;
import org.uario.seaworkengine.platform.persistence.dao.ConfigurationDAO;
import org.uario.seaworkengine.utility.BeansTag;

/**
 * Define App context... Application Lifecycle Listener implementation class
 * WebAppContext
 *
 */
public class WebAppContext implements ServletContextListener {

	private final Logger	logger	= Logger.getLogger(WebAppContext.class);

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

		// set platform caches
		final WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(arg0.getServletContext());
		final ConfigurationDAO configuration = (ConfigurationDAO) ctx.getBean(BeansTag.CONFIGURATION_DAO);
		final ITaskCache shift_cache = (ITaskCache) ctx.getBean(BeansTag.TASK_CACHE);
		final List<UserTask> list_task = configuration.loadTasks();
		shift_cache.buildCache(list_task);

	}

}
