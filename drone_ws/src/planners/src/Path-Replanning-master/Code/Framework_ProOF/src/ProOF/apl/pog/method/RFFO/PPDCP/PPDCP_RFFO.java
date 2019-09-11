/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.method.RFFO.PPDCP;

import ProOF.apl.pog.method.CustomizedApproach.AddObjective;
import ProOF.apl.pog.method.CustomizedApproach.Objectives.Norm1;
import ProOF.apl.pog.method.CustomizedApproach.Objectives.Norm2SQR_Cpx;
import ProOF.apl.pog.method.CustomizedApproach.Objectives.Norm2SQR_aprox32;
import ProOF.apl.pog.method.CustomizedApproach.Objectives.Norm2_aprox32;
import ProOF.apl.advanced2.FMS.RFFO.RelaxVar;
import ProOF.apl.advanced2.FMS.RFFO.RFFOModel;
import ProOF.apl.pog.problem.PPDCP.PPDCPInstance;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerResults;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.cplex.IloCplex;
import java.util.ArrayList;
import jsc.distributions.Normal;

/**
 *
 * @author marcio
 */
public class PPDCP_RFFO extends RFFOModel{
    private static final IloNumVarType type = IloNumVarType.Bool;
    
    private PPDCPInstance inst = new PPDCPInstance();
    
    /** Vx, Vy*/
    protected IloNumVar Ut[][];  
    /** px, vx, py, vy*/
    protected IloNumVar MUt[][];
    
    private IloNumVar Zjti[][][] ;
    private IloNumVar alfa[][][][];
    
    private IloNumVar Djt[][];
    private IloNumVar Bjtni[][][][];
    
    protected IloNumVar ObjBeta;
    
    
    public PPDCP_RFFO() throws IloException {
        
    }
    @Override
    public String name() {
        return "PPDCP";
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        super.services(link); //To change body of generated methods, choose Tools | Templates.
        inst = link.add(inst);
    }
    
    @Override
    public ArrayList<RelaxVar> relax_variables() throws Exception {
        ArrayList<RelaxVar> list = new ArrayList<RelaxVar>();
        for(int t=0; t<inst.T+1; t++){
            for(int j=0; j<inst.J; j++){
                for(int i=0; i<inst.G(j); i++){
                    list.add(new RelaxVar(type, Zjti[j][t][i]));
                }
            }
        }
        return list;
    }

    @Override
    public ArrayList<RelaxVar> fix_variables() throws Exception {
        ArrayList<RelaxVar> list = new ArrayList<RelaxVar>();
        for(int t=0; t<inst.T+1; t++){
            for(int j=0; j<inst.J; j++){
                for(int i=0; i<inst.G(j); i++){
                    list.add(new RelaxVar(type, Zjti[j][t][i]));
                }
            }
        }
        return list;
    }

    @Override
    protected double getLB(int i) {
        return 0.0;
    }
    @Override
    protected double getUB(int i) {
        return 1.0;
    }

    @Override
    protected double round(double value) {
        return value > 0.5 ? 1.0 : 0.0;
    }
    
    @Override
    public int size() {
        int size = 0;
        for(int j=0; j<inst.J; j++){
            size += inst.G(j);
        }
        return size * (inst.T+1);
    }
    @Override
    public void extra_conversion() throws IloException {
        
    }
    @Override
    public void print() throws Exception{
        cpx.output().println("Solution status  = " + cpx.getStatus());
        cpx.output().println("Solution value   = " + cpx.getObjValue());
    }

