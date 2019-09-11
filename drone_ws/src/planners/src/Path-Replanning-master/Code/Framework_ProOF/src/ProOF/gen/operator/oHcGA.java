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
public abstract class oHcGA <
        Prob extends Problem, Sol extends Solution
> extends Operator {
    public abstract void initialize(Prob prob) throws Exception;
    public abstract void create(Prob prob, Sol ind) throws Exception;
    public abstract void update(Prob prob, Sol ind, int popSize) throws Exception;
}
