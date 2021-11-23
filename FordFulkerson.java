import java.util.*;

public class FordFulkerson {

	public static ArrayList<Integer> pathDFS(Integer source, Integer destination, WGraph graph){
		//Initialize AL for return path and stack we will be using to search through graph
		ArrayList<Integer> path = new ArrayList<Integer>();
		Stack<Integer> dfsStack = new Stack<Integer>();

		//Initialize an integer array that classifies whether ith node is visited and the direct parent of the node i
		int[] visited = new int[graph.getNbNodes()];
		int[] parents = new int[graph.getNbNodes()];

		ArrayList<Edge> edges = graph.getEdges();

		int currNode;
		Edge currEdge = null;

		//push source node to the stack to initialize dfs process
		dfsStack.push(source);

		//pop the source node in the graph and add it to the beginning of the path
		currNode = dfsStack.pop();
		path.add(source);
		visited[source] = 1;
		parents[source] = -1; //Let the parent of the source node be -1

		//Push adjacent edges onto the stack and mark their parents as the source
		for(Edge e: edges){
			if(e.nodes[0] == currNode){
				dfsStack.push(e.nodes[1]);
				parents[e.nodes[1]] = currNode;
			}
		}

		//Iterate through the stack until it is empty of we have reached a point where we are stuck
		//Either for lack of capacity on edges or reaching the sink node
		while(!dfsStack.empty() && currNode != destination){
			//pop the current node in the graph that we want to check and compute edge based on parent of currNode
			currNode = dfsStack.pop();
			currEdge = graph.getEdge(parents[currNode], currNode);

			if(visited[currNode] == 0 && currEdge != null && currEdge.weight > 0){
				//Mark the current node as visited and add the current node to the path
				visited[currNode] = 1;
				path.add(currNode);

				//Push adjacent edges onto the stack
				for(Edge e: edges){
					if(e.nodes[0] == currNode){
						dfsStack.push(e.nodes[1]);
						parents[e.nodes[1]] = currNode;
					}
				}

			}
			//If we have a 0 weight edge and the current node is the destination we want to update
			//Our current node so that we can pass through the while condition and continue iterating through the stack
			else if(currEdge.weight == 0 && currNode == destination){
				currNode = dfsStack.peek();
				continue;
			}
		}

		if(currNode == destination) dfsStack.push(destination);
		else {return null;}

		return path;
	}

	private static int computeMinCap(ArrayList<Integer> path, WGraph graph){
		//Let the minimum capacity begin as the current remaining residual capacity
		int minCap = graph.getEdge(path.get(0),path.get(1)).weight;
		int currCap = 0;
		for(int i =1; i< path.size(); ++i){
			//Compute value for current capacity of the edge in the path
			if(i != path.size()-1 && graph.getEdge(path.get(i),path.get(i+1)) != null){
				currCap = graph.getEdge(path.get(i),path.get(i+1)).weight;
			}

			//if current residual capacity is less than the minCap, update minCap
			if(currCap < minCap){
				minCap = currCap;
			}
		}

		return minCap;
	}

	private static void augmentPath(ArrayList<Integer> path, WGraph residualGraph, WGraph graph, int minCap){

		//Update the weights/capacities for the rest of the nodes on the current path
		for(int i=0; i< path.size(); ++i){
			if(i != path.size()-1 && residualGraph.getEdge(path.get(i), path.get(i + 1)) != null) {
				residualGraph.getEdge(path.get(i), path.get(i + 1)).weight -= minCap;
				residualGraph.getEdge(path.get(i + 1), path.get(i)).weight += minCap;
				graph.getEdge(path.get(i), path.get(i + 1)).weight -= minCap;
			}
		}
	}

	public static String fordfulkerson(WGraph graph){
		String answer = "";
		int maxFlow = 0;

		//Create a residual graph based on input graph
		WGraph residualG = new WGraph(graph);

		//For each edge in the copy graph residualG we need to create a backwards edge that has a current weight/cap of 0
		for(Edge e: graph.getEdges()){
			residualG.addEdge(new Edge(e.nodes[1],e.nodes[0], 0));
		}

		//Compute the first augmenting path of the current graph
		ArrayList<Integer> path = pathDFS(graph.getSource(), graph.getDestination(), graph);
		
		while(path != null){

			//Compute the min cap for the current path
			int minCap = computeMinCap(path, residualG);

			//Augment path with given minCap
			if(minCap == 0) {
				path = pathDFS(graph.getSource(), graph.getDestination(), graph);
				continue; //If the minimum capacity is 0, then move to the next path
			}
			augmentPath(path, residualG, graph, minCap);

			maxFlow += minCap; //Update the max flow by the amount the current path was augmented by i.e. the minCap of current path

			path = pathDFS(graph.getSource(), graph.getDestination(), graph);
		}

		answer += maxFlow + "\n" + graph.toString();	
		return answer;
	}
	

	 public static void main(String[] args){
		 String file = "src/ff2.txt";
		 WGraph g = new WGraph(file);
		 System.out.println(fordfulkerson(g));
	 }
}

