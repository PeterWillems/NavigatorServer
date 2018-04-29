package nl.tno.willemsph.coins_navigator.coins_class;

public class CoinsProperty {

	private String name;
	private String rangeUri;

	public CoinsProperty() {
	}

	public CoinsProperty(String name, String rangeUri) {
		this.name = name;
		this.rangeUri = rangeUri;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRangeUri() {
		return rangeUri;
	}

	public void setRangeUri(String rangeUrl) {
		this.rangeUri = rangeUrl;
	}

}
