/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV;

import ProOF.CplexExtended.CplexExtended;
import ProOF.apl.UAV.abst.UAVApproach;
import ProOF.apl.UAV.abst.UAVModel;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.opt.abst.run.Heuristic;
import ProOF.utilities.uTime;
import ProOF.utilities.uTimeMilli;
/**
 * Customized Solution Approach(CSA)
 * @author marcio
 */
public class CSA extends Heuristic{
    private UAVApproach approach;
    private oCSA csa;
    
    protected double execTime;
    protected double epGap;
    protected int threads;
    private boolean print_war;
    private boolean print_out;
    
    private UAVModel frr;
    private UAVModel frt;
    private UAVModel raa1;
    private UAVModel raa2;
    
    private String status = null;
    private double upper;
    private double lower;
    
    private final uTime elapsed_time = new uTimeMilli();
    
    @Override
    public String name() {
        return "AUV.CSA";
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        approach = link.get(fUAVApproach.obj, approach);
        csa = link.need(oCSA.class, csa);
    }
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        execTime = link.Dbl("Time", 3600.0, 1.0, 180000.0);
        epGap = link.Dbl("Gap Rel", 0.0001, 0.0, 100.0);
        threads = link.Int("Threads", 1, 0, 16);
        
        print_war = link.Bool("warning", false);
        print_out = link.Bool("output", true);
    }

    private double remaining_time(){
        return execTime-elapsed_time.time();
    }
    @Override
    public void execute() throws Exception {
        elapsed_time.start();
        upper = Integer.MAX_VALUE;
        lower = Integer.MIN_VALUE;
        status = "ok";
        
        frr = csa.build_FRR(approach, new CplexExtended());
        frr.solve(remaining_time()/2, epGap, threads, print_war, print_out);
        if (frr.isFeasible()) {
            lower = frr.lower();
            raa1 = csa.build_RAA(approach, new CplexExtended(), frr);
            raa1.solve(remaining_time()/2, epGap, threads, print_war, print_out);
            if (raa1.isFeasible()) {
                upper = raa1.upper();
                //rote = raa1.rote();
            } else {
                frt = csa.build_FRT(approach, new CplexExtended());
                frt.solve(remaining_time()*0.95, epGap, threads, print_war, print_out);
                if (frt.isFeasible()) {
                    upper = frt.upper();
                    raa2 = csa.build_RAA(approach, new CplexExtended(), frt);
                    raa2.solve(remaining_time(), epGap, threads, print_war, print_out);
                    if (raa2.isFeasible()) {
                        upper = raa2.upper();
                        //rote = raa2.rote();
                    } else {
                        status = "fail[3]";
                        System.err.println("Problem fail[3]");
                        upper = Integer.MAX_VALUE;
                    }
                } else {
                    upper = Integer.MAX_VALUE;
                    status = "fail[2]";
                    System.err.println("Problem fail[2]");
                }
            }
        } else {
            lower = Integer.MAX_VALUE;
            System.err.println("Problem fail[1]");
            status = "fail[1]";
        }
    }
    @Override
    public void results(LinkerResults link) throws Exception {
        link.writeString("CSA-status", status);
        link.writeDbl("CSA-upper", upper);
        link.writeDbl("CSA-lower", lower);
        link.writeDbl("CSA-time", elapsed_time.time());
        
        
        if (frr != null && frr.isFeasible()) {
            frr.results(link);
        }
        if (frt != null && frt.isFeasible()) {
            frt.results(link);
        }
        if (raa1 != null && raa1.isFeasible()) {
            raa1.results(link);
            raa1.save();
        }
        if (raa2 != null && raa2.isFeasible()) {
            raa2.results(link);
            raa2.save();
        }
    }
}
