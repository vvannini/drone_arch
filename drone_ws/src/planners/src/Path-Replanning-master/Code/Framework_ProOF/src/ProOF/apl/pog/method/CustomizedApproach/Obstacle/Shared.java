/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.method.CustomizedApproach.Obstacle;

import ProOF.apl.pog.problem.PPDCP.PPDCPInstance;
import ProOF.CplexExtended.CplexExtended;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import jsc.distributions.Normal;

/**
 *
 * @author marcio
 */
public class Shared {
    protected final PPDCPInstance inst;

    public Shared(PPDCPInstance inst) {
        this.inst = inst;
    }
    protected void addSumZjti_1(CplexExtended cplex, IloIntVar[][][] Zjti) throws IloException{
        for(int j=0; j<inst.J; j++){
            for(int t=0; t<inst.T+1; t++){
                IloNumExpr exp = null;
                for(int i=0; i<inst.G(j); i++){
                    if(exp==null){
                        exp = Zjti[j][t][i];
                    }else{
                        exp = cplex.sum(exp, Zjti[j][t][i]);
                    }
                }
                cplex.addGe(exp, 1, "SA."+(j+1)+","+(t+1));
            }
        }
    }
    public int[][][] Zjti(CplexExtended cplex, IloIntVar[][][] Zjti) throws IloException {
        int Rjti[][][] = new int[inst.J][inst.T+1][];
        for(int j=0; j<inst.J; j++){
            for(int t=0; t<inst.T+1; t++){
                  Rjti[j][t] = new int[inst.G(j)];
                  for(int i=0; i<inst.G(j); i++){
                      double v = cplex.getValue(Zjti[j][t][i]);
                      Rjti[j][t][i] = v>0.99 ? 1 : 0;
                  }
//                LinkedList<Integer> list = new LinkedList<Integer>();
//                for(int i=0; i<inst.bji[j].length; i++){
//                    double v = cplex.getValue(Zjti[j][t][i]);
//                    if(v>0.99){
//                        list.addLast(i);
//                    }
//                }
//                Rjti[j][t] = new int[list.size()];
//                int n=0;
//                for(int i : list){
//                    Rjti[j][t][n] = i;
//                    n++;
//                }
            }
        }
        return Rjti;
    }
    public IloIntVar[][][] create_Zjti(CplexExtended cplex) throws IloException{
        IloIntVar[][][] Zjti = new IloIntVar[inst.J][inst.T+1][];
        for(int j=0; j<inst.J; j++){
            for(int t=0; t<inst.T+1; t++){
                Zjti[j][t] = cplex.boolVarArray(inst.G(j), "Z"+(j+1)+""+(t+1));
            }
        }
        return Zjti;
    }
    public IloIntVar[][][] create_DyZjti(CplexExtended cplex) throws IloException{
        IloIntVar[][][] DyZjti = new IloIntVar[inst.E][inst.T+1][];
        for(int j=0; j<inst.E; j++){
            for(int t=0; t<inst.T+1; t++){
                DyZjti[j][t] = cplex.boolVarArray(inst.E(j,t), "Zy"+(j+1)+""+(t+1));
            }
        }
        return DyZjti;
    }

    
    public void fix_Zjti(IloIntVar[][][] Zjti, int Rjti[][][]) throws IloException{
        for(int j=0; j<inst.J; j++){
            for(int t=0; t<inst.T+1; t++){
                for(int i=0; i<inst.G(j); i++){
                    Zjti[j][t][i].setMax(Rjti[j][t][i]);
                    Zjti[j][t][i].setMin(Rjti[j][t][i]);
                }
            }
        }
    }
    
