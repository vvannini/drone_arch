/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.PPCCS;

import ProOF.apl.UAV.PPCCS.data.FunctionObjective;
import ProOF.apl.UAV.PPCCS.data.Limits;
import ProOF.apl.UAV.PPCCS.data.Map;
import ProOF.apl.UAV.PPCCS.data.TypeOfCriticalSituation;
import ProOF.apl.UAV.PPCCS.util.ConfigSimulation;
import ProOF.apl.UAV.map.Hyperplane;
import ProOF.apl.UAV.map.Obstacle;
import ProOF.apl.UAV.map.Obstacle3DHalf;
import ProOF.com.Linker.LinkerResults;
import ProOF.opt.abst.problem.Instance;
import java.awt.Rectangle;
import java.util.LinkedList;

/**
 *
 * @author marcio e jesimar
 */
public abstract class PPCCSInstance extends Instance{
    
    /**
     * data of map
     */
    public Map map = new Map();
    
    /**
     * limits of velocity and acceleration of uav
     */
    public Limits limits = new Limits();
    
    public TypeOfCriticalSituation TYPE_OF_FAILURE;
    
    public FunctionObjective func = ConfigSimulation.FUNCTION;
    
    /**
     * initial state (px py ... vx vy ...)
     */
    public double start_state[];
    
    public final int Sp = 32;
    public double deltaP;
    
    public Hyperplane[] hi;
    public Integer[] xPlus;
    public Integer[] xMinus;
    public Integer[] uPlus;
    public Obstacle[] obstacles;
    
    public Integer[] Φb;
    public Integer[] Φp;
    public Integer[] Φn;
    public Integer[][] Oj;
    
    
    protected LinkedList<Hyperplane> listHyper = new LinkedList<Hyperplane>();
    protected LinkedList<Hyperplane> listHyperWing = new LinkedList<Hyperplane>();
    
    protected void addHyper(Hyperplane toAdd, LinkedList<Integer> setIndex){
        setIndex.addLast(listHyper.size());
        listHyper.addLast(toAdd);
    }
    
    protected void addHyperWing(Hyperplane toAdd, LinkedList<Integer> setIndex){
        setIndex.addLast(listHyperWing.size());
        listHyperWing.addLast(toAdd);
    }
    
    public PPCCSInstance(){
        
    }
    
    public void createHyperplans(){
        if (TYPE_OF_FAILURE == TypeOfCriticalSituation.FAIL_ENGINE){
            //Faz com que as velocidades maximas e minimas da aeronave fiquem 
            //dentro do intervalo [23; 25], ou seja, aproximadamente 24m/s
            limits.V_H_MAX = limits.V_H_MAX_m;
            limits.V_H_MIN = limits.V_H_MIN_m;
        }
        
        LinkedList<Integer> setIndexXPlus = new  LinkedList<Integer>();
        for (int i = 0; i < Sp; i++){
            Hyperplane hyperTemp = new Hyperplane(limits.V_H_MAX, 0, 0, 0, 
                    Math.cos(2*Math.PI*i/Sp), 
                    Math.sin(2*Math.PI*i/Sp), 0);
            addHyper(hyperTemp, setIndexXPlus);
        }
        addHyper(new Hyperplane(limits.V_V_MAX, 0, 0, 0, 0, 0, 1), setIndexXPlus);
        addHyper(new Hyperplane(-limits.V_V_MIN, 0, 0, 0, 0, 0, -1), setIndexXPlus);
        xPlus = setIndexXPlus.toArray(new Integer[setIndexXPlus.size()]);        
        
        LinkedList<Integer> setIndexXMinus = new  LinkedList<Integer>();
        for (int i = 0; i < Sp; i++){
            Hyperplane hyperTemp = new Hyperplane(limits.V_H_MIN, 
                    0, 0, 0, 
                    Math.cos(2*Math.PI*i/Sp), 
                    Math.sin(2*Math.PI*i/Sp), 0);
            addHyper(hyperTemp, setIndexXMinus);
        }
        xMinus = setIndexXMinus.toArray(new Integer[setIndexXMinus.size()]);
        
        LinkedList<Integer> setIndexUPlus = new  LinkedList<Integer>();
        for (int i = 0; i < Sp; i++){
            Hyperplane hyperTemp = new Hyperplane(limits.A_H_MAX, 
                    Math.cos(2*Math.PI*i/Sp), 
                    Math.sin(2*Math.PI*i/Sp), 0);
            addHyper(hyperTemp, setIndexUPlus);
        }
        addHyper(new Hyperplane(limits.A_V_MAX, 0, 0, 1), setIndexUPlus);
        addHyper(new Hyperplane(-limits.A_V_MIN, 0, 0, -1), setIndexUPlus);
        uPlus = setIndexUPlus.toArray(new Integer[setIndexUPlus.size()]);                
    }
                    
    public final int J(){
        return obstacles.length;
    }          
    
    public int minP(){
        int j = Φp[0];
        return Oj[j][0];
    }
    
    public final int qtdEdgeZoneP(){
        int qtd = 0;
        for (int j : Φp){
            for (int i : Oj[j]){
                qtd++;
            }
        }
        return qtd;
    }
    
    public final int qtdEdgeZoneB(){
        int qtd = 0;
        for (int j : Φb){
            for (int i : Oj[j]){
                qtd++;
            }
        }
        return qtd;
    }
    
    public final int qtdEdgeZoneN(){
        int qtd = 0;
        for (int j : Φn){
            for (int i : Oj[j]){
                qtd++;
            }
        }
        return qtd;
    }        
    
    public final double Hj(int j){
        if (obstacles[j] instanceof Obstacle3DHalf){
            return ((Obstacle3DHalf)(obstacles[j])).getHeight();
        }else {
            return 0.0;
        }
    }
    
    public final double bigM() {
        return 1e4;
    }    
    
    public abstract int N();
    
    public abstract String map_mame();
    
    public final Rectangle rectangle(){
        Rectangle rect = new Rectangle();
        rect.add(start_state[0], start_state[1]);
        for (Obstacle obstacle : obstacles) {
            if(obstacle.rect!=null){
                rect.add(obstacle.rect);
            }
        }
        return rect;
    }

    @Override
    public void results(LinkerResults link) throws Exception {
        super.results(link);
        if (ConfigSimulation.DEBUG_INSTANCE){
            System.out.println("::::Hi::::");
            for (int i = 0; i < hi.length; i++){
                System.out.printf("[t = %2d][b = %8.2f][a (px, py, pz, vx, vy, vz) = ", i, hi[i].b);
                for (int j = 0; j < hi[i].a.length; j++){
                    System.out.printf("%8.2f", hi[i].a[j]);
                }
                System.out.println("]");
            }
            System.out.println("xPlus");
            for (int i = 0; i < xPlus.length; i++){
                System.out.printf("%4d", xPlus[i]);                
            }
            System.out.println("\nxMinus");
            for (int i = 0; i < xMinus.length; i++){
                System.out.printf("%4d", xMinus[i]);                
            }
            System.out.println("\nuPlus");
            for (int i = 0; i < uPlus.length; i++){
                System.out.printf("%4d", uPlus[i]);                
            }
            System.out.println();
        }
    }
}
