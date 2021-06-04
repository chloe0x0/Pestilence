# Pestilence
Basic Epidemic modeler using cellular automaton in Java 

## Pathogen: 
The Pathogen object holds data relevant to the simulated Pathogen. Such as: R0, incubation period, and fatality rate
## Display: 
primitive GUI to visualize a simulation
## Engine: 
Handles the simulation computations, and can be set to track simulation statistics to be output to a CSV. The engine uses a spatial SIRS model, though it can be generalized to other compartmental models by nullifying certain parameters such as immunity loss. (The effect of setting immunity loss to 0.0 is transforming the SIRS model to a SIR model)

# TODO
1. Rather than iterating over the entire lattice and performing an if statement to check if we pass the cell through the logic function, it should instead store an ArrayList<> of all positions which need to be passed through logic(). This optimazation should allow faster simulations on large lattices. 
2. Add live graphing of statistics into display.java
