package de.sambalmueslie.herold.model.access;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.sambalmueslie.herold.DataModelChangeListener;
import de.sambalmueslie.herold.DataModelElement;
import de.sambalmueslie.herold.annotations.AllowedReader;
import de.sambalmueslie.herold.annotations.AllowedWriter;
import de.sambalmueslie.herold.exceptions.ReadAccessException;
import de.sambalmueslie.herold.exceptions.WriteAccessException;
import de.sambalmueslie.herold.model.LocalModel;
import de.sambalmueslie.herold.model.Metadata;
import de.sambalmueslie.herold.util.AnnotationSpy;

public class AccessController<T extends DataModelElement> implements LocalModel<T> {

	public AccessController(String operatorId, LocalModel<T> model) {
		this.operatorId = operatorId;
		this.model = model;

		setup();
	}

	@Override
	public boolean contains(long elementId) {
		if (isReadRestricted()) throw new ReadAccessException(operatorId, getElementType(), "Cannot read");
		return model.contains(elementId);
	}

	@Override
	public void dispose() {
		allowedReaders.clear();
		allowedWriters.clear();
		model.dispose();
	}

	@Override
	public Optional<T> get(long elementId) {
		if (isReadRestricted()) throw new ReadAccessException(operatorId, getElementType(), "Cannot read");
		return model.get(elementId);
	}

	@Override
	public Collection<T> getAll() {
		if (isReadRestricted()) throw new ReadAccessException(operatorId, getElementType(), "Cannot read");
		return model.getAll();
	}

	@Override
	public Metadata<T> getMetadata() {
		return model.getMetadata();
	}

	@Override
	public void handleLocalAdd(long instanceId, T element) {
		if (isWriteRestricted())
			throw new WriteAccessException(operatorId, getElementType(), "Cannot add element");

		model.handleLocalAdd(instanceId, element);
	}

	@Override
	public void handleLocalRemove(long instanceId, long elementId) {
		if (isWriteRestricted())
			throw new WriteAccessException(operatorId, getElementType(), "Cannot remove element");

		model.handleLocalRemove(instanceId, elementId);
	}

	@Override
	public void handleLocalRemove(long instanceId, T element) {
		if (isWriteRestricted())
			throw new WriteAccessException(operatorId, getElementType(), "Cannot remove element");

		model.handleLocalRemove(instanceId, element);
	}

	@Override
	public void handleLocalRemoveAll(long instanceId) {
		if (isWriteRestricted())
			throw new WriteAccessException(operatorId, getElementType(), "Cannot remove all elements");

		model.handleLocalRemoveAll(instanceId);
	}

	@Override
	public void handleLocalUpdate(long instanceId, T element) {
		if (isWriteRestricted())
			throw new WriteAccessException(operatorId, getElementType(), "Cannot update element");

		model.handleLocalUpdate(instanceId, element);
	}

	@Override
	public boolean isEmpty() {
		if (isReadRestricted()) throw new ReadAccessException(operatorId, getElementType(), "Cannot read");
		return model.isEmpty();
	}

	@Override
	public void register(long instanceId, DataModelChangeListener<T> listener) {
		model.register(instanceId, listener);
	}

	@Override
	public int size() {
		if (isReadRestricted()) throw new ReadAccessException(operatorId, getElementType(), "Cannot read");
		return model.size();
	}

	@Override
	public Stream<T> stream() {
		if (isReadRestricted()) throw new ReadAccessException(operatorId, getElementType(), "Cannot read");
		return model.stream();
	}

	@Override
	public void unregister(long instanceId) {
		model.unregister(instanceId);
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
	private final LocalModel<T> model;
	private final String operatorId;
}
