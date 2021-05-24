package Pestilence;
// The Pathogen object stores information relevant to the simulation, such as infectious period, recovery rate, and attack rate
// R0 : probability of succesfully infecting a susceptible cell
// Immunity Gain : probability of an infected cell transfering to the Immune state
// Fatality : probability of an infected cell transfering to the Dead state
// incubation : incubation period, baseline time for a cell to stay in the Incubating stage
// infectious : the infectious period of the Pathogen
public class Pathogen {
    private double R0, immunity_gain, fatality;
    private int incubation, infectious;
    public Pathogen(double R0, double fatality, int incubation, int infectious, double immunity_gain){
        this.immunity_gain = immunity_gain;
        this.R0 = R0;
        this.fatality = fatality;
        this.incubation = incubation;
        this.infectious = infectious;
    }
   // Setter Methods
    public void setImmunity(double newRate){
        immunity_gain = newRate;
    }
    
    public void setInfectious(int newRate){
        infectious = newRate;
    }

    public void setR0(double newRate){
        R0 = newRate;
    }

    public void setFatality(double newRate){
        fatality = newRate;
    }

    public void setIncubation(int newRate){
        incubation = newRate;
    }
    // Getter methods
    public double getImmunity(){
        return immunity_gain;
    }

    public double getFatality(){
        return fatality;
    }

    public double getR0(){
        return R0;
    }

    public int getIncubation(){
        return incubation;
    }

    public int getInfectious(){
        return infectious;
    }
}
