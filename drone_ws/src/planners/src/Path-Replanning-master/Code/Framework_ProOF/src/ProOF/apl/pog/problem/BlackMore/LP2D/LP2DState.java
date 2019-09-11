/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.BlackMore.LP2D;

import ProOF.apl.pog.problem.BlackMore.*;
import jsc.distributions.Normal;

/**
 *
 * @author marcio
 */
public class LP2DState extends BMState{
    public final double x;
    public final double y;

    public LP2DState(LP2DState other) {
        this.x = other.x;
        this.y = other.y;
    }
    @Override
    public double dist_obj(BMProblem prob) {
        return Math.abs(x-prob.inst.Xgoal[0]) + Math.abs(y-prob.inst.Xgoal[2]);
    }
    @Override
    public double NFZ_violation(BMProblem prob, int j, int t){
        double max = -Integer.MAX_VALUE;
        for(int i=0; i<prob.inst.G(j); i++){
            double exp = prob.inst.Ob[j].lines[i].ax*x + prob.inst.Ob[j].lines[i].ay*y - prob.inst.Ob[j].lines[i].b;
            exp = exp/prob.inst.Rjti[j][t][i];
            max = Math.max(max, exp);
        }
        return (1-Normal.standardTailProb(max, false))/2;
    }
    
    
}
