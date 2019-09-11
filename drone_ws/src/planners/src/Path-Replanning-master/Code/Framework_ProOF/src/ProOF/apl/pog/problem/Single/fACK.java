/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.Single;

import ProOF.apl.pog.gen.codification.real.aRealSingleObj;

/**
 *
 * @author marcio
 */
public class fACK extends aRealSingleObj{
    @Override
    public String name() {
        return "ACK-10";
    }
    @Override
    public int size() {
        return 10;
    }
    @Override
    public double F(double... X) {
        double sum1 = 0;
        double sum2 = 0;
        for(double x : X){
            //Dedouble x : Xcoder
            double xi = decode(x, -30, +30);
            sum1 += xi * xi;
            sum2 += Math.cos(2*Math.PI*xi);
        }
        return -20.0*Math.exp(-0.02*Math.sqrt(sum1/X.length)) - Math.exp(sum2/X.length) + 20 + Math.E;
    }
}
