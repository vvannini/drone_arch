/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.method.CustomizedApproach.Obstacle;

import ProOF.apl.pog.method.CustomizedApproach.AddRestrictions;
import static ProOF.apl.pog.method.CustomizedApproach.AddRestrictions.ID_RELAXATION;
import static ProOF.apl.pog.method.CustomizedApproach.AddRestrictions.ID_UPERBOUND;
import static ProOF.apl.pog.method.CustomizedApproach.AddRestrictions.ID_VARIABLE;
import ProOF.apl.pog.problem.PPDCP.PPDCPInstance;
import ProOF.CplexExtended.CplexExtended;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import java.util.LinkedList;

/**
 *
 * @author marcio
 */
public class REST_new extends Shared implements AddRestrictions<REST_new> {
    public REST_new(PPDCPInstance inst) {
        super(inst);
    }
    private IloIntVar[][][] Zjti = null;
    private IloIntVar[][][] DyZjti = null;

    private IloNumVar[][] Djt = null;
    private IloNumVar[][] DyDjt = null;
    private IloNumVar[][][] InDjti = null;
    private IloNumVar[][] UDti = null;

    private int ID = -1;

    
    @Override
    public void fix(CplexExtended cplex, REST_new tofix) throws IloException{
        System.out.println("REST_new fix Zjti");
        fix_Zjti( Zjti, tofix.Zjti(cplex, tofix.Zjti));
    }
    @Override
    public IloNumExpr addRestrictions(CplexExtended cplex, IloNumVar[][] Ut, IloNumVar[][] MUt, int ID, boolean tofix) throws Exception {
        this.ID = ID;
        Zjti = create_Zjti(cplex);
        DyZjti = create_DyZjti(cplex);
        System.out.println("ID = "+ID);
        if(tofix){
            
        }else{
            System.out.println("REST_new addModule restriction");
            IloNumVar alfa[][][][] = create_alfa(cplex);
            addModuleAlfa(cplex, Zjti, alfa);
            addSumZjti_2(cplex, Zjti, alfa);

            IloNumVar DyAlfa[][][][] = create_DyAlfa(cplex);
            addDyModuleAlfa(cplex, DyZjti, DyAlfa);
            addDySumZjti_2(cplex, DyZjti, DyAlfa);
        }

        IloNumExpr obj = null;
        switch(ID){
            case ID_RELAXATION:
                System.out.println("ID_RELAXATION");
                ObstacleAvoidanceFix(cplex, MUt, Zjti, AlocationFix());
                DyObstacleAvoidanceFix(cplex, MUt, DyZjti, AlocationFix());
                break;
            case ID_UPERBOUND:
                System.out.println("ID_UPERBOUND");
                ObstacleAvoidanceFix(cplex, MUt, Zjti, AlocationFix()); 
                DyObstacleAvoidanceFix(cplex, MUt, DyZjti, AlocationFix());
                break;
            case ID_VARIABLE:
                System.out.println("ID_VARIABLE");
                Djt = create_Djt(cplex, inst.DN);
                obj = ObstacleAvoidanceFree(cplex, MUt, Zjti, Djt, inst.DN);
                DyDjt = create_DyDjt(cplex, inst.DN);
                obj = DyObstacleAvoidanceFree(obj, cplex, MUt, DyZjti, DyDjt, inst.DN);
                InDjti = create_InDjti(cplex, inst.DN);
                obj = InObstacleAvoidanceFree(obj, cplex, MUt, InDjti, inst.DN);
                if(inst.U!=null){
                    UDti = create_UDti(cplex, inst.DN);
                    obj = UControlAvoidanceFree(obj, cplex, Ut, UDti, inst.DN);
                }
                addDelta(cplex, Djt, DyDjt, InDjti, UDti);
                break;
            default: throw new Exception("Optiom ID = "+ID+", not defined");
        }
        return obj;
    }
    @Override
    public IloIntVar[][][] getZjti() throws IloException {
        return Zjti;
    }
    @Override
    public int[][][] getZjti(CplexExtended cplex, IloNumVar[][] MUt, boolean obst[][]) throws Exception{
        int [][][] bZjti = new int[inst.J][inst.T+1][];
        for(int j=0; j<inst.J; j++){
            for(int t=0; t<inst.T+1; t++){
                bZjti[j][t] = new int[inst.G(j)];
            }
        }
        switch(ID){
            case ID_RELAXATION: ObstacleAvoidanceFixZjti(cplex, MUt, bZjti, obst, inst.DELTA); break;
            case ID_UPERBOUND:  ObstacleAvoidanceFixZjti(cplex, MUt, bZjti, obst, inst.DELTA / (inst.J * (inst.T + 1))); break;
            case ID_VARIABLE:   ObstacleAvoidanceFreeZjti(cplex, MUt, bZjti, obst); break;
            default: throw new Exception("Optiom ID = "+ID+", not defined");
        }
        return bZjti;
    }
    @Override
    public LinkedList<IloNumVar> AlocationFree(int t){
        if(ID == ID_RELAXATION || ID == ID_UPERBOUND){
            return null;
        }
        LinkedList<IloNumVar> list = new LinkedList<IloNumVar>();
        for(int j=0; j<inst.J; j++){
            list.addLast(Djt[j][t]);
        }
        list.add(null);
        for(int j=0; j<inst.E; j++){
            list.addLast(DyDjt[j][t]);
        }
        list.add(null);
        for(int j=0; j<inst.I; j++){
            for(int i=0; i<inst.I(j, t); i++){
                list.addLast(InDjti[j][t][i]);
            }
        }
        if(UDti!=null){
            list.add(null);
            if(t<inst.T){
                for(int i=0; i<inst.U.length(); i++){
                    list.addLast(UDti[t][i]);
                }
            }
        }
        return list;
    }
    @Override
    public double AlocationFix(){
        switch(ID){
            case ID_RELAXATION: return inst.DELTA;
            case ID_UPERBOUND:  return inst.DELTA / (inst.J * (inst.T + 1));
            default: return inst.DELTA/Math.pow(2, inst.DN);
        }
    }
    
