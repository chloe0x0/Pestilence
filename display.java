
package Pestilence;

import javax.swing.*;
import java.awt.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.*;

import java.util.Scanner;

class DisplayPanel extends JPanel implements KeyListener{
    private int[][] state_space;
    private Boolean isLooping;
    private engine e;
    public DisplayPanel(int width, int height){
        this.setBackground(Color.BLACK);
        this.e = new engine(width, height, "moore");
        this.isLooping = false;
        addKeyListener(this);
        this.setFocusable(true);
    }

    public void engine_init(double recovery_rate, double inf_period, double attack_rate, double infection_seed){
        e.engineInit(recovery_rate, inf_period, attack_rate, infection_seed);
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        int[][] lattice = e.getLattice();
        for (int x = 0; x < lattice.length; ++x){
            for (int y = 0; y < lattice[0].length; ++y){
                if (lattice[x][y] == 0){
                    g.setColor(Color.GREEN);
                    g.fillRect(x, y, 1, 1);
                }
                else if (lattice[x][y] == 1){
                    g.setColor(Color.RED);
                    g.fillRect(x, y, 1, 1);
                }
                else if (lattice[x][y] == 2){
                    g.setColor(Color.BLUE);
                    g.fillRect(x, y, 1, 1);
                }
            }
        }
    }

    public void epoch(){
        if (!isLooping){
            return;
        }
        e.timeStep();
    }

    public void keyPressed(KeyEvent e){
        int keycode = e.getKeyCode();
        //System.out.println(offX + " " + offY + " " + zoomFactor);
        switch( keycode ){
            case KeyEvent.VK_SPACE:
                isLooping = !isLooping;
                break;
        }
    }
    public void keyReleased(KeyEvent e){
        return;
    }
    public void keyTyped(KeyEvent e){
        return;
    }
}

class Frame extends JFrame{
    DisplayPanel p;
    private int width, height;
    public Frame(int width, int height){
        this.setSize(width, height);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Lattice Siumulation");
        this.p = new DisplayPanel(width, height);
        this.getContentPane().add(p);
        this.setVisible(true);
    }
}

public class display{
    public static void main(String[] args) throws InterruptedException{
        Scanner sc = new Scanner(System.in);
        double recovery_rate, inf_period, attack_rate, infection_seed;
        int width, height;

        System.out.print("Width of lattice: ");
        width = sc.nextInt();

        System.out.print("Height of lattice: ");
        height = sc.nextInt();

        System.out.print("Recovery Rate: ");
        recovery_rate = sc.nextDouble();
        
        System.out.print("Infectious Period: ");
        inf_period = sc.nextDouble();

        System.out.print("Attack Rate: ");
        attack_rate = sc.nextDouble();

        System.out.print("Infection Seed Probability: ");
        infection_seed = sc.nextDouble();

        
        Frame f = new Frame(width, height);
        f.p.engine_init(recovery_rate, inf_period, attack_rate, infection_seed);
        while(f.p.isVisible()){
            f.p.epoch();
            f.p.repaint();
            Thread.sleep(10);
        }
    }
}