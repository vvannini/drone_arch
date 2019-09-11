/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.method.BranchAndBound;

import ProOF.apl.pog.method.BranchAndBound.aOptimalProblem.Lower;
import ProOF.apl.pog.method.BranchAndBound.aOptimalProblem.Node;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.language.Run;
import java.util.LinkedList;
import java.util.Locale;
import java.util.TreeSet;

/**
 *
 * @author marcio
 */
public class BranchAndBound extends Run {
    private final TreeSet<Node> tree = new TreeSet<Node>();
    private final TreeSet<Lower> tree_lower = new TreeSet<Lower>();
    //private final TreeSet<Upper> tree_upper = new TreeSet<Upper>();
    private double uper_bound;
    private double lower_bound;
    
    private aOptimalProblem problem;
    
    @Override
    public String name() {
        return "Branch & Bound";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        problem = link.get(fOptimalProblem.obj, problem);
    }
    
    private int width_lim;
    private double greedy_factor;
    private double death_factor;
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        width_lim = link.Int("Width-Lim", 1000, 10, 1000000000);
        greedy_factor = link.Dbl("Greedy-Factor", 2.0, 1, 10000);
        death_factor = link.Dbl("Death-Factor",   0.5, 0, 1);
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
        uper_bound  = +1e9;
        lower_bound = +1e9;
        IntSol = 0;
        TotalCuts = 0;
        ArvCut = 0;
        mem_tot = 0;
        best_integer = null;
    }
    
    private int mode;
    private long IntSol;
    
    private long TotalCuts;
    private long ArvCut;
    private long mem_tot;
    private long inital_time;
    private Node best_integer;
    private boolean add(Node node) throws Exception{
        if(node.is_integer()){
            IntSol++;
            if(node.cur_cost < uper_bound){
                best_integer = node;
                uper_bound = node.cur_cost;
                tree_cuts();
                printBest("*");
            }
        }else if(node.lower < uper_bound){
            tree.add(node);
            tree_lower.add(new Lower(node));
            if(node.lower < lower_bound){
                lower_bound = node.lower;
                printBest("-");
            }
            mem_tot += node.mem_bytes();
            return true;
        }else{
            TotalCuts++;
        }
        return false;
    }
    private void remove(Node c) throws Exception {
        tree.remove(c);
        tree_lower.remove(c.ptr);
        ArvCut++;
        
        double min = c.lower;
        if(!tree.isEmpty()){
            min = Math.min(min, tree_lower.first().node.lower);
        }
        if(min>lower_bound){
            lower_bound = min;
            printBest("-");
        }
        if(last()>5){
            printBest(".");
        }
    }

    private void tree_cuts() {
        //cortando nos da arvore
	if(!tree.isEmpty()){
            Lower max = tree_lower.last();
            while(!tree.isEmpty() && max.node.lower >= uper_bound){
                //remove 
                tree.remove(max.node);
                tree_lower.remove(max);
                ArvCut++;
                
                max = tree_lower.last();
            }
        }
        
    }
    
    private Node remove_lower() throws Exception{
        Lower lower = tree_lower.pollFirst();
        tree.remove(lower.node);
        double min = lower.node.lower;
        if(min>lower_bound){
            lower_bound = min;
            printBest("-");
        }
        if(last()>5){
            printBest(".");
        }
        return lower.node;
    }
    
    private Node remove_width() throws Exception{
        Node node = tree.pollFirst();
        tree_lower.remove(node.ptr);
        double min = node.lower;
        if(!tree.isEmpty()){
            min = Math.min(min, tree_lower.first().node.lower);
        }
        if(min>lower_bound){
            lower_bound = min;
            printBest("-");
        }
        if(last()>5){
            printBest(".");
        }
        return node;
    }
    private Node remove_depth() throws Exception{
        Node node = tree.pollLast();
        tree_lower.remove(node.ptr);
        double min = node.lower;
        if(!tree.isEmpty()){
            min = Math.min(min, tree_lower.first().node.lower);
        }
        if(min>lower_bound){
            lower_bound = min;
            printBest("-");
        }
        if(last()>5){
            printBest(".");
        }
        return node;
    }
    
    private void printTitle(){
        inital_time = System.nanoTime();
        System.out.printf("%4s %11s %11s %7s %11s %11s %9s %9s %6s %7s %10s %10s %7s %7s %7s %7s\n", "Mark",
                 "UpperB:", "LowerB:", "IntSol:", "Total Nodes:", "Total Cuts:",
                 "ArvCut:", "Tree:", "Mode:", "time:", "mem tot:", "mem tree:", "gap(%):", "lv-min", "lv-avg", "lv-std");
    }
    
    private double lv_min(){
        int min = Integer.MAX_VALUE;
        for(Node n : tree){
            min = Math.min(min, n.level);
        }
        return tree.isEmpty() ?  0 : min;
    }
    private double lv_avg(){
        double avg = 0;
        for(Node n : tree){
            avg += n.level;
        }
        return tree.isEmpty() ?  0 : avg/tree.size();
    }
    private double lv_std(double avg){
        double std = 0;
        for(Node n : tree){
            std += (n.level-avg)*(n.level-avg);
        }
        return tree.isEmpty() ?  0 : Math.sqrt(std/tree.size());
    }
    
    private long last_print;
    private void printBest(String mark) throws Exception{
        if(mark.equals("-") && last()<2){
            return;
        }
        last_print = System.nanoTime();
        double gap  = (uper_bound-lower_bound)*100/(Math.abs(uper_bound)+1e-10);
        double tree_mem = tree.isEmpty()? 0 :(tree.size() * tree.first().mem_bytes())/1048576.0; 
        System.out.printf(Locale.ENGLISH, "%4s %11g %11g %7d %11d %11d %9d %9d %6s %7.3f %10.2f %10.2f %7.3f %7.3f %7.3f %7.3f\n", mark,
                 uper_bound, lower_bound, IntSol, problem.total_nodes(), TotalCuts,
                 ArvCut, tree.size(),   (mode==0 ? "Heur" : 
                                        (mode==1 ? "Width" :
                                        (mode==2 ? "Depth" : "Greedy"))),
                 time(), mem_tot/1048576.0, tree_mem, gap, lv_min(), lv_avg(), lv_std(lv_avg()));
    }
    private double time(){
        return (System.nanoTime() - inital_time)/1e9;
    }
    private double last(){
        return (System.nanoTime() - last_print)/1e9;
    }
    
    
    private int rec_lim = 1000000;
    private int n_rec = 0;
    
    @Override
    public void execute() throws Exception {
        printTitle();
        mode = 0;
        System.out.println("----------------------- processing heuristic solution ----------------------");
        
        
        System.out.println("------------------------- processing branch & bound ------------------------");
        add(problem.frist_node(uper_bound));
        
        
        while(!tree.isEmpty()){
            
                mode = 3;
                printBest("G");
                //System.out.println("mode-3");
                
                n_rec = 0;
                while(!tree.isEmpty() && n_rec<rec_lim){
                    //System.out.println(tree);
                    Node node = remove_width();
                    //System.out.println("width : "+node);
                    rec(node);
                }
            //do{
                mode = 1;
                printBest("W");
                while(!tree.isEmpty() && tree.size()<width_lim){
                    //System.out.println(tree);
                    Node node = remove_lower();
                    //System.out.println("width : "+node);
                    if(node.lower < uper_bound){
                        for(Node c : node.childs(uper_bound)){
                            add(c);
                        }
                    }else{
                        ArvCut++;
                    }
                }
                mode = 2;
                //System.out.println("mode-1");
                printBest("D");
                while(!tree.isEmpty() && width_lim*death_factor<tree.size() && tree.size()<width_lim/death_factor){
                    //System.out.println(tree);
                    Node node = remove_width();
                    //System.out.println("width : "+node);
                    if(node.lower < uper_bound){
                        for(Node c : node.childs(uper_bound)){
                            add(c);
                        }
                    }else{
                        ArvCut++;
                    }
                }

                
            //}while(!tree.isEmpty() && width_lim/greedy_factor > tree.size());
            /*
            mode = 2;
            printBest("D");
            //System.out.println("mode-2");
            while(!tree.isEmpty() && Math.min(width_lim*death_factor, width_lim/greedy_factor)<tree.size() && tree.size()<width_lim*greedy_factor/death_factor ){
                Node node = remove_depth();
                
                if(node.lower < uper_bound){
                    for(Node c : node.childs()){
                        add(c);
                    }
                }else{
                    ArvCut++;
                }    
            }
            
            if(!tree.isEmpty() && tree.size()>width_lim*death_factor){
                mode = 3;
                printBest("G-2");
                //System.out.println("mode-3");
                while(!tree.isEmpty() && tree.size()>width_lim*death_factor){
                    //System.out.println(tree);
                    Node node = remove_width();
                    //System.out.println("width : "+node);
                    rec(node);
                }
            }*/
                
        }
        printBest("End");
        
        problem.call_end(best_integer);
        
    }
    
    private boolean rec(Node node) throws Exception {
        n_rec++;
        if(last()>5){
            printBest(".");
        }
        if(node.lower < uper_bound){
            LinkedList<Node> list = new LinkedList<Node>();
            for(Node c : ordenate(node.childs(uper_bound))){
                if(add(c)){
                    list.addLast(c);
                }
            }
            if(width_lim/greedy_factor<tree.size() && tree.size()<width_lim*greedy_factor && n_rec<rec_lim){//tree.size()<width_lim*greedy_factor && tree.size()>width_lim/greedy_factor
                boolean flag = true;
                for(Node c : list){
                    if(rec(c)){
                        remove(c);
                    }else{
                        flag = false;
                    }
                }
                return flag;
            }else{
                return false;
            }
        }else{
            ArvCut++;
            return true;
        }
    }
    private Node[] ordenate(Node[] childs) {
        for(int i=1; i<childs.length; i++){
            Node aux = childs[i];
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
