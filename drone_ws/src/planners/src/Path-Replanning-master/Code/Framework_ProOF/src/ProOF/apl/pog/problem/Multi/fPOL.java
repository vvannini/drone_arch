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
public class fPOL extends aRealMultiObj{
    @Override
    public String name() {
        return "POL";
    }

    private static final BoundDbl bound = new BoundDbl(-1e+8, +1e+8, 1e-6);
    @Override
    public BoundDbl bounds() {
        return bound;
    }
    
    @Override
    public int size() {
        return 2;
    }
    @Override
    public ifunction[] New() {
        return new ifunction[]{
            new f1(),
            new f2()
        };
    }
    private static final double A1 = B1(1,2);
    private static final double A2 = B2(1,2);
    private static final double B1(double x1, double x2){
        return 0.5*Math.sin(x1) - 2*Math.cos(x1) + Math.sin(x2) - 1.5*Math.cos(x2);
    }
    private static final double B2(double x1, double x2){
        return 1.5*Math.sin(x1) - Math.cos(x1) + 2*Math.sin(x2) - 0.5*Math.cos(x2);
    }
    
    private class f1 implements ifunction{
        @Override
        public double F(double... X) {
            double x1 = decode(X[0], -Math.PI, +Math.PI);
            double x2 = decode(X[1], -Math.PI, +Math.PI);
            double B1 = B1(x1,x2);
            double B2 = B2(x1,x2);
            return 1 + Math.pow(A1-B1, 2) + Math.pow(A2-B2, 2);
        }
    }
    private class f2 implements ifunction{
        @Override
        public double F(double... X) {
            double x1 = decode(X[0], -Math.PI, +Math.PI);
            double x2 = decode(X[1], -Math.PI, +Math.PI);
            return Math.pow(x1+3, 2) + Math.pow(x2+1, 2);
        }
    }
}
