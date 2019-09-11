/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.RA.Codif2.TSP;

import ProOF.CplexExtended.CplexExtended;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex.UnknownObjectException;
import java.util.ArrayList;

/**
 *
 * @author marcio
 */
public class iRATSPModel extends aRATSP{
    
    public CplexExtended cplex;
    private IloIntVar Aj[];
    private IloIntVar Bj[];
    private IloIntVar Xij[][];
    private IloNumVar Ui[];
    
    private IloRange Col_j[];
    private IloRange Row_i[];
    private IloRange Sum_X;
            
    @Override
    public String name() {
        return "RA-TSP-Model";
    }

    @Override
    public void start() throws Exception {
        super.start();
        
        cplex = new CplexExtended();
        
        cplex.setOut(null);
        cplex.setWarning(null);
        
        Aj  = cplex.boolVarArray(inst.N, "Aj");
        Bj  = cplex.boolVarArray(inst.N, "Bj");
        Xij = cplex.boolVarArray(inst.N, inst.N, "Xij");
        Ui  = cplex.numVarArray(inst.N-1, 0, inst.N*2, "Ui");
        
        for(int i=0; i<inst.N; i++){
            Xij[i][i].setMin(0);
            Xij[i][i].setMax(0);
        }
        
        IloNumExpr sum = null;
        for(int i=0; i<inst.N; i++){
            for(int j=0; j<inst.N; j++){
                if(sum==null){
                    sum = cplex.prod(Xij[i][j], inst.STij[i][j]);
                }else{
                    sum = cplex.sum(sum, cplex.prod(Xij[i][j], inst.STij[i][j]));
                }
            }
        }
        cplex.addMinimize(sum);
        
        sum = null;
        for(int i=0; i<inst.N; i++){
            if(sum==null){
                sum = cplex.sum(Aj[i], Bj[i]);
            }else{
                sum = cplex.sum(sum, Aj[i], Bj[i]);
            }
            for(int j=0; j<inst.N; j++){
                if(sum==null){
                    sum = Xij[i][j];
                }else{
                    sum = cplex.sum(sum, Xij[i][j]);
                }
            }
        }
        Sum_X = cplex.addRange(0, sum, inst.N, "SumX");
        
        Col_j = new IloRange[inst.N];
        for(int j=0; j<inst.N; j++){
            sum = Aj[j];
            for(int i=0; i<inst.N; i++){
                if(sum==null){
                    sum = Xij[i][j];
                }else{
                    sum = cplex.sum(sum, Xij[i][j]);
                }
            }
            Col_j[j] = cplex.addRange(1, sum, 1, "Col["+(j+1)+"]");
        }
        
        Row_i = new IloRange[inst.N];
        for(int i=0; i<inst.N; i++){
            sum = Bj[i];
            for(int j=0; j<inst.N; j++){
                if(sum==null){
                    sum = Xij[i][j];
                }else{
                    sum = cplex.sum(sum, Xij[i][j]);
                }
            }
            Row_i[i] = cplex.addRange(1, sum, 1, "Row["+(i+1)+"]");
        }
        sum = null;
        for(int j=0; j<inst.N; j++){
            if(sum==null){
                sum = Aj[j];
            }else{
                sum = cplex.sum(sum, Aj[j]);
            }
        }
        cplex.addEq(sum, 1, "Aj");
        
        sum = null;
        for(int j=0; j<inst.N; j++){
            if(sum==null){
                sum = Bj[j];
            }else{
                sum = cplex.sum(sum, Bj[j]);
            }
        }
        cplex.addEq(sum, 1, "Bj");
        
        for(int i=0; i<inst.N-1; i++){
            for(int j=0; j<inst.N-1; j++){
                if(i!=j){
                    IloNumExpr aux[] = new IloNumExpr[3];
                    aux[0] = cplex.prod(Ui[i], +1);
                    aux[1] = cplex.prod(Ui[j], -1);
                    aux[2] = cplex.prod(Xij[i][j], inst.N);

                    cplex.addLe(cplex.sum(aux), inst.N-1);
                }
            }
        }
        
    }
    
    private int init(int[][] Wit, int t) throws IloException{
        int cout = 0;
        for(int i=0; i<inst.N; i++){
            if(Wit[i][t]>0){
                cout++;
                Aj[i].setMax(1);
                Bj[i].setMax(1);
                
                Row_i[i].setBounds(1, 1);
                Col_j[i].setBounds(1, 1);
            }else{
                Aj[i].setMax(0);
                Bj[i].setMax(0);
                
                Row_i[i].setBounds(0, 0);
                Col_j[i].setBounds(0, 0);
            }
        }
        //cout = cout<=0? 1 : cout; 
        Sum_X.setBounds(cout+1, cout+1);
        return cout;
    }
    
    @Override
    public ArrayList<Integer>[] solve(int[][] Wit) throws Exception {
        //System.out.println("model");
        ArrayList<Integer> Yt[] = new ArrayList[inst.T];
        double custo_final = 0;
        for(int t=0; t<inst.T; t++){
            /*System.out.printf("Wit [%d] -- > [ ", t);
            for(int i=0; i<inst.N; i++){
                System.out.printf("%s ", Wit[i][t]==0?"-":"*");
            }
            System.out.println();*/

            int cout = init(Wit, t);
            if(cout==0){
                Yt[t] = new ArrayList<Integer>();
                //System.out.println("List ["+t+"] -- > "+ Yt[t]);
            }else{
                //cplex.exportModel("RATSP_model.lp");
                if(cplex.solve()){
                    
                    double custo_total = cplex.getObjValue();
                    
                    Yt[t] = new ArrayList<Integer>();
                    rec(Yt[t], -1);
                    
                    if(custo_total>0.1){
                        custo_final += custo_total;
                        System.out.printf("List [%d] = %8g -- > %s\n",t, custo_total, Yt[t]);
                    }
                    
                    /*System.out.println("List ["+t+"] -- > "+ Yt[t]);
                    for(int i=0; i<inst.N; i++){
                        for(int j=0; j<inst.N; j++){
                            double val = cplex.getValue(Xij[i][j]);
                            System.out.printf("%s ", val>0.5?"*":".");
                        }
                        System.out.println();
                    }*/
                    /*double sum = cplex.getValue(Sum_X);
                    if(Math.abs(Yt[t].size()-sum)<0.01){
                        cplex.exportModel("RATSP_sum_error.lp");
                        throw new Exception("cplex sum error");
                    }*/
                }else{
                    cplex.exportModel("RATSP_not_solved.lp");
                    throw new Exception("cplex not solve");
                }
                //System.exit(1);
            }
        }
        if(custo_final>0.1){
            System.out.printf("custo final -- > %8g\n",custo_final);
        }
        return Yt;
    }

    private void rec(ArrayList<Integer> list, int i) throws UnknownObjectException, IloException {
        if(list.isEmpty()){
            for(int j=0; j<inst.N; j++){
                double val = cplex.getValue(Aj[j]);
                if(val>0.5){
                    list.add(j);
                    rec(list, j);
                }
            }
        }else{
            for(int j=0; j<inst.N; j++){
                double val = cplex.getValue(Xij[i][j]);
                if(val>0.5){
                    list.add(j);
                    rec(list, j);
                }
            }
        }
    }
}
