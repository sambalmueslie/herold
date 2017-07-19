package de.sambalmueslie.herold.model.instance;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import de.sambalmueslie.herold.DataModel;
import de.sambalmueslie.herold.DataModelChangeListener;
import de.sambalmueslie.herold.DataModelElement;
import de.sambalmueslie.herold.model.LocalModel;

public class ModelInstance<T extends DataModelElement> implements DataModel<T> {

	public ModelInstance(LocalModel<T> model) {
		this.model = model;
		instanceId = UUID.randomUUID().getLeastSignificantBits();
	}

	@Override
	public void add(T element) {
		model.add(instanceId, element);
	}

	@Override
	public boolean contains(long elementId) {
		return model.contains(elementId);
	}

	public void dispose() {
		model.unregisterAll(instanceId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final ModelInstance<?> other = (ModelInstance<?>) obj;
		if (instanceId != other.instanceId) return false;
		return true;
	}

	@Override
	public Optional<T> get(long elementId) {
		return model.get(elementId);
	}

	@Override
	public Collection<T> getAll() {
		return model.getAll();
	}

	public long getId() {
		return instanceId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (instanceId ^ instanceId >>> 32);
		return result;
	}

	@Override
	public boolean isEmpty() {
		return model.isEmpty();
	}

	@Override
	public void register(DataModelChangeListener<T> listener) {
		model.register(instanceId, listener);
	}

	@Override
	public void remove(long elementId) {
		model.remove(instanceId, elementId);
	}

	@Override
	public void remove(T element) {
		model.remove(instanceId, element);
	}

	@Override
	public void removeAll() {
		model.removeAll(instanceId);
	}

	@Override
	public int size() {
		return model.size();
	}

	@Override
	public Stream<T> stream() {
		return model.stream();
	}

	@Override
	public void unregister(DataModelChangeListener<T> listener) {
		model.unregister(instanceId, listener);
	}

	@Override
	public void update(T element) {
		model.update(instanceId, element);
	}

	private final long instanceId;
	private final LocalModel<T> model;

}
