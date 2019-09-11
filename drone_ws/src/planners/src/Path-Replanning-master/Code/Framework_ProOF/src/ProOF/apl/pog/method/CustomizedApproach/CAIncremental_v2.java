/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.method.CustomizedApproach;

import static ProOF.apl.pog.method.CustomizedApproach.AddRestrictions.ID_RELAXATION;
import static ProOF.apl.pog.method.CustomizedApproach.AddRestrictions.ID_UPERBOUND;
import static ProOF.apl.pog.method.CustomizedApproach.AddRestrictions.ID_VARIABLE;
import ProOF.apl.pog.method.CustomizedApproach.Obstacle.REST_empty;
import ProOF.com.Linker.LinkerResults;
import ProOF.CplexExtended.CplexExtended;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import jsc.distributions.Normal;



/**
 *
 * @author marcio
 */
public class CAIncremental_v2 extends Abstraction {
    private double upper;
    private double lower;
    private double frr_per_obst;
    private double frr_per_chek;
    private double frt_per_obst;
    private double frt_per_chek;
    
    private Model frr;
    private Model frt;
    private Model fra1;
    private Model fra2;
    
    private boolean obst[][];
    private boolean chek[][];
    
    @Override
    public String name() {
        return "C&A Incremental_v2";
    }

    @Override
    public void start() throws Exception {
        super.start(); //To change body of generated methods, choose Tools | Templates.
        chek = new boolean[inst.J][inst.T + 1];
        obst = new boolean[inst.J][inst.T + 1];
    }
//    private void ObstacleAvoidanceFix(ProOFCplex cplex, IloNumVar[][] MUt, IloIntVar[][][] Zjti, final double Djt) throws IloException{
//        for(int j=0; j<inst.J; j++){
//            for(int t=0; t<inst.T+1; t++){
//                if(obst[j][t]){
//                    
//                }
//            }
//        }
//    }
    private void addObstacleAvoidanceFix(CplexExtended cplex, IloNumVar[][] MUt, IloNumVar[][][] Zjti, final double Djt, int j, int t) throws IloException{
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
//    private void fix_Zjti(IloIntVar[][][] Zjti, int Rjti[][][]) throws IloException{
//        for(int j=0; j<inst.J; j++){
//            for(int t=0; t<inst.T+1; t++){
//                if(obst[j][t]){
//                    for(int i=0; i<inst.G(j); i++){
//                        Zjti[j][t][i].setMax(Rjti[j][t][i]);
//                        Zjti[j][t][i].setMin(Rjti[j][t][i]);
//                    }
//                }
//            }
//        }
//    }
    
//    public class REST_empty extends Shared implements AddRestrictions<REST_empty> {
//        private IloIntVar[][][] Zjti = null;
//        private IloNumVar[][] Djt = null;
//        private int ID = -1;
//
//        public REST_empty(PPDCPInstance inst) {
//            super(inst);
//        }
//        
//        @Override
//        public void fix(ProOFCplex cplex, REST_empty tofix) throws IloException{
//            System.out.println("REST_empty fix Zjti");
//            fix_Zjti(Zjti, tofix.Zjti(cplex, tofix.Zjti));
//        }
//        @Override
//        public IloNumExpr addRestrictions(ProOFCplex cplex, IloNumVar[][] Ut, IloNumVar[][] MUt, int ID, boolean tofix) throws Exception {
//            this.ID = ID;
//            Zjti = create_Zjti(cplex);
//            if(tofix){
//
//            }else{
//                addSumZjti_1(cplex, Zjti);
//            }
//
//            IloNumExpr obj = null;
//            switch(ID){
//                case ID_RELAXATION: ObstacleAvoidanceFix(cplex, MUt, Zjti, inst.DELTA); break;
//                case ID_UPERBOUND:  ObstacleAvoidanceFix(cplex, MUt, Zjti, inst.DELTA / (inst.J * (inst.T + 1))); break;
//                case ID_VARIABLE:
//                    Djt = create_Djt(cplex, inst.DN);
//                    obj = ObstacleAvoidanceFree(cplex, MUt, Zjti, Djt, inst.DN);
//                    addDelta(cplex, Djt, null, null, null);
//                    break;
//                default: throw new Exception("Optiom ID = "+ID+", not defined");
//            }
//            return obj;
//        }
//    
//        
//        @Override
//        public IloIntVar[][][] getZjti() throws IloException {
//            return Zjti;
//        }
//        @Override
//        public int [][][] getZjti(ProOFCplex cplex, IloNumVar[][] MUt, boolean obst[][]) throws Exception{
//            int [][][] bZjti = new int[inst.J][inst.T+1][];
//            for(int j=0; j<inst.J; j++){
//                for(int t=0; t<inst.T+1; t++){
//                    bZjti[j][t] = new int[inst.G(j)];
//                }
//            }
//            switch(ID){
//                case ID_RELAXATION: ObstacleAvoidanceFixZjti(cplex, MUt, bZjti, obst, inst.DELTA); break;
//                case ID_UPERBOUND:  ObstacleAvoidanceFixZjti(cplex, MUt, bZjti, obst, inst.DELTA / (inst.J * (inst.T + 1))); break;
//                case ID_VARIABLE:   ObstacleAvoidanceFreeZjti(cplex, MUt, bZjti, obst); break;
//                default: throw new Exception("Optiom ID = "+ID+", not defined");
//            }
//            return bZjti;
//        }
//        
//        @Override
//        public LinkedList<IloNumVar> AlocationFree(int t){
//            if(ID == ID_RELAXATION || ID == ID_UPERBOUND){
//                return null;
//            }
//            LinkedList<IloNumVar> list = new LinkedList<IloNumVar>();
//            for(int j=0; j<inst.J; j++){
//                list.addLast(Djt[j][t]);
//            }
//            return list;
//        }
//        @Override
//        public double AlocationFix(){
//            switch(ID){
//                case ID_RELAXATION: return inst.DELTA;
//                case ID_UPERBOUND:  return inst.DELTA / (inst.J * (inst.T + 1));
//                default: return inst.DELTA/Math.pow(2, inst.DN);
//            }
//        }
//    
//    }
//    
    private boolean isFeasible(Model model) throws IloException, Exception{
        
        int [][][] bZjti = model.addRest.getZjti(model.cplex, model.MUt, null);
        
        double Mt[][] = new double[inst.T+1][4];
        for (int t = 0; t < inst.T + 1; t++) {
            for(int i=0; i<4; i++){
                Mt[t][i] = model.cplex.getValue(model.MUt[t][i]);
            }
        }
        
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
                double max = -Integer.MAX_VALUE;
                for(int i=0; i<inst.Ob[j].length(); i++){
                    double exp = 
                            inst.Ob[j].lines[i].ax*Mt[t][0] + 
                            inst.Ob[j].lines[i].ay*Mt[t][2];
                    
                    exp -= inst.Ob[j].lines[i].b;
                    exp = exp/inst.Rjti[j][t][i];
                    max = Math.max(max, exp);
                }
                double delta = (1-Normal.standardTailProb(max, false))/2;
                if(delta <= model.addRest.AlocationFix()+1e-6 ){
                    //Esta fora do obstaculo
                }else if(!obst[j][t]){
                    //Esta colidindo com o obstaculo
                    feasible = addObstacle(feasible, model, Zjti, j, t, "Obstacle1");
                    feasible = addRestriction(feasible, model, Zjti, j, t);
                }
            }
        }
        if(feasible){
            for (int j = 0; j < inst.J; j++) {
                for (int t = 1; t < inst.T + 1; t++) {
                    double max = -Integer.MAX_VALUE;
                    for(int i=0; i<inst.Ob[j].length(); i++){
                        double exp = 
                                inst.Ob[j].lines[i].ax*Mt[t][0] + 
                                inst.Ob[j].lines[i].ay*Mt[t][2];

                        exp -= inst.Ob[j].lines[i].b;
                        exp = exp/inst.Rjti[j][t][i];
                        max = Math.max(max, exp);
                    }
                    double delta = (1-Normal.standardTailProb(max, false))/2;
                    if(delta <= model.addRest.AlocationFix()+1e-6 ){
                        //Esta fora do obstaculo
                        if(sumZjti[j][t] + 0.9 < sumAjtil[j][t]){
                            feasible = addObstacle(feasible, model, Zjti, j, t, "Obstacle2");
                            feasible = addRestriction(feasible, model, Zjti, j, t);
                        }                            
                    }
                }
            }
        }

        
        return feasible;
    }
    private boolean addObstacle(boolean not_add, Model model, IloNumVar[][][] Zjti, int j, int t, String name) throws IloException{
        if(!obst[j][t]){
            addObstacleAvoidanceFix(model.cplex, model.MUt, Zjti, model.addRest.AlocationFix(), j, t);
            obst[j][t] = true;
            System.out.printf("add "+name+" in j=%2d t=%2d \n", j+1, t);
            not_add = false;
        }

        final int N = 2;
        for(int s=t+1; s<=t+N && s<inst.T+1; s++){
            if(!obst[j][s]){
                addObstacleAvoidanceFix(model.cplex, model.MUt, Zjti, model.addRest.AlocationFix(), j, s);
                obst[j][s] = true;
                System.out.printf("add "+name+" in j=%2d t=%2d \n", j+1, s);
                not_add = false;
            }
        }
        for(int s=t-1; s>=t-(N+1) && s>=1; s--){
            if(!obst[j][s]){
                addObstacleAvoidanceFix(model.cplex, model.MUt, Zjti, model.addRest.AlocationFix(), j, s);
                obst[j][s] = true;
                System.out.printf("add "+name+" in j=%2d t=%2d \n", j+1, s);
                not_add = false;
            }
        }
        return not_add;
    }
    private boolean addRestriction(boolean not_add, Model model, IloNumVar[][][] Zjti, int j, int t) throws IloException{
        if(!chek[j][t]){
            addRestrictions(model.cplex, Zjti, j, t);
            chek[j][t] = true;
            not_add = false;
        } 
        final int N = 2;
        for(int s=t+1; s<=t+N && s<inst.T+1; s++){
            if(!chek[j][s]){
                addRestrictions(model.cplex, Zjti, j, s);
                chek[j][s] = true;
                not_add = false;
            }
        }
        for(int s=t-1; s>=t-N && s>=1; s--){
            if(!chek[j][s]){
                addRestrictions(model.cplex, Zjti, j, s);
                chek[j][s] = true;
                not_add = false;
            }
        }
        return not_add;
    }
        
    private void clear(boolean val[][]){
        for (int j = 0; j < inst.J; j++) {
            for (int t = 0; t < inst.T + 1; t++) {
                val[j][t] = false;
            }
        }
    }
    private double percentage(boolean val[][]){
        double sum = 0;
        for (int j = 0; j < inst.J; j++) {
            for (int t = 1; t < inst.T + 1; t++) {
                sum += val[j][t] ? 1 : 0;
            }
        }
        return sum * 100.0/ (inst.J * inst.T);
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
        //frr = selectFRR("FRR");
        frr = new Model("FRR", inst, selectOBJ(), ID_RELAXATION, new REST_empty(inst), null);
        
        int inter = 0;
        do{
            frr_per_chek = percentage(chek);
            frr_per_obst = percentage(obst);
            System.out.println("FRR-Infeasible - "+inter);
            System.out.println("FRR-lower           = "+lower);
            System.out.println("FRR-percentage_chek = "+frr_per_chek);
            System.out.println("FRR-percentage_obst = "+frr_per_obst);
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
                clear(obst);
                clear(chek);
                frt = new Model("FRT", inst, selectOBJ(), ID_UPERBOUND, new REST_empty(inst), null);
                inter = 0;
                double lower2 = Integer.MIN_VALUE;
                do{
                    frt_per_chek = percentage(chek);
                    frt_per_obst = percentage(obst);
                    System.out.println("FRT-Infeasible - "+inter);
                    System.out.println("FRT-lower           = "+lower2);
                    System.out.println("FRT-percentage_chek = "+frt_per_chek);
                    System.out.println("FRT-percentage_obst = "+frt_per_obst);
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
        link.writeDbl("CA-frr-obst", frr_per_obst);
        link.writeDbl("CA-frr-chek", frr_per_chek);
        link.writeDbl("CA-frt-obst", frt_per_obst);
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
