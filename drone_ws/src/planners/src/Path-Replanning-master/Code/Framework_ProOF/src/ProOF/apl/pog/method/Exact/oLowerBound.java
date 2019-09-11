/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.method.Exact;


import ProOF.opt.abst.problem.meta.codification.Operator;

/**
 *
 * @author marcio
 * @param <Prob>
 * @param <No>
 */
public abstract class oLowerBound <
        Prob extends aComb, No extends ExactNode
> extends Operator {
    public abstract double lower_bound(Prob prob, No base) throws Exception;
}
