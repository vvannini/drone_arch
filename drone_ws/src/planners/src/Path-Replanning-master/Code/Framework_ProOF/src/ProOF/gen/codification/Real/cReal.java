/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.codification.Real;

import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.codification.Codification;

/**
 *
 * @author marcio
 */
public class cReal<Prob extends Problem> extends Codification<Prob, cReal> {
    public final double X[];

    public cReal(int size) {
        this.X = new double[size];
    }
    
    public double X(int i){
        return X[i];
    }
    public double X(int i, double a, double b){
        return a + X[i]*(b-a);
    }
    
    @Override
    public void copy(Prob mem, cReal source) throws Exception {
        System.arraycopy(source.X, 0, this.X, 0, this.X.length);
    }
    @Override
    public cReal build(Prob mem) throws Exception {
        return new cReal(X.length);
    }

}
