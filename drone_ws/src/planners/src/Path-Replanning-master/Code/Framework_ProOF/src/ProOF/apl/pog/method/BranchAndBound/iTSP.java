/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.method.BranchAndBound;

import ProOF.apl.pog.problem.TSP.TSPInstance;
import ProOF.com.Linker.LinkerApproaches;
import java.util.LinkedList;

/**
 *
 * @author marcio
 */
public class iTSP extends aOptimalProblem{
    //-----------------------------------[TSP]---------------------------------------
    private TSPInstance inst = new TSPInstance();

    @Override
    public String name() {
        return "TSP";
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        link.add(inst);
    }
    
    private static int frist = -1;

    
    private class City extends aOptimalProblem.Node<City, Integer> {
        
        private int frist_city(){
            if(frist<0){
                City aux = this;
                while(aux!=null){
                    frist = aux.data;
                    aux = aux.back;
                }
            }
            return frist;
        }
        
        public City(City back, int city, double uper_bound) throws Exception {
            super(back, city, uper_bound);
        }
        @Override
        protected boolean is_integer() {
            return level+1>=inst.N;
        }
        @Override
        protected double partial_cost() {
            int a = back.data;
            int b = this.data;
            return back.cur_cost + inst.Cij[a][b];
        }
        @Override
        protected double integer_cost() {
            int a = back.data;
            int b = this.data;
            int c = frist_city();
            return back.cur_cost + inst.Cij[a][b] + inst.Cij[b][c];
        }
        @Override
        protected double lower_bound(double uper_bound) {
            //return cur_cost;
            double lower = inst.adj_i[data][0];
            for(int a=0; a<inst.N; a++){
                if(!contains(a)){
                    lower += inst.adj_i[a][0];
                }
            }
            return cur_cost + lower;
            
        }
        @Override
        protected int mem_bytes() {
            return mem_base() + 4;
        }

        @Override
        protected City[] childs(double uper_bound) throws Exception{
            LinkedList<City> list = new LinkedList<City>();
            for(int i=0; i<inst.N; i++){
                if(!contains(i)){
                    list.addLast(new City(this, i, uper_bound));
                }                
            }
            return list.toArray(new City[list.size()]);
        }
    }
    @Override
    public Node frist_node(double uper_bound) throws Exception{
        return new City(null, 0, uper_bound);
    }
    @Override
    public void call_end(Node best_integer) {
        Node aux = best_integer;
        while(aux!=null){
            System.out.printf("%s <- ", aux.data);
            aux = aux.back;
        }
        System.out.println();
        
        aux = best_integer;
        while(aux!=null && aux.back!=null){
            System.out.printf("%g <- ", inst.Cij[(Integer)(aux.back.data)][(Integer)(aux.data)]);
            aux = aux.back;
        }
        System.out.printf("%g <- ", inst.Cij[(Integer)(best_integer.data)][(Integer)(aux.data)]);
        System.out.println();
        
        aux = best_integer;
        while(aux!=null){
            System.out.printf("%g <- ", aux.lower);
            aux = aux.back;
        }
        System.out.println();
    }
}
