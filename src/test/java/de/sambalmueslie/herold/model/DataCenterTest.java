package de.sambalmueslie.herold.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Collection;
import java.util.Optional;

import org.junit.Test;

import de.sambalmueslie.herold.DataModel;
import de.sambalmueslie.herold.DataModelChangeListener;
import de.sambalmueslie.herold.HeroldDataCenter;
import de.sambalmueslie.herold.model.element.TestElement;
import de.sambalmueslie.herold.model.element.TestElementInterface;

public class DataCenterTest {

	@Test
	public void testInterfaceInstance() {
		final HeroldDataCenter dataCenter = HeroldFactory.createDataCenter();
		final DataModel<TestElement> m1 = dataCenter.createModel(TestElement.class).get();
		final DataModel<TestElementInterface> m2 = dataCenter.createModel(TestElementInterface.class).get();

		final TestElement element = new TestElement(1234, "Hello World");
		m1.add(element);

		final Optional<TestElementInterface> result = m2.get(element.getId());

		assertTrue(result.isPresent());
		assertEquals(element.getId(), result.get().getId());
		assertEquals(element.getContent(), result.get().getContent());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMultipleModel() {
		final HeroldDataCenter dataCenter = HeroldFactory.createDataCenter();

		final DataModel<TestElement> sender = dataCenter.createModel(TestElement.class).get();
		final DataModel<TestElement> receiver = dataCenter.createModel(TestElement.class).get();

		final DataModelChangeListener<TestElement> listener = mock(DataModelChangeListener.class);
		receiver.register(listener);

		final TestElement element = new TestElement(1234, "Hello World");
		sender.add(element);

		verify(listener).handleElementAdded(element);
		assertEquals(1, receiver.size());
		assertEquals(1, sender.size());

		element.setContent("Hello all");
		sender.update(element);

		verify(listener).handleElementChanged(element);
		assertEquals(1, receiver.size());
		assertEquals(1, sender.size());

		sender.remove(element);

		verify(listener).handleElementRemoved(element);
		assertEquals(0, receiver.size());
		assertEquals(0, sender.size());

		dataCenter.removeModel(TestElement.class, receiver);

		sender.add(element);
		assertEquals(1, sender.size());

		final DataModel<TestElement> newReceiver = dataCenter.createModel(TestElement.class).get();
		assertEquals(1, newReceiver.size());
	}

	@Test
	public void testSingleEmptyModel() {
		final HeroldDataCenter dataCenter = HeroldFactory.createDataCenter();
		final DataModel<TestElement> model = dataCenter.createModel(TestElement.class).get();

		assertEquals(0, model.size());
		assertTrue(model.isEmpty());

		final Collection<TestElement> elements = model.getAll();
		assertNotNull(elements);
		assertEquals(0, elements.size());
		assertTrue(elements.isEmpty());

		dataCenter.removeAllModel();

	}

	@Test
	public void testSingleInstance() {
		final HeroldDataCenter dataCenter = HeroldFactory.createDataCenter();

		final DataModel<TestElement> model = dataCenter.createModel(TestElement.class).get();

		final TestElement e1 = new TestElement(1234, "Hello World");
		model.add(e1);
		final TestElement e2 = new TestElement(4711, "Hello Earth");
		model.update(e2);

		assertEquals(2, model.size());
		assertFalse(model.isEmpty());

		Optional<TestElement> result = model.get(e1.getId());
		assertTrue(result.isPresent());
		assertEquals(e1, result.get());

		final Collection<TestElement> elements = model.getAll();
		assertEquals(2, elements.size());

		assertTrue(model.contains(e1.getId()));

		e1.setContent("Hello All");
		model.add(e1);

		result = model.get(e1.getId());
		assertTrue(result.isPresent());
		assertEquals(e1.getId(), result.get().getId());
		assertEquals(e1.getContent(), result.get().getContent());

		model.remove(e1);

		assertEquals(1, model.size());
		assertFalse(model.isEmpty());
		result = model.get(e1.getId());
		assertFalse(result.isPresent());

		model.remove(e2.getId());

		assertEquals(0, model.size());
		result = model.get(e2.getId());
		assertFalse(result.isPresent());

		model.add(new TestElement(1, ""));
		model.add(new TestElement(2, ""));
		model.add(new TestElement(3, ""));
		assertEquals(3, model.size());

		model.removeAll();
		assertEquals(0, model.stream().count());

		dataCenter.removeAllModel(TestElement.class);

	}

	@Test
	public void testSingleInvalidParameter() {
		final HeroldDataCenter dataCenter = HeroldFactory.createDataCenter();

		final DataModel<TestElement> model = dataCenter.createModel(TestElement.class).get();

		model.register(null);
		model.unregister(null);
		model.add(null);
		model.update(null);
		model.remove(null);
	}

}
