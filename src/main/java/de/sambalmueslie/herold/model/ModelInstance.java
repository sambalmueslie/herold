package de.sambalmueslie.herold.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import de.sambalmueslie.herold.DataModel;
import de.sambalmueslie.herold.DataModelChangeListener;
import de.sambalmueslie.herold.DataModelElement;

class ModelInstance<T extends DataModelElement> implements DataModel<T> {

	private class ModelChangesForwarder implements DataModelChangeListener<T> {

		@Override
		public void handleElementAdded(T element) {
			changeListener.forEach(l -> l.handleElementAdded(element));
		}

		@Override
		public void handleElementChanged(T element) {
			changeListener.forEach(l -> l.handleElementChanged(element));
		}

		@Override
		public void handleElementRemoved(T element) {
			changeListener.forEach(l -> l.handleElementRemoved(element));
		}

	}

	ModelInstance(Model<T> model) {
		this.model = model;
		instanceId = UUID.randomUUID().getLeastSignificantBits();

		model.register(instanceId, new ModelChangesForwarder());
	}

	@Override
	public void add(T element) {
		model.handleLocalAdd(instanceId, element);
	}

	@Override
	public boolean contains(long elementId) {
		return model.contains(elementId);
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
		if (listener == null) return;
		if (changeListener.contains(listener)) return;

		changeListener.add(listener);

		model.stream().forEach(listener::handleElementAdded);
	}

	@Override
	public void remove(long elementId) {
		model.handleLocalRemove(instanceId, elementId);
	}

	@Override
	public void remove(T element) {
		model.handleLocalRemove(instanceId, element);
	}

	@Override
	public void removeAll() {
		model.handleLocalRemoveAll(instanceId);
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
		if (listener == null) return;

		changeListener.remove(listener);
	}

	@Override
	public void update(T element) {
		model.handleLocalUpdate(instanceId, element);
	}

	void dispose() {
		model.unregister(instanceId);
		changeListener.clear();
	}

	long getId() {
		return instanceId;
	}

	private final List<DataModelChangeListener<T>> changeListener = new LinkedList<>();
	private final long instanceId;
	private final Model<T> model;

}
