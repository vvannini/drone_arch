/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.gen.operator;

import ProOF.com.Linker.LinkerParameters;
import ProOF.opt.abst.problem.meta.Solution;
import ProOF.opt.abst.problem.meta.codification.Codification;
import ProOF.utilities.uRoulette;
import ProOF.utilities.uUtil;


/**
 *
 * @author marcio
 */
public class iCrossLearning extends aCrossover{
    private static iCrossLearning obj = null;
    public static iCrossLearning object(){
        if(obj==null){
            obj = new iCrossLearning();
        }
        return obj;
    }


    private double alpha;   //taxa de aprendizado
    private double beta;    //taxa de memorizacao
    private int index;      //current index of crossover
    private double weigth[];
    private uRoulette roulette;
    
    private iCrossLearning() {
    
    }

    @Override
    public String name() {
        return "Iterations";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        alpha   = link.Dbl("alpha", 1,      0,  1);   //memorizacao
        beta    = link.Dbl("beta",  0.1,    0,  1);   //aprendizado
    }
    @Override
    public void start() throws Exception {
        
    }

    @Override
    public void load() throws Exception {
        weigth = new double[cross.length];
        for(int i=0; i<weigth.length; i++){
            weigth[i] = 1.0/cross.length;
        }
        index = -1;
        roulette = new uRoulette(prob.rnd, 1, weigth);
    }
    public void not_insert(){
        weigth[index] *= ( 1.0 - beta );    //aprendizado
        normalize();
    }
    public void has_insert() throws Exception {
        weigth[index] *= ( 1.0 + beta );    //aprendizado
        normalize();
    }
    
    @Override
    public Solution crossover(Solution ind1, Solution ind2) throws Exception {
        index = roulette.roulette_wheel();
        Codification sol = cross[index].crossover(prob, ind1.codif(), ind2.codif());
        return prob.build_sol(sol);
    }
    
    private void normalize(){
        //memorizacao
        double sum = uUtil.sum(weigth);
        for(int i=0; i<weigth.length; i++){
            weigth[i] = weigth[i]*alpha + (1-alpha)/sum;
        }
        //normalizacao
        sum = uUtil.sum(weigth);
        for(int i=0; i<weigth.length; i++){
            weigth[i] = weigth[i]/sum;
        }
        index = -1;
    }
}
