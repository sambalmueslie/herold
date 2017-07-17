package de.sambalmueslie.herold.model.parse;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Element {

	public Element() {
		// intentionally left empty
	}

	public Element(long id) {
		this.id = id;
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
