/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.GCILT.withZyt;

import ProOF.apl.pog.problem.GCILT.GCILTLot;
import ProOF.CplexExtended.CplexExtended;
import ProOF.utilities.uUtil;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;


/**
 *
 * @author marcio
 */
public final class iGCILTCplexNum extends aGCILTModel{
 
    private CplexExtended cplex;
    
    private IloNumVar Xikt[][][];
    private IloNumVar Iipt[][][];   
    private IloNumVar Wipvt[][][][]; 
    private IloNumVar Q0ip[][];     
    
    private IloRange RangeBlkt[][][];
    private IloRange RangeClty[][][];
    
    private IloNumExpr ObjHold;
    private IloNumExpr ObjTransfer;
    private IloNumExpr ObjOverHold;
    private IloNumExpr ObjValue;
    
    private double ObjSwaping = 0;
    
    private double Blyt[][][];
    private double Clyt[][][];
    private int Mikt[][][];
    /*private int Tulyt[][][][];
    private int Blty_[][][];
    private double Clty_[][][];
    private int Mikt[][][];
    */
    @Override
    public String name() {
        return "CplexNum";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    private boolean print = false;
    public void set(boolean print){
        this.print = print;
    }
    
    @Override
    public void start() throws Exception {
        cplex = new CplexExtended();
        
        
        Blyt = new double[gcilt.inst.L][gcilt.inst.Y][gcilt.inst.T];
        Clyt = new double[gcilt.inst.L][gcilt.inst.Y][gcilt.inst.T];
        Mikt  = new int[gcilt.inst.N][gcilt.inst.K][gcilt.inst.T];
        
        /*Tulyt = new int[gcilt.inst.L][gcilt.inst.L][gcilt.inst.Y][gcilt.inst.T];
        Blty_ = new int[gcilt.inst.L][gcilt.inst.T][gcilt.inst.Y];
        Clty_ = new double[gcilt.inst.L][gcilt.inst.T][gcilt.inst.Y];
        Mikt  = new int[gcilt.inst.N][gcilt.inst.K][gcilt.inst.T];
        */
        Xikt = cplex.numVarArray(gcilt.inst.N, gcilt.inst.K, gcilt.inst.T, 0, uUtil.maxInt(gcilt.inst.Qt), "X");
        Iipt = cplex.numVarArray(gcilt.inst.N, gcilt.inst.P, gcilt.inst.T, 0, Double.MAX_VALUE, "I");
        Wipvt = cplex.numVarArray(gcilt.inst.N, gcilt.inst.P, gcilt.inst.P, gcilt.inst.T, 0, Double.MAX_VALUE, "W"); 
        Q0ip = cplex.numVarArray(gcilt.inst.N, gcilt.inst.P, 0, Double.MAX_VALUE, "Io");
        
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
                Obj_ip[i][p] = cplex.prod(Q0ip[i][p], gcilt.inst.Pip[i][p]);
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
                            }
                        }
                        if(prod==null){
                            throw new Exception("prod==null");
                        }
                        
