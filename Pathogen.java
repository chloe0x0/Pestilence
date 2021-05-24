package Pestilence;
// The Pathogen object stores information relevant to the simulation, such as infectious period, recovery rate, and attack rate
// Recovery Rate: percent chance of an Infected individual gaining immunity
// The infectious period: number of epochs an indiviudal will be infectious for. Transfering to either dead, recovered, or susceptible
// attack rate: given an infected cell and a Susceptible neighbour, the probability that it will sucessfully transmit the Pathogen
public class Pathogen {
    private double recovery_rate, inf_period, attack_rate;
    
    public Pathogen(double recovery_rate, double inf_period, double attack_rate){
        this.recovery_rate = recovery_rate;
        this.inf_period = inf_period;
        this.attack_rate = attack_rate;
    }
   // Setter Methods
    public void setRecov(double newRate){
        recovery_rate = newRate;
    }
    
    public void setInf(double newInf){
        inf_period = newInf;
    }

    public void setAttackRate(double newAttackRate){
        attack_rate = newAttackRate;
    }
    // Getter methods 
    public double getRecov(){
        return recovery_rate;
    }
    
    public double getInf(){
        return inf_period;
    }

    public double getAttackRate(){
        return attack_rate;
    }
}
