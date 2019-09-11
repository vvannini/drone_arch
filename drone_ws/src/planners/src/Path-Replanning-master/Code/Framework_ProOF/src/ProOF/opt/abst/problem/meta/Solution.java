package ProOF.opt.abst.problem.meta;

import ProOF.com.Stream.StreamPrinter;
import ProOF.opt.abst.problem.meta.codification.Codification;


/**
 *
 * @author Márcio
 */
public abstract class Solution<
        Prob extends Problem,
        Obj extends Objective, 
        Codif extends Codification,
        Sol extends Solution
    > implements Comparable<Sol> {
    
    protected final Obj objective;
    protected final Codif codification;
    
    public Solution(Obj objective, Codif codification) throws Exception {
        this.objective = objective;
        this.codification = codification;
        this.objective.start();
    }
    /**
     * 
     * @param prob
     * @return
     * @throws Exception 
     */
    public abstract Sol build(Prob prob) throws Exception;
    
    /**
     * 
     * @param prob
     * @return
     * @throws Exception 
     */
    public abstract Sol build(Prob prob, Codif codification) throws Exception;

    public final void evaluate(Prob prob) throws Exception{
        this.objective.evaluate(prob, this.codification);
    }
    public final boolean penalized() throws Exception{
        return this.objective.penalized();
    }
    public final double compareToAbs(Sol other){
        return this.objective.compareToAbs(other.objective);
    }
    public final double compareToRel(Sol other){
        return this.objective.compareToRel(other.objective);
    }
    public final Solution minimum(Sol other){
        int cp = compareTo(other);
        return cp <= 0 ? this : other;
    }
    public final Solution maximum(Sol other){
        int cp = compareTo(other);
        return cp >= 0 ? this : other;
    }
    public final boolean GT(Sol other){
        int cp = compareTo(other);
        return cp > 0 ? true : false;
    }
    public final boolean GE(Sol other){
        int cp = compareTo(other);
        return cp >= 0 ? true : false;
    }
    public final boolean EQ(Sol other){
        int cp = compareTo(other);
        return cp == 0 ? true : false;
    }
    public final boolean LE(Sol other){
        int cp = compareTo(other);
        return cp <= 0 ? true : false;
    }
    public final boolean LT(Sol other){
        int cp = compareTo(other);
        return cp < 0 ? true : false;
    }
    
    public final Codif codif(){
        return codification;
    }
    public final Obj obj(){
        return objective;
    }
    @Override
    public int compareTo(Sol other) {
        if(other==null){
            return -1;
        }
        return this.objective.compareTo(other.objective);
    }
    @Override
    public String toString() {
        return objective.toString();
    }
    /**
     * 
     * @param prob
     * @param source
     * @throws Exception 
     */
    public void copy(Prob prob, Sol source) throws Exception {
        this.codification.copy(prob, source.codification);
        this.objective.copy(prob, source.objective);
    }
    /**
     * 
     * @param prob
     * @return
     * @throws Exception 
     */
    public final Sol clone(Prob prob) throws Exception{
        Sol sol = Solution.this.build(prob);
        sol.copy(prob, this);
        return sol;
    }

    public void copyIfBetter(Prob prob, Sol source) throws Exception {
        if(source.LT(this)){
            this.copy(prob, source);
        }
    }

    public void printer(Problem prob, StreamPrinter stream) throws Exception {
        this.objective.printer(prob, stream, codification);
        this.codification.printer(prob, stream);
    }
}
