package Pestilence;

public class Pathogen {
    private double recovery_rate, inf_period, attack_rate;
    
    public Pathogen(double recovery_rate, double inf_period, double attack_rate){
        this.recovery_rate = recovery_rate;
        this.inf_period = inf_period;
        this.attack_rate = attack_rate;
    }
   
    public void setRecov(double newRate){
        recovery_rate = newRate;
    }
    
    public void setInf(double newInf){
        inf_period = newInf;
    }

    public void setAttackRate(double newAttackRate){
        attack_rate = newAttackRate;
    }
    
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
