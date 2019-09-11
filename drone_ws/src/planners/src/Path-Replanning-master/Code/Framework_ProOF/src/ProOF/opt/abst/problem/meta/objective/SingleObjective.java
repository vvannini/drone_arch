package ProOF.opt.abst.problem.meta.objective;

import ProOF.com.Linker.LinkerResults;
import ProOF.com.Stream.StreamPrinter;
import ProOF.opt.abst.problem.Val;
import ProOF.opt.abst.problem.meta.Objective;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.codification.Codification;


/**
 *
 * @author Márcio
 */
public abstract class SingleObjective<
        Prob extends Problem,
        Codif extends Codification,
        Obj extends SingleObjective
> extends Objective<Prob, Codif, Obj>{
    
    private final BoundDbl bound;
            
    private double value;
    
    public SingleObjective() throws Exception {
        this(BoundDbl.defalt);
    }
    public SingleObjective(BoundDbl bound) throws Exception {
        this.bound = bound;
        this.value = bound.ub;
    }

    /**
     * @return the bound valid for the absolute value this objective 
     */
    public BoundDbl bound() {
        return bound;
    }
    /**
     * @return the absolute value cost this objective, interval [lb , ub]
     */
    public final double abs_value(){
        return value;
    }
    /**
     * @return the relative cost this objective, interval [0 , 1]
     */
    public final double rel_value(){
        return bound.relative(value);
    }
    /**
     * set the value this objective in absolute terms
     * interval [lb, ub]
     * @param value
     */
    protected final void set(double value){
        this.value = bound.valid(value);
    }

    @Override
    public void start() throws Exception {
        value = bound.ub;
    }
    @Override
    public void copy(Prob prob, Obj source) throws Exception{
        this.value = source.abs_value();
    }
    
    @Override
    public int compareTo(Obj other) {
        return bound.compareTo(this.abs_value(), other.abs_value());
    }
    @Override
    public double compareToAbs(Obj other) {
        if(this.compareTo(other)!=0){
            return this.abs_value() - other.abs_value();
        }
        return 0;
    }
    @Override
    public double compareToRel(Obj other) {
        if(this.compareTo(other)!=0){
            return this.rel_value() - other.rel_value();
        }
        return 0;
    }

    @Override
    public String toString() {
        return ""+value;
    }

    @Override
    public void printer(Prob prob, StreamPrinter stream, Codif codif) throws Exception {
        if(prob!=null){
            for(Val v : prob.vals()){
                stream.printDbl(v.title+"(%)", v.percent(value));
            }
        }
        stream.printDbl(bound.name, value);
    }
    @Override
    public void results(Prob prob, LinkerResults link, Codif codif) throws Exception {
        if(prob!=null){
            for(Val v : prob.vals()){
                link.writeDbl(v.title+"(%)", v.percent(value));
            }
        }
        link.writeDbl(bound.name, value);
    }
}
