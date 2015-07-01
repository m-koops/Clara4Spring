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

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.vaadin.teemu.clara.Clara;
import org.vaadin.teemu.clara.binder.BinderException;
import org.vaadin.teemu.clara.inflater.ComponentProvider;
import org.vaadin.teemu.clara.inflater.LayoutInflaterException;
import org.vaadin.teemu.clara.inflater.filter.AttributeFilter;

import com.vaadin.ui.Component;

/**
 * Inspired by {@link Clara}, with the extension that this Class is also able to
 * resolve (autowire) components from a Spring application context.
 */
@org.springframework.stereotype.Component
public class SpringClara {
	private final ApplicationContext applicationContext;

	@Autowired
	public SpringClara(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * Returns a {@link Component} that is read from the XML representation
	 * given as {@link InputStream}. If you would like to bind the resulting
	 * {@link Component} to a controller object, you should use
	 * {@link #create(InputStream, Object, AttributeFilter...)} method instead.
	 * 
	 * @param xml
	 *            XML representation.
	 * @return a {@link Component} that is read from the XML representation.
	 */
	public Component create(InputStream xml) {
		return create(xml, null);
	}

	/**
	 * Returns a {@link Component} that is read from an XML file in the
	 * classpath and binds the resulting {@link Component} to the given
	 * {@code controller} object.
	 * 
	 * <br>
	 * <br>
	 * The filename is given either as a path relative to the class of the
	 * {@code controller} object or as an absolute path. For example if you have
	 * a {@code MyController.java} and {@code MyController.xml} files in the
	 * same package, you can call this method like
	 * {@code Clara.create("MyController.xml", new MyController())}.
	 * 
	 * <br>
	 * <br>
	 * Optionally you may also provide {@link AttributeFilter}s to do some
	 * modifications (or example localized translations) to any attributes
	 * present in the XML representation.
	 * 
	 * @param xmlClassResourceFileName
	 *            filename of the XML representation (within classpath, relative
	 *            to {@code controller}'s class or absolute path).
	 * @param controller
	 *            controller object to bind the resulting {@code Component}
	 *            (non-{@code null} ).
	 * @return a {@link Component} that is read from the XML representation and
	 *         bound to the given {@code controller}.
	 * 
	 * @throws LayoutInflaterException
	 *             if an error is encountered during the layout inflation.
	 * @throws BinderException
	 *             if an error is encountered during the binding.
	 */
	public Component create(String xmlClassResourceFileName, Object controller) {
		InputStream xml = controller.getClass().getResourceAsStream(
				xmlClassResourceFileName);
		return create(xml, controller);
	}

	/**
	 * Returns a {@link Component} that is read from the XML representation
	 * given as {@link InputStream} and binds the resulting {@link Component} to
	 * the given {@code controller} object.
	 * 
	 * <br>
	 * <br>
	 * Optionally you may also provide {@link AttributeFilter}s to do some
	 * modifications (or example localized translations) to any attributes
	 * present in the XML representation.
	 * 
	 * @param xml
	 *            XML representation.
	 * @param controller
	 *            controller object to bind the resulting {@code Component} (
	 *            {@code null} allowed).
	 * @return a {@link Component} that is read from the XML representation and
	 *         bound to the given {@code controller}.
	 * 
	 * @throws LayoutInflaterException
	 *             if an error is encountered during the layout inflation.
	 * @throws BinderException
	 *             if an error is encountered during the binding.
	 */
	public Component create(InputStream xml, Object controller) {
		Map<String, AttributeFilter> attributeFilterBeans = applicationContext
				.getBeansOfType(AttributeFilter.class);
		AttributeFilter[] attributeFilters = attributeFilterBeans.values()
				.toArray(new AttributeFilter[attributeFilterBeans.size()]);
		ComponentProvider springComponentProvider = (ComponentProvider) applicationContext
				.getBean(SpringComponentProvider.class);
		return Clara.create(xml, controller,
				Collections.singletonList(springComponentProvider),
				attributeFilters);
	}
}
