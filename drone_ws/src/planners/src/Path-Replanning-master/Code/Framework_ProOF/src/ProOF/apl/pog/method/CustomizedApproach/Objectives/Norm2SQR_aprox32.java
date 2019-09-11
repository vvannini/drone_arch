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
public class Norm2SQR_aprox32 implements AddObjective {
    private final PPDCPInstance inst;

    public Norm2SQR_aprox32(PPDCPInstance inst) {
        this.inst = inst;
    }
    @Override
    public IloNumExpr objective(CplexExtended cplex, IloNumVar[][] Ut, IloNumVar[][] MUt) throws Exception {
        IloNumExpr obj = null;

        IloNumVar NUM[][][] = cplex.numVarArray(inst.T, 2, inst.NORM2_precision, 0, inst.UMAX / inst.NORM2_precision, "NUM");

        IloNumVar Vt[][] = cplex.numVarArray(inst.T, 2, 0, Double.POSITIVE_INFINITY, "V");
        //------------------------ (NOM-2) ^ 2 -----------------------------
        for (int t = 0; t < inst.T; t++) {
            for (int j = 0; j < 2; j++) {
                cplex.addGe(Vt[t][j], cplex.prod(+1, Ut[t][j]));
                cplex.addGe(Vt[t][j], cplex.prod(-1, Ut[t][j]));
            }
        }
        for (int t = 0; t < inst.T; t++) {
            for (int j = 0; j < 2; j++) {
                IloNumExpr sum_D = null;
                for (int m = 0; m < inst.NORM2_precision; m++) {
                    if (sum_D == null) {
                        sum_D = NUM[t][j][m];
                    } else {
                        sum_D = cplex.sum(sum_D, NUM[t][j][m]);
                    }
                }
                cplex.addEq(Vt[t][j], sum_D);
            }
        }
        for (int t = 0; t < inst.T; t++) {
            for (int j = 0; j < 2; j++) {
                for (int m = 0; m < inst.NORM2_precision; m++) {
                    double x0 = (m * inst.UMAX) / inst.NORM2_precision;
                    double x1 = ((m + 1) * inst.UMAX) / inst.NORM2_precision;
                    double custo0 = F(x0);
                    double custo1 = F(x1);
                    double a = (custo1 - custo0) / (x1 - x0);
                    if (obj == null) {
                        obj = cplex.prod(inst.P_Obj * a, NUM[t][j][m]);
                    } else {
                        obj = cplex.sum(obj, cplex.prod(inst.P_Obj * a, NUM[t][j][m]));
                    }
                }
            }
        }
        return obj;
    }

    public double F(double x) {  //minimize norm-2 ^ 2
        return x * x;
    }
}
