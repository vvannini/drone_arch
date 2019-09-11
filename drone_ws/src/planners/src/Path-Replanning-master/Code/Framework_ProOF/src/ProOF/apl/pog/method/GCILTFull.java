
package ProOF.apl.pog.method;

import ProOF.apl.pog.problem.GCILT.GCILTInstance;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.language.Run;
import ProOF.CplexExtended.CplexExtended;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.UnknownObjectException;

/**
 *
 * @author Hossomi
 */
public class GCILTFull extends Run {

    private GCILTInstance inst = new GCILTInstance();
    private double execTime;
    private double epGap;
    private int threads;
    private CplexExtended cplex;
    
    private IloNumVar Xikt[][][];
    private IloNumVar Yuikt[][][][];
    private IloNumVar Iipt[][][];   
    private IloNumVar Wipvt[][][][]; 
    private IloNumVar I0ip[][];     
    private IloNumVar Blyt[][][];   
    private IloNumVar Alyt[][][];   
    private IloNumVar Tluyt[][][][];
    private IloNumVar Vlyt[][][];   
    private IloNumVar Zyt[][];      
    
    private IloNumExpr ObjHold;
    private IloNumExpr ObjSwaping;
    private IloNumExpr ObjTransfer;
    private IloNumExpr ObjOverHold;
    private IloNumExpr ObjValue;
    
    private double time;
    
    @Override
    public String name() {
        return "GCILT Full Model";
    }

    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void services(LinkerApproaches com) throws Exception {
        com.add(inst);
    }

    @Override
    public void parameters(LinkerParameters com) throws Exception {
        execTime    = com.Dbl("Time", 3600.0, 1.0, 180000.0);
        epGap       = com.Dbl("Gap Rel", 0.0001, 0.0, 100.0);
        threads     = com.Int("Threads", 1, 0, 16);
    }

    @Override
    public boolean validation(LinkerValidations com) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void load() throws Exception {
    }

    @Override
    public void results(LinkerResults com) throws Exception {
        com.writeDbl("Best Time", time);
        com.writeString("Status", cplex.getStatus().toString());
        com.writeDbl("Obj Value", cplex.getObjValue());
        com.writeDbl("Lower Bound", cplex.getBestObjValue());
        com.writeDbl("Swap Cost", cplex.getValue(ObjSwaping));
        com.writeDbl("Holding Cost", cplex.getValue(ObjHold));
        com.writeDbl("Transfer Cost", cplex.getValue(ObjTransfer));
        com.writeDbl("OverHold Cost", cplex.getValue(ObjOverHold));
        
    }
    
