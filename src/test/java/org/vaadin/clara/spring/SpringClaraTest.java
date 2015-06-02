package org.vaadin.clara.spring;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.vaadin.teemu.clara.Clara;
import org.vaadin.teemu.clara.inflater.LayoutInflaterException;

import com.vaadin.ui.Component;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SpringClaraTest.TestConfig.class })
public class SpringClaraTest {
	@Autowired
	private SpringClara springClara;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	public static class Controller {
	}

	@Test
	public void testShouldResolveAndAutowireComponentByType() {
		Component inflated = springClara.create("autowiredByType.xml",
				new Controller());
		TestComponent component = (TestComponent) Clara.findComponentById(
				inflated, "testComponent");
		assertNotNull(component);
		assertNotNull(component.getSomeBean());
	}

	@Test
	public void testShouldResolveAndAutowireComponentById() {
		Component inflated = springClara.create("autowiredById.xml",
				new Controller());
		OtherTestComponent component = (OtherTestComponent) Clara
				.findComponentById(inflated, "testComponent");
		assertSame(TestConfig.SPECIFIC_TEST_COMPONENT, component);
		assertNotNull(component.getSomeBean());
	}

	@Test
	public void testShouldThrowDueToMismatchBetweenDeclaredTypeAndBeanType() {
		thrown.expect(LayoutInflaterException.class);
		thrown.expectMessage("the component bean with id 'specificTestComponent' is not matching type "
				+ "'TestComponent' (actual type: org.vaadin.clara.spring.OtherTestComponent)");
		springClara.create("autowiredByIdWithWrongTypeOfNode.xml",
				new Controller());
	}

	@ComponentScan(basePackages = "org.vaadin.clara.spring")
	public static class TestConfig {
		private static final OtherTestComponent SPECIFIC_TEST_COMPONENT = new OtherTestComponent();

		@Bean
		public OtherTestComponent specificTestComponent() {
			return SPECIFIC_TEST_COMPONENT;
		}
	}

}