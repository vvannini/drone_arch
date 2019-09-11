/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ProOF.apl.jsa.problem.PPCCS.util;

import ProOF.apl.jsa.problem.PPCCS.instance.InstanceProblem;
import ProOF.apl.jsa.problem.PPCCS.structure.PPCCSState;
import java.awt.Polygon;
import jsc.distributions.Normal;

/**
 *
 * @author jesimar
 */
public class Util {
    
    public static String landingLocal(InstanceProblem inst, double dropX, double dropY){
        Polygon polygon[][] = inst.polygon;
        for(int i = 0; i < polygon.length; i++){
            for(int j = 0; j < polygon[i].length; j++){
                if (polygon[i][j].contains(dropX, dropY)){
                    return (i == 0 ? "n" : i == 1 ? "p" : "b");
                }
            }
        }
        return "r";
    }
    
    public static double probOfFail(InstanceProblem inst, PPCCSState state, int l, int t) {
        double probOfFail = 0;
        double x = state.getPositionX();
        double y = state.getPositionY();
        for (int j = 0; j < inst.SIZE_REGIONS[l]; j++) {
            double chance = chanceOfColision(inst, l, j, t, x, y);
            probOfFail += Math.min(chance, 1);
        }
        probOfFail = Math.min(probOfFail, 1);
        return probOfFail;
    }
    
    public static double chanceOfColision(InstanceProblem inst, int l, int j, int t, 
            double x, double y) {
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < inst.bji[l][j].length; i++) {
            double exp = 0;
            exp += inst.aji[l][j][i][0] * x;
            exp += inst.aji[l][j][i][2] * y;
            exp -= inst.bji[l][j][i];
            exp = exp / inst.Rjti[l][j][t][i];
            max = Math.max(max, exp);
        }
        double delta = (1 - Normal.standardTailProb(max, false)) / 2.0;
        return l == inst.L-1 ? 2* delta : delta;
    }
    
    public static int isNearObstacle(InstanceProblem inst, PPCCSState state, int t) {
        double probFail = 0;
        int idObstacle = -1;
        for (int i = 0; i < inst.SIZE_REGIONS[0]; i++){
            double value = probOfFail(inst, state, i, t);
            if (value > probFail){
                probFail = value;
                idObstacle = i;
            }
        }
        if (probFail > inst.DELTA){
            return idObstacle;
        }
        return -1;
    }
    
    /*Verifica se o vetor contem o valor dado*/
    public static boolean containsValue(double vector[], double value){
        for (int i = 0; i < vector.length; i++){
            if (vector[i] == value){
                return true;
            }
        }
        return false;
    }   
}
