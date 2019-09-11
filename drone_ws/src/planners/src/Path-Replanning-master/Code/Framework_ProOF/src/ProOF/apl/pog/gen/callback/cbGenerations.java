/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.gen.callback;

import ProOF.opt.abst.problem.meta.codification.Codification;

/**
 *
 * @author marcio
 */
public interface cbGenerations<Codif extends Codification> {
    public void generations(Codif best, int ID, int MaxID, double percent) throws Exception ;
}
