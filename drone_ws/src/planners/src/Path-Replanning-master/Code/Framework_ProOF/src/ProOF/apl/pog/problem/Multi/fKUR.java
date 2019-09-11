/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.Multi;

import ProOF.apl.pog.gen.codification.real.aRealMultiObj;
import ProOF.apl.pog.gen.codification.real.ifunction;
import ProOF.opt.abst.problem.meta.objective.BoundDbl;

/**
 *
 * @author marcio
 */
public class fKUR extends aRealMultiObj{
    @Override
    public String name() {
        return "KUR";
    }
    private static final BoundDbl bound = new BoundDbl(-1e+4, +1e+4, 1e-2);
    @Override
    public BoundDbl bounds() {
        return bound;
    }
    @Override
    public int size() {
        return 3;
    }
    @Override
    public ifunction[] New() {
        return new ifunction[]{
            new f1(),
            new f2()
        };
    }
    private class f1 implements ifunction{
        @Override
        public double F(double... X) {
            double sum = 0;
            for(int i=0; i<X.length-1; i++){
                double xi = decode(X[i], -5, +5);
                double xi_1 = decode(X[i+1], -5, +5);
                
                sum += -10*Math.exp(0.2*Math.sqrt(xi*xi + xi_1*xi_1));
            }
            return sum;
        }
    }
    private class f2 implements ifunction{
        @Override
        public double F(double... X) {
            double sum = 0;
            for(double x : X){
                x = decode(x, -5, +5);
                sum += Math.pow(Math.abs(x),0.8) + 5*Math.sin(Math.pow(x, 3));
            }
            return sum;
        }
    }
}
