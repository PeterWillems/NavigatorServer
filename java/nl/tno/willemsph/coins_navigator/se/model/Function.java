package nl.tno.willemsph.coins_navigator.se.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class Function extends SeObject {
	private URI input;
	private URI output;
	private List<URI> requirements;

	public Function() {
	}

	public Function(String uri, String label, String assemblyUri, List<String> partUris) throws URISyntaxException {
		super(uri, label, assemblyUri, partUris);
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
