package de.sambalmueslie.herold.model;

import de.sambalmueslie.herold.HeroldDataCenter;

/**
 * The {@link HeroldFactory}.
 */
public final class HeroldFactory {

	/**
	 * @return the {@link HeroldDataCenter}.
	 */
	public static HeroldDataCenter createDataCenter() {
		return new DataCenter();
	}

	/**
	 * Constructor.
	 */
	private HeroldFactory() {
		// intentionally left empty
	}
}
