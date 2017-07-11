package de.sambalmueslie.herold.model;

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

class ModelAccessController<T extends DataModelElement> implements LocalModel<T> {

	ModelAccessController(String operatorId, LocalModel<T> model, Class<T> elementType) {
		this.operatorId = operatorId;
		this.model = model;
		this.elementType = elementType;

		setup();
	}

	@Override
	public boolean contains(long elementId) {
		if (isReadRestricted()) throw new ReadAccessException(operatorId, elementType, "Cannot read");
		return model.contains(elementId);
	}

	@Override
	public Optional<T> create() {
		return model.create();
	}

	@Override
	public Optional<T> get(long elementId) {
		if (isReadRestricted()) throw new ReadAccessException(operatorId, elementType, "Cannot read");
		return model.get(elementId);
	}

	@Override
	public Collection<T> getAll() {
		if (isReadRestricted()) throw new ReadAccessException(operatorId, elementType, "Cannot read");
		return model.getAll();
	}

	@Override
	public void handleLocalAdd(long instanceId, T element) {
		if (isWriteRestricted())
			throw new WriteAccessException(operatorId, elementType, "Cannot add element");

		model.handleLocalAdd(instanceId, element);
	}

	@Override
	public void handleLocalRemove(long instanceId, long elementId) {
		if (isWriteRestricted())
			throw new WriteAccessException(operatorId, elementType, "Cannot remove element");

		model.handleLocalRemove(instanceId, elementId);
	}

	@Override
	public void handleLocalRemove(long instanceId, T element) {
		if (isWriteRestricted())
			throw new WriteAccessException(operatorId, elementType, "Cannot remove element");

		model.handleLocalRemove(instanceId, element);
	}

	@Override
	public void handleLocalRemoveAll(long instanceId) {
		if (isWriteRestricted())
			throw new WriteAccessException(operatorId, elementType, "Cannot remove all elements");

		model.handleLocalRemoveAll(instanceId);
	}

	@Override
	public void handleLocalUpdate(long instanceId, T element) {
		if (isWriteRestricted())
			throw new WriteAccessException(operatorId, elementType, "Cannot update element");

		model.handleLocalUpdate(instanceId, element);
	}

	@Override
	public boolean isEmpty() {
		if (isReadRestricted()) throw new ReadAccessException(operatorId, elementType, "Cannot read");
		return model.isEmpty();
	}

	@Override
	public void register(long instanceId, DataModelChangeListener<T> listener) {
		model.register(instanceId, listener);
	}

	@Override
	public int size() {
		if (isReadRestricted()) throw new ReadAccessException(operatorId, elementType, "Cannot read");
		return model.size();
	}

	@Override
	public Stream<T> stream() {
		if (isReadRestricted()) throw new ReadAccessException(operatorId, elementType, "Cannot read");
		return model.stream();
	}

	@Override
	public void unregister(long instanceId) {
		model.unregister(instanceId);
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
		final AllowedReader reader = elementType.getAnnotation(AllowedReader.class);
		if (reader != null) {
			allowedReaders = Arrays.stream(reader.value()).collect(Collectors.toSet());
		}

		final AllowedWriter writer = elementType.getAnnotation(AllowedWriter.class);
		if (writer != null) {
			allowedWriters = Arrays.stream(writer.value()).collect(Collectors.toSet());
		}
	}

	private Set<String> allowedReaders;
	private Set<String> allowedWriters;
	private final Class<T> elementType;
	private final LocalModel<T> model;
	private final String operatorId;
}
