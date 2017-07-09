package de.sambalmueslie.herold;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Base implementation for the {@link DataModelElement}.
 */
public abstract class BaseDataModelElement implements DataModelElement {

	@Override
	public final boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final BaseDataModelElement other = (BaseDataModelElement) obj;
		if (id != other.id) return false;
		return true;
	}

	@Override
	public final long getId() {
		return id;
	}

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ id >>> 32);
		return result;
	}

	public final void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	/** the id. */
	private long id;

}
