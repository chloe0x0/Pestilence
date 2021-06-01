package Pestilence;

import java.lang.String;
import java.lang.Math;
import java.lang.Double;

import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;

import java.io.FileWriter;
import java.io.IOException;

public class engine {

    // cell states are as follows: Dead, Susceptible, Incubating, Infected, Immune
    class cell{
        int state, timeInfected;
        public cell(int state){
            this.state = state;
            this.timeInfected = 0;
        }
    }

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
    private int[][] neighbourhood;
    private cell[][] lattice;
    private boolean tracking;
    private Pestilence.Pathogen pathogen;
    private ArrayList<String[]> statistics;
    public engine(int dimX, int dimY, String n_str, boolean isTracking){
        this.lattice = new cell[dimX][dimY];
        this.neighbourhood = getNeighbourhood(n_str);
        this.width = dimX;
        this.height = dimY;
        this.tracking = isTracking;
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
            case "von neuman":
                return von_neuman_neighbourhood;
            default:
                return new int[][] {{}};
        }
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
            states[ix] = lattice[dx][dy].state;
        } 

        return states;
    }
    // select a random neighbouring cell, if rand() < Pathogen's R0 and cell is susceptible we convert it to Infected.
    public void infectCell(int x, int y){
        int random_neighbour_ix = ThreadLocalRandom.current().nextInt(0, neighbourhood.length);
        int[] offset = neighbourhood[random_neighbour_ix];
        int site = lattice[x + offset[0]][y + offset[1]].state;

        if (site != 1){
            return;
        }

        if (Math.random() < pathogen.getR0()){
            lattice[x + offset[0]][y + offset[1]].state = 2;
        }

    }

    // function to be applied on every infected cell. 
    // Updates cell state based on these rules
    // Cell states = {Dead, Susceptible, Incubating, Infected/Infectious, Immune}
    public int logic(int x, int y){
        cell c = lattice[x][y];
        // incubating cell
        if (c.state == 2){
            if (c.timeInfected >= pathogen.getIncubation()){
                c.timeInfected = 0;
                return 3;
            }
            ++c.timeInfected;
        }
        // Infected cell
        else if (c.state == 3){
            if (c.timeInfected >= pathogen.getInfectious()){
                if (Math.random() < pathogen.getImmunity()){
                    c.timeInfected = 0;
                    return 4;
                }
                else{
                    c.timeInfected = 0;
                    return 1;
                }
            }

            else if (Math.random() < pathogen.getFatality()){
                c.timeInfected = 0;
                return 0;
            }

            else{
                infectCell(x, y);
                ++c.timeInfected;
                return 3;
            }
        }
        return c.state;
    }
   // iterate the simulation by a single epoch, applying the logic function to every incubated/ infected cell
   // it is unnescary to consider any cells which are not either Incubating or Infected is because no logic needs to be applied to them
   // thus we can simply continue in the loop
    public void timeStep() throws ArrayIndexOutOfBoundsException{
        ++iteration;
        for (int x = 1; x < width - 1; ++x){
            for (int y = 1; y < height - 1; ++y){
                int state = lattice[x][y].state;
                
                if (state == 1 || state == 4 || state == 0){
                    continue;
                }
               // int[] neighbour_states = getNeighbourStates(x, y);
                lattice[x][y].state = logic(x, y);
            }
        }
    
        if (tracking){
            trackStatistics();
        }
    
    }
   // iterate the simulation n times
    public void nSimulation(int n){
        for (int i = 0; i < n; ++i){
            timeStep();
        }
    }
    // getter method to return the lattice
    public cell[][] getLattice(){
        return lattice;
    }

    public int getIteration(){
        return iteration;
    }
   // initialize the infected cells. Iterate over every cell, set it to infected by the given probability
    public void seedInfected(double probability){
        for (int x = 0; x < width; ++x){
            for (int y = 0; y < height; ++y){
                if (Math.random() < probability){
                    lattice[x][y] = new cell(2);
                }
                else{
                    lattice[x][y] = new cell(1);
                }
            }
        }
    }
    // functions to extract simulation data to CSV
      public String[][] extractStatistics(){
        int[] StateSums = new int[5];

        for (cell[] x : lattice){
            for (cell c : x){
                switch ( c.state ){
                    case 0:
                        StateSums[0]++;
                        break;
                    case 1:
                        StateSums[1]++;
                        break;
                    case 2:
                        StateSums[2]++;
                        break;
                    case 3:
                        StateSums[3]++;
                        break;
                    case 4:
                        StateSums[4]++;
                        break;
                }
            }
        }

        String[][] data = new String[5][2];


        for (int ix = 0; ix < 5; ++ix){
            String total, percentage;
            total = Integer.toString(StateSums[ix]);
            percentage = String.format("%.02f", (float)StateSums[ix]/(float)populationCount * 100);
            data[ix] = new String[] {total, percentage};
        }
        return data;
    }
    // Cell states = {Dead, Susceptible, Incubating, Infected/Infectious, Immune}
    public void trackStatistics(){
        String[][] data = extractStatistics();

        String[] rowData = {
            Integer.toString(iteration),
            data[0][0], data[1][0], data[2][0], data[3][0], data[4][0],
            Integer.toString(populationCount),
            data[0][1], data[1][1], data[2][1], data[3][1], data[4][1]
        };

        statistics.add(rowData);
        
    }

    public void extractToCSV(String fileName) throws IOException{
        FileWriter  csvWriter = new FileWriter(fileName);
        csvWriter.append("iteration,D,S,Ic,I,Im,Population,D%,S%,I%,Ic%,Im%\n");

        for (String[] rowData : statistics){
            csvWriter.append(String.join(",", rowData));
            csvWriter.append("\n");
        }
        csvWriter.flush(); 
        csvWriter.close();
    }
    /*
    public static void main(String[] args) throws IOException{
        engine e = new engine(600, 600, "moore", true);
        e.engineInit(0.25, 0.68, 7, 15, 0.5, 0.005);
        e.nSimulation(250);
        e.extractToCSV("dataTest.csv");
    }*/
}
