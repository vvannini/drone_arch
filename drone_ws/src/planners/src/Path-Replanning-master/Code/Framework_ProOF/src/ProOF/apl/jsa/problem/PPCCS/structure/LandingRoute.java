/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.jsa.problem.PPCCS.structure;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jesimar
 */
public class LandingRoute implements Serializable {

    private final Position2D position[];
    private final double time[];
    private int timeForLanding;

    public LandingRoute(int T) {
        position = new Position2D[T];
        for (int i = 0; i < T; i++) {
            position[i] = new Position2D();
        }
        time = new double[T];
    }

    public void setTimeForLanding(int timeForLangind) {
        this.timeForLanding = timeForLangind;
    }

    public void setPositionX(double valeu, int index) {
        position[index].setX(valeu);
    }

    public void setPositionY(double valeu, int index) {
        position[index].setY(valeu);
    }

    public void setTime(double valeu, int index) {
        time[index] = valeu;
    }

    public Position2D[] getPosition2D() {
        return position;
    }

    public double getPositionX(int index) {
        return position[index].getX();
    }

    public double getPositionY(int index) {
        return position[index].getY();
    }

    public double[] getTime() {
        return time;
    }

    public int getTimeForLanding() {
        return timeForLanding;
    }

    public void saveRouteObject(String name) {
        File file = new File(name);
        try {
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file));
            output.writeObject(this);
            output.close();
        } catch (IOException ex) {
            Logger.getLogger(LandingRoute.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void saveRouteText(String name) {
        File file = new File(name);
        try {
            PrintStream output = new PrintStream(file);
            for (int i = 0; i < timeForLanding; i++) {
                output.println(String.format("%.4f %.4f", position[i].getX(), position[i].getY()));
            }
            output.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LandingRoute.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
