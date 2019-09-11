/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.method.CustomizedApproach;

import static ProOF.apl.pog.method.CustomizedApproach.AddRestrictions.ID_RELAXATION;
import static ProOF.apl.pog.method.CustomizedApproach.AddRestrictions.ID_UPERBOUND;
import static ProOF.apl.pog.method.CustomizedApproach.AddRestrictions.ID_VARIABLE;
import ProOF.apl.pog.method.CustomizedApproach.Obstacle.REST_empty;
import static ProOF.apl.pog.problem.PPDCP.PPDCPInstance.REST_empty;
import ProOF.com.Linker.LinkerResults;
import ProOF.CplexExtended.CplexExtended;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;



/**
 *
 * @author marcio
 */
public class CAIncremental_v1 extends Abstraction {
    private double upper;
    private double lower;
    private double frr_per_chek;
    private double frt_per_chek;
    
    private Model frr;
    private Model frt;
    private Model fra1;
    private Model fra2;
    
    private boolean chek[][];
    
    @Override
    public String name() {
        return "C&A Incremental_v1";
    }

    @Override
    public void start() throws Exception {
        super.start(); //To change body of generated methods, choose Tools | Templates.
        chek = new boolean[inst.J][inst.T + 1];
    }
    
    private boolean isFeasible(Model model) throws IloException, Exception{
        
        int [][][] bZjti = model.addRest.getZjti(model.cplex, model.MUt, null);
        
        double alfa[][][][] = new double[inst.J][][][];
        for (int j = 0; j < inst.J; j++) {
           alfa[j] = new double[inst.T + 1][inst.G(j)][inst.G(j)];
           for (int i = 0; i < inst.G(j); i++) {
               for (int l = 0; l < inst.G(j); l++) {
                   if (l != i) {
//                       alfa[j][0][i][l]  =  Math.max( 0 , model.cplex.getValue(Zjti[j][0][i]) + model.cplex.getValue(Zjti[j][0][l]) - 1);
//                       for (int t = 1; t < inst.T + 1; t++) {
//                           
//                           alfa[j][t][i][l]  =  Math.max( 0 , 
//                                   Math.abs(model.cplex.getValue(Zjti[j][t][i])-model.cplex.getValue(Zjti[j][t - 1][i])) +
//                                   Math.abs(model.cplex.getValue(Zjti[j][t][l])-model.cplex.getValue(Zjti[j][t - 1][l])) - 1);
//                       }
                       alfa[j][0][i][l]  =  Math.max( 0 , bZjti[j][0][i] + bZjti[j][0][l] - 1);
                       for (int t = 1; t < inst.T + 1; t++) {
                           
                           alfa[j][t][i][l]  =  Math.max( 0 , 
                                   Math.abs(bZjti[j][t][i]-bZjti[j][t - 1][i]) +
                                   Math.abs(bZjti[j][t][l]-bZjti[j][t - 1][l]) - 1);
                       }
                   } else {
                       for (int t = 0; t < inst.T + 1; t++) {
                           alfa[j][t][i][l] = 0;
                       }
                   }
               }
           }
        }
        double sumZjti[][] = new double[inst.J][inst.T + 1];
        double sumAjtil[][] = new double[inst.J][inst.T + 1];
        for (int j = 0; j < inst.J; j++) {
            for (int t = 1; t < inst.T + 1; t++) {
                sumZjti[j][t] = 0;
                sumAjtil[j][t] = 1;
                for (int i = 0; i < inst.G(j); i++) {
                    //sumZjti[j][t] += model.cplex.getValue(Zjti[j][t][i]);
                    sumZjti[j][t] += bZjti[j][t][i];
                    for (int l = 0; l < inst.G(j); l++) {
                        sumAjtil[j][t] += alfa[j][t][i][l];
                    }
                }
            }
        }
        for (int j = 0; j < inst.J; j++) {
            for (int t = 1; t < inst.T + 1; t++) {
                if(sumZjti[j][t] + 0.9 < sumAjtil[j][t]){
                    System.out.printf("infeasible by j = %2d, t = %2d, sumZ = %8g , sumA = %8g\n",(j+1),t, sumZjti[j][t], sumAjtil[j][t]);
                    System.out.printf("j=%2d t=%2d [ ", j+1, t-1);
                    for (int i = 0; i < inst.G(j); i++) {
                        //System.out.printf("%8g ", model.cplex.getValue(Zjti[j][t-1][i]));
                        System.out.printf("%8d ", bZjti[j][t-1][i]);
                    }
                    System.out.println("]");
                    System.out.printf("j=%2d t=%2d [ ", j+1, t);
                    for (int i = 0; i < inst.G(j); i++) {
                        //System.out.printf("%8g ", model.cplex.getValue(Zjti[j][t][i]));
                        System.out.printf("%8d ", bZjti[j][t][i]);
                    }
                    System.out.println("]");
                }
            }
        }
        IloNumVar[][][] Zjti = model.addRest.getZjti();
        boolean feasible = true;
        for (int j = 0; j < inst.J; j++) {
            for (int t = 1; t < inst.T + 1; t++) {
                if(sumZjti[j][t] + 0.9 < sumAjtil[j][t]){
                    if(!chek[j][t]){
                        feasible = false;
                        addRestrictions(model.cplex, Zjti, j, t);
                        chek[j][t] = true;
                        
                        final int N = 0;
                        for(int s=t+1; s<=t+N && s<inst.T+1; s++){
                            addRestrictions(model.cplex, Zjti, j, s);
                            chek[j][s] = true;
                        }
                        for(int s=t-1; s>=t-N && s>=1; s--){
                            addRestrictions(model.cplex, Zjti, j, s);
                            chek[j][s] = true;
                        }
                    }else{
                        System.out.println("warnning: chek status faill in j = "+(j+1)+" , t = "+t);
                        //throw new IloException("chek status faill in j = "+(j+1)+" , t = "+t);
                    }
                }
            }
        }
        return feasible;
    }
    private double percentage_chek(){
        double sum = 0;
        for (int j = 0; j < inst.J; j++) {
            for (int t = 1; t < inst.T + 1; t++) {
                sum += chek[j][t] ? 1 : 0;
            }
        }
        return sum * 100.0/ (inst.J * inst.T);
    }
    private void clear(boolean val[][]){
        for (int j = 0; j < inst.J; j++) {
            for (int t = 0; t < inst.T + 1; t++) {
                val[j][t] = false;
            }
        }
    }
    private void addRestrictions(CplexExtended cplex, IloNumVar[][][] Zjti, int j, int t) throws IloException{
        System.out.printf("add Restriction in j=%2d t=%2d \n", j+1, t);
        
        IloNumVar alfa[][] = new IloNumVar[inst.G(j)][inst.G(j)];
        for (int i = 0; i < inst.G(j); i++) {
            for (int l = 0; l < inst.G(j); l++) {
                //Cria variável
                alfa[i][l] = cplex.numVar(0, 1, "alfa("+(j+1+","+(t+1)+","+(i+1)+","+(l+1))+")");
            }
        }
        for (int i = 0; i < inst.G(j); i++) {
            for (int l = 0; l < inst.G(j); l++) {
                if (l != i) {
                    if(t==0){
                        IloNumExpr exp0 = cplex.sum(cplex.prod(alfa[i][l], -1), Zjti[j][0][i], Zjti[j][0][l]);
                        cplex.addLe(exp0, 1, "NEWexp1." + (j + 1) + "," + (i + 1) + "," + (l + 1) + "," + 1);
                    }else{
                        IloNumExpr exp1 = cplex.sum(cplex.prod(alfa[i][l], -1), Zjti[j][t][i], cplex.prod(Zjti[j][t - 1][i], -1), Zjti[j][t][l], cplex.prod(Zjti[j][t - 1][l], -1));
                        cplex.addLe(exp1, 1, "NEWexp1." + (j + 1) + "," + (i + 1) + "," + (l + 1) + "," + (t + 1));

                        IloNumExpr exp2 = cplex.sum(cplex.prod(alfa[i][l], -1), Zjti[j][t][i], cplex.prod(Zjti[j][t - 1][i], -1), cplex.prod(Zjti[j][t][l], -1), Zjti[j][t - 1][l]);
                        cplex.addLe(exp2, 1, "NEWexp2." + (j + 1) + "," + (i + 1) + "," + (l + 1) + "," + (t + 1));

                        IloNumExpr exp3 = cplex.sum(cplex.prod(alfa[i][l], -1), cplex.prod(Zjti[j][t][i], -1), Zjti[j][t - 1][i], Zjti[j][t][l], cplex.prod(Zjti[j][t - 1][l], -1));
                        cplex.addLe(exp3, 1, "NEWexp3." + (j + 1) + "," + (i + 1) + "," + (l + 1) + "," + (t + 1));

                        IloNumExpr exp4 = cplex.sum(cplex.prod(alfa[i][l], -1), cplex.prod(Zjti[j][t][i], -1), Zjti[j][t - 1][i], cplex.prod(Zjti[j][t][l], -1), Zjti[j][t - 1][l]);
                        cplex.addLe(exp4, 1, "NEWexp4." + (j + 1) + "," + (i + 1) + "," + (l + 1) + "," + (t + 1));

                    }
                } else {
                    alfa[i][l].setUB(0);
                }
            }
        }
        //System.out.println("inst.ALPHA = "+inst.ALPHA);
        IloNumExpr exp = null;
        for (int i = 0; i < inst.G(j); i++) {
            exp = cplex.SumProd(exp, 1, Zjti[j][t][i]);
            IloNumExpr exp2 = null;
            for (int l = 0; l < inst.G(j); l++) {
                exp2 = cplex.SumProd(exp2, inst.ALPHA, alfa[i][l]);
            }
            exp = cplex.sum(exp, cplex.prod(exp2, -1));
        }
        cplex.addGe(exp, 1, "NEW2." + (j + 1) + "," + (t + 1));
    }
    @Override
    public void execute() throws Exception {
        time = System.currentTimeMillis();
        upper = Integer.MAX_VALUE;
        lower = Integer.MIN_VALUE;
        rote = null;
        status = "ok";
        frr = new Model("FRR", inst, selectOBJ(), ID_RELAXATION, new REST_empty(inst), null);
        
        int inter = 0;
        do{
            frr_per_chek = percentage_chek();
            System.out.println("FRR-Infeasible - "+inter);
            System.out.println("FRR-lower           = "+lower);
            System.out.println("FRR-percentage_chek = "+frr_per_chek);
            inter++;
            frr.execute(execTime, epGap, threads);
            if(frr.isFeasible()){
                lower = Math.max(lower, frr.lower());
            }else{
                break;
            }
        }while(!isFeasible(frr));
        
        if(frr.isFeasible()){
            System.out.println("FRR-Feasible - "+inter+" = Ok");
            fra1 = new Model("FRA1", inst, selectOBJ(), ID_VARIABLE, new REST_empty(inst), frr);
            fra1.execute(execTime, epGap, threads);
            if (fra1.isFeasible()) {
                upper = fra1.upper();
                rote = fra1.rote();
            } else {
                clear(chek);
                frt = new Model("FRT", inst, selectOBJ(), ID_UPERBOUND, new REST_empty(inst), null);
                inter = 0;
                double lower2 = Integer.MIN_VALUE;
                do{
                    frt_per_chek = percentage_chek();
                    System.out.println("FRT-Infeasible - "+inter);
                    System.out.println("FRT-lower           = "+lower2);
                    System.out.println("FRT-percentage_chek = "+frt_per_chek);
                    inter++;
                    frt.execute(execTime, epGap, threads);
                    if(frt.isFeasible()){
                        lower2 = Math.max(lower2, frt.lower());
                    }else{
                        break;
                    }
                }while(!isFeasible(frt));
                
                if(frt.isFeasible()){
                    System.out.println("FRT-Feasible - "+inter+" = Ok");
                    upper = frt.upper();
                    fra2 = new Model("FRA2", inst, selectOBJ(), ID_VARIABLE, new REST_empty(inst), frt);
                    fra2.execute(execTime, epGap, threads);
                    if (fra2.isFeasible()) {
                        upper = fra2.upper();
                        rote = fra2.rote();
                    } else {
                        status = "not solved[3]";
                        System.err.println("Problem not solved [3]");
                        upper = Integer.MAX_VALUE;
                        //JOptionPane.showMessageDialog(null, "not solved [3]", "nontrivial cost bounds", JOptionPane.INFORMATION_MESSAGE);
                    }
                }else{
                    upper = Integer.MAX_VALUE;
                    status = "not solved[2]";
                    System.err.println("Problem not solved [2]");
                }
            }
        }else{
            lower = Integer.MAX_VALUE;
            status = "not solved[1]";
            System.err.println("Problem not solved [1]");
        }

        time = System.currentTimeMillis() - time;
    }

    

    @Override
    public void results(LinkerResults link) throws Exception {
        link.writeString("CA-status", status);
        link.writeDbl("CA-upper", upper);
        link.writeDbl("CA-lower", lower);
        link.writeDbl("CA-time", time/1000.0);
        link.writeDbl("CA-frr-chek", frr_per_chek);
        link.writeDbl("CA-frt-chek", frt_per_chek);
        
        if (frr != null && frr.isFeasible()) {
            frr.results(link);
        }
        if (frt != null && frt.isFeasible()) {
            frt.results(link);
        }
        if (fra1 != null && fra1.isFeasible()) {
            fra1.results(link);
        }
        if (fra2 != null && fra2.isFeasible()) {
            fra2.results(link);
        }
    }
}
