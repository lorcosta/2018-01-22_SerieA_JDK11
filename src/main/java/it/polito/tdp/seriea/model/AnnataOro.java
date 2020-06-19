package it.polito.tdp.seriea.model;

public class AnnataOro {
	private Season s;
	private Double maxSomma;
	/**
	 * @param s
	 * @param maxSomma
	 */
	public AnnataOro(Season s, Double maxSomma) {
		super();
		this.s = s;
		this.maxSomma = maxSomma;
	}
	public Season getS() {
		return s;
	}
	public void setS(Season s) {
		this.s = s;
	}
	public Double getMaxSomma() {
		return maxSomma;
	}
	public void setMaxSomma(Double maxSomma) {
		this.maxSomma = maxSomma;
	}
	@Override
	public String toString() {
		return s.getDescription()+" "+maxSomma;
	}
	
}
