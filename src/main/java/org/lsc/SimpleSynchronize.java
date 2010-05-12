/*
 ****************************************************************************
 * Ldap Synchronization Connector provides tools to synchronize
 * electronic identities from a list of data sources including
 * any database with a JDBC connector, another LDAP directory,
 * flat files...
 *
 *                  ==LICENSE NOTICE==
 * 
 * Copyright (c) 2008, LSC Project 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:

 *    * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of the LSC Project nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *                  ==LICENSE NOTICE==
 *
 *               (c) 2008 - 2009 LSC Project
 *         Sebastien Bahloul <seb@lsc-project.org>
 *         Thomas Chemineau <thomas@lsc-project.org>
 *         Jonathan Clarke <jon@lsc-project.org>
 *         Remy-Christophe Schermesser <rcs@lsc-project.org>
 ****************************************************************************
 */
package org.lsc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.lang.ArrayUtils;
import org.lsc.jndi.IJndiWritableService;
import org.lsc.service.IAsynchronousService;
import org.lsc.service.IService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extends AbstractSynchronize to instantiate a simple synchronization engine
 * This class is responsible for reading LSC properties and using specified classes
 * and objects to avoid implementing each every time. You may want to override
 * this class to implement your own way of synchronizing - but you also need
 * to rewrite the org.lsc.Launcher class.
 * 
 * @author Sebastien Bahloul &lt;seb@lsc-project.org&gt;
 */
public class SimpleSynchronize extends AbstractSynchronize {

	/** the magic keyword for all synchronization. */
	public static final String ALL_TASKS_KEYWORD = "all";

	/** lsc prefix. */
	public static final String LSC_PROPS_PREFIX = "lsc";

	/** lsc.tasks property. */
	public static final String TASKS_PROPS_PREFIX = "tasks";

	/** lsc.tasks.TASKNAME.srcService property. */
	public static final String SRCSERVICE_PROPS_PREFIX = "srcService";

	/** lsc.tasks.TASKNAME.dstService property. */
	public static final String DSTSERVICE_PROPS_PREFIX = "dstService";

	/** lsc.tasks.TASKNAME.customLibrary property. */
	public static final String CUSTOMLIBRARY_PROPS_PREFIX = "customLibrary";

	/** lsc.tasks.TASKNAME.object property. */
	public static final String OBJECT_PROPS_PREFIX = "object";

	/** lsc.tasks.TASKNAME.bean property. */
	public static final String BEAN_PROPS_PREFIX = "bean";

	/** lsc.tasks.TASKNAME.bean property. */
	public static final String ASYNCHRONOUS_PROPS = "async";

	/** lsc.tasks.TASKNAME.postSyncHook property. */
	public static final String POST_SYNC_HOOK_PROPS_PREFIX = "postSyncHook";
	
	/** lsc.tasks.TASKNAME.postCleanHook property. */
	public static final String POST_CLEAN_HOOK_PROPS_PREFIX = "postCleanHook";
	    
	private static final Logger LOGGER =
					LoggerFactory.getLogger(SimpleSynchronize.class);

	/** The lsc properties. */
	private Properties lscProperties;
	
	/**
	 * Default constructor
	 */
	public SimpleSynchronize() {
		super();
		setThreads(5); 
	}

	/**
	 * Main method Check properties, and for each task, launch the
	 * synchronization and the cleaning phases.
	 *
	 * @param syncTasks
	 *                string list of the synchronization tasks to launch
	 * @param cleanTasks
	 *                string list of the cleaning tasks to launch
	 *
	 * @return the launch status - true if all tasks executed successfully, false if no tasks were executed or any failed
	 * @throws Exception
	 */
	public final boolean launch(final List<String> asyncTasks, final List<String> syncTasks,
					final List<String> cleanTasks) throws Exception {
		Boolean foundATask = false;

		// Get the list of defined tasks from LSC properties
		String tasks = getLscProperties().getProperty(TASKS_PROPS_PREFIX);
		if (tasks == null) {
			LOGGER.error("No tasks defined in LSC properties! Exiting ...");
			return false;
		}

		// Iterate on each task
		StringTokenizer tasksSt = new StringTokenizer(tasks, ",");
		boolean isASyncTaskAll = asyncTasks.contains(ALL_TASKS_KEYWORD);
		boolean isSyncTaskAll = syncTasks.contains(ALL_TASKS_KEYWORD);
		boolean isCleanTaskAll = cleanTasks.contains(ALL_TASKS_KEYWORD);

		while (tasksSt.hasMoreTokens()) {
			String taskName = tasksSt.nextToken();

			// Launch the task either if explicitly specified or if "all" magic keyword used
			if (isSyncTaskAll || syncTasks.contains(taskName)) {
				foundATask = true;

				if (!launchTask(taskName, TaskMode.sync)) {
					return false;
				} else {
					String syncHook = lscProperties.getProperty(TASKS_PROPS_PREFIX + "." + taskName + "." + POST_SYNC_HOOK_PROPS_PREFIX);
					if(syncHook != null && syncHook != "") {
						runPostHook(taskName, syncHook);
					}
				}
			}
			if (isCleanTaskAll || cleanTasks.contains(taskName)) {
				foundATask = true;

				if (!launchTask(taskName, TaskMode.clean)) {
					return false;
				} else {
					String cleanHook = lscProperties.getProperty(TASKS_PROPS_PREFIX + "." + taskName + "." + POST_CLEAN_HOOK_PROPS_PREFIX);
					if(cleanHook != null && cleanHook != "") {
						runPostHook(taskName, cleanHook);
					}
				}
			}
			if (isASyncTaskAll || asyncTasks.contains(taskName)) {
				foundATask = true;

				if(!launchTask(taskName, TaskMode.async)) {
					return false;
				}
			}
		}

		if (!foundATask) {
			LOGGER.error("No specified tasks could be launched! Check spelling and that they exist in the configuration file.");
			return false;
		}

		return true;
	}

