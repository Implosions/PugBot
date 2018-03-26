package core.commands;

import java.sql.ResultSet;
import java.sql.SQLException;

import core.Constants;
import core.Database;
import core.entities.Server;
import core.util.Utils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CmdPugServers extends Command{
	
	public CmdPugServers(){
		this.name = Constants.PUGSERVERS_NAME;
		this.helpMsg = Constants.PUGSERVERS_HELP;
		this.description = Constants.PUGSERVERS_DESC;
		
	}

	@Override
	public Message execCommand(Server server, Member member, String[] args) {
		if(args.length < 2){
			String output;
			if(args.length == 0){
				output = getServers(server.getGuild().getId(), null);
			}else{
				output = getServers(server.getGuild().getId(), args[0].toLowerCase());
			}
			this.response = Utils.createMessage(null, output, true);
		}else{
			this.response = Utils.createMessage("Error!", "Too many arguments", false);
		}
		
		return response;
	}
	
	public String getServers(String serverId, String region){
		String joinLink;
		String message = "";
		ResultSet rs;
		if(region != null){
			rs = Database.queryGetPugServers(Long.valueOf(serverId), region);
		}else{
			rs = Database.queryGetPugServers(Long.valueOf(serverId));
		}
		try{
			while(rs.next()){
				joinLink = String.format("steam://rungameid/%d//%s%%3Fpassword=%s%%20%d", rs.getInt("gameId"), rs.getString("ip"), rs.getString("password"), rs.getInt("port"));
				message += String.format("%s (%s) - [Join](%s)%n%n", rs.getString("name"), rs.getString("region").toUpperCase(), joinLink);
			}
			if(message.isEmpty()){
				message = "N/A";
			}
			rs.close();
			return message;
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		return null;
	}
}