    @Override
    public void start() throws Exception {
        cplex = new CplexExtended();
        
//        Xikt = cplex.intVarArray(inst.N, inst.K, inst.T, 0, 31, "X");
//        Yuikt = cplex.boolVarArray(inst.L, inst.N, inst.K, inst.T, "Y");
//        Iipt = cplex.numVarArray(inst.N, inst.P, inst.T, 0, Double.MAX_VALUE, "I");
//        Wipvt = cplex.numVarArray(inst.N, inst.P, inst.P, inst.T, 0, Double.MAX_VALUE, "W"); 
//        I0ip = cplex.numVarArray(inst.N, inst.P, 0, Double.MAX_VALUE, "Io");
//        
//        Blyt = cplex.numVarArray(inst.L, inst.Y, inst.T, 0, 31, "B");
//        
//        Alyt = cplex.boolVarArray(inst.L, inst.Y, inst.T, "A");
//        Tluyt = cplex.boolVarArray(inst.L, inst.L, inst.Y, inst.T, "T");
//        Vlyt = cplex.numVarArray(inst.L, inst.Y, inst.T, 0, Integer.MAX_VALUE, "V");
//        Zyt = cplex.boolVarArray(inst.Y, inst.T, "Z");
//        
        
        System.out.println("Y = "+inst.Y);
        
        Xikt = new IloNumVar[inst.N][inst.K][inst.T];
        for(int i=0; i<inst.N; i++){
            for(int k=0; k<inst.K; k++){
                for(int t=0; t<inst.T; t++){
                    Xikt[i][k][t] = cplex.intVar(0, 31, String.format("%s#%d#%d#%d", "Xikt", i, k, t));
                }
            }
        }
        Yuikt = new IloNumVar[inst.L][inst.N][inst.K][inst.T];
        for(int l=0; l<inst.L; l++){
            for(int i=0; i<inst.N; i++){
                for(int k=0; k<inst.K; k++){
                    for(int t=0; t<inst.T; t++){
                        Yuikt[l][i][k][t] = cplex.boolVar(String.format("%s#%d#%d#%d#%d", "Ylikt", l, i, k, t));
                    }
                }
            }
        }
        Iipt = new IloNumVar[inst.N][inst.P][inst.T];
        for(int i=0; i<inst.N; i++){
            for(int p=0; p<inst.P; p++){
                for(int t=0; t<inst.T; t++){
                    Iipt[i][p][t] = cplex.numVar(0, Double.MAX_VALUE, String.format("%s#%d#%d#%d", "Iipt", i, p, t));
                }
            }
        }
        Wipvt = new IloNumVar[inst.N][inst.P][inst.P][inst.T];
        for(int i=0; i<inst.N; i++){
            for(int p=0; p<inst.P; p++){
                for(int v=0; v<inst.P; v++){
                    for(int t=0; t<inst.T; t++){
                        Wipvt[i][p][v][t] = cplex.numVar(0, Double.MAX_VALUE, String.format("%s#%d#%d#%d#%d", "Wipvt", i, p, v, t));
                    }
                }
            }
        }    
        I0ip = new IloNumVar[inst.N][inst.P];
        for(int i=0; i<inst.N; i++){
            for(int p=0; p<inst.P; p++){
                I0ip[i][p] = cplex.numVar(0, Double.MAX_VALUE, String.format("%s#%d#%d", "qip", i, p));
            }
        }
        Blyt = new IloNumVar[inst.L][inst.Y][inst.T];
        for(int l=0; l<inst.L; l++){
            for(int y=0; y<inst.Y; y++){
                for(int t=0; t<inst.T; t++){
                    Blyt[l][y][t] = cplex.numVar(0, 31, String.format("%s#%d#%d#%d", "Blyt", l, y, t));
                }
            }
        }
        Alyt = new IloNumVar[inst.L][inst.Y][inst.T];
        for(int l=0; l<inst.L; l++){
            for(int y=0; y<inst.Y; y++){
                for(int t=0; t<inst.T; t++){
                    Alyt[l][y][t] = cplex.boolVar(String.format("%s#%d#%d#%d", "alyt", l, y, t));
                }
            }
        }
        Tluyt = new IloNumVar[inst.L][inst.L][inst.Y][inst.T];
        for(int l=0; l<inst.L; l++){
            for(int u=0; u<inst.L; u++){
                for(int y=0; y<inst.Y; y++){
                    for(int t=0; t<inst.T; t++){
                        Tluyt[l][u][y][t] = cplex.boolVar(String.format("%s#%d#%d#%d#%d", "Tluyt", l, u, y, t));
                    }
                }
            }
        }
        Vlyt = new IloNumVar[inst.L][inst.Y][inst.T];
        for(int l=0; l<inst.L; l++){
            for(int y=0; y<inst.Y; y++){
                for(int t=0; t<inst.T; t++){
                    Vlyt[l][y][t] = cplex.boolVar(String.format("%s#%d#%d#%d", "Vuyt", l, y, t));
                }
            }
        }
        Zyt = new IloNumVar[inst.Y][inst.T];
        for(int y=0; y<inst.Y; y++){
            for(int t=0; t<inst.T; t++){
                Zyt[y][t] = cplex.boolVar(String.format("%s#%d#%d", "Zyt", y, t));
            }
        }
        //        Alyt = cplex.boolVarArray(inst.L, inst.Y, inst.T, "A");
//        Tluyt = cplex.boolVarArray(inst.L, inst.L, inst.Y, inst.T, "T");
//        Vlyt = cplex.numVarArray(inst.L, inst.Y, inst.T, 0, Integer.MAX_VALUE, "V");
//        Zyt = cplex.boolVarArray(inst.Y, inst.T, "Z");
        
        
        //--------------------------Definindo função objetivo ------------------
        IloNumExpr Obj_luyt[][][][] = new IloNumExpr[inst.L][inst.L][inst.Y][inst.T];
        for(int l=0; l<inst.L; l++){
            for(int u=0; u<inst.L; u++){
                for(int y=0; y<inst.Y; y++){
                    for(int t=0; t<inst.T; t++){
                        Obj_luyt[l][u][y][t] = cplex.prod(Tluyt[l][u][y][t], inst.SCluy[l][u][y]);
                    }
                }
            }
        }
        
        IloNumExpr Obj_ipvt[][][][] = new IloNumExpr[inst.N][inst.P][inst.P][inst.T];
        for(int i=0; i<inst.N; i++){
            for(int p=0; p<inst.P; p++){
                for(int v=0; v<inst.P; v++){
                    for(int t=0; t<inst.T; t++){
                        Obj_ipvt[i][p][v][t] = cplex.prod(Wipvt[i][p][v][t], inst.Ripv[i][p][v]);
                    }
                }
            }
        }
        
        IloNumExpr Obj_ipt[][][] = new IloNumExpr[inst.N][inst.P][inst.T];
        for(int i=0; i<inst.N; i++){
            for(int p=0; p<inst.P; p++){
                for(int t=0; t<inst.T; t++){
                    Obj_ipt[i][p][t] = cplex.prod(Iipt[i][p][t], inst.Hip[i][p]);
                }
            }
        }
        
        IloNumExpr Obj_ip[][] = new IloNumExpr[inst.N][inst.P];
        for(int i=0; i<inst.N; i++){
            for(int p=0; p<inst.P; p++){
                Obj_ip[i][p] = cplex.prod(I0ip[i][p], inst.Pip[i][p]);
            }
        }

        
        ObjHold      = Sum(cplex, Obj_ipt);
        ObjSwaping   = Sum(cplex, Obj_luyt);
        ObjTransfer  = Sum(cplex, Obj_ipvt);
        ObjOverHold  = Sum(cplex, Obj_ip);
        
        ObjValue = cplex.sum(ObjHold, ObjSwaping, ObjTransfer, ObjOverHold);
        
        cplex.addMinimize(ObjValue);
        
        //----------------------------------------------------------------------
        //Wipvt = 0 se p=v
        for(int i=0; i<inst.N; i++){
            for(int p=0; p<inst.P; p++){
                for(int t=0; t<inst.T; t++){
                    Wipvt[i][p][p][t].setLB(0);
                    Wipvt[i][p][p][t].setUB(0);
                }
            }
        }
        //----------------------------------------------------------------------
        //Tluyt = 0 se l=u
        for(int l=0; l<inst.L; l++){
            for(int y=0; y<inst.Y; y++){
                for(int t=0; t<inst.T; t++){
                    Tluyt[l][l][y][t].setLB(0);
                    Tluyt[l][l][y][t].setUB(0);
                    for(int i : inst.Nl[l]){
                        for(int k : inst.Ky[y]){
                            Yuikt[l][i][k][t].setLB(0);
                            Yuikt[l][i][k][t].setUB(0);
                        }
                    }
                }
            }
        }

        //--------------------------------- sub(2) -----------------------------
//        for(int l=0; l<inst.L; l++){
//            for(int i : inst.Nl[l]){
//                for(int p=0; p<inst.P; p++){
//                    for(int t=0; t<inst.T; t++){
//                        IloNumExpr hold_out = cplex.prod(Iipt[i][p][t], +1);
//
//                        IloNumExpr transfer_out = null;
//                        for(int v=0; v<inst.P; v++){
//                            IloNumExpr temp = cplex.prod(Wipvt[i][p][v][t], +1);
//                            if(transfer_out==null){
//                                transfer_out = temp;
//                            }else{
//                                transfer_out = cplex.sum(transfer_out, temp);
//                            }
//                        }
//                        IloNumExpr transfer_in = null;
//                        for(int v=0; v<inst.P; v++){
//                            IloNumExpr temp = cplex.prod(Wipvt[i][v][p][t], -1);
//                            if(transfer_in==null){
//                                transfer_in = temp;
//                            }else{
//                                transfer_in = cplex.sum(transfer_in, temp);
//                            }
//                        }
//                        IloNumExpr prod = null;
//                        for(int y: inst.Fp[p]){
//                            for(int k: inst.Ky[y]){
//                                IloNumExpr temp = cplex.prod(Xikt[i][k][t], -inst.Ek[k]*inst.Pik[i][k]);
//                                if(prod==null){
//                                    prod = temp;
//                                }else{
//                                    prod = cplex.sum(prod, temp);
//                                }
//                                for(int u=0; u<inst.L; u++){
//                                    prod = cplex.sum(prod, cplex.prod(Yuikt[u][i][k][t], -inst.Lluy(u, l, y)*inst.Ek[k]*inst.Pik[i][k]));
//                                }
//                            }
//                        }
//                        if(prod==null){
//                            cplex.output().println("prod==null");
//                        }
//
//                        if(t==0){
//                            IloNumExpr overhold_in = cplex.prod(I0ip[i][p], -1);
//
//                            if(transfer_in==null && transfer_out==null){
//                                cplex.addEq(
//                                        cplex.sum(hold_out, overhold_in, prod),
//                                        /*inst.Iip0[i][p]*/-inst.Dipt[i][p][t],
//                                        "Estoque("+i+","+p+","+t+")");
//                            }else{
//                                cplex.addEq(
//                                        cplex.sum(hold_out, transfer_out, overhold_in, transfer_in, prod),
//                                        /*inst.Iip0[i][p]*/-inst.Dipt[i][p][t],
//                                        "Estoque("+i+","+p+","+t+")");
//                            }
//                        }else{
//                            IloNumExpr hold_in = cplex.prod(Iipt[i][p][t-1], -1);
//
//                            if(transfer_in==null && transfer_out==null){
//                                cplex.addEq(
//                                    cplex.sum(hold_out, hold_in, prod),
//                                    -inst.Dipt[i][p][t],
//                                    "Estoque("+i+","+p+","+t+")");
//                            }else{
//                                cplex.addEq(
//                                    cplex.sum(hold_out, transfer_out, hold_in, transfer_in, prod),
//                                    -inst.Dipt[i][p][t],
//                                    "Estoque("+i+","+p+","+t+")");
//                            }
//
//                        }
//                    }
//                }
//            }
//        }
        for(int l=0; l<inst.L; l++){
            for(int i : inst.Nl[l]){
                for(int p=0; p<inst.P; p++){
                    int t=0;
                    IloNumExpr hold_out = cplex.prod(Iipt[i][p][t], +1);

                    IloNumExpr transfer_out = null;
                    for(int v=0; v<inst.P; v++){
                        IloNumExpr temp = cplex.prod(Wipvt[i][p][v][t], +1);
                        if(transfer_out==null){
                            transfer_out = temp;
                        }else{
                            transfer_out = cplex.sum(transfer_out, temp);
                        }
                    }
                    IloNumExpr transfer_in = null;
                    for(int v=0; v<inst.P; v++){
                        IloNumExpr temp = cplex.prod(Wipvt[i][v][p][t], -1);
                        if(transfer_in==null){
                            transfer_in = temp;
                        }else{
                            transfer_in = cplex.sum(transfer_in, temp);
                        }
                    }
                    IloNumExpr prod = null;
                    for(int y: inst.Fp[p]){
                        for(int k: inst.Ky[y]){
                            //System.out.println("y = "+y+" k ="+k);
                            IloNumExpr temp = cplex.prod(Xikt[i][k][t], -inst.Ek[k]*inst.Pik[i][k]);
                            if(prod==null){
                                prod = temp;
                            }else{
                                prod = cplex.sum(temp, prod);
                            }
                            for(int u=0; u<inst.L; u++){
                                prod = cplex.sum( cplex.prod(-inst.Lluy(u, l, y)*inst.Ek[k]*inst.Pik[i][k], Yuikt[u][i][k][t]), prod);
                            }
                        }
                    }
                    if(prod==null){
                        cplex.output().println("prod==null");
                    }else{
                        //cplex.output().println("prod=="+prod.toString());
                    }

                    if(t==0){
                        IloNumExpr overhold_in = cplex.prod(I0ip[i][p], -1);

                        if(transfer_in==null && transfer_out==null){
                            cplex.addEq(
                                    cplex.sum(hold_out, overhold_in, prod),
                                    -inst.Dipt[i][p][t]);
                        }else{
                            cplex.addEq(
                                    cplex.sum(hold_out, transfer_out, overhold_in, transfer_in, prod),
                                    -inst.Dipt[i][p][t]);
                        }
                    }else{
                        IloNumExpr hold_in = cplex.prod(Iipt[i][p][t-1], -1);

                        if(transfer_in==null && transfer_out==null){
                            cplex.addEq(
                                cplex.sum(hold_out, hold_in, prod),
                                -inst.Dipt[i][p][t]);
                        }else{
                            cplex.addEq(
                                cplex.sum(hold_out, transfer_out, hold_in, transfer_in, prod),
                                -inst.Dipt[i][p][t]);
                        }

                    }
                }
            }
        }
        for(int l=0; l<inst.L; l++){
            for(int i : inst.Nl[l]){
                for(int p=0; p<inst.P; p++){
                    for(int t=1; t<inst.T; t++){
                        IloNumExpr hold_out = cplex.prod(Iipt[i][p][t], +1);

                        IloNumExpr transfer_out = null;
                        for(int v=0; v<inst.P; v++){
                            IloNumExpr temp = cplex.prod(Wipvt[i][p][v][t], +1);
                            if(transfer_out==null){
                                transfer_out = temp;
                            }else{
                                transfer_out = cplex.sum(transfer_out, temp);
                            }
                        }
                        IloNumExpr transfer_in = null;
                        for(int v=0; v<inst.P; v++){
                            IloNumExpr temp = cplex.prod(Wipvt[i][v][p][t], -1);
                            if(transfer_in==null){
                                transfer_in = temp;
                            }else{
                                transfer_in = cplex.sum(transfer_in, temp);
                            }
                        }
                        IloNumExpr prod = null;
                        for(int y: inst.Fp[p]){
                            for(int k: inst.Ky[y]){
                                IloNumExpr temp = cplex.prod(Xikt[i][k][t], -inst.Ek[k]*inst.Pik[i][k]);
                                if(prod==null){
                                    prod = temp;
                                }else{
                                    prod = cplex.sum(prod, temp);
                                }
                                for(int u=0; u<inst.L; u++){
                                    prod = cplex.sum(prod, cplex.prod(Yuikt[u][i][k][t], -inst.Lluy(u, l, y)*inst.Ek[k]*inst.Pik[i][k]));
                                }
                            }
                        }
                        if(prod==null){
                            cplex.output().println("prod==null");
                        }

                        if(t==0){
                            IloNumExpr overhold_in = cplex.prod(I0ip[i][p], -1);

                            if(transfer_in==null && transfer_out==null){
                                cplex.addEq(
                                        cplex.sum(hold_out, overhold_in, prod),
                                        -inst.Dipt[i][p][t]);
                            }else{
                                cplex.addEq(
                                        cplex.sum(hold_out, transfer_out, overhold_in, transfer_in, prod),
                                        -inst.Dipt[i][p][t]);
                            }
                        }else{
                            IloNumExpr hold_in = cplex.prod(Iipt[i][p][t-1], -1);

                            if(transfer_in==null && transfer_out==null){
                                cplex.addEq(
                                    cplex.sum(hold_out, hold_in, prod),
                                    -inst.Dipt[i][p][t]);
                            }else{
                                cplex.addEq(
                                    cplex.sum(hold_out, transfer_out, hold_in, transfer_in, prod),
                                    -inst.Dipt[i][p][t]);
                            }

                        }
                    }
                }
            }
        }
        
        for(int u=0; u<inst.L; u++){
            for(int l=0; l<inst.L; l++){
                for(int y=0; y<inst.Y; y++){
                    for(int k : inst.Ky[y]){
                        for(int t=0; t<inst.T; t++){
                            IloNumExpr sum = null;
                            for(int i : inst.Nl[l]){
                                IloNumExpr temp = cplex.prod(Yuikt[u][i][k][t], 1); //deveria ser Yuikt[l][i][k][t]
                                if(sum==null){
                                    sum = temp;
                                }else{
                                    sum = cplex.sum(sum, temp);
                                }
                            }
                            if(inst.Lluy(u, l, y)>0.001){
                                cplex.addEq(sum,Tluyt[u][l][y][t]);
                            }else{
                                cplex.addEq(sum,0);
                            }
                        }
                    }
                }
            }
        }
        
        
        for(int l=0; l<inst.L; l++){
            for(int y=0; y<inst.Y; y++){
                for(int k : inst.Ky[y]){
                    for(int t=0; t<inst.T; t++){
                        IloNumExpr sum = null;
                        for(int i : inst.Nl[l]){
                            IloNumExpr temp = cplex.prod(Xikt[i][k][t], 1);
                            if(sum==null){
                                sum = temp;
                            }else{
                                sum = cplex.sum(sum, temp);
                            }
                        }
                        for(int u=0; u<inst.L; u++){
                            IloNumExpr temp = cplex.prod(Tluyt[u][l][y][t], inst.STluy[u][l][y] + inst.Lluy(u,l,y));
                            sum = cplex.sum(sum, temp);
                        }
                        cplex.addEq(sum, Blyt[l][y][t]);
                    }
                }
            }
        }
        
        for(int y=0; y<inst.Y; y++){
            for(int t=1; t<inst.T; t++){
                cplex.addLe(Zyt[y][t], Zyt[y][t-1]);
            }
        }
        
        
//        for(int y=0; y<inst.Y; y++){
//            for(int t=0; t<inst.T; t++){
//                IloNumExpr sum = null;
//                for(int l=0; l<inst.L; l++){
//                    IloNumExpr temp = cplex.prod(Blyt[l][y][t], +1);
//                    if(sum==null){
//                        sum = temp;
//                    }else{
//                        sum = cplex.sum(sum, temp);
//                    }
//                }
//                if(t==0){
//                    cplex.addLe(sum, inst.Qt[t]);
//                }else{
//                    cplex.addLe(sum, cplex.prod(inst.Qt[t], Zyt[y][t-1]));
//                }
//                    
//            }
//        }
        for(int y=0; y<inst.Y; y++){
            int t=0;
            IloNumExpr sum = null;
            for(int l=0; l<inst.L; l++){
                IloNumExpr temp = cplex.prod(Blyt[l][y][t], +1);
                if(sum==null){
                    sum = temp;
                }else{
                    sum = cplex.sum(sum, temp);
                }
            }
            if(t==0){
                cplex.addLe(sum, inst.Qt[t]);
            }else{
                cplex.addLe(sum, cplex.prod(inst.Qt[t], Zyt[y][t-1]));
            }
        }
        for(int y=0; y<inst.Y; y++){
            for(int t=1; t<inst.T; t++){
                IloNumExpr sum = null;
                for(int l=0; l<inst.L; l++){
                    IloNumExpr temp = cplex.prod(Blyt[l][y][t], +1);
                    if(sum==null){
                        sum = temp;
                    }else{
                        sum = cplex.sum(sum, temp);
                    }
                }
                if(t==0){
                    cplex.addLe(sum, inst.Qt[t]);
                }else{
                    cplex.addLe(sum, cplex.prod(inst.Qt[t], Zyt[y][t-1]));
                }
                    
            }
        }
        for(int y=0; y<inst.Y; y++){
            for(int t=0; t<inst.T; t++){
                IloNumExpr sum = null;
                for(int l=0; l<inst.L; l++){
                    IloNumExpr temp = cplex.prod(Blyt[l][y][t], +1);
                    if(sum==null){
                        sum = temp;
                    }else{
                        sum = cplex.sum(sum, temp);
                    }
                }
                cplex.addGe(sum, cplex.prod(inst.Qt[t], Zyt[y][t]));
            }
        }
        
        for(int l=0; l<inst.L; l++){
            for(int y=0; y<inst.Y; y++){
                for(int t=0; t<inst.T; t++){
                    IloNumExpr sum = null;
                    for(int k : inst.Ky[y]){
                        for(int i : inst.Nl[l]){
                            IloNumExpr temp = cplex.prod(Xikt[i][k][t], inst.Pik[i][k]);
                            if(sum==null){
                                sum = temp;
                            }else{
                                sum = cplex.sum(sum, temp);
                            }
                            for(int u=0; u<inst.L; u++){
                                temp = cplex.prod(Yuikt[u][i][k][t], inst.Lluy(u, l, y)*inst.Pik[i][k]);
                                sum = cplex.sum(sum, temp);
                            }
                        }
                    }
                    for(int u=0; u<inst.L; u++){
                        IloNumExpr temp = cplex.prod(Tluyt[u][l][y][t], inst.STluy[u][l][y] * inst.Cy[y]);
                        sum = cplex.sum(sum, temp);
                    }
                    cplex.addLe(sum, cplex.prod(Blyt[l][y][t], inst.Cy[y]));
                }
            }
        }
        
        for(int l=0; l<inst.L; l++){
            for(int y=0; y<inst.Y; y++){
                for(int t=0; t<inst.T; t++){
                    for(int k : inst.Ky[y]){
                        for(int i : inst.Nl[l]){
                            IloNumExpr max = cplex.prod(Alyt[l][y][t], inst.Mikt(i,k,t));
                            for(int u=0; u<inst.L; u++){
                                //IloNumExpr temp = cplex.prod(Tluyt[l][u][y][t], inst.Mikt[i][k][t]);
                                IloNumExpr temp = cplex.prod(Tluyt[u][l][y][t], inst.Mikt(i,k,t));
                                max = cplex.sum(max, temp);
                            }
                            
                            IloNumExpr prod = Xikt[i][k][t];
                            for(int u=0; u<inst.L; u++){
                                //prod = cplex.sum(prod, Yuikt[u][i][k][t]);
                                prod = cplex.sum(prod, cplex.prod(inst.Lluy(u,l,y), Yuikt[u][i][k][t]));
                            }
                            
                            cplex.addLe(prod, max);
                        }
                    }
                }
            }
        }
        
        for(int y=0; y<inst.Y; y++){
            for(int t=0; t<inst.T; t++){
                IloNumExpr one = null;
                for(int l=0; l<inst.L; l++){
                    if(one==null){
                        one = Alyt[l][y][t];
                    }else{
                        one = cplex.sum(one, Alyt[l][y][t]);
                    }
                }
                
                cplex.addEq(one, 1);
            }
        }
        
        for(int l=0; l<inst.L; l++){
            for(int y=0; y<inst.Y; y++){
                for(int t=0; t<inst.T; t++){
                    IloNumExpr in = cplex.prod(Alyt[l][y][t], +1);
                    for(int u=0; u<inst.L; u++){
                        IloNumExpr temp = cplex.prod(Tluyt[u][l][y][t], +1);
                        if(in==null){
                            in = temp;
                        }else{
                            in = cplex.sum(in, temp);
                        }
                    }

                    IloNumExpr out = t+1==inst.T ? null : cplex.prod(Alyt[l][y][t+1], -1);
                    for(int u=0; u<inst.L; u++){
                        IloNumExpr temp = cplex.prod(Tluyt[l][u][y][t], -1);
                        if(out==null){
                            out = temp;
                        }else{
                            out = cplex.sum(out, temp);
                        }
                    }
                    
                    if(t+1==inst.T){
                        if(out==null){
                            cplex.addLe(in, 1);
                        }else{
                            cplex.addLe(cplex.sum(in,out), 1);
                        }
                    }else{
                        cplex.addEq(cplex.sum(in,out), 0);
                    }
                }
            }
        }
                    
        
        for(int l=0; l<inst.L; l++){
            for(int u=0; u<inst.L; u++){
                if(l!=u){
                    for(int y=0; y<inst.Y; y++){
                        for(int t=0; t<inst.T; t++){
                            cplex.addGe(cplex.sum(
                                    cplex.prod(Vlyt[u][y][t], +1),
                                    cplex.prod(Vlyt[l][y][t], -1),
                                    cplex.prod(Tluyt[l][u][y][t], -(inst.L)),
                                    cplex.prod(Alyt[l][y][t], +(inst.L))
                                ), 1-(inst.L));
                        }
                    }
                }
            }
        }
        
        //Subject 07(B)
        for(int l=0; l<inst.L; l++){
            for(int y=0; y<inst.Y; y++){
                Alyt[l][y][0].setLB(inst.Aly0[l][y]);
                Alyt[l][y][0].setUB(inst.Aly0[l][y]);
            }
        }
    }

    
    
