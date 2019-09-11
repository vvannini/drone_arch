/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.gen.codification.real;

import ProOF.opt.abst.problem.meta.objective.BoundDbl;



/**
 *
 * @author marcio
 */
public abstract class aRealMultiObj{
    private ifunction objs[] = New();
    
    public abstract String name();
    public abstract int size();
    public abstract ifunction[] New();

    public int goals(){
        return objs.length;
    }
    public final double F(int i, double... X) {
        return objs[i].F(X);
    }
    public final double decode(double x, double min, double max){
        return min + x*(max-min);
    }

    public BoundDbl bounds() {
        return BoundDbl.defalt;
    }
}
