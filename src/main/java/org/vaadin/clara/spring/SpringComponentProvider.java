/*
	Copyright 2015 Mark Koops
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	    http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.package org.vaadin.clara.spring;
 */

package org.vaadin.clara.spring;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.vaadin.teemu.clara.inflater.ComponentProvider;
import org.vaadin.teemu.clara.inflater.LayoutInflaterException;

import com.vaadin.ui.Component;

@org.springframework.stereotype.Component
public class SpringComponentProvider implements ComponentProvider {
	private static final String AUTOWIRED_GROUP_TYPE = "autowiredType";
	private static final String AUTOWIRED_GROUP_ID = "autowiredId";
	static final String AUTOWIRED_URN_PREFIX = "urn:autowired:";
	static final String BY_ID = "byId";
	static final String BY_TYPE = "byType";

	private static final Pattern AUTOWIRED_URN_PATTERN = Pattern.compile("^" + AUTOWIRED_URN_PREFIX
			+ "(?<" + AUTOWIRED_GROUP_TYPE + ">" + BY_ID + "|" + BY_TYPE + "):(?<"
			+ AUTOWIRED_GROUP_ID + ">.*)$");
	private final ApplicationContext applicationContext;

	@Autowired
	public SpringComponentProvider(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public boolean isApplicableFor(String uri, String localName, String id) {
		return AUTOWIRED_URN_PATTERN.matcher(uri).matches();
	}

	@Override
	public Component getComponent(String uri, String localName, String id)
			throws LayoutInflaterException {
		Matcher matcher = AUTOWIRED_URN_PATTERN.matcher(uri);
		if (!matcher.matches()) {
			return null;
		}

		if (BY_ID.equals(matcher.group(AUTOWIRED_GROUP_TYPE))) {
			return getComponentById(localName, matcher.group(AUTOWIRED_GROUP_ID));
		}

		return getComponentByType(localName, matcher.group(AUTOWIRED_GROUP_ID));
	}

	private Component getComponentById(String localName, String beanId) {
		Component bean = applicationContext.getBean(beanId, Component.class);
		Class<? extends Component> beanClass = bean.getClass();
		boolean beanClassSimpleMatchesLocalName = localName.equals(beanClass.getSimpleName());
		if (!beanClassSimpleMatchesLocalName) {
			throw new LayoutInflaterException("the component bean with id '" + beanId
					+ "' is not matching type '" + localName + "' (actual type: "
					+ beanClass.getName() + ")");
		}
		return bean;
	}

	private Component getComponentByType(String localName, String packageName) {
		return (Component)applicationContext.getBean(asBeanClass(localName, packageName));
	}

	private Class<?> asBeanClass(String localName, String packageName) {
		String qualifiedClassName = packageName + "." + localName;
		try {
			Class<?> beanClass = Class.forName(qualifiedClassName);
			assertBeanClassDerivesFromComponent(localName, beanClass);
			return beanClass;
		} catch (ClassNotFoundException e) {
			throw new LayoutInflaterException("failed to resolve the class '" + qualifiedClassName
					+ "' of component '" + localName + "'", e);
		}

	}

	private void assertBeanClassDerivesFromComponent(String localName, Class<?> beanClass) {
		if (!Component.class.isAssignableFrom(beanClass)) {
			throw new LayoutInflaterException("Class '" + beanClass.getName() + "' of component '"
					+ localName + "' does not derive from class Component.");
		}
	}
}
