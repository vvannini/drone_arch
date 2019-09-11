/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.advanced2.FMS.RFFO;

import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.cplex.IloCplex;

/**
 *
 * @author marcio
 */
public class RelaxVar {
    public final IloNumVar var;
    public final IloNumVar extra[];
    private final IloNumVarType var_type;
    private final IloNumVarType extra_types[];

    public RelaxVar(IloNumVarType all_types, IloNumVar var, IloNumVar ...extra) {
        this.var = var;
        this.extra = extra;
        this.var_type = all_types;
        if(extra!=null && extra.length>0){
            this.extra_types = new IloNumVarType[extra.length];
            for(int i=0; i<extra_types.length; i++){
                extra_types[i] = all_types;
            }
        }else{
            this.extra_types = null;
        }
    }
    public RelaxVar(IloNumVarType var_type, IloNumVar var, IloNumVarType extra_type, IloNumVar ...extra) {
        this.var = var;
        this.extra = extra;
        this.var_type = var_type;
        this.extra_types = new IloNumVarType[extra.length];
        for(int i=0; i<extra.length; i++){
            extra_types[i] = extra_type;
        }
    }
    public RelaxVar(IloNumVarType var_type, IloNumVar var, IloNumVarType extra_type[], IloNumVar extra[]) {
        if(extra.length != extra_type.length){
            throw new IllegalArgumentException("extra.length["+extra.length+"] != extra_type.length["+extra_type.length+"]");
        }
        this.var = var;
        this.extra = extra;
        this.var_type = var_type;
        this.extra_types = extra_type;
    }
    
    public void convert(IloCplex cpx) throws IloException {
        cpx.add(cpx.conversion(var, var_type));
        if(extra!=null){
            for(int i=0; i<extra.length; i++){
                cpx.add(cpx.conversion(extra[i], extra_types[i]));
            }
        }
    }
}
