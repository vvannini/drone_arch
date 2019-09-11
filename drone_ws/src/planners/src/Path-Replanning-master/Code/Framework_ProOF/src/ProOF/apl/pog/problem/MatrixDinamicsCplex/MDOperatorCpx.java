/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.MatrixDinamicsCplex;

import ProOF.apl.pog.problem.MatrixDinamics.*;
import ProOF.com.language.Factory;
import ProOF.gen.operator.oCrossover;
import ProOF.gen.operator.oInitialization;
import ProOF.gen.operator.oLocalMove;
import ProOF.gen.operator.oMutation;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.codification.Codification;
import ProOF.opt.abst.problem.meta.codification.Operator;

/**
 *
 * @author marcio
 */
public class MDOperatorCpx extends Factory<Operator>{
    public static final MDOperatorCpx obj = new MDOperatorCpx();
    
    @Override
    public String name() {
        return "MD-Cpx Operators";
    }
    @Override
    public Operator build(int index) {
        switch(index){
            case 0: return new INIT();
            case 1: return new CrossOX();
            case 2: return new CrossAVG();
            case 3: return new MUT_exp();
            case 4: return new MOV_exp();
            case 5: return new MUT_plus();
            case 6: return new MOV_plus();
            case 7: return new MUT_swap();
            case 8: return new MOV_swap();
            case 9: return new MUT_round();
            case 10: return new MOV_round();
            case 11: return new MUT_single();
            case 12: return new MOV_single();
            case 13: return new MUT_geometriy_rmd();
            case 14: return new MOV_geometriy_rmd();
            case 15: return new MUT_geometriy_Apro();
            case 16: return new MOV_geometriy_Apro();
            case 17: return new MUT_repair();
            case 18: return new MOV_repair();
        }
        return null;
    }
    private class INIT extends oInitialization<MDFactoryCpx, MDCodificationCpx>{
        @Override
        public String name() {
            return "Random";
        }
        @Override
        public void initialize(MDFactoryCpx prob, MDCodificationCpx ind) throws Exception {
            ind.R = prob.inst.M<=1 ? 0 : prob.rnd.nextInt(1, prob.inst.S*(prob.inst.M-1));
            
            if(prob.rnd.nextBoolean()){
                for(int i=0; i<ind.C.length; i++){
                    for(int j=0; j<ind.C[i].length; j++){
                        ind.C[i][j] = prob.rnd.nextDouble(-1, 2);
                    }
                }
            }else{
                for(int i=0; i<ind.C.length; i++){
                    for(int j=0; j<ind.C[i].length; j++){
                        ind.C[i][j] = prob.rnd.nextInt(-2, 4)/2.0;
                    }
                }
            }
        }
    }
    private class CrossOX extends oCrossover<MDFactoryCpx, MDCodificationCpx>{
        @Override
        public String name() {
            return "OX";
        }
        @Override
        public MDCodificationCpx crossover(MDFactoryCpx prob, MDCodificationCpx ind1, MDCodificationCpx ind2) throws Exception {
            MDCodificationCpx child = ind1.build(prob);
            child.R = prob.rnd.nextInt(ind1.R, ind2.R);
            for(int i=0; i<child.C.length; i++){
                for(int j=0; j<child.C[i].length; j++){
                    child.C[i][j] = prob.rnd.nextBoolean() ? ind1.C[i][j] : ind2.C[i][j];
                }
            }
            return child;
        }
    }
    private class CrossAVG extends oCrossover<MDFactoryCpx, MDCodificationCpx>{
        @Override
        public String name() {
            return "AVG";
        }
        @Override
        public MDCodificationCpx crossover(MDFactoryCpx prob, MDCodificationCpx ind1, MDCodificationCpx ind2) throws Exception {
            MDCodificationCpx child = ind1.build(prob);
            child.R = prob.rnd.nextInt(ind1.R, ind2.R);
            for(int i=0; i<child.C.length; i++){
                for(int j=0; j<child.C[i].length; j++){
                    child.C[i][j] = (ind1.C[i][j] + ind2.C[i][j])/2;
                }
            }
            return child;
        }
    }
    
