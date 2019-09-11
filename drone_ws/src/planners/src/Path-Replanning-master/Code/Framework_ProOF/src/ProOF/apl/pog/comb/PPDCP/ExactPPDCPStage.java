/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.comb.PPDCP;

import ProOF.apl.pog.method.Exact.ExactNode;
import ProOF.apl.pog.method.Exact.aComb;
import ProOF.com.Stream.StreamPrinter;
import ProOF.utilities.uUtil;

/**
 *
 * @author marcio
 */
public class ExactPPDCPStage extends ExactNode<ExactPPDCP, ExactPPDCPStage, Double[]>{
    public ExactPPDCPStage(ExactPPDCP prob, ExactPPDCPStage back, Double X0[]) throws Exception {
        super(prob, back, X0);
    }
    @Override
    public int mem_bytes() throws Exception {
        return 32+mem_base();
    }
    @Override
    public boolean is_integer(ExactPPDCP prob) throws Exception {
        return level>=prob.inst.T;
    }
    @Override
    protected double evaluate(ExactPPDCP prob) throws Exception {
        Double Xi[] = back.state();
        Double Xf[] = this.state();
        double dist = uUtil.SQN2(Xi[0], Xi[2], Xf[0], Xf[2]);

        if(is_integer(prob)){
            double dist_goal = uUtil.SQN2(prob.inst.Xgoal[0], prob.inst.Xgoal[2], Xf[0], Xf[2]);
            return back.cur_cost + dist + prob.inst.GOAL(dist_goal);
        }else{
            //double dist_goal = uUtil.SQN2(prob.inst.Xgoal[0], prob.inst.Xgoal[2], Xf[0], Xf[2]);
            return back.cur_cost + dist;
        }
    }
    public Double[] state() {
        return data;
    }
  
    @Override
    public void printer(ExactPPDCP prob, StreamPrinter link, ExactNode base) throws Exception { 
        link.printLong("id", id);
        link.printInt("level", level);
        link.printDbl("cost", cur_cost);
        
        if(is_integer(prob)){
            double Xt[][] = new double[prob.inst.T+1][4];
            ExactPPDCPStage aux = this;
            int t=prob.inst.T;
            while(aux!=null){
                for(int i=0; i<4; i++){
                    Xt[t][i] = aux.state()[i];
                }
                aux = aux.back;
            }
            prob.inst.plot(Xt);
            prob.inst.save();
        }
    }
}
