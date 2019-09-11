/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.RA.Codif1;

import ProOF.com.Linker.LinkerResults;
import ProOF.opt.abst.problem.meta.codification.Codification;
import java.util.ArrayList;

/**
 *
 * @author marcio
 */
public class RACodification1 extends Codification<RAProblem1, RACodification1> {

    public final ArrayList<Integer> Yt[];

    public RACodification1(RAProblem1 prob) {
        Yt = new ArrayList[prob.inst.T];
        for (int t = 0; t < prob.inst.T; t++) {
            Yt[t] = new ArrayList<Integer>();
        }
    }

    @Override
    public void copy(RAProblem1 prob, RACodification1 source) throws Exception {
        for (int t = 0; t < prob.inst.T; t++) {
            Yt[t] = new ArrayList<Integer>(source.Yt[t]);
        }
    }

    @Override
    public RACodification1 build(RAProblem1 prob) throws Exception {
        return new RACodification1(prob);
    }

    protected void repair(RAProblem1 prob, int t) throws Exception {
        for (int i = 0; i < prob.inst.N; i++) {
            prob.repetidos[i] = false;
        }
        for (int i = 0; i < Yt[t].size(); i++) {
            int J = Yt[t].get(i);
            if (prob.repetidos[J]) {
                Yt[t].remove(new Integer(J));
                i--;
            } else {
                prob.repetidos[J] = true;
            }
        }
        for (int i = 0; i < prob.inst.N; i++) {
            prob.repetidos[i] = false;
        }
        for (int i = 0; i < Yt[t].size(); i++) {
            int J = Yt[t].get(i);
            if (prob.repetidos[J]) {
                throw new Exception("Aquiiiiii");
            } else {
                prob.repetidos[J] = true;
            }
        }
    }

    @Override
    public void resulter(RAProblem1 prob, LinkerResults win) throws Exception {
        super.resulter(prob, win);
        for (int t = 0; t < prob.inst.T; t++) {
            int seq[] = new int[this.Yt[t].size()];
            int i = 0;
            for (Integer prod : this.Yt[t]) {
                seq[i++] = prod;
            }
            win.writeArray("Yt", t + "", seq);
        }        
    }
}
