# BearMaps
CS61B Bear Maps

BearMaps is a Google Maps inspired clone for the vicinity of the UC Berkeley campus. It is capable of performing most features you would expect of a mapping application. The "smart" features of the application include map dragging/zooming, map rasterization, A* search algorithm between two points, and an auto-complete search feature.

<img src="/BearMaps/images/demo.gif" alt="bear-maps" width="100%"/>

Feature | Description
------- | -------
[RasterAPIHandler](https://github.com/LayneWei/mid-size-projects/blob/master/BearMaps/bearmaps/proj2c/server/handler/APIRouteHandler.java) | Renders map images given a user's requested area and level of zoom.
[AugmentedStreetMapGraph](https://github.com/LayneWei/mid-size-projects/blob/master/BearMaps/bearmaps/proj2c/AugmentedStreetMapGraph.java) | Graph representation of the contents of Berkeley Open Street Map data.
[AStarSolver](https://github.com/LayneWei/mid-size-projects/blob/master/BearMaps/bearmaps/hw4/AStarSolver.java) | The A* search algorithm to find the shortest path between two points in Berkeley.
[MyTrieSet](https://github.com/LayneWei/mid-size-projects/blob/master/BearMaps/bearmaps/proj2c/MyTrieSet.java) | A TrieSet backs the autocomplete search feature, matching a prefix to valid location names in Θ(k) time, where k in the number of words sharing the prefix.
[KDTree](https://github.com/LayneWei/mid-size-projects/blob/master/BearMaps/bearmaps/proj2ab/KDTree.java) | A K-Dimensional Tree backs the A* search algorithm, allowing efficient nearest neighbor lookup averaging O(log(n)) time.
[ArrayHeapMinPQ](https://github.com/LayneWei/mid-size-projects/blob/master/BearMaps/bearmaps/proj2ab/ArrayHeapMinPQ.java) | A min-heap priority queue backs the A* search algorithm.

# zelda-ii Tiles Based Game
CS61B 2D Tiles Game is a Video Game built with the reference of zelda-ii (the-adventure-of-link). In this video game, user will start with a avatar in a world filled with randomly generated and connected rooms tryinhg to escape from monsters. Some basic function provided for user are moving avatar based on keyboard, getting tile information with mouse, saving last game history, loading from last saved point, changing player's name. A demo of this game will be uploaded later.

<img src="/zelda-II/images/zelda2-4.jpg" alt="zelda-ii" width="100%"/>

# ThunderCross Plane Game
ThunderCross Plane Game is a small video game inspired by Thunder Cross for OOD practice. The user could control the hero plane to destory enemy plane and get award for adding life and set double fireforce.

<img src="/thunderCross/images/demo.gif" alt="thunder-cross" width="50%"/>