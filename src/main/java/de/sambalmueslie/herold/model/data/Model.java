package de.sambalmueslie.herold.model.data;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
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
		listenerMgr = new ListenerMgr<>(metadata);
		dataMgr = new DataMgr<>(metadata, listenerMgr);
	}

	@Override
	public void add(long instanceId, T element) {
		dataMgr.insert(instanceId, element);
	}

	@Override
	public boolean contains(long elementId) {
		return dataMgr.contains(elementId);
	}

	@Override
	public void dispose() {
		dataMgr.clear();
		listenerMgr.dispose();
	}

	@Override
	public Optional<T> get(long elementId) {
		return dataMgr.get(elementId);
	}

	@Override
	public Collection<T> getAll() {
		return Collections.unmodifiableCollection(dataMgr.getAll());
	}

	@Override
	public Metadata<T> getMetadata() {
		return metadata;
	}

	@Override
	public boolean isEmpty() {
		return dataMgr.isEmpty();
	}

	@Override
	public void register(long instanceId, DataModelChangeListener<T> listener) {
		listenerMgr.register(instanceId, listener);
	}

	@Override
	public void remove(long instanceId, long elementId) {
		final Optional<T> element = dataMgr.get(elementId);
		element.ifPresent(e -> this.remove(instanceId, e));
	}

	@Override
	public void remove(long instanceId, T element) {
		dataMgr.remove(instanceId, element);
		if (element == null) {
			logger.error("Cannot local remove null element");
			return;
		}
		remove(instanceId, element.getId());
	}

	@Override
	public void removeAll(long instanceId) {
		final Set<T> copy = new LinkedHashSet<>(dataMgr.getAll());
		copy.forEach(e -> remove(instanceId, e));
	}

	@Override
	public int size() {
		return dataMgr.size();
	}

	@Override
	public Stream<T> stream() {
		return dataMgr.stream();
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
		dataMgr.insert(instanceId, element);
	}

	/** the {@link DataMgr}. */
	private final DataMgr<T> dataMgr;
	/** the {@link ListenerMgr}. */
	private final ListenerMgr<T> listenerMgr;
	/** the {@link Metadata}. */
	private final Metadata<T> metadata;

}
