package de.sambalmueslie.herold.model.parse;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	private long id;
	/** the values by key. */
	private final Map<String, String> values = new LinkedHashMap<>();
}
