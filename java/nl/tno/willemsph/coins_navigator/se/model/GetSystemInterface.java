package nl.tno.willemsph.coins_navigator.se.model;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.ParameterizedSparqlString;

import com.fasterxml.jackson.databind.JsonNode;

import nl.tno.willemsph.coins_navigator.se.SeService;

public class GetSystemInterface extends GetSeObject {

	public GetSystemInterface() {
	}

	public GetSystemInterface(SeService seService, int datasetId, String uri) throws URISyntaxException {
		super(seService, datasetId, uri);
	}

	public URI getSystemSlot0() throws IOException, URISyntaxException {
		List<URI> systemSlots = getSystemSlots();
		if (systemSlots.size() > 0) {
			return systemSlots.get(0);
		}
		return null;
	}

	public void updateSystemSlots(URI systemSlot0, URI systemSlot1) throws URISyntaxException, IOException {
		deleteSystemSlots();
		if (systemSlot0 != null) {
			insertSystemSlot(systemSlot0);
		}
		if (systemSlot1 != null) {
			insertSystemSlot(systemSlot1);
		}
	}

	public void deleteSystemSlots() throws URISyntaxException, IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("system_interface", getUri().toString());

		queryStr.append("  DELETE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?system_slot se:hasInterfaces ?system_interface . ");
		queryStr.append("    } ");
		queryStr.append("  } ");
		queryStr.append("  WHERE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      { ");
		queryStr.append("        ?system_slot se:hasInterfaces ?system_interface . ");
		queryStr.append("      } ");
		queryStr.append("    }");
		queryStr.append("  }");

		getEmbeddedServer().update(queryStr);
	}

	public void insertSystemSlot(URI systemSlot) throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("system_interface", getUri().toString());
		queryStr.setIri("system_slot", systemSlot.toString());

		queryStr.append("  INSERT { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?system_slot se:hasInterfaces ?system_interface . ");
		queryStr.append("    } ");
		queryStr.append("  }");
		queryStr.append("WHERE { } ");

		getEmbeddedServer().update(queryStr);
	}

	public URI getSystemSlot1() throws IOException, URISyntaxException {
		List<URI> systemSlots = getSystemSlots();
		if (systemSlots.size() > 1) {
			return systemSlots.get(1);
		}
		return null;
	}

	private List<URI> getSystemSlots() throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("system_interface", getUri().toString());
		queryStr.append("SELECT ?system_slot ");
		queryStr.append("WHERE {");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("    ?system_slot se:hasInterfaces ?system_interface . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = getEmbeddedServer().query(queryStr);
		List<URI> systemSlotUris = new ArrayList<>();

		for (JsonNode node : responseNodes) {
			String system_slotUri = node.get("system_slot").get("value").asText();
			systemSlotUris.add(new URI(system_slotUri));
		}
		return systemSlotUris;
	}

	public List<URI> getRequirements() throws URISyntaxException, IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("system_interface", getUri().toString());
		queryStr.append("SELECT ?requirement ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?system_interface se:hasRequirement ?requirement . ");
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
		queryStr.setIri("system_interface", getUri().toString());
		queryStr.setIri("requirement", requirement.toString());
		queryStr.append("  INSERT { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?system_interface se:hasRequirement ?requirement . ");
		queryStr.append("    } ");
		queryStr.append("  }");
		queryStr.append("WHERE { } ");

		getEmbeddedServer().update(queryStr);
	}

	public void deleteRequirements() throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("system_interface", getUri().toString());

		queryStr.append("  DELETE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?system_interface se:hasRequirement ?requirement . ");
		queryStr.append("    } ");
		queryStr.append("  } ");
		queryStr.append("  WHERE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      { ");
		queryStr.append("        ?system_interface se:hasRequirement ?requirement . ");
		queryStr.append("      } ");
		queryStr.append("    }");
		queryStr.append("  }");

		getEmbeddedServer().update(queryStr);
	}

	public void update(PutSystemInterface putSystemInterface) throws IOException, URISyntaxException {
		super.update(putSystemInterface);
		updateSystemSlots(putSystemInterface.getSystemSlot0(), putSystemInterface.getSystemSlot1());
		updateRequirements(putSystemInterface.getRequirements());
	}

	public static GetSystemInterface create(SeService seService, int datasetId, String uri)
			throws URISyntaxException, IOException {
		GetSystemInterface systemInterface = new GetSystemInterface(seService, datasetId, uri);
		systemInterface.create();
		return systemInterface;
	}

}
