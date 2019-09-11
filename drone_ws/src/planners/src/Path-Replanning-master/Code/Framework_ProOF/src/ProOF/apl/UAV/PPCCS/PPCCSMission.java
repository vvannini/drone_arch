/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.PPCCS;

import ProOF.apl.UAV.PPCCS.data.FunctionObjective;
import ProOF.apl.UAV.PPCCS.data.TypeOfCriticalSituation;
import ProOF.apl.UAV.Swing.Graphics2DReal;
import ProOF.apl.UAV.gen.linear.mission.oLinearMission;
import ProOF.com.Linker.LinkerResults;
import ilog.concert.IloNumExpr;
import java.awt.Color;

/**
 *
 * @author marcio e jesimar
 */
public class PPCCSMission extends oLinearMission<PPCCSApproach, PPCCSModel> {

    private IloNumExpr objControl = null;
    private IloNumExpr objProbLandB = null;
    private IloNumExpr objBatery = null;
    private PPCCSModel modelo;    

    @Override
    public String name() {
        return "PPCCS";
    }

    @Override
    public void addObjective(PPCCSApproach approach, PPCCSModel model) throws Exception {
        model.PBj = model.cplex.numVarArray(approach.inst.map.sizeB, 0, 1, "PBj");
        model.Ht = model.cplex.boolVarArray(approach.Waypoints() + 1, "Ht");
        if (approach.inst.func == FunctionObjective.FUNCTION_1) {
            for (int t = 0; t < approach.Waypoints(); t++) {
                if (approach.inst.TYPE_OF_FAILURE == TypeOfCriticalSituation.FAIL_BATTERY) {
                    objControl = model.cplex.prod(Math.pow(2, (t - approach.Waypoints() + 1.0) / 10.0),
                            model.cplex.SumNumScalProd(objControl, "obj", approach.inst.Sp,
                                    approach.maxControl(), model.controls[t].u));
                } else {
                    objControl = model.cplex.SumNumScalProd(objControl, "obj", approach.inst.Sp,
                            approach.maxControl(), model.controls[t].u);
                }
            }
            for (int j = 0; j < approach.inst.map.sizeB; j++) {
                objProbLandB = model.cplex.SumProd(objProbLandB, approach.inst.map.Cb, model.PBj[j]);
            }
            if (approach.inst.TYPE_OF_FAILURE == TypeOfCriticalSituation.FAIL_BATTERY) {
                for (int t = 1; t < approach.Waypoints() + 1; t++) {
                    IloNumExpr exp = model.cplex.sum(model.Ht[t - 1], model.cplex.prod(-1, model.Ht[t]));
                    objBatery = model.cplex.SumProd(objBatery, -approach.inst.map.Cb
                            * Math.pow(2, (t - approach.Waypoints() + 1.0) / 10.0), exp);
                }
            }
            IloNumExpr sumObj = model.cplex.sum(objControl, objProbLandB);
            if (approach.inst.TYPE_OF_FAILURE == TypeOfCriticalSituation.FAIL_BATTERY) {
                sumObj = model.cplex.sum(sumObj, objBatery);
            }
            model.cplex.addMinimize(sumObj);
            this.modelo = model;
        } else if (approach.inst.func == FunctionObjective.FUNCTION_2) {
            for (int t = 0; t < approach.Waypoints(); t++) {                
                objControl = model.cplex.prod(Math.pow(2, (t - approach.Waypoints() + 1.0) / 10.0),
                        model.cplex.SumNumScalProd(objControl, "obj", approach.inst.Sp,
                                approach.maxControl(), model.controls[t].u));                
            }
            for (int j = 0; j < approach.inst.map.sizeB; j++) {
                objProbLandB = model.cplex.SumProd(objProbLandB, approach.inst.map.Cb, model.PBj[j]);
            }            
            for (int t = 1; t < approach.Waypoints() + 1; t++) {
                IloNumExpr exp = model.cplex.sum(model.Ht[t - 1], model.cplex.prod(-1, model.Ht[t]));
                objBatery = model.cplex.SumProd(objBatery, -approach.inst.map.Cb
                        * Math.pow(2, (t - approach.Waypoints() + 1.0) / 10.0), exp);
            }

            IloNumExpr sumObj = model.cplex.sum(objControl, objProbLandB);            
            sumObj = model.cplex.sum(sumObj, objBatery);
            model.cplex.addMinimize(sumObj);
            this.modelo = model;
        }
    }

    @Override
    public void addConstraints(PPCCSApproach approach, PPCCSModel model) throws Exception {
        //--------------- Mission path planning from start state ---------------
        int t = 0;
        for (int i = 0; i < approach.N() * 2; i++) {
            model.states[t].x[i].setLB(approach.inst.start_state[i]);
            model.states[t].x[i].setUB(approach.inst.start_state[i]);
        }
    }

    @Override
    public void paint(PPCCSApproach approach, Graphics2DReal gr, double size) throws Exception {
        //start state
        gr.paintOvalR(approach.inst.start_state[0], approach.inst.start_state[1],
                0.01 * size, 0.01 * size, Color.ORANGE, Color.BLACK);
    }

    @Override
    public void results(LinkerResults link) throws Exception {
        super.results(link);
        double control = modelo.cplex.getValue(objControl);
        double probLandB = modelo.cplex.getValue(objProbLandB);
        link.writeDbl("Obj Control", control);
        link.writeDbl("Obj ProbLandB", probLandB);
        if (objBatery != null) {
            double batery = modelo.cplex.getValue(objBatery);
            link.writeDbl("Obj Batery", batery);
        }

    }
}
