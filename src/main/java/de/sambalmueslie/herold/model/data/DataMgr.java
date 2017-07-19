package de.sambalmueslie.herold.model.data;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.sambalmueslie.herold.DataModelElement;
import de.sambalmueslie.herold.model.Metadata;
import de.sambalmueslie.herold.model.parse.Element;
import de.sambalmueslie.herold.model.parse.ElementConverter;
import de.sambalmueslie.herold.model.parse.JsonConverter;

class DataMgr<T extends DataModelElement> {
	private static Logger logger = LogManager.getLogger(DataMgr.class);

	DataMgr(Metadata<T> metadata, ListenerMgr<T> listenerMgr) {
		elementImplType = metadata.getElementImplType();
		converter = new JsonConverter<>(elementImplType);
		this.listenerMgr = listenerMgr;
	}

	void clear() {
		data.clear();
		elementCache.clear();
		dataCache.clear();
	}

	boolean contains(long elementId) {
		return data.containsKey(elementId);
	}

	Optional<T> get(long elementId) {
		final T element = data.get(elementId);
		return Optional.ofNullable(element);
	}

	Collection<T> getAll() {
		return Collections.unmodifiableCollection(data.values());
	}

	void insert(long instanceId, T element) {
		if (element == null) {
			logger.error("Cannot local add/update null element");
			return;
		}
		final long elementId = element.getId();
		if (data.containsKey(elementId)) {
			handleInsertAdd(instanceId, element);
		} else {
			handleInsertUpdate(instanceId, element);
		}
	}

	boolean isEmpty() {
		return data.isEmpty();
	}

	void remove(long instanceId, T element) {
		if (element == null) {
			logger.error("Cannot local remove null element");
			return;
		}
		final long elementId = element.getId();
		data.remove(elementId);
		elementCache.remove(elementId);
		dataCache.remove(elementId);
		listenerMgr.notifyElementRemoved(instanceId, element);
	}

	int size() {
		return data.size();
	}

	Stream<T> stream() {
		return data.values().stream();
	}

	private Map<String, Object> getChanges(long elementId, Element diff) {
		final T former = dataCache.get(elementId);

		final Map<String, Object> result = new LinkedHashMap<>();
		for (final Entry<String, String> e : diff.getValues().entrySet()) {
			final String fieldName = e.getKey();
			Field f = fieldCache.get(fieldName);
			if (f == null) {
				try {
					f = elementImplType.getField(fieldName);
					f.setAccessible(true);
					fieldCache.put(fieldName, f);
				} catch (NoSuchFieldException | SecurityException e1) {
					logger.error("Cannot find field for change " + fieldName + " on type " + elementImplType, e);
					continue;
				}
			}

			try {
				final Object formerValue = f.get(former);
				result.put(fieldName, formerValue);
			} catch (IllegalArgumentException | IllegalAccessException e1) {
				logger.error("Cannot get field for change " + fieldName + " on type " + elementImplType, e);
			}
		}

		return result;
	}

	private Element getDiff(Optional<Element> current, Optional<Element> former) {
		if (!current.isPresent() && !former.isPresent()) return new Element();
		if (current.isPresent() && !former.isPresent()) return current.get();
		if (!current.isPresent() && former.isPresent()) return former.get();

		final Element element = new Element(current.get().getId());

		final Element f = former.get();
		final Element c = current.get();

		// added and changed values
		c.getValues().entrySet().stream()
				.filter(e -> f.contains(e.getKey()))
				.filter(e -> StringUtils.equals(f.get(e.getKey()).get(), e.getValue()))
				.forEach(e -> element.set(e.getKey(), f.get(e.getKey()).get()));

		// removed values
		f.getValues().entrySet().stream()
				.filter(e -> !element.contains(e.getKey()))
				.filter(e -> !c.contains(e.getKey()))
				.forEach(e -> element.set(e.getKey(), f.get(e.getKey()).get()));

		return element;
	}

	private void handleInsertAdd(long instanceId, T element) {
		logger.debug("Insert new element {}", element);
		data.put(element.getId(), element);
		dataCache.put(element.getId(), element);
		listenerMgr.notifyElementAdded(instanceId, element);
	}

	private void handleInsertUpdate(long instanceId, T element) {
		logger.debug("Update existing element {}", element);

		final long elementId = element.getId();
		final Optional<Element> former = elementCache.get(elementId);
		final Optional<Element> current = converter.convert(element);
		final Element diff = getDiff(current, former);
		if (diff.getValues().isEmpty()) return;

		current.ifPresent(elementCache::update);
		final Map<String, Object> changes = getChanges(elementId, diff);
		dataCache.put(elementId, element);
		listenerMgr.notifyElementUpdated(instanceId, element, changes);
	}

	/** the {@link ElementConverter}. */
	private final ElementConverter<T> converter;

	private final Map<Long, T> data = new LinkedHashMap<>();

	private final Map<Long, T> dataCache = new LinkedHashMap<>();

	private final ElementCache elementCache = new ElementCache();
	private final Class<? extends T> elementImplType;
	private final Map<String, Field> fieldCache = new HashMap<>();
	/** the {@link ListenerMgr}. */
	private final ListenerMgr<T> listenerMgr;
}
