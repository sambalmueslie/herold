package de.sambalmueslie.herold.model;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import de.sambalmueslie.herold.DataModelChangeListener;
import de.sambalmueslie.herold.DataModelElement;

public abstract class LocalModelFilter<T extends DataModelElement> implements LocalModel<T> {

	protected LocalModelFilter(LocalModel<T> next) {
		this.next = next;
	}

	@Override
	public void add(long instanceId, T element) {
		next.add(instanceId, element);
	}

	@Override
	public boolean contains(long elementId) {
		return next.contains(elementId);
	}

	@Override
	public void dispose() {
		next.dispose();
	}

	@Override
	public Optional<T> get(long elementId) {
		return next.get(elementId);
	}

	@Override
	public Collection<T> getAll() {
		return next.getAll();
	}

	@Override
	public Metadata<T> getMetadata() {
		return next.getMetadata();
	}

	@Override
	public boolean isEmpty() {
		return next.isEmpty();
	}

	@Override
	public void register(long instanceId, DataModelChangeListener<T> listener) {
		next.register(instanceId, listener);
	}

	@Override
	public void remove(long instanceId, long elementId) {
		next.remove(instanceId, elementId);
	}

	@Override
	public void remove(long instanceId, T element) {
		next.remove(instanceId, element);
	}

	@Override
	public void removeAll(long instanceId) {
		next.removeAll(instanceId);
	}

	@Override
	public int size() {
		return next.size();
	}

	@Override
	public Stream<T> stream() {
		return next.stream();
	}

	@Override
	public void unregister(long instanceId, DataModelChangeListener<T> listener) {
		next.unregister(instanceId, listener);
	}

	@Override
	public void unregisterAll(long instanceId) {
		next.unregisterAll(instanceId);
	}

	@Override
	public void update(long instanceId, T element) {
		next.update(instanceId, element);
	}

	protected LocalModel<T> getNext() {
		return next;
	}

	/** the next {@link LocalModel} for the filter. */
	private final LocalModel<T> next;

}
