package it.polito.tdp.seriea.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.seriea.model.Match;
import it.polito.tdp.seriea.model.Season;
import it.polito.tdp.seriea.model.Team;

public class SerieADAO {

	public List<Season> listAllSeasons(Map<Integer, Season> idMapSeason) {
		String sql = "SELECT season, description FROM seasons";
		List<Season> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				Season s=new Season(res.getInt("season"), res.getString("description"));
				result.add(s);
				idMapSeason.put(s.getSeason(),s);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Team> listTeams(Map<String,Team> idMapTeam) {
		String sql = "SELECT team FROM teams";
		List<Team> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				Team t=new Team(res.getString("team"));
				result.add(t);
				idMapTeam.put(t.getTeam(), t);
			}

			conn.close();
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public List<Match> selezionaSquadra(Team squadra, Map<Integer,Season> idMapSeason,Map<String,Team> idMapTeam){
		String sql="SELECT match_id,Season,matches.`Div`, Date, HomeTeam,AwayTeam, FTHG,FTAG,FTR " + 
				"FROM matches " + 
				"WHERE (HomeTeam=? OR AwayTeam=?) ";
		List<Match> matches= new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, squadra.getTeam());
			st.setString(2, squadra.getTeam());
			ResultSet res = st.executeQuery();

			while (res.next()) {
				matches.add(new Match(res.getInt("match_id"),idMapSeason.get(res.getInt("Season")),
						res.getString("Div"),res.getDate("Date").toLocalDate(),
						idMapTeam.get(res.getString("HomeTeam")),idMapTeam.get(res.getString("AwayTeam")),
						res.getInt("FTHG"),res.getInt("FTAG"),res.getString("FTR")));
			}

			conn.close();
			return matches;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

}

