package de.sambalmueslie.herold.model.parse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.sambalmueslie.herold.DataModelElement;

public class JsonConverter<T extends DataModelElement> extends BaseConverter<T> {

	public JsonConverter(Class<? extends T> elementType) {
		super(elementType);

		gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
	}

	@Override
	protected String objToString(Object value) {
		return gson.toJson(value);
	}

	@Override
	protected Object stringToObj(String value, Class<?> type) {
		return gson.fromJson(value, type);
	}

	/** the Gson. */
	private final Gson gson;
}
