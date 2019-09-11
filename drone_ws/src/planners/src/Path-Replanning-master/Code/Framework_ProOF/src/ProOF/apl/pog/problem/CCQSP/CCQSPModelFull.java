/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ProOF.apl.pog.problem.CCQSP;

import ProOF.apl.pog.problem.PPDCP.PPDCPInstance;
import ProOF.com.Linker.LinkerResults;
import ProOF.CplexExtended.CplexExtended;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import jsc.distributions.Normal;

/**
 *
 * @author marcio
 */
public final class CCQSPModelFull {
    protected final CplexExtended cplex;
    
    /** Vx, Vy*/
    protected final IloNumVar Ut[][];  
    /** px, vx, py, vy*/
    protected final IloNumVar Xt[][];
    
    protected final IloIntVar[][][] Wkij;
    
    protected final IloNumVar Tk[];
    protected final IloNumVar Ijt[][];
    
    
    private IloNumVar[][] Djt = null;
    private IloNumVar[][] DyDjt = null;
    private IloNumVar[][][] InDjti = null;
    private IloNumVar[][] UDti = null;
    
    private IloIntVar[][][] Zjti = null;
    private IloIntVar[][][] DyZjti = null;
    
    private IloNumVar alfa[][][][] = null;
    
    protected final CCQSPInstance inst;
    protected final String name;
    
    private double time_best;
    private double time_total;
    private final long initial;
    
    public static void main(String args[]) throws IOException, IloException{
        CCQSPInstance inst = new CCQSPInstance();
        //inst.file_dinamic = new File("./CCQSP/dynamic.sgl");
        //inst.file_map = new File("./CCQSP/map.sgl");
        
        inst.file_dinamic = new File("./CCQSP/simple/dynamic.sgl");
        inst.file_map = new File("./CCQSP/simple/map.sgl");
        
        inst.load();
        
        CplexExtended cplex = new CplexExtended();
        CCQSPModelFull model = new CCQSPModelFull(cplex, inst, "CCQSP");
        model.execute(10000, 0, 1);
        model.plot();
    }
    
