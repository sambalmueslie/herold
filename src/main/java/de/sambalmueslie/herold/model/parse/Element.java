package de.sambalmueslie.herold.model.parse;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class Element {

	public Element() {
		// intentionally left empty
	}

	public Element(long id) {
		this.id = id;
	}

	public boolean contains(String name) {
		return values.containsKey(name);
	}

	public Optional<String> get(String name) {
		return Optional.ofNullable(values.get(name));
	}

	public long getId() {
		return id;
	}

	public Map<String, String> getValues() {
		return Collections.unmodifiableMap(values);
	}

	public void set(String name, String value) {
		values.put(name, value);
	}

	private long id;
	/** the values by key. */
	private final Map<String, String> values = new LinkedHashMap<>();
}
