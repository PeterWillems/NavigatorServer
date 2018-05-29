package nl.tno.willemsph.coins_navigator.se.model;

import java.net.URI;
import java.util.List;

public class PutSystemSlot extends PutSeObject {
	private List<URI> functions;
	private List<URI> interfaces;
	private List<URI> requirements;

	public PutSystemSlot() {
		super();
	}

	public List<URI> getFunctions() {
		return functions;
	}

	public void setFunctions(List<URI> functions) {
		this.functions = functions;
	}

	public List<URI> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(List<URI> interfaces) {
		this.interfaces = interfaces;
	}

	public List<URI> getRequirements() {
		return requirements;
	}

	public void setRequirements(List<URI> requirements) {
		this.requirements = requirements;
	}

}
