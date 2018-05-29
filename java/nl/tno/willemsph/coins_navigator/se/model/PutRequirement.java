package nl.tno.willemsph.coins_navigator.se.model;

import java.net.URI;

public class PutRequirement extends PutSeObject {
	private URI maxValue;
	private URI minValue;

	public PutRequirement() {
	}

	public URI getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(URI maxValue) {
		this.maxValue = maxValue;
	}

	public URI getMinValue() {
		return minValue;
	}

	public void setMinValue(URI minValue) {
		this.minValue = minValue;
	}

}
