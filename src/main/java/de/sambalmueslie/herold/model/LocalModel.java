package de.sambalmueslie.herold.model;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import de.sambalmueslie.herold.DataModelChangeListener;
import de.sambalmueslie.herold.DataModelElement;

public interface LocalModel<T extends DataModelElement> {

	boolean contains(long elementId);

	void dispose();

	Optional<T> get(long elementId);

	Collection<T> getAll();

	Metadata<T> getMetadata();

	void add(long instanceId, T element);

	void remove(long instanceId, long elementId);

	void remove(long instanceId, T element);

	void removeAll(long instanceId);

	void update(long instanceId, T element);

	boolean isEmpty();

	void register(long instanceId, DataModelChangeListener<T> listener);

	int size();

	Stream<T> stream();

	void unregister(long instanceId);
}
