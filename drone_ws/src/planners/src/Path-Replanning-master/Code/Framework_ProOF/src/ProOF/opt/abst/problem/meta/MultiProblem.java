/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.opt.abst.problem.meta;

import ProOF.opt.abst.problem.meta.codification.Codification;
import ProOF.opt.abst.problem.meta.objective.MultiObjective;

/**
 *
 * @author marcio
 */
public abstract class MultiProblem<B extends Best> extends Problem<B> {
    
    public abstract int goals() throws Exception;
    public double goal(int i, MultiObjective obj) throws Exception{
        return obj.abs_value(i);
    }
    public double goal(int i, Solution<?,MultiObjective,?,?> sol) throws Exception{
        return sol.objective.abs_value(i);
    }
    public int compareTo(int i, Solution<?,MultiObjective,?,?> a, Solution<?,MultiObjective,?,?> b){
        return a.objective.compareTo(i, b.objective);
    }
    
    @Override
    public abstract B best();
    @Override
    public abstract Codification build_codif() throws Exception;
    @Override
    public abstract MultiObjective build_obj() throws Exception;
}

