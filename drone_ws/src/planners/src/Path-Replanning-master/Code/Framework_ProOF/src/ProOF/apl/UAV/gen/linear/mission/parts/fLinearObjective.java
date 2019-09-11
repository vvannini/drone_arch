/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.gen.linear.mission.parts;

import ProOF.apl.UAV.gen.linear.LinearApproach;
import ProOF.apl.UAV.gen.linear.LinearModel;
import ProOF.com.language.Factory;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;

/**
 *
 * @author marcio
 */
public class fLinearObjective extends Factory<oLinearObjective>{
    public static final fLinearObjective obj = new fLinearObjective();
    @Override
    public String name() {
        return "Objective";
    }
    @Override
    public oLinearObjective build(int index) {  //build the operators
        switch(index){
            case 0: return new Norm1Ut();
            case 1: return new Norm2Ut2D();
            case 2: return new ScalProdUtAprox();  
            case 3: return new ScalProdUtCplex();
        }
        return null;
    }
    private class Norm1Ut extends oLinearObjective<LinearApproach, LinearModel>{
        @Override
        public String name() {
            return "|u|";
        }
        @Override
        public void addObjective(LinearApproach approach, LinearModel model) throws Exception {
            IloNumExpr obj = null;
            for (int t = 0; t < approach.Waypoints(); t++) {
                obj = model.cplex.SumNumNorm1(obj, "|u|", approach.maxControl(), model.controls[t].u);
            }
            model.cplex.addMinimize(obj);
        }
    }
    private class Norm2Ut2D extends oLinearObjective<LinearApproach, LinearModel>{
        @Override
        public String name() {
            return "aprox sqrt{u*u]";
        }
        @Override
        public void addObjective(LinearApproach approach, LinearModel model) throws Exception {
            IloNumVar UtNorm[][] = model.cplex.numVarArray(approach.Waypoints(), approach.N(), 0, Double.POSITIVE_INFINITY, "Ut.norm");
            //------------------------ (NOM-2)  -----------------------------
            for (int t = 0; t < approach.Waypoints(); t++) {
                for (int j = 0; j < approach.N(); j++) {
                    model.cplex.addGe(UtNorm[t][j], model.cplex.prod(+1, model.controls[t].u[j]));
                    model.cplex.addGe(UtNorm[t][j], model.cplex.prod(-1, model.controls[t].u[j]));
                }
            }
            IloNumExpr obj = null;
            for (int t = 0; t < approach.Waypoints(); t++) {
                for (int j = 0; j < approach.N(); j++) {
                    obj = model.cplex.SumNumNorm2_2D(obj, "‖ux+uy‖", 32, approach.maxControl(), model.controls[t].u[0], model.controls[t].u[1]);
                }
                if(approach.N()==3){
                    obj = model.cplex.SumNumNorm1(obj, "|uz|", approach.maxControl(), model.controls[t].u[2]);
                }
            }
            model.cplex.addMinimize(obj);
        }
    }
    
    private class ScalProdUtAprox extends oLinearObjective<LinearApproach, LinearModel>{
        @Override
        public String name() {
            return "aprox{ u*u }";
        }
        @Override
        public void addObjective(LinearApproach approach, LinearModel model) throws Exception {
            IloNumExpr obj = null;
            for (int t = 0; t < approach.Waypoints(); t++) {
                for (int j = 0; j < approach.N(); j++) {
                    obj = model.cplex.SumNumScalProd(obj, "u*u", 32, approach.maxControl(), model.controls[t].u);
                }
            }
            model.cplex.addMinimize(obj);
        }
    }
    
    private class ScalProdUtCplex extends oLinearObjective<LinearApproach, LinearModel>{
        @Override
        public String name() {
            return "cplex{ u*u }";
        }
        @Override
        public void addObjective(LinearApproach approach, LinearModel model) throws Exception {
            IloNumExpr obj = null;
            for (int t = 0; t < approach.Waypoints(); t++) {
                for (int j = 0; j < approach.N(); j++) {
                    obj = model.cplex.Sum(obj, model.cplex.scalProd(model.controls[t].u, model.controls[t].u));
                }
            }
            model.cplex.addMinimize(obj);
        }
    }
}
