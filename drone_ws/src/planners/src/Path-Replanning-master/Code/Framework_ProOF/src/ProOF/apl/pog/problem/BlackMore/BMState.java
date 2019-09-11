/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.BlackMore;


/**
 *
 * @author marcio
 */
public abstract class BMState {
    public abstract double NFZ_violation(BMProblem prob, int j, int t) throws Exception;
    public abstract double dist_obj(BMProblem prob) throws Exception;
}
