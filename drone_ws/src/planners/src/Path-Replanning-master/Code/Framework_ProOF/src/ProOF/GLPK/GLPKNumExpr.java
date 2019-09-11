/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.GLPK;

import ilog.concert.IloCopyManager;
import ilog.concert.IloCopyable;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;

/**
 *
 * @author marcio
 */
public class GLPKNumExpr implements IloNumExpr{
    @Override
    public IloCopyable makeCopy(IloCopyManager icm) throws IloException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public void needCopy(IloCopyManager.Check check) throws IloCopyManager.Check {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
