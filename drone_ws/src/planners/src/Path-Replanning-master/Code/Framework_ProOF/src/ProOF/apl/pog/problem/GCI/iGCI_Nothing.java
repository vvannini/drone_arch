/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.GCI;




/**
 *
 * @author marcio
 */
public class iGCI_Nothing extends aGCI{
    @Override
    public String name() {
        return "Nothing";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void setCap(GCICodification codif, int t, double C) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected double solve(GCICodification codif) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
