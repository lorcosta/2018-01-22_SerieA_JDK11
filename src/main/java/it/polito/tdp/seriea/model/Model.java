package it.polito.tdp.seriea.model;

import java.util.ArrayList;
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
	private List<RisultatoAnnata> risultatiAnnataSquadra=new ArrayList<>();
	private Graph<Season,DefaultWeightedEdge> graph;
	
	public Model() {
		idMapSeason=new HashMap<>();
		idMapTeam=new HashMap<>();
		dao.listAllSeasons(idMapSeason);
	}
	public List<Team> getTeams(){
		return dao.listTeams(idMapTeam);
	}
	public List<RisultatoAnnata> selezionaSquadra(Team squadra) {
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
				if(!r1.equals(r2)) {
					Double peso=(double) (r1.getPunti()-r2.getPunti());
					if(peso>0) {
						//aggiungo un arco da r1 a r2 con peso r1-r2
						Graphs.addEdgeWithVertices(this.graph, r1.getStagione(), r2.getStagione(),peso);
					}else {
						//aggiungo un arco da r2 a r1 con peso r2-r1
						Graphs.addEdgeWithVertices(this.graph, r2.getStagione(),r1.getStagione(),-peso);
					}
				}
			}
		}
		
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
		return best;
	}
	
	
}
