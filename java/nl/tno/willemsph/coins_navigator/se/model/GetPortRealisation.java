package nl.tno.willemsph.coins_navigator.se.model;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.jena.query.ParameterizedSparqlString;

import com.fasterxml.jackson.databind.JsonNode;

import nl.tno.willemsph.coins_navigator.EmbeddedServer;
import nl.tno.willemsph.coins_navigator.se.SeService;

public class GetPortRealisation extends GetSeObject {

	public GetPortRealisation() {
	}

	public GetPortRealisation(SeService seService, int datasetId, String uri) throws URISyntaxException {
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

	public void deleteSystemInterface() throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("port_realisation", getUri().toString());
		queryStr.append("  DELETE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?port_realisation se:interface ?interface . ");
		queryStr.append("    } ");
		queryStr.append("  } ");
		queryStr.append("  WHERE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      { ");
		queryStr.append("        ?port_realisation se:interface ?interface . ");
		queryStr.append("      } ");
		queryStr.append("    }");
		queryStr.append("  }");

		getEmbeddedServer().update(queryStr);
	}

	public void insertSystemInterface(URI systemInterface) throws IOException, URISyntaxException {
		if (systemInterface != null) {
			ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
			queryStr.setIri("graph", getDatasetUri());
			queryStr.setIri("port_realisation", getUri().toString());
			queryStr.setIri("system_interface", systemInterface.toString());
			queryStr.append("  INSERT { ");
			queryStr.append("    GRAPH ?graph { ");
			queryStr.append("      ?port_realisation se:interface ?system_interface . ");
			queryStr.append("    } ");
			queryStr.append("  }");
			queryStr.append("WHERE { } ");

			getEmbeddedServer().update(queryStr);
		}
	}

	public void updateSystemInterface(URI systemInterface) throws IOException, URISyntaxException {
		deleteSystemInterface();
		insertSystemInterface(systemInterface);
	}

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

	public void updateRealisationPort(URI realisationPort) throws IOException, URISyntaxException {
		deleteRealisationPort();
		insertRealisationPort(realisationPort);
	}

	public void deleteRealisationPort() throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("port_realisation", getUri().toString());
		queryStr.append("  DELETE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?port_realisation se:port ?realisation_port . ");
		queryStr.append("    } ");
		queryStr.append("  } ");
		queryStr.append("  WHERE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      { ");
		queryStr.append("        ?port_realisation se:port ?realisation_port . ");
		queryStr.append("      } ");
		queryStr.append("    }");
		queryStr.append("  }");

		getEmbeddedServer().update(queryStr);
	}

	public void insertRealisationPort(URI realisationPort) throws IOException, URISyntaxException {
		if (realisationPort != null) {
			ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
			queryStr.setIri("graph", getDatasetUri());
			queryStr.setIri("port_realisation", getUri().toString());
			queryStr.setIri("realisation_port", realisationPort.toString());
			queryStr.append("  INSERT { ");
			queryStr.append("    GRAPH ?graph { ");
			queryStr.append("      ?port_realisation se:port ?realisation_port . ");
			queryStr.append("    } ");
			queryStr.append("  }");
			queryStr.append("WHERE { } ");

			getEmbeddedServer().update(queryStr);
		}
	}

	public void update(PutPortRealisation putPortRealisation) throws IOException, URISyntaxException {
		super.update(putPortRealisation);
		updateSystemInterface(putPortRealisation.getSystemInterface());
		updateRealisationPort(putPortRealisation.getRealisationPort());
	}

	public static GetPortRealisation create(SeService seService, int datasetId, String uri)
			throws URISyntaxException, IOException {
		GetPortRealisation portRealisation = new GetPortRealisation(seService, datasetId, uri);
		portRealisation.create();
		return portRealisation;
	}
	
	@Override
	public String containsRelation() {
		return EmbeddedServer.SE + "ContainsPortRealisation";
	}
}
