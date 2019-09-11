/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.com.language;

import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerValidations;

/**
 *
 * @author marcio
 */
public final class ApproachFactory extends Approach{
    private final Factory factory;
    
    public ApproachFactory(Factory factory) {
        this.factory = factory;
    }
    
    @Override
    public final void services(LinkerApproaches link) throws Exception {
        for(Approach s : factory.split()){
            link.add(s);
            //System.out.printf("************ %s -> %s | %d\n", factory.name(), s.name(), s.getID());
        }
    }
    @Override
    public final void parameters(LinkerParameters link) throws Exception {
        
    }
    @Override
    public final boolean validation(LinkerValidations link) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public final void load() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public final void start() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public final void results(LinkerResults link) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public final String name() {
        return factory.name();
    }
    @Override
    public final String class_name(){
        return factory.class_name(); 
    }
    @Override
    public final String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
