/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.PID;

import ProOF.gen.codification.Real.cReal;
import ProOF.opt.abst.problem.meta.objective.SingleObjective;

/**
 *
 * @author marcio
 */
public class PIDObjective extends SingleObjective<PIDProblem, cReal, PIDObjective> {

    public PIDObjective() throws Exception {
        super();
    }
    
    @Override
    public void evaluate(PIDProblem pid, cReal codif) throws Exception {
        double fitness = 0;
        for(int w=0; w<pid.inst.weights.length; w++){
            fitness += pid.inst.weights[w] * propagation(w, pid.inst, codif);
        }
        set(fitness);
    }
    private double propagation(int w, PIDInstance inst,  cReal codif){
        double out[] = new double[19];
        out[0] = 1;
        out[1] = inst.decode(0, codif.X[0]);    //Kc
        out[2] = inst.decode(1, codif.X[1]);    //Ti
        out[3] = inst.decode(2, codif.X[2]);    //Td
        for(int j=4; j<19; j++){
            double soma = 0;
            for(int i=0; i<j; i++){
                soma += inst.RNA[w][i][j] * out[i];
            }
            if(j+1== 19){
                out[j] = soma;
            }else{
                out[j] = 1.0 / (1.0 + Math.exp(-soma));
            }
        }
        if(out[18]<0){
            return 999999;
        }else{
            return 4.0 / out[18];
        }
    }
    
    @Override
    public PIDObjective build(PIDProblem mem) throws Exception {
        return new PIDObjective();
    }
}