    public IloNumVar[][] create_Djt(CplexExtended cplex, final int DN) throws IloException{
        final double Dmin = inst.DELTA/Math.pow(2, DN);
        final double Dmax = inst.DELTA;//Double.POSITIVE_INFINITY
        IloNumVar Djt[][] = cplex.numVarArray(inst.J, inst.T+1, Dmin, Dmax, "D");
        return Djt;
    }
    public IloNumVar[][] create_DyDjt(CplexExtended cplex, final int DN) throws IloException{
        final double Dmin = inst.DELTA/Math.pow(2, DN);
        final double Dmax = inst.DELTA;//Double.POSITIVE_INFINITY
        IloNumVar DyDjt[][] = cplex.numVarArray(inst.E, inst.T+1, Dmin, Dmax, "Dy");
        return DyDjt;
    }
    public IloNumVar[][][] create_InDjti(CplexExtended cplex, final int DN) throws IloException{
        final double Dmin = inst.DELTA/Math.pow(2, DN);
        final double Dmax = inst.DELTA;//Double.POSITIVE_INFINITY
        //inicia a nova variavel
        IloNumVar InDjti[][][] = new IloNumVar[inst.I][inst.T + 1][];
        for (int j = 0; j < inst.I; j++) {
            for (int t = 0; t < inst.T + 1; t++) {
                InDjti[j][t] = new IloNumVar[inst.I(j,t)];
                for (int i = 0; i < inst.I(j,t); i++) {
                    InDjti[j][t][i] = cplex.numVar(Dmin, Dmax, "I"+j+""+t+""+i);
                }
            }
        }
        return InDjti;
    }
    public IloNumVar[][] create_UDti(CplexExtended cplex, final int DN) throws IloException{
        if(inst.U==null){
            return null;
        }
        final double Dmin = inst.DELTA/Math.pow(2, DN);
        final double Dmax = inst.DELTA;//Double.POSITIVE_INFINITY
        //inicia a nova variavel
        IloNumVar UDt[][] = cplex.numVarArray(inst.T, inst.U.length(),Dmin, Dmax, "Du");
        return UDt;
    }
    
    public void ObstacleAvoidanceFixZjti(CplexExtended cplex, IloNumVar[][] Mt, int[][][] bZjti, boolean obst[][], double DELTA) throws IloException {
        for(int j=0; j<inst.J; j++){
            for(int t=0; t<inst.T+1; t++){
                if(obst==null || obst[j][t]){
                    double max = -Integer.MAX_VALUE;
                    boolean flag = false;
                    for(int i=0; i<inst.Ob[j].length(); i++){
                        double exp = inst.Ob[j].lines[i].ax*cplex.getValue(Mt[t][0]) + inst.Ob[j].lines[i].ay*cplex.getValue(Mt[t][2]);
                        exp -= inst.Ob[j].lines[i].b;
                        exp = exp/inst.Rjti[j][t][i];

                        if(exp<=0){
                            bZjti[j][t][i] = 0;
                        }else{
                            double delta = (1-Normal.standardTailProb(exp, false))/2;
                            bZjti[j][t][i] = delta<=DELTA+1e-6 ? 1 : 0;
                            flag = true;
                        }
                        max = Math.max(max, exp);
                    }
                    if(!flag){
                        //throw new IloException("Sum_i Zjti = 0");
                    }
                    //double delta = (1-Normal.standardTailProb(max, false))/2;
                }
            }
        }
    }
    public void ObstacleAvoidanceFreeZjti(CplexExtended cplex, IloNumVar[][] Mt, int[][][] bZjti, boolean obst[][]) throws IloException {
        double vDjt[][] = new double[inst.J][inst.T+1];
        for(int j=0; j<inst.J; j++){
            for(int t=0; t<inst.T+1; t++){
                if(obst==null || obst[j][t]){
                    double max = -Integer.MAX_VALUE;
                    for(int i=0; i<inst.Ob[j].length(); i++){
                        double exp = inst.Ob[j].lines[i].ax*cplex.getValue(Mt[t][0]) + inst.Ob[j].lines[i].ay*cplex.getValue(Mt[t][2]);
                        exp -= inst.Ob[j].lines[i].b;
                        exp = exp/inst.Rjti[j][t][i];
                        max = Math.max(max, exp);
                    }
                    if(max>=0){
                        double delta = (1-Normal.standardTailProb(max, false))/2;
                        vDjt[j][t] = delta;
                    }else{
                        throw new IloException("error not espected: max<0");
                    }
                }
            }
        }
        for(int j=0; j<inst.J; j++){
            for(int t=0; t<inst.T+1; t++){
                if(obst==null || obst[j][t]){
                    double max = -Integer.MAX_VALUE;
                    for(int i=0; i<inst.Ob[j].length(); i++){
                        double exp = inst.Ob[j].lines[i].ax*cplex.getValue(Mt[t][0]) + inst.Ob[j].lines[i].ay*cplex.getValue(Mt[t][2]);
                        exp -= inst.Ob[j].lines[i].b;
                        exp = exp/inst.Rjti[j][t][i];

                        if(exp<=0){
                            bZjti[j][t][i] = 0;
                        }else{
                            double delta = (1-Normal.standardTailProb(exp, false))/2;
                            bZjti[j][t][i] = delta<=vDjt[j][t] + 1e-9 ? 1 : 0;
                        }
                        max = Math.max(max, exp);
                    }
                    //double delta = (1-Normal.standardTailProb(max, false))/2;
                }
            }
        }
    }
    