                        IloNumExpr hold_in;
                        if(t==0){
                            hold_in = cplex.prod(Q0ip[i][p], -1);
                        }else{
                            hold_in = cplex.prod(Iipt[i][p][t-1], -1);
                        }
                        if(transfer_in==null && transfer_out==null){
                            cplex.addEq(
                                cplex.sum(hold_out, hold_in, prod),
                                -gcilt.inst.Dipt[i][p][t],
                                "Estoque02("+i+","+p+","+t+")");
                        }else{
                            cplex.addEq(
                                cplex.sum(hold_out, transfer_out, hold_in, transfer_in, prod),
                                -gcilt.inst.Dipt[i][p][t],
                                "Estoque02("+i+","+p+","+t+")");
                        }
                    }
                }
            }
        }
        
        
        RangeBlkt = new IloRange[gcilt.inst.L][gcilt.inst.K][gcilt.inst.T];
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
                        RangeBlkt[l][k][t] = cplex.addEq(sum, gcilt.inst.Qt[t],
                            "Subject03("+l+","+y+","+t+")");
                    }
                }
            }
        }
        
        RangeClty = new IloRange[gcilt.inst.L][gcilt.inst.T][gcilt.inst.Y];
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
                        }
                    }
                    RangeClty[l][t][y] = cplex.addLe(sum, gcilt.inst.Cy[y]*gcilt.inst.Qt[t],
                        "Subject04("+l+","+y+","+t+")");
                }
            }
        }
    }
    
    @Override
    public double initialize(GCILTCodification codif) throws Exception {
        double penalty = 0;
        
        ObjSwaping = 0;
        clear(Blyt);
        clear(Clyt);
        clear(Mikt);
        
        
        for(int y=0; y<gcilt.inst.Y; y++){
            int u = gcilt.inst.ovenStartColor(y);
            for(int t=0; t<gcilt.inst.T; t++){
                for(GCILTLot lot : codif.Syt[y][t]){
                    int l = lot.color;
                    
                    if(lot.days - gcilt.inst.STluy[u][l][y] < -0.001){
                        penalty += gcilt.inst.STluy[u][l][y] - lot.days;
                    }
                    Blyt[l][y][t] += ( lot.days - gcilt.inst.STluy[u][l][y] );
                    
                    if(lot.days - gcilt.inst.STluy[u][l][y] < -0.001){
                        penalty += gcilt.inst.Cy[y]*( gcilt.inst.STluy[u][l][y] - lot.days );
                    }
                    Clyt[l][y][t] += gcilt.inst.Cy[y]*( lot.days - gcilt.inst.STluy[u][l][y] );
                    
                    ObjSwaping += gcilt.inst.SCluy[u][l][y];
                    
                    for(int k : gcilt.inst.Ky[y]){
                        for(int i : gcilt.inst.Nl[l]){
                            Mikt[i][k][t] = gcilt.inst.Mikt(i, k, t);
                        }
                    }
                    
                    u = l;
                }
            }
        }
        
        if(penalty > 0.001){
            return penalty;
        }
        //private IloRange Yultk[][][][];
        //private IloRange Bltk[][][];
        //private IloRange Clty[][][];
        //private IloRange Xitk[][][];
        
        
        for(int l=0; l<gcilt.inst.L; l++){
            for(int t=0; t<gcilt.inst.T; t++){
                for(int y=0; y<gcilt.inst.Y; y++){
                    for(int k : gcilt.inst.Ky[y]){
                        RangeBlkt[l][k][t].setBounds(Blyt[l][y][t], Blyt[l][y][t]);
                    }
                }
            }
        }
        
        
        for(int l=0; l<gcilt.inst.L; l++){
            for(int t=0; t<gcilt.inst.T; t++){
                for(int y=0; y<gcilt.inst.Y; y++){
                    RangeClty[l][t][y].setBounds(0, Clyt[l][y][t]);
                }
            }
        }
        
        for(int l=0; l<gcilt.inst.L; l++){
            for(int t=0; t<gcilt.inst.T; t++){
                for(int y=0; y<gcilt.inst.Y; y++){
                    for(int k : gcilt.inst.Ky[y]){
                        for(int i : gcilt.inst.Nl[l]){
                            Xikt[i][k][t].setUB(Mikt[i][k][t]);
                        }
                    }
                }
            }
        }
        
        for (int y = 0; y < gcilt.inst.Y; y++) {
            System.out.printf(" y%d -> ", y);
            for(int t = 0; t < gcilt.inst.T; t++){
                for(GCILTLot lot : codif.Syt[y][t]){
                    System.out.printf("[ %2d %2d ]", lot.color, lot.days);
                }
                System.out.printf(" | ");
            }
            System.out.println();
        }
	
	return 0;
    }
    
    @Override
    public double execute(GCILTCodification codif) throws Exception {
	
        //Escrita do modelo em arquivo
        //cplex.exportModel("LTGCIP.lp");
        
        if (cplex.solve()) {
            
            cplex.output().println("Solution status     = " + cplex.getStatus());
            cplex.output().println("Solution value      = " + (cplex.getObjValue()+ObjSwaping));
            cplex.output().println("Solution Swaping    = " + ObjSwaping);
            cplex.output().println("Solution Transfer   = " + cplex.getValue(ObjTransfer));
            cplex.output().println("Solution Hold       = " + cplex.getValue(ObjHold));
            cplex.output().println("Solution OverHold   = " + cplex.getValue(ObjOverHold));
            

            double[][][] v_Xikt     = cplex.getValues(Xikt);
            double[][][][] v_Wipvt  = cplex.getValues(Wipvt);
            double[][][] v_Iipt     = cplex.getValues(Iipt);
            double[][] v_I0ip       = cplex.getValues(Q0ip);
            

            cplex.output().printf("--------------- Blyt: (y,l) x t ---------------------\n");
            for(int y=0; y<gcilt.inst.Y; y++){
                for(int l=0; l<gcilt.inst.L; l++){
                    boolean teste = false;
                    for(int t=0; t<gcilt.inst.T; t++){
                        if(Blyt[l][y][t] > 0.001){
                           teste = true; 
                           break;
                        }
                    }
                    if(teste){
                        cplex.output().printf("    (%2d,%2d) | ", y, l);
                        for(int t=0; t<gcilt.inst.T; t++){
                            cplex.output().printf("%5.2f ", Blyt[l][y][t]);
                        }
                        cplex.output().printf("\n");
                    }
                }
            }

                       
            cplex.output().printf("--------------- Xikt: (k,i) x T ---------------------\n");
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
                        cplex.output().printf("    (%2d,%3d) | ", k, i);
                        for(int t=0; t<gcilt.inst.T; t++){
                            cplex.output().printf("%5.2f ", v_Xikt[i][k][t]);
                        }
                        cplex.output().printf("\n");
                    }
                }
            }
           
            cplex.output().printf("--------------- I0ip: (i) x p ---------------------\n");
            cplex.output().printf("             | ");
            for(int p=0; p<gcilt.inst.P; p++){
                cplex.output().printf("%12s ", p);
            }
            cplex.output().printf("\n");
            for(int i=0; i<gcilt.inst.N; i++){
                boolean teste = false;
                for(int p=0; p<gcilt.inst.P; p++){
                    if(v_I0ip[i][p] > 0.001){
                       teste = true; 
                       break;
                    }
                }
                if(teste){
                    cplex.output().printf("       (%3d) | ", i);
                    for(int p=0; p<gcilt.inst.P; p++){
                        cplex.output().printf("%12.2f ", v_I0ip[i][p]);
                    }
                    cplex.output().printf("\n");
                }
            }
 
            
            cplex.output().printf("--------------- Iipt: (p,i) x t ---------------------\n");
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
                        cplex.output().printf("    (%2d,%3d) | ", p, i);
                        for(int t=0; t<gcilt.inst.T; t++){
                            cplex.output().printf("%12.2f ", v_Iipt[i][p][t]);
                        }
                        cplex.output().printf("\n");
                    }
                }
            }
            cplex.output().printf("--------------- Wipvt: (i,p,v) x t ---------------------\n");
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
                            cplex.output().printf(" (%3d,%2d,%2d) | ", i, p, v);
                            for(int t=0; t<gcilt.inst.T; t++){
                                cplex.output().printf("%12.2f ", v_Wipvt[i][p][v][t]);
                            }
                            cplex.output().printf("\n");
                        }
                    }
                }
            }
            
            
            
            return ObjSwaping + cplex.getObjValue();
        }
	return 1e11;
    }


}
