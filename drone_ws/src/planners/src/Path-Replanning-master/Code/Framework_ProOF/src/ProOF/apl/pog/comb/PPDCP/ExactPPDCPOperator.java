/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.comb.PPDCP;

import ProOF.apl.pog.method.Exact.oExpand;
import ProOF.apl.pog.method.Exact.oLowerBound;
import ProOF.com.language.Factory;
import ProOF.opt.abst.problem.meta.codification.Operator;
import ProOF.utilities.uUtil;
import java.util.LinkedList;

/**
 *
 * @author marcio
 */
public class ExactPPDCPOperator extends Factory<Operator>{
    public static final ExactPPDCPOperator obj = new ExactPPDCPOperator();
    
    @Override
    public String name() {
        return "ExactPPDCP Operators";
    }
    @Override
    public Operator build(int index) {
        switch(index){
            case 0: return new EXPAND();
            //case 1: return new LOWER_BOUND_01();
        }
        return null;
    }
    private class EXPAND extends oExpand<ExactPPDCP, ExactPPDCPStage>{
        private final int D = 100;
        @Override
        public String name() {
            return "Expand";
        }
        @Override
        public ExactPPDCPStage[] expand(ExactPPDCP prob, ExactPPDCPStage base) throws Exception {
            LinkedList<ExactPPDCPStage> list = new LinkedList<ExactPPDCPStage>();
            if(!base.is_integer(prob)){
                for(int i=0; i<D; i++){
                    Double U[] = rmd(prob);
                    Double X[] = next(prob, base.state(), U);
                    ExactPPDCPStage no = new ExactPPDCPStage(prob, base, X);
                    list.addLast(no);
                }
            }
            return list.toArray(new ExactPPDCPStage[list.size()]);
        }
        public Double[] rmd(ExactPPDCP prob){
            double ax = prob.rmd.nextDouble(-prob.inst.VMAX, +prob.inst.VMAX);
            double ay = prob.rmd.nextDouble(-prob.inst.VMAX, +prob.inst.VMAX);
            double a = Math.sqrt(ax*ax + ay*ay);
            ax = ax/a;
            ay = ay/a;
            return new Double[]{
                //prob.rmd.nextDouble(-prob.inst.VMAX, +prob.inst.VMAX),
                //prob.rmd.nextDouble(-prob.inst.VMAX, +prob.inst.VMAX)
                ax,ay
            };
        }
        public Double[] next(ExactPPDCP prob, Double Xi[], Double U[]){
            Double Xf[] = new Double[4];
            for(int i=0; i<4; i++){
                double sum = 0;
                for(int k=0; k<4; k++){
                    sum += prob.inst.A[i][k]*Xi[k];
                }
                for(int j=0; j<2; j++){
                    sum += prob.inst.B[i][j]*U[j];
                }
                Xf[i] = sum;
            }
            return Xf;
        }
    }
    /*private class LOWER_BOUND_01 extends oLowerBound<ExactPPDCP, ExactPPDCPStage>{
        @Override
        public String name() {
            return "LB-01";
        }
        @Override
        public double lower_bound(ExactPPDCP prob, ExactPPDCPStage base) throws Exception {
            //return cur_cost;
            if(base.is_integer(prob)){
                return base.cur_cost;
            }else{
                double lower = prob.inst.adj_i[base.city()][0];
                for(int a=0; a<prob.inst.N; a++){
                    if(!base.contains(a)){
                        lower += prob.inst.adj_i[a][0];
                    }
                }
                return base.cur_cost + lower;
            }
        }
    }*/
}
