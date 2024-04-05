# Metro-Route-Optimizer

Overview
The Optimal Route Planning System is a Java program designed for the DSA Craft Competition. It utilizes various data structures and algorithms to find the most efficient route between two points in a transportation network.

Problem Statement
Given a transportation network represented as a graph with nodes as locations and edges as connections between locations, the system should be able to:

Find the shortest path (in terms of distance or time) between two given locations.
Provide real-time traffic updates and suggest alternate routes if necessary.
Optimize routes based on user preferences such as avoiding toll roads, minimizing travel time, or prioritizing scenic routes.

Key Features:

1. Graph Representation: Utilizes a graph data structure to represent the transportation network.
Dijkstra's Algorithm: Finds the shortest path between two locations using Dijkstra's algorithm, considering edge weights (distance or time).
2. Real-Time Traffic Updates: Integrates with a traffic API to provide real-time traffic information and suggest optimal routes.
3. User Preferences: Allows users to specify preferences such as avoiding toll roads, choosing fastest routes, or selecting scenic routes.
4. Interactive Interface: Provides a user-friendly interface for inputting locations, viewing routes, and receiving recommendations.
Components
5. Graph Module: Manages the graph structure, including nodes (locations/stations) and edges (connections/routes).
6. Dijkstra Module: Implements Dijkstra's algorithm for finding the shortest path between two nodes based on edge weights.
7. User Interface: Provides a command-line interface for users to interact with the system, input preferences, and view routes.

Technologies Used:

- Data Structures : Graph, Priority Queue, HashMap, ArrayList
- Java Programming Language
- Algorithms : Dijkstra's Algorithm Implementation
- Command-Line Interface

Getting Started
Clone the Repository:

https://github.com/tejaswini1304/DSA-Craft-Matrix-2x2


Acknowledgments:

- This project was developed as part of the DSA Craft Competition.
- Special thanks to the competition organizers and mentors for their support.
- Inspiration drawn from real-world optimal route planning applications and traffic management systems.
