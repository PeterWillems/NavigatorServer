package nl.tno.willemsph.coins_navigator.se.model;

import java.net.URI;
import java.net.URISyntaxException;

public class NumericProperty extends SeObject {
	private URI type;
	private Double datatypeValue;
	private URI unit;

	public NumericProperty() {
	}

	public NumericProperty(String uri, String label, String type, Double datatypeValue, String unit)
			throws URISyntaxException {
		setUri(new URI(uri));
		setLabel(label);
		setType(type != null ? new URI(type) : null);
		setDatatypeValue(datatypeValue);
		setUnit(unit != null ? new URI(unit) : null);
	}

	public URI getType() {
		return type;
	}

	public void setType(URI type) {
		this.type = type;
	}

	public Double getDatatypeValue() {
		return datatypeValue;
	}

	public void setDatatypeValue(Double datatypeValue) {
		this.datatypeValue = datatypeValue;
	}

	public URI getUnit() {
		return unit;
	}

	public void setUnit(URI unit) {
		this.unit = unit;
	}

}
