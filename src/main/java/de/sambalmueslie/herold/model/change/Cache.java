package de.sambalmueslie.herold.model.change;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import de.sambalmueslie.herold.DataModelElement;

class Cache<T extends DataModelElement> {

	void clear() {
		cache.clear();
	}

	Optional<T> get(long elementId) {
		return Optional.ofNullable(cache.get(elementId));
	}

	void remove(long elementId) {
		cache.remove(elementId);
	}

	void update(T element) {
		final long elementId = element.getId();
		cache.put(elementId, element);
	}

	private final Map<Long, T> cache = new LinkedHashMap<>();
}
