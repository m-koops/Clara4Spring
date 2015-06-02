package org.vaadin.clara.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.ui.CustomComponent;

@Component
@Scope (BeanDefinition.SCOPE_PROTOTYPE)
public class OtherTestComponent extends CustomComponent {
	private static final long serialVersionUID = -1412782741143839197L;
	
	@Autowired
	private SomeBean someBean;

	public SomeBean getSomeBean() {
		return someBean;
	}
}
