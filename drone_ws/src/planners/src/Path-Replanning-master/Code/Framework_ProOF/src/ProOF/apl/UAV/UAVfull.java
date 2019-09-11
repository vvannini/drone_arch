/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV;
import ProOF.apl.UAV.abst.UAVApproach;
import ProOF.apl.UAV.abst.UAVModel;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.CplexOpt.CplexFull;
import ProOF.com.Linker.LinkerResults;
import ilog.concert.IloException;

/**
 *
 * @author marcio
 */
public class UAVfull extends CplexFull{
    
    private UAVApproach approach;
    private oFull full_op;
    private UAVModel model;
    
    public UAVfull() throws IloException {
    }
    
    @Override
    public void services(LinkerApproaches link) throws Exception {
        super.services(link);
        approach = link.get(fUAVApproach.obj, approach);
        full_op = link.need(oFull.class, full_op);
    }
    
    @Override
    public String name() {
        return "UAVfull";
    }
    
    @Override
    public void model() throws Exception {
        model = full_op.build_model(approach, cpx);
//        cpx.exportModel("./"+name()+".lp");        
    }
    
    @Override
    public void solve() throws Exception{
        model();
        if(model.solve()){
            print();
        }
    }
    
    @Override
    public void results(LinkerResults link) throws Exception {
        //super.results(link);
        model.results(link);
        model.save();
    }
}