    @Override
    public void execute() throws Exception {
        final long initial = System.nanoTime();
        
        
        cplex.use(new IloCplex.IncumbentCallback() {
            @Override
            protected void main() throws IloException {
                time = (System.nanoTime() - initial)/1e9;
            }
        });
        
        //Escrita do modelo em arquivo
        cplex.exportModel("LTGCIP.lp");

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
        
        if (cplex.solve()) {
            cplex.output().println("Solution status     = " + cplex.getStatus());
            cplex.output().println("Solution value      = " + cplex.getObjValue());
            cplex.output().println("Solution Swaping    = " + cplex.getValue(ObjSwaping));
            cplex.output().println("Solution Transfer   = " + cplex.getValue(ObjTransfer));
            cplex.output().println("Solution Hold       = " + cplex.getValue(ObjHold));
            cplex.output().println("Solution OverHold   = " + cplex.getValue(ObjOverHold));
            
            double[][][] v_Alyt     = getValues(cplex,Alyt);
            double[][][][] v_Tluyt  = getValues(cplex,Tluyt);
            
            double[][][] v_Blyt     = getValues(cplex,Blyt);
            
            double[][][] v_Xikt     = getValues(cplex,Xikt);
            double[][][][] v_Yuikt    = getValues(cplex,Yuikt);
            
            double[][][][] v_Wipvt  = getValues(cplex,Wipvt);
            double[][][] v_Iipt     = getValues(cplex,Iipt);
            double[][] v_I0ip       = getValues(cplex,I0ip);
            
            double[][] v_Zyt        = getValues(cplex,Zyt);
            //double[][][] v_Vlyt     = getValues(cplex,Vlyt);
            
            
            cplex.output().printf("--------------- Zyt: (y) x t ---------------------\n");
            cplex.output().printf("             | ");
            for(int t=0; t<inst.T; t++){
                cplex.output().printf("%12s ", t);
            }
            cplex.output().printf("\n");
            for(int y=0; y<inst.Y; y++){
                cplex.output().printf("       (%3d) | ", y);
                for(int t=0; t<inst.T; t++){
                    cplex.output().printf("%12.2f ", v_Zyt[y][t]);
                }
                cplex.output().printf("\n");
            }
            cplex.output().printf("--------------- Blyt: (y,l) x t ---------------------\n");
            for(int y=0; y<inst.Y; y++){
                for(int l=0; l<inst.L; l++){
                    boolean teste = false;
                    for(int t=0; t<inst.T; t++){
                        if(v_Blyt[l][y][t] > 0.001){
                           teste = true; 
                           break;
                        }
                    }
                    if(teste){
                        cplex.output().printf("    (%2d,%2d) | ", y, l);
                        for(int t=0; t<inst.T; t++){
                            cplex.output().printf("%5.2f ", v_Blyt[l][y][t]);
                        }
                        cplex.output().printf("\n");
                    }
                }
            }
            
            cplex.output().printf("--------------- Prod: (k,i) x T ---------------------\n");
            for(int y=0; y<inst.Y; y++){
                for(int k : inst.Ky[y]){
                    for(int l=0; l<inst.L; l++){
                        for(int i : inst.Nl[l]){
                            boolean teste = false;
                            for(int t=0; t<inst.T; t++){
                                double flag = 0;
                                for(int u=0; u<inst.L; u++){
                                    if(v_Yuikt[u][i][k][t]>0.5){
                                        flag = inst.Lluy(u,l,y);
                                    }
                                }
                                if(flag>0.001 || v_Xikt[i][k][t] > 0.001){
                                   teste = true; 
                                   break;
                                }
                            }
                            if(teste){
                                cplex.output().printf("    (%2d,%3d) | ", k, i);
                                for(int t=0; t<inst.T; t++){
                                    double flag = 0;
                                    for(int u=0; u<inst.L; u++){
                                        if(v_Yuikt[u][i][k][t]>0.5){
                                            flag = inst.Lluy(u,l,y);
                                        }
                                    }
                                    cplex.output().printf("[%5.0f + %3.0f]", v_Xikt[i][k][t]*inst.Pik[i][k]*inst.Ek[k], flag*inst.Pik[i][k]*inst.Ek[k]);
                                }
                                cplex.output().printf("\n");
                            }
                        }
                    }
                }
            }
            
            cplex.output().printf("--------------- Alyt: (y,l) x t ---------------------\n");
            for(int y=0; y<inst.Y; y++){
                for(int l=0; l<inst.L; l++){
                    boolean teste = false;
                    for(int t=0; t<inst.T; t++){
                        if(v_Alyt[l][y][t] > 0.001){
                           teste = true; 
                           break;
                        }
                    }
                    if(teste){
                        cplex.output().printf("    (%2d,%2d) | ", y, l);
                        for(int t=0; t<inst.T; t++){
                            cplex.output().printf("%5.2f ", v_Alyt[l][y][t]);
                        }
                        cplex.output().printf("\n");
                    }
                }
            }
            cplex.output().printf("--------------- Tluyt: (y,l,u) x t ---------------------\n");
            for(int y=0; y<inst.Y; y++){
                for(int l=0; l<inst.L; l++){
                    for(int u=0; u<inst.L; u++){
                        boolean teste = false;
                        for(int t=0; t<inst.T; t++){
                            if(v_Tluyt[l][u][y][t] > 0.001){
                               teste = true; 
                               break;
                            }
                        }
                        if(teste){
                            cplex.output().printf(" (%2d,%2d,%2d) | ", y, l, u);
                            for(int t=0; t<inst.T; t++){
                                cplex.output().printf("%5.2f ", v_Tluyt[l][u][y][t]);
                            }
                            cplex.output().printf("\n");
                        }
                    }
                }
            }
            
            
            
            cplex.output().printf("--------------- Xikt: (k,i) x T ---------------------\n");
            for(int k=0; k<inst.K; k++){
                for(int i=0; i<inst.N; i++){
                    boolean teste = false;
                    for(int t=0; t<inst.T; t++){
                        if(v_Xikt[i][k][t] > 0.001){
                           teste = true; 
                           break;
                        }
                    }
                    if(teste){
                        cplex.output().printf("    (%2d,%3d) | ", k, i);
                        for(int t=0; t<inst.T; t++){
                            cplex.output().printf("%5.2f ", v_Xikt[i][k][t]);
                        }
                        cplex.output().printf("\n");
                    }
                }
            }
            
            cplex.output().printf("--------------- Yuikt: (u,i,k) x T ---------------------\n");
            for(int y=0; y<inst.Y; y++){
                for(int k : inst.Ky[y]){
                    for(int l=0; l<inst.L; l++){
                        for(int i : inst.Nl[l]){
                            for(int u=0; u<inst.L; u++){
                                boolean teste = false;
                                for(int t=0; t<inst.T; t++){
                                    if(v_Yuikt[u][i][k][t] > 0.001){
                                       teste = true; 
                                       break;
                                    }
                                }
                                if(teste){
                                    cplex.output().printf(" (%2d,%3d,%2d) | ", u, i, k);
                                    for(int t=0; t<inst.T; t++){
                                        cplex.output().printf("%5.2f ", v_Yuikt[u][i][k][t]);
                                    }
                                    cplex.output().printf("\n");
                                }
                                
                            }
                            
                        }
                    }
                }
            }
            
            cplex.output().printf("--------------- I0ip: (i) x p ---------------------\n");
            cplex.output().printf("             | ");
            for(int p=0; p<inst.P; p++){
                cplex.output().printf("%12s ", p);
            }
            cplex.output().printf("\n");
            for(int i=0; i<inst.N; i++){
                boolean teste = false;
                for(int p=0; p<inst.P; p++){
                    if(v_I0ip[i][p] > 0.001){
                       teste = true; 
                       break;
                    }
                }
                if(teste){
                    cplex.output().printf("       (%3d) | ", i);
                    for(int p=0; p<inst.P; p++){
                        cplex.output().printf("%12.2f ", v_I0ip[i][p]);
                    }
                    cplex.output().printf("\n");
                }
            }
 
            
            cplex.output().printf("--------------- Iipt: (p,i) x t ---------------------\n");
            for(int p=0; p<inst.P; p++){
                for(int i=0; i<inst.N; i++){
                    boolean teste = false;
                    for(int t=0; t<inst.T; t++){
                        if(v_Iipt[i][p][t] > 0.001){
                           teste = true; 
                           break;
                        }
                    }
                    if(teste){
                        cplex.output().printf("    (%2d,%3d) | ", p, i);
                        for(int t=0; t<inst.T; t++){
                            cplex.output().printf("%12.2f ", v_Iipt[i][p][t]);
                        }
                        cplex.output().printf("\n");
                    }
                }
            }
            cplex.output().printf("--------------- Wipvt: (i,p,v) x t ---------------------\n");
            for(int i=0; i<inst.N; i++){
                for(int p=0; p<inst.P; p++){
                    for(int v=0; v<inst.P; v++){
                        boolean teste = false;
                        for(int t=0; t<inst.T; t++){
                            if(v_Wipvt[i][p][v][t] > 0.001){
                               teste = true; 
                               break;
                            }
                        }
                        if(teste){
                            cplex.output().printf(" (%3d,%2d,%2d) | ", i, p, v);
                            for(int t=0; t<inst.T; t++){
                                cplex.output().printf("%12.2f ", v_Wipvt[i][p][v][t]);
                            }
                            cplex.output().printf("\n");
                        }
                    }
                }
            }
        }
    }
    
