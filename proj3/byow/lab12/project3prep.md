# Project 3 Prep

**For tessellating hexagons, one of the hardest parts is figuring out where to place each hexagon/how to easily place hexagons on screen in an algorithmic way.
After looking at your own implementation, consider the implementation provided near the end of the lab.
How did your implementation differ from the given one? What lessons can be learned from it?**

Answer:
Make a position class is very convenient. Draw a row first, then one Hexagon, then draw one column of Hexagons, then draw the whole world. 

-----

**Can you think of an analogy between the process of tessellating hexagons and randomly generating a world using rooms and hallways?
What is the hexagon and what is the tesselation on the Project 3 side?**

Answer: hexagon is like the room, tesselation is like placing multiple rooms.

-----
**If you were to start working on world generation, what kind of method would you think of writing first? 
Think back to the lab and the process used to eventually get to tessellating hexagons.**

Answer:First the position class, then room class, draw room at random location and make sure they don't claash with each other

-----
**What distinguishes a hallway from a room? How are they similar?**

Answer: hallway is width 1 or 2, width and height of room are random. Hallway is kinda a room restricted with width.
