package nl.tno.willemsph.coins_navigator.se.model;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.jena.query.ParameterizedSparqlString;

import com.fasterxml.jackson.databind.JsonNode;

import nl.tno.willemsph.coins_navigator.se.SeService;

public class GetNumericProperty extends GetSeObject {

	public GetNumericProperty() {
	}

	public GetNumericProperty(SeService seService, int datasetId, String uri) throws URISyntaxException {
		super(seService, datasetId, uri);
	}

	public URI getType() throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("numeric_property", getUri().toString());
		queryStr.append("SELECT ?type ");
		queryStr.append("{");
		// queryStr.append(" GRAPH ?graph { ");
		// queryStr.append(" ?numeric_property rdf:type ?type . ");
		// queryStr.append(" }");
		// queryStr.append(" ?type rdfs:subClassOf+ coins2:NumericProperty . ");
		queryStr.append(" GRAPH ?graph { ");
		queryStr.append("   OPTIONAL {");
		queryStr.append("     ?se_object rdf:type ?type . ");
		queryStr.append("     ?type rdfs:subClassOf coins2:FloatProperty . ");
		queryStr.append("   } ");
		queryStr.append("   OPTIONAL {");
		queryStr.append("     ?se_object rdf:type ?type . ");
		queryStr.append("     ?type rdfs:subClassOf coins2:IntegerProperty . ");
		queryStr.append("   } ");
		queryStr.append("  }");
		queryStr.append("   OPTIONAL {");
		queryStr.append("     GRAPH ?graph { ");
		queryStr.append("       ?se_object rdf:type ?type . ");
		queryStr.append("     }");
		queryStr.append("     ?type rdfs:subClassOf+ coins2:NumericProperty . ");
		queryStr.append("   }");
		queryStr.append("}");

		JsonNode responseNodes = getEmbeddedServer().query(queryStr);
		for (JsonNode node : responseNodes) {
			JsonNode typeNode = node.get("type");
			String typeUri = typeNode != null ? typeNode.get("value").asText() : null;
			return typeUri != null ? new URI(typeUri) : null;
		}
		return null;
	}

	public Double getDatatypeValue() throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("numeric_property", getUri().toString());
		queryStr.append("SELECT ?datatype_value ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?numeric_property coins2:datatypeValue ?datatype_value . ");
		queryStr.append("    }");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = getEmbeddedServer().query(queryStr);
		for (JsonNode node : responseNodes) {
			JsonNode datatypeValueNode = node.get("datatype_value");
			return datatypeValueNode != null ? datatypeValueNode.get("value").asDouble() : null;
		}
		return null;
	}

	public void updateDatatypeValue(Double datatypeValue) throws URISyntaxException, IOException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("subject", getUri().toString());
		if (datatypeValue != null) {
			queryStr.setLiteral("new_value", datatypeValue);
		}
		queryStr.append("  DELETE { GRAPH ?graph { ?subject coins2:datatypeValue ?datatype_value }} ");
		if (datatypeValue != null) {
			queryStr.append("  INSERT { GRAPH ?graph { ?subject coins2:datatypeValue ?new_value }} ");
		}
		queryStr.append("WHERE { GRAPH ?graph { OPTIONAL { ?subject coins2:datatypeValue ?datatype_value }} ");
		queryStr.append("}");

		getEmbeddedServer().update(queryStr);
	}

	public URI getUnit() throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("numeric_property", getUri().toString());
		queryStr.append("SELECT ?unit ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?numeric_property coins2:unit ?unit . ");
		queryStr.append("    }");
		queryStr.append("  }");
		queryStr.append("}");
		queryStr.append("ORDER BY ?label");

		JsonNode responseNodes = getEmbeddedServer().query(queryStr);
		for (JsonNode node : responseNodes) {
			JsonNode unitNode = node.get("unit");
			String unitUri = unitNode != null ? unitNode.get("value").asText() : null;
			return unitUri != null ? new URI(unitUri) : null;
		}
		return null;
	}

	public void updateUnit(URI unit) throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("numeric_property", getUri().toString());
		if (unit != null) {
			queryStr.setIri("new_unit", unit.toString());
		}
		queryStr.append("  DELETE { GRAPH ?graph { ?numeric_property coins2:unit ?unit }} ");
		if (unit != null) {
			queryStr.append("  INSERT { GRAPH ?graph { ?numeric_property coins2:unit ?new_unit }} ");
		}
		queryStr.append("WHERE { GRAPH ?graph { OPTIONAL { ?numeric_property coins2:unit ?unit }} ");
		queryStr.append("}");

		getEmbeddedServer().update(queryStr);
	}

	public static GetNumericProperty create(SeService seService, int datasetId, String uri, String typeUri)
			throws URISyntaxException, IOException {
		GetNumericProperty numericProperty = new GetNumericProperty(seService, datasetId, uri);
		// numericProperty.create();

		String label = numericProperty.getLocalName().substring(0,
				numericProperty.getClass().getSimpleName().length() + 2);
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(
				numericProperty.getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("se_object", numericProperty.getUri().toString());
		queryStr.setIri("numeric_property_type", typeUri);
		queryStr.setIri("graph", numericProperty.getDatasetUri());
		queryStr.setLiteral("label", label);
		queryStr.append("INSERT {");
		queryStr.append("  GRAPH ?graph {");
		queryStr.append("    ?se_object rdf:type ?numeric_property_type .");
		queryStr.append("    ?se_object rdf:type coins2:CoinsContainerObject .");
		queryStr.append("    ?se_object rdfs:label ?label .");
		queryStr.append("  }");
		queryStr.append("}");
		queryStr.append("WHERE {");
		queryStr.append("}");

		numericProperty.getEmbeddedServer().update(queryStr);
		return numericProperty;
	}

	public void update(PutNumericProperty putNumericProperty) throws IOException, URISyntaxException {
		super.update(putNumericProperty);
		updateDatatypeValue(putNumericProperty.getDatatypeValue());
		updateUnit(putNumericProperty.getUnit());
	}

}
