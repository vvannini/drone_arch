/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.method.CustomizedApproach.Obstacle;

import ProOF.apl.pog.method.CustomizedApproach.AddRestrictions;
import ProOF.apl.pog.method.CustomizedApproach.Model;
import ProOF.apl.pog.problem.PPDCP.PPDCPInstance;
import ProOF.CplexExtended.CplexExtended;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import java.util.LinkedList;

/**
 *
 * @author marcio
 */
public class REST_empty extends Shared implements AddRestrictions<REST_empty> {
    public REST_empty(PPDCPInstance inst) {
        super(inst);
    }
    
    private IloIntVar[][][] Zjti = null;
    private IloNumVar[][] Djt = null;
    private int ID = -1;

    @Override
    public void fix(CplexExtended cplex, REST_empty tofix) throws IloException{
        System.out.println("REST_empty fix Zjti");
        //fix_Zjti(cplex, Zjti, Zjti(cplex, tofix.Zjti));
        fix_Zjti(Zjti, tofix.Zjti(cplex, tofix.Zjti));
    }
    @Override
    public IloNumExpr addRestrictions(CplexExtended cplex, IloNumVar[][] Ut, IloNumVar[][] MUt, int ID, boolean tofix) throws Exception {
        this.ID = ID;
        Zjti = create_Zjti(cplex);
        if(tofix){
            
        }else{
            addSumZjti_1(cplex, Zjti);
        }

        IloNumExpr obj = null;
        switch(ID){
            case ID_RELAXATION: ObstacleAvoidanceFix(cplex, MUt, Zjti, inst.DELTA); break;
            case ID_UPERBOUND:  ObstacleAvoidanceFix(cplex, MUt, Zjti, inst.DELTA / (inst.J * (inst.T + 1))); break;
            case ID_VARIABLE:
                Djt = create_Djt(cplex, inst.DN);
                obj = ObstacleAvoidanceFree(cplex, MUt, Zjti, Djt, inst.DN);
                addDelta(cplex, Djt, null, null, null);
                break;
            default: throw new Exception("Optiom ID = "+ID+", not defined");
        }
        return obj;
    }
    @Override
    public IloIntVar[][][] getZjti() throws IloException {
        return Zjti;
    }
    @Override
    public int [][][] getZjti(CplexExtended cplex, IloNumVar[][] MUt, boolean obst[][]) throws Exception{
        int [][][] bZjti = new int[inst.J][inst.T+1][];
        for(int j=0; j<inst.J; j++){
            for(int t=0; t<inst.T+1; t++){
                bZjti[j][t] = new int[inst.G(j)];
            }
        }
        switch(ID){
            case ID_RELAXATION: ObstacleAvoidanceFixZjti(cplex, MUt, bZjti, obst, inst.DELTA); break;
            case ID_UPERBOUND:  ObstacleAvoidanceFixZjti(cplex, MUt, bZjti, obst, inst.DELTA / (inst.J * (inst.T + 1))); break;
            case ID_VARIABLE:   ObstacleAvoidanceFreeZjti(cplex, MUt, bZjti, obst); break;
            default: throw new Exception("Optiom ID = "+ID+", not defined");
        }
        return bZjti;
    }
    @Override
    public LinkedList<IloNumVar> AlocationFree(int t){
        if(ID == ID_RELAXATION || ID == ID_UPERBOUND){
            return null;
        }
        LinkedList<IloNumVar> list = new LinkedList<IloNumVar>();
        for(int j=0; j<inst.J; j++){
            list.addLast(Djt[j][t]);
        }
        return list;
    }
    @Override
    public double AlocationFix(){
        switch(ID){
            case ID_RELAXATION: return inst.DELTA;
            case ID_UPERBOUND:  return inst.DELTA / (inst.J * (inst.T + 1));
            default: return inst.DELTA/Math.pow(2, inst.DN);
        }
    }

    
}
