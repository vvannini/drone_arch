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
public class fFON extends aRealMultiObj{
    @Override
    public String name() {
        return "FON";
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
            for(double x : X){
                x = decode(x, -4, +4);
                sum += (x-1/Math.sqrt(3))*(x-1/Math.sqrt(3));
            }
            return 1 - Math.exp(-sum);
        }
    }
    private class f2 implements ifunction{
        @Override
        public double F(double... X) {
            double sum = 0;
            for(double x : X){
                x = decode(x, -4, +4);
                sum += (x+1/Math.sqrt(3))*(x+1/Math.sqrt(3));
            }
            return 1 - Math.exp(-sum);
        }
    }
}
