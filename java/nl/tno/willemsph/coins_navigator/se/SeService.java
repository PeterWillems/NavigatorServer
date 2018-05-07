package nl.tno.willemsph.coins_navigator.se;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.jena.query.ParameterizedSparqlString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import nl.tno.willemsph.coins_navigator.EmbeddedServer;
import nl.tno.willemsph.coins_navigator.se.model.Function;
import nl.tno.willemsph.coins_navigator.se.model.NetworkConnection;
import nl.tno.willemsph.coins_navigator.se.model.RealisationModule;
import nl.tno.willemsph.coins_navigator.se.model.SeObject;
import nl.tno.willemsph.coins_navigator.se.model.SystemSlot;

@Service
public class SeService {
	private enum SeObjectType {
		SystemSlot, RealisationModule, Function, NetworkConnection;

		SeObject create(String uri, String label, String assemblyUri, List<String> partUris) throws URISyntaxException {
			switch (this) {
			case Function:
				return new Function(uri, label, assemblyUri, partUris);
			case NetworkConnection:
				return new NetworkConnection(uri, label, assemblyUri, partUris);
			case SystemSlot:
				return new SystemSlot(uri, label, assemblyUri, partUris);
			case RealisationModule:
				return new RealisationModule(uri, label, assemblyUri, partUris);
			default:
				return null;
			}
		}

		String getUri() {
			return EmbeddedServer.SE + this.name();
		}
	}

	@Autowired
	private EmbeddedServer _embeddedServer;

	public List<SystemSlot> getAllSystemSlots(int datasetId) throws IOException, URISyntaxException {
		String datasetUri = getDatasetUri(datasetId);
		List<SeObject> seObjects = getAllSeObjects(datasetId, SeObjectType.SystemSlot);

		List<SystemSlot> systemSlots = new ArrayList<>();
		for (SeObject seObject : seObjects) {
			SystemSlot systemSlot = (SystemSlot) seObject;
			systemSlot.setFunctions(getFunctionsOfSystemSlot(datasetUri, systemSlot.getUri().toString()));
			systemSlot.setInterfaces(getInterfacesOfSystemSlot(datasetUri, systemSlot.getUri().toString()));
			systemSlots.add(systemSlot);
		}
		return systemSlots;
	}

	private List<URI> getFunctionsOfSystemSlot(String datasetUri, String systemSlotUri)
			throws IOException, URISyntaxException {
		List<URI> functionUris = null;
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("system_slot", systemSlotUri);
		queryStr.append("SELECT ?function ");
		queryStr.append("WHERE {");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("    ?system_slot se:hasFunction ?function . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		if (responseNodes.size() > 0) {
			functionUris = new ArrayList<>();
		}
		for (JsonNode node : responseNodes) {
			String uri = node.get("function").get("value").asText();
			functionUris.add(new URI(uri));
		}
		return functionUris;
	}

	private List<URI> getInterfacesOfSystemSlot(String datasetUri, String systemSlotUri)
			throws IOException, URISyntaxException {
		List<URI> interfaceUris = null;
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("system_slot", systemSlotUri);
		queryStr.append("SELECT ?interface ");
		queryStr.append("WHERE {");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("    ?system_slot se:hasInterfaces ?interface . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		if (responseNodes.size() > 0) {
			interfaceUris = new ArrayList<>();
		}
		for (JsonNode node : responseNodes) {
			String uri = node.get("interface").get("value").asText();
			interfaceUris.add(new URI(uri));
		}
		return interfaceUris;
	}

	public Function getFunction(int datasetId, String functionUri) throws URISyntaxException, IOException {
		return (Function) getSeObject(datasetId, functionUri, SeObjectType.Function);
	}

	public SystemSlot getSystemSlot(int datasetId, String localName) throws URISyntaxException, IOException {
		return (SystemSlot) getSeObject(datasetId, localName, SeObjectType.SystemSlot);
	}

	public List<Dataset> getAllDatasets() throws URISyntaxException {
		return _embeddedServer.getDatasets();
	}

	public Function createFunction(int datasetId) throws URISyntaxException, IOException {
		String datasetUri = getDatasetUri(datasetId);
		String functionUri = createSeObject(datasetUri, SeObjectType.Function);
		return getFunction(datasetId, functionUri);
	}

