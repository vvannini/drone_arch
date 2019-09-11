/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.GCILT;

/**
 *
 * @author Hossomi
 */
public class GCILTLot {

    public final int color;
    public final int days;
    public double remTime;
    public double remCap;

    public GCILTLot(int color, int days) throws Exception {
        if (days == 0) {
            throw new Exception("Creating null lot");
        }

        this.color = color;
        this.days = days;
        this.remTime = days;
        this.remCap = -1;
    }

    public GCILTLot getCopy() throws Exception {
        return new GCILTLot(color, days);
    }
}
