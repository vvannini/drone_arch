/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.com.Stream;

/**
 *
 * @author marcio
 */
public abstract class StreamAdapter implements Stream{
    protected final String type;
    protected final String mk_name;
    private boolean isOpen = false;
    public StreamAdapter(String type, String mk_name) throws Exception {
        this.type = type;
        this.mk_name = mk_name;
        open();
    }
    @Override
    public final String type() {
        return type;
    }
    @Override
    public final String name() {
        return mk_name;
    }
    @Override
    public final void open() throws Exception {
        if(isOpen){
            throw new Exception("'"+name()+"' already open");
        }else{
            System.out.printf("#%s$%s$%s\n", "open", type(), name());
            isOpen = true;
        }
    }
    @Override
    public final void close() throws Exception {
        if(isOpen){
            System.out.printf("#%s$%s\n", "close", name());
            isOpen = false;
        }else{
            throw new Exception("'"+name()+"' already close");
        }
    }
    @Override
    public void flush() {

    }
}