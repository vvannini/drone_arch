/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.GCILT.withZyt;

import ProOF.apl.pog.problem.GCILT.GCILTLot;
import ProOF.com.Linker.LinkerResults;
import ProOF.CplexExtended.CplexExtended;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;


/**
 *
 * @author marcio
 */
public final class iGCILTCplexInt extends aGCILTModel{
 
    private CplexExtended cplex;
    
    private IloNumVar Xikt[][][];
    private IloNumVar Yuikt[][][][];
    private IloNumVar Iipt[][][];   
    private IloNumVar Wipvt[][][][]; 
    private IloNumVar I0ip[][];     
    
    private IloRange Yultk[][][][];
    private IloRange Bltk[][][];
    private IloRange Clty[][][];
    private IloRange Xitk[][][];
    
    private IloNumExpr ObjHold;
    private IloNumExpr ObjTransfer;
    private IloNumExpr ObjOverHold;
    private IloNumExpr ObjValue;
    
    private double ObjSwaping = 0;
    
    private int Tulyt[][][][];
    private int Blty_[][][];
    private double Clty_[][][];
    private int Mikt[][][];
    
    @Override
    public String name() {
        return "CplexInt";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void start() throws Exception {
        cplex = new CplexExtended();
        
        cplex.setOut(null);
        cplex.setWarning(null);
        
        Tulyt = new int[gcilt.inst.L][gcilt.inst.L][gcilt.inst.Y][gcilt.inst.T];
        Blty_ = new int[gcilt.inst.L][gcilt.inst.T][gcilt.inst.Y];
        Clty_ = new double[gcilt.inst.L][gcilt.inst.T][gcilt.inst.Y];
        Mikt  = new int[gcilt.inst.N][gcilt.inst.K][gcilt.inst.T];
        
        Xikt = cplex.intVarArray(gcilt.inst.N, gcilt.inst.K, gcilt.inst.T, 0, 31, "X");
        Yuikt = cplex.boolVarArray(gcilt.inst.L, gcilt.inst.N, gcilt.inst.K, gcilt.inst.T, "Y");
        Iipt = cplex.numVarArray(gcilt.inst.N, gcilt.inst.P, gcilt.inst.T, 0, Double.MAX_VALUE, "I");
        Wipvt = cplex.numVarArray(gcilt.inst.N, gcilt.inst.P, gcilt.inst.P, gcilt.inst.T, 0, Double.MAX_VALUE, "W"); 
        I0ip = cplex.numVarArray(gcilt.inst.N, gcilt.inst.P, 0, Double.MAX_VALUE, "Io");
        
        //--------------------------Definindo função objetivo ------------------
        IloNumExpr Obj_ipvt[][][][] = new IloNumExpr[gcilt.inst.N][gcilt.inst.P][gcilt.inst.P][gcilt.inst.T];
        for(int i=0; i<gcilt.inst.N; i++){
            for(int p=0; p<gcilt.inst.P; p++){
                for(int v=0; v<gcilt.inst.P; v++){
                    for(int t=0; t<gcilt.inst.T; t++){
                        Obj_ipvt[i][p][v][t] = cplex.prod(Wipvt[i][p][v][t], gcilt.inst.Ripv[i][p][v]);
                    }
                }
            }
        }
        
        IloNumExpr Obj_ipt[][][] = new IloNumExpr[gcilt.inst.N][gcilt.inst.P][gcilt.inst.T];
        for(int i=0; i<gcilt.inst.N; i++){
            for(int p=0; p<gcilt.inst.P; p++){
                for(int t=0; t<gcilt.inst.T; t++){
                    Obj_ipt[i][p][t] = cplex.prod(Iipt[i][p][t], gcilt.inst.Hip[i][p]);
                }
            }
        }
        
        IloNumExpr Obj_ip[][] = new IloNumExpr[gcilt.inst.N][gcilt.inst.P];
        for(int i=0; i<gcilt.inst.N; i++){
            for(int p=0; p<gcilt.inst.P; p++){
                Obj_ip[i][p] = cplex.prod(I0ip[i][p], gcilt.inst.Pip[i][p]);
            }
        }

        
        ObjHold      = cplex.Sum(Obj_ipt);
        ObjTransfer  = cplex.Sum(Obj_ipvt);
        ObjOverHold  = cplex.Sum(Obj_ip);
        
        ObjValue = cplex.sum(ObjHold, ObjTransfer, ObjOverHold);
        
        cplex.addMinimize(ObjValue);
        
        //----------------------------------------------------------------------
        //Wipvt = 0 se p=v
        for(int i=0; i<gcilt.inst.N; i++){
            for(int p=0; p<gcilt.inst.P; p++){
                for(int t=0; t<gcilt.inst.T; t++){
                    Wipvt[i][p][p][t].setLB(0);
                    Wipvt[i][p][p][t].setUB(0);
                }
            }
        }
        //----------------------------------------------------------------------
        //Tluyt = 0 se l=u
        for(int l=0; l<gcilt.inst.L; l++){
            for(int y=0; y<gcilt.inst.Y; y++){
                for(int t=0; t<gcilt.inst.T; t++){
                    for(int i : gcilt.inst.Nl[l]){
                        for(int k : gcilt.inst.Ky[y]){
                            Yuikt[l][i][k][t].setLB(0);
                            Yuikt[l][i][k][t].setUB(0);
                        }
                    }
                }
            }
        }

        //--------------------------------- sub(2) -----------------------------
        for(int l=0; l<gcilt.inst.L; l++){
            for(int i : gcilt.inst.Nl[l]){
                for(int p=0; p<gcilt.inst.P; p++){
                    for(int t=0; t<gcilt.inst.T; t++){
                        IloNumExpr hold_out = cplex.prod(Iipt[i][p][t], +1);

                        IloNumExpr transfer_out = null;
                        for(int v=0; v<gcilt.inst.P; v++){
                            IloNumExpr temp = cplex.prod(Wipvt[i][p][v][t], +1);
                            if(transfer_out==null){
                                transfer_out = temp;
                            }else{
                                transfer_out = cplex.sum(transfer_out, temp);
                            }
                        }
                        IloNumExpr transfer_in = null;
                        for(int v=0; v<gcilt.inst.P; v++){
                            IloNumExpr temp = cplex.prod(Wipvt[i][v][p][t], -1);
                            if(transfer_in==null){
                                transfer_in = temp;
                            }else{
                                transfer_in = cplex.sum(transfer_in, temp);
                            }
                        }
                        IloNumExpr prod = null;
                        for(int y: gcilt.inst.Fp[p]){
                            for(int k: gcilt.inst.Ky[y]){
                                IloNumExpr temp = cplex.prod(Xikt[i][k][t], -gcilt.inst.Ek[k]*gcilt.inst.Pik[i][k]);
                                if(prod==null){
                                    prod = temp;
                                }else{
                                    prod = cplex.sum(prod, temp);
                                }
                                for(int u=0; u<gcilt.inst.L; u++){
                                    prod = cplex.sum(prod, cplex.prod(Yuikt[u][i][k][t], -gcilt.inst.Lluy(u, l, y)*gcilt.inst.Ek[k]*gcilt.inst.Pik[i][k]));
                                }
                            }
                        }
                        if(prod==null){
                            throw new Exception("prod==null");
                        }

                        if(t==0){
                            IloNumExpr overhold_in = cplex.prod(I0ip[i][p], -1);

                            if(transfer_in==null && transfer_out==null){
                                cplex.addEq(
                                        cplex.sum(hold_out, overhold_in, prod),
                                        /*gcilt.inst.Iip0[i][p]*/-gcilt.inst.Dipt[i][p][t],
                                        "Estoque("+i+","+p+","+t+")");
                            }else{
                                cplex.addEq(
                                        cplex.sum(hold_out, transfer_out, overhold_in, transfer_in, prod),
                                        /*gcilt.inst.Iip0[i][p]*/-gcilt.inst.Dipt[i][p][t],
                                        "Estoque("+i+","+p+","+t+")");
                            }
                        }else{
                            IloNumExpr hold_in = cplex.prod(Iipt[i][p][t-1], -1);

                            if(transfer_in==null && transfer_out==null){
                                cplex.addEq(
                                    cplex.sum(hold_out, hold_in, prod),
                                    -gcilt.inst.Dipt[i][p][t],
                                    "Estoque("+i+","+p+","+t+")");
                            }else{
                                cplex.addEq(
                                    cplex.sum(hold_out, transfer_out, hold_in, transfer_in, prod),
                                    -gcilt.inst.Dipt[i][p][t],
                                    "Estoque("+i+","+p+","+t+")");
                            }

                        }
                    }
                }
            }
        }
        Yultk = new IloRange[gcilt.inst.L][gcilt.inst.L][gcilt.inst.T][gcilt.inst.K];
        for(int u=0; u<gcilt.inst.L; u++){
            for(int l=0; l<gcilt.inst.L; l++){
                for(int t=0; t<gcilt.inst.T; t++){
                    for(int y=0; y<gcilt.inst.Y; y++){
                        for(int k : gcilt.inst.Ky[y]){
                            IloNumExpr sum = null;
                            for(int i : gcilt.inst.Nl[l]){
                                IloNumExpr temp = cplex.prod(Yuikt[u][i][k][t], 1);
                                if(sum==null){
                                    sum = temp;
                                }else{
                                    sum = cplex.sum(sum, temp);
                                }
                            }
                            if(gcilt.inst.Lluy(u, l, y)>0.001){
                                Yultk[u][l][t][k] = cplex.addEq(sum,1,
                                    "Subject03("+u+","+l+","+y+","+k+","+t+")");
                            }else{
                                Yultk[u][l][t][k] = cplex.addEq(sum,0,
                                    "Subject03("+u+","+l+","+y+","+k+","+t+")");
                            }
                        }
                    }
                }
            }
        }
        
        Bltk = new IloRange[gcilt.inst.L][gcilt.inst.T][gcilt.inst.K];
        for(int l=0; l<gcilt.inst.L; l++){
            for(int t=0; t<gcilt.inst.T; t++){
                for(int y=0; y<gcilt.inst.Y; y++){
                    for(int k : gcilt.inst.Ky[y]){
                        IloNumExpr sum = null;
                        for(int i : gcilt.inst.Nl[l]){
                            IloNumExpr temp = cplex.prod(Xikt[i][k][t], 1);
                            if(sum==null){
                                sum = temp;
                            }else{
                                sum = cplex.sum(sum, temp);
                            }
                        }
                        Bltk[l][t][k] = cplex.addEq(sum, gcilt.inst.Qt[t],
                            "Subject04("+l+","+y+","+t+")");
                    }
                }
            }
        }
        
        Clty = new IloRange[gcilt.inst.L][gcilt.inst.T][gcilt.inst.Y];
        for(int l=0; l<gcilt.inst.L; l++){
            for(int t=0; t<gcilt.inst.T; t++){
                for(int y=0; y<gcilt.inst.Y; y++){
                    IloNumExpr sum = null;
                    for(int k : gcilt.inst.Ky[y]){
                        for(int i : gcilt.inst.Nl[l]){
                            IloNumExpr temp = cplex.prod(Xikt[i][k][t], gcilt.inst.Pik[i][k]);
                            if(sum==null){
                                sum = temp;
                            }else{
                                sum = cplex.sum(sum, temp);
                            }
                            for(int u=0; u<gcilt.inst.L; u++){
                                temp = cplex.prod(Yuikt[u][i][k][t], gcilt.inst.Lluy(u, l, y)*gcilt.inst.Pik[i][k]);
                                sum = cplex.sum(sum, temp);
                            }
                        }
                    }
                    Clty[l][t][y] = cplex.addLe(sum, gcilt.inst.Cy[y]*gcilt.inst.Qt[t],
                        "Subject08("+l+","+y+","+t+")");
                }
            }
        }
 
        Xitk = new IloRange[gcilt.inst.N][gcilt.inst.T][gcilt.inst.K];
        for(int l=0; l<gcilt.inst.L; l++){
            for(int t=0; t<gcilt.inst.T; t++){
                for(int y=0; y<gcilt.inst.Y; y++){
                    for(int k : gcilt.inst.Ky[y]){
                        for(int i : gcilt.inst.Nl[l]){
                            IloNumExpr prod = Xikt[i][k][t];
                            for(int u=0; u<gcilt.inst.L; u++){
                                prod = cplex.sum(prod, cplex.prod(gcilt.inst.Lluy(u,l,y), Yuikt[u][i][k][t]));
                            }
                            
                            Xitk[i][t][k] = cplex.addLe(prod, gcilt.inst.Mikt(i,k,t),
                                "Subject09("+y+","+t+")");
                        }
                    }
                }
            }
        }
    }
    
    
    private boolean print = false;
    public void set(boolean print){
        this.print = print;
    }
    @Override
    public void results(LinkerResults com) throws Exception {
        super.results(com);
        
        com.writeString("Status", cplex.getStatus().toString());
        com.writeDbl("Obj Value", cplex.getObjValue()+ObjSwaping);
        com.writeDbl("Lower Bound", cplex.getBestObjValue()+ObjSwaping);
        com.writeDbl("Swap Cost", ObjSwaping);
        com.writeDbl("Holding Cost", cplex.getValue(ObjHold));
        com.writeDbl("Transfer Cost", cplex.getValue(ObjTransfer));
        com.writeDbl("OverHold Cost", cplex.getValue(ObjOverHold));
    }
    
    @Override
    public double initialize(GCILTCodification codif) throws Exception {
        double penalty = 0;
        
        ObjSwaping = 0;
        clear(Tulyt);
        clear(Blty_);
        clear(Clty_);
        clear(Mikt);
        
        
        //-------------- penalities : begin -------------
        /*for(int y=0; y<gcilt.inst.Y; y++){
            int u = gcilt.inst.ovenStartColor(y);
            for(int t=0; t<gcilt.inst.T; t++){
                for(GCILTLot lot : codif.Syt[y][t]){
                    int l = lot.color;
                    if(lot.days - gcilt.inst.fullSetup(u, l, y) < 0.001){
                        penalty += gcilt.inst.fullSetup(u, l, y) - lot.days;
                    }
                    if(lot.days - gcilt.inst.STluy[u][l][y] < 0.001){
                        penalty += gcilt.inst.STluy[u][l][y] - lot.days;
                    }
                }
            }
        }
        */
        //-------------- penalities : end -------------
        
        for(int y=0; y<gcilt.inst.Y; y++){
            int u = gcilt.inst.ovenStartColor(y);
            int total_days = 0;
            for(int t=0; t<gcilt.inst.T && total_days<codif.Zy[y]; t++){
                for(GCILTLot lot : codif.Syt[y][t]){
                    if(total_days >= codif.Zy[y]){
                        break;
                    }
                    int l = lot.color;
                    int days = lot.days;
                    
                    if(total_days + days <= codif.Zy[y]){
                        total_days += days;
                    }else if(total_days + gcilt.inst.fullSetup(u, l, y) >= codif.Zy[y]){
                        codif.Zy[y] = total_days;   //repair codification
                        break;
                    }else{
                        days = codif.Zy[y]-total_days;
                        total_days = codif.Zy[y];
                    }
                    
                    if(days - gcilt.inst.fullSetup(u, l, y) < -0.001){
                        penalty += gcilt.inst.fullSetup(u, l, y) - days;
                    }
                    if(days - gcilt.inst.STluy[u][l][y] < -0.001){
                        penalty += gcilt.inst.STluy[u][l][y] - days;
                    }
                    
                    if(penalty < 0.001){
                        Blty_[l][t][y] += ( days - gcilt.inst.fullSetup(u, l, y) );

                        Clty_[l][t][y] += gcilt.inst.Cy[y]*( days - gcilt.inst.STluy[u][l][y] );

                        Tulyt[u][l][y][t] = 1;

                        ObjSwaping += gcilt.inst.SCluy[u][l][y];

                        for(int k : gcilt.inst.Ky[y]){
                            for(int i : gcilt.inst.Nl[l]){
                                Mikt[i][k][t] = gcilt.inst.Mikt(i, k, t);
                            }
                        }
                    }
                    
                    u = l;
                }
            }
        }
        if(penalty > 0.001){
            return penalty;
        }
        
        for(int u=0; u<gcilt.inst.L; u++){
            for(int l=0; l<gcilt.inst.L; l++){
                for(int t=0; t<gcilt.inst.T; t++){
                    for(int y=0; y<gcilt.inst.Y; y++){
                        for(int k : gcilt.inst.Ky[y]){
                            if(gcilt.inst.Lluy(u, l, y)>0.001){
                                Yultk[u][l][t][k].setBounds(Tulyt[u][l][y][t], Tulyt[u][l][y][t]);
                            }
                        }
                    }
                }
            }
        }
        
        
        for(int l=0; l<gcilt.inst.L; l++){
            for(int t=0; t<gcilt.inst.T; t++){
                for(int y=0; y<gcilt.inst.Y; y++){
                    for(int k : gcilt.inst.Ky[y]){
                        Bltk[l][t][k].setBounds(Blty_[l][t][y], Blty_[l][t][y]);
                    }
                }
            }
        }
        
        
        for(int l=0; l<gcilt.inst.L; l++){
            for(int t=0; t<gcilt.inst.T; t++){
                for(int y=0; y<gcilt.inst.Y; y++){
                    Clty[l][t][y].setBounds(0, Clty_[l][t][y]);
                }
            }
        }
        
        
        for(int l=0; l<gcilt.inst.L; l++){
            for(int t=0; t<gcilt.inst.T; t++){
                for(int y=0; y<gcilt.inst.Y; y++){
                    for(int k : gcilt.inst.Ky[y]){
                        for(int i : gcilt.inst.Nl[l]){
                            Xitk[i][t][k].setBounds(0, Mikt[i][k][t]);
                        }
                    }
                }
            }
        }
        
	return 0;
    }
    
    @Override
    public double execute(GCILTCodification codif) throws Exception {
	
        //Escrita do modelo em arquivo
        //cplex.exportModel("LTGCIP.lp");
        if(!print){
            cplex.setParam(IloCplex.DoubleParam.EpGap, 0.05);
        }
        cplex.setParam(IloCplex.IntParam.Threads, 1);
        cplex.setParam(IloCplex.DoubleParam.TiLim, 10);
        
        if (cplex.solve()) {
            if(print){
                print = false;
                codif.print(gcilt);
                System.out.println("Solution status     = " + cplex.getStatus());
                System.out.println("Solution value      = " + (cplex.getObjValue()+ObjSwaping));
                System.out.println("Solution Swaping    = " + ObjSwaping);
                System.out.println("Solution Transfer   = " + cplex.getValue(ObjTransfer));
                System.out.println("Solution Hold       = " + cplex.getValue(ObjHold));
                System.out.println("Solution OverHold   = " + cplex.getValue(ObjOverHold));


                double[][][] v_Xikt     = cplex.getValues(Xikt);
                double[][][][] v_Yuikt    = cplex.getValues(Yuikt);

                double[][][][] v_Wipvt  = cplex.getValues(Wipvt);
                double[][][] v_Iipt     = cplex.getValues(Iipt);
                double[][] v_I0ip       = cplex.getValues(I0ip);


                System.out.printf("--------------- Blyt: (y,l) x t ---------------------\n");
                for(int y=0; y<gcilt.inst.Y; y++){
                    for(int l=0; l<gcilt.inst.L; l++){
                        boolean teste = false;
                        for(int t=0; t<gcilt.inst.T; t++){
                            if(Blty_[l][t][y] > 0.001){
                               teste = true; 
                               break;
                            }
                        }
                        if(teste){
                            System.out.printf("    (%2d,%2d) | ", y, l);
                            for(int t=0; t<gcilt.inst.T; t++){
                                System.out.printf("%5d ", Blty_[l][t][y]);
                            }
                            System.out.printf("\n");
                        }
                    }
                }

                System.out.printf("--------------- Prod: (k,i) x T ---------------------\n");
                for(int y=0; y<gcilt.inst.Y; y++){
                    for(int k : gcilt.inst.Ky[y]){
                        for(int l=0; l<gcilt.inst.L; l++){
                            for(int i : gcilt.inst.Nl[l]){
                                boolean teste = false;
                                for(int t=0; t<gcilt.inst.T; t++){
                                    double flag = 0;
                                    for(int u=0; u<gcilt.inst.L; u++){
                                        if(v_Yuikt[u][i][k][t]>0.5){
                                            flag = gcilt.inst.Lluy(u,l,y);
                                        }
                                    }
                                    if(flag>0.001 || v_Xikt[i][k][t] > 0.001){
                                       teste = true; 
                                       break;
                                    }
                                }
                                if(teste){
                                    System.out.printf("    (%2d,%3d) | ", k, i);
                                    for(int t=0; t<gcilt.inst.T; t++){
                                        double flag = 0;
                                        for(int u=0; u<gcilt.inst.L; u++){
                                            if(v_Yuikt[u][i][k][t]>0.5){
                                                flag = gcilt.inst.Lluy(u,l,y);
                                            }
                                        }
                                        System.out.printf("[%5.0f + %3.0f]", v_Xikt[i][k][t]*gcilt.inst.Pik[i][k]*gcilt.inst.Ek[k], flag*gcilt.inst.Pik[i][k]*gcilt.inst.Ek[k]);
                                    }
                                    System.out.printf("\n");
                                }
                            }
                        }
                    }
                }



                System.out.printf("--------------- Xikt: (k,i) x T ---------------------\n");
                for(int k=0; k<gcilt.inst.K; k++){
                    for(int i=0; i<gcilt.inst.N; i++){
                        boolean teste = false;
                        for(int t=0; t<gcilt.inst.T; t++){
                            if(v_Xikt[i][k][t] > 0.001){
                               teste = true; 
                               break;
                            }
                        }
                        if(teste){
                            System.out.printf("    (%2d,%3d) | ", k, i);
                            for(int t=0; t<gcilt.inst.T; t++){
                                System.out.printf("%5.2f ", v_Xikt[i][k][t]);
                            }
                            System.out.printf("\n");
                        }
                    }
                }

                System.out.printf("--------------- Yuikt: (u,i,k) x T ---------------------\n");
                for(int y=0; y<gcilt.inst.Y; y++){
                    for(int k : gcilt.inst.Ky[y]){
                        for(int l=0; l<gcilt.inst.L; l++){
                            for(int i : gcilt.inst.Nl[l]){
                                for(int u=0; u<gcilt.inst.L; u++){
                                    boolean teste = false;
                                    for(int t=0; t<gcilt.inst.T; t++){
                                        if(v_Yuikt[u][i][k][t] > 0.001){
                                           teste = true; 
                                           break;
                                        }
                                    }
                                    if(teste){
                                        System.out.printf(" (%2d,%3d,%2d) | ", u, i, k);
                                        for(int t=0; t<gcilt.inst.T; t++){
                                            System.out.printf("%5.2f ", v_Yuikt[u][i][k][t]);
                                        }
                                        System.out.printf("\n");
                                    }

                                }

                            }
                        }
                    }
                }

                System.out.printf("--------------- I0ip: (i) x p ---------------------\n");
                System.out.printf("             | ");
                for(int p=0; p<gcilt.inst.P; p++){
                    System.out.printf("%12s ", p);
                }
                System.out.printf("\n");
                for(int i=0; i<gcilt.inst.N; i++){
                    boolean teste = false;
                    for(int p=0; p<gcilt.inst.P; p++){
                        if(v_I0ip[i][p] > 0.001){
                           teste = true; 
                           break;
                        }
                    }
                    if(teste){
                        System.out.printf("       (%3d) | ", i);
                        for(int p=0; p<gcilt.inst.P; p++){
                            System.out.printf("%12.2f ", v_I0ip[i][p]);
                        }
                        System.out.printf("\n");
                    }
                }


                System.out.printf("--------------- Iipt: (p,i) x t ---------------------\n");
                for(int p=0; p<gcilt.inst.P; p++){
                    for(int i=0; i<gcilt.inst.N; i++){
                        boolean teste = false;
                        for(int t=0; t<gcilt.inst.T; t++){
                            if(v_Iipt[i][p][t] > 0.001){
                               teste = true; 
                               break;
                            }
                        }
                        if(teste){
                            System.out.printf("    (%2d,%3d) | ", p, i);
                            for(int t=0; t<gcilt.inst.T; t++){
                                System.out.printf("%12.2f ", v_Iipt[i][p][t]);
                            }
                            System.out.printf("\n");
                        }
                    }
                }
                System.out.printf("--------------- Wipvt: (i,p,v) x t ---------------------\n");
                for(int i=0; i<gcilt.inst.N; i++){
                    for(int p=0; p<gcilt.inst.P; p++){
                        for(int v=0; v<gcilt.inst.P; v++){
                            boolean teste = false;
                            for(int t=0; t<gcilt.inst.T; t++){
                                if(v_Wipvt[i][p][v][t] > 0.001){
                                   teste = true; 
                                   break;
                                }
                            }
                            if(teste){
                                System.out.printf(" (%3d,%2d,%2d) | ", i, p, v);
                                for(int t=0; t<gcilt.inst.T; t++){
                                    System.out.printf("%12.2f ", v_Wipvt[i][p][v][t]);
                                }
                                System.out.printf("\n");
                            }
                        }
                    }
                }
            }
            return ObjSwaping + cplex.getObjValue();
        }
	return 1e11;
    }


}
