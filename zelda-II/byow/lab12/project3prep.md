# Project 3 Prep

**For tessellating hexagons, one of the hardest parts is figuring out where to place each hexagon/how to easily place hexagons on screen in an algorithmic way.
After looking at your own implementation, consider the implementation provided near the end of the lab.
How did your implementation differ from the given one? What lessons can be learned from it?**

Answer:

My original implementation (or at least what I intended to implement since I barely started implementing Hexagon class when the solution was presented) did not take advantage of classes. Instead of creating classes like Position, Hexagon, and Tessellation, I was going to cram everything inside the HexWorld file. I might notice this issue later on when things get complicated but it is good practice to keep object oriented programming in mind.

**Can you think of an analogy between the process of tessellating hexagons and randomly generating a world using rooms and hallways?
What is the hexagon and what is the tesselation on the Project 3 side?**

Answer:

My guess is that rooms are like hexagons and connecting them using hallways is similar to the process of tessellating hexagons.

**If you were to start working on world generation, what kind of method would you think of writing first? 
Think back to the lab and the process used to eventually get to tessellating hexagons.**

Answer:

I would start by writing methods that allows me to visualize the world first because it helps with debugging.

**What distinguishes a hallway from a room? How are they similar?**

Answer:

A hallway is a width (or "thickness" because there could be turns) 1 room. However, hallways can have L-turns and intersections, whereas rooms should not overlap with eachother.
