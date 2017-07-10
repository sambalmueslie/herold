package de.sambalmueslie.herold.model;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.sambalmueslie.herold.DataModel;
import de.sambalmueslie.herold.HeroldDataCenter;
import de.sambalmueslie.herold.exceptions.ReadAccessException;
import de.sambalmueslie.herold.exceptions.WriteAccessException;
import de.sambalmueslie.herold.model.element.RestrictedTestElement;

public class RestrictedDataCenterTest {

	@Before
	public void setup() {
		dataCenter = HeroldFactory.createDataCenter("writer");

		writer = dataCenter.createModel(RestrictedTestElement.class);
		reader = dataCenter.createModel(RestrictedTestElement.class, "reader");
	}

	@After
	public void teardown() {
		dataCenter.removeModel(RestrictedTestElement.class, writer);
		dataCenter.removeModel(RestrictedTestElement.class, reader);
	}

	@Test(expected = ReadAccessException.class)
	public void testReadGetAllException() {
		final RestrictedTestElement element = new RestrictedTestElement(1234, "Hello World");
		writer.add(element);
		writer.getAll();
	}

	@Test(expected = ReadAccessException.class)
	public void testReadGetException() {
		final RestrictedTestElement element = new RestrictedTestElement(1234, "Hello World");
		writer.add(element);

		writer.get(element.getId());
	}

	@Test(expected = ReadAccessException.class)
	public void testReadIsEmptyException() {
		final RestrictedTestElement element = new RestrictedTestElement(1234, "Hello World");
		writer.add(element);
		writer.isEmpty();
	}

	@Test(expected = ReadAccessException.class)
	public void testReadSizeException() {
		final RestrictedTestElement element = new RestrictedTestElement(1234, "Hello World");
		writer.add(element);
		writer.size();
	}

	@Test(expected = ReadAccessException.class)
	public void testReadStreamException() {
		final RestrictedTestElement element = new RestrictedTestElement(1234, "Hello World");
		writer.add(element);
		writer.stream();
	}

	@Test(expected = WriteAccessException.class)
	public void testWriteAddException() {
		final RestrictedTestElement element = new RestrictedTestElement(1234, "Hello World");
		reader.add(element);
	}

	@Test(expected = WriteAccessException.class)
	public void testWriteRemoveAllException() {
		reader.removeAll();
	}

	@Test(expected = WriteAccessException.class)
	public void testWriteRemoveByIdException() {
		final RestrictedTestElement element = new RestrictedTestElement(1234, "Hello World");
		reader.remove(element.getId());
	}

	@Test(expected = WriteAccessException.class)
	public void testWriteRemoveByRefException() {
		final RestrictedTestElement element = new RestrictedTestElement(1234, "Hello World");
		reader.remove(element);
	}

	@Test
	public void testWriteRestrictions() {
		final RestrictedTestElement element = new RestrictedTestElement(1234, "Hello World");
		writer.add(element);

		final Optional<RestrictedTestElement> result = reader.get(element.getId());
		assertEquals(element, result.get());
	}

	@Test(expected = WriteAccessException.class)
	public void testWriteUpdateException() {
		final RestrictedTestElement element = new RestrictedTestElement(1234, "Hello World");
		reader.update(element);
	}

	private HeroldDataCenter dataCenter;

	private DataModel<RestrictedTestElement> reader;

	private DataModel<RestrictedTestElement> writer;

}
