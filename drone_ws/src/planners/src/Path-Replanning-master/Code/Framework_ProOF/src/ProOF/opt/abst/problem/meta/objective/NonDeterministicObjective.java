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
public abstract class NonDeterministicObjective<
        Prob extends Problem,
        Codif extends Codification<Prob,Codif>,
        Obj extends NonDeterministicObjective
> extends Objective<Prob, Codif, Obj>{

    private final BoundDbl bound;
    
    private double average_value;
    private double standard_deviation;
    private int number_evaluations;

    public NonDeterministicObjective() throws Exception {
        this(BoundDbl.defalt);
    }
    public NonDeterministicObjective(BoundDbl bound) throws Exception {
        this.bound = bound;
        average_value = 0;
        standard_deviation = 0;
        number_evaluations = 0;
    }

    /**
     * @return the bound valid for the absolute value this objective 
     */
    public BoundDbl bound() {
        return bound;
    }
    /**
     * @return the average absolute value cost this objective, interval [lb , ub]
     */
    public final double abs_avg(){
        return average_value;
    }
    /**
     * @return the standard deviation absolute value cost this objective, interval [lb , ub]
     */
    public final double abs_sd(){
        return standard_deviation;
    }
    /**
     * @return the number of evaluations this objective, interval [0 .. infinity]
     */
    public final int num_eval(){
        return number_evaluations;
    }
    /**
     * @return the average relative value cost this objective, interval [0 , 1]
     */
    public final double rel_avg(){
        return bound.relative(average_value);
    }
    /**
     * @return the standard deviation relative value cost this objective, interval [0 , 1]
     */
    public final double rel_sd(){
        return bound.relative(standard_deviation);
    }
    
    /**
     * add more one value in this objective in absolute terms
     * interval [lb, ub]
     * @param value
     */
    protected final void add(double value){
        value = bound.valid(value);
        average_value = (average_value*number_evaluations + value)/(number_evaluations+1);
        //standard_deviation = ???? pesquisar
        number_evaluations++;
    }
    

    @Override
    public void evaluate(Prob prob, Codif codification) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void start() throws Exception {
        average_value = 0;
        standard_deviation = 0;
        number_evaluations = 0;
    }
    @Override
    public void copy(Prob prob, Obj source) throws Exception{
        this.average_value = source.abs_avg();
        this.standard_deviation = source.abs_sd();
        this.number_evaluations = source.num_eval();
    }

    @Override
    public String toString() {
        return String.format("%g,%g,%d", average_value, standard_deviation, number_evaluations);
    }
    
    @Override
    public int compareTo(Obj other) {
        throw new UnsupportedOperationException("Not supported yet.");
        /*if(this.num_eval()<2 || other.num_eval()<2){
            return -199;//???
        }else if(this.abs_avg()-this.abs_sd()>other.abs_avg()+other.abs_sd()){
            return +1;
        }else if(this.abs_avg()+this.abs_sd()<other.abs_avg()-other.abs_sd()){
            return -1;
        }else{
            return 0;
        }*/
    }
    
    @Override
    public double compareToAbs(Obj other) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double compareToRel(Obj other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    @Override
    public void printer(Prob prob, StreamPrinter stream, Codif codif) throws Exception {
        stream.printDbl("obj-avg", average_value);
        stream.printDbl("obj-count", number_evaluations);
        stream.printDbl("obj-sd", standard_deviation);
    }
    @Override
    public void results(Prob prob, LinkerResults link, Codif codif) throws Exception {
        link.writeDbl("obj-avg", average_value);
        link.writeDbl("obj-count", number_evaluations);
        link.writeDbl("obj-sd", standard_deviation);
    }
    
}
