package de.sambalmueslie.herold.model.change;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.Map;

import org.junit.Test;

import de.sambalmueslie.herold.model.element.TestElement;

public class ElementChangeDetectorTest {

	@Test
	public void test() throws NoSuchFieldException, SecurityException {
		final ElementChangeDetector<TestElement> detector = new ElementChangeDetector<>(TestElement.class);
		final TestElement element = detector.setup(new TestElement(1, "Empty")).get();

		element.setContent("Hello World");

		final Map<Field, Object> changes = detector.getChangedFields(element);
		assertEquals(1, changes.size());
		final Field declaredField = TestElement.class.getDeclaredField("content");
		declaredField.setAccessible(true);

		assertEquals("Empty", changes.get(declaredField));

	}

}