    public void ObstacleAvoidanceFix(CplexExtended cplex, IloNumVar[][] MUt, IloIntVar[][][] Zjti, final double Djt) throws IloException{
        for(int j=0; j<inst.J; j++){
            for(int t=0; t<inst.T+1; t++){
                for(int i=0; i<inst.G(j); i++){
                    IloNumExpr exp = cplex.prod(inst.Mjti[j][t][i], Zjti[j][t][i]);
                    exp = cplex.sum(exp, cplex.prod(-inst.Ob[j].lines[i].ax, MUt[t][0]));
                    exp = cplex.sum(exp, cplex.prod(-inst.Ob[j].lines[i].ay, MUt[t][2]));
                    
                    double Cit = inst.Rjti[j][t][i]*Normal.inverseStandardCdf(1-2*Djt);
                    //double val = inst.Rjti[j][t][i]*(-inst.AA-inst.BB) - inst.bji[j][i] + inst.M - (inst.Rjti[j][t][i]*-2*inst.AA*Djt);
                    double val = - inst.Ob[j].lines[i].b + inst.Mjti[j][t][i] - Cit;
                    cplex.addLe(exp, val, "S36."+(j+1)+","+(t+1)+","+(i+1)+"");                    
                }
            }
        }
    }
    public void DyObstacleAvoidanceFix(CplexExtended cplex, IloNumVar[][] MUt, IloIntVar[][][] DyZjti, final double Djt) throws IloException{
        for(int j=0; j<inst.E; j++){
            for(int t=0; t<inst.T+1; t++){
                for(int i=0; i<inst.E(j,t); i++){
                    double Rjti = inst.Dy[j][t].R(i, inst.sum_t[t]);
                    IloNumExpr exp = cplex.prod(inst.Ejti[j][t][i], DyZjti[j][t][i]);
                    exp = cplex.sum(exp, cplex.prod(-inst.Dy[j][t].lines[i].ax, MUt[t][0]));
                    exp = cplex.sum(exp, cplex.prod(-inst.Dy[j][t].lines[i].ay, MUt[t][2]));
                    
                    double Cit = Rjti*Normal.inverseStandardCdf(1-2*Djt);
                    //double val = inst.Rjti[j][t][i]*(-inst.AA-inst.BB) - inst.bji[j][i] + inst.M - (inst.Rjti[j][t][i]*-2*inst.AA*Djt);
                    double val = - inst.Dy[j][t].lines[i].b + inst.Ejti[j][t][i] - Cit;
                    cplex.addLe(exp, val, "S36."+(j+1)+","+(t+1)+","+(i+1)+"");                    
                }
            }
        }
    }
    public void addDelta(CplexExtended cplex, IloNumVar[][] Djt, IloNumVar[][] DyDjt, IloNumVar[][][] InDjti, IloNumVar[][] UDti) throws IloException{
        IloNumExpr exp = null;
        for(int j=0; Djt!=null && j<Djt.length; j++){
            for(int t=0; t<Djt[j].length; t++){
                if(exp==null){
                    exp = Djt[j][t];
                }else{
                    exp = cplex.sum(exp, Djt[j][t]);
                }
            }
        }
        for(int j=0; DyDjt!=null && j<DyDjt.length; j++){
            for(int t=0; t<DyDjt[j].length; t++){
                if(exp==null){
                    exp = DyDjt[j][t];
                }else{
                    exp = cplex.sum(exp, DyDjt[j][t]);
                }
            }
        }
        for(int j=0; InDjti!=null && j<InDjti.length; j++){
            for(int t=0; t<InDjti[j].length; t++){
                for(int i=0; i<InDjti[j][t].length; i++){
                    exp = cplex.sum(exp, InDjti[j][t][i]);
                }
            }
        }
        for(int t=0; UDti!=null && t<UDti.length; t++){
            for(int i=0; i<UDti[t].length; i++){
                exp = cplex.sum(exp, UDti[t][i]);
            }
        }
        cplex.addLe(exp, inst.DELTA, "Delta");
    }
//    private void addDelta(ProOFCplex cplex, IloNumVar[][][] InDjti, IloNumVar[][]... Djt) throws IloException{
//        IloNumExpr exp = null;
//        for(int n=0; n<Djt.length; n++){
//            for(int j=0; j<Djt[n].length; j++){
//                for(int t=0; t<Djt[n][j].length; t++){
//                    if(exp==null){
//                        exp = Djt[n][j][t];
//                    }else{
//                        exp = cplex.sum(exp, Djt[n][j][t]);
//                    }
//                }
//            }
//        }
//        for(int j=0; j<InDjti.length; j++){
//            for(int t=0; t<InDjti[j].length; t++){
//                for(int i=0; i<InDjti[j][t].length; i++){
//                    exp = cplex.sum(exp, InDjti[j][t][i]);
//                }
//            }
//        }
//        cplex.addLe(exp, inst.DELTA, "Delta");
//    }
    public IloNumExpr ObstacleAvoidanceFree(CplexExtended cplex, IloNumVar[][] MUt, IloIntVar[][][] Zjti, IloNumVar Djt[][], final int DN) throws IloException{
        //final double DN_cost = 1;//1e-3; // 1e-5;
        //final double M_max = M(DN-1);
        final double B0 = 1-2*inst.DELTA;
        final double F0 = Normal.inverseStandardCdf(B0);

        IloNumVar Bjtni[][][][] = new IloNumVar[inst.J][inst.T+1][DN][];
        for(int j=0; j<inst.J; j++){
            for(int t=0; t<inst.T+1; t++){
                for(int n=0; n<DN; n++){
                    Bjtni[j][t][n] = new IloNumVar[inst.G(j)];
                    for(int i=0; i<inst.G(j); i++){
                        double x0 = 1-2*inst.DELTA/Math.pow(2, n);
                        double x1 = 1-2*inst.DELTA/Math.pow(2, n+1);
                        Bjtni[j][t][n][i] = cplex.numVar(0,  (x1-x0)*(n+1==DN ? 2 : 1) , "B"+(j+1)+""+(t+1)+""+(n+1)+""+(i+1));
                    }
                }
            }
        }
        IloNumExpr obj = null;
        /*for(int j=0; j<inst.J; j++){
            for(int t=0; t<inst.T+1; t++){
                for(int n=0; n<DN; n++){
                    for(int i=0; i<inst.bji[j].length; i++){
                        double cost = DN_cost*M(n)/M_max;
                        //System.out.printf("cost = %g\n", cost);
                        if(obj==null){
                            obj = cplex.prod(cost, Bjtni[j][t][n][i]);
                        }else{
                            obj = cplex.sum(obj, cplex.prod(cost, Bjtni[j][t][n][i]));
                        }
                    }
                }
            }
        }*/
        for(int j=0; j<inst.J; j++){
            for(int t=0; t<inst.T+1; t++){
                for(int i=0; i<inst.G(j); i++){
                    //1-2*Djt <= Bjt0i + sum_n{Bjtni} para todo i
                    IloNumExpr exp = null;
                    for(int n=0; n<DN; n++){
                        exp = exp==null ? Bjtni[j][t][n][i] : cplex.sum(exp, Bjtni[j][t][n][i]);
                    }
                    cplex.addGe(cplex.sum(exp, cplex.prod(2,Djt[j][t])), 1 - B0);
                }
            }
        }
        //-------------------------------- (36) --------------------------------
        for(int j=0; j<inst.J; j++){
            for(int t=0; t<inst.T+1; t++){
                for(int i=0; i<inst.G(j); i++){
                    
                    IloNumExpr Cit = null;
                    double Cit0 = inst.Rjti[j][t][i]*F0;
                    for(int n=0; n<DN; n++){
                        Cit = (Cit==null) ? cplex.prod(M(n), Bjtni[j][t][n][i]) :  cplex.sum(Cit, cplex.prod(M(n), Bjtni[j][t][n][i]));
                    }
                    IloNumExpr exp = cplex.prod(inst.Rjti[j][t][i], Cit);
                    exp = cplex.sum(exp, cplex.prod(-inst.Ob[j].lines[i].ax, MUt[t][0]));
                    exp = cplex.sum(exp, cplex.prod(-inst.Ob[j].lines[i].ay, MUt[t][2]));
                    
                    
                    exp = cplex.sum(exp, cplex.prod(inst.Mjti[j][t][i], Zjti[j][t][i]));
                    
                    double val = inst.Mjti[j][t][i] - inst.Ob[j].lines[i].b - Cit0;
                    cplex.addLe(exp, val, "S36."+(j+1)+","+(t+1)+","+(i+1)+"");
                    
                }
            }
        }
        
        
        return obj;
    }
    public IloNumExpr DyObstacleAvoidanceFree(IloNumExpr obj, CplexExtended cplex, IloNumVar[][] MUt, IloIntVar[][][] DyZjti, IloNumVar DyDjt[][], final int DN) throws IloException{
        //final double DN_cost = 1;//1e-3; // 1e-5;
        //final double M_max = M(DN-1);
        final double B0 = 1-2*inst.DELTA;
        final double F0 = Normal.inverseStandardCdf(B0);

        IloNumVar Bjtni[][][][] = new IloNumVar[inst.E][inst.T+1][DN][];
        for(int j=0; j<inst.E; j++){
            for(int t=0; t<inst.T+1; t++){
                for(int n=0; n<DN; n++){
                    Bjtni[j][t][n] = new IloNumVar[inst.E(j,t)];
                    for(int i=0; i<inst.E(j,t); i++){
                        double x0 = 1-2*inst.DELTA/Math.pow(2, n);
                        double x1 = 1-2*inst.DELTA/Math.pow(2, n+1);
                        Bjtni[j][t][n][i] = cplex.numVar(0,  (x1-x0)*(n+1==DN ? 2 : 1) , "B"+(j+1)+""+(t+1)+""+(n+1)+""+(i+1));
                    }
                }
            }
        }
        for(int j=0; j<inst.E; j++){
            for(int t=0; t<inst.T+1; t++){
                for(int i=0; i<inst.E(j,t); i++){
                    //1-2*Djt >= Bjt0i + sum_n{Bjtni} para todo i
                    IloNumExpr exp = null;
                    for(int n=0; n<DN; n++){
                        exp = exp==null ? Bjtni[j][t][n][i] : cplex.sum(exp, Bjtni[j][t][n][i]);
                    }
                    cplex.addGe(cplex.sum(exp, cplex.prod(2,DyDjt[j][t])), 1 - B0);
                }
            }
        }
        //-------------------------------- (36) --------------------------------
        for(int j=0; j<inst.E; j++){
            for(int t=0; t<inst.T+1; t++){
                for(int i=0; i<inst.E(j,t); i++){
                    double Rjti = inst.Dy[j][t].R(i, inst.sum_t[t]);
                    
                    IloNumExpr Cit = null;
                    double Cit0 = Rjti*F0;
                    for(int n=0; n<DN; n++){
                        Cit = (Cit==null) ? cplex.prod(M(n), Bjtni[j][t][n][i]) :  cplex.sum(Cit, cplex.prod(M(n), Bjtni[j][t][n][i]));
                    }
                    IloNumExpr exp = cplex.prod(Rjti, Cit);
                    exp = cplex.sum(exp, cplex.prod(-inst.Dy[j][t].lines[i].ax, MUt[t][0]));
                    exp = cplex.sum(exp, cplex.prod(-inst.Dy[j][t].lines[i].ay, MUt[t][2]));
                    
                    
                    exp = cplex.sum(exp, cplex.prod(inst.Ejti[j][t][i], DyZjti[j][t][i]));
                    
                    double val = inst.Ejti[j][t][i] - inst.Dy[j][t].lines[i].b - Cit0;
                    cplex.addLe(exp, val, "S36."+(j+1)+","+(t+1)+","+(i+1)+"");
                    
                }
            }
        }
        return obj;
    }
    public IloNumExpr InObstacleAvoidanceFree(IloNumExpr obj, CplexExtended cplex, IloNumVar[][] MUt, IloNumVar InDjti[][][], final int DN) throws IloException{
        //final double DN_cost = 1;//1e-3; // 1e-5;
        //final double M_max = M(DN-1);
        final double B0 = 1-2*inst.DELTA;
        final double F0 = Normal.inverseStandardCdf(B0);

        IloNumVar Bjtni[][][][] = new IloNumVar[inst.I][inst.T+1][DN][];
        for(int j=0; j<inst.I; j++){
            for(int t=0; t<inst.T+1; t++){
                for(int n=0; n<DN; n++){
                    Bjtni[j][t][n] = new IloNumVar[inst.I(j,t)];
                    for(int i=0; i<inst.I(j,t); i++){
                        double x0 = 1-2*inst.DELTA/Math.pow(2, n);
                        double x1 = 1-2*inst.DELTA/Math.pow(2, n+1);
                        Bjtni[j][t][n][i] = cplex.numVar(0,  (x1-x0)*(n+1==DN ? 2 : 1) , "B"+(j+1)+""+(t+1)+""+(n+1)+""+(i+1));
                    }
                }
            }
        }
        for(int j=0; j<inst.I; j++){
            for(int t=0; t<inst.T+1; t++){
                for(int i=0; i<inst.I(j,t); i++){
                    //1-2*Djt >= Bjt0i + sum_n{Bjtni} para todo i
                    IloNumExpr exp = null;
                    for(int n=0; n<DN; n++){
                        exp = exp==null ? Bjtni[j][t][n][i] : cplex.sum(exp, Bjtni[j][t][n][i]);
                    }
                    cplex.addGe(cplex.sum(exp, cplex.prod(2,InDjti[j][t][i])), 1 - B0);
                }
            }
        }
        //-------------------------------- (36) --------------------------------
        for(int j=0; j<inst.I; j++){
            for(int t=0; t<inst.T+1; t++){
                for(int i=0; i<inst.I(j,t); i++){
                    double Rjti = inst.In[j][t].R(i, inst.sum_t[t]);
                    
                    IloNumExpr Cit = null;
                    double Cit0 = Rjti*F0;
                    for(int n=0; n<DN; n++){
                        Cit = (Cit==null) ? cplex.prod(M(n), Bjtni[j][t][n][i]) :  cplex.sum(Cit, cplex.prod(M(n), Bjtni[j][t][n][i]));
                    }
                    IloNumExpr exp = cplex.prod(Rjti, Cit);
                    exp = cplex.sum(exp, cplex.prod(+inst.In[j][t].lines[i].ax, MUt[t][0]));
                    exp = cplex.sum(exp, cplex.prod(+inst.In[j][t].lines[i].ay, MUt[t][2]));
                    
                    double val = +inst.In[j][t].lines[i].b - Cit0;
                    cplex.addLe(exp, val, "In."+(j+1)+","+(t+1)+","+(i+1)+"");
                    
                    
//                    IloNumExpr exp = cplex.prod(+inst.In[j][t].lines[i].ax, MUt[t][0]);
//                    exp = cplex.sum(exp, cplex.prod(+inst.In[j][t].lines[i].ay, MUt[t][2]));
//                    
//                    double val = +inst.In[j][t].lines[i].b;
//                    cplex.addLe(exp, val, "In."+(j+1)+","+(t+1)+","+(i+1)+"");
                }
            }
        }
        return obj;
    }
    public IloNumExpr UControlAvoidanceFree(IloNumExpr obj, CplexExtended cplex, IloNumVar[][] Ut, IloNumVar UDt[][], final int DN) throws IloException{
        if(inst.U==null){
            return null;
        }
        //final double DN_cost = 1;//1e-3; // 1e-5;
        //final double M_max = M(DN-1);
        final double B0 = 1-2*inst.DELTA;
        final double F0 = Normal.inverseStandardCdf(B0);

        IloNumVar Btni[][][] = new IloNumVar[inst.T][DN][];
        for(int t=0; t<inst.T; t++){
            for(int n=0; n<DN; n++){
                Btni[t][n] = new IloNumVar[inst.U.length()];
                for(int i=0; i<inst.U.length(); i++){
                    double x0 = 1-2*inst.DELTA/Math.pow(2, n);
                    double x1 = 1-2*inst.DELTA/Math.pow(2, n+1);
                    Btni[t][n][i] = cplex.numVar(0,  (x1-x0)*(n+1==DN ? 2 : 1) , "B"+(t+1)+""+(n+1)+""+(i+1));
                }
            }
        }
        for(int t=0; t<inst.T; t++){
            for(int i=0; i<inst.U.length(); i++){
                //1-2*Djt >= Bjt0i + sum_n{Bjtni} para todo i
                IloNumExpr exp = null;
                for(int n=0; n<DN; n++){
                    exp = exp==null ? Btni[t][n][i] : cplex.sum(exp, Btni[t][n][i]);
                }
                cplex.addGe(cplex.sum(exp, cplex.prod(2,UDt[t][i])), 1 - B0);
            }
        }
        
        //-------------------------------- (36) --------------------------------
        for(int t=0; t<inst.T; t++){
            for(int i=0; i<inst.U.length(); i++){
                double Rjti = inst.U.U(i, inst.sum_Ut[t]);

                IloNumExpr Cit = null;
                double Cit0 = Rjti*F0;
                for(int n=0; n<DN; n++){
                    Cit = (Cit==null) ? cplex.prod(M(n), Btni[t][n][i]) :  cplex.sum(Cit, cplex.prod(M(n), Btni[t][n][i]));
                }
                IloNumExpr exp = cplex.prod(Rjti, Cit);
                exp = cplex.sum(exp, cplex.prod(+inst.U.lines[i].ax, Ut[t][0]));
                exp = cplex.sum(exp, cplex.prod(+inst.U.lines[i].ay, Ut[t][1]));

                double val = +inst.U.lines[i].b - Cit0;
                cplex.addLe(exp, val, "U."+(t+1)+","+(i+1)+"");

            }
        }
        
        return obj;
    }
    private double M(int n){
        double x0 = 1-2*inst.DELTA/Math.pow(2, n);
        double x1 = 1-2*inst.DELTA/Math.pow(2, n+1);//x1>x0

        //x = 1 - 2*delta/2^n
        //n = DN
        //

        double y0 = Normal.inverseStandardCdf(x0);
        double y1 = Normal.inverseStandardCdf(x1);

        double m = (y1-y0)/(x1-x0);
        return m;
    }
    
}
