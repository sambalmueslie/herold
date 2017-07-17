package de.sambalmueslie.herold.model.data;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

import de.sambalmueslie.herold.DataModelChangeListener;
import de.sambalmueslie.herold.DataModelElement;
import de.sambalmueslie.herold.model.Metadata;
import de.sambalmueslie.herold.model.parse.ElementConverter;

/**
 * @param <T>
 */
class ListenerMgr<T extends DataModelElement> {

	private class Instance {

		void dispose() {
			listeners.clear();
		}

		void notifyListeners(Consumer<DataModelChangeListener<T>> message) {
			listeners.forEach(message);
		}

		void register(DataModelChangeListener<T> listener) {
			if (listener == null) return;
			if (isSpecificListener(listener)) {
				listeners.add(new SpecificListenerWrapper<>(listener, cache, converter));
			} else {
				listeners.add(listener);
			}
		}

		void unregister(DataModelChangeListener<T> listener) {
			if (listener == null) return;
			listeners.remove(listener);
		}

		private boolean isSpecificListener(DataModelChangeListener<T> listener) {
			return specificListenerType.isAssignableFrom(listener.getClass());
		}

		private final Set<DataModelChangeListener<T>> listeners = new LinkedHashSet<>();

	}

	ListenerMgr(Metadata<T> metadata, ElementCache cache, ElementConverter<T> converter) {
		this.specificListenerType = metadata.getSpecificListenerType();
		this.cache = cache;
		this.converter = converter;
	}

	void dispose() {
		instances.clear();
	}

	void notifyElementAdded(long instanceId, T element) {
		notifyListeners(instanceId, e -> e.handleElementAdded(element));
	}

	void notifyElementRemoved(long instanceId, T element) {
		notifyListeners(instanceId, e -> e.handleElementRemoved(element));
	}

	void notifyElementUpdated(long instanceId, T element) {
		notifyListeners(instanceId, e -> e.handleElementChanged(element));
	}

	void register(long instanceId, DataModelChangeListener<T> listener) {
		Instance instance = instances.get(instanceId);
		if (instance == null) {
			instance = new Instance();
			instances.put(instanceId, instance);
		}

		instance.register(listener);
	}

	void unregister(long instanceId, DataModelChangeListener<T> listener) {
		final Instance instance = instances.get(instanceId);
		if (instance == null) return;

		instance.unregister(listener);

		if (instance.listeners.isEmpty()) {
			instances.remove(instanceId);
		}
	}

	void unregisterAll(long instanceId) {
		final Instance instance = instances.remove(instanceId);
		if (instance == null) return;

		instance.dispose();
	}

	private void notifyListeners(long instanceId, Consumer<DataModelChangeListener<T>> message) {
		instances.entrySet().stream().filter(e -> e.getKey() != instanceId).map(Entry::getValue).forEach(i -> i.notifyListeners(message));
	}

	private final ElementCache cache;
	private final ElementConverter<T> converter;
	/** the instances by id. */
	private final Map<Long, Instance> instances = new HashMap<>();
	private final Class<?> specificListenerType;

}
