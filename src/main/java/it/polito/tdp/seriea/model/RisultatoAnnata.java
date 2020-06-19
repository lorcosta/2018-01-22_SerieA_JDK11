package it.polito.tdp.seriea.model;

public class RisultatoAnnata implements Comparable<RisultatoAnnata>{
	private Team squadra;
	private Integer punti;
	private Season stagione;
	/**
	 * @param squadra
	 * @param punti
	 * @param stagione
	 */
	public RisultatoAnnata(Team squadra, Integer punti, Season stagione) {
		super();
		this.squadra = squadra;
		this.punti = punti;
		this.stagione = stagione;
	}
	public Team getSquadra() {
		return squadra;
	}
	public void setSquadra(Team squadra) {
		this.squadra = squadra;
	}
	public Integer getPunti() {
		return punti;
	}
	public void setPunti(Integer punti) {
		this.punti = punti;
	}
	public Season getStagione() {
		return stagione;
	}
	public void setStagione(Season stagione) {
		this.stagione = stagione;
	}
	@Override
	public String toString() {
		return stagione+" "+squadra+", punti: "+punti;
	}
	@Override
	public int compareTo(RisultatoAnnata other) {
		return this.stagione.getSeason().compareTo(other.getStagione().getSeason());
	}
	
}
