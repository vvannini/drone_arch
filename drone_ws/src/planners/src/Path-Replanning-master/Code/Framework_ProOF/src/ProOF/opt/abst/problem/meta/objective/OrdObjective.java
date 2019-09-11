package ProOF.opt.abst.problem.meta.objective;

import ProOF.com.Linker.LinkerResults;
import ProOF.com.Stream.StreamPrinter;
import ProOF.opt.abst.problem.meta.Objective;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.codification.Codification;


/**
 *
 * @author Márcio
 */
public abstract class OrdObjective<
        Prob extends Problem, Codif extends Codification<Prob,Codif>, Obj extends OrdObjective
> extends Objective<Prob, Codif, Obj> {
    
    private final BoundDbl bounds[];
    protected final double values[];
    
    
    public OrdObjective(final int nOrds) throws Exception {
        this(nOrds, BoundDbl.defalt);
    }
    public OrdObjective(final int nOrds, BoundDbl bounds) throws Exception {
        this.values = new double[nOrds];
        this.bounds = new BoundDbl[nOrds];
        for(int i=0; i<nOrds; i++){
            this.bounds[i] = bounds;
            this.values[i] = bounds.ub;
        }
    }
    public OrdObjective(BoundDbl ...bounds) throws Exception {
        this.values = new double[bounds.length];
        this.bounds = bounds;
        for(int i=0; i<bounds.length; i++){
            this.values[i] = bounds[i].ub;
        }
    }
    /**
     * @return the bound valid for each value this objective
     */
    public BoundDbl bound(int i) {
        return bounds[i];
    }
    /**
     * @return the bounds vector valid each value this objective 
     */
    public BoundDbl[] bounds() {
        return bounds;
    }
    
    /**
     * @return the absolute value cost for i-th value this objective, interval [lb(i) , ub(i)]
     */
    public final double abs_value(int i){
        return values[i];
    }
    /**
     * @return the relative cost for i-th value this objective, interval [0 , 1]
     */
    public final double rel_value(int i){
        return bounds[i].relative(values[i]);
    }
    /**
     * set the value for each term this objective in absolute value
     * interval [lb(i), ub(i)]
     * @param value
     */
    protected final void set(int i, double value){
        this.values[i] = bounds[i].valid(value);
    }

    @Override
    public void start() throws Exception {
        for(int i=0; i<values.length; i++){
            values[i] = bounds[i].ub;
        }
    }
    @Override
    public void copy(Prob prob, Obj source) throws Exception{
        System.arraycopy(source.values, 0, this.values, 0, values.length);
    }    
    @Override
    public int compareTo(Obj other) {
        for(int i=this.values.length-1; i>=0; i--){
            int cp = bounds[i].compareTo(this.abs_value(i), other.abs_value(i));
            if( cp < 0 ){
                return -1;
            }else if( cp > 0 ){
                return +1;
            }
        }
	return 0;
    }
    @Override
    public double compareToAbs(Obj other) {
        for(int i=this.values.length-1; i>=0; i--){
            int cp = bounds[i].compareTo(this.abs_value(i), other.abs_value(i));
            if(cp != 0){
                return this.abs_value(i) - other.abs_value(i);
            }
        }
        return 0;
    }
    @Override
    public double compareToRel(Obj other) {
        for(int i=this.values.length-1; i>=0; i--){
            int cp = bounds[i].compareTo(this.abs_value(i), other.abs_value(i));
            if(cp != 0){
                return this.rel_value(i) - other.rel_value(i);
            }
        }
        return 0;
    }
    @Override
    public String toString() {
        String s = String.format("%g", values[0]);
        for(int i=1; i<values.length; i++){
            s += String.format(",%g", values[i]);
        }
        return s;
    }
    @Override
    public void printer(Prob prob, StreamPrinter stream, Codif codif) throws Exception {
        for(int i=0; i<values.length; i++){
            stream.printDbl("objective["+i+"]", values[i]);
        }
    }
    @Override
    public void results(Prob prob, LinkerResults link, Codif codif) throws Exception {
        for(int i=0; i<values.length; i++){
            link.writeDbl("objective["+i+"]", values[i]);
        }
    }
}
