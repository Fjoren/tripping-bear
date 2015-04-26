package ctf.agent;

import ctf.common.AgentEnvironment;
import ctf.agent.Agent;
import java.util.ArrayList;
import java.util.Hashtable;

import ctf.common.AgentAction;
            
private class Board {

    private class Coordinate {
        int x;
        int y;
        Coordinate(int _x, int _y) {
            x = _x;
            y = _y;
        }
        public boolean equals(Object o) {
            if (x instanceof Coordinate && x == o.x && y = o.y)
                return true;
            else
                return false;
        }
    }

    private class BoardTile {
        private enum Flag {empty,baseSouth,baseNorth}
        Hashtable<Flag,boolean> flags;

        BoardTile(Hashtable<String, boolean> options) {
            flags = new Hashtable<Flag, boolean>();
            if (options.isEmpty())
                flags.put(empty,true);
        }
    }

    Hashtable<Coordinate,BoardTile> board;
    int size = -1;
    boolean complete = false;

    Board() {
        board = new Hashtable<Coordinate,BoardTile>();
    }

    void addBoardTile(int x, int y, Hashtable<String, boolean> hash) {
        board.put(new Coordinate(x,y),new BoardTile(hash));
    }

    void updateMap(int x, int y, AgentEnvironment env) {
        
    }

    void match() {
    
    }

    void normalize() {
    
    }

    void completed() {complete = true;}
    boolean isComplete() {return complete;}

}

