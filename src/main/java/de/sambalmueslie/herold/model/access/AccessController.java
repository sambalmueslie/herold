package de.sambalmueslie.herold.model.access;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.sambalmueslie.herold.DataModelElement;
import de.sambalmueslie.herold.annotations.AllowedReader;
import de.sambalmueslie.herold.annotations.AllowedWriter;
import de.sambalmueslie.herold.exceptions.ReadAccessException;
import de.sambalmueslie.herold.exceptions.WriteAccessException;
import de.sambalmueslie.herold.model.LocalModel;
import de.sambalmueslie.herold.model.LocalModelFilter;
import de.sambalmueslie.herold.util.AnnotationSpy;

public class AccessController<T extends DataModelElement> extends LocalModelFilter<T> {

	public AccessController(String operatorId, LocalModel<T> model) {
		super(model);
		this.operatorId = operatorId;

		setup();
	}

	@Override
	public void add(long instanceId, T element) {
		if (isWriteRestricted())
			throw new WriteAccessException(operatorId, getElementType(), "Cannot add element");

		getNext().add(instanceId, element);
	}

	@Override
	public boolean contains(long elementId) {
		if (isReadRestricted()) throw new ReadAccessException(operatorId, getElementType(), "Cannot read");
		return getNext().contains(elementId);
	}

	@Override
	public void dispose() {
		allowedReaders.clear();
		allowedWriters.clear();
		getNext().dispose();
	}

	@Override
	public Optional<T> get(long elementId) {
		if (isReadRestricted()) throw new ReadAccessException(operatorId, getElementType(), "Cannot read");
		return getNext().get(elementId);
	}

	@Override
	public Collection<T> getAll() {
		if (isReadRestricted()) throw new ReadAccessException(operatorId, getElementType(), "Cannot read");
		return getNext().getAll();
	}

	@Override
	public boolean isEmpty() {
		if (isReadRestricted()) throw new ReadAccessException(operatorId, getElementType(), "Cannot read");
		return getNext().isEmpty();
	}

	@Override
	public void remove(long instanceId, long elementId) {
		if (isWriteRestricted())
			throw new WriteAccessException(operatorId, getElementType(), "Cannot remove element");

		getNext().remove(instanceId, elementId);
	}

	@Override
	public void remove(long instanceId, T element) {
		if (isWriteRestricted())
			throw new WriteAccessException(operatorId, getElementType(), "Cannot remove element");

		getNext().remove(instanceId, element);
	}

	@Override
	public void removeAll(long instanceId) {
		if (isWriteRestricted())
			throw new WriteAccessException(operatorId, getElementType(), "Cannot remove all elements");

		getNext().removeAll(instanceId);
	}

	@Override
	public int size() {
		if (isReadRestricted()) throw new ReadAccessException(operatorId, getElementType(), "Cannot read");
		return getNext().size();
	}

	@Override
	public Stream<T> stream() {
		if (isReadRestricted()) throw new ReadAccessException(operatorId, getElementType(), "Cannot read");
		return getNext().stream();
	}

	@Override
	public void update(long instanceId, T element) {
		if (isWriteRestricted())
			throw new WriteAccessException(operatorId, getElementType(), "Cannot update element");

		getNext().update(instanceId, element);
	}

	Class<T> getElementType() {
		return getMetadata().getElementType();
	}

	private boolean isReadRestricted() {
		if (operatorId == null || operatorId.isEmpty()) return false;
		if (allowedReaders == null || allowedReaders.isEmpty()) return false;

		return !allowedReaders.contains(operatorId);
	}

	private boolean isWriteRestricted() {
		if (operatorId == null || operatorId.isEmpty()) return false;
		if (allowedWriters == null || allowedWriters.isEmpty()) return false;
		return !allowedWriters.contains(operatorId);
	}

	private void setup() {
		final Optional<AllowedReader> reader = AnnotationSpy.findAnnotation(getElementType(), AllowedReader.class);
		if (reader.isPresent()) {
			allowedReaders = Arrays.stream(reader.get().value()).collect(Collectors.toSet());
		}

		final Optional<AllowedWriter> writer = AnnotationSpy.findAnnotation(getElementType(), AllowedWriter.class);
		if (writer.isPresent()) {
			allowedWriters = Arrays.stream(writer.get().value()).collect(Collectors.toSet());
		}
	}

	private Set<String> allowedReaders;
	private Set<String> allowedWriters;
	private final String operatorId;
}
