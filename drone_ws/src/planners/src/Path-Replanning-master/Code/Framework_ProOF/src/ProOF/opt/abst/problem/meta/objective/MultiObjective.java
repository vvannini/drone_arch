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
public abstract class MultiObjective<
        Prob extends Problem, Codif extends Codification, Obj extends MultiObjective
> extends Objective<Prob, Codif, Obj> {
    
    private final BoundDbl bounds[];
    protected final double values[];
    
    
    public MultiObjective(final int nObjectives) throws Exception {
        this(nObjectives, BoundDbl.defalt);
    }
    public MultiObjective(final int nObjectives, BoundDbl bounds) throws Exception {
        this.values = new double[nObjectives];
        this.bounds = new BoundDbl[nObjectives];
        for(int i=0; i<nObjectives; i++){
            this.bounds[i] = bounds;
            this.values[i] = bounds.ub;
        }
    }
    public MultiObjective(BoundDbl ...bounds) throws Exception {
        this.values = new double[bounds.length];
        this.bounds = bounds;
        for(int i=0; i<bounds.length; i++){
            this.values[i] = bounds[i].ub;
        }
    }
    public final int goals(){
        return this.values.length;
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
     * @param i
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
    public void printer(Prob prob, StreamPrinter stream, Codif codif) throws Exception {
        for(int i=0; i<values.length; i++){
            stream.printDbl(bounds[i].name, values[i]);
        }
    }
    @Override
    public void results(Prob prob, LinkerResults link, Codif codif) throws Exception {
        for(int i=0; i<values.length; i++){
            link.writeDbl(bounds[i].name, values[i]);
        }
    }

    public int compareTo(int i, Obj other){
        return bounds[i].compareTo(this.abs_value(i), other.abs_value(i));
    }
    @Override
    public int compareTo(Obj other) {
        int cont_this = 0;
        int cont_other = 0;
        for(int i=0; i<this.values.length; i++){
            int cp = compareTo(i, other);
            if( cp < 0 ){
                cont_this++;
            }else if( cp > 0 ){
                cont_other++;
            }
        }
	return cont_this != 0 && cont_other != 0 ? 0 : cont_other - cont_this;
    }
    @Override
    public double compareToAbs(Obj other) {
        double int_this = 0;
        double int_other = 0;
        double dbl_this = 0;
        double dbl_other = 0;
        for(int i=0; i<this.values.length; i++){
            int cp = compareTo(i, other);
            if( cp < 0 ){
                int_this++;
                dbl_this += (other.abs_value(i) - this.abs_value(i));
            }else if( cp > 0 ){
                int_other++;
                dbl_other += (this.abs_value(i) - other.abs_value(i));
            }
        }
        if(int_this != 0 && int_other != 0){
            return 0;
        }else if(int_this > int_other){
            return dbl_this;
        }else if(int_this < int_other){
            return -dbl_other;
        }else{
            return dbl_this - dbl_other;
        }
    }
    @Override
    public double compareToRel(Obj other) {
        double int_this = 0;
        double int_other = 0;
        double dbl_this = 0;
        double dbl_other = 0;
        for(int i=0; i<this.values.length; i++){
            int cp = compareTo(i, other);
            if( cp < 0 ){
                int_this++;
                dbl_this += (other.rel_value(i) - this.rel_value(i));
            }else if( cp > 0 ){
                int_other++;
                dbl_other += (this.rel_value(i) - other.rel_value(i));
            }
        }
        if(int_this != 0 && int_other != 0){
            return 0;
        }else if(int_this > int_other){
            return dbl_this;
        }else if(int_this < int_other){
            return -dbl_other;
        }else{
            return dbl_this - dbl_other;
        }
    }
    
    
    @Override
    public String toString() {
        String s = String.format("%g", values[0]);
        for(int i=1; i<values.length; i++){
            s += String.format(",%g", values[i]);
        }
        return s;
    }
    
}
