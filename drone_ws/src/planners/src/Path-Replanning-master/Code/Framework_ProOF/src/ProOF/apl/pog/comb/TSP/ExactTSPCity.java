/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.comb.TSP;

import ProOF.apl.pog.method.Exact.ExactNode;

/**
 *
 * @author marcio
 */
public class ExactTSPCity extends ExactNode<ExactTSP, ExactTSPCity, Integer>{
    public ExactTSPCity(ExactTSP prob, ExactTSPCity back, Integer city) throws Exception {
        super(prob, back, city);
    }
    @Override
    public int mem_bytes() throws Exception {
        return 4+mem_base();
    }
    @Override
    public boolean is_integer(ExactTSP prob) throws Exception {
        return level>=prob.inst.N;
    }
    @Override
    protected double evaluate(ExactTSP prob) throws Exception {
        if(is_integer(prob)){
            int a = back.city();
            int b = this.city();
            int c = 0;      //frist_city();
            return back.cur_cost + prob.inst.Cij[a][b] + prob.inst.Cij[b][c];
        }else{
            int a = back.city();
            int b = this.city();
            return back.cur_cost + prob.inst.Cij[a][b];
        }
    }
    public int city() {
        return data;
    }
}