	/**
	 * Enum for the type of mode
	 *
	 */
	private enum TaskMode {
		clean,
		sync,
		async;
	}

	private void checkTaskOldProperty(Properties props, String taskName, String propertyName, String message) {
		if (props.getProperty(TASKS_PROPS_PREFIX + "." + taskName + "." + propertyName) != null) {
			String errorMessage = "Deprecated value specified in task " + taskName + " for " + propertyName + "! Please read upgrade notes ! (" + message + ")";
			throw new RuntimeException(errorMessage);
		}
	}

	private String getTaskPropertyAndCheckNotNull(String taskName, Properties props, String propertyName) {
		String value = props.getProperty(TASKS_PROPS_PREFIX + "." + taskName + "." + propertyName);

		if (value == null) {
			String errorMessage = "No value specified in task " + taskName + " for " + propertyName + "! Aborting.";
			throw new RuntimeException(errorMessage);
		}

		return value;
	}

	/**
	 * Launch a task. Call this for once each task type and task mode.
	 *
	 * @param taskName
	 *                the task name (historically the LDAP object class name, but can be any string)
	 *  @param taskMode
	 *                the task mode (clean or sync)
	 *
	 * @return boolean true on success, false if an error occurred
	 * @throws Exception
	 */
	private boolean launchTask(final String taskName, final TaskMode taskMode) throws Exception {
		try {
			LOGGER.info("Starting {} for {}", taskMode.name(), taskName);

			String prefix = TASKS_PROPS_PREFIX + "." + taskName + ".";

			Properties lscProperties = getLscProperties();
			// Get all properties
			// TODO : nice error message if a class name is specified but doesn't exist
			checkTaskOldProperty(lscProperties, taskName, OBJECT_PROPS_PREFIX, "Please take a look at upgrade notes at http://lsc-project.org/wiki/documentation/1.2/upgrade-from-1.1");
			String beanClassName = getTaskPropertyAndCheckNotNull(taskName, lscProperties, BEAN_PROPS_PREFIX);
			String srcServiceClass = getTaskPropertyAndCheckNotNull(taskName, lscProperties, SRCSERVICE_PROPS_PREFIX);
			String dstServiceClass = getTaskPropertyAndCheckNotNull(taskName, lscProperties, DSTSERVICE_PROPS_PREFIX);

			// Instantiate the destination service from properties
			Properties dstServiceProperties = Configuration.getAsProperties(LSC_PROPS_PREFIX + "." + prefix + DSTSERVICE_PROPS_PREFIX);
			Constructor<?> constr = Class.forName(dstServiceClass).getConstructor(new Class[]{Properties.class, String.class});
			IJndiWritableService dstJndiService = (IJndiWritableService) constr.newInstance(new Object[]{dstServiceProperties, beanClassName});

			// Instantiate custom JavaScript library from properties
			String customLibraryName = lscProperties.getProperty(prefix + CUSTOMLIBRARY_PROPS_PREFIX);
			Object customLibrary = null;
			if (customLibraryName != null) {
				customLibrary = Class.forName(customLibraryName).newInstance();
			}

			// Instantiate source service and pass any properties
			IService srcService = null;
			Properties srcServiceProperties = Configuration.getAsProperties(LSC_PROPS_PREFIX + "." + prefix + SRCSERVICE_PROPS_PREFIX);
			try {
				Constructor<?> constrSrcService = Class.forName(srcServiceClass).getConstructor(new Class[]{Properties.class, String.class});
				srcService = (IService) constrSrcService.newInstance(new Object[]{srcServiceProperties, beanClassName});

			} catch (NoSuchMethodException e) {
				try {
					// backwards compatibility: if the source service doesn't take a beanClassName,
					// use just the properties as a parameter
					Constructor<?> constrSrcService = Class.forName(srcServiceClass).getConstructor(new Class[]{Properties.class});
					srcService = (IService) constrSrcService.newInstance(new Object[] {srcServiceProperties});
				} catch (NoSuchMethodException e1) {
					// backwards compatibility: if the source service doesn't take any properties or a beanClassName,
					// use the parameter less constructor
					Constructor<?> constrSrcService = Class.forName(srcServiceClass).getConstructor(new Class[]{});
					srcService = (IService) constrSrcService.newInstance();
				}
			}

			// Do the work!
			switch (taskMode) {
				case clean:
					clean2Ldap(taskName, srcService, dstJndiService);
					break;
				case sync:
					synchronize2Ldap(taskName, srcService, dstJndiService, customLibrary);
					break;
				case async:
					if(srcService instanceof IAsynchronousService) {
						startAsynchronousSynchronize2Ldap(taskName, (IAsynchronousService) srcService, dstJndiService, customLibrary);
					} else {
						LOGGER.error("Requested asynchronous source service does not implement IAsynchronousService ! (" + srcService.getClass().getName() + ")");
					}
				default:
					//Should not happen
					LOGGER.error("Unknown task mode type {}", taskMode.toString());
					return false;
			}

			// Manage exceptions
		} catch (Exception e) {
			Class<?>[] exceptionsCaught = {InstantiationException.class, IllegalAccessException.class,
				ClassNotFoundException.class, SecurityException.class, NoSuchMethodException.class,
				IllegalArgumentException.class, InvocationTargetException.class};

			if (ArrayUtils.contains(exceptionsCaught, e.getClass())) {
				String errorDetail;
				if (e instanceof InvocationTargetException && e.getCause() != null) {
					errorDetail = e.getCause().toString();
				} else {
					errorDetail = e.toString();
				}

				LOGGER.error("Error while launching task \"{}\". Please check your configuration! ({})", taskName, errorDetail);
				LOGGER.debug(e.toString(), e);
				return false;
			} else {
				throw e;
			}
		}

		return true;
	}
	
