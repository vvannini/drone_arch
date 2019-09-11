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
public class fFunction extends aRealSingleObj{
    @Override
    public String name() {
        return "Function_v3";
    }
    @Override
    public int size() {
        return 1;
    }
    @Override
    public double F(double... X) {
        double x = decode(X[0], -1, +2);
        return -(x * Math.sin(10*Math.PI*x)+1);
    }
}