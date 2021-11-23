package pugbot.core.commands;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import pugbot.Utils;
import pugbot.core.entities.Game;
import pugbot.core.entities.PUGTeam;
import pugbot.core.entities.QueueManager;
import pugbot.core.exceptions.BadArgumentsException;
import pugbot.core.exceptions.InvalidUseException;

import java.util.ArrayList;

public class CmdSetTeams extends Command {
    /**
     * Set the teams manually by hand
     * <p>
     * Expected command:
     * !setteams @player1 ... @playerN vs @playerN+1 ... @playerM
     */
    @Override
    public Message execCommand(Member caller, String[] args) {
        QueueManager qm = server.getQueueManager();
        ArrayList<Member> firstTeamMembers = new ArrayList<>();
        ArrayList<Member> secondTeamMembers = new ArrayList<>();
        Game game = qm.getPlayersGame(caller);

        if (game == null) {
            throw new InvalidUseException(String.format("%s is not in-game", caller.getEffectiveName()));
        }
        if (!(game.isCaptain(caller))) {
            throw new InvalidUseException("You must be a captain to use this command");
        }
        if (game.getStatus() != Game.GameStatus.PICKING) {
            throw new InvalidUseException("Game must be currently picking");
        }

        int maxPlayers = game.getParentQueue().getMaxPlayers();
        // Add 1 to account for the word "vs" in the middle
        if (args.length != maxPlayers + 1) {
            throw new BadArgumentsException();
        }

        for (int i = 0; i < maxPlayers / 2; i++) {
            Member member = server.getMember(args[i]);
            if (!game.containsPlayer(member)) {
                throw new InvalidUseException(String.format("Player %s is not in game", member.getEffectiveName()));
            }
            firstTeamMembers.add(member);
        }

        // Add 1 to account for the word "vs" in the middle
        for (int i = (maxPlayers / 2) + 1; i < maxPlayers + 1; i++) {
            Member member = server.getMember(args[i]);
            if (!game.containsPlayer(member)) {
                throw new InvalidUseException(String.format("Player %s is not in game", member.getEffectiveName()));
            }
            secondTeamMembers.add(member);
        }

        PUGTeam team0 = game.getPUGTeams()[0];
        PUGTeam team1 = game.getPUGTeams()[1];
        int i = 0;
        if (firstTeamMembers.contains(team0.getCaptain())) {
            // firstTeam corresponds to team0
            for (Member member : firstTeamMembers) {
                if (member != team0.getCaptain()) {
                    team0.addPlayer(member, i);
                    i++;
                }
            }
            i = 0;
            for (Member member : secondTeamMembers) {
                if (member != team1.getCaptain()) {
                    team1.addPlayer(member, i);
                    i++;
                }
            }
        } else {
            // secondTeam corresponds to team0
            for (Member member : secondTeamMembers) {
                if (member != team0.getCaptain()) {
                    team0.addPlayer(member, i);
                    i++;
                }
            }
            i = 0;
            for (Member member : firstTeamMembers) {
                if (member != team1.getCaptain()) {
                    team1.addPlayer(member, i);
                    i++;
                }
            }
        }

        game.setTeamsComplete();
        return Utils.createMessage("Teams have been set!");
    }

    @Override
    public boolean isAdminRequired() {
        return false;
    }

    @Override
    public boolean isGlobalCommand() {
        return false;
    }

    @Override
    public String getName() {
        return "SetTeams";
    }

    @Override
    public String getDescription() {
        return "Set the teams for a game";
    }

    @Override
    public String getHelp() {
        return getBaseCommand() + " <team1player1> <team1player2> <team1player3> <team1player4> <team1player5> vs <team2player1> <team2player2> <team2player3> <team2player4> <team2player5>";
    }

}
