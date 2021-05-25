
package Pestilence;

import javax.swing.*;
import java.awt.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.*;

import java.util.Scanner;

import java.io.IOException;

class DisplayPanel extends JPanel implements KeyListener{
    private Boolean isLooping;
    engine e;
    public DisplayPanel(int width, int height){
        this.setBackground(Color.BLACK);
        this.e = new engine(width, height, "moore", true);
        this.isLooping = false;
        addKeyListener(this);
        this.setFocusable(true);
    }

    public void engine_init(double R0, double fatality, int incubation, int infectious, double immunity_gain, double infection_seed){
        e.engineInit(R0, fatality, incubation, infectious, immunity_gain, infection_seed);
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        engine.cell[][] lattice = e.getLattice();
        for (int x = 0; x < lattice.length; ++x){
            for (int y = 0; y < lattice[0].length; ++y){
                if (lattice[x][y].state == 0){
                    g.setColor(Color.GRAY);
                    g.fillRect(x, y, 1, 1);
                }
                else if (lattice[x][y].state == 1){
                    g.setColor(Color.GREEN);
                    g.fillRect(x, y, 1, 1);
                }
                else if (lattice[x][y].state == 2){
                    g.setColor(Color.YELLOW);
                    g.fillRect(x, y, 1, 1);
                }
                else if (lattice[x][y].state == 3){
                    g.setColor(Color.RED);
                    g.fillRect(x, y, 1, 1);
                }
                else if (lattice[x][y].state == 4){
                    g.setColor(Color.BLUE);
                    g.fillRect(x, y, 1, 1);
                }
            }
        }
        g.setColor(Color.BLACK);
        g.drawString(Integer.toString(e.getIteration()), 150, 150);
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
    public static void main(String[] args) throws InterruptedException, IOException{
        Scanner sc = new Scanner(System.in);

        double R0, fatality, immunity_gain, infection_seed;
        int incubation, infectious, width, height;


        System.out.print("Width of lattice: ");
        width = sc.nextInt(); 

        System.out.print("Height of the lattice: ");
        height = sc.nextInt();

        System.out.print("R0: ");
        R0 = sc.nextDouble();

        System.out.print("fatality: ");
        fatality = sc.nextDouble();

        System.out.print("incubation: ");
        incubation = sc.nextInt();

        System.out.print("infectious: ");
        infectious = sc.nextInt();

        System.out.print("immunity_gain: ");
        immunity_gain = sc.nextDouble();
        
        System.out.print("infection seed probability: ");
        infection_seed = sc.nextDouble();

        Frame f = new Frame(width, height);
        f.p.engine_init(R0, fatality, incubation, infectious, immunity_gain, infection_seed);
        while(f.p.isVisible()){
            if (f.p.e.getIteration() == 15){
                f.p.e.extractToCSV("dataTest.csv");
            }
            f.p.epoch();
            f.p.repaint();
            Thread.sleep(10);
        }
    }
}
