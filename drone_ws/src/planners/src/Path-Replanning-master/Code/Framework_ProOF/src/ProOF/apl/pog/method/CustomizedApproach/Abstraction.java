/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.method.CustomizedApproach;

import static ProOF.apl.pog.method.CustomizedApproach.AddRestrictions.ID_RELAXATION;
import static ProOF.apl.pog.method.CustomizedApproach.AddRestrictions.ID_UPERBOUND;
import static ProOF.apl.pog.method.CustomizedApproach.AddRestrictions.ID_VARIABLE;
import ProOF.apl.pog.method.CustomizedApproach.Objectives.Norm1;
import ProOF.apl.pog.method.CustomizedApproach.Objectives.Norm2SQR_Cpx;
import ProOF.apl.pog.method.CustomizedApproach.Objectives.Norm2SQR_aprox32;
import ProOF.apl.pog.method.CustomizedApproach.Objectives.Norm2_aprox32;
import ProOF.apl.pog.method.CustomizedApproach.Obstacle.REST_empty;
import ProOF.apl.pog.method.CustomizedApproach.Obstacle.REST_new;
import ProOF.apl.pog.problem.PPDCP.PPDCPInstance;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.language.Run;

/**
 *
 * @author marcio
 */
public abstract class Abstraction extends Run {

    protected PPDCPInstance inst = new PPDCPInstance();
    protected double execTime;
    protected double epGap;
    protected int threads;

    protected double time;
    protected double rote[][];
    protected String status = null;

    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void services(LinkerApproaches link) throws Exception {
        link.add(inst);
    }

    @Override
    public void parameters(LinkerParameters link) throws Exception {
        execTime = link.Dbl("Time", 3600.0, 1.0, 180000.0);
        epGap = link.Dbl("Gap Rel", 0.0001, 0.0, 100.0);
        threads = link.Int("Threads", 1, 0, 16);
    }

    @Override
    public void load() throws Exception {
    }

    @Override
    public void start() throws Exception {
    }

    @Override
    public boolean validation(LinkerValidations link) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected AddObjective selectOBJ() throws Exception {
        System.out.println("inst.OBJ = "+inst.OBJ);
        switch (inst.OBJ) {
            case 0:
                return new Norm2SQR_Cpx(inst);
            case 1:
                return new Norm1(inst);
            case 2:
                return new Norm2_aprox32(inst);
            case 3:
                return new Norm2SQR_aprox32(inst);
        }
        return null;
    }

    protected AddRestrictions selectREST() throws Exception {
        System.out.println("inst.REST = "+inst.REST);
        switch (inst.REST) {
            case 0:
                return new REST_empty(inst);
            case 1:
                return new REST_new(inst);
        }
        return null;
    }

    protected Model selectFRR(String name) throws Exception {
        return new Model(name, inst, selectOBJ(), ID_RELAXATION, selectREST(), null);
    }
    protected Model selectFRT(String name) throws Exception {
        return new Model(name, inst, selectOBJ(), ID_UPERBOUND, selectREST(), null);
    }
    protected Model selectFRA(String name, Model toFix) throws Exception {
        return new Model(name, inst, selectOBJ(), ID_VARIABLE, selectREST(), toFix);
    }
    protected Model selectModelFull(String name) throws Exception {
        return new Model(name, inst, selectOBJ(), ID_VARIABLE, selectREST(), null);
    }
}
