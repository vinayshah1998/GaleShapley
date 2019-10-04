/*
 * Name: Vinay Shah
 * EID: vss452
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

/**
 * Your solution goes in this class.
 * 
 * Please do not modify the other files we have provided for you, as we will use
 * our own versions of those files when grading your project. You are
 * responsible for ensuring that your solution works with the original version
 * of all the other files we have provided for you.
 * 
 * That said, please feel free to add additional files and classes to your
 * solution, as you see fit. We will use ALL of your additional files when
 * grading your solution.
 */
public class Program1 extends AbstractProgram1 {
    /**
     * Determines whether a candidate Matching represents a solution to the
     * Stable Marriage problem. Study the description of a Matching in the
     * project documentation to help you with this.
     */

    public boolean isStableMatching(Matching allocation) {

        //creating lookups for the user and server pref lists
        //creation time -> O(n^2)
        //lookup time -> O(1)
        ArrayList<ArrayList<Integer>> userPrefLookUp = new ArrayList<>();
        for (int i = 0; i < allocation.getUserPreference().size(); i++){
            userPrefLookUp.add(new ArrayList<>(allocation.getUserPreference().get(i).size()));
            while (userPrefLookUp.get(i).size() < allocation.getUserPreference().get(i).size()) userPrefLookUp.get(i).add(0);
            for (int j = 0; j < allocation.getUserPreference().get(i).size(); j++){
                userPrefLookUp.get(i).set(allocation.getUserPreference().get(i).get(j), j);
            }
        }

        ArrayList<ArrayList<Integer>> serverPrefLookUp = new ArrayList<>();
        for (int i = 0; i < allocation.getServerPreference().size(); i++){
            serverPrefLookUp.add(new ArrayList<>(allocation.getServerPreference().get(i).size()));
            while (serverPrefLookUp.get(i).size() < allocation.getServerPreference().get(i).size()) serverPrefLookUp.get(i).add(0);
            for (int j = 0; j < allocation.getServerPreference().get(i).size(); j++){
                serverPrefLookUp.get(i).set(allocation.getServerPreference().get(i).get(j), j);
            }
        }

        for (int u = 0; u < allocation.getUserMatching().size(); u++) { //loop through the users in the matching
            int s = allocation.getUserMatching().get(u); //corresponding server s to that user u
            if (s == -1) continue; //if user is unmatched, carry on to the next user
//            int u_rank = allocation.getServerPreference().get(s).indexOf(u); //find the rank of u in preference list of s
            int u_rank = serverPrefLookUp.get(s).get(u);
            int u_prime_rank;
            int u_prime;
            for (u_prime_rank = 0; u_prime_rank < u_rank; u_prime_rank++){ //loop through all u_primes ranked higher than u
                u_prime = allocation.getServerPreference().get(s).get(u_prime_rank); //get the u_prime for that server corresponding to that rank
                if (allocation.getUserMatching().get(u_prime).equals(-1)){
                    return false; //return false if u_prime is unmatched
                } else {
                    int s_prime = allocation.getUserMatching().get(u_prime); //find the s_prime that u_prime is matched with
//                    int s_prime_rank = allocation.getUserPreference().get(u_prime).indexOf(s_prime); //find the rank of s_prime in u_prime's list
                    int s_prime_rank = userPrefLookUp.get(u_prime).get(s_prime);
//                    int s_rank = allocation.getUserPreference().get(u_prime).indexOf(s); //find the rank of s in u_prime's list
                    int s_rank = userPrefLookUp.get(u_prime).get(s);
                    if (s_rank < s_prime_rank){
                        return false; //if s is ranked higher than s_prime, return false
                    }
                }
            }
        }

        int unmatched_users = 0;
        for (int i = 0; i < allocation.getUserMatching().size(); i++){
            if (allocation.getUserMatching().get(i).equals(-1)){
                unmatched_users++;
            }
        }
        int matched_users = allocation.getUserCount() - unmatched_users;

        if (matched_users != allocation.totalServerSlots()){
            return false;
        }
        return true;
    }


    public int findIndex (LinkedList<Server> queue, int server_id){
        for (int i = 0; i < queue.size(); i++){
            if (server_id == queue.get(i).getServer_id()){
                return i;
            }
        }
        return -1;
    }

