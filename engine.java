package Pestilence;

import java.lang.String;
import java.lang.Math;
import java.lang.Double;

import java.util.concurrent.ThreadLocalRandom;

import java.io.FileWriter;
import java.io.IOException;

public class engine {
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
    private String[][] statistics;
    public engine(int dimX, int dimY, String n_str){
        this.lattice = new int[dimX][dimY];
        this.neighbourhood = getNeighbourhood(n_str);
        this.width = dimX;
        this.height = dimY;
        this.populationCount = width * height;
        this.iteration = 0;
        this.pathogen = new Pathogen(0.0, 0.0, 0.0);
        List<List<String>> statistics = ;
    }

    public int[][] getNeighbourhood(String n_str){
        return n_str.toLowerCase() == "moore" ? moore_neighbourhood : von_neuman_neighbourhood; 
    }

    public void setCustomNeighbourhood(int[][] newNeighbourhood){
        if (newNeighbourhood.length == 0){
            throw new ArrayIndexOutOfBoundsException("Empty neighbourhood");
        }
        else{
            neighbourhood = newNeighbourhood;
        }
    }

    public void engineInit(double recovery_rate, double inf_period, double attack_rate, double infection_seed){
        pathogen.setRecov(recovery_rate);
        pathogen.setInf(inf_period);
        pathogen.setAttackRate(attack_rate);

        seedInfected(infection_seed);
    }

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

    public void infectCell(int x, int y){
        int random_neighbour_ix = ThreadLocalRandom.current().nextInt(0, neighbourhood.length);
        int[] offset = neighbourhood[random_neighbour_ix];
        int site = lattice[x + offset[0]][y + offset[1]];

        if (site != 0){
            return;
        }

        if (Math.random() < pathogen.getAttackRate()){
            lattice[x + offset[0]][y + offset[1]] = 1;
        }

    }

    public int logic(int x, int y){
        if (Math.random() < pathogen.getRecov()){
            return 2;
        }
        else{
            infectCell(x, y);
            return 1;
        }
    }

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
    }

    public void nSimulation(int n){
        for (int i = 0; i < n; ++i){
            timeStep();
        }
    }

    public int[][] getLattice(){
        return lattice;
    }

    public void seedInfected(double probability){
        for (int x = 0; x < width; ++x){
            for (int y = 0; y < height; ++y){
                if (Math.random() < probability){
                    lattice[x][y] = 1;
                }
            }
        }
    }

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
        percentage = String.format("%.02f", (float)sum/(float)populationCount * 100)+"%";
        return new String[] {total, percentage};
    }


    public void 



    public void extractToCSV(String fileName, String[][] rowData) throws IOException{
        FileWriter  csvWriter = new FileWriter(fileName);
        // CSV structure is as follows
        // Epoch, S, I, R, total_population
        csvWriter.append("iteration");
        csvWriter.append(",");
        csvWriter.append("S");
        csvWriter.append(",");
        csvWriter.append("I");
        csvWriter.append(",");
        csvWriter.append("R");
        csvWriter.append(",");
        csvWriter.append("Population");
        csvWriter.append(",");
        csvWriter.append("S%");
        csvWriter.append(",");
        csvWriter.append("I%");
        csvWriter.append(",");
        csvWriter.append("R%");
        csvWriter.append("\n");

        for (String[] rowData : statistics){
            csvWriter.append(String.join(",", rowData));
            csvWriter.append("\n");
        }
        csvWriter.flush(); 
        csvWriter.close();
    }
}
