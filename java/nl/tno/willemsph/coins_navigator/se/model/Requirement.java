package nl.tno.willemsph.coins_navigator.se.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class Requirement extends SeObject {
	
	private URI minValue;
	private URI maxValue;

	public Requirement() {
	}

	public Requirement(String uri, String label, String assemblyUri, List<String> partUris) throws URISyntaxException {
		super(uri, label, assemblyUri, partUris);
	}

	public URI getMinValue() {
		return minValue;
	}

	public void setMinValue(URI minValue) {
		this.minValue = minValue;
	}

	public URI getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(URI maxValue) {
		this.maxValue = maxValue;
	}

}
