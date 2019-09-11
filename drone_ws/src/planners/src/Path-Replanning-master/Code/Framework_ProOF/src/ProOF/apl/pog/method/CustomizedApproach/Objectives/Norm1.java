/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.method.CustomizedApproach.Objectives;

import ProOF.apl.pog.method.CustomizedApproach.AddObjective;
import ProOF.apl.pog.problem.PPDCP.PPDCPInstance;
import ProOF.CplexExtended.CplexExtended;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;

/**
 *
 * @author marcio
 */
public class Norm1 implements AddObjective {
    private final PPDCPInstance inst;

    public Norm1(PPDCPInstance inst) {
        this.inst = inst;
    }
    @Override
    public IloNumExpr objective(CplexExtended cplex, IloNumVar[][] Ut, IloNumVar[][] MUt) throws Exception {
        IloNumExpr obj = null;
        IloNumVar Vt[][] = cplex.numVarArray(inst.T, 2, 0, Double.POSITIVE_INFINITY, "V");
        //------------------------ (NOM-1)  -----------------------------
        for (int t = 0; t < inst.T; t++) {
            for (int j = 0; j < 2; j++) {
                cplex.addGe(Vt[t][j], cplex.prod(+1, Ut[t][j]));
                cplex.addGe(Vt[t][j], cplex.prod(-1, Ut[t][j]));
            }
        }
        for (int t = 0; t < inst.T; t++) {
            if (obj == null) {
                obj = cplex.sum(cplex.prod(inst.P_Obj, Vt[t][0]), cplex.prod(inst.P_Obj, Vt[t][1]));
            } else {
                obj = cplex.sum(obj, cplex.prod(inst.P_Obj, Vt[t][0]), cplex.prod(inst.P_Obj, Vt[t][1]));
            }
        }
        return obj;
    }
}
