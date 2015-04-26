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
            if (x instanceof Coordinate && x == (Coordinate)o.x && y = (Coordinate)o.y)
                return true;
            else
                return false;
        }
    }

    private class BoardTile {
        Hashtable<String,boolean> flags;

        BoardTile(Hashtable<String, boolean> options) {
            flags = new Hashtable<String, boolean>();
            update(options);
        }

        BoardTile() {
            flags = new Hashtable<String,boolean>();
        }

        void update(HashTable<String,boolean> options) {
            for (Enumeration<E> e = options.keys(); e.hasMoreElements;) {
                Object temp = e.nextElement();
                flags.put((String)temp,options.get((String)temp));
            }
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

    void addBoardTile(int x, int y, BoardTile tile) {
        board.put(new Coordinate(x,y),tile);
    }

    void addBoardTile(int x, int y) {
        board.put(new Coordinate(x,y),new BoardTile());
    }

    void updateMap(int x, int y, AgentEnvironment env) {
        BoardTile current = board.get(new Coordinate(x,y));
        //Shouldn't ever occur
        if (current == null) {
            board.addBoardTile(x,y,new BoardTile());
        }
        //Check update current tile
        Hashtable<String,boolean> upd = new Hashtable<String,boolean>();
        if (env.isBaseNorth(env.OUR_TEAM,false))
            upd.put("baseNorth",true);
        if (env.isBaseSouth(env.OUR_TEAM,true))
            upd.put("baseSouth",true);
        current.update(upd);
        //Add surrounding tiles
        upd.clear();
        //North
        if (board.get(new Coordinate(x,y+1)) == null)
            board.addBoardTile(x,y+1);
        if (env.)
    }

    void placeBomb(int x, int y) {
        
    }

    void match() {
    
    }

    void normalize() {
    
    }

    void completed() {complete = true;}
    boolean isComplete() {return complete;}

}