    protected AddObjective selectOBJ() throws Exception {
        System.out.println("inst.OBJ = "+inst.OBJ);
        switch (inst.OBJ) {
            case 0:
                return new Norm2SQR_Cpx(inst);
            case 1:
                return new Norm1(inst);
            case 2:
                return new Norm2_aprox32(inst);
            case 3:
                return new Norm2SQR_aprox32(inst);
        }
        return null;
    }

    
    private long initial;
    @Override
    public void model() throws Exception {
        initial = System.nanoTime();
        
        final AddObjective addObj = selectOBJ();
        
        Ut  = cpx.numVarArray(inst.T, 2, -inst.UMAX, +inst.UMAX, "U");
        MUt = cpx.numVarArray(inst.T+1, 4, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, "MU");
        ObjBeta = cpx.numVar(0, Double.POSITIVE_INFINITY, "Beta");
        
        
        Zjti = new IloNumVar[inst.J][inst.T+1][];
        for(int j=0; j<inst.J; j++){
            for(int t=0; t<inst.T+1; t++){
                Zjti[j][t] = cpx.numVarArray(inst.G(j), 0.0, 1.0, "Ztji"+cpx.Index(t,inst.T)+""+cpx.Index(j,inst.J));
                //Zjti[j][t] = cpx.boolVarArray(inst.G(j), "Ztji"+cpx.Index(t,inst.T)+""+cpx.Index(j,inst.J));
            }
        }
        alfa = new IloNumVar[inst.J][inst.T + 1][][];
        for (int j = 0; j < inst.J; j++) {
            for (int t = 0; t < inst.T + 1; t++) {
                alfa[j][t] = new IloNumVar[inst.G(j)][inst.G(j)];
                for (int i = 0; i < inst.G(j); i++) {
                    for (int l = 0; l < inst.G(j); l++) {
                        alfa[j][t][i][l] = cpx.numVar(0, 1, "alfa("+(j+1+","+(t+1)+","+(i+1)+","+(l+1))+")");
                    }
                }

            }
        }
        final double Dmin = inst.DELTA/Math.pow(2, inst.DN);
        final double Dmax = inst.DELTA;//Double.POSITIVE_INFINITY
        Djt = cpx.numVarArray(inst.J, inst.T+1, Dmin, Dmax, "D");
        
        Bjtni = new IloNumVar[inst.J][inst.T+1][inst.DN][];
        for(int j=0; j<inst.J; j++){
            for(int t=0; t<inst.T+1; t++){
                for(int n=0; n<inst.DN; n++){
                    Bjtni[j][t][n] = new IloNumVar[inst.G(j)];
                    for(int i=0; i<inst.G(j); i++){
                        double x0 = 1-2*inst.DELTA/Math.pow(2, n);
                        double x1 = 1-2*inst.DELTA/Math.pow(2, n+1);
                        Bjtni[j][t][n][i] = cpx.numVar(0,  (x1-x0)*(n+1==inst.DN ? 2 : 1) , "B"+(j+1)+""+(t+1)+""+(n+1)+""+(i+1));
                    }
                }
            }
        }
        
        for(int t=0; t<inst.T+1; t++){
            MUt[t][1].setLB(-inst.VMAX);
            MUt[t][1].setUB(+inst.VMAX);
            MUt[t][3].setLB(-inst.VMAX);
            MUt[t][3].setUB(+inst.VMAX);
            
//            MUt[t][0].setLB(inst.minX);
//            MUt[t][0].setUB(inst.maxX);
//            MUt[t][2].setLB(inst.minY);
//            MUt[t][2].setUB(inst.maxY);
        }
        
        //-------------------------------- (Objective) --------------------------------
        IloNumExpr obj = addObj.objective(cpx, Ut, MUt);
        
        obj = cpx.sum(obj, cpx.prod(ObjBeta, inst.P_Goal));
        
        cpx.addMinimize(obj);
        
        //-------------------------------- (Dynamic equation - Dy ) --------------------------------
        for(int t=0; t<inst.T+1; t++){
            IloNumExpr exp[] = new IloNumExpr[4];
            for(int i=0; i<t; i++){
                double R[][] = inst.prod(inst.pow(inst.A,t-1-i), inst.B);
                for(int j=0; j<2; j++){
                    for(int k=0; k<4; k++){
                        if(exp[k]==null){
                            exp[k] = cpx.prod(R[k][j], Ut[i][j]);
                        }else{
                            exp[k] = cpx.sum(exp[k], cpx.prod(R[k][j], Ut[i][j]));
                        }
                    }
                }
            }
            double AX0[][] = inst.prod(inst.pow(inst.A,t), inst.X0);
            for(int k=0; k<4; k++){
                if(exp[k]==null){
                    //exp[k] = AX0[k][0];
                }else{
                    exp[k] = cpx.sum(exp[k], AX0[k][0]);
                }
            }
            for(int k=0; k<4; k++){
                if(exp[k]==null){
                    cpx.addEq(MUt[t][k], AX0[k][0], "Dy."+(t+1)+","+(k+1)+"");
                }else{
                    cpx.addEq(MUt[t][k], exp[k], "Dy."+(t+1)+","+(k+1)+"");
                }
            }
        }
        
        //-------------------------------- (Goal position) --------------------------------
        for(int k=0; k<4; k++){
            if(k%2==0){//Position only
                cpx.addGe(cpx.sum(ObjBeta, cpx.prod(MUt[inst.T][k],+1)), +inst.Xgoal[k], "S34a."+(k+1)+"");     
                cpx.addGe(cpx.sum(ObjBeta, cpx.prod(MUt[inst.T][k],-1)), -inst.Xgoal[k], "S34b."+(k+1)+"");
            }
            //Position and velocity
        }
        
        
        
        
        //-----------------------------Restrições novas------------------------
       //Primeira restriçao
       for (int j = 0; j < inst.J; j++) {
           for (int i = 0; i < inst.G(j); i++) {
               for (int l = 0; l < inst.G(j); l++) {
                   if (l != i) {
                       IloNumExpr exp0 = cpx.sum(cpx.prod(alfa[j][0][i][l], -1), Zjti[j][0][i], Zjti[j][0][l]);
                       cpx.addLe(exp0, 1, "NEWexp1." + (j + 1) + "," + (i + 1) + "," + (l + 1) + "," + 1);

                       for (int t = 1; t < inst.T + 1; t++) {
                           IloNumExpr exp1 = cpx.sum(cpx.prod(alfa[j][t][i][l], -1), Zjti[j][t][i], cpx.prod(Zjti[j][t - 1][i], -1), Zjti[j][t][l], cpx.prod(Zjti[j][t - 1][l], -1));
                           cpx.addLe(exp1, 1, "NEWexp1." + (j + 1) + "," + (i + 1) + "," + (l + 1) + "," + (t + 1));

                           IloNumExpr exp2 = cpx.sum(cpx.prod(alfa[j][t][i][l], -1), Zjti[j][t][i], cpx.prod(Zjti[j][t - 1][i], -1), cpx.prod(Zjti[j][t][l], -1), Zjti[j][t - 1][l]);
                           cpx.addLe(exp2, 1, "NEWexp2." + (j + 1) + "," + (i + 1) + "," + (l + 1) + "," + (t + 1));

                           IloNumExpr exp3 = cpx.sum(cpx.prod(alfa[j][t][i][l], -1), cpx.prod(Zjti[j][t][i], -1), Zjti[j][t - 1][i], Zjti[j][t][l], cpx.prod(Zjti[j][t - 1][l], -1));
                           cpx.addLe(exp3, 1, "NEWexp3." + (j + 1) + "," + (i + 1) + "," + (l + 1) + "," + (t + 1));

                           IloNumExpr exp4 = cpx.sum(cpx.prod(alfa[j][t][i][l], -1), cpx.prod(Zjti[j][t][i], -1), Zjti[j][t - 1][i], cpx.prod(Zjti[j][t][l], -1), Zjti[j][t - 1][l]);
                           cpx.addLe(exp4, 1, "NEWexp4." + (j + 1) + "," + (i + 1) + "," + (l + 1) + "," + (t + 1));

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
                            cpx.addEq(alfa[j][t][i][l], alfa[j][t][l][i], "Eq." + (j + 1) + "," + (i + 1) + "," + (l + 1) + "," + (t + 1));
                        }
                    }
               }
           }
       }
       
       System.out.println("inst.ALPHA = "+inst.ALPHA);
        //Segunda restrição
        for (int j = 0; j < inst.J; j++) {
            for (int t = 0; t < inst.T + 1; t++) {
                IloNumExpr exp = null;
                for (int i = 0; i < inst.G(j); i++) {
                    exp = cpx.SumProd(exp, 1, Zjti[j][t][i]);
                    IloNumExpr exp2 = null;
                    for (int l = 0; l < inst.G(j); l++) {
                        exp2 = cpx.SumProd(exp2, inst.ALPHA, alfa[j][t][i][l]);
                    }
                    exp = cpx.sum(exp, cpx.prod(exp2, -1));
                }
                cpx.addGe(exp, 1, "NEW2." + (j + 1) + "," + (t + 1));
            }
        }
        
        final double B0 = 1-2*inst.DELTA;
        final double F0 = Normal.inverseStandardCdf(B0);

        
        /*for(int j=0; j<inst.J; j++){
            for(int t=0; t<inst.T+1; t++){
                for(int n=0; n<DN; n++){
                    for(int i=0; i<inst.bji[j].length; i++){
                        double cost = DN_cost*M(n)/M_max;
                        //System.out.printf("cost = %g\n", cost);
                        if(obj==null){
                            obj = cpx.prod(cost, Bjtni[j][t][n][i]);
                        }else{
                            obj = cpx.sum(obj, cpx.prod(cost, Bjtni[j][t][n][i]));
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
                    for(int n=0; n<inst.DN; n++){
                        exp = cpx.SumProd(exp, 1.0, Bjtni[j][t][n][i]);
                    }
                    cpx.addGe(cpx.sum(exp, cpx.prod(2,Djt[j][t])), 1 - B0);
                }
            }
        }
        //-------------------------------- (36) --------------------------------
        for(int j=0; j<inst.J; j++){
            for(int t=0; t<inst.T+1; t++){
                for(int i=0; i<inst.G(j); i++){
                    
                    IloNumExpr Cit = null;
                    double Cit0 = inst.Rjti[j][t][i]*F0;
                    for(int n=0; n<inst.DN; n++){
                        Cit = cpx.SumProd(Cit, M(n), Bjtni[j][t][n][i]);
                    }
                    IloNumExpr exp = cpx.prod(inst.Rjti[j][t][i], Cit);
                    exp = cpx.sum(exp, cpx.prod(-inst.Ob[j].lines[i].ax, MUt[t][0]));
                    exp = cpx.sum(exp, cpx.prod(-inst.Ob[j].lines[i].ay, MUt[t][2]));
                    
                    
                    exp = cpx.sum(exp, cpx.prod(inst.Mjti[j][t][i], Zjti[j][t][i]));
                    
                    double val = inst.Mjti[j][t][i] - inst.Ob[j].lines[i].b - Cit0;
                    cpx.addLe(exp, val, "S36."+(j+1)+","+(t+1)+","+(i+1)+"");
                    
                }
            }
        }
        
        IloNumExpr exp = null;
        for(int j=0; Djt!=null && j<Djt.length; j++){
            for(int t=0; t<Djt[j].length; t++){
                exp = cpx.SumProd(exp, 1.0, Djt[j][t]);
            }
        }
        cpx.addLe(exp, inst.DELTA, "Delta");
        
        
        cpx.use(new IloCplex.IncumbentCallback() {
            double best = Integer.MAX_VALUE;
            @Override
            protected void main() throws IloException {
                double time_best = (System.nanoTime() - initial)/1e9;
                
                if(PPDCPInstance.PLOT){
                    double incb = getIncumbentObjValue();
                    //if(incb<best){
                        //cpx.output().println("Solution incb     = " + incb);
                        //cpx.output().println("Solution best     = " + getBestObjValue());

                        best = incb;
                        double vMt[][] = new double[inst.T+1][4];
                        for(int t=0; t<inst.T+1; t++){
                            for(int j=0; j<4; j++){
                                vMt[t][j] = getValue(MUt[t][j]);
                            }
                        }
                        
                        
                        double minU = inst.UMAX;
                        double maxU = 0;
                        double avgU = 0;
                        for(int t=0; t<inst.T; t++){
                            double ux = getValue(Ut[t][0]);
                            double uy = getValue(Ut[t][1]);
                            double u = Math.sqrt(ux*ux+uy*uy);
                            maxU = Math.max(maxU, u);
                            minU = Math.min(minU, u);
                            avgU += u;
                        }
                        avgU = avgU/inst.T;
//                        System.out.println("---------------------------------------------------------------------");
//                        System.out.printf("maxU: %12g\n", maxU);
//                        System.out.printf("avgU: %12g\n", avgU);
//                        System.out.printf("minU: %12g\n", minU);
                        
                        double vDt[] = new double[inst.T+1];
                        

                        //System.out.println("----------------[Djt + DyDjt + InDjti + UDti | Dt]---------------");
                        for(int t=0; t<inst.T+1; t++){
                            double max = 0;
                            for (int j = 0; j < inst.J; j++) {
                                double djt = getValue(Djt[j][t]);
                                max = Math.max(max, djt);
                            }
                            //System.out.printf(" | %12g\n", max);
                            vDt[t] = max;
                        }
                        
                        
                        double total_dist = 0;
                        for(int t=0; t<inst.T; t++){
                             total_dist += inst.dist2_position(vMt[t], vMt[t+1]);
                        }
                         
                        double gap = (getObjValue() - getBestObjValue())*100 / (getObjValue() + 1e-10);
                        //inst.plot(String.format("%s - %6.2f %%", name, gap), vMt, vDt);
                        inst.plot(String.format("%s - %6.2f %%", status, gap), vMt, vDt, getObjValue(), total_dist, gap, time_best);
                    }
                //}
            }
        });
        if(PPDCPInstance.PLOT){
            cpx.use(new IloCplex.MIPInfoCallback() {
                @Override
                protected void main() throws IloException {
                    double gap = (getIncumbentObjValue() - getBestObjValue())*100 / (getIncumbentObjValue() + 1e-10);
                    inst.title(String.format("%s - %6.2f %%", status, gap));
                }
            });
        }
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
    
    
    @Override
    public void results(LinkerResults link) throws Exception {
        super.results(link); //To change body of generated methods, choose Tools | Templates.
        
        
        link.writeDbl("RFFO"+"-Obj Dist", cpx.getValue(ObjBeta));
        
        double vMt[][] = cpx.getValues(MUt);
        double vUt[][] = cpx.getValues(Ut);
        
//        double vMUt[][] = new double[inst.T+1][4];
//        for(int t=0; t<inst.T+1; t++){
//            for(int j=0; j<4; j++){
//                vMUt[t][j] = cpx.getValue(MUt[t][j]);
//            }
//        }
       
        double vDt[] = new double[inst.T+1];
        for(int t=0; t<inst.T+1; t++){
            double max = 0;
            for (int j = 0; j < inst.J; j++) {
                double djt = cpx.getValue(Djt[j][t]);
                max = Math.max(max, djt);
            }
            //System.out.printf(" | %12g\n", max);
            vDt[t] = max;
        }
        
        double total_dist = 0;
        for(int t=0; t<inst.T; t++){
             total_dist += inst.dist2_position(vMt[t], vMt[t+1]);
        }
        link.writeDbl("RFFO"+"-total_dist", total_dist);
        double total_control = 0;
        for(int t=0; t<inst.T; t++){
             total_control += inst.norm2(vUt[t])*inst.DT;
        }
        link.writeDbl("RFFO"+"-total_control", total_control);

        double gap = (cpx.getObjValue() - cpx.getBestObjValue())*100 / (cpx.getObjValue() + 1e-10);
        inst.plot(String.format("%s - %6.2f %%", "model-full", gap), vMt, vUt, vDt, cpx.getObjValue(), total_dist, gap, (System.nanoTime() - initial)/1e9);
        inst.save("RFFO", -1, false);
    }
    
}
