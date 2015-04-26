package ctf.agent;

import ctf.common.AgentEnvironment;
import ctf.agent.Agent;
import java.util.ArrayList;

import ctf.common.AgentAction;
//this is the jobs you can have
enum Job {MAPPING, RANDOM_MOVES, FIND_MAP_SIZE, GUARDING, ATTACKING}

//Jeffrey Jennifer Agent
public class JJAgent extends Agent {
    
    static Board board;
    //which one is which
    int id;
    //this instaniazies it 
    boolean firstMove = true;
    ArrayList<Job> currentJobs = new ArrayList<Job>();
    ArrayList<AgentAction> history = new ArrayList<AgentAction>(); 
    int counter = 0;
    int xDisplacement = 0;
    int yDisplacement = 0;
    
    //top one gets size
    //both are constantly mapping (updates the map) 
    //
    public int getMove(AgentEnvironment inEnvironment) {
        if (firstMove) {
            if (inEnvironment.isBaseSouth(AgentEnvironment.OUR_TEAM, false))
                id = 0;
            if (inEnvironment.isBaseNorth(AgentEnvironment.OUR_TEAM, false))
                id = 1;
            if (id == 0) {
                board = new Board();
                currentJobs.add(Job.MAPPING);
                currentJobs.add(Job.FIND_MAP_SIZE);
            }
            if (id == 1) {
                currentJobs.add(Job.RANDOM_MOVES);
                currentJobs.add(Job.MAPPING);
            }
            firstMove = false;
        }

        //Non Final Jobs
        if (currentJobs.contains(Job.MAPPING)) {
            
        }

        //Final Jobs
        

    }

    //tile on board, stores any information you want it to have
    private class BoardTile {
    
        int hasBomb;
        boolean blocked;
        int hasAgent;
        boolean unknown;
    
    }

    private class Board {
    
        ArrayList<ArrayList<BoardTile>> board = new ArrayList<ArrayList<BoardTile>>();
        boolean complete = false;

        void makeBigger() {
            
        }
        void completed() {complete = true;}
        boolean isComplete() {return complete;}

    }

}
