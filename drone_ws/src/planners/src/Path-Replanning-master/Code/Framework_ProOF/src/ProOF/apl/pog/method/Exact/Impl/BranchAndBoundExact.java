/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.method.Exact.Impl;

import ProOF.apl.factorys.fBranchProblem;
import ProOF.apl.factorys.fStop;
import ProOF.apl.pog.method.Exact.ExactNode;
import ProOF.apl.pog.method.Exact.aComb;
import ProOF.apl.pog.method.Exact.oExpand;
import ProOF.apl.pog.method.Exact.oLowerBound;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.language.Run;
import ProOF.gen.stopping.Stop;
import java.util.TreeSet;

/**
 *
 * @author marcio
 */
public class BranchAndBoundExact extends Run {
    
    private Stop stop;
    private aComb problem;
    private oExpand expand;
    private oLowerBound lower;
    
    private double memLim;
    
    private double LB;
    
    @Override
    public String name() {
        return "Branch & Bound Exact";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        problem = link.get(fBranchProblem.obj, problem);
        stop    = link.get(fStop.obj, stop);
        expand = link.need(oExpand.class, expand);
        lower = link.need(oLowerBound.class, lower);
    }
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        memLim = link.Dbl("Mem-Lim (MB)", 5, 1, 2048);
    }
    @Override
    public void load() throws Exception {
        
    }
    @Override
    public boolean validation(LinkerValidations link) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void start() throws Exception {
        LB = 0;
    }
    
    @Override
    public void execute() throws Exception {
        double mem = 0;
        ExactNode node = problem.frist_node();
        
        TreeSet<ExactNode> tree = new TreeSet<ExactNode>();
        tree.add(node);
        mem+=node.mem_bytes();
        
        while(!tree.isEmpty() && !stop.end() && mem<memLim*1048576){
            node = tree.pollFirst();
            
            tree.remove(node);
            mem-=node.mem_bytes();
            
            double lower_bound = lower.lower_bound(problem, node);
            
            if(lower_bound < problem.uper_bound()){
                for(ExactNode no : expand.expand(problem, node)){
                    lower_bound = lower.lower_bound(problem, no);
                    if(lower_bound < problem.uper_bound()){
                        tree.add(no);
                        mem+=node.mem_bytes();
                    }
                }
            }
        }
        if(tree.isEmpty()){
            LB = problem.uper_bound();
        }else{
            LB = Double.MAX_VALUE;
            while(!tree.isEmpty()){
                node = tree.pollFirst();
                tree.remove(node);
                mem-=node.mem_bytes();
                LB = Math.min(LB, lower.lower_bound(problem, node));
            }
        }
    }
    
    @Override
    public void results(LinkerResults link) throws Exception {
        link.writeDbl("lower_bound", LB);
    }
}
