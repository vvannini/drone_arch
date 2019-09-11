/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.method.CustomizedApproach;

import ProOF.apl.pog.problem.PPDCP.PPDCPInstance;
import ProOF.com.Linker.LinkerResults;
import ProOF.CplexExtended.CplexExtended;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.UnknownObjectException;
import java.io.File;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Locale;
import jsc.distributions.Normal;

/**
 *
 * @author marcio
 * @param <R>
 */
public class Model <R extends AddRestrictions>{
    protected final CplexExtended cplex;
    
    /** Vx, Vy*/
    protected final IloNumVar Ut[][];  
    /** px, vx, py, vy*/
    protected final IloNumVar MUt[][];
    
    protected final IloNumVar ObjBeta;
    
    protected final PPDCPInstance inst;
    
    protected final String base_name;
    protected String name;
    
    protected final R addRest;
   
    
    private double time_best;
    private double time_total;
    private final long initial;
    
    private int iteration = 0;
    public Model(String name, PPDCPInstance inst, AddObjective addObj,  int ID, R addRest, Model toFix) throws Exception {
        this.base_name = name;
        this.inst = inst;
        this.addRest = addRest;
        this.cplex = new CplexExtended();
        initial = System.nanoTime();
        
        Ut  = cplex.numVarArray(inst.T, 2, -inst.UMAX, +inst.UMAX, "U");
        MUt = cplex.numVarArray(inst.T+1, 4, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, "MU");
        ObjBeta = cplex.numVar(0, Double.POSITIVE_INFINITY, "Beta");
        
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
        
        //-------------------------------- (Dynamic equation - Dy ) --------------------------------
        for(int t=0; t<inst.T+1; t++){
            IloNumExpr exp[] = new IloNumExpr[4];
            for(int i=0; i<t; i++){
                double R[][] = inst.prod(inst.pow(inst.A,t-1-i), inst.B);
                for(int j=0; j<2; j++){
                    for(int k=0; k<4; k++){
                        if(exp[k]==null){
                            exp[k] = cplex.prod(R[k][j], Ut[i][j]);
                        }else{
                            exp[k] = cplex.sum(exp[k], cplex.prod(R[k][j], Ut[i][j]));
                        }
                    }
                }
            }
            double AX0[][] = inst.prod(inst.pow(inst.A,t), inst.X0);
            for(int k=0; k<4; k++){
                if(exp[k]==null){
                    //exp[k] = AX0[k][0];
                }else{
                    exp[k] = cplex.sum(exp[k], AX0[k][0]);
                }
            }
            for(int k=0; k<4; k++){
                if(exp[k]==null){
                    cplex.addEq(MUt[t][k], AX0[k][0], "Dy."+(t+1)+","+(k+1)+"");
                }else{
                    cplex.addEq(MUt[t][k], exp[k], "Dy."+(t+1)+","+(k+1)+"");
                }
            }
        }
        
        //-------------------------------- (Goal position) --------------------------------
        for(int k=0; k<4; k++){
            if(k%2==0){//Position only
                cplex.addGe(cplex.sum(ObjBeta, cplex.prod(MUt[inst.T][k],+1)), +inst.Xgoal[k], "S34a."+(k+1)+"");     
                cplex.addGe(cplex.sum(ObjBeta, cplex.prod(MUt[inst.T][k],-1)), -inst.Xgoal[k], "S34b."+(k+1)+"");
            }
            //Position and velocity
        }
        
        
        //-------------------------------- (Objective) --------------------------------
        IloNumExpr obj = addObj.objective(cplex, Ut, MUt);
        obj = cplex.sum(obj, cplex.prod(ObjBeta, inst.P_Goal));
        if(addRest!=null){
            IloNumExpr extra_obj = addRest.addRestrictions(cplex, Ut, MUt, ID, toFix!=null);
            if(toFix!=null){//???
                addRest.fix(toFix.cplex, toFix.addRest);
            }
            if(extra_obj!=null){
                obj = cplex.sum(obj, extra_obj);
            }
        }
        cplex.addMinimize(obj);
        
    }
    