    private class MOV_exp extends oLocalMove<MDFactoryCpx, MDCodificationCpx>{
        @Override
        public String name() {
            return "Mov(*)";
        }
        @Override
        public void local_search(MDFactoryCpx prob, MDCodificationCpx ind) throws Exception {
            int i= prob.rnd.nextInt(prob.inst.S + ind.R);
            int j= ind.R==0 ? 0 : prob.rnd.nextInt(0, ind.R-1);
            ind.C[i][j] *= prob.rnd.nextDouble(0.5, 2);
        }
    }
    private class MOV_plus extends oLocalMove<MDFactoryCpx, MDCodificationCpx>{
        @Override
        public String name() {
            return "Mov(+)";
        }
        @Override
        public void local_search(MDFactoryCpx prob, MDCodificationCpx ind) throws Exception {
            int i= prob.rnd.nextInt(prob.inst.S + ind.R);
            int j= ind.R==0 ? 0 : prob.rnd.nextInt(0, ind.R-1);
            ind.C[i][j] += prob.rnd.nextDouble(-0.1, +0.1);
        }
    }
    private class MOV_swap extends oLocalMove<MDFactoryCpx, MDCodificationCpx>{
        @Override
        public String name() {
            return "Mov-Swap";
        }
        @Override
        public void local_search(MDFactoryCpx prob, MDCodificationCpx ind) throws Exception {
            int i, j;
            i= prob.rnd.nextInt(prob.inst.S + ind.R);
            j= ind.R==0 ? 0 : prob.rnd.nextInt(0, ind.R-1);
            double v = ind.C[i][j]*prob.rnd.nextDouble();
            ind.C[i][j] -= v;
            i= prob.rnd.nextInt(prob.inst.S + ind.R);
            j= ind.R==0 ? 0 : prob.rnd.nextInt(0, ind.R-1);
            ind.C[i][j] += v;
        }
    }
    private class MOV_round extends oLocalMove<MDFactoryCpx, MDCodificationCpx>{
        @Override
        public String name() {
            return "Mov-Round";
        }
        @Override
        public void local_search(MDFactoryCpx prob, MDCodificationCpx ind) throws Exception {
            int level = (int)(Math.pow(10, prob.rnd.nextInt(5))+0.5);
            for(int i=0; i<ind.C.length; i++){
                for(int j=0; j<ind.C[i].length; j++){
                    ind.C[i][j] = ((double)((int)(ind.C[i][j]*level)))/level;
                }
            }
        }
    }
    private class MOV_single extends oLocalMove<MDFactoryCpx, MDCodificationCpx>{
        @Override
        public String name() {
            return "Mov-Single";
        }
        @Override
        public void local_search(MDFactoryCpx prob, MDCodificationCpx ind) throws Exception {
            for(int i=0; i<ind.C.length; i++){
                for(int j=0; j<ind.C[i].length; j++){
                    if(Math.abs(ind.C[i][j])<0.1){
                        ind.C[i][j] = 0;
                    }else if(Math.abs(ind.C[i][j]-1)<0.1){
                        ind.C[i][j] = +1;
                    }else if(Math.abs(ind.C[i][j]+1)<0.1){
                        ind.C[i][j] = -1;
                    }
                }
            }
        }
    }
    //private static int teste = 0;
    private static double find_geometry(MDFactoryCpx prob, MDCodificationCpx ind, int r[], double factor){
        //teste++;
        double dist = -1;
        double max = -1;
        boolean flag = false;
        double next = Double.MAX_VALUE;
        for(int i=0; i<ind.C.length; i++){
            for(int j=0; j<ind.C[i].length; j++){
                for(int k=i; k<ind.C.length; k++){
                    for(int m=(k==i?j+1:j) ; m<ind.C[k].length; m++){
                        double v1 = ind.C[i][j];
                        double v2 = ind.C[k][m];
                        double val = Math.abs(v1-v2);
                        double f = Math.abs(v1*v2-1);
                        if(f <= factor){
                            if(val>dist){
                                dist = val;
                                max = Math.max(v1, v2);
                                r[0] = i;
                                r[1] = j;
                                r[2] = k;
                                r[3] = m;
                                flag = true;
                            }
                        }else{
                            next = Math.min(next, f);
                        }
                    }
                }
            }
        }
        return flag ? max : find_geometry(prob, ind, r, next*1.1);
    }
    
