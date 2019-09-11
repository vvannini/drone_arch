/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.BlackMore.LP2D;

import ProOF.apl.pog.problem.BlackMore.BMControl;
import ProOF.apl.pog.problem.BlackMore.BMProblem;


/**
 *
 * @author marcio
 */
public class LP2DControl extends BMControl{
    public final double ax;
    public final double ay;
    
    public LP2DControl(LP2DControl other) {
        this.ax = other.ax;
        this.ay = other.ay;
    }
    
    public LP2DControl(BMProblem prob) {
        this.ax = prob.rnd.nextDouble(-prob.inst.UMAX, +prob.inst.UMAX);
        this.ay = prob.rnd.nextDouble(-prob.inst.UMAX, +prob.inst.UMAX);
    }
    
    
    @Override
    public double norm1() throws Exception{
        return Math.abs(ax) + Math.abs(ay);
    }
    @Override
    public double norm2() throws Exception{
        return Math.sqrt(ax*ax + ay*ay);
    }
    @Override
    public double norm2sqr() throws Exception{
        return ax*ax + ay*ay;
    }
}
