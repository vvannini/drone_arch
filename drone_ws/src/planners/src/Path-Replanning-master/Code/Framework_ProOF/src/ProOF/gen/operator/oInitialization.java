/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.operator;


import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.codification.Codification;
import ProOF.opt.abst.problem.meta.codification.Operator;

/**
 *
 * @author marcio
 */
public abstract class oInitialization <
        Prob extends Problem, Codif extends Codification
> extends Operator {
    public abstract void initialize(Prob prob, Codif ind) throws Exception;
}
