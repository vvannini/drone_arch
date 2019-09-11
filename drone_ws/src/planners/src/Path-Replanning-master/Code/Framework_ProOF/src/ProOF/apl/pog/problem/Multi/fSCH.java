/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.Multi;

import ProOF.apl.pog.gen.codification.real.aRealMultiObj;
import ProOF.apl.pog.gen.codification.real.ifunction;

/**
 *
 * @author marcio
 */
public class fSCH extends aRealMultiObj{
    @Override
    public String name() {
        return "SCH";
    }
    @Override
    public int size() {
        return 1;
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
            double x = decode(X[0], -1e3, +1e3);;
            return x*x;
        }
    }
    private class f2 implements ifunction{
        @Override
        public double F(double... X) {
            double x = decode(X[0], -1e3, +1e3);;
            return (x-2)*(x-2);
        }
    }
}
