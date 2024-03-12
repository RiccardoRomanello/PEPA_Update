/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;

public class PepatoOptionForwarder {

	/**
	 * Save the given Pepato option under this resource's persistent settings
	 * <p>
	 * All arguments must be not null
	 * 
	 * @param resource
	 *            the resource
	 * @param key
	 *            the Pepato Option
	 * @param value
	 *            the value
	 * @throws CoreException
	 */
	public static void saveOptionInPersistentResource(IResource resource,
			String key, Object value) throws CoreException {

		Assert.isNotNull(resource);
		Assert.isNotNull(key);
		Assert.isNotNull(value);

		resource.setPersistentProperty(new QualifiedName(PepaCore.ID, key), ""
				+ value);
	}

	/**
	 * Return the option map for the given resource. If resource-specific
	 * settings are not found, the option map is initialised with the plugin
	 * specific options.
	 * 
	 * @param resource
	 *            the given resource containing solver settings
	 * @return the option map
	 * @throws CoreException
	 */
	public static OptionMap getOptionMapFromPersistentResource(
			IResource resource) throws CoreException {
		Map<String, Object> map = new HashMap<String, Object>();
		for (String key : OptionMap.defaultKeys()) {
			String value = getOptionFromPersistentResource(resource, key);
			if (value == null)
				value = PepaCore.getDefault().getPluginPreferences().getString(
						key);
			map.put(key, forString(key, value));
		}
		return new OptionMap(map);

	}

	/**
	 * Arguments must all be not null
	 * 
	 * @param resource
	 * @param key
	 * @return the persisted string representing the option value
	 * @throws CoreException
	 */
	public static String getOptionFromPersistentResource(IResource resource,
			String key) throws CoreException {

		Assert.isNotNull(resource);
		Assert.isNotNull(key);

		String value = resource.getPersistentProperty(new QualifiedName(
				PepaCore.ID, key));
		return value;
	}

	private static Object forString(String key, String rawValue) {
		Object value = OptionMap.getDefaultValue(key);
		if (value instanceof Boolean)
			return Boolean.parseBoolean(rawValue);
		else if (value instanceof Float)
			return Float.parseFloat(rawValue);
		else if (value instanceof Double)
			return Double.parseDouble(rawValue);
		else if (value instanceof Integer)
			return Integer.parseInt(rawValue);
		else if (value instanceof String)
			return rawValue;
		else if (value instanceof Long)
			return Long.parseLong(rawValue);
		else
			return null;
	}
}
