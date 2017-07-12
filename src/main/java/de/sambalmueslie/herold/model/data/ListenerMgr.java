package de.sambalmueslie.herold.model.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import de.sambalmueslie.herold.DataModelChangeListener;
import de.sambalmueslie.herold.DataModelElement;

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
			if (listeners.contains(listener)) return;
			listeners.add(listener);
		}

		void unregister(DataModelChangeListener<T> listener) {
			if (listener == null) return;
			listeners.remove(listener);
		}

		private final List<DataModelChangeListener<T>> listeners = new LinkedList<>();
	}

	void dispose() {

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

	/** the instances by id. */
	private final Map<Long, Instance> instances = new HashMap<>();

}
