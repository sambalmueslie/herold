package de.sambalmueslie.herold.model;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import de.sambalmueslie.herold.DataModelChangeListener;
import de.sambalmueslie.herold.DataModelElement;

public interface LocalModel<T extends DataModelElement> {

	void add(long instanceId, T element);

	boolean contains(long elementId);

	void dispose();

	Optional<T> get(long elementId);

	Collection<T> getAll();

	Metadata<T> getMetadata();

	boolean isEmpty();

	void register(long instanceId, DataModelChangeListener<T> listener);

	void remove(long instanceId, long elementId);

	void remove(long instanceId, T element);

	void removeAll(long instanceId);

	int size();

	Stream<T> stream();

	void unregister(long instanceId, DataModelChangeListener<T> listener);

	void unregisterAll(long instanceId);

	void update(long instanceId, T element);
}
