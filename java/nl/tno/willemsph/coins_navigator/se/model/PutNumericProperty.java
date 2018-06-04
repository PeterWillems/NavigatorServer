package nl.tno.willemsph.coins_navigator.se.model;

import java.net.URI;

public class PutNumericProperty extends PutSeObject {

	private Double datatypeValue;
	private URI type;
	private URI unit;

	public PutNumericProperty() {
		super();
	}

	public Double getDatatypeValue() {
		return datatypeValue;
	}

	public void setDatatypeValue(Double datatypeValue) {
		this.datatypeValue = datatypeValue;
	}

	public URI getType() {
		return type;
	}

	public void setType(URI type) {
		this.type = type;
	}

	public URI getUnit() {
		return unit;
	}

	public void setUnit(URI unit) {
		this.unit = unit;
	}

}
