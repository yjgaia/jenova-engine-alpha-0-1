package co.hanul.jenova;

public class IdWrapper {
	public IdWrapper(Long id) {
		this.id = id;
	}

	private Long id;

	public Long getId() {
		return id;
	}

	public void idIncrease() {
		this.id++;
	}

}
