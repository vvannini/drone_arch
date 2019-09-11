/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.jsa.method.de;

import ProOF.opt.abst.problem.meta.codification.Codification;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.codification.Operator;

/**
 *
 * @author marcio
 */
public abstract class oRecombine <
        Mem extends Problem, Codif extends Codification
> extends Operator {
    public abstract Codif recombine(Mem mem, Codif ind1, Codif ind2, int rateCross) throws Exception;
}
