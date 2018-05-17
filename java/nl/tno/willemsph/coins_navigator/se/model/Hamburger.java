package nl.tno.willemsph.coins_navigator.se.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class Hamburger extends SeObject {
	private URI functionalUnit;
	private URI technicalSolution;
	private List<URI> portRealisations;

	public Hamburger() {
	}

	public Hamburger(String uri, String label, String assemblyUri, List<String> partUris) throws URISyntaxException {
		super(uri, label, assemblyUri, partUris);
	}

	public URI getFunctionalUnit() {
		return functionalUnit;
	}

	public void setFunctionalUnit(URI functionalUnit) {
		this.functionalUnit = functionalUnit;
	}

	public URI getTechnicalSolution() {
		return technicalSolution;
	}

	public void setTechnicalSolution(URI technicalSolution) {
		this.technicalSolution = technicalSolution;
	}

	public List<URI> getPortRealisations() {
		return portRealisations;
	}

	public void setPortRealisations(List<URI> portRealisations) {
		this.portRealisations = portRealisations;
	}

}
