# Time Table Generator

The repository contains the files for generating a time table with some input data. The files needed are as follows:
  1. Capacity.room contains the capacites for each room available with each room in a new line.
  2. Registration.txt which contains the already registered students and their slots.
  3. General.txt which contains all available slots.

The program uses Genetic Algorithm with Crossover Mutation to generate the best possible and clash-free time table. A population is created with a set of Time Tables and then fitness is calculated according to the factors which include:
  1. Number of rooms occupied
  2. Multiple slots at the same time
  3. 2 continuous slots per student

TimeTable.java contains the code for creation of a chromosome. An array (generation) of these is stored in Population.java which performs mutations and computes Fitness. The runner program is GeneticAlgo.java which uses both files.

# How to Run?
Download the files. Use any Java IDE and provide the input files for the program. Run the program and it will generate the best possible combination.
