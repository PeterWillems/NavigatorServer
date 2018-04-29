package nl.tno.willemsph.coins_navigator.coins_class;

public class CoinsClass {

	private String name;
	private Boolean isAbstract;

	public CoinsClass() {
	}

	public CoinsClass(String name, Boolean isAbstract) {
		this.name = name;
		this.isAbstract = isAbstract;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getIsAbstract() {
		return isAbstract;
	}

	public void setIsAbstract(Boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

}