	public Function updateFunction(int datasetId, String localName, Function function)
			throws URISyntaxException, IOException {
		URI datasetUri = _embeddedServer.getDatasets().get(datasetId).getUri();
		updateLabel(datasetUri, function.getUri(), function.getLabel());
		updateAssembly(datasetUri, function.getUri(), function.getAssembly());
		return getFunction(datasetId, function.getUri().toString());
	}

	public SystemSlot createSystemSlot(int datasetId) throws URISyntaxException, IOException {
		String datasetUri = getDatasetUri(datasetId);
		String systemSlotUri = createSeObject(datasetUri, SeObjectType.SystemSlot);
		return getSystemSlot(datasetId, systemSlotUri);
	}

	public SystemSlot updateSystemSlot(int datasetId, String localName, SystemSlot systemSlot)
			throws URISyntaxException, IOException {
		URI datasetUri = _embeddedServer.getDatasets().get(datasetId).getUri();
		updateLabel(datasetUri, systemSlot.getUri(), systemSlot.getLabel());
		updateAssembly(datasetUri, systemSlot.getUri(), systemSlot.getAssembly());
		return getSystemSlot(datasetId, systemSlot.getUri().toString());
	}

	private void updateLabel(URI datasetUri, URI subjectUri, String newLabel) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri.toString());
		queryStr.setIri("subject", subjectUri.toString());
		queryStr.setLiteral("new_label", newLabel);
		queryStr.append("  DELETE { GRAPH ?graph { ?subject rdfs:label ?label }} ");
		queryStr.append("  INSERT { GRAPH ?graph { ?subject rdfs:label ?new_label }} ");
		queryStr.append("WHERE { GRAPH ?graph { OPTIONAL { ?subject rdfs:label ?label }} ");
		queryStr.append("}");

		_embeddedServer.update(queryStr);
	}

	private void updateAssembly(URI datasetUri, URI subjectUri, URI assemblyUri) throws IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri.toString());
		queryStr.setIri("subject", subjectUri.toString());

		queryStr.append("  DELETE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      ?c ?p1 ?o1 . ");
		queryStr.append("      ?o2 ?p2 ?c . ");
		queryStr.append("    } ");
		queryStr.append("  } ");
		queryStr.append("  WHERE { ");
		queryStr.append("    GRAPH ?graph { ");
		queryStr.append("      { ");
		queryStr.append("        ?c ?p1 ?o1 . ");
		queryStr.append("        OPTIONAL { ?o2 ?p2 ?c . } ");
		queryStr.append("        ?c coins2:hasPart ?subject . ");
		queryStr.append("        ?c coins2:hasAssembly ?assembly . ");
		queryStr.append("      } UNION { ");
		queryStr.append("        ?c ?p1 ?o1 . ");
		queryStr.append("        ?o2 ?p2 ?c . ");
		queryStr.append("        ?subject coins2:partOf ?c . ");
		queryStr.append("        ?c coins2:hasAssembly ?assembly . ");
		queryStr.append("      } UNION { ");
		queryStr.append("        ?c ?p1 ?o1 . ");
		queryStr.append("        ?o2 ?p2 ?c . ");
		queryStr.append("        ?c coins2:hasPart ?subject . ");
		queryStr.append("        ?assembly coins2:hasContainsRelation ?c . ");
		queryStr.append("      } UNION { ");
		queryStr.append("        ?c ?p1 ?o1 . ");
		queryStr.append("        ?o2 ?p2 ?c . ");
		queryStr.append("        ?subject coins2:partOf ?c . ");
		queryStr.append("        ?assembly coins2:hasContainsRelation ?c . ");
		queryStr.append("      } ");
		queryStr.append("    }");
		queryStr.append("  }");

		_embeddedServer.update(queryStr);

		if (assemblyUri != null) {
			String containsRelationUri = datasetUri + "#ContainsRelation_" + UUID.randomUUID().toString();
			queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
			queryStr.setIri("graph", datasetUri.toString());
			queryStr.setIri("subject", subjectUri.toString());
			queryStr.setIri("contains_relation", containsRelationUri);
			queryStr.setIri("assembly", assemblyUri.toString());
			queryStr.append("  INSERT { ");
			queryStr.append("    GRAPH ?graph { ");
			queryStr.append("      ?contains_relation rdf:type coins2:ContainsRelation . ");
			queryStr.append("      ?contains_relation rdf:type coins2:CoinsContainerObject . ");
			queryStr.append("      ?contains_relation coins2:hasAssembly ?assembly . ");
			queryStr.append("      ?contains_relation coins2:hasPart ?subject . ");
			queryStr.append("    } ");
			queryStr.append("  }");
			queryStr.append("WHERE { } ");

			_embeddedServer.update(queryStr);
		}
	}

	public List<Function> getAllFunctions(int datasetId) throws IOException, URISyntaxException {
		String datasetUri = getDatasetUri(datasetId);
		List<SeObject> seObjects = getAllSeObjects(datasetId, SeObjectType.Function);

		List<Function> functions = new ArrayList<>();
		for (SeObject seObject : seObjects) {
			Function function = (Function) seObject;
			function.setInput(getInputOfFunction(datasetUri, function.getUri().toString()));
			function.setOutput(getOutputOfFunction(datasetUri, function.getUri().toString()));
			functions.add(function);
		}
		return functions;
	}

	private URI getInputOfFunction(String datasetUri, String functionUri) throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("function", functionUri);
		queryStr.append("SELECT ?input ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?function se:input ?input . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		URI inputUri = null;
		for (JsonNode node : responseNodes) {
			JsonNode inputNode = node.get("input");
			String input = inputNode != null ? inputNode.get("value").asText() : null;
			inputUri = input != null ? new URI(input) : null;
		}
		return inputUri;
	}

	private URI getOutputOfFunction(String datasetUri, String functionUri) throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("function", functionUri);
		queryStr.append("SELECT ?output ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?function se:output ?output . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		URI outputUri = null;
		for (JsonNode node : responseNodes) {
			JsonNode outputNode = node.get("output");
			String output = outputNode != null ? outputNode.get("value").asText() : null;
			outputUri = output != null ? new URI(output) : null;
		}
		return outputUri;
	}

	public List<NetworkConnection> getAllNetworkConnections(int datasetId) throws IOException, URISyntaxException {
		List<SeObject> seObjects = getAllSeObjects(datasetId, SeObjectType.NetworkConnection);

		List<NetworkConnection> networkConnections = new ArrayList<>();
		for (SeObject seObject : seObjects) {
			networkConnections.add((NetworkConnection) seObject);
		}
		return networkConnections;
	}

	public List<RealisationModule> getAllRealisationModules(int datasetId) throws IOException, URISyntaxException {
		List<SeObject> seObjects = getAllSeObjects(datasetId, SeObjectType.RealisationModule);

		List<RealisationModule> realisationModules = new ArrayList<>();
		for (SeObject seObject : seObjects) {
			realisationModules.add((RealisationModule) seObject);
		}
		return realisationModules;
	}

	private List<SeObject> getAllSeObjects(int datasetId, SeObjectType seObjectType)
			throws IOException, URISyntaxException {
		String datasetUri = getDatasetUri(datasetId);
		List<SeObject> seObjects = new ArrayList<>();
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("SeObject", seObjectType.getUri());
		queryStr.append("SELECT ?se_object ?label ?assembly ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("    ?se_object rdf:type ?SeObject . ");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?se_object rdfs:label ?label . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?contains coins2:hasPart ?se_object . ");
		queryStr.append("      ?assembly coins2:hasContainsRelation ?contains . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?contains coins2:hasPart ?se_object . ");
		queryStr.append("      ?contains coins2:hasAssembly ?assembly . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?assembly coins2:hasContainsRelation ?contains . ");
		queryStr.append("      ?se_object coins2:partOf ?contains . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?contains coins2:hasAssembly ?assembly . ");
		queryStr.append("      ?se_object coins2:partOf ?contains . ");
		queryStr.append("    }");
		queryStr.append("  }");
		queryStr.append("}");
		queryStr.append("ORDER BY ?label");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		for (JsonNode node : responseNodes) {
			String seObjectUri = node.get("se_object").get("value").asText();
			JsonNode labelNode = node.get("label");
			String label = labelNode != null ? labelNode.get("value").asText() : null;
			JsonNode assemblyNode = node.get("assembly");
			String assembly = assemblyNode != null ? node.get("assembly").get("value").asText() : null;
			List<String> seObjectParts = getSeObjectParts(datasetId, getLocalName(seObjectUri), seObjectType);
			SeObject seObject = seObjectType.create(seObjectUri, label, assembly, seObjectParts);
			seObjects.add(seObject);
		}

		return seObjects;
	}

	public SeObject getSeObject(int datasetId, String localName, SeObjectType seObjectType)
			throws URISyntaxException, IOException {
		String datasetUri = getDatasetUri(datasetId);
		String ontologyUri = getOntologyUri(datasetId);
		String seObjectUri = ontologyUri + "#" + localName;
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("se_object", seObjectUri);
		queryStr.setIri("SeObject", seObjectType.getUri());
		queryStr.append("SELECT ?label ?assembly ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("    ?se_object rdf:type ?SeObject .");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?se_object rdfs:label ?label . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?contains coins2:hasPart ?se_object . ");
		queryStr.append("      ?assembly coins2:hasContainsRelation ?contains . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?contains coins2:hasPart ?se_object . ");
		queryStr.append("      ?contains coins2:hasAssembly ?assembly . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?assembly coins2:hasContainsRelation ?contains . ");
		queryStr.append("      ?se_object coins2:partOf ?contains . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?contains coins2:hasAssembly ?assembly . ");
		queryStr.append("      ?se_object coins2:partOf ?contains . ");
		queryStr.append("    }");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		SeObject seObject = null;
		for (JsonNode node : responseNodes) {
			JsonNode labelNode = node.get("label");
			String label = labelNode != null ? labelNode.get("value").asText() : null;
			JsonNode assemblyNode = node.get("assembly");
			String assembly = assemblyNode != null ? node.get("assembly").get("value").asText() : null;
			List<String> seObjectParts = getSeObjectParts(datasetId, getLocalName(seObjectUri), seObjectType);
			seObject = seObjectType.create(seObjectUri, label, assembly, seObjectParts);
		}

		return seObject;
	}

	public List<String> getSeObjectParts(int datasetId, String localName, SeObjectType seObjectType)
			throws URISyntaxException, IOException {
		String datasetUri = getDatasetUri(datasetId);
		String ontologyUri = getOntologyUri(datasetId);
		String seObjectUri = ontologyUri + "#" + localName;
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("assembly", seObjectUri);
		queryStr.setIri("SeObject", seObjectType.getUri());
		queryStr.append("SELECT ?part ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?contains coins2:hasPart ?part . ");
		queryStr.append("      ?assembly coins2:hasContainsRelation ?contains . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?contains coins2:hasPart ?part . ");
		queryStr.append("      ?contains coins2:hasAssembly ?assembly . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?assembly coins2:hasContainsRelation ?contains . ");
		queryStr.append("      ?part coins2:partOf ?contains . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?contains coins2:hasAssembly ?assembly . ");
		queryStr.append("      ?part coins2:partOf ?contains . ");
		queryStr.append("    }");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		List<String> parts = new ArrayList<>();
		for (JsonNode node : responseNodes) {
			JsonNode partNode = node.get("part");
			if (partNode != null) {
				String partUri = partNode.get("value").asText();
				parts.add(partUri);
			}
		}

		return parts;
	}

	private String createSeObject(String datasetUri, SeObjectType seObjectType) throws URISyntaxException, IOException {
		String localName = seObjectType.name() + "_" + UUID.randomUUID().toString();
		String label = localName.substring(0, seObjectType.name().length() + 5);
		String seObjectUri = datasetUri + "#" + localName;
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("se_object", seObjectUri);
		queryStr.setIri("SeObject", seObjectType.getUri());
		queryStr.setIri("graph", datasetUri);
		queryStr.setLiteral("label", label);
		queryStr.append("INSERT {");
		queryStr.append("  GRAPH ?graph {");
		queryStr.append("    ?se_object rdf:type ?SeObject .");
		queryStr.append("    ?se_object rdf:type coins2:CoinsContainerObject .");
		queryStr.append("    ?se_object rdfs:label ?label .");
		queryStr.append("  }");
		queryStr.append("}");
		queryStr.append("WHERE {");
		queryStr.append("}");

		_embeddedServer.update(queryStr);

		return localName;
	}

	private String getDatasetUri(int datasetId) throws URISyntaxException {
		return _embeddedServer.getDatasets().get(datasetId).getUri().toString();

	}

	private String getOntologyUri(int datasetId) throws URISyntaxException {
		return _embeddedServer.getDatasets().get(datasetId).getOntologyUri().toString();
	}

	private String getLocalName(String uri) {
		int indexOfHashMark = uri.indexOf('#');
		return uri.substring(indexOfHashMark + 1);
	}

}
