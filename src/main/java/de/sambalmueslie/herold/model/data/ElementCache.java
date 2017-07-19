package de.sambalmueslie.herold.model.data;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import de.sambalmueslie.herold.model.parse.Element;

class ElementCache {
	void clear() {
		cache.clear();
	}

	Optional<Element> get(long elementId) {
		return Optional.ofNullable(cache.get(elementId));
	}

	void remove(long elementId) {
		cache.remove(elementId);
	}

	void update(Element element) {
		final long elementId = element.getId();
		cache.put(elementId, element);
	}

	private final Map<Long, Element> cache = new LinkedHashMap<>();
}
