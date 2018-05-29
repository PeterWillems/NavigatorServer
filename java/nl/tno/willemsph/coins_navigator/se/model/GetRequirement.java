package nl.tno.willemsph.coins_navigator.se.model;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.jena.query.ParameterizedSparqlString;

import com.fasterxml.jackson.databind.JsonNode;

import nl.tno.willemsph.coins_navigator.se.SeService;

public class GetRequirement extends GetSeObject {

	public GetRequirement() {
	}

	public GetRequirement(SeService seService, int datasetId, String uri) throws URISyntaxException {
		super(seService, datasetId, uri);
	}

	public URI getMinValue() throws IOException, URISyntaxException {
		URI minValueUri = null;
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("requirement", getUri().toString());
		queryStr.append("SELECT ?min_value ?value ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?requirement se:minValue ?min_value . ");
		queryStr.append("      ?min_value coins2:datatypeValue ?value . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = getEmbeddedServer().query(queryStr);
		for (JsonNode node : responseNodes) {
			JsonNode minValueNode = node.get("min_value");
			String minValue = minValueNode != null ? minValueNode.get("value").asText() : null;
			minValueUri = minValue != null ? new URI(minValue) : null;
		}
		return minValueUri;
	}

	public URI getMaxValue() throws IOException, URISyntaxException {
		URI maxValueUri = null;
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString(getEmbeddedServer().getPrefixMapping());
		queryStr.setIri("graph", getDatasetUri());
		queryStr.setIri("requirement", getUri().toString());
		queryStr.append("SELECT ?max_value ");
		queryStr.append("{");
		queryStr.append("  GRAPH ?graph { ");
		queryStr.append("      ?requirement se:maxValue ?max_value . ");
		queryStr.append("      ?max_value coins2:datatypeValue ?value . ");
		queryStr.append("  }");
		queryStr.append("}");

		JsonNode responseNodes = getEmbeddedServer().query(queryStr);
		for (JsonNode node : responseNodes) {
			JsonNode maxValueNode = node.get("max_value");
			String maxValue = maxValueNode != null ? maxValueNode.get("value").asText() : null;
			maxValueUri = maxValue != null ? new URI(maxValue) : null;
		}
		return maxValueUri;
	}

	public static GetRequirement create(SeService seService, int datasetId, String uri)
			throws URISyntaxException, IOException {
		GetRequirement requirement = new GetRequirement(seService, datasetId, uri);
		requirement.create();
		return requirement;
	}

}
