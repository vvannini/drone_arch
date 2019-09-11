/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.FuncB;
import ProOF.apl.factorys.fRealOperator;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.gen.best.BestSol;
import ProOF.gen.codification.Real.cReal;
import ProOF.opt.abst.problem.meta.Objective;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.codification.Codification;

/**
 *
 * @author marcio
 */
public class FuncBFactory extends Problem<BestSol>{
    public final FuncBParameter param = new FuncBParameter();
    @Override
    public String name() {
        return "Fuction-v2";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public Codification build_codif() throws Exception {
        return new cReal(1);
    }
    @Override
    public Objective build_obj() throws Exception {
        return new FuncBObjective();
    }
    @Override
    public BestSol best() {
        return BestSol.object();
    }

    @Override
    public void services(LinkerApproaches link) throws Exception {
        super.services(link);
        link.add(param);
        link.add(fRealOperator.obj);
    }
}

/*
@Override
public pObjective NewObjective(pMemSync mem) throws Exception {
    return new pSingleObjective() {
        @Override
        public void Evaluate(pMemSync mem, pCodification codif) throws Exception {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        @Override
        public pObjective New(pMemSync mem) throws Exception {
            return this.getClass().newInstance();
        }
    };
}
*/