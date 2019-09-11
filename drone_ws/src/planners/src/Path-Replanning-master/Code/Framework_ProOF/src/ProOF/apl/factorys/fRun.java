/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.factorys;

import ProOF.apl.pog.conversores.UAVMap01;
import ProOF.apl.pog.method.BranchAndBound.BranchAndBound;
import ProOF.apl.pog.method.CustomizedApproach.CAIncremental_v1;
import ProOF.apl.pog.method.CustomizedApproach.CAIncremental_v2;
import ProOF.apl.pog.method.CustomizedApproach.CAIncremental_v3;
import ProOF.apl.pog.method.CustomizedApproach.CustomizedApproach;
import ProOF.apl.pog.method.CustomizedApproach.ModelFull;
import ProOF.apl.pog.method.DPOC.DPOC;
import ProOF.apl.pog.method.Descend;
import ProOF.apl.pog.method.Exact.Impl.BranchAndBoundExact;
import ProOF.apl.pog.method.Exact.Impl.GreedyExact;
import ProOF.apl.pog.method.GCILTFixOpt_v1;
import ProOF.apl.pog.method.GCILTFixOpt_v2;
import ProOF.apl.pog.method.GCILTFull;
import ProOF.apl.pog.method.NSGAII;
import ProOF.apl.pog.method.OUT;
import ProOF.apl.pog.method.PPDCPFullAlpha;
import ProOF.apl.pog.method.PPDCPFullOrig;
import ProOF.apl.pog.method.PPDCPFullTrocas;
import ProOF.apl.pog.method.PPDCP_DP01;
import ProOF.apl.advanced1.method.Tabler;
import ProOF.apl.pog.method.mAG;
import ProOF.apl.pog.method.mSAImplementation;
import ProOF.apl.pog.method.pSuluPlanner;
import ProOF.apl.sample1.method.GeneticAlgorithm;
import ProOF.apl.sample1.method.RandomAlgorithm;
import ProOF.com.language.Factory;
import ProOF.com.language.Run;
import ProOF.CplexOpt.CplexModel;
import ProOF.apl.UAV.CSA;
import ProOF.apl.advanced1.method.MPGA;
import ProOF.apl.advanced2.method.RFFO;
import ProOF.apl.jsa.method.apstd.APSTD;
import ProOF.apl.jsa.method.de.DifferentialEvolution;
import ProOF.apl.jsa.method.greedy.GreedyAlgorithm;
import ProOF.apl.jsa.method.multistart.MultiStart;
import ProOF.apl.jsa.method.sga.SimpleGeneticAlgorithm;

/**
 *
 * @author marcio
 */
public class fRun extends Factory<Run>{
    public static final fRun obj = new fRun();
    private fRun(){}
    
    @Override
    public String name() {
        return "Run";
    }
    @Override
    public Run build(int index) {
        switch(index){
            case 0: return new mSAImplementation();
            case 1: return new GeneticAlgorithm();
            case 2: return new NSGAII();
            case 3: return new BranchAndBound();
            case 4: return new CplexModel(fCplexFull.obj);
            case 5: return new RandomAlgorithm();
            case 6: return new Descend();
            case 7: return new MPGA();
            case 8: return new CustomizedApproach();
            case 9: return new ModelFull();
            case 10: return new pSuluPlanner();
            case 11: return new GCILTFull();
            case 12: return new OUT();
            case 13: return new GCILTFixOpt_v2();
            case 14: return new GCILTFixOpt_v1();
            case 15: return new PPDCPFullOrig();//PPDCPFullOrig();
            case 16: return new Tabler();
            case 17: return new PPDCPFullTrocas();
            case 18: return new PPDCPFullAlpha();
            case 19: return new mAG();
            case 20: return new BranchAndBoundExact();
            case 21: return new GreedyExact();
            case 22: return new PPDCP_DP01();
            case 23: return new DPOC();
            case 24: return new CAIncremental_v1();
            case 25: return new CAIncremental_v2();
            case 26: return new CAIncremental_v3();
            case 27: return new UAVMap01();
            case 28: return new RFFO();
            case 29: return new CSA();
            case 30: return new SimpleGeneticAlgorithm();
            case 31: return new DifferentialEvolution();
            case 32: return new GreedyAlgorithm();
            case 33: return new APSTD();
            case 34: return new MultiStart();
        }
        return null;
    }

    
}
