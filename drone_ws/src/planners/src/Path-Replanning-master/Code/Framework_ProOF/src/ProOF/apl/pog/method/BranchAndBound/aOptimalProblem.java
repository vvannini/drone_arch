/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.method.BranchAndBound;

import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerValidations;

/**
 *
 * @author marcio
 */
public abstract class aOptimalProblem extends ProOF.com.language.Approach {
    public abstract Node frist_node(double uper_bound) throws Exception;
    public abstract void call_end(Node best_integer);
    
    public final double total_nodes(){
        return TotalNodes;
    }
    
    private static long cout = 0;
    private long TotalNodes;

    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        
    }
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        
    }
    @Override
    public void load() throws Exception {
        
    }
    @Override
    public void start() throws Exception {
        TotalNodes = 0;
    }
    @Override
    public boolean validation(LinkerValidations link) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void results(LinkerResults link) throws Exception {
        
    }

    
    public abstract class Node <N extends Node, D> implements Comparable<Node>{
        protected final N back;
        protected final D data;
        protected final double cur_cost;
        protected final double lower;
        protected final int level;
        protected final long id;
        
        protected Lower ptr;
        
        public Node(Node back, D data, double uper_bound) throws Exception {
            TotalNodes++;
            
            this.back = (N) back;
            this.data = data;
            this.id = cout++;
            if(back==null){
                this.level = 0;
                this.cur_cost = 0;
                this.lower = lower_bound(uper_bound);
            }else if(back.is_integer()){
                throw new Exception("try create a node by other integer node");
            }else{
                this.level = back.level+1;
                if(is_integer()){
                    this.cur_cost = integer_cost();
                    this.lower = this.cur_cost;
                }else{
                    this.cur_cost = partial_cost();
                    this.lower = lower_bound(uper_bound);
                }
            }
        }
        protected final int mem_base(){
            return 44;
        }
        protected abstract int mem_bytes() throws Exception;;
        protected abstract boolean is_integer() throws Exception;;
        protected abstract double partial_cost() throws Exception;;
        protected abstract double integer_cost() throws Exception;;
        protected abstract double lower_bound(double uper_bound);
        protected abstract N[] childs(double uper_bound) throws Exception;
        
        public final boolean contains(D data){
            Node aux = this;
            while(aux!=null){
                if(aux.data.equals(data)){
                    return true;
                }
                aux = aux.back;
            }
            return false;
        }

        @Override
        public String toString() {
            return String.format("[%d %d %4.0f]", id, level, lower*1.0/level);
        }
        @Override
        public int compareTo(Node o) {
            double v = lower*o.level - o.lower*level;
            return  ( id == o.id ? 0 :
                        (v<0 ? -1 :
                        (v>0 ? +1 :
                            (id<o.id ? -1 :
                            (id>o.id ? +1 :
                                        0 )))));
        }
    }
    
    /*private final class Upper implements Comparable<Lower>{
        private final Node node;
        public Upper(Node node) {
            this.node = node;
        }
        
        @Override
        public int compareTo(Lower o) {
            return  (node.cur_cost < o.node.cur_cost ? -1 :
                    (node.cur_cost > o.node.cur_cost ? +1 :
                                                     0 ));
        }
    }*/
    public static final class Lower implements Comparable<Lower>{
        protected final Node node;
        public Lower(Node node) {
            this.node = node;
            this.node.ptr = this;
        }
        @Override
        public int compareTo(Lower o) {
            double v = node.lower - o.node.lower;
            
            return  ( node.id == o.node.id ? 0 :
                        (v<0 ? -1 :
                        (v>0 ? +1 :
                            (node.id<o.node.id ? -1 :
                            (node.id>o.node.id ? +1 :
                                               0 )))));
        }
        @Override
        public String toString() {
            return String.format("[%d %d %4.0f]", node.id, node.level, node.lower);
        }
    }
    
    
    
}
