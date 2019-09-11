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
public class Norm2_aprox32 implements AddObjective {
    private final PPDCPInstance inst;

    public Norm2_aprox32(PPDCPInstance inst) {
        this.inst = inst;
    }
    @Override
    public IloNumExpr objective(CplexExtended cplex, IloNumVar[][] Ut, IloNumVar[][] MUt) throws Exception {
        IloNumExpr obj = null;
        IloNumVar Wt[] = cplex.numVarArray(inst.T, 0, +inst.UMAX, "Wt");
        for (int t = 0; t < inst.T; t++) {
            for (int n = 0; n < inst.NORM2_precision; n++) {
                IloNumExpr exp = cplex.sum(cplex.prod(Math.cos(2 * Math.PI * n / inst.NORM2_precision), Ut[t][0]), cplex.prod(Math.sin(2 * Math.PI * n / inst.NORM2_precision), Ut[t][1]));
                cplex.addLe(exp, Wt[t]);
            }
        }
        for (int t = 0; t < inst.T; t++) {
            if (obj == null) {
                obj = cplex.prod(inst.P_Obj, Wt[t]);
            } else {
                obj = cplex.sum(obj, cplex.prod(inst.P_Obj, Wt[t]));
            }
        }
        return obj;
    }
}
