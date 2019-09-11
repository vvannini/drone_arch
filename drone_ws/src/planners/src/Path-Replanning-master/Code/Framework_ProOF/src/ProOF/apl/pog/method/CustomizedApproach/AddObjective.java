/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.method.CustomizedApproach;

import ProOF.CplexExtended.CplexExtended;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;

/**
 *
 * @author marcio
 */
public interface AddObjective {
    public IloNumExpr objective(CplexExtended cplex, IloNumVar Ut[][], IloNumVar MUt[][]) throws Exception;
}
