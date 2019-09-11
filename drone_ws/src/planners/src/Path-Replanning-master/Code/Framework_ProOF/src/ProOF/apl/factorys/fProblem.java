package ProOF.apl.factorys;

import ProOF.apl.jsa.problem.PPCCS.core.PPCCSProblem;
import ProOF.apl.pog.gen.codification.real.pRealMultiObjProblem;
import ProOF.apl.pog.gen.codification.real.pRealSingleObjProblem;
import ProOF.apl.pog.problem.ACK.ACKFactory;
import ProOF.apl.pog.problem.FuncA.FuncAFactory;
import ProOF.apl.pog.problem.FuncB.FuncBFactory;
import ProOF.apl.pog.problem.GCI.GCIFactory;
import ProOF.apl.pog.problem.GCILT.notZyt.GCILTNProblem;
import ProOF.apl.pog.problem.GCILT.withZyt.GCILTWProblem;
import ProOF.apl.pog.problem.GCILTB.GCILTBProblem;
import ProOF.apl.pog.problem.MatrixDinamics.MDFactory;
import ProOF.apl.pog.problem.MatrixDinamicsCplex.MDFactoryCpx;
import ProOF.apl.pog.problem.Multi.fFON;
import ProOF.apl.pog.problem.Multi.fKUR;
import ProOF.apl.pog.problem.Multi.fPOL;
import ProOF.apl.pog.problem.Multi.fSCH;
import ProOF.apl.pog.problem.PID.PIDProblem;
import ProOF.apl.pog.problem.PID2.PIDProblem2;
import ProOF.apl.pog.problem.PPDCP.PPDCPFactory;
import ProOF.apl.pog.problem.RA.Codif1.RAProblem1;
import ProOF.apl.pog.problem.RA.Codif2.RAProblem2;
import ProOF.apl.pog.problem.Single.fACK;
import ProOF.apl.pog.problem.Single.fFunction;
import ProOF.apl.pog.problem.TSP.TSPFactory;
import ProOF.apl.pog.problem.Test.TestFactory;
import ProOF.com.language.Factory;
import ProOF.gen.codification.FunctionSingle.SingleBinRealFunction;
import ProOF.gen.codification.FunctionSingle.SingleRealFunction;
import ProOF.gen.codification.FunctionMulti.MultiBinRealFunction;
import ProOF.gen.codification.FunctionMulti.MultiRealFunction;
import ProOF.opt.abst.problem.meta.Problem;

/**
 *
 * @author marcio
 */
public final class fProblem extends Factory<Problem>{
    public static final fProblem obj = new fProblem();
    
    @Override
    public String name() {
        return "Problem";
    }
    
    @Override
    public Problem build(int index) {
        switch(index){
            case 0: return new TSPFactory();
            case 1: return new SingleRealFunction   (fRealSingle.obj,   fRealOperator.obj);
            case 2: return new SingleBinRealFunction(fRealSingle.obj,   fBinRealOperator.obj);
            case 3: return new MultiRealFunction    (fRealMulti.obj,    fRealOperator.obj);
            case 4: return new MultiBinRealFunction (fRealMulti.obj,    fBinRealOperator.obj);
            case 5: return new ACKFactory();
            case 6: return new GCIFactory();
            case 7: return new FuncAFactory();
            case 8: return new FuncBFactory();
            case 9: return new pRealSingleObjProblem(new fACK());
            case 10: return new pRealSingleObjProblem(new fFunction());
            case 11: return new PIDProblem();
            case 12: return new PIDProblem2();
            case 13: return new GCILTWProblem();
            case 14: return new GCILTNProblem();
            case 15: return new GCILTBProblem();
            case 16: return new pRealMultiObjProblem(new fSCH());
            case 17: return new pRealMultiObjProblem(new fFON());
            case 18: return new pRealMultiObjProblem(new fPOL());
            case 19: return new pRealMultiObjProblem(new fKUR());
            case 20: return new PPDCPFactory();
            case 21: return new RAProblem2();
            case 22: return new RAProblem1();
            case 23: return new MDFactory();
            case 24: return new MDFactoryCpx();
            case 25: return new TestFactory();
            case 26: return new PPCCSProblem();
        }
        return null;
    }
}
