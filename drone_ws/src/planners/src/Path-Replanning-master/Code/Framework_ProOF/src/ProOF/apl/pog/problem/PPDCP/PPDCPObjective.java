/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.PPDCP;

import ProOF.com.Linker.LinkerResults;
import ProOF.com.Stream.StreamPrinter;
import ProOF.gen.codification.Real.cReal;
import ProOF.opt.abst.problem.meta.objective.BoundDbl;
import ProOF.opt.abst.problem.meta.objective.SingleObjective;
import java.io.PrintStream;
import java.util.Locale;
import jsc.distributions.Normal;

/**
 *
 * @author marcio
 */
public class PPDCPObjective extends SingleObjective<PPDCPFactory, cReal, PPDCPObjective> {
   
    private double beta, delta, vel_penalty;
    
    private static final BoundDbl bound = new BoundDbl(0, 1e20, 1e-1);
    
    
    public PPDCPObjective() throws Exception {
        super(bound);
    }

    @Override
    public void copy(PPDCPFactory prob, PPDCPObjective source) throws Exception {
        super.copy(prob, source);
        this.beta = source.beta;
        this.delta = source.delta;
        this.vel_penalty = source.vel_penalty;
    }
    
    @Override
    public void evaluate(PPDCPFactory prob, cReal codif) throws Exception {
        //------------------------(11)---------------------------
        double Mt[][] = Mt(prob.inst, codif);
        
        
        //------------------------[beta]---------------------------
        delta = 0;
        vel_penalty = 0;
        //int k=prob.inst.T;
        for(int t=0; t<prob.inst.T; t++){
            for(int j=0; j<prob.inst.J; j++){
                delta += Delta(prob, j, t, Mt);///Math.pow(1.1, t);
                /*if(delta>=prob.inst.DELTA){
                    k = t;
                    break;
                }*/
            }
            for(int j=0; j<prob.inst.E; j++){
                delta += DyDelta(prob, j, t, Mt);///Math.pow(1.1, t);
            }
            if(Mt[t][1]>prob.inst.VMAX){
                vel_penalty += Mt[t][1]-prob.inst.VMAX;
            }else if(Mt[t][1]< -prob.inst.VMAX){
                vel_penalty += prob.inst.VMAX-Mt[t][1];
            }
        }
        delta = Math.max(0, delta-prob.inst.DELTA);
        
        //------------------------[beta]---------------------------
        beta = prob.inst.dist1(Mt[prob.inst.T], prob.inst.Xgoal);
        
        
        //------------------------[goal]---------------------------
        double goal = 0;
        for(int t=0; t<prob.inst.T; t++){
            goal += Ut(prob.inst, codif, t, 0)*Ut(prob.inst, codif, t, 0)+Ut(prob.inst, codif, t, 1)*Ut(prob.inst, codif, t, 1);
        }
        
        double fitness = prob.inst.GOAL(beta) + prob.inst.DELTA(delta) + prob.inst.OBJ(goal) + vel_penalty*1e3;
        set(fitness);
    }
    
    
    
    
    public static double Delta(PPDCPFactory prob, int j, int t, double Mt[][]){
        double max = -Integer.MAX_VALUE;
        for(int i=0; i<prob.inst.Ob[j].length(); i++){

            double exp = prob.inst.Ob[j].lines[i].ax*Mt[t][0] + prob.inst.Ob[j].lines[i].ay*Mt[t][2];

            exp -= prob.inst.Ob[j].lines[i].b;

            exp = exp/prob.inst.Rjti[j][t][i];

            max = Math.max(max, exp);
        }
        double delta = (1-Normal.standardTailProb(max, false))/2;
        return delta;
    }
    public static double DyDelta(PPDCPFactory prob, int j, int t, double Mt[][]){
        double max = -Integer.MAX_VALUE;
        for(int i=0; i<prob.inst.Dy[j][t].length(); i++){
            double exp = prob.inst.Dy[j][t].lines[i].ax*Mt[t][0] + prob.inst.Dy[j][t].lines[i].ay*Mt[t][2];

            exp -= prob.inst.Dy[j][t].lines[i].b;

            exp = exp/prob.inst.Dy[j][t].R(i, prob.inst.sum_t[t]);

            max = Math.max(max, exp);
        }
        double delta = (1-Normal.standardTailProb(max, false))/2;
        return delta;
    }
    /*
     public static double Delta(PPDCPFactory prob, int j, int t, double Mt[][]){
        double min = Integer.MAX_VALUE;
        for(int i=0; i<prob.inst.bji[j].length; i++){
            double exp = prob.inst.chi(j, t, i);
            for(int k=0; k<4; k++){
                exp += prob.inst.aji[j][i][k] * Mt[t][k];
            }
            exp = exp / prob.inst.psi(j, t, i);
            min = Math.min(min, exp);
        }
        return Math.max(0, min);
    }
     */
    
