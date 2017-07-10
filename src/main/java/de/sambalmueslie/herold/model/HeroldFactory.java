package de.sambalmueslie.herold.model;

import de.sambalmueslie.herold.HeroldDataCenter;

/**
 * The {@link HeroldFactory}.
 */
public final class HeroldFactory {

	/**
	 * Create a anonymous data center.
	 *
	 * @return the {@link HeroldDataCenter}.
	 */
	public static HeroldDataCenter createDataCenter() {
		return createDataCenter(null);
	}

	/**
	 * Create a data center for a specified operator.
	 *
	 * @param operatorId
	 *            the operator id to use the data center.
	 * @return the {@link HeroldDataCenter}.
	 */
	public static HeroldDataCenter createDataCenter(String operatorId) {
		return new DataCenter(operatorId);
	}

	/**
	 * Constructor.
	 */
	private HeroldFactory() {
		// intentionally left empty
	}
}
