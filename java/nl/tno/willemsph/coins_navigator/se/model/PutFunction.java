package nl.tno.willemsph.coins_navigator.se.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import nl.tno.willemsph.coins_navigator.se.SeService;

public class PutFunction extends PutSeObject {
	private URI input;
	private URI output;
	private List<URI> requirements;

	public PutFunction() {
	}

	public PutFunction(SeService seService, int datasetId, String uri) throws URISyntaxException {
		super();
	}

	public URI getInput() {
		return input;
	}

	public void setInput(URI input) {
		this.input = input;
	}

	public URI getOutput() {
		return output;
	}

	public void setOutput(URI output) {
		this.output = output;
	}

	public List<URI> getRequirements() {
		return requirements;
	}

	public void setRequirements(List<URI> requirements) {
		this.requirements = requirements;
	}

}
