package nl.tno.willemsph.coins_navigator.se;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.jena.query.ParameterizedSparqlString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import nl.tno.willemsph.coins_navigator.EmbeddedServer;
import nl.tno.willemsph.coins_navigator.se.model.Function;
import nl.tno.willemsph.coins_navigator.se.model.Hamburger;
import nl.tno.willemsph.coins_navigator.se.model.NetworkConnection;
import nl.tno.willemsph.coins_navigator.se.model.Performance;
import nl.tno.willemsph.coins_navigator.se.model.RealisationModule;
import nl.tno.willemsph.coins_navigator.se.model.Requirement;
import nl.tno.willemsph.coins_navigator.se.model.SeObject;
import nl.tno.willemsph.coins_navigator.se.model.SystemSlot;

@Service
public class SeService {
	private enum SeObjectType {
		SystemSlot, RealisationModule, Function, Performance, Requirement, NetworkConnection, Hamburger;

		SeObject create(String uri, String label, String assemblyUri, List<String> partUris) throws URISyntaxException {
			switch (this) {
			case Function:
				return new Function(uri, label, assemblyUri, partUris);
			case Hamburger:
				return new Hamburger(uri, label, assemblyUri, partUris);
			case NetworkConnection:
				return new NetworkConnection(uri, label, assemblyUri, partUris);
			case RealisationModule:
				return new RealisationModule(uri, label, assemblyUri, partUris);
			case Performance:
				return new Performance(uri, label, assemblyUri, partUris);
			case Requirement:
				return new Requirement(uri, label, assemblyUri, partUris);
			case SystemSlot:
				return new SystemSlot(uri, label, assemblyUri, partUris);
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
			systemSlot.setRequirements(getRequirements(datasetId, systemSlot.getUri().toString()));
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
		String functionUri = createSeObject(datasetId, SeObjectType.Function);
		return getFunction(datasetId, functionUri);
	}

	public Function updateFunction(int datasetId, String localName, Function function)
			throws URISyntaxException, IOException {
		URI datasetUri = _embeddedServer.getDatasets().get(datasetId).getUri();
		updateLabel(datasetUri, function.getUri(), function.getLabel());
		updateAssembly(datasetUri, function.getUri(), function.getAssembly());
		return getFunction(datasetId, function.getUri().toString());
	}

	public RealisationModule createRealisationModule(int datasetId) throws URISyntaxException, IOException {
		String realisationModuleUri = createSeObject(datasetId, SeObjectType.RealisationModule);
		return getRealisationModule(datasetId, realisationModuleUri);
	}

	public RealisationModule getRealisationModule(int datasetId, String realisationModuleUri)
			throws URISyntaxException, IOException {
		RealisationModule realisationModule = (RealisationModule) getSeObject(datasetId, realisationModuleUri,
				SeObjectType.RealisationModule);
		realisationModule.setPerformances(getPerformances(datasetId, realisationModule.getUri().toString()));

		return realisationModule;
	}

	public Requirement createRequirement(int datasetId) throws URISyntaxException, IOException {
		String requirementUri = createSeObject(datasetId, SeObjectType.Requirement);
		return getRequirement(datasetId, requirementUri);
	}

	public Performance getPerformance(int datasetId, String functionUri) throws URISyntaxException, IOException {
		return (Performance) getSeObject(datasetId, functionUri, SeObjectType.Performance);
	}

	public Requirement getRequirement(int datasetId, String functionUri) throws URISyntaxException, IOException {
		return (Requirement) getSeObject(datasetId, functionUri, SeObjectType.Requirement);
	}

	public SystemSlot createSystemSlot(int datasetId) throws URISyntaxException, IOException {
		String systemSlotUri = createSeObject(datasetId, SeObjectType.SystemSlot);
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
			function.setRequirements(getRequirements(datasetId, function.getUri().toString()));
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

	private List<URI> getPerformances(int datasetId, String ownerUri) throws URISyntaxException, IOException {
		String datasetUri = getDatasetUri(datasetId);
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("owner", ownerUri);
		queryStr.append("SELECT ?performance ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?owner se:hasPerformance ?performance . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		List<URI> performanceUris = new ArrayList<>();
		for (JsonNode node : responseNodes) {
			JsonNode performanceNode = node.get("performance");
			String performanceUri = performanceNode != null ? performanceNode.get("value").asText() : null;
			if (performanceUri != null) {
				performanceUris.add(new URI(performanceUri));
			}
		}
		return performanceUris;
	}

	private List<URI> getRequirements(int datasetId, String ownerUri) throws URISyntaxException, IOException {
		String datasetUri = getDatasetUri(datasetId);
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("owner", ownerUri);
		queryStr.append("SELECT ?requirement ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?owner se:hasRequirement ?requirement . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
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

	public List<Performance> getAllPerformances(int datasetId) throws IOException, URISyntaxException {
		List<SeObject> seObjects = getAllSeObjects(datasetId, SeObjectType.Performance);

		List<Performance> performances = new ArrayList<>();
		for (SeObject seObject : seObjects) {
			Performance performance = (Performance) seObject;
			performances.add(performance);
		}
		return performances;
	}

	public Performance createPerformance(int datasetId) throws URISyntaxException, IOException {
		String performanceUri = createSeObject(datasetId, SeObjectType.Performance);
		return getPerformance(datasetId, performanceUri);
	}

	public List<Requirement> getAllRequirements(int datasetId) throws IOException, URISyntaxException {
		List<SeObject> seObjects = getAllSeObjects(datasetId, SeObjectType.Requirement);

		List<Requirement> requirements = new ArrayList<>();
		for (SeObject seObject : seObjects) {
			Requirement requirement = (Requirement) seObject;
			requirements.add(requirement);
		}
		return requirements;
	}

	public List<NetworkConnection> getAllNetworkConnections(int datasetId) throws IOException, URISyntaxException {
		List<SeObject> seObjects = getAllSeObjects(datasetId, SeObjectType.NetworkConnection);

		List<NetworkConnection> networkConnections = new ArrayList<>();
		for (SeObject seObject : seObjects) {
			NetworkConnection networkConnection = (NetworkConnection) seObject;
			List<URI> systemSlots = getSystemSlotsOfNetworkConnection(datasetId, networkConnection.getUri().toString());
			if (systemSlots.size() > 0) {
				networkConnection.setSystemSlot0(systemSlots.get(0));
				if (systemSlots.size() > 1) {
					networkConnection.setSystemSlot1(systemSlots.get(1));
				}
			}
			networkConnections.add(networkConnection);
		}
		return networkConnections;
	}

	private List<URI> getSystemSlotsOfNetworkConnection(int datasetId, String networkConnectionUri)
			throws IOException, URISyntaxException {
		String datasetUri = getDatasetUri(datasetId);
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("network_connection", networkConnectionUri);
		queryStr.append("SELECT ?system_slot ");
		queryStr.append("WHERE {");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("    ?system_slot se:hasInterfaces ?network_connection . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = _embeddedServer.query(queryStr);
		List<URI> systemSlotUris = new ArrayList<>();

		for (JsonNode node : responseNodes) {
			String system_slotUri = node.get("system_slot").get("value").asText();
			systemSlotUris.add(new URI(system_slotUri));
		}
		return systemSlotUris;
	}

	public List<RealisationModule> getAllRealisationModules(int datasetId) throws IOException, URISyntaxException {
		List<SeObject> seObjects = getAllSeObjects(datasetId, SeObjectType.RealisationModule);

		List<RealisationModule> realisationModules = new ArrayList<>();
		for (SeObject seObject : seObjects) {
			RealisationModule realisationModule = (RealisationModule) seObject;
			realisationModule.setPerformances(getPerformances(datasetId, realisationModule.getUri().toString()));
			realisationModules.add(realisationModule);
		}
		return realisationModules;
	}

	public List<Hamburger> getHamburgersForSystemSlot(int datasetId, String systemSlotLocalName)
			throws URISyntaxException, IOException {
		String datasetUri = getDatasetUri(datasetId);
		String ontologyUri = getOntologyUri(datasetId);
		String systemSlotUri = ontologyUri + "#" + systemSlotLocalName;
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("system_slot", systemSlotUri);
		queryStr.append("SELECT ?hamburger ?technical_solution ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("    ?hamburger se:functionalUnit ?system_slot . ");
		queryStr.append("    OPTIONAL { ");
		queryStr.append("      ?hamburger se:technicalSolution ?technical_solution . ");
		queryStr.append("    }");
		queryStr.append("  }");
		queryStr.append("}");

		Map<String, String> hamburgerMap = new HashMap<>();
		JsonNode responseNodes = _embeddedServer.query(queryStr);
		for (JsonNode node : responseNodes) {
			String hamburgerUri = node.get("hamburger").get("value").asText();
			JsonNode technical_solutionNode = node.get("technical_solution");
			String technical_solutionUri = technical_solutionNode != null ? technical_solutionNode.get("value").asText()
					: null;
			hamburgerMap.put(hamburgerUri, technical_solutionUri);
		}

		List<Hamburger> hamburgers = new ArrayList<>();
		for (String hamburgerUri : hamburgerMap.keySet()) {
			String hamburgerLocalName = getLocalName(hamburgerUri);
			Hamburger hamburger = (Hamburger) getSeObject(datasetId, hamburgerLocalName, SeObjectType.Hamburger);
			hamburger.setFunctionalUnit(new URI(systemSlotUri));
			String technicalSolutionUri = hamburgerMap.get(hamburgerUri);
			if (technicalSolutionUri != null)
				hamburger.setTechnicalSolution(new URI(technicalSolutionUri));
			hamburgers.add(hamburger);
		}

		return hamburgers;
	}

	public List<Hamburger> getHamburgersForRealisationModule(int datasetId, String realisationModuleLocalName)
			throws URISyntaxException, IOException {
		String datasetUri = getDatasetUri(datasetId);
		String ontologyUri = getOntologyUri(datasetId);
		String realisationModuleUri = ontologyUri + "#" + realisationModuleLocalName;
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(_embeddedServer.getPrefixMapping());
		queryStr.setIri("graph", datasetUri);
		queryStr.setIri("realisation_module", realisationModuleUri);
		queryStr.append("SELECT ?hamburger ?functional_unit ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("    ?hamburger se:technicalSolution ?realisation_module . ");
		queryStr.append("    OPTIONAL { ");
		queryStr.append("      ?hamburger se:functionalUnit ?functional_unit . ");
		queryStr.append("    }");
		queryStr.append("  }");
		queryStr.append("}");

		Map<String, String> hamburgerMap = new HashMap<>();
		JsonNode responseNodes = _embeddedServer.query(queryStr);
		for (JsonNode node : responseNodes) {
			String hamburgerUri = node.get("hamburger").get("value").asText();
			JsonNode functionalUnitNode = node.get("functional_unit");
			String functionalUnitUri = functionalUnitNode != null ? functionalUnitNode.get("value").asText() : null;
			hamburgerMap.put(hamburgerUri, functionalUnitUri);
		}

		List<Hamburger> hamburgers = new ArrayList<>();
		for (String hamburgerUri : hamburgerMap.keySet()) {
			String hamburgerLocalName = getLocalName(hamburgerUri);
			Hamburger hamburger = (Hamburger) getSeObject(datasetId, hamburgerLocalName, SeObjectType.Hamburger);
			hamburger.setTechnicalSolution(new URI(realisationModuleUri));
			String functionalUnitUri = hamburgerMap.get(hamburgerUri);
			if (functionalUnitUri != null)
				hamburger.setFunctionalUnit(new URI(functionalUnitUri));
			hamburgers.add(hamburger);
		}

		return hamburgers;
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

	private String createSeObject(int datasetId, SeObjectType seObjectType) throws URISyntaxException, IOException {
		String datasetUri = getDatasetUri(datasetId);
		String ontologyUri = getOntologyUri(datasetId);
		String localName = seObjectType.name() + "_" + UUID.randomUUID().toString();
		String label = localName.substring(0, seObjectType.name().length() + 5);
		String seObjectUri = ontologyUri + "#" + localName;
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
