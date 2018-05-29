package nl.tno.willemsph.coins_navigator.se.model;

import java.net.URI;
import java.util.List;

public class PutRealisationModule extends PutSeObject {

	private List<URI> performances;
	private List<URI> ports;

	public PutRealisationModule() {
		super();
	}

	public List<URI> getPerformances() {
		return performances;
	}

	public void setPerformances(List<URI> performances) {
		this.performances = performances;
	}

	public List<URI> getPorts() {
		return ports;
	}

	public void setPorts(List<URI> ports) {
		this.ports = ports;
	}

}
