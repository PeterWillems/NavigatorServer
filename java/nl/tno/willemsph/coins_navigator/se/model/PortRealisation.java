package nl.tno.willemsph.coins_navigator.se.model;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.jena.query.ParameterizedSparqlString;

import com.fasterxml.jackson.databind.JsonNode;

import nl.tno.willemsph.coins_navigator.se.SeService;

public class PortRealisation extends GetSeObject {
	// private URI systemInterface;
	// private URI realisationPort;

	public PortRealisation() {
	}

	// public PortRealisation(String uri, String label, String assemblyUri,
	// List<String> partUris)
	// throws URISyntaxException {
	// super(uri, label, assemblyUri, partUris);
	// }

	public PortRealisation(SeService seService, int datasetId, String uri) throws URISyntaxException {
		super(seService, datasetId, uri);
	}

	public URI getSystemInterface() throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("port_realisation", getUri().toString());
		queryStr.append("SELECT ?system_interface ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?port_realisation se:interface ?system_interface . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = getEmbeddedServer().query(queryStr);
		URI systemInterfaceUri = null;
		for (JsonNode node : responseNodes) {
			JsonNode systemInterfaceNode = node.get("system_interface");
			String systemInterface = systemInterfaceNode != null ? systemInterfaceNode.get("value").asText() : null;
			systemInterfaceUri = systemInterface != null ? new URI(systemInterface) : null;
		}
		return systemInterfaceUri;
	}

	// public void setSystemInterface(URI systemInterface) {
	// this.systemInterface = systemInterface;
	// }

	public URI getRealisationPort() throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("port_realisation", getUri().toString());
		queryStr.append("SELECT ?realisation_port ");
		queryStr.append("{");
		queryStr.append(" GRAPH ?graph { ");
		queryStr.append(" ?port_realisation se:port ?realisation_port . ");
		queryStr.append(" }");
		queryStr.append("}");

		JsonNode responseNodes = getEmbeddedServer().query(queryStr);
		URI realisationPortUri = null;
		for (JsonNode node : responseNodes) {
			JsonNode realisationPortNode = node.get("realisation_port");
			String realisationPort = realisationPortNode != null ? realisationPortNode.get("value").asText() : null;
			realisationPortUri = realisationPort != null ? new URI(realisationPort) : null;
		}
		return realisationPortUri;
	}

	// public void setRealisationPort(URI realisationPort) {
	// this.realisationPort = realisationPort;
	// }

}
