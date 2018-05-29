package nl.tno.willemsph.coins_navigator.se.model;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.jena.query.ParameterizedSparqlString;

import com.fasterxml.jackson.databind.JsonNode;

import nl.tno.willemsph.coins_navigator.se.SeService;

public class RealisationPort extends GetSeObject {
	// private URI owner;

	public RealisationPort() {
	}

	// public RealisationPort(String uri, String label, String assemblyUri,
	// List<String> partUris)
	// throws URISyntaxException {
	// super(uri, label, assemblyUri, partUris);
	// }

	public RealisationPort(SeService seService, int datasetId, String uri) throws URISyntaxException {
		super(seService, datasetId, uri);
	}

	public URI getOwner() throws URISyntaxException, IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("port", getUri().toString());
		queryStr.append("SELECT ?owner ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?owner se:hasPort ?port . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = getEmbeddedServer().query(queryStr);
		URI ownerUri = null;
		for (JsonNode node : responseNodes) {
			JsonNode ownerNode = node.get("owner");
			ownerUri = ownerNode != null ? new URI(ownerNode.get("value").asText()) : null;
		}
		return ownerUri;
	}

	// public void setOwner(URI owner) {
	// this.owner = owner;
	// }

}
