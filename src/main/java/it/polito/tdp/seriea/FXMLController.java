package it.polito.tdp.seriea;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.seriea.model.AnnataOro;
import it.polito.tdp.seriea.model.Model;
import it.polito.tdp.seriea.model.RisultatoAnnata;
import it.polito.tdp.seriea.model.Team;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

public class FXMLController {
	
	private Model model;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ChoiceBox<Team> boxSquadra;

    @FXML
    private Button btnSelezionaSquadra;

    @FXML
    private Button btnTrovaAnnataOro;

    @FXML
    private Button btnTrovaCamminoVirtuoso;

    @FXML
    private TextArea txtResult;

    @FXML
    void doSelezionaSquadra(ActionEvent event) {
    	this.txtResult.clear();
    	Team team=this.boxSquadra.getValue();
    	if(team==null) {
    		this.txtResult.appendText("ATTENZIONE! Nessuna scelta effettuata per il campo squadra.\n");
    		return;
    	}
    	List<RisultatoAnnata> risultati=model.selezionaSquadra(team);
    	this.txtResult.appendText("Numero di punti per ogni annata:\n");
    	for(RisultatoAnnata r:risultati) {
    		this.txtResult.appendText(r+"\n");
    	}
    }

    @FXML
    void doTrovaAnnataOro(ActionEvent event) {
    	this.txtResult.clear();
    	model.creaGrafo();
    	AnnataOro best=model.annataOro();
    	this.txtResult.appendText("L'annata d'oro per la squadra selezionata e': \n");
    	this.txtResult.appendText(best.toString());
    }

    @FXML
    void doTrovaCamminoVirtuoso(ActionEvent event) {

    }
    
    void loadData() {
    	this.boxSquadra.getItems().addAll(model.getTeams());
    }
    @FXML
    void initialize() {
        assert boxSquadra != null : "fx:id=\"boxSquadra\" was not injected: check your FXML file 'SerieA.fxml'.";
        assert btnSelezionaSquadra != null : "fx:id=\"btnSelezionaSquadra\" was not injected: check your FXML file 'SerieA.fxml'.";
        assert btnTrovaAnnataOro != null : "fx:id=\"btnTrovaAnnataOro\" was not injected: check your FXML file 'SerieA.fxml'.";
        assert btnTrovaCamminoVirtuoso != null : "fx:id=\"btnTrovaCamminoVirtuoso\" was not injected: check your FXML file 'SerieA.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'SerieA.fxml'.";

    }

	public void setModel(Model model) {
		this.model = model;
		loadData();
	}
}