	public Properties getLscProperties() {
		if (lscProperties == null) {
			lscProperties = Configuration.getAsProperties(LSC_PROPS_PREFIX);
			if (lscProperties == null) {
				throw new RuntimeException("Unable to get LSC properties!");
			}
		}
		return lscProperties;
	}

	/**
	 * Invoke the hook method wether it's a postsync or postclean
	 * 
	 * @param taskName the task name
	 * @param servicePostHook the fully qualified name of the method to invoke
	 */
	private void runPostHook(String taskName, String servicePostHook) {
		if (servicePostHook != null && servicePostHook.length() > 0) {
			LOGGER.debug("Service Post Hook found: " + servicePostHook);
			String hookClass = servicePostHook.substring(0, servicePostHook
					.lastIndexOf('.'));
			String hookMethod = servicePostHook.substring(servicePostHook
					.lastIndexOf('.') + 1);

			LOGGER.debug("Hook Class: " + hookClass);
			LOGGER.debug("Hook Method: " + hookMethod);

			if (hookClass.length() > 0 && hookMethod.length() > 0) {
				try {
					Method hook = Class.forName(hookClass).getMethod(
							hookMethod, new Class[] {});

					hook.invoke(null, new Object[] {});
				} catch (ClassNotFoundException e) {
					LOGGER.error("Invalid Hook Class specified " + hookClass
							+ " for task " + taskName);
					LOGGER.debug(e.toString(), e);
				} catch (NoSuchMethodException e) {
					LOGGER.error("Invalid hook method " + hookMethod
							+ " specified for task " + taskName);
					LOGGER.debug(e.toString(), e);
				} catch (IllegalArgumentException e) {
					LOGGER.error("Invalid argument exception for hook method "
							+ hookClass + "." + hookMethod);
					LOGGER.debug(e.toString(), e);
				} catch (IllegalAccessException e) {
					LOGGER.error("Illegal access exception for hook method "
							+ hookClass + "." + hookMethod);
					LOGGER.debug(e.toString(), e);
				} catch (InvocationTargetException e) {
					LOGGER.error("Invocation target exception for hook method "
							+ hookClass + "." + hookMethod);
					LOGGER.debug(e.toString(), e);
				}
			}
		}
	}
}
