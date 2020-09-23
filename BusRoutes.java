import java.util.*;
import java.util.regex.*;
import java.lang.Math;
import java.lang.String;
import java.util.ArrayList;
import java.io.*;

/*

In this program we make use of:
- a Dijkstra's Algorithm library
- Node & Graph classes

Source: https://github.com/eugenp/tutorials/tree/master/algorithms-miscellaneous-2/src/main/java/com/baeldung/algorithms/ga/dijkstra
Via: baeldung.com/java-dijkstra

All route instances are added to a graph as nodes with a weighted direct edge
from the first node to the second.

Once all routes are added to the graph, the shortest path (using cost instead
of weight/distance) is calculated. The output gives the shortest path for
the top line arguments of the input file.

The program only accepts a file as input.

*/

public class BusRoutes {

  public static void main(String[] args) throws IOException{

    if (args.length < 1) {
        System.out.println("Invalid: No input");
        System.exit(1);
    }

    Scanner scan = null;

    try {
      scan = new Scanner(new FileInputStream(args[0]));
    } catch (FileNotFoundException e) {
      System.out.println("Invalid: No input");
      System.exit(1);
    }

    String origin;
    String dest = "";

    Node originNode = null;
    Node destNode = null;

    Graph g = new Graph();

    // seenRoutes stores routes to ensure no duplicates present
    ArrayList<String> seenRoutes = new ArrayList<String>();
    Set<String> set = new HashSet<String>(seenRoutes);

    // First line (source to dest)
    if (scan.hasNextLine()) {
      String fullRoute = scan.nextLine();

      // First line must be in format - a, b
      //boolean match = Pattern.compile("^[a-zA-Z]+(\\s[a-zA-Z]+)?,(\\s)[a-zA-Z]+(\\s[a-zA-Z]+)?(\\s)*$").matcher(fullRoute).matches();

      // Checks first line input is separated by exactly 2 commas
      String[] split = fullRoute.split(",");
      if (split.length != 2) {
        System.out.println("Invalid: route");
        System.exit(1);
        return;
      }

      origin = fullRoute.split(",")[0].trim().toLowerCase();
      dest = fullRoute.split(",")[1].trim().toLowerCase();

      originNode = new Node(origin);

      g.addNode(originNode);

      // Checks that input is not blank.
    } else {
      System.out.println("Invalid: No input");
      System.exit(1);
      return;
    }

    while (scan.hasNextLine()) {
      String r = scan.nextLine();

      // Checks input is a line of 3 arguments separated by exactly 2 commas
      String[] split = r.split(",");
      //System.out.println(split);
      if (split.length != 3) {
        System.out.println("Invalid: route set");
        System.exit(1);
        return;
      }

      double weight = Double.parseDouble(r.split(",")[2].trim()); // cost

      Node a = new Node(r.split(",")[0].trim().toLowerCase()); // from node
      Node b = new Node(r.split(",")[1].trim().toLowerCase()); // to node

      // For tracking duplicates
      seenRoutes.add(a.getName());
      seenRoutes.add(b.getName());

      // if the graph is empty
      if (g.getNodes().size() == 0) {
        a.addDestination(b, weight); // add b as an adjacency of a
        g.addNode(a); // add a to graph
        g.addNode(b); // add b to graph
      } else { // otherwise

        // Ensures non-origin points are added to the graph.
        // Case for end destination points.
        boolean existingNodeB = false;

        for (Node n: g.getNodes()) {
          if (b.getName().equals(n.getName())) { // if it's not a new node
            existingNodeB = true;
            b = n; // store the existing node address in variable b
          }
        }

        if (!existingNodeB) { // if it's a new node
          g.addNode(b); // add the new node to the graph
        }

        boolean duplicateNodeA = false;
        for (Node n: g.getNodes()) { // for each node in the graph
          if (a.getName().equals(n.getName())) { // if the node already exists
            n.addDestination(b, weight); // add the dest to the existing node
            duplicateNodeA = true;
            break;
          }
        }
        if (!duplicateNodeA) {
          a.addDestination(b, weight); // add the dest to the new node
          g.addNode(a); // add the new node to the graph
        }
      }
      
      //for (Node n : g.getNodes()){
      // System.out.println("A: " + a.getName() + "\n" + "B: " + b.getName());
      //}
    }
    // Checks for duplicate route sets
    set.addAll(seenRoutes);
    seenRoutes.clear();
    seenRoutes.addAll(set);
    //System.out.println("Set: " + set.toString());

    //System.out.println(formattedRoutes);
    if (set.size() < seenRoutes.size()){
      System.out.println("Invalid: Non-unique routes");
      System.exit(1);
      return;
    }

    g = Dijkstra.calculateShortestPathFromSource(g, originNode);

    // Final output
    for (Node n: g.getNodes()) {
      if (n.getName().equals(dest)) {
        // System.out.print("Shortest path: ");
          for (Node s: n.getShortestPath()) {
            // System.out.print(s.getName());
      
            System.out.print(s.getName() + "-");
          }
          System.out.println(n.getName());
      }
    }
  }
}
