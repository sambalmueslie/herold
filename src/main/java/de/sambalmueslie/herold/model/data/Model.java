package de.sambalmueslie.herold.model.data;

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
import de.sambalmueslie.herold.model.LocalModel;
import de.sambalmueslie.herold.model.Metadata;

public class Model<T extends DataModelElement> implements LocalModel<T> {

	private static Logger logger = LogManager.getLogger(Model.class);

	public Model(Metadata<T> metadata) {
		this.metadata = metadata;
	}

	@Override
	public boolean contains(long elementId) {
		return data.containsKey(elementId);
	}

	@Override
	public void dispose() {
		data.clear();
		listeners.clear();
	}

	@Override
	public Optional<T> get(long elementId) {
		final T element = data.get(elementId);
		return Optional.ofNullable(element);
	}

	@Override
	public Collection<T> getAll() {
		return Collections.unmodifiableCollection(data.values());
	}

	@Override
	public Metadata<T> getMetadata() {
		return metadata;
	}

	@Override
	public void add(long instanceId, T element) {
		if (element == null) {
			logger.error("Cannot local add null element");
			return;
		}

		final long elementId = element.getId();
		if (data.containsKey(elementId)) {
			update(instanceId, element);
			return;
		}

		logger.debug("Localy add new element {}", element);
		data.put(elementId, element);

		notifyListeners(instanceId, l -> l.handleElementAdded(element));

	}

	@Override
	public void remove(long instanceId, long elementId) {
		final T element = data.remove(elementId);

		if (element == null) return;

		logger.debug("Localy remove element {}", element);
		notifyListeners(instanceId, l -> l.handleElementRemoved(element));
	}

	@Override
	public void remove(long instanceId, T element) {
		if (element == null) {
			logger.error("Cannot local remove null element");
			return;
		}
		remove(instanceId, element.getId());
	}

	@Override
	public void removeAll(long instanceId) {
		final Set<Long> copy = new LinkedHashSet<>(data.keySet());
		copy.forEach(id -> remove(instanceId, id));
	}

	@Override
	public void update(long instanceId, T element) {
		if (element == null) {
			logger.error("Cannot local update null element");
			return;
		}

		final long elementId = element.getId();
		if (!data.containsKey(elementId)) {
			add(instanceId, element);
			return;
		}

		// TODO merge with existing element
		// TODO ignore updates without a change

		logger.debug("Localy update element {}", element);
		data.put(elementId, element);

		notifyListeners(instanceId, l -> l.handleElementChanged(element));
	}

	@Override
	public boolean isEmpty() {
		return data.isEmpty();
	}

	@Override
	public void register(long instanceId, DataModelChangeListener<T> listener) {
		if (listener == null) return;

		listeners.put(instanceId, listener);
	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public Stream<T> stream() {
		return data.values().stream();
	}

	@Override
	public void unregister(long instanceId) {
		listeners.remove(instanceId);
	}

	private void notifyListeners(long instanceId, Consumer<DataModelChangeListener<T>> message) {
		listeners.entrySet().stream().filter(e -> e.getKey() != instanceId).map(Entry::getValue).forEach(message);
	}

	private final Map<Long, T> data = new LinkedHashMap<>();
	private final Map<Long, DataModelChangeListener<T>> listeners = new LinkedHashMap<>();
	private final Metadata<T> metadata;

}
