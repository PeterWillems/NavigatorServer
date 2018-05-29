package nl.tno.willemsph.coins_navigator.se.model;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.jena.query.ParameterizedSparqlString;

import com.fasterxml.jackson.databind.JsonNode;

import nl.tno.willemsph.coins_navigator.se.SeService;

public class NumericProperty extends GetSeObject {
	// private URI type;
	// private Double datatypeValue;
	// private URI unit;

	public NumericProperty() {
	}

	// public NumericProperty(String uri, String label, String type, Double
	// datatypeValue, String unit)
	// throws URISyntaxException {
	// setUri(new URI(uri));
	// setLabel(label);
	// setType(type != null ? new URI(type) : null);
	// setDatatypeValue(datatypeValue);
	// setUnit(unit != null ? new URI(unit) : null);
	// }

	public NumericProperty(SeService seService, int datasetId, String uri) throws URISyntaxException {
		super(seService, datasetId, uri);
	}

	public URI getType() throws IOException, URISyntaxException {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("numeric_property", getUri().toString());
		queryStr.append("SELECT ?type ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?type rdfs:subClassOf coins2:FloatProperty . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?type rdfs:subClassOf coins2:IntegerProperty . ");
		queryStr.append("    }");
		queryStr.append("    OPTIONAL {");
		queryStr.append("      ?numeric_property rdf:type ?type . ");
		queryStr.append("    }");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = getEmbeddedServer().query(queryStr);
		for (JsonNode node : responseNodes) {
			JsonNode typeNode = node.get("type");
			String typeUri = typeNode != null ? typeNode.get("value").asText() : null;
			return typeUri != null ? new URI(typeUri) : null;
		}
		return null;
	}

	// public void setType(URI type) {
	// this.type = type;
	// }

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

	// public void setDatatypeValue(Double datatypeValue) {
	// this.datatypeValue = datatypeValue;
	// }

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

	// public void setUnit(URI unit) {
	// this.unit = unit;
	// }

}
