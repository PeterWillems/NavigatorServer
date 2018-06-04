package nl.tno.willemsph.coins_navigator.se.model;

import java.net.URI;
import java.util.List;

public class PutRealisationPort extends PutSeObject {
	private URI owner;
	private List<URI> performances;

	public PutRealisationPort() {
		super();
	}

	public URI getOwner() {
		return owner;
	}

	public void setOwner(URI owner) {
		this.owner = owner;
	}

	public List<URI> getPerformances() {
		return performances;
	}

	public void setPerformances(List<URI> performances) {
		this.performances = performances;
	}

}
