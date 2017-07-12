package de.sambalmueslie.herold.model.data;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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

		listenerMgr = new ListenerMgr<>();
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

		listenerMgr.notifyElementAdded(instanceId, element);

	}

	@Override
	public boolean contains(long elementId) {
		return data.containsKey(elementId);
	}

	@Override
	public void dispose() {
		data.clear();
		listenerMgr.dispose();
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
	public boolean isEmpty() {
		return data.isEmpty();
	}

	@Override
	public void register(long instanceId, DataModelChangeListener<T> listener) {
		listenerMgr.register(instanceId, listener);
	}

	@Override
	public void remove(long instanceId, long elementId) {
		final T element = data.remove(elementId);

		if (element == null) return;

		logger.debug("Localy remove element {}", element);
		listenerMgr.notifyElementRemoved(instanceId, element);
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
	public int size() {
		return data.size();
	}

	@Override
	public Stream<T> stream() {
		return data.values().stream();
	}

	@Override
	public void unregister(long instanceId, DataModelChangeListener<T> listener) {
		listenerMgr.unregister(instanceId, listener);
	}

	@Override
	public void unregisterAll(long instanceId) {
		listenerMgr.unregisterAll(instanceId);
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

		logger.debug("Localy update element {}", element);
		data.put(elementId, element);

		listenerMgr.notifyElementUpdated(instanceId, element);
	}

	private final Map<Long, T> data = new LinkedHashMap<>();
	private final ListenerMgr<T> listenerMgr;
	private final Metadata<T> metadata;

}
