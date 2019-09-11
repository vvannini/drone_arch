/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.BlackMore.LP2D;

import ProOF.apl.pog.problem.BlackMore.BMAddObj;
import ProOF.apl.pog.problem.BlackMore.BMCodification;
import ProOF.apl.pog.problem.BlackMore.BMControl;
import ProOF.apl.pog.problem.BlackMore.BMProblem;
import ProOF.apl.pog.problem.BlackMore.BMState;
import ProOF.apl.pog.problem.PPDCP.PPDCPInstance;
import ProOF.com.Linker.LinkerResults;

/**
 *
 * @author marcio
 */
public class LP2DServices {
    public BMState build_state(LP2DState other) {
        return new LP2DState(other);
    }
    public BMControl build_control(LP2DControl other) {
        return new LP2DControl(other);
    }
    public BMControl build_control(BMProblem prob) {
        return new LP2DControl(prob);
    }
    public void plot(BMProblem prob, BMCodification codif) {
        prob.inst.plot(Mt(prob.inst, codif));
    }
    

    /*private BMAddRest selectREST() throws Exception {
        switch (inst.REST) {
            case 0:
                return new REST_empty();
            case 1:
                return new REST_new();
            case 2:
                return new REST_new2();
        }
        return null;
    }*/

    
    public void results(LinkerResults com, PPDCPInstance inst, BMCodification codif) throws Exception {
        
        double vMt[][] = Mt(inst, codif);
        double M[][] = inst.trans(vMt);
        com.writeArray("PPDCP", "Mt[0]", M[0]);
        com.writeArray("PPDCP", "Mt[1]", M[1]);
        com.writeArray("PPDCP", "Mt[2]", M[2]);
        com.writeArray("PPDCP", "Mt[3]", M[3]);
        double U[][] = new double[2][inst.T];
        for(int t=0; t<inst.T; t++){
            U[0][t] = X(codif.Xt[t]);
            U[1][t] = Y(codif.Xt[t]);
        }
        
        com.writeArray("PPDCP", "Ut[0]", U[0]);
        com.writeArray("PPDCP", "Ut[1]", U[1]);
        
        inst.plot(vMt);
        inst.save();
    }
    public double X(BMState state){
        return ((LP2DState)(state)).x;
    }
    public double Y(BMState state){
        return ((LP2DState)(state)).y;
    }
    public double[][] Mt(PPDCPInstance inst, BMCodification codif){
        double exp[][] = new double[inst.T+1][4];
        //-------------------------------- (11) --------------------------------
        for(int t=0; t<inst.T+1; t++){
            for(int i=0; i<t; i++){
                double R[][] = inst.prod(inst.pow(inst.A,t-1-i), inst.B);
                for(int j=0; j<2; j++){
                    for(int k=0; k<4; k++){
                        exp[t][k] += R[k][j] * (j==0 ? X(codif.Xt[t]) : Y(codif.Xt[t]));
                    }
                }
            }
            double AX0[][] = inst.prod(inst.pow(inst.A,t), inst.X0);
            for(int k=0; k<4; k++){
                exp[t][k] += AX0[k][0];
            }
        }
        return exp;
    }
    public BMAddObj selectOBJ(BMProblem prob) throws Exception {
        switch (prob.inst.OBJ) {
            case 0:
                return new BMAddObj() {
                    @Override
                    public double cost(BMProblem prob, BMState[] Xt, BMControl[] Ut) throws Exception {
                        double cost = 0;
                        for(BMControl u : Ut){
                            cost += u.norm2sqr();
                        }
                        return cost;
                    }
                };
            case 1:
                return new BMAddObj() {
                    @Override
                    public double cost(BMProblem prob, BMState[] Xt, BMControl[] Ut) throws Exception {
                        double cost = 0;
                        for(BMControl u : Ut){
                            cost += u.norm1();
                        }
                        return cost;
                    }
                };
            case 2:
                return new BMAddObj() {
                    @Override
                    public double cost(BMProblem prob, BMState[] Xt, BMControl[] Ut) throws Exception {
                        double cost = 0;
                        for(BMControl u : Ut){
                            cost += u.norm2();
                        }
                        return cost;
                    }
                };
            case 3:
                return new BMAddObj() {
                    @Override
                    public double cost(BMProblem prob, BMState[] Xt, BMControl[] Ut) throws Exception {
                        double cost = 0;
                        for(BMControl u : Ut){
                            cost += u.norm2sqr();
                        }
                        return cost;
                    }
                };
        }
        return null;
    }
}
