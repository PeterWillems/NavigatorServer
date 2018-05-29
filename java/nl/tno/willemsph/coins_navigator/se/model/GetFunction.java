package nl.tno.willemsph.coins_navigator.se.model;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.ParameterizedSparqlString;

import com.fasterxml.jackson.databind.JsonNode;

import nl.tno.willemsph.coins_navigator.se.SeService;

public class GetFunction extends GetSeObject {

	public GetFunction() {
	}

	public GetFunction(SeService seService, int datasetId, String uri) throws URISyntaxException {
		super(seService, datasetId, uri);
	}

	public URI getInput() throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("function", getUri().toString());
		queryStr.append("SELECT ?input ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?function se:input ?input . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = getEmbeddedServer().query(queryStr);
		URI inputUri = null;
		for (JsonNode node : responseNodes) {
			JsonNode inputNode = node.get("input");
			String input = inputNode != null ? inputNode.get("value").asText() : null;
			inputUri = input != null ? new URI(input) : null;
		}
		return inputUri;
	}

	public void deleteInput() throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("function", getUri().toString());

		queryStr.append("  DELETE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?function se:input ?system_interface . ");
		queryStr.append("    } ");
		queryStr.append("  } ");
		queryStr.append("  WHERE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      { ");
		queryStr.append("        ?function se:output ?system_interface . ");
		queryStr.append("      } ");
		queryStr.append("    }");
		queryStr.append("  }");

		getEmbeddedServer().update(queryStr);
	}

	public void insertInput(URI systemInterface) throws IOException, URISyntaxException {
		if (systemInterface != null) {
			ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
			queryStr.setIri("graph", getDatasetUri());
			queryStr.setIri("function", getUri().toString());
			queryStr.setIri("system_interface", systemInterface.toString());
			queryStr.append("  INSERT { ");
			queryStr.append("    GRAPH ?graph { ");
			queryStr.append("      ?function se:input ?system_interface . ");
			queryStr.append("    } ");
			queryStr.append("  }");
			queryStr.append("WHERE { } ");

			getEmbeddedServer().update(queryStr);
		}
	}

	public void updateInput(URI systemInterface) throws IOException, URISyntaxException {
		deleteInput();
		insertInput(systemInterface);
	}

	public URI getOutput() throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("function", getUri().toString());
		queryStr.append("SELECT ?output ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?function se:output ?output . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = getEmbeddedServer().query(queryStr);
		URI outputUri = null;
		for (JsonNode node : responseNodes) {
			JsonNode outputNode = node.get("output");
			String output = outputNode != null ? outputNode.get("value").asText() : null;
			outputUri = output != null ? new URI(output) : null;
		}
		return outputUri;
	}

	public void deleteOutput() throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("function", getUri().toString());

		queryStr.append("  DELETE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?function se:output ?system_interface . ");
		queryStr.append("    } ");
		queryStr.append("  } ");
		queryStr.append("  WHERE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      { ");
		queryStr.append("        ?function se:output ?system_interface . ");
		queryStr.append("      } ");
		queryStr.append("    }");
		queryStr.append("  }");

		getEmbeddedServer().update(queryStr);
	}

	public void insertOutput(URI systemInterface) throws IOException, URISyntaxException {
		if (systemInterface != null) {
			ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
			queryStr.setIri("graph", getDatasetUri());
			queryStr.setIri("function", getUri().toString());
			queryStr.setIri("system_interface", systemInterface.toString());
			queryStr.append("  INSERT { ");
			queryStr.append("    GRAPH ?graph { ");
			queryStr.append("      ?function se:output ?system_interface . ");
			queryStr.append("    } ");
			queryStr.append("  }");
			queryStr.append("WHERE { } ");

			getEmbeddedServer().update(queryStr);
		}
	}

	public void updateOutput(URI systemInterface) throws IOException, URISyntaxException {
		deleteOutput();
		insertOutput(systemInterface);
	}

	public List<URI> getRequirements() throws URISyntaxException, IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("function", getUri().toString());
		queryStr.append("SELECT ?requirement ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?function se:hasRequirement ?requirement . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = getEmbeddedServer().query(queryStr);
		List<URI> requirementUris = new ArrayList<>();
		for (JsonNode node : responseNodes) {
			JsonNode requirementNode = node.get("requirement");
			String requirementUri = requirementNode != null ? requirementNode.get("value").asText() : null;
			if (requirementUri != null) {
				requirementUris.add(new URI(requirementUri));
			}
		}
		return requirementUris;
	}

	public void deleteRequirements() throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("function", getUri().toString());

		queryStr.append("  DELETE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?function se:hasRequirement ?requirement . ");
		queryStr.append("    } ");
		queryStr.append("  } ");
		queryStr.append("  WHERE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      { ");
		queryStr.append("        ?function se:hasRequirement ?requirement . ");
		queryStr.append("      } ");
		queryStr.append("    }");
		queryStr.append("  }");

		getEmbeddedServer().update(queryStr);
	}

	public void insertRequirements(List<URI> requirements) throws IOException, URISyntaxException {
		if (requirements != null) {
			for (URI requirement : requirements) {
				insertRequirement(requirement);
			}
		}
	}

	private void insertRequirement(URI requirement) throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("function", getUri().toString());
		queryStr.setIri("requirement", requirement.toString());
		queryStr.append("  INSERT { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?function se:hasRequirement ?requirement . ");
		queryStr.append("    } ");
		queryStr.append("  }");
		queryStr.append("WHERE { } ");

		getEmbeddedServer().update(queryStr);
	}

	public void updateRequirements(List<URI> requirements) throws IOException, URISyntaxException {
		deleteRequirements();
		insertRequirements(requirements);
	}

	public void update(PutFunction putFunction) throws IOException, URISyntaxException {
		super.update(putFunction);
		updateInput(putFunction.getInput());
		updateOutput(putFunction.getOutput());
		updateRequirements(putFunction.getRequirements());
	}

	public static GetFunction create(SeService seService, int datasetId, String uri)
			throws URISyntaxException, IOException {
		GetFunction function = new GetFunction(seService, datasetId, uri);
		function.create();
		return function;
	}
}
