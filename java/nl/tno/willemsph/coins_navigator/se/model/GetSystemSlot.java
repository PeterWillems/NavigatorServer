package nl.tno.willemsph.coins_navigator.se.model;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.ParameterizedSparqlString;

import com.fasterxml.jackson.databind.JsonNode;

import nl.tno.willemsph.coins_navigator.EmbeddedServer;
import nl.tno.willemsph.coins_navigator.se.SeService;

public class GetSystemSlot extends GetSeObject {

	public GetSystemSlot() {
	}

	public GetSystemSlot(SeService seService, int datasetId, String uri) throws URISyntaxException {
		super(seService, datasetId, uri);
	}

	public List<URI> getRequirements() throws URISyntaxException, IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("system_slot", getUri().toString());
		queryStr.append("SELECT ?requirement ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?system_slot se:hasRequirement ?requirement . ");
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

	public void updateRequirements(List<URI> requirements) throws IOException, URISyntaxException {
		deleteRequirements();
		insertRequirements(requirements);
	}

	public void deleteRequirements() throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("system_slot", getUri().toString());

		queryStr.append("  DELETE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?system_slot se:hasRequirement ?requirement . ");
		queryStr.append("    } ");
		queryStr.append("  } ");
		queryStr.append("  WHERE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      { ");
		queryStr.append("        ?system_slot se:hasRequirement ?requirement . ");
		queryStr.append("      } ");
		queryStr.append("    }");
		queryStr.append("  }");

		getEmbeddedServer().update(queryStr);
	}

	public void insertRequirements(List<URI> requirements) throws URISyntaxException, IOException {
		if (requirements != null) {
			for (URI requirement : requirements) {
				insertRequirement(requirement);
			}
		}
	}

	public void insertRequirement(URI requirement) throws URISyntaxException, IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("system_slot", getUri().toString());
		queryStr.setIri("requirement", requirement.toString());
		queryStr.append("  INSERT { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?system_slot se:hasRequirement ?requirement . ");
		queryStr.append("    } ");
		queryStr.append("  }");
		queryStr.append("WHERE { } ");

		getEmbeddedServer().update(queryStr);
	}

	public List<URI> getFunctions() throws IOException, URISyntaxException {
		List<URI> functionUris = new ArrayList<>();
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("system_slot", getUri().toString());
		queryStr.append("SELECT ?function ");
		queryStr.append("WHERE {");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("    ?system_slot se:hasFunction ?function . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = getEmbeddedServer().query(queryStr);
		for (JsonNode node : responseNodes) {
			String uri = node.get("function").get("value").asText();
			functionUris.add(new URI(uri));
		}
		return functionUris;
	}

	public void updateFunctions(List<URI> functions) throws IOException, URISyntaxException {
		deleteFunctions();
		insertFunctions(functions);
	}

	public void deleteFunctions() throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("system_slot", getUri().toString());

		queryStr.append("  DELETE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?system_slot se:hasFunction ?function . ");
		queryStr.append("    } ");
		queryStr.append("  } ");
		queryStr.append("  WHERE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      { ");
		queryStr.append("        ?system_slot se:hasFunction ?function . ");
		queryStr.append("      } ");
		queryStr.append("    }");
		queryStr.append("  }");

		getEmbeddedServer().update(queryStr);
	}

	public void insertFunctions(List<URI> functions) throws URISyntaxException, IOException {
		if (functions != null) {
			for (URI function : functions) {
				insertFunction(function);
			}
		}
	}

	public void insertFunction(URI function) throws URISyntaxException, IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("system_interface", getUri().toString());
		queryStr.setIri("function", function.toString());
		queryStr.append("  INSERT { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?system_interface se:hasFunction ?function . ");
		queryStr.append("    } ");
		queryStr.append("  }");
		queryStr.append("WHERE { } ");

		getEmbeddedServer().update(queryStr);
	}

	public List<URI> getInterfaces() throws IOException, URISyntaxException {
		List<URI> interfaceUris = new ArrayList<>();
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("system_slot", getUri().toString());
		queryStr.append("SELECT ?interface ");
		queryStr.append("WHERE {");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("    ?system_slot se:hasInterfaces ?interface . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = getEmbeddedServer().query(queryStr);
		for (JsonNode node : responseNodes) {
			String uri = node.get("interface").get("value").asText();
			interfaceUris.add(new URI(uri));
		}
		return interfaceUris;
	}

	public void updateInterfaces(List<URI> interfaces) throws IOException, URISyntaxException {
		deleteInterfaces();
		insertInterfaces(interfaces);
	}

	public void deleteInterfaces() throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("system_slot", getUri().toString());

		queryStr.append("  DELETE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?system_slot se:hasInterfaces ?interface . ");
		queryStr.append("    } ");
		queryStr.append("  } ");
		queryStr.append("  WHERE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      { ");
		queryStr.append("        ?system_slot se:hasInterfaces ?interface . ");
		queryStr.append("      } ");
		queryStr.append("    }");
		queryStr.append("  }");

		getEmbeddedServer().update(queryStr);
	}

	public void insertInterfaces(List<URI> interfaces) throws URISyntaxException, IOException {
		if (interfaces != null) {
			for (URI systemInterface : interfaces) {
				insertInterface(systemInterface);
			}
		}
	}

	public void insertInterface(URI interfaceUri) throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("system_slot", getUri().toString());
		queryStr.setIri("interface", interfaceUri.toString());
		queryStr.append("  INSERT { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?system_slot se:hasInterfaces ?interface . ");
		queryStr.append("    } ");
		queryStr.append("  }");
		queryStr.append("WHERE { } ");

		getEmbeddedServer().update(queryStr);
	}

	public void update(PutSystemSlot putSystemSlot) throws IOException, URISyntaxException {
		super.update(putSystemSlot);
		updateFunctions(putSystemSlot.getFunctions());
		updateInterfaces(putSystemSlot.getInterfaces());
		updateRequirements(putSystemSlot.getRequirements());
	}

	public static GetSystemSlot create(SeService seService, int datasetId, String uri)
			throws URISyntaxException, IOException {
		GetSystemSlot systemSlot = new GetSystemSlot(seService, datasetId, uri);
		systemSlot.create();
		return systemSlot;
	}
	
	@Override
	public String containsRelation() {
		return EmbeddedServer.SE + "ContainsSystemSlot";
	}
}
