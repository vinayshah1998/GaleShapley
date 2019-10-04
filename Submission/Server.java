/*
 * Name: Vinay Shah
 * EID: vss452
 */

public class Server {
    private int server_id;
    private int numFreeSlots;

    public Server (int server_id, int numFreeSlots){
        this.server_id = server_id;
        this.numFreeSlots = numFreeSlots;
    }

    public void occupySlot(){
        if (numFreeSlots > 0) this.numFreeSlots--;
    }

    public void freeSlot(){
        this.numFreeSlots++;
    }

    public int getNumFreeSlots() {
        return numFreeSlots;
    }

    public int getServer_id(){
        return server_id;
    }
}