    public CCQSPModelFull(CplexExtended cplex, CCQSPInstance inst, String name) throws IloException {
        this.cplex = cplex;
        this.inst = inst;
        this.name = name;
        this.initial = System.nanoTime();
        Ut  = cplex.numVarArray(inst.T(), 2, -inst.map.UMAX, +inst.map.UMAX, "U");
        Xt = cplex.numVarArray(inst.T()+1, 4, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, "MU");
        
        
        
        //Bounds of velocity and possition
        for(int t=0; t<inst.T()+1; t++){
            Xt[t][1].setLB(-inst.map.VMAX);
            Xt[t][1].setUB(+inst.map.VMAX);
            Xt[t][3].setLB(-inst.map.VMAX);
            Xt[t][3].setUB(+inst.map.VMAX);
            
//            Xt[t][0].setLB(inst.map.minX);
//            Xt[t][0].setUB(inst.map.maxX);
//            Xt[t][2].setLB(inst.map.minY);
//            Xt[t][2].setUB(inst.map.maxY);
        }
        
        //Start position
        for(int j=0; j<4; j++){
            Xt[0][j].setLB(inst.map.X0[j]);
            Xt[0][j].setUB(inst.map.X0[j]);
        }
        Xt[inst.T()][0].setLB(inst.map.X0[0]+24);
        Xt[inst.T()][0].setUB(inst.map.X0[0]+24);
        Xt[inst.T()][2].setLB(inst.map.X0[2]);
        Xt[inst.T()][2].setUB(inst.map.X0[2]);
        
        //Dynamic equation
        for(int t=1; t<inst.T()+1; t++){
            for(int j=0; j<4; j++){
                IloNumExpr exp = null;
                for(int k=0; k<4; k++){
                    exp = cplex.SumProd(exp, inst.dy.A[j][k], Xt[t-1][k]);
                }
                for(int k=0; k<2; k++){
                    exp = cplex.SumProd(exp, inst.dy.B[j][k], Ut[t-1][k]);
                }
                cplex.addEq(Xt[t][j], exp, "Dy");
            }
        }
        
        
        Wkij = new IloIntVar[inst.I()][][];
        for(int k=0; k<inst.I(); k++){
            Wkij[k] = new IloIntVar[inst.qsp.O(k-1)][inst.qsp.O(k)];
            for(int i=0; i<inst.qsp.O(k-1); i++){
                for(int j=0; j<inst.qsp.O(k); j++){
                    Wkij[k][i][j] = cplex.boolVar("W"+k+""+i+""+j);
                }
            }
        }
        
//        for(int k=0; k<inst.qsp.K; k++){
//            if(k==0){
//                IloNumExpr sum = null;
//                for(int i=0; i<inst.qsp.O(k-1); i++){
//                    for(int j=0; j<inst.qsp.O(k); j++){
//                        sum = cplex.SumProd(sum, 1.0, Wkij[k][i][j]);
//                    }
//                }
//                cplex.addEq(sum, 1, "Sum(Zkij).eq.1");
//            }else{
//                IloNumExpr sum1 = null;
//                for(int i=0; i<inst.qsp.O(k-2); i++){
//                    for(int j=0; j<inst.qsp.O(k-1); j++){
//                        sum1 = cplex.SumProd(sum1, 1.0, Wkij[k][i][j]);
//                    }
//                }
//                IloNumExpr sum2 = null;
//                for(int i=0; i<inst.qsp.O(k-1); i++){
//                    for(int j=0; j<inst.qsp.O(k); j++){
//                        sum2 = cplex.SumProd(sum2, 1.0, Wkij[k][i][j]);
//                    }
//                }
//                cplex.addEq(sum1, sum2, "Sum(Zkij).eq.V");
//            }
//        }
        
        for(int k=0; k<inst.I(); k++){
            if(k==0){
                IloNumExpr sum = null;
                for(int i=0; i<inst.qsp.O(k-1); i++){
                    for(int j=0; j<inst.qsp.O(k); j++){
                        sum = cplex.SumProd(sum, 1.0, Wkij[k][i][j]);
                    }
                }
                System.out.printf("k = %d | %s = 1\n", k, sum);
                cplex.addEq(sum, 1, "Sum(Zkij).eq.1");
            }else{
                for(int v=0; v<inst.qsp.O(k-1); v++){
                    IloNumExpr sum1 = null;
                    for(int i=0; i<inst.qsp.O(k-2); i++){
                        sum1 = cplex.SumProd(sum1, 1.0, Wkij[k-1][i][v]);
                    }
                    IloNumExpr sum2 = null;
                    for(int i=0; i<inst.qsp.O(k); i++){
                        sum2 = cplex.SumProd(sum2, 1.0, Wkij[k][v][i]);
                    }
                    cplex.addEq(sum1, sum2, "Sum(Zkij).eq.V");
                    System.out.printf("k = %d | %s = %s\n", k, sum1, sum2);
                }
                
            }
        }
        
//        for(int k=0; k<inst.qsp.K; k++){
//            if(k==0){
//                final int i=0;
//                IloNumExpr sum = null;
//                for(int j=0; j<inst.qsp.O(k); j++){ //j:=[0 .. N]
//                    sum = cplex.SumProd(sum, 1.0, Wkij[k][i][j]);
//                }
//                cplex.addEq(sum, 1, "Sum(Zkij).eq.1");
//            }else{
//                for(int i=0; i<inst.qsp.O(k-1); i++){//i:=[0 .. N]
//                    IloNumExpr sum1 = null;
//                    for(int j=0; j<inst.qsp.O(k-2); j++){//j:=[]
//                        sum1 = cplex.SumProd(sum1, 1.0, Wkij[k][j][i]);
//                    }
//                    
//                    IloNumExpr sum2 = null;
//                    for(int j=0; j<inst.qsp.O(k); j++){
//                        sum2 = cplex.SumProd(sum2, 1.0, Wkij[k][i][j]);
//                    }
//                    cplex.addEq(sum1, sum2, "Sum(Zkij).eq.V");
//                }
//            }
//        }
        
        
        //Tk = cplex.intVarArray(inst.qsp.K, 0, 10, "T");
        Tk = cplex.numVarArray(inst.I(), 0, inst.map.T*inst.map.DT, "T");
        
        for(int k=0; k<inst.I(); k++){
            IloNumExpr sum = null;
            for(int l=0; l<=k; l++){
                for(int i=0; i<inst.qsp.O(l-1); i++){
                    for(int j=0; j<inst.qsp.O(l); j++){
                        sum = cplex.SumProd(sum, inst.qsp.Ckij(l,i,j), Wkij[l][i][j]);
                    }
                }
            }
            cplex.addEq(Tk[k], sum, "Tk("+k+")");
            //cplex.addEq(Tk[k], (k+1)*5*inst.map.DT, "Tk");
        }
        
        Ijt = cplex.numVarArray(inst.I(), inst.T()+1, 0, 1, "I");
        for(int j=0; j<inst.I(); j++){
            for(int t=0; t<inst.T()+1; t++){
                IloNumExpr exp = cplex.sum(Tk[j],   -t*inst.map.DT);
                cplex.addIF_Eq_Them_Y("I", Ijt[j][t], exp);
                //cplex.addIF_Le_Them_Y("I", Ijt[j][t], exp, cplex.prod(-1, exp));
            }
        }
        
        //NAO FAZ QUASE NADA, MAS FORTALECE OS LIMITANTES DO MODELO
        //(OBS, lembrar que o QSP pode exigir que uma região seja visitada duas vezes em tempos distintos, neste caso lembrar de mudar o numero 1 para 2 na equacao) 
        for(int j=0; j<inst.I(); j++){
            IloNumExpr sum = null;
            for(int t=0; t<inst.T()+1; t++){
                sum = cplex.SumProd(sum, 1, Ijt[j][t]);
            }
            cplex.addEq(sum, 1, "sumIjt.eq.1");
        }
        
        
        //Ijt[0][10].setLB(1);
        //Ijt[4][24].setLB(1);
        
        Zjti = create_Zjti(cplex);
        DyZjti = create_DyZjti(cplex);
            
        alfa = create_alfa(cplex);
        addModuleAlfa(cplex, Zjti, alfa);
        addSumZjti_2(cplex, Zjti, alfa);

        IloNumVar DyAlfa[][][][] = create_DyAlfa(cplex);
        addDyModuleAlfa(cplex, DyZjti, DyAlfa);
        addDySumZjti_2(cplex, DyZjti, DyAlfa);

        //InDjti = create_InDjti(cplex, inst.map.DN);
        Djt = create_Djt(cplex, inst.map.DN);
        ObstacleAvoidanceFree(cplex, Xt, Zjti, Djt, inst.map.DN);
        DyDjt = create_DyDjt(cplex, inst.map.DN);
        DyObstacleAvoidanceFree(cplex, Xt, DyZjti, DyDjt, inst.map.DN);
        InDjti = create_InDjti(cplex, inst.map.DN);
        InObstacleAvoidanceFree(cplex, Xt, InDjti, Ijt, inst.map.DN);
        if(inst.map.U!=null){
            UDti = create_UDti(cplex, inst.map.DN);
            UControlAvoidanceFree(cplex, Ut, UDti, inst.map.DN);
        }
        //addDelta(cplex, Djt, DyDjt, InDjti, UDti);
        addDelta(cplex, Djt, DyDjt, InDjti, UDti);
        
        
//        //Single temporal constraint
//        IloNumExpr exp = cplex.sum(Tk[inst.qsp.K-1], 0);
//        cplex.addGe(exp, 0, "Tk_LB");
//        cplex.addLe(exp, (inst.map.T-2) * inst.map.DT, "Tk_UB");
        
        
        
        IloNumExpr obj = cplex.prod(10, Tk[inst.I()-1]);
        //IloNumExpr obj = null;
        for(int t=0; t<inst.T(); t++){
            obj = cplex.SumNumScalProd(obj, "sqr", 32, inst.map.UMAX, Ut[t]);
        }
        cplex.addMinimize(obj);
    }
    