    public static double[] getValues(IloCplex cplex, IloNumVar V[]) throws UnknownObjectException, IloException{
        return cplex.getValues(V);
    }
    public static double[][] getValues(IloCplex cplex, IloNumVar V[][]) throws UnknownObjectException, IloException{
        double X[][] = new double[V.length][];
        for(int i=0; i<V.length; i++){
            X[i] = getValues(cplex, V[i]);
        }
        return X;
    }
    public static double[][][] getValues(IloCplex cplex, IloNumVar V[][][]) throws UnknownObjectException, IloException{
        double X[][][] = new double[V.length][][];
        for(int i=0; i<V.length; i++){
            X[i] = getValues(cplex, V[i]);
        }
        return X;
    }
    public static double[][][][] getValues(IloCplex cplex, IloNumVar V[][][][]) throws UnknownObjectException, IloException{
        double X[][][][] = new double[V.length][][][];
        for(int i=0; i<V.length; i++){
            X[i] = getValues(cplex, V[i]);
        }
        return X;
    }
    public static IloNumExpr Sum(IloCplex cplex, IloNumExpr M[]) throws IloException{
        return cplex.sum(M);
    }
    public static IloNumExpr Sum(IloCplex cplex, IloNumExpr M[][]) throws IloException{
        IloNumExpr aux[] = new IloNumExpr[M.length];
        for(int i=0; i<M.length; i++){
            aux[i] = Sum(cplex, M[i]);
        }
        return cplex.sum(aux);
    }
    public static IloNumExpr Sum(IloCplex cplex, IloNumExpr M[][][]) throws IloException{
        IloNumExpr aux[] = new IloNumExpr[M.length];
        for(int i=0; i<M.length; i++){
            aux[i] = Sum(cplex, M[i]);
        }
        return cplex.sum(aux);
    }
    public static IloNumExpr Sum(IloCplex cplex, IloNumExpr M[][][][]) throws IloException{
        IloNumExpr aux[] = new IloNumExpr[M.length];
        for(int i=0; i<M.length; i++){
            aux[i] = Sum(cplex, M[i]);
        }
        return cplex.sum(aux);
    }
    
}