    private class MOV_geometriy_rmd extends oLocalMove<MDFactoryCpx, MDCodificationCpx>{
        @Override
        public String name() {
            return "Mov-Geo-Rmd";
        }
        @Override
        public void local_search(MDFactoryCpx prob, MDCodificationCpx ind) throws Exception {
            int r[] = new int[4];
            find_geometry(prob, ind, r, 0.2*prob.rnd.nextDouble());
            int i = r[0];
            int j = r[1];
            int k = r[2];
            int m = r[3];
            double v1 = prob.rnd.nextBoolean() ? prob.rnd.nextInt(1,5) : 1.0/prob.rnd.nextInt(1,5);
            double v2 = 1/v1;
            ind.C[i][j] = v1;
            ind.C[k][m] = v2;
            
        }
    }
    private class MOV_geometriy_Apro extends oLocalMove<MDFactoryCpx, MDCodificationCpx>{
        @Override
        public String name() {
            return "Mov-Geo-Apro";
        }
        @Override
        public void local_search(MDFactoryCpx prob, MDCodificationCpx ind) throws Exception {
            int r[] = new int[4];
            double max = find_geometry(prob, ind, r, 0.2*prob.rnd.nextDouble());
            int i = r[0];
            int j = r[1];
            int k = r[2];
            int m = r[3];
            double v1 = (int) (prob.rnd.nextBoolean() && max>1.1 ? max: max+1);
            double v2 = 1/v1;
            ind.C[i][j] = v1;
            ind.C[k][m] = v2;
        }
    }
    private class MOV_repair extends oLocalMove<MDFactoryCpx, MDCodificationCpx>{
        @Override
        public String name() {
            return "Mov-repair";
        }
        @Override
        public void local_search(MDFactoryCpx prob, MDCodificationCpx ind) throws Exception {
            int level = (int)(Math.pow(10, prob.rnd.nextInt(5))+0.5);
            int r[] = new int[4];
            double max = find_geometry(prob, ind, r, 0.2/level);
            int i = r[0];
            int j = r[1];
            int k = r[2];
            int m = r[3];
            double v1 = prob.rnd.nextDouble(1, max);
            double v2 = 1/v1;
            v1 = v1>v2 ? (int)(max) : 1.0/(int)(max);
            v2 = 1/v1;
            ind.C[i][j] = v1;
            ind.C[k][m] = v2;
            
            for( i=0; i<ind.C.length; i++){
                for( j=0; j<ind.C[i].length; j++){
                    ind.C[i][j] = ((double)((int)(ind.C[i][j]*level)))/level;
                }
            }
        }
    }
    private class MUT_exp extends oMutation<MDFactoryCpx, MDCodificationCpx>{
        @Override
        public String name() {
            return "Mut(*)";
        }
        @Override
        public void mutation(MDFactoryCpx prob, MDCodificationCpx ind) throws Exception {
            int i= prob.rnd.nextInt(prob.inst.S + ind.R);
            int j= ind.R==0 ? 0 : prob.rnd.nextInt(0, ind.R-1);
            ind.C[i][j] *= prob.rnd.nextDouble(0.5, 2);
        }
    }
    private class MUT_plus extends oMutation<MDFactoryCpx, MDCodificationCpx>{
        @Override
        public String name() {
            return "Mut(+)";
        }
        @Override
        public void mutation(MDFactoryCpx prob, MDCodificationCpx ind) throws Exception {
            int i= prob.rnd.nextInt(prob.inst.S + ind.R);
            int j= ind.R==0 ? 0 : prob.rnd.nextInt(0, ind.R-1);
            ind.C[i][j] += prob.rnd.nextDouble(-0.1, +0.1);
        }
    }
    private class MUT_swap extends oMutation<MDFactoryCpx, MDCodificationCpx>{
        @Override
        public String name() {
            return "Mut-Swap";
        }
        @Override
        public void mutation(MDFactoryCpx prob, MDCodificationCpx ind) throws Exception {
            int i, j;
            i= prob.rnd.nextInt(prob.inst.S + ind.R);
            j= ind.R==0 ? 0 : prob.rnd.nextInt(0, ind.R-1);
            double v = ind.C[i][j]*prob.rnd.nextDouble();
            ind.C[i][j] -= v;
            i= prob.rnd.nextInt(prob.inst.S + ind.R);
            j= ind.R==0 ? 0 : prob.rnd.nextInt(0, ind.R-1);
            ind.C[i][j] += v;
        }
    }
    private class MUT_round extends oMutation<MDFactoryCpx, MDCodificationCpx>{
        @Override
        public String name() {
            return "Mut-Round";
        }
        @Override
        public void mutation(MDFactoryCpx prob, MDCodificationCpx ind) throws Exception {
            int level = (int)(Math.pow(10, prob.rnd.nextInt(5))+0.5);
            for(int i=0; i<ind.C.length; i++){
                for(int j=0; j<ind.C[i].length; j++){
                    ind.C[i][j] = ((double)((int)(ind.C[i][j]*level)))/level;
                }
            }
        }
    }
    private class MUT_single extends oMutation<MDFactoryCpx, MDCodificationCpx>{
        @Override
        public String name() {
            return "Mut-Single";
        }
        @Override
        public void mutation(MDFactoryCpx prob, MDCodificationCpx ind) throws Exception {
            for(int i=0; i<ind.C.length; i++){
                for(int j=0; j<ind.C[i].length; j++){
                    if(Math.abs(ind.C[i][j])<0.1){
                        ind.C[i][j] = 0;
                    }else if(Math.abs(ind.C[i][j]-1)<0.1){
                        ind.C[i][j] = +1;
                    }else if(Math.abs(ind.C[i][j]+1)<0.1){
                        ind.C[i][j] = -1;
                    }
                }
            }
        }
    }
    
