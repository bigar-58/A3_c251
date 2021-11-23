import java.util.*;

public class BellmanFord{

    private int[] distances = null;
    private int[] predecessors = null;
    private int source;

    class BellmanFordException extends Exception{
        public BellmanFordException(String str){
            super(str);
        }
    }

    class NegativeWeightException extends BellmanFordException{
        public NegativeWeightException(String str){
            super(str);
        }
    }

    class PathDoesNotExistException extends BellmanFordException{
        public PathDoesNotExistException(String str){
            super(str);
        }
    }

    BellmanFord(WGraph g, int source) throws NegativeWeightException{
        /* Constructor, input a graph and a source
         * Computes the Bellman Ford algorithm to populate the
         * attributes 
         *  distances - at position "n" the distance of node "n" to the source is kept
         *  predecessors - at position "n" the predecessor of node "n" on the path
         *                 to the source is kept
         *  source - the source node
         *
         *  If the node is not reachable from the source, the
         *  distance value must be Integer.MAX_VALUE
         */

        //Initialize variables for the distances and predecessors arrays
        this.distances = new int[g.getNbNodes()];
        this.predecessors = new int[g.getNbNodes()];

        for(int i = 0; i < g.getNbNodes(); ++i){
            this.distances[i] = Integer.MAX_VALUE; //Initialize all of the distances to infinity
            predecessors[i] = -2; // Let -2 indicate that predecessor has not yet been found
        }

        //Initialize the values for the source node
        this.source = source;
        this.distances[this.source] = 0;
        this.predecessors[this.source] = -1; // Let -1 as predecessor indicate that the node is the source

        ArrayList<Edge> edges = g.getEdges();

        //Using condition that we iterate a maximum of |V|-1 times, relax edges of the graph
        for(int i = 0; i < g.getNbNodes()-1; ++i){
            //Relax each edge in the graph
            for(int j = 0; j< edges.size(); ++j){
                //Edge relaxation property i.e. check if moving along edge yields lower length
                if(this.distances[edges.get(j).nodes[0]] + edges.get(j).weight < this.distances[edges.get(j).nodes[1]]){

                    //Update optimal predecessor of currently adjacent node
                    this.predecessors[edges.get(j).nodes[1]] = edges.get(j).nodes[0];
                    
                    //Relax edge to satisfy shorter distance edge
                    this.distances[edges.get(j).nodes[1]] = this.distances[edges.get(j).nodes[0]] + edges.get(j).weight;
                }
            }
        }

        /*
        Bellman-Ford Algorithm must converge after visiting all vertices and relaxing all edges i.e. we have
        reached an optimal solution. Since we have an optimal solution, we can run the process again, but immediately
        terminate if we reach an edge that can actually be relaxed (contradiction we have already reached optimal solution)
         */

        boolean pathValid = true;
        for(int i = 0; i < g.getNbNodes()-1; ++i){
            //Relax each edge in the graph
            for(int j = 0; j< edges.size(); ++j){
                //Edge relaxation property i.e. check if moving along edge yields lower length
                if(this.distances[edges.get(j).nodes[0]] + edges.get(j).weight < this.distances[edges.get(j).nodes[1]]){

                    //If an edge can be further relaxed after running, it is a negative cycle or unreachable and should have MAX distance
                    this.distances[edges.get(j).nodes[1]] = Integer.MAX_VALUE;
                    pathValid = false;

                }
            }
        }

        //If the path is invalid because of a negative weight cycle throw an exception
        if(!pathValid){
            throw new NegativeWeightException("Negative-Weight cycle present in graph!");
        }




    }

    public int[] shortestPath(int destination) throws PathDoesNotExistException{
        /* Returns the list of nodes along the shortest path from 
         * the object source to the input destination
         * If not path exists an Error is thrown
         */

        //Create a shortestPath array that can be up to length of the amount of nodes in the graph
        ArrayList<Integer> shortestPathR = new ArrayList<Integer>();

        int currNode = destination;
        //Use predecessor array to find shortest path from the destination node back to the source
        while(this.predecessors[currNode] != -1 && this.distances[currNode] != Integer.MAX_VALUE){

            shortestPathR.add(currNode);//Add the current node to the path
            currNode = this.predecessors[currNode]; //Update the current node to be the optimal parent of current node

        }

        //If the distance of the current node is infinity after the BellmanFord Algorithm, it must be unreachable or a neg cycle
        if(this.distances[currNode] == Integer.MAX_VALUE || this.predecessors[currNode] != -1){
            throw new PathDoesNotExistException("There does not exist a path from source to input destination");
        }

        //If the current node at the end of traversing the path backwards is the source, we have a valid path and can add the source to the path
        if(currNode == this.source) {
            shortestPathR.add(currNode);//Add the source node to the path
            Collections.reverse(shortestPathR); // Reverse the list because we were traversing backwards
        }
        int[] shortestPath = new int[shortestPathR.size()]; //Create array to return valid input

        for(int i = 0; i < shortestPathR.size(); ++i){
            shortestPath[i] = shortestPathR.get(i);
        }

        return shortestPath;
    }

    public void printPath(int destination){
        /* Print the path in the format s->n1->n2->destination
         * if the path exists, else catch the Error and 
         * prints it
         */
        try {
            int[] path = this.shortestPath(destination);
            for (int i = 0; i < path.length; i++){
                int next = path[i];
                if (next == destination){
                    System.out.println(destination);
                }
                else {
                    System.out.print(next + "-->");
                }
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    public static void main(String[] args){
        String file = "src/bf1.txt";
        WGraph g = new WGraph(file);
        try{
            BellmanFord bf = new BellmanFord(g, g.getSource());
            bf.printPath(g.getDestination());
        }
        catch (Exception e){
            System.out.println(e);
        }

   } 
}

