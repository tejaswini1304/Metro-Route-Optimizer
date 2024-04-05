
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Scanner;

//  import com.google.maps.GeoApiContext;
//  import com.google.maps.TrafficApi;
//  import com.google.maps.model.LatLng;
//  import com.google.maps.model.TrafficFlowSegment;

public class PuneMetro {

    public static Scanner sc = new Scanner(System.in);

    public PuneMetro() {
        stations = new HashMap<>();
    }

    public static class Vertex {
        HashMap<String, Integer> neighbour = new HashMap<>();
    }

    static HashMap<String, Vertex> stations;

    public static int vertexNum(String name) {
        return stations.size();
    }

    public static boolean containStation(String name) {
        return stations.containsKey(name);
    }

    public static void addVertex(String name) {
        Vertex v = new Vertex();
        stations.put(name, v);
    }

    public static void removeVertex(String name) {
        Vertex v = stations.get(name);
        ArrayList<String> keys = new ArrayList<>(v.neighbour.keySet());

        for (String key : keys) {
            Vertex nv = stations.get(key);
            nv.neighbour.remove(name);
        }

        stations.remove(name);
    }

    public static int edgeNum(String name) {
        ArrayList<String> keys = new ArrayList<>(stations.keySet());
        int count = 0;

        for (String key : keys) {
            Vertex v = stations.get(key);
            count = count + v.neighbour.size();
        }

        return count / 2;
    }

    public static boolean containEdge(String name1, String name2) {
        Vertex v1 = stations.get(name1);
        Vertex v2 = stations.get(name2);

        if (v1 == null || v2 == null || !v1.neighbour.containsKey(name2)) {
            return false;
        } else {
            return true;
        }
    }

    public static void addEdge(String name1, String name2, int value) {
        Vertex v1 = stations.get(name1);
        Vertex v2 = stations.get(name2);

        if (v1 == null || v2 == null || v1.neighbour.containsKey(name2)) {
            return;
        } else {
            v1.neighbour.put(name2, value);
            v2.neighbour.put(name1, value);

            v2.neighbour.put(name1, value);
            v1.neighbour.put(name2, value);
        }
    }

    public static void removeEdge(String name1, String name2) {
        Vertex v1 = stations.get(name1);
        Vertex v2 = stations.get(name2);

        if (v1 == null || v2 == null || !v1.neighbour.containsKey(name2)) {
            return;
        } else {
            v1.neighbour.remove(name2);
            v2.neighbour.remove(name1);
        }
    }

