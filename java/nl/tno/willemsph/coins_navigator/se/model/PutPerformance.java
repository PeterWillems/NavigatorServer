package nl.tno.willemsph.coins_navigator.se.model;

import java.net.URI;

public class PutPerformance extends PutSeObject {
	
	private URI value;

	public PutPerformance() {
		super();
	}

	public URI getValue() {
		return value;
	}

	public void setValue(URI value) {
		this.value = value;
	}

}
