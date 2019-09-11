/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.BlackMore;

/**
 *
 * @author marcio
 */
public interface BMAddObj<State extends BMState, Control extends BMControl> {
    public double cost(BMProblem prob, State[] Xt, Control[] Ut) throws Exception;
}