    public static boolean hasPath(String name1, String name2, HashMap<String, Boolean> visit) {
        if (containEdge(name1, name2) || containEdge(name2, name1)) {
            return true;
        }

        visit.put(name1, true);
        Vertex v = stations.get(name1);
        ArrayList<String> neighbors = new ArrayList<>(v.neighbour.keySet());

        for (String neighbor : neighbors) {
            if (!visit.containsKey(neighbor)) {
                if (hasPath(neighbor, name2, visit)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static class DjPair implements Comparable<DjPair> {
        String name;
        int distCost;

        @Override
        public int compareTo(DjPair o) {
            return o.distCost - this.distCost;
        }
    }

    public static int dijkstra(String src, String des, boolean nan) {
        int val = 0;
        HashMap<String, DjPair> unVisited = new HashMap<>();
        PriorityQueue<DjPair> queue = new PriorityQueue<>((a, b) -> a.distCost - b.distCost);

        // Initialize all vertices with maximum distance except for the source vertex
        for (String key : stations.keySet()) {
            DjPair newPair = new DjPair();
            newPair.name = key;
            newPair.distCost = Integer.MAX_VALUE; // Set distance to infinity initially
            if (key.equals(src)) {
                newPair.distCost = 0; // Set distance to 0 for the source vertex
            }
            queue.add(newPair); // Add the vertex to the priority queue
            unVisited.put(key, newPair); // Add the vertex to the unvisited map
        }

        // Dijkstra's algorithm iteration
        while (!queue.isEmpty()) {
            DjPair curr = queue.poll(); // Get the vertex with the smallest distance from the queue
            if (curr.name.equals(des)) {
                val = curr.distCost; // Update the final distance value if the destination vertex is reached
                break; // Exit the loop since the shortest path to the destination is found
            }
            unVisited.remove(curr.name); // Mark the current vertex as visited
            Vertex v = stations.get(curr.name); // Get the current vertex object

            // Explore neighbors of the current vertex
            for (String num : v.neighbour.keySet()) {
                if (unVisited.containsKey(num)) { // Check if the neighbor is unvisited
                    int oldDist = unVisited.get(num).distCost; // Get the old distance of the neighbor
                    Vertex k = stations.get(curr.name); // Get the current vertex object again
                    int newDist;
                    if (nan) {
                        newDist = curr.distCost + 120 + 40 * k.neighbour.get(num);
                        // Calculate new distance with additional time cost (if nan is true)
                    } else {
                        newDist = curr.distCost + k.neighbour.get(num);
                        // Calculate new distance without additional time cost
                    }
                    if (newDist < oldDist) {
                        DjPair gp = unVisited.get(num); // Get the DjPair object for the neighbor
                        gp.distCost = newDist; // Update the distance of the neighbor
                        queue.remove(gp); // Remove and re-insert the neighbor in the priority queue
                        queue.add(gp);
                    }
                }
            }
        }
        return val; // Return the final shortest distance value
    }

    private static class StationInfo {
        String stationName;
        String pathtrav;
        int minDistance;
    }

    public static void getShortestPath(BufferedReader input) throws IOException {
        if (stations.isEmpty()) {
            System.out.println("No stations available.");
            return;
        }

        System.out.println("Enter the Source Station:");
        String sourceStation = input.readLine();

        System.out.println("Enter the Destination Station:");
        String destinationStation = input.readLine();

        if (!containStation(sourceStation) || !containStation(destinationStation)) {
            System.out.println("Invalid stations.");
            return;
        }

        HashMap<String, Boolean> processed = new HashMap<>();
        if (!hasPath(sourceStation, destinationStation, processed)) {
            System.out.println("No path available between the stations.");
            return;
        }

        ArrayList<String> pathStations = get_Interchanges(getMinimumDistance(sourceStation, destinationStation));
        int len = pathStations.size();

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("SOURCE STATION : " + sourceStation);
        System.out.println("DESTINATION STATION : " + destinationStation);
        System.out.println("DISTANCE : " + pathStations.get(len - 1));
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        StringBuilder pathBuilder = new StringBuilder("PATH: ");
        for (int i = 0; i < len - 3; i++) {
            pathBuilder.append(pathStations.get(i)).append(" --> ");
        }
        pathBuilder.append(pathStations.get(len - 3)).append(" ==> END");
        System.out.println(pathBuilder.toString());
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }


    public static String getMinimumDistance(String source, String destination) {
        int minDistance = Integer.MAX_VALUE;
        String shortestPath = "";
        HashMap<String, Boolean> processedStations = new HashMap<>();
        LinkedList<StationInfo> stack = new LinkedList<>();
        StationInfo startStation = new StationInfo();
        startStation.stationName = source;
        startStation.pathtrav = source + " ";
        startStation.minDistance = 0;
        stack.addFirst(startStation);

        while (!stack.isEmpty()) {
            StationInfo currentStation = stack.removeFirst();
            if (processedStations.containsKey(currentStation.stationName)) {
                continue;
            }
            processedStations.put(currentStation.stationName, true);
            if (currentStation.stationName.equals(destination)) {
                int distance = currentStation.minDistance;
                if (distance < minDistance) {
                    shortestPath = currentStation.pathtrav;
                    minDistance = distance;
                }
                continue;
            }
            Vertex currentVertex = stations.get(currentStation.stationName);
            ArrayList<String> neighbors = new ArrayList<>(currentVertex.neighbour.keySet());

            for (String neighbor : neighbors) {
                if (!processedStations.containsKey(neighbor)) {
                    StationInfo nextStation = new StationInfo();
                    nextStation.stationName = neighbor;
                    nextStation.pathtrav = currentStation.pathtrav + neighbor + " ";
                    nextStation.minDistance = currentStation.minDistance + currentVertex.neighbour.get(neighbor);
                    stack.addFirst(nextStation);
                }
            }
        }
        shortestPath = shortestPath + Integer.toString(minDistance);
        return shortestPath;
    }

    public static void getShortestTime(BufferedReader input) throws IOException {
        if (stations.isEmpty()) {
            System.out.println("No stations available.");
            return;
        }

        ArrayList<String> keys = new ArrayList<>(stations.keySet());
        printCodelist(); // Assuming printCodelist is a method to display station codes

        System.out.println("Enter the Serial Number of Source Station:");
        int srcSerial = Integer.parseInt(input.readLine()) - 1;
        if (srcSerial < 0 || srcSerial >= keys.size()) {
            System.out.println("Invalid source station number.");
            return;
        }
        String src = keys.get(srcSerial);

        System.out.println("Enter the Serial Number of Destination Station:");
        int dstSerial = Integer.parseInt(input.readLine()) - 1;
        if (dstSerial < 0 || dstSerial >= keys.size()) {
            System.out.println("Invalid destination station number.");
            return;
        }
        String dst = keys.get(dstSerial);

        HashMap<String, Boolean> processed = new HashMap<>();
        if (!containStation(src) || !containStation(dst) || !hasPath(src, dst, processed)) {
            System.out.println("Invalid stations or no path available.");
        } else {
            int distanceInMeters = dijkstra(src, dst, false);
            double totalTimeSeconds = (double) distanceInMeters / 27;
            double totalTimeMinutes = totalTimeSeconds / 60;
            System.out.println("Shortest Time from " + src + " TO " + dst + " IS-----> "
                    + String.format("%.2f", totalTimeMinutes) + " minutes\n");
        }
    }

    public static void getShortestDistance(BufferedReader input) throws IOException {
        if (stations.isEmpty()) {
            System.out.println("No stations available.");
            return;
        }

        ArrayList<String> keys = new ArrayList<>(stations.keySet());
        printCodelist(); // Assuming printCodelist is a method to display station codes

        System.out.println("Enter the Serial Number of Source Station:");
        int srcSerial = Integer.parseInt(input.readLine()) - 1;
        if (srcSerial < 0 || srcSerial >= keys.size()) {
            System.out.println("Invalid source station number.");
            return;
        }
        String src = keys.get(srcSerial);

        System.out.println("Enter the Serial Number of Destination Station:");
        int dstSerial = Integer.parseInt(input.readLine()) - 1;
        if (dstSerial < 0 || dstSerial >= keys.size()) {
            System.out.println("Invalid destination station number.");
            return;
        }
        String dst = keys.get(dstSerial);

        HashMap<String, Boolean> h = new HashMap<>();
        if (!containStation(src) || !containStation(dst) || !hasPath(src, dst, h)) {
            System.out.println("Invalid stations or no path available.");
        } else {
            System.out.println("Shortest DISTANCE from " + src + " TO " + dst + " IS-----> "
                    + dijkstra(src, dst, false) + " meters\n");
        }
    }

    public static void printCodelist() {
        System.out.println("List of stations:\n");
        ArrayList<String> key = new ArrayList<>(stations.keySet());
        int i = 1;
        for (String keys : key) {
            System.out.println(i + ". " + keys);
            i++;
        }
    }

    public static void displayStations() {
        if (stations.isEmpty()) {
            System.out.println("No stations available.");
            return;
        }
        System.out.println("List of all stations:");
        int i = 1;
        for (String station : stations.keySet()) {
            System.out.println(i + ". " + station);
            i++;
        }
    }


    public static ArrayList<String> get_Interchanges(String str) {
        ArrayList<String> arr = new ArrayList<>();
        String res[] = str.split(" ");
        arr.add(res[0]);
        int count = 0;
        for (int i = 1; i < res.length - 1; i++) {
            int index = res[i].indexOf('~');
            String s = res[i].substring(index + 1);
            if (s.length() == 2) {
                String prev = res[i - 1].substring(res[i - 1].indexOf('~') + 1);
                String next = res[i + 1].substring(res[i + 1].indexOf('~') + 1);
                if (prev.equals(next))
                    arr.add(res[i]);
                else {
                    arr.add(res[i] + " ==> " + res[i + 1]);
                    i++;
                    count++;
                }
            } else
                arr.add(res[i]);
        }
        arr.add(Integer.toString(count));
        arr.add(res[res.length - 1]);
        return arr;
    }

    public static void displayMap() {
        if (stations.isEmpty()) {
            System.out.println("No stations available.");
            return;
        }
        System.out.println("PUNE METRO MAP:");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("STATION NAME\t\t\tDISTANCE (in meters)");
        System.out.println("-------------\t\t\t------------------");
        for (String key : stations.keySet()) {
            String str = key + "==>\n";
            Vertex v = stations.get(key);
            for (String num : v.neighbour.keySet()) {
                str += "\t" + num + "\t";
                str += (num.length() < 16) ? "\t" : "";
                str += (num.length() < 8) ? "\t" : "";
                str += v.neighbour.get(num) + "\n";
            }
            System.out.println(str);
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    //  public static void getTrafficUpdate(double latitude, double longitude) {
    //      // Provide your API key
    //      String apiKey = "YOUR_API_KEY";

    //      // Initialize GeoApiContext with your API key
    //      GeoApiContext context = new GeoApiContext.Builder()
    //              .apiKey(apiKey)
    //              .build();

    //      // Create LatLng object with the provided latitude and longitude
    //      LatLng location = new LatLng(latitude, longitude);

    //      try {
    //          // Use TrafficApi to get real-time traffic data for the provided location
    //          List<TrafficFlowSegment> trafficFlowSegments = TrafficApi.newRequest(context)
    //                  .location(location)
    //                  .await();

    //          // Print real-time traffic information
    //          System.out.println("Real-time Traffic Information:");
    //          for (TrafficFlowSegment segment : trafficFlowSegments) {
    //              System.out.println("Road Name: " + segment.getRoadName());
    //              System.out.println("Current Speed: " + segment.getCurrentSpeed() + " meters per second");
    //              System.out.println("Free Flow Speed: " + segment.getFreeFlowSpeed() + " meters per second");
    //              System.out.println("Delay: " + segment.getDelay() + " seconds");
    //              System.out.println("Distance: " + segment.getLength() + " meters");
    //              System.out.println("----------------------------------");
    //          }
    //      } catch (Exception e) {
    //          e.printStackTrace();
    //      }
    //  }

    public static void Metro_Map(PuneMetro p) {
        p.addVertex("Vanaz");
        p.addVertex("Anand Nagar");
        p.addVertex("Ideal Colony");
        p.addVertex("Nal Stop");
        p.addVertex("Garware College");
        p.addVertex("Deccan");
        p.addVertex("C.Sambhaji Park");
        p.addVertex("PMC");
        p.addVertex("Civil Court");
        p.addVertex("Budhwar Peth");
        p.addVertex("Mandai");
        p.addVertex("Swargate");
        p.addVertex("Pune Railway Station");
        p.addVertex("Ruby Hall Clinic");
        p.addVertex("Bund Garden");
        p.addVertex("Yerwada");
        p.addVertex("Kalyani Nagar");
        p.addVertex("Ramwadi");
        p.addVertex("Shivaji Nagar");
        p.addVertex("Range Hills");
        p.addVertex("Khadaki");
        p.addVertex("Bopoli");
        p.addVertex("Phugewadi");
        p.addVertex("Kasarwadi");
        p.addVertex("Bhosari");
        p.addVertex("Sant Tukaram Nagar");
        p.addVertex("PCMC");
        p.addVertex("Mangalwar Peth");

        p.addEdge("Civil Court", "Shivaji Nagar", 1500);
        p.addEdge("Shivaji Nagar", "Range Hills", 2000);
        p.addEdge("Range Hills", "Khadaki", 2800);
        p.addEdge("Khadaki", "Bopoli", 1500);
        p.addEdge("Bopoli", "Phugewadi", 4000);
        p.addEdge("Phugewadi", "Kasarwadi", 1000);
        p.addEdge("Kasarwadi", "Bhosari", 1400);
        p.addEdge("Bhosari", "Sant Tukaram Nagar", 700);
        p.addEdge("Sant Tukaram Nagar", "PCMC", 2000);
        p.addEdge("Civil Court", "PMC", 1000);
        p.addEdge("PMC", "C.Sambhaji Park", 750);
        p.addEdge("C.Sambhaji Park", "Deccan", 650);
        p.addEdge("Deccan", "Garware College", 1000);
        p.addEdge("Garware College", "Nal Stop", 1200);
        p.addEdge("Nal Stop", "Ideal Colony", 1000);
        p.addEdge("Ideal Colony", "Anand Nagar", 1700);
        p.addEdge("Anand Nagar", "Vanaz", 1000);
        p.addEdge("Civil Court", "Budhwar Peth", 1100);
        p.addEdge("Budhwar Peth", "Mandai", 1300);
        p.addEdge("Mandai", "Swargate", 1700);
        p.addEdge("Civil Court", "Mangalwar Peth", 2400);
        p.addEdge("Mangalwar Peth", "Pune Railway Station", 450);
        p.addEdge("Pune Railway Station", "Ruby Hall Clinic", 650);
        p.addEdge("Ruby Hall Clinic", "Bund Garden", 1700);
        p.addEdge("Bund Garden", "Yerwada", 1000);
        p.addEdge("Yerwada", "Kalyani Nagar", 3400);
        p.addEdge("Kalyani Nagar", "Ramwadi", 2000);
    }

    public static void main(String[] args) throws IOException {
        PuneMetro p = new PuneMetro();
        Metro_Map(p);
        int choice=0;
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in)); // take the input
        do {
            // DISPLAYING THE MENU
            System.out.println("\n*WELCOME TO PUNE METRO SERVICE*");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("~How can I help you?");
            System.out.println("1. List all stations");
            System.out.println("2. Show the metro map");
            System.out.println("3. Get shortest distance between stations");
            System.out.println("4. Get shortest time to reach stations");
            System.out.println("5. Get shortest path (distance wise) between stations");
            System.out.println("6. REAL-TIME TRAFFIC UPDATE");
            System.out.println("7. Exit");
            System.out.println("Enter your choice (from 1 to 7): ");
            choice = Integer.parseInt(input.readLine());
            System.out.print("\n*********************\n");

            switch (choice) {
                case 1:
                    displayStations();
                    break;

                case 2:
                    displayMap();
                    break;

                case 3:
                    getShortestDistance(input);
                    break;

                case 4:
                    getShortestTime(input);
                    break;

                case 5:
                    getShortestPath(input);
                    break;

                case 6:
                    // Implement real-time traffic update if needed
                    break;

                case 7:
                    System.out.println("Exiting Pune Metro Service..!");
                    break;

                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 7.");
            }
        } while (choice != 7);
        System.out.println("THANK YOU !!!");
    }
}