    public static double Ut(PPDCPInstance inst, cReal codif, int t, int j){
        return codif.X(t*2+j, -inst.UMAX, +inst.UMAX);
    }
    public static double[][] Mt(PPDCPInstance inst, cReal codif){
        double exp[][] = new double[inst.T+1][4];
        //-------------------------------- (11) --------------------------------
        for(int t=0; t<inst.T+1; t++){
            for(int i=0; i<t; i++){
                double R[][] = inst.prod(inst.pow(inst.A,t-1-i), inst.B);
                for(int j=0; j<2; j++){
                    for(int k=0; k<4; k++){
                        exp[t][k] += R[k][j] * Ut(inst,codif,i,j);
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

    @Override
    public PPDCPObjective build(PPDCPFactory mem) throws Exception {
        return new PPDCPObjective();
    }

    @Override
    public void printer(PPDCPFactory prob, StreamPrinter com, cReal codif) throws Exception {
        super.printer(prob, com, codif);
        com.printDbl("beta", prob.inst.GOAL(beta));
        com.printDbl("delta", prob.inst.DELTA(delta));
        com.printDbl("vel_penalty", vel_penalty);
        com.printDbl("obj_val",  abs_value()-prob.inst.GOAL(beta)-prob.inst.DELTA(delta));
        if(PPDCPInstance.PLOT){
            double vMt[][] = Mt(prob.inst, codif);
            double total_dist = 0;
            for(int t=0; t<prob.inst.T; t++){
                 total_dist += prob.inst.dist2_position(vMt[t], vMt[t+1]);
            }
            prob.inst.plot(String.format("%s - unknow %%", "OBG"), vMt, null, abs_value(), total_dist, -1, prob.best().time_now());
            //prob.inst.plot(Mt(prob.inst, codif));
        }
    }
    @Override
    public void results(PPDCPFactory prob, LinkerResults com, cReal codif) throws Exception {
        super.results(prob, com, codif);
        com.writeDbl("beta", prob.inst.GOAL(beta));
        com.writeDbl("delta", prob.inst.DELTA(delta));
        com.writeDbl("vel_penalty", vel_penalty);
        com.writeDbl("obj_val", abs_value()-prob.inst.GOAL(beta)-prob.inst.DELTA(delta));
        
        double vMt[][] = Mt(prob.inst, codif);
        double M[][] = prob.inst.trans(vMt);
        com.writeArray("PPDCP", "Mt[0]", M[0]);
        com.writeArray("PPDCP", "Mt[1]", M[1]);
        com.writeArray("PPDCP", "Mt[2]", M[2]);
        com.writeArray("PPDCP", "Mt[3]", M[3]);
        double U[][] = new double[2][prob.inst.T];
        for(int t=0; t<prob.inst.T; t++){
            U[0][t] = Ut(prob.inst, codif, t, 0);
            U[1][t] = Ut(prob.inst, codif, t, 1);
        }
        
        com.writeArray("PPDCP", "Ut[0]", U[0]);
        com.writeArray("PPDCP", "Ut[1]", U[1]);
        
        if (prob.inst.online) {
            PrintStream output = new PrintStream("./rote.txt");
            output.println("<is feasible>");
            if (vMt == null) {
                output.println(false);
            } else {
                output.println(true);
                output.println("<T>");
                output.println(prob.inst.T);
                output.println("<waypoints: id  x   y   z>");
                for (int t = 1; t < prob.inst.T + 1; t++) {
                    output.printf(Locale.ENGLISH, "%d\t%g\t%g\t%g\n", t-1, vMt[t][0], vMt[t][2], 8000.0);
                }
            }
            output.close();
        }
        double total_dist = 0;
        for(int t=0; t<prob.inst.T; t++){
             total_dist += prob.inst.dist2_position(vMt[t], vMt[t+1]);
        }
        prob.inst.plot(String.format("%s - unknow %%", "OBG"), vMt, null, abs_value(), total_dist, -1, prob.best().time_now());
        //prob.inst.plot(vMt);
        prob.inst.save();
    }
    
    
    /*
    public static double Delta(PPDCPFactory prob, int j, int t, double Mt[][]){
        double max = Integer.MIN_VALUE;
        for(int i=0; i<prob.inst.bji[j].length; i++){
            double exp = prob.inst.chi(j, t, i);
            for(int k=0; k<4; k++){
                exp += prob.inst.aji[j][i][k] * Mt[t][k];
            }
            exp = exp / prob.inst.psi(j, t, i);
            max = Math.max(max, exp);
        }
        return -Math.min(0, max);
    }
     */

    
}
