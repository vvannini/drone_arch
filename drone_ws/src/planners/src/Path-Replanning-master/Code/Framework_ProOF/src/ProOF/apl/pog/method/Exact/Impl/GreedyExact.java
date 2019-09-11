/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.method.Exact.Impl;

import ProOF.apl.factorys.fStop;
import ProOF.apl.pog.method.Exact.ExactNode;
import ProOF.apl.pog.method.Exact.aComb;
import ProOF.apl.factorys.fBranchProblem;
import ProOF.apl.pog.method.Exact.oExpand;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.language.Run;
import ProOF.gen.stopping.Stop;

/**
 *
 * @author marcio
 */
public class GreedyExact extends Run {

    private Stop stop;
    private aComb problem;
    private oExpand expand;
    
    @Override
    public String name() {
        return "Greedy Exact";
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
    }
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        
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
        
    }
    
    @Override
    public void execute() throws Exception {
        ExactNode node = problem.frist_node();
        recursive(node);
    }
    private void recursive(ExactNode node) throws Exception{
        if(!node.is_integer(problem) && !stop.end()){
            ExactNode[] list = expand.expand(problem, node);
            for(ExactNode no : ordenate(list)){
                recursive(no);
            }
        }
    }
    private ExactNode[] ordenate(ExactNode[] childs) {
        for(int i=1; i<childs.length; i++){
            ExactNode aux = childs[i];
            int j = i-1;
            while(j>=0 && childs[j].compareTo(aux)>0){
                childs[j+1] = childs[j];
                j--;
            }
            childs[j+1] = aux;
        }
        return childs;
    }
    
    @Override
    public void results(LinkerResults link) throws Exception {
        
    }
}
