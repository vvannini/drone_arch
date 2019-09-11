/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.PPDCP.Old;

import ProOF.com.Linker.LinkerApproaches;
import ProOF.gen.codification.FunctionMulti.RealMulti;

/**
 * <pre>
 * function with 2 objectives
 * name   : FON(x[3])
 * domine : [-4, +4]
 * optimal: x1=x2=x3 in [-1/sqrt(3) ... +1/sqrt(3)]
 * </pre>
 * @author marcio
 */
public class PPDCPMulti extends RealMulti{
    private PPDCPInstanceOld inst = new PPDCPInstanceOld();
    @Override
    public String name() {
        return "PPDCP-Multi";
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        super.services(link);
        link.add(inst);
    }
    
    @Override
    public int size() throws Exception {
        return inst.T*2;
    }
    @Override
    public int goals() throws Exception {
        return 2;
    }
    @Override
    public double F(int goal, double[] X) throws Exception {
        //------------------------(11)---------------------------
        double Mt[][] = Mt(X);
        
        switch(goal){
            case 0: return f1(Mt, X);
            case 1: return f2(Mt, X);
        }
        throw new Exception("index of goal = "+goal+" not exists");
    }
    
    public double Ut(double X[], int t, int j){
        return decode(X[t*2+j], -3, +3);
    }
    
    public double[][] Mt(double... X){
        double exp[][] = new double[inst.T][4];
        //-------------------------------- (11) --------------------------------
        for(int t=0; t<inst.T; t++){
            for(int i=0; i<t; i++){
                double R[][] = inst.prod(inst.pow(inst.A,t-i), inst.B);
                for(int j=0; j<2; j++){
                    for(int k=0; k<4; k++){
                        exp[t][k] += R[k][j] * Ut(X, t, j);
                    }
                }
            }
            double AX0[][] = inst.prod(inst.pow(inst.A,t+1), inst.X0);
            for(int k=0; k<4; k++){
                exp[t][k] += AX0[k][0];
            }
        }
        return exp;
    }
    
    public double Delta(int j, int t, double Mt[][]){
        double min = Integer.MAX_VALUE;
        for(int i=0; i<inst.bji[j].length; i++){
            double exp = inst.chi(j, t, i);
            for(int k=0; k<4; k++){
                exp += inst.aji[j][i][k] * Mt[t][k];
            }
            exp = exp / inst.psi(j, t, i);
            min = Math.min(min, exp);
        }
        return Math.max(0, min);
    }
    
    private double f1(double Mt[][], double... X) {
        //------------------------[beta]---------------------------
        double beta = inst.norm2(Mt[inst.T-1], inst.Xgoal);
        
        //------------------------[goal]---------------------------
        double goal = 0;
        for(int t=0; t<inst.T; t++){
            goal += Ut(X, t, 0)*Ut(X, t, 0)+Ut(X, t, 1)*Ut(X, t, 1);
        }
        return inst.BETA(beta) + goal;
    }
    private double f2(double Mt[][], double... X) throws Exception {
        //------------------------[beta]---------------------------
        double delta = 0;
        for(int j=0; j<inst.J; j++){
            for(int t=0; t<inst.T; t++){
                delta += Delta(j, t, Mt);
            }
        }
        delta = Math.max(0, delta-inst.DELTA);

        return inst.DELTA(delta);
    }
    
}
