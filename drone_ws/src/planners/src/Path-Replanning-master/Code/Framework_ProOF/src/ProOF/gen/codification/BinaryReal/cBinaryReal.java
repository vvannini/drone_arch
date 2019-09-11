/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.codification.BinaryReal;

import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.codification.Codification;

/**
 *
 * @author marcio
 */
public final class cBinaryReal<Prob extends Problem> extends Codification<Prob, cBinaryReal> {
    public final boolean X[][];

    public cBinaryReal(int size, int length) {
        this.X = new boolean[size][length];
    }
    
    public double[] X(){
        double x[] = new double[X.length];
        for(int i=0; i<X.length; i++){
            x[i] = X(i);
        }
        return x;
    }
    public double X(int i){
        double s = 0;
        for(int j=0; j<X[i].length; i++){
            s *= 2;
            s += X[i][j] ? 1 : 0;
        }
        return s/(Math.pow(2, X[i].length)-1);
    }
    public double X(int i, double a, double b){
        return a + X(i)*(b-a);
    }
    
    @Override
    public void copy(Prob mem, cBinaryReal source) throws Exception {
        for(int i=0; i<source.X.length; i++){
            System.arraycopy(source.X[i], 0, this.X[i], 0, this.X[i].length);
        }
    }
    @Override
    public cBinaryReal build(Prob mem) throws Exception {
        return new cBinaryReal(X.length, X[0].length);
    }

}
