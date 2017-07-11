package de.sambalmueslie.herold.model;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import de.sambalmueslie.herold.DataModelChangeListener;
import de.sambalmueslie.herold.DataModelElement;

interface LocalModel<T extends DataModelElement> {

	boolean contains(long elementId);

	Optional<T> create();

	Optional<T> get(long elementId);

	Collection<T> getAll();

	void handleLocalAdd(long instanceId, T element);

	void handleLocalRemove(long instanceId, long elementId);

	void handleLocalRemove(long instanceId, T element);

	void handleLocalRemoveAll(long instanceId);

	void handleLocalUpdate(long instanceId, T element);

	boolean isEmpty();

	void register(long instanceId, DataModelChangeListener<T> listener);

	int size();

	Stream<T> stream();

	void unregister(long instanceId);

}