    protected IloNumVar[][][][] create_alfa(CplexExtended cplex) throws IloException{
        IloNumVar alfa[][][][];
        //inicia a nova variavel
        alfa = new IloNumVar[inst.J][inst.T + 1][][];
        for (int j = 0; j < inst.J; j++) {
            for (int t = 0; t < inst.T + 1; t++) {
                alfa[j][t] = new IloNumVar[inst.G(j)][inst.G(j)];
                for (int i = 0; i < inst.G(j); i++) {
                    for (int l = 0; l < inst.G(j); l++) {
                        //Cria variável
                        alfa[j][t][i][l] = cplex.numVar(0, 1, "alfa("+(j+1+","+(t+1)+","+(i+1)+","+(l+1))+")");
                    }
                }

            }
        }
        return alfa;
    }
    protected IloNumVar[][][][] create_DyAlfa(CplexExtended cplex) throws IloException{
        IloNumVar alfa[][][][];
        //inicia a nova variavel
        alfa = new IloNumVar[inst.E][inst.T + 1][][];
        for (int j = 0; j < inst.E; j++) {
            for (int t = 0; t < inst.T + 1; t++) {
                alfa[j][t] = new IloNumVar[inst.E(j,t)][inst.E(j,t)];
                for (int i = 0; i < inst.E(j,t); i++) {
                    for (int l = 0; l < inst.E(j,t); l++) {
                        //Cria variável
                        alfa[j][t][i][l] = cplex.numVar(0, 1);
                    }
                }

            }
        }
        return alfa;
    }
    private void addSumZjti_2(CplexExtended cplex, IloIntVar[][][] Zjti, IloNumVar alfa[][][][]) throws IloException{
        System.out.println("inst.ALPHA = "+inst.ALPHA);
        //Segunda restrição
        for (int j = 0; j < inst.J; j++) {
            for (int t = 0; t < inst.T + 1; t++) {
                IloNumExpr exp = null;
                for (int i = 0; i < inst.G(j); i++) {
                    exp = cplex.SumProd(exp, 1, Zjti[j][t][i]);
                    IloNumExpr exp2 = null;
                    for (int l = 0; l < inst.G(j); l++) {
                        exp2 = cplex.SumProd(exp2, inst.ALPHA, alfa[j][t][i][l]);
                    }
                    exp = cplex.sum(exp, cplex.prod(exp2, -1));
                }
                cplex.addGe(exp, 1, "NEW2." + (j + 1) + "," + (t + 1));
            }
        }
    }
    private void addDySumZjti_2(CplexExtended cplex, IloIntVar[][][] DyZjti, IloNumVar alfa[][][][]) throws IloException{
        //Segunda restrição
        for (int j = 0; j < inst.E; j++) {
            for (int t = 0; t < inst.T + 1; t++) {
                IloNumExpr exp = null;
                for (int i = 0; i < inst.E(j,t); i++) {
                    exp = cplex.SumProd(exp, 1, DyZjti[j][t][i]);
                    IloNumExpr exp2 = null;
                    for (int l = 0; l < inst.E(j,t); l++) {
                        exp2 = cplex.SumProd(exp2, inst.ALPHA, alfa[j][t][i][l]);
                    }
                    exp = cplex.sum(exp, cplex.prod(exp2, -1));
                }

                cplex.addGe(exp, 1, "DyNEW2." + (j + 1) + "," + (t + 1));
            }
        }
    }
    private void addModuleAlfa(CplexExtended cplex, IloIntVar[][][] Zjti, IloNumVar alfa[][][][]) throws IloException{
       //-----------------------------Restrições novas------------------------
       //Primeira restriçao
       for (int j = 0; j < inst.J; j++) {
           for (int i = 0; i < inst.G(j); i++) {
               for (int l = 0; l < inst.G(j); l++) {
                   if (l != i) {
                       IloNumExpr exp0 = cplex.sum(cplex.prod(alfa[j][0][i][l], -1), Zjti[j][0][i], Zjti[j][0][l]);
                       cplex.addLe(exp0, 1, "NEWexp1." + (j + 1) + "," + (i + 1) + "," + (l + 1) + "," + 1);

                       for (int t = 1; t < inst.T + 1; t++) {
                           IloNumExpr exp1 = cplex.sum(cplex.prod(alfa[j][t][i][l], -1), Zjti[j][t][i], cplex.prod(Zjti[j][t - 1][i], -1), Zjti[j][t][l], cplex.prod(Zjti[j][t - 1][l], -1));
                           cplex.addLe(exp1, 1, "NEWexp1." + (j + 1) + "," + (i + 1) + "," + (l + 1) + "," + (t + 1));

                           IloNumExpr exp2 = cplex.sum(cplex.prod(alfa[j][t][i][l], -1), Zjti[j][t][i], cplex.prod(Zjti[j][t - 1][i], -1), cplex.prod(Zjti[j][t][l], -1), Zjti[j][t - 1][l]);
                           cplex.addLe(exp2, 1, "NEWexp2." + (j + 1) + "," + (i + 1) + "," + (l + 1) + "," + (t + 1));

                           IloNumExpr exp3 = cplex.sum(cplex.prod(alfa[j][t][i][l], -1), cplex.prod(Zjti[j][t][i], -1), Zjti[j][t - 1][i], Zjti[j][t][l], cplex.prod(Zjti[j][t - 1][l], -1));
                           cplex.addLe(exp3, 1, "NEWexp3." + (j + 1) + "," + (i + 1) + "," + (l + 1) + "," + (t + 1));

                           IloNumExpr exp4 = cplex.sum(cplex.prod(alfa[j][t][i][l], -1), cplex.prod(Zjti[j][t][i], -1), Zjti[j][t - 1][i], cplex.prod(Zjti[j][t][l], -1), Zjti[j][t - 1][l]);
                           cplex.addLe(exp4, 1, "NEWexp4." + (j + 1) + "," + (i + 1) + "," + (l + 1) + "," + (t + 1));

                       }
                   } else {
                       for (int t = 0; t < inst.T + 1; t++) {
                           alfa[j][t][i][l].setUB(0);
                       }
                   }
               }
           }
       }
       for (int j = 0; j < inst.J; j++) {
           for (int t = 0; t < inst.T + 1; t++) {
               for (int i = 0; i < inst.G(j); i++) {
                    for (int l = 0; l < inst.G(j); l++) {
                        if (l != i) {
                            cplex.addEq(alfa[j][t][i][l], alfa[j][t][l][i], "Eq." + (j + 1) + "," + (i + 1) + "," + (l + 1) + "," + (t + 1));
                        }
                    }
               }
           }
       }
   }
    private void addDyModuleAlfa(CplexExtended cplex, IloIntVar[][][] DyZjti, IloNumVar alfa[][][][]) throws IloException{
        //-----------------------------Restrições novas------------------------
       //Primeira restriçao
       for (int j = 0; j < inst.E; j++) {
           for (int t = 0; t < inst.T + 1; t++) {
               for (int i = 0; i < inst.E(j,t); i++) {
                    for (int l = 0; l < inst.E(j,t); l++) {
                        if (l != i) {
                            if(t==0){
                                IloNumExpr exp0 = cplex.sum(cplex.prod(alfa[j][0][i][l], -1), DyZjti[j][0][i], DyZjti[j][0][l]);
                                cplex.addLe(exp0, 1, "DyExp1." + (j + 1) + "," + (i + 1) + "," + (l + 1) + "," + 1);
                            }else{
                                IloNumExpr exp1 = cplex.sum(cplex.prod(alfa[j][t][i][l], -1), DyZjti[j][t][i], cplex.prod(DyZjti[j][t - 1][i], -1), DyZjti[j][t][l], cplex.prod(DyZjti[j][t - 1][l], -1));
                                cplex.addLe(exp1, 1, "DyExp1." + (j + 1) + "," + (i + 1) + "," + (l + 1) + "," + (t + 1));

                                IloNumExpr exp2 = cplex.sum(cplex.prod(alfa[j][t][i][l], -1), DyZjti[j][t][i], cplex.prod(DyZjti[j][t - 1][i], -1), cplex.prod(DyZjti[j][t][l], -1), DyZjti[j][t - 1][l]);
                                cplex.addLe(exp2, 1, "DyExp2." + (j + 1) + "," + (i + 1) + "," + (l + 1) + "," + (t + 1));

                                IloNumExpr exp3 = cplex.sum(cplex.prod(alfa[j][t][i][l], -1), cplex.prod(DyZjti[j][t][i], -1), DyZjti[j][t - 1][i], DyZjti[j][t][l], cplex.prod(DyZjti[j][t - 1][l], -1));
                                cplex.addLe(exp3, 1, "DyExp3." + (j + 1) + "," + (i + 1) + "," + (l + 1) + "," + (t + 1));

                                IloNumExpr exp4 = cplex.sum(cplex.prod(alfa[j][t][i][l], -1), cplex.prod(DyZjti[j][t][i], -1), DyZjti[j][t - 1][i], cplex.prod(DyZjti[j][t][l], -1), DyZjti[j][t - 1][l]);
                                cplex.addLe(exp4, 1, "DyExp4." + (j + 1) + "," + (i + 1) + "," + (l + 1) + "," + (t + 1));
                            }
                        } else {
                            alfa[j][t][i][l].setUB(0);
                        }
                    }
                }
           }
       }
       for (int j = 0; j < inst.E; j++) {
           for (int t = 0; t < inst.T + 1; t++) {
               for (int i = 0; i < inst.E(j,t); i++) {
                    for (int l = 0; l < inst.E(j,t); l++) {
                        if (l != i) {
                            cplex.addEq(alfa[j][t][i][l], alfa[j][t][l][i], "DyEq." + (j + 1) + "," + (i + 1) + "," + (l + 1) + "," + (t + 1));
                        }
                    }
               }
           }
       }
   }
}
