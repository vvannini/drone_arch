/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.GCILT.notZyt;

import ProOF.apl.pog.problem.GCILT.GCILTLot;
import ProOF.opt.abst.problem.meta.codification.Codification;
import java.util.LinkedList;

/**
 *
 * @author Hossomi
 */
public class GCILTCodification extends Codification<GCILTNProblem, GCILTCodification> {

    public LinkedList<GCILTLot>[][] Syt;
    public int Zy[];
    
    public GCILTCodification(GCILTNProblem gcilt) {
        Syt = new LinkedList[gcilt.inst.Y][gcilt.inst.T];
        Zy = new int[gcilt.inst.Y];
        for (int y = 0; y < gcilt.inst.Y; y++) {
            for (int t = 0; t < gcilt.inst.T; t++) {
                Syt[y][t] = new LinkedList<GCILTLot>();
            }
        }
    }

    @Override
    public void copy(GCILTNProblem gcilt, GCILTCodification source) throws Exception {
        for (int y = 0; y < gcilt.inst.Y; y++) {
            for (int t = 0; t < gcilt.inst.T; t++) {
                Syt[y][t] = new LinkedList<GCILTLot>(source.Syt[y][t]);
            }
        }
        System.arraycopy(source.Zy, 0, Zy, 0, gcilt.inst.Y);
    }

    @Override
    public GCILTCodification build(GCILTNProblem gcilt) throws Exception {
        return new GCILTCodification(gcilt);
    }
    
    public void print( GCILTNProblem problem ) {
        for (int y = 0; y < problem.inst.Y; y++) {
            System.out.printf(" y[%2d] : days[%3d] -> ", y, Zy[y]);
            for(int t = 0; t < problem.inst.T; t++){
                for(GCILTLot lot : Syt[y][t]){
                    System.out.printf("[ %2d %2d ]", lot.color, lot.days);
                }
                System.out.printf(" | ");
            }
            System.out.println();
        }
    }
}
