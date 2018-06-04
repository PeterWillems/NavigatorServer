package nl.tno.willemsph.coins_navigator.se.model;

import java.net.URI;

public class PutPortRealisation extends PutSeObject {
	
	private URI systemInterface;
	private URI realisationPort;

	public PutPortRealisation() {
		super();
	}

	public URI getSystemInterface() {
		return systemInterface;
	}

	public void setSystemInterface(URI systemInterface) {
		this.systemInterface = systemInterface;
	}

	public URI getRealisationPort() {
		return realisationPort;
	}

	public void setRealisationPort(URI realisationPort) {
		this.realisationPort = realisationPort;
	}

}
