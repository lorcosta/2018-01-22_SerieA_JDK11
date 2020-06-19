package it.polito.tdp.seriea.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.seriea.db.SerieADAO;

public class Model {
	private SerieADAO dao= new SerieADAO();
	private Map<Integer,Season> idMapSeason;
	private Map<String,Team> idMapTeam;
	private List<RisultatoAnnata> risultatiAnnataSquadra;
	private Graph<Season,DefaultWeightedEdge> graph;
	private List<RisultatoAnnata> camminoVirtuoso;
	
	public Model() {
		idMapSeason=new HashMap<>();
		idMapTeam=new HashMap<>();
		camminoVirtuoso=new ArrayList<>();
		dao.listAllSeasons(idMapSeason);
	}
	public List<Team> getTeams(){
		return dao.listTeams(idMapTeam);
	}
	public List<RisultatoAnnata> selezionaSquadra(Team squadra) {
		risultatiAnnataSquadra=new ArrayList<>();
		List<Match> matches=dao.selezionaSquadra(squadra, idMapSeason, idMapTeam);
		Season stagione=matches.get(0).getSeason();
		Integer punteggioStagione=0;
		for(Match m:matches) {
			if(stagione.equals(m.getSeason())) {
				//sono ancora nella stessa stagione perciò continuo a controllare le partite
				if(m.getHomeTeam().getTeam().equals(squadra.getTeam()) && m.getFtr().equals("H") ) {
					punteggioStagione+=3;
				}else if(m.getAwayTeam().getTeam().equals(squadra.getTeam()) && m.getFtr().equals("A")) {
					punteggioStagione+=3;
				}else if(m.getFtr().equals("D")) {
					punteggioStagione++;
				}
			}else {
				//sono in una nuova stagione, cambio "stagione" e controllo nuovi risultati
				risultatiAnnataSquadra.add(new RisultatoAnnata(idMapTeam.get(squadra.getTeam()),punteggioStagione,stagione));
				stagione=m.getSeason();
				punteggioStagione=0;
				//controllo il risultato al quale sono, per non perdere questo match
				if(m.getHomeTeam().getTeam().equals(squadra.getTeam()) && m.getFtr().equals("H") ) {
					punteggioStagione+=3;
				}else if(m.getAwayTeam().getTeam().equals(squadra.getTeam()) && m.getFtr().equals("A")) {
					punteggioStagione+=3;
				}else if(m.getFtr().equals("D")) {
					punteggioStagione++;
				}
			}
		}
		if(stagione.getSeason().compareTo(2017)==0) {
			risultatiAnnataSquadra.add(new RisultatoAnnata(idMapTeam.get(squadra.getTeam()),punteggioStagione,stagione));
		}
		return risultatiAnnataSquadra;
	}
	public void creaGrafo() {
		this.graph=new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		for(RisultatoAnnata r:risultatiAnnataSquadra) {//riempie il grafo con tutti i vertici
			this.graph.addVertex(r.getStagione());
		}
		//creo gli archi
		for(RisultatoAnnata r1:risultatiAnnataSquadra) {
			for(RisultatoAnnata r2:risultatiAnnataSquadra) {
				if(!r1.equals(r2) && !this.graph.containsEdge(this.graph.getEdge(r1.getStagione(), r2.getStagione()))
						&& !this.graph.containsEdge(this.graph.getEdge(r2.getStagione(), r1.getStagione()))) {
					Double peso=(double) (r1.getPunti()-r2.getPunti());
					if(r1.getPunti()>r2.getPunti()) {
						//aggiungo un arco da r1 a r2 con peso r1-r2
						Graphs.addEdgeWithVertices(this.graph, r1.getStagione(), r2.getStagione(),peso);
					}else if(r2.getPunti()>r1.getPunti()){
						//aggiungo un arco da r2 a r1 con peso r2-r1
						Graphs.addEdgeWithVertices(this.graph, r2.getStagione(),r1.getStagione(),-peso);
					}
				}
			}
		}
		
	}
	
	public Integer getNumVertici() {
		return this.graph.vertexSet().size();
	}
	public Integer getNumArchi() {
		return this.graph.edgeSet().size();
	}
	
	public AnnataOro annataOro() {
		Double maxSomma=Double.MIN_VALUE;
		Double entranti=0.0,uscenti=0.0;
		AnnataOro best=null;
		for(Season s:this.graph.vertexSet()) {
			Set<DefaultWeightedEdge> incoming=this.graph.incomingEdgesOf(s);
			for(DefaultWeightedEdge e:incoming) {
				entranti+=this.graph.getEdgeWeight(e);
			}
			Set<DefaultWeightedEdge> outgoing=this.graph.outgoingEdgesOf(s);
			for(DefaultWeightedEdge e:outgoing) {
				uscenti+=this.graph.getEdgeWeight(e);
			}
			if(maxSomma<(entranti-uscenti)) {
				maxSomma=entranti-uscenti;
				best=new AnnataOro(s,maxSomma);
			}
		}
		/*Collections.sort(risultatiAnnataSquadra);
		Season s=risultatiAnnataSquadra.get(0).getStagione();
		Set<DefaultWeightedEdge> incoming=this.graph.incomingEdgesOf(s);
		for(DefaultWeightedEdge e:incoming) {
			entranti+=this.graph.getEdgeWeight(e);
		}
		Set<DefaultWeightedEdge> outgoing=this.graph.outgoingEdgesOf(s);
		for(DefaultWeightedEdge e:outgoing) {
			uscenti+=this.graph.getEdgeWeight(e);
		}
		maxSomma=entranti-uscenti;
		best=new AnnataOro(s,maxSomma);*/
		return best;
	}
	
	public List<RisultatoAnnata> camminoVirtuoso() {
		Collections.sort(risultatiAnnataSquadra);
		List<RisultatoAnnata> parziale= new ArrayList<>();
		ricorsione(parziale);
		return camminoVirtuoso;
	}
	private void ricorsione(List<RisultatoAnnata> parziale) {
		if(parziale.size()>1) {
			camminoVirtuoso.add(parziale.get(0));
			if(camminoVirtuoso.get(camminoVirtuoso.size()-1).getPunti()<parziale.get(parziale.size()-1).getPunti() 
					&& parziale.get(parziale.size()-1).getStagione().getSeason()>camminoVirtuoso.get(camminoVirtuoso.size()-1).getStagione().getSeason()) {
				//caso terminale
				camminoVirtuoso=new ArrayList<>(parziale);
			}
		}
		
		for(RisultatoAnnata r:risultatiAnnataSquadra) {
			if(parziale.size()>1 && r.getPunti()>parziale.get(parziale.size()-1).getPunti()
					&& r.getStagione().getSeason()>parziale.get(parziale.size()-1).getStagione().getSeason()) {
				parziale.add(r);
				ricorsione(parziale);
				parziale.remove(parziale.size()-1);
			}else if(parziale.size()<=1) {
				parziale.add(r);
			}
		}
		
	}
	
	
	
}