    protected IloIntVar[][][] create_Zjti(CplexExtended cplex) throws IloException{
        IloIntVar[][][] Zjti = new IloIntVar[inst.map.J][inst.map.T+1][];
        for(int j=0; j<inst.map.J; j++){
            for(int t=0; t<inst.map.T+1; t++){
                Zjti[j][t] = cplex.boolVarArray(inst.G(j), "Z"+(j+1)+""+(t+1));
            }
        }
        return Zjti;
    }
    private IloIntVar[][][] create_DyZjti(CplexExtended cplex) throws IloException{
        IloIntVar[][][] DyZjti = new IloIntVar[inst.map.E][inst.map.T+1][];
        for(int j=0; j<inst.map.E; j++){
            for(int t=0; t<inst.map.T+1; t++){
                DyZjti[j][t] = cplex.boolVarArray(inst.E(j,t), "Zy"+(j+1)+""+(t+1));
            }
        }
        return DyZjti;
    }
    protected IloNumVar[][] create_Djt(CplexExtended cplex, final int DN) throws IloException{
        final double Dmin = inst.map.DELTA/Math.pow(2, DN);
        final double Dmax = inst.map.DELTA;//Double.POSITIVE_INFINITY
        IloNumVar Djt[][] = cplex.numVarArray(inst.map.J, inst.map.T+1, Dmin, Dmax, "D");
        return Djt;
    }
    protected IloNumVar[][] create_DyDjt(CplexExtended cplex, final int DN) throws IloException{
        final double Dmin = inst.map.DELTA/Math.pow(2, DN);
        final double Dmax = inst.map.DELTA;//Double.POSITIVE_INFINITY
        IloNumVar DyDjt[][] = cplex.numVarArray(inst.map.E, inst.map.T+1, Dmin, Dmax, "Dy");
        return DyDjt;
    }
    protected IloNumVar[][][] create_InDjti(CplexExtended cplex, final int DN) throws IloException{
        final double Dmin = inst.map.DELTA/Math.pow(2, DN);
        final double Dmax = inst.map.DELTA;//Double.POSITIVE_INFINITY
        //inicia a nova variavel
        IloNumVar InDjti[][][] = new IloNumVar[inst.map.I][inst.map.T + 1][];
        for (int j = 0; j < inst.map.I; j++) {
            for (int t = 0; t < inst.map.T + 1; t++) {
                InDjti[j][t] = new IloNumVar[inst.I(j)];
                for (int i = 0; i < inst.I(j); i++) {
                    InDjti[j][t][i] = cplex.numVar(Dmin, Dmax, "I"+j+""+t+""+i);
                }
            }
        }
        return InDjti;
    }
    protected IloNumVar[][] create_UDti(CplexExtended cplex, final int DN) throws IloException{
        if(inst.map.U==null){
            return null;
        }
        final double Dmin = inst.map.DELTA/Math.pow(2, DN);
        final double Dmax = inst.map.DELTA;//Double.POSITIVE_INFINITY
        //inicia a nova variavel
        IloNumVar UDt[][] = cplex.numVarArray(inst.map.T, inst.map.U.length(),Dmin, Dmax, "Du");
        return UDt;
    }
    protected void addDelta(CplexExtended cplex, IloNumVar[][] Djt, IloNumVar[][] DyDjt, IloNumVar[][][] InDjti, IloNumVar[][] UDti) throws IloException{
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
                    exp = cplex.SumProd(exp, 1, InDjti[j][t][i]);
                }
            }
        }
        for(int t=0; UDti!=null && t<UDti.length; t++){
            for(int i=0; i<UDti[t].length; i++){
                exp = cplex.SumProd(exp, 1, UDti[t][i]);
            }
        }
        if(exp!=null){
            cplex.addLe(exp, inst.map.DELTA, "Delta");
        }
    }
    
    public void execute(double execTime, double epGap, int threads) throws IloException{
        
        cplex.setWarning(null);
        
        cplex.use(new IloCplex.IncumbentCallback() {
            double best = Integer.MAX_VALUE;
            @Override
            protected void main() throws IloException {
                time_best = (System.nanoTime() - initial)/1e9;
                
                if(CCQSPPlot.PLOT){
                    double incb = getIncumbentObjValue();
                    if(incb<best){
                        //cplex.output().println("Solution incb     = " + incb);
                        //cplex.output().println("Solution best     = " + getBestObjValue());

                        best = incb;
                        double vMt[][] = new double[inst.T()+1][4];
                        for(int t=0; t<inst.T()+1; t++){
                            for(int j=0; j<4; j++){
                                vMt[t][j] = getValue(Xt[t][j]);
                            }
                        }
                        
                        
                        double minU = inst.map.UMAX;
                        double maxU = 0;
                        double avgU = 0;
                        for(int t=0; t<inst.T(); t++){
                            double ux = getValue(Ut[t][0]);
                            double uy = getValue(Ut[t][1]);
                            double u = Math.sqrt(ux*ux+uy*uy);
                            maxU = Math.max(maxU, u);
                            minU = Math.min(minU, u);
                            avgU += u;
                        }
                        avgU = avgU/inst.T();

                        double total_dist = 0;
                        for(int t=0; t<inst.T(); t++){
                             total_dist += PPDCPInstance.dist2_position(vMt[t], vMt[t+1]);
                        }
                         
                        double vDt[] = new double[inst.map.T+1];
                        

                        //System.out.println("----------------[Djt + DyDjt + InDjti + UDti | Dt]---------------");
                        for(int t=0; t<inst.map.T+1; t++){
                            double max = inst.map.DELTA/Math.pow(2, inst.map.DN);
                            LinkedList<IloNumVar> list = AlocationFree(t);
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
                        
                        double gap = (getObjValue() - getBestObjValue())*100 / (getObjValue() + 1e-10);
                        inst.plot.plot(String.format("%s - %6.2f %%", name, gap), vMt, vDt, getObjValue(), total_dist, gap, time_best);
                    }
                }
            }
        });
        if(CCQSPPlot.PLOT){
            cplex.use(new IloCplex.MIPInfoCallback() {
                @Override
                protected void main() throws IloException {
                    double gap = (getIncumbentObjValue() - getBestObjValue())*100 / (getIncumbentObjValue() + 1e-10);
                    inst.plot.title(String.format("%s - %6.2f %%", name, gap));
                }
            });
        }
        
        //Escrita do modelo em arquivo
        cplex.exportModel(name+".lp");

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

     private LinkedList<IloNumVar> AlocationFree(int t){
        LinkedList<IloNumVar> list = new LinkedList<IloNumVar>();
        for(int j=0; j<inst.map.J; j++){
            list.addLast(Djt[j][t]);
        }
        list.add(null);
        for(int j=0; j<inst.map.E; j++){
            list.addLast(DyDjt[j][t]);
        }
        list.add(null);
        for(int j=0; j<inst.map.I; j++){
            for(int i=0; i<inst.I(j); i++){
                list.addLast(InDjti[j][t][i]);
            }
        }
        if(UDti!=null){
            list.add(null);
            if(t<inst.map.T){
                for(int i=0; i<inst.map.U.length(); i++){
                    list.addLast(UDti[t][i]);
                }
            }
        }
        return list;
    }
    
    public boolean isFeasible() throws IloException {
        return cplex.getStatus()==IloCplex.Status.Feasible || cplex.getStatus()==IloCplex.Status.Optimal;
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
        link.writeDbl(name+"-Best Time", time_best);
        link.writeDbl(name+"-Total Time", time_total);
        link.writeString(name+"-Status", cplex.getStatus().toString());
        link.writeDbl(name+"-BIN", cplex.getNbinVars());
        link.writeDbl(name+"-ROWS", cplex.getNrows());
        link.writeDbl(name+"-COL", cplex.getNcols());
        link.writeDbl(name+"-Obj Value", cplex.getObjValue());
        link.writeDbl(name+"-Obj Lower", cplex.getBestObjValue());
        //------------------------(11)---------------------------
        double vMt[][] = cplex.getValues(Xt);
        double vUt[][] = cplex.getValues(Ut); 

        
        
        double total_dist = 0;
        for(int t=0; t<inst.T(); t++){
             total_dist += PPDCPInstance.dist2_position(vMt[t], vMt[t+1]);
        }
        link.writeDbl(name+"-total_dist", total_dist);
        double total_control = 0;
        for(int t=0; t<inst.T(); t++){
             total_control += PPDCPInstance.norm2(vUt[t])*inst.map.DT;
        }
        link.writeDbl(name+"-total_control", total_control);
        
        
        
        if(isFeasible()){
            //cplex.output().println("Solution incb     = " + incb);
            //cplex.output().println("Solution best     = " + getBestObjValue());

            plot();
        }
    }
    
    public void plot() throws IloException{
        double vMt[][] = cplex.getValues(Xt);
        double gap = (cplex.getObjValue() - cplex.getBestObjValue())*100 / (cplex.getObjValue() + 1e-10);
        
        double total_dist = 0;
        for(int t=0; t<inst.T(); t++){
             total_dist += PPDCPInstance.dist2_position(vMt[t], vMt[t+1]);
        }
        
        double vDt[] = new double[inst.map.T+1];
                        

        //System.out.println("----------------[Djt + DyDjt + InDjti + UDti | Dt]---------------");
        for(int t=0; t<inst.map.T+1; t++){
            double max = inst.map.DELTA/Math.pow(2, inst.map.DN);
            LinkedList<IloNumVar> list = AlocationFree(t);
            if(list!=null){
                for(IloNumVar var : list){
                    if(var!=null){
                        double djt = cplex.getValue(var);
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
        
        
        if(CCQSPPlot.PLOT){
            inst.plot.title(String.format("%s - %6.2f %%", name, gap));
        }
        
        
        inst.plot.plot(String.format("%s - %6.2f %%", name, gap), vMt, vDt, cplex.getObjValue(), total_dist, gap, time_total);
        inst.plot.save(name);
        
        System.out.printf("Gap       = %g\n",gap);
        System.out.printf("Obj Value = %g\n", cplex.getObjValue());
        System.out.printf("Obj Lower = %g\n", cplex.getBestObjValue());
        
        double vTk[] = cplex.getValues(Tk);
        double vIjt[][] = cplex.getValues(Ijt);
        System.out.println("-----------------Tk-------------------");
        System.out.print("[ ");
        for(int k=0; k<vTk.length; k++){
            System.out.printf("%8g ", vTk[k]);
        }
        System.out.println("]");
        System.out.println("-----------------Ijt-------------------");
        for(int j=0; j<vIjt.length; j++){
            System.out.print("[ ");
            for(int t=0; t<vIjt[j].length; t++){
                System.out.printf("%8g ", vIjt[j][t]);
            }
            System.out.println("]");
        }
        
        System.out.println("-----------------Wkij-------------------");
        double vWkij[][][] = cplex.getValues(Wkij);
        for(int k=0; k<inst.I(); k++){
            System.out.printf("------------[k=%d]----------\n", k);
            for(int i=0; i<inst.qsp.O(k-1); i++){
                double cost = 0;
                for(int j=0; j<inst.qsp.O(k); j++){
                    cost += vWkij[k][i][j] * inst.qsp.Ckij(k, i, j);
                }
                System.out.printf("i=%d | Cost = %8g [ ", i, cost);
                for(int j=0; j<inst.qsp.O(k); j++){
                    System.out.printf("%8g ", vWkij[k][i][j]);
                }
                System.out.println("]");
            }
        }
        
        
        double vZjti[][][] = cplex.getValues(Zjti);
        double vAjtil[][][][] = cplex.getValues(alfa);
        
        for(int j=0; j<inst.map.J; j++){
            System.out.printf("------------[j=%d]----------\n", j+1);
            for(int t=0; t<inst.map.T+1; t++){
                System.out.printf("\t------------[t=%d]----------\n", t);
                for(int i=0; i<inst.G(j); i++){
                    System.out.printf("\t%8g | [ ", vZjti[j][t][i]);
                    for(int l=0; l<inst.G(j); l++){
                        System.out.printf("%8g ", vAjtil[j][t][i][l]);
                    }
                    System.out.println("]");
                }
            }
        }
        //?? Executar e Ver que Ijt = 1 em t=1
    }
    
    
    
    protected void ObstacleAvoidanceFree(CplexExtended cplex, IloNumVar[][] MUt, IloIntVar[][][] Zjti, IloNumVar Djt[][], final int DN) throws IloException{
        //final double DN_cost = 1;//1e-3; // 1e-5;
        //final double M_max = M(DN-1);
        final double B0 = 1-2*inst.map.DELTA;
        final double F0 = Normal.inverseStandardCdf(B0);

        IloNumVar Bjtni[][][][] = new IloNumVar[inst.map.J][inst.map.T+1][DN][];
        for(int j=0; j<inst.map.J; j++){
            for(int t=0; t<inst.map.T+1; t++){
                for(int n=0; n<DN; n++){
                    Bjtni[j][t][n] = new IloNumVar[inst.G(j)];
                    for(int i=0; i<inst.G(j); i++){
                        double x0 = 1-2*inst.map.DELTA/Math.pow(2, n);
                        double x1 = 1-2*inst.map.DELTA/Math.pow(2, n+1);
                        Bjtni[j][t][n][i] = cplex.numVar(0,  (x1-x0)*(n+1==DN ? 2 : 1) , "B"+(j+1)+""+(t+1)+""+(n+1)+""+(i+1));
                    }
                }
            }
        }
        for(int j=0; j<inst.map.J; j++){
            for(int t=0; t<inst.map.T+1; t++){
                for(int i=0; i<inst.G(j); i++){
                    //1-2*Djt >= Bjt0i + sum_n{Bjtni} para todo i
                    IloNumExpr exp = null;
                    for(int n=0; n<DN; n++){
                        exp = exp==null ? Bjtni[j][t][n][i] : cplex.sum(exp, Bjtni[j][t][n][i]);
                    }
                    cplex.addGe(cplex.sum(exp, cplex.prod(2,Djt[j][t])), 1 - B0);
                }
            }
        }
        //-------------------------------- (36) --------------------------------
        for(int j=0; j<inst.map.J; j++){
            for(int t=0; t<inst.map.T+1; t++){
                for(int i=0; i<inst.G(j); i++){
                    
                    IloNumExpr Cit = null;
                    double Cit0 = inst.map.Rjti[j][t][i]*F0;
                    for(int n=0; n<DN; n++){
                        Cit = (Cit==null) ? cplex.prod(M(n), Bjtni[j][t][n][i]) :  cplex.sum(Cit, cplex.prod(M(n), Bjtni[j][t][n][i]));
                    }
                    IloNumExpr exp = cplex.prod(inst.map.Rjti[j][t][i], Cit);
                    exp = cplex.sum(exp, cplex.prod(-inst.map.Ob[j].lines[i].ax, MUt[t][0]));
                    exp = cplex.sum(exp, cplex.prod(-inst.map.Ob[j].lines[i].ay, MUt[t][2]));
                    
                    
                    exp = cplex.sum(exp, cplex.prod(inst.map.Mjti[j][t][i], Zjti[j][t][i]));
                    
                    double val = inst.map.Mjti[j][t][i] - inst.map.Ob[j].lines[i].b - Cit0;
                    cplex.addLe(exp, val, "S36."+(j+1)+","+(t+1)+","+(i+1)+"");
                    
                }
            }
        }
    }
    private void DyObstacleAvoidanceFree(CplexExtended cplex, IloNumVar[][] MUt, IloIntVar[][][] DyZjti, IloNumVar DyDjt[][], final int DN) throws IloException{
        //final double DN_cost = 1;//1e-3; // 1e-5;
        //final double M_max = M(DN-1);
        final double B0 = 1-2*inst.map.DELTA;
        final double F0 = Normal.inverseStandardCdf(B0);

        IloNumVar Bjtni[][][][] = new IloNumVar[inst.map.E][inst.map.T+1][DN][];
        for(int j=0; j<inst.map.E; j++){
            for(int t=0; t<inst.map.T+1; t++){
                for(int n=0; n<DN; n++){
                    Bjtni[j][t][n] = new IloNumVar[inst.E(j,t)];
                    for(int i=0; i<inst.E(j,t); i++){
                        double x0 = 1-2*inst.map.DELTA/Math.pow(2, n);
                        double x1 = 1-2*inst.map.DELTA/Math.pow(2, n+1);
                        Bjtni[j][t][n][i] = cplex.numVar(0,  (x1-x0)*(n+1==DN ? 2 : 1) , "B"+(j+1)+""+(t+1)+""+(n+1)+""+(i+1));
                    }
                }
            }
        }
        for(int j=0; j<inst.map.E; j++){
            for(int t=0; t<inst.map.T+1; t++){
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
        for(int j=0; j<inst.map.E; j++){
            for(int t=0; t<inst.map.T+1; t++){
                for(int i=0; i<inst.E(j,t); i++){
                    double Rjti = inst.map.Dy[j][t].R(i, inst.map.sum_t[t]);
                    
                    IloNumExpr Cit = null;
                    double Cit0 = Rjti*F0;
                    for(int n=0; n<DN; n++){
                        Cit = (Cit==null) ? cplex.prod(M(n), Bjtni[j][t][n][i]) :  cplex.sum(Cit, cplex.prod(M(n), Bjtni[j][t][n][i]));
                    }
                    IloNumExpr exp = cplex.prod(Rjti, Cit);
                    exp = cplex.sum(exp, cplex.prod(-inst.map.Dy[j][t].lines[i].ax, MUt[t][0]));
                    exp = cplex.sum(exp, cplex.prod(-inst.map.Dy[j][t].lines[i].ay, MUt[t][2]));
                    
                    
                    exp = cplex.sum(exp, cplex.prod(inst.map.Ejti[j][t][i], DyZjti[j][t][i]));
                    
                    double val = inst.map.Ejti[j][t][i] - inst.map.Dy[j][t].lines[i].b - Cit0;
                    cplex.addLe(exp, val, "S36."+(j+1)+","+(t+1)+","+(i+1)+"");
                    
                }
            }
        }
    }
    private void InObstacleAvoidanceFree(CplexExtended cplex, IloNumVar[][] MUt, IloNumVar InDjti[][][], IloNumVar Ijt[][], final int DN) throws IloException{
        //final double DN_cost = 1;//1e-3; // 1e-5;
        //final double M_max = M(DN-1);
        final double B0 = 1-2*inst.map.DELTA;
        final double F0 = Normal.inverseStandardCdf(B0);

        IloNumVar Bjtni[][][][] = new IloNumVar[inst.map.I][inst.map.T+1][DN][];
        for(int j=0; j<inst.map.I; j++){
            for(int t=0; t<inst.map.T+1; t++){
                for(int n=0; n<DN; n++){
                    Bjtni[j][t][n] = new IloNumVar[inst.I(j)];
                    for(int i=0; i<inst.I(j); i++){
                        double x0 = 1-2*inst.map.DELTA/Math.pow(2, n);
                        double x1 = 1-2*inst.map.DELTA/Math.pow(2, n+1);
                        Bjtni[j][t][n][i] = cplex.numVar(0,  (x1-x0)*(n+1==DN ? 2 : 1) , "B"+(j+1)+""+(t+1)+""+(n+1)+""+(i+1));
                    }
                }
            }
        }
        for(int j=0; j<inst.map.I; j++){
            for(int t=0; t<inst.map.T+1; t++){
                for(int i=0; i<inst.I(j); i++){
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
        for(int j=0; j<inst.map.I; j++){
            for(int t=0; t<inst.map.T+1; t++){
                
                IloNumExpr exp[] = new IloNumExpr[inst.I(j)];
                for(int i=0; i<inst.I(j); i++){
                    double Rjti = inst.map.In[j].R(i, inst.map.sum_t[t]);
                    
                    IloNumExpr Cit = null;
                    double Cit0 = Rjti*F0;
                    for(int n=0; n<DN; n++){
                        Cit = (Cit==null) ? cplex.prod(M(n), Bjtni[j][t][n][i]) :  cplex.sum(Cit, cplex.prod(M(n), Bjtni[j][t][n][i]));
                    }
                    exp[i] = cplex.SumProd(exp[i], Rjti, Cit);
                    exp[i] = cplex.SumProd(exp[i], +inst.map.In[j].lines[i].ax, MUt[t][0]);
                    exp[i] = cplex.SumProd(exp[i], +inst.map.In[j].lines[i].ay, MUt[t][2]);
                    
                    double val = +inst.map.In[j].lines[i].b - Cit0;
                    exp[i] = cplex.sum(exp[i], -val);
                    
                    //cplex.addLe(exp, val, "In."+(j+1)+","+(t+1)+","+(i+1)+"");
                   
                }
                cplex.addIF_Y_Them_Le("I", Ijt[j][t], exp);
            }
        }
    }
    private void UControlAvoidanceFree(CplexExtended cplex, IloNumVar[][] Ut, IloNumVar UDt[][], final int DN) throws IloException{
        if(inst.map.U==null){
            return;
        }
        //final double DN_cost = 1;//1e-3; // 1e-5;
        //final double M_max = M(DN-1);
        final double B0 = 1-2*inst.map.DELTA;
        final double F0 = Normal.inverseStandardCdf(B0);

        IloNumVar Btni[][][] = new IloNumVar[inst.map.T][DN][];
        for(int t=0; t<inst.map.T; t++){
            for(int n=0; n<DN; n++){
                Btni[t][n] = new IloNumVar[inst.map.U.length()];
                for(int i=0; i<inst.map.U.length(); i++){
                    double x0 = 1-2*inst.map.DELTA/Math.pow(2, n);
                    double x1 = 1-2*inst.map.DELTA/Math.pow(2, n+1);
                    Btni[t][n][i] = cplex.numVar(0,  (x1-x0)*(n+1==DN ? 2 : 1) , "B"+(t+1)+""+(n+1)+""+(i+1));
                }
            }
        }
        for(int t=0; t<inst.map.T; t++){
            for(int i=0; i<inst.map.U.length(); i++){
                //1-2*Djt >= Bjt0i + sum_n{Bjtni} para todo i
                IloNumExpr exp = null;
                for(int n=0; n<DN; n++){
                    exp = exp==null ? Btni[t][n][i] : cplex.sum(exp, Btni[t][n][i]);
                }
                cplex.addGe(cplex.sum(exp, cplex.prod(2,UDt[t][i])), 1 - B0);
            }
        }
        
        //-------------------------------- (36) --------------------------------
        for(int t=0; t<inst.map.T; t++){
            for(int i=0; i<inst.map.U.length(); i++){
                double Rjti = inst.map.U.U(i, inst.map.sum_Ut[t]);

                IloNumExpr Cit = null;
                double Cit0 = Rjti*F0;
                for(int n=0; n<DN; n++){
                    Cit = (Cit==null) ? cplex.prod(M(n), Btni[t][n][i]) :  cplex.sum(Cit, cplex.prod(M(n), Btni[t][n][i]));
                }
                IloNumExpr exp = cplex.prod(Rjti, Cit);
                exp = cplex.sum(exp, cplex.prod(+inst.map.U.lines[i].ax, Ut[t][0]));
                exp = cplex.sum(exp, cplex.prod(+inst.map.U.lines[i].ay, Ut[t][1]));

                double val = +inst.map.U.lines[i].b - Cit0;
                cplex.addLe(exp, val, "U."+(t+1)+","+(i+1)+"");

            }
        }
        
    }
    private double M(int n){
        double x0 = 1-2*inst.map.DELTA/Math.pow(2, n);
        double x1 = 1-2*inst.map.DELTA/Math.pow(2, n+1);//x1>x0

        //x = 1 - 2*delta/2^n
        //n = DN
        //

        double y0 = Normal.inverseStandardCdf(x0);
        double y1 = Normal.inverseStandardCdf(x1);

        double m = (y1-y0)/(x1-x0);
        return m;
    }
    
    
    
    protected IloNumVar[][][][] create_alfa(CplexExtended cplex) throws IloException{
        IloNumVar alfa[][][][];
        //inicia a nova variavel
        alfa = new IloNumVar[inst.map.J][inst.map.T + 1][][];
        for (int j = 0; j < inst.map.J; j++) {
            for (int t = 0; t < inst.map.T + 1; t++) {
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
        alfa = new IloNumVar[inst.map.E][inst.map.T + 1][][];
        for (int j = 0; j < inst.map.E; j++) {
            for (int t = 0; t < inst.map.T + 1; t++) {
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
    
    private void addModuleAlfa(CplexExtended cplex, IloIntVar[][][] Zjti, IloNumVar alfa[][][][]) throws IloException{
        //-----------------------------Restrições novas------------------------
       //Primeira restriçao
       for (int j = 0; j < inst.map.J; j++) {
           for (int i = 0; i < inst.G(j); i++) {
               for (int l = 0; l < inst.G(j); l++) {
                   if (l != i) {
                       IloNumExpr exp0 = cplex.sum(cplex.prod(alfa[j][0][i][l], -1), Zjti[j][0][i], Zjti[j][0][l]);
                       cplex.addLe(exp0, 1, "NEWexp1." + (j + 1) + "," + (i + 1) + "," + (l + 1) + "," + 1);

                       for (int t = 1; t < inst.map.T + 1; t++) {
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
                       for (int t = 0; t < inst.map.T + 1; t++) {
                           alfa[j][t][i][l].setUB(0);
                       }
                   }
               }
           }
       }
       for (int j = 0; j < inst.map.J; j++) {
           for (int t = 0; t < inst.map.T + 1; t++) {
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
       for (int j = 0; j < inst.map.E; j++) {
           for (int t = 0; t < inst.map.T + 1; t++) {
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
       for (int j = 0; j < inst.map.E; j++) {
           for (int t = 0; t < inst.map.T + 1; t++) {
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
    protected void addSumZjti_1(CplexExtended cplex, IloIntVar[][][] Zjti) throws IloException{
        for(int j=0; j<inst.map.J; j++){
            for(int t=0; t<inst.map.T+1; t++){
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
    private void addSumZjti_2(CplexExtended cplex, IloIntVar[][][] Zjti, IloNumVar alfa[][][][]) throws IloException{
        //Segunda restrição
        final double ALPHA = 1.0;
        for (int j = 0; j < inst.map.J; j++) {
            for (int t = 0; t < inst.map.T + 1; t++) {
                IloNumExpr exp = null;
                for (int i = 0; i < inst.G(j); i++) {
                    exp = cplex.SumProd(exp, 1, Zjti[j][t][i]);
                    IloNumExpr exp2 = null;
                    for (int l = 0; l < inst.G(j); l++) {
                        exp2 = cplex.SumProd(exp2, ALPHA, alfa[j][t][i][l]);
                    }
                    exp = cplex.sum(exp, cplex.prod(exp2, -1));
                }
                cplex.addGe(exp, 1, "NEW2." + (j + 1) + "," + (t + 1));
            }
        }
    }
    private void addDySumZjti_2(CplexExtended cplex, IloIntVar[][][] DyZjti, IloNumVar alfa[][][][]) throws IloException{
        //Segunda restrição
        final double ALPHA = 1.0;
        for (int j = 0; j < inst.map.E; j++) {
            for (int t = 0; t < inst.map.T + 1; t++) {
                IloNumExpr exp = null;
                for (int i = 0; i < inst.E(j,t); i++) {
                    exp = cplex.SumProd(exp, 1, DyZjti[j][t][i]);
                    IloNumExpr exp2 = null;
                    for (int l = 0; l < inst.E(j,t); l++) {
                        exp2 = cplex.SumProd(exp2, ALPHA, alfa[j][t][i][l]);
                    }
                    exp = cplex.sum(exp, cplex.prod(exp2, -1));
                }

                cplex.addGe(exp, 1, "DyNEW2." + (j + 1) + "," + (t + 1));
            }
        }
    }
}
