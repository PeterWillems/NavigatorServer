package nl.tno.willemsph.coins_navigator.se.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import nl.tno.willemsph.coins_navigator.se.SeService;

public class PutHamburger extends PutSeObject {
	private URI functionalUnit;
	private URI technicalSolution;
	private List<URI> portRealisations;

	public PutHamburger() {
	}

	public PutHamburger(SeService seService, int datasetId, String uri) throws URISyntaxException {
		super();
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