    /**
     * Determines a solution to the Stable Marriage problem from the given input
     * set. Study the project description to understand the variables which
     * represent the input to your solution.
     * 
     * @return A stable Matching.
     */
    @SuppressWarnings("Duplicates")
    public Matching stableMarriageGaleShapley(Matching allocation) {


        //creating a linkedlist of servers with free slots for O(1) first/last access
        LinkedList<Server> freeServerSlots = new LinkedList<>();
        for (int i = 0; i < allocation.getServerCount(); i++){
            if (allocation.getServerSlots().get(i) > 0){
                freeServerSlots.add(new Server(i, allocation.getServerSlots().get(i)));
            }
        }

        ArrayList<Server> allServerSlots = new ArrayList<>();
        for (int i = 0; i < allocation.getServerCount(); i++){
                allServerSlots.add(new Server(i, allocation.getServerSlots().get(i)));
        }

        //keeps track of which user on server preference list was last proposed to. Initialised to 0, and incremented each time server proposes
        ArrayList<Integer> serverTopPreferenceIndex = new ArrayList<>(allocation.getServerCount());
        for (int i = 0; i < allocation.getServerCount(); i++){
            serverTopPreferenceIndex.add(i, 0);
        }

        //keeps track of which server each user is currently matched to. Default value is -1 to show that it is not yet matched
        ArrayList<Integer> userCurrentlyMatchedTo = new ArrayList<>(allocation.getUserCount());
        for (int i = 0; i < allocation.getUserCount(); i++){
            userCurrentlyMatchedTo.add(i,-1);
        }
        if (Objects.equals(allocation.getUserMatching(), null)){
            allocation.setUserMatching(userCurrentlyMatchedTo);
        }

        //creating lookups for the user preference list to do comparisions in O(1) time
        ArrayList<ArrayList<Integer>> userPrefLookUp = new ArrayList<>();
        for (int i = 0; i < allocation.getUserPreference().size(); i++){
            userPrefLookUp.add(new ArrayList<>(allocation.getUserPreference().get(i).size()));
            while (userPrefLookUp.get(i).size() < allocation.getUserPreference().get(i).size()) userPrefLookUp.get(i).add(0);
            for (int j = 0; j < allocation.getUserPreference().get(i).size(); j++){
                userPrefLookUp.get(i).set(allocation.getUserPreference().get(i).get(j), j);
            }
        }

        //creating lookups for the server preference list to do comparisions in O(1) time
        ArrayList<ArrayList<Integer>> serverPrefLookUp = new ArrayList<>();
        for (int i = 0; i < allocation.getServerPreference().size(); i++){
            serverPrefLookUp.add(new ArrayList<>(allocation.getServerPreference().get(i).size()));
            while (serverPrefLookUp.get(i).size() < allocation.getServerPreference().get(i).size()) serverPrefLookUp.get(i).add(0);
            for (int j = 0; j < allocation.getServerPreference().get(i).size(); j++){
                serverPrefLookUp.get(i).set(allocation.getServerPreference().get(i).get(j), j);
            }
        }

//        System.out.println(allocation.getUserPreference());
//        System.out.println(allocation.getServerPreference());

        int s = freeServerSlots.getFirst().getServer_id(); //the server id
        while ((freeServerSlots.size() > 0) && (serverTopPreferenceIndex.get(s) < allocation.getUserCount())){
            if (freeServerSlots.getFirst().getNumFreeSlots() == 0){
                freeServerSlots.removeFirst();
                if (freeServerSlots.size() > 0){
                    s = freeServerSlots.getFirst().getServer_id();
                }
                continue;
            }
            //find a u at the top of the preflist of s
            int u = allocation.getServerPreference().get(s).get(serverTopPreferenceIndex.get(s));

            //increment index by 1
            serverTopPreferenceIndex.set(s, serverTopPreferenceIndex.get(s)+1);

            //if u is not matched with anything
            if (userCurrentlyMatchedTo.get(u).equals(-1)){
                userCurrentlyMatchedTo.set(u, s); //make s and u a matching
                freeServerSlots.getFirst().occupySlot(); //remove one of the free slots
                allServerSlots.get(s).occupySlot();

            } else {
                //else u is matched to some server s'
                int s_prime = userCurrentlyMatchedTo.get(u);
                if (userPrefLookUp.get(u).get(s) < userPrefLookUp.get(u).get(s_prime)){
                    userCurrentlyMatchedTo.set(u, s); //make s and u a matching
                    freeServerSlots.getFirst().occupySlot(); //remove one of the free slots
                    allServerSlots.get(s).occupySlot(); //update main tracker too

                    //if the slots are full on a server, remove from list
//                    if(freeServerSlots.getFirst().getNumFreeSlots() == 0){
//                        freeServerSlots.removeFirst();
//                        if (freeServerSlots.size() > 0){
//                            s = freeServerSlots.getFirst().getServer_id();
//                        }
//                    }


                    if(allServerSlots.get(s_prime).getNumFreeSlots() == 0){
                        allServerSlots.get(s_prime).freeSlot(); //update s_prime slots
                        freeServerSlots.add(allServerSlots.get(s_prime)); //add back to freeServerSlots if slot empty

                    } else {
                        allServerSlots.get(s_prime).freeSlot(); //update s_prime slots
                        freeServerSlots.get(findIndex(freeServerSlots, s_prime)).freeSlot();
                    }
                }
            }


        }

        allocation.setUserMatching(userCurrentlyMatchedTo);
        return allocation;
    }
}
