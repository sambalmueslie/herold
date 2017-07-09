package de.sambalmueslie.herold.model;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.sambalmueslie.herold.DataModelChangeListener;
import de.sambalmueslie.herold.DataModelElement;

class Model<T extends DataModelElement> {

	private static Logger logger = LogManager.getLogger(Model.class);

	boolean contains(long elementId) {
		return data.containsKey(elementId);
	}

	void dispose() {
		data.clear();
		listeners.clear();
	}

	Optional<T> get(long elementId) {
		final T element = data.get(elementId);
		return Optional.ofNullable(element);
	}

	Collection<T> getAll() {
		return Collections.unmodifiableCollection(data.values());
	}

	void handleLocalAdd(long instanceId, T element) {
		if (element == null) {
			logger.error("Cannot local add null element");
			return;
		}

		final long elementId = element.getId();
		if (data.containsKey(elementId)) {
			handleLocalUpdate(instanceId, element);
			return;
		}

		logger.debug("Localy add new element {}", element);
		data.put(elementId, element);

		notifyListeners(instanceId, l -> l.handleElementAdded(element));

	}

	void handleLocalRemove(long instanceId, long elementId) {
		final T element = data.remove(elementId);

		if (element == null) return;

		logger.debug("Localy remove element {}", element);
		notifyListeners(instanceId, l -> l.handleElementRemoved(element));
	}

	void handleLocalRemove(long instanceId, T element) {
		if (element == null) {
			logger.error("Cannot local remove null element");
			return;
		}
		handleLocalRemove(instanceId, element.getId());
	}

	void handleLocalRemoveAll(long instanceId) {
		final Set<Long> copy = new LinkedHashSet<>(data.keySet());
		copy.forEach(id -> handleLocalRemove(instanceId, id));
	}

	void handleLocalUpdate(long instanceId, T element) {
		if (element == null) {
			logger.error("Cannot local update null element");
			return;
		}

		final long elementId = element.getId();
		if (!data.containsKey(elementId)) {
			handleLocalAdd(instanceId, element);
			return;
		}

		// TODO merge with existing element
		// TODO ignore updates without a change

		logger.debug("Localy update element {}", element);
		data.put(elementId, element);

		notifyListeners(instanceId, l -> l.handleElementChanged(element));
	}

	boolean isEmpty() {
		return data.isEmpty();
	}

	void register(long instanceId, DataModelChangeListener<T> listener) {
		if (listener == null) return;

		listeners.put(instanceId, listener);
	}

	int size() {
		return data.size();
	}

	Stream<T> stream() {
		return data.values().stream();
	}

	void unregister(long instanceId) {
		listeners.remove(instanceId);
	}

	private void notifyListeners(long instanceId, Consumer<DataModelChangeListener<T>> message) {
		listeners.entrySet().stream().filter(e -> e.getKey() != instanceId).map(Entry::getValue).forEach(message);
	}

	private final Map<Long, T> data = new LinkedHashMap<>();

	private final Map<Long, DataModelChangeListener<T>> listeners = new LinkedHashMap<>();

}
