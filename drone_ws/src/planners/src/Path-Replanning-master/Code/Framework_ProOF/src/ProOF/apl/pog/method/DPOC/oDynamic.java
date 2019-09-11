/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.method.DPOC;


import ProOF.apl.pog.dynamic.aDyProblem;
import ProOF.opt.abst.problem.meta.codification.Operator;

/**
 *
 * @author marcio
 */
public abstract class oDynamic <
        Prob extends aDyProblem, State, Control
> extends Operator {
    public abstract Control control(Prob prob, State base) throws Exception;
}
