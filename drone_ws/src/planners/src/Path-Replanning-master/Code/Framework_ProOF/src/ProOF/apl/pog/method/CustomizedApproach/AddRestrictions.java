/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.method.CustomizedApproach;

import ProOF.CplexExtended.CplexExtended;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import java.util.LinkedList;

/**
 *
 * @author marcio
 * @param <R>
 */
public interface AddRestrictions<R> {
    public static final int ID_RELAXATION = 0;
    public static final int ID_UPERBOUND = 1;
    public static final int ID_VARIABLE = 2;
    
    public void fix(CplexExtended cplex, R tofix) throws IloException;
    public IloNumExpr addRestrictions(CplexExtended cplex, IloNumVar Ut[][], IloNumVar MUt[][], int ID, boolean tofix) throws Exception;
    public LinkedList<IloNumVar> AlocationFree(int t);
    public double AlocationFix();
    public IloNumVar[][][] getZjti() throws IloException;
    public int[][][] getZjti(CplexExtended cplex, IloNumVar[][] MUt, boolean obst[][]) throws Exception;
}
