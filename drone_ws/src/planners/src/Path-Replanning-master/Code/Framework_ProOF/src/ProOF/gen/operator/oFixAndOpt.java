/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.operator;

import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.Solution;
import ProOF.opt.abst.problem.meta.codification.Operator;

/**
 *
 * @author marcio
 */
public abstract class oFixAndOpt <
        Mem extends Problem, Sol extends Solution
> extends Operator {
    public abstract void initialize(Mem prob) throws Exception;
    public abstract Sol execute(Mem prob, Sol ind) throws Exception;
    public abstract void increment(Mem prob) throws Exception;
}
