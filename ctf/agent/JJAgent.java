package ctf.agent;

import ctf.common.AgentEnvironment;
import ctf.agent.Agent;

import ctf.common.AgentAction;

enum Job {IDLE, FIND_MAP_SIZE}

public class JJAgent extends Agent {
    
    static int nextID = 0;
    int id;
    boolean firstMove = true;
    Job currentJob = Job.IDLE;
    int counter = 0;

    public int getMove(AgentEnvironment inEnvironment) {
    
        if (firstMove) {
           id = nextID;
           nextID++;
           firstMove = false; 
        }

        if (id == nextID - 2)
            currentJob = Job.FIND_MAP_SIZE;

        switch(currentJob) {
        
            case FIND_MAP_SIZE:
                if (inEnvironment.isFlagSouth(inEnvironment.OUR_TEAM, false)) {
                    counter++;
                    return AgentAction.MOVE_SOUTH;
                }
                else if (inEnvironment.isFlagSouth(inEnvironment.OUR_TEAM, true)) {
                    counter++;
                    System.err.println(counter);
                    currentJob = Job.IDLE;
                }
        
        }

        return AgentAction.MOVE_WEST;
    
    }

    private class BoardTile {
    
        int hasBomb;
        boolean blocked;

    
    }

    private class Board {
    
        BoardTile[][] board = new BoardTile[10][10];

    }

}