    public void execute(double execTime, double epGap, int threads) throws IloException{
        iteration++;
        name = base_name+"_"+iteration;
        
        cplex.setWarning(null);
        
        cplex.use(new IloCplex.IncumbentCallback() {
            double best = Integer.MAX_VALUE;
            @Override
            protected void main() throws IloException {
                time_best = (System.nanoTime() - initial)/1e9;
                
                if(PPDCPInstance.PLOT){
                    double incb = getIncumbentObjValue();
                    if(incb<best){
                        //cplex.output().println("Solution incb     = " + incb);
                        //cplex.output().println("Solution best     = " + getBestObjValue());

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
                            double max = addRest.AlocationFix();
                            LinkedList<IloNumVar> list = addRest.AlocationFree(t);
                            if(list!=null){
                                for(IloNumVar var : list){
                                    if(var!=null){
                                        double djt = getValue(var);
                                        max = Math.max(max, djt);
                                        //System.out.format("%12g ", djt);
                                    }else{
                                        //System.out.printf("+ ");
                                    }
                                }
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
                        inst.plot(String.format("%s - %6.2f %%", name, gap), vMt, vDt, getObjValue(), total_dist, gap, time_best);
                    }
                }
            }
        });
        if(PPDCPInstance.PLOT){
            cplex.use(new IloCplex.MIPInfoCallback() {
                @Override
                protected void main() throws IloException {
                    double gap = (getIncumbentObjValue() - getBestObjValue())*100 / (getIncumbentObjValue() + 1e-10);
                    inst.title(String.format("%s - %6.2f %%", name, gap));
                }
            });
        }
        
        //Escrita do modelo em arquivo
        //cplex.exportModel(name+".lp");

        //execTime = 120;
        //epGap = 0.5;
        //Parada por tempo maximo de 10 segundos
        cplex.setParam(IloCplex.DoubleParam.TiLim, execTime);

        //Parada por gap relativo
        cplex.setParam(IloCplex.DoubleParam.EpGap, epGap);
        
        //Modo de paralelismo, numero de threads
        cplex.setParam(IloCplex.IntParam.Threads, threads);
        //Parada por gap absoluto de 100
        //cplex.setParam(IloCplex.DoubleParam.EpAGap, 100);
        
        //Parada por gap relativo
        //cplex.setParam(IloCplex.DoubleParam.EpGap, 0.000001);
        
        //cplex.setParam(IloCplex.DoubleParam.EpLin, 0);
        
        
        //cplex.setParam(IloCplex.IntParam.MIPDisplay, 2);
        System.out.println("BIN: "+cplex.getNbinVars());
        System.out.println("ROWS: "+cplex.getNrows());
        System.out.println("COL: "+cplex.getNcols());
        
        System.out.println("==================================[ "+name+" ]========================================");
        if (cplex.solve()) {
            cplex.output().println("Solution status     = " + cplex.getStatus());
            cplex.output().println("Solution value      = " + cplex.getObjValue());
        }else{
            cplex.output().println("Solution status     = " + cplex.getStatus());
        }
        time_total = (System.nanoTime() - initial)/1e9;
    }

    public boolean isFeasible() throws IloException {
        if(cplex.getStatus()==IloCplex.Status.Feasible || cplex.getStatus()==IloCplex.Status.Optimal){
            return cplex.getBestObjValue() < 1e3;
        }else{
            return false;
        }
    }

    public double upper() throws IloException {
        return cplex.getObjValue();
    }
    public double lower() throws IloException {
        return cplex.getBestObjValue();
    }
//    public void plot() throws Exception{
//        //------------------------(11)---------------------------
//        double vMt[][] = cplex.getValues(MUt);
//        inst.plot(name, vMt);
//        inst.save(name);
//    }
    
    public void results(LinkerResults link) throws Exception {       
        link.writeInt(base_name+"-Iteration", iteration);
        link.writeDbl(base_name+"-Best Time", time_best);
        link.writeDbl(base_name+"-Total Time", time_total);
        link.writeString(base_name+"-Status", cplex.getStatus().toString());
        link.writeDbl(base_name+"-BIN", cplex.getNbinVars());
        link.writeDbl(base_name+"-ROWS", cplex.getNrows());
        link.writeDbl(base_name+"-COL", cplex.getNcols());
        link.writeDbl(base_name+"-Obj Value", cplex.getObjValue());
        link.writeDbl(base_name+"-Obj Lower", cplex.getBestObjValue());
        link.writeDbl(base_name+"-Obj Dist", cplex.getValue(ObjBeta));
        //------------------------(11)---------------------------
        double vMt[][] = cplex.getValues(MUt);
        double vUt[][] = cplex.getValues(Ut); 
        //------------------------[beta]---------------------------
        double dist = inst.dist2_position(vMt[inst.T], inst.Xgoal);
        
        //------------------------[beta]---------------------------
        double delta = 0;
        for(int j=0; j<inst.J; j++){
            for(int t=0; t<inst.T; t++){
                delta += Delta(inst, j, t, vMt);
            }
        }
        delta = Math.max(0, delta-inst.DELTA);
        
        //------------------------[goal]---------------------------
        double goal = 0;
        for(int t=0; t<inst.T; t++){
            goal += vUt[t][0]*vUt[t][0]+vUt[t][1]*vUt[t][1];
        }
        
        double fitness = dist*inst.P_Goal + delta*inst.P_Delta  + goal;
       
        link.writeDbl(base_name+"-GA fitness", fitness);
        link.writeDbl(base_name+"-GA dist", dist*inst.P_Goal);
        link.writeDbl(base_name+"-GA extra delta", delta*inst.P_Delta);
        
        double total_dist = 0;
        for(int t=0; t<inst.T; t++){
             total_dist += inst.dist2_position(vMt[t], vMt[t+1]);
        }
        link.writeDbl(base_name+"-total_dist", total_dist);
        double total_control = 0;
        for(int t=0; t<inst.T; t++){
             total_control += inst.norm2(vUt[t])*inst.DT;
        }
        link.writeDbl(base_name+"-total_control", total_control);
        
        double M[][] = inst.trans(vMt);
        link.writeArray(base_name, "fMt[0]", M[0]);
        link.writeArray(base_name, "fMt[1]", M[1]);
        link.writeArray(base_name, "fMt[2]", M[2]);
        link.writeArray(base_name, "fMt[3]", M[3]);
        double U[][] = inst.trans(vUt);
        link.writeArray(base_name, "fUt[0]", U[0]);
        link.writeArray(base_name, "fUt[1]", U[1]);
        
        if(PPDCPInstance.OUTPUT){
            PrintStream output = new PrintStream(new File("ouput-"+base_name+".txt"));
            output.println("--------------------------------------------------------");
            output.printf(Locale.ENGLISH, "Time        = %g\n", time_best);
            output.printf(Locale.ENGLISH, "Status      = %s\n", cplex.getStatus().toString());
            output.printf(Locale.ENGLISH, "Obj Value   = %g\n", cplex.getObjValue());
            output.printf(Locale.ENGLISH, "Lower Bound = %g\n", cplex.getBestObjValue());
            output.println("--------------------------------------------------------");

            output.printf(Locale.ENGLISH, "%3s ", "T");
            for(int t=0; t<inst.T; t++){
                output.printf(Locale.ENGLISH, "%20d ", t);
            }
            output.println();

            for(int j=0; j<2; j++){
                output.printf(Locale.ENGLISH, "Ut%d ", j);
                for(int t=0; t<inst.T; t++){
                    output.printf(Locale.ENGLISH, "%20g ", vUt[t][j]);
                }
                output.println();
            }
            output.printf(Locale.ENGLISH, "%3s ", "T");
            for(int t=0; t<inst.T+1; t++){
                output.printf(Locale.ENGLISH, "%20d ", t);
            }
            output.println();
            for(int j=0; j<4; j++){
                output.printf(Locale.ENGLISH, "Xt%d ", j);
                for(int t=0; t<inst.T+1; t++){
                    output.printf(Locale.ENGLISH, "%20g ", vMt[t][j]);
                }
                output.println();
            }
            output.close();
        }
            
        
        
        if(isFeasible()){
            //cplex.output().println("Solution incb     = " + incb);
            //cplex.output().println("Solution best     = " + getBestObjValue());
            save(-1);
        }
    }
    
    public double[] RiskAlocation() throws IloException{
        double vDt[] = new double[inst.T+1];

        System.out.println("----------------[Djt + DyDjt + InDjti + UDti | Dt]---------------");
        for(int t=0; t<inst.T+1; t++){
            double max = addRest.AlocationFix();
            LinkedList<IloNumVar> list = addRest.AlocationFree(t);
            if(list!=null){
                for(IloNumVar var : list){
                    if(var!=null){
                        double djt = cplex.getValue(var);
                        max = Math.max(max, djt);
                        System.out.format("%12g ", djt);
                    }else{
                        System.out.printf("+ ");
                    }
                }
            }
            System.out.printf(" | %12g\n", max);
            vDt[t] = max;
        }
        return vDt;
    }
    
    public void save(int t_plot) throws IloException{
        double vMt[][] = cplex.getValues(MUt);
        double vUt[][] = cplex.getValues(Ut);
        
//        double vMUt[][] = new double[inst.T+1][4];
//        for(int t=0; t<inst.T+1; t++){
//            for(int j=0; j<4; j++){
//                vMUt[t][j] = cplex.getValue(MUt[t][j]);
//            }
//        }
       
        double vDt[] = RiskAlocation();

        double total_dist = 0;
        for(int t=0; t<inst.T; t++){
             total_dist += inst.dist2_position(vMt[t], vMt[t+1]);
        }

        double gap = (cplex.getObjValue() - cplex.getBestObjValue())*100 / (cplex.getObjValue() + 1e-10);
        inst.plot(String.format("%s - %6.2f %%", name, gap), vMt, vUt, vDt, cplex.getObjValue(), total_dist, gap, time_total);
        inst.save(base_name, t_plot, t_plot>=0);
    }
    
    
    public static double Delta(PPDCPInstance inst, int j, int t, double Mt[][]){
        double max = -Integer.MAX_VALUE;
        for(int i=0; i<inst.Ob[j].length(); i++){
            double exp = inst.Ob[j].lines[i].ax*Mt[t][0] + inst.Ob[j].lines[i].ay*Mt[t][2];
            exp -= inst.Ob[j].lines[i].b;
            exp = exp/inst.Rjti[j][t][i];
            max = Math.max(max, exp);
        }
        double delta = (1-Normal.standardTailProb(max, false))/2;
        return delta;
    }
    
    public double[][] rote() throws UnknownObjectException, IloException {
        return cplex.getValues(MUt);
    }
}
