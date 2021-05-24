package Pestilence;

import java.lang.String;
import java.lang.Math;
import java.lang.Double;

import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;

import java.io.FileWriter;
import java.io.IOException;

public class engine {
    // Hard coded neighbourhoods in terms of <x, y> offsets
    private final int[][] moore_neighbourhood = {
        {-1, -1}, {0, -1}, {1, -1},
        {-1, 0},           {1, 0},
        {-1, 1},  {0, 1},  {1, 1}
    };
    private final int[][] von_neuman_neighbourhood = {
          {0, -1},
    {-1, 0},     {1, 0},
           {0, 1}
    };

    private int width, height, iteration, populationCount;
    private int[][] lattice, neighbourhood;
    private Pestilence.Pathogen pathogen;
    private ArrayList<String[]> statistics;
    public engine(int dimX, int dimY, String n_str){
        this.lattice = new int[dimX][dimY];
        this.neighbourhood = getNeighbourhood(n_str);
        this.width = dimX;
        this.height = dimY;
        this.populationCount = width * height;
        this.iteration = 0;
        this.pathogen = new Pathogen(0.0, 0.0, 0, 0, 0.0);
        this.statistics = new ArrayList<String[]>();
    }

    // get relevant neighborhood given string
    public int[][] getNeighbourhood(String n_str){
        switch ( n_str.toLowerCase() ){
            case "moore":
                return moore_neighbourhood;
                break;
            case "von neuman":
                return von_neuman_neighbourhood;
                break;
    }
    // set custom neighbourhood, check first if invalid
    public void setCustomNeighbourhood(int[][] newNeighbourhood){
        if (newNeighbourhood.length == 0){
            throw new ArrayIndexOutOfBoundsException("Empty neighbourhood");
        }
        else{
            neighbourhood = newNeighbourhood;
        }
    }
    // initialize Pathogen and seed infections
    public void engineInit(double R0, double fatality, int incubation, int infectious, double immunity_gain, double infection_seed){
        pathogen.setImmunity(immunity_gain);
        pathogen.setR0(R0);
        pathogen.setFatality(fatality);
        pathogen.setIncubation(incubation);
        pathogen.setInfectious(infectious);

        seedInfected(infection_seed);
    }
    // Given the 2D array of x, y offsets compute the states of neighbouring cells
    // iterate over each <x, y> offset array in neighbourhood, index into the cell and append to states array
    public int[] getNeighbourStates(int x, int y){
        int[] states = new int[neighbourhood.length];

        for (int ix = 0; ix < neighbourhood.length; ++ix){
            int dx, dy;
            dx = neighbourhood[ix][0] + x;
            dy = neighbourhood[ix][1] + y;
            states[ix] = lattice[dx][dy];
        } 

        return states;
    }
    // select a random neighbouring cell, if rand() < Pathogen's R0 and cell is susceptible we convert it to Infected.
    public void infectCell(int x, int y){
        int random_neighbour_ix = ThreadLocalRandom.current().nextInt(0, neighbourhood.length);
        int[] offset = neighbourhood[random_neighbour_ix];
        int site = lattice[x + offset[0]][y + offset[1]];

        if (site != 0){
            return;
        }

        if (Math.random() < pathogen.getR0()){
            lattice[x + offset[0]][y + offset[1]] = 1;
        }

    }

    // function to be applied on every infected cell. 
    public int logic(int x, int y){
        if (Math.random() < pathogen.getImmunity()){
            return 2;
        }
        else{
            infectCell(x, y);
            return 1;
        }
    }
   // iterate the simulation by a single epoch, applying the logic function to every infected cell
    public void timeStep() throws ArrayIndexOutOfBoundsException{
        ++iteration;
        for (int x = 1; x < width - 1; ++x){
            for (int y = 1; y < height - 1; ++y){
                int state = lattice[x][y];

                if (state == 2 || state == 0){
                    continue;
                }

                int[] neighbour_states = getNeighbourStates(x, y);
                lattice[x][y] = logic(x, y);
            }
        }
        trackStatistics();
    }
   // iterate the simulation n times
    public void nSimulation(int n){
        for (int i = 0; i < n; ++i){
            timeStep();
        }
    }
    // getter method to return the lattice
    public int[][] getLattice(){
        return lattice;
    }
   // initialize the infected cells. Iterate over every cell, set it to infected by the given probability
    public void seedInfected(double probability){
        for (int x = 0; x < width; ++x){
            for (int y = 0; y < height; ++y){
                if (Math.random() < probability){
                    lattice[x][y] = 1;
                }
            }
        }
    }
    // functions to extract simulation data to CSV
    public String[] extractStatistics(int state){
        int sum = 0;
        for (int[] x : lattice){
            for (int y : x){
                if (y == state){
                    ++sum;
                }
            }
        }
        String total, percentage; 
        total = Integer.toString(sum);
        percentage = String.format("%.02f", (float)sum/(float)populationCount * 100);
        return new String[] {total, percentage};
    }

    public void trackStatistics(){
        String[] S, I, R;
        S = extractStatistics(0);
        I = extractStatistics(1);
        R = extractStatistics(2);

        String[] rowData = {
            Integer.toString(iteration),
            S[0], I[0], R[0],
            Integer.toString(populationCount),
            S[1], I[1], R[1]
        };

        statistics.add(rowData);
        
    }

    public void extractToCSV(String fileName) throws IOException{
        FileWriter  csvWriter = new FileWriter(fileName);
        // CSV structure is as follows
        // Epoch, S, I, R, total_population
        csvWriter.append("iteration,S,I,R,Population,S%,I%,R%");

        for (String[] rowData : statistics){
            csvWriter.append(String.join(",", rowData));
            csvWriter.append("\n");
        }
        csvWriter.flush(); 
        csvWriter.close();
    }
}