    private class MUT_geometriy_rmd extends oMutation<MDFactoryCpx, MDCodificationCpx>{
        @Override
        public String name() {
            return "Mut-Geo-Rmd";
        }
        @Override
        public void mutation(MDFactoryCpx prob, MDCodificationCpx ind) throws Exception {
            int r[] = new int[4];
            find_geometry(prob, ind, r, 0.2*prob.rnd.nextDouble());
            int i = r[0];
            int j = r[1];
            int k = r[2];
            int m = r[3];
            double v1 = prob.rnd.nextBoolean() ? prob.rnd.nextInt(1,5) : 1.0/prob.rnd.nextInt(1,5);
            double v2 = 1/v1;
            ind.C[i][j] = v1;
            ind.C[k][m] = v2;
            
        }
    }
    private class MUT_geometriy_Apro extends oMutation<MDFactoryCpx, MDCodificationCpx>{
        @Override
        public String name() {
            return "Mut-Geo-Apro";
        }
        @Override
        public void mutation(MDFactoryCpx prob, MDCodificationCpx ind) throws Exception {
            int r[] = new int[4];
            double max = find_geometry(prob, ind, r, 0.2*prob.rnd.nextDouble());
            int i = r[0];
            int j = r[1];
            int k = r[2];
            int m = r[3];
            double v1 = (int) (prob.rnd.nextBoolean() && max>1.1 ? max: max+1);
            double v2 = 1/v1;
            ind.C[i][j] = v1;
            ind.C[k][m] = v2;
        }
    }
    private class MUT_repair extends oMutation<MDFactoryCpx, MDCodificationCpx>{
        @Override
        public String name() {
            return "Mut-repair";
        }
        @Override
        public void mutation(MDFactoryCpx prob, MDCodificationCpx ind) throws Exception {
            int level = (int)(Math.pow(10, prob.rnd.nextInt(5))+0.5);
            int r[] = new int[4];
            double max = find_geometry(prob, ind, r, 0.2/level);
            int i = r[0];
            int j = r[1];
            int k = r[2];
            int m = r[3];
            double v1 = prob.rnd.nextDouble(1, max);
            double v2 = 1/v1;
            v1 = v1>v2 ? (int)(max) : 1.0/(int)(max);
            v2 = 1/v1;
            ind.C[i][j] = v1;
            ind.C[k][m] = v2;
            
            for( i=0; i<ind.C.length; i++){
                for( j=0; j<ind.C[i].length; j++){
                    ind.C[i][j] = ((double)((int)(ind.C[i][j]*level)))/level;
                }
            }
        }
    }
    
}
