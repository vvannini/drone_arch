/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.comb.TSP;

import ProOF.apl.pog.method.Exact.oExpand;
import ProOF.apl.pog.method.Exact.oLowerBound;
import ProOF.com.language.Factory;
import ProOF.opt.abst.problem.meta.codification.Operator;
import java.util.LinkedList;

/**
 *
 * @author marcio
 */
public class ExactTSPOperator extends Factory<Operator>{
    public static final ExactTSPOperator obj = new ExactTSPOperator();
    
    @Override
    public String name() {
        return "ExactTSP Operators";
    }
    @Override
    public Operator build(int index) {
        switch(index){
            case 0: return new EXPAND();
            case 1: return new LOWER_BOUND_01();
        }
        return null;
    }
    private class EXPAND extends oExpand<ExactTSP, ExactTSPCity>{
        @Override
        public String name() {
            return "Expand";
        }
        @Override
        public ExactTSPCity[] expand(ExactTSP prob, ExactTSPCity base) throws Exception {
            LinkedList<ExactTSPCity> list = new LinkedList<ExactTSPCity>();
            if(!base.is_integer(prob)){
                for(int i=0; i<prob.inst.N; i++){
                    if(!base.contains(i)){
                        ExactTSPCity no = new ExactTSPCity(prob, base, i);
                        list.addLast(no);
                    }
                }
            }
            return list.toArray(new ExactTSPCity[list.size()]);
        }
    }
    private class LOWER_BOUND_01 extends oLowerBound<ExactTSP, ExactTSPCity>{
        @Override
        public String name() {
            return "LB-01";
        }
        @Override
        public double lower_bound(ExactTSP prob, ExactTSPCity base) throws Exception {
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
    }
}
