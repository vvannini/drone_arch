/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.gen.linear.vehicle.parts;

import ProOF.apl.UAV.abst.vehicle.parts.oDynamic;
import ProOF.apl.UAV.gen.linear.LinearApproach;
import ProOF.apl.UAV.gen.linear.LinearModel;
import ilog.concert.IloNumExpr;

/**
 *
 * @author marcio
 * @param <App>
 * @param <Model>
 */
public abstract class oLinearDynamic<App extends LinearApproach, Model extends LinearModel> extends oDynamic<App, Model>{
    private double A[][];
    private double B[][];

    @Override
    public void addConstraints(App approach, Model model) throws Exception {
        for(int t=0; t<model.controls.length; t++){
            for(int i=0; i<A.length; i++){
                IloNumExpr exp = null;
                for(int j=0; j<A[i].length; j++){
                    exp = model.cplex.SumProd(exp, A[i][j], model.states[t].x[j]);
                }
                for(int j=0; j<B[i].length; j++){
                    exp = model.cplex.SumProd(exp, B[i][j], model.controls[t].u[j]);
                }
                model.cplex.addEq(model.states[t+1].x[i], exp, "DynamicLinear["+(i+1)+"]");
            }
        }
    }
    
    public double[] next(double x[], double u[]){
        double r[] = new double[x.length];
        for(int i=0; i<A.length; i++){
            r[i] = 0;
            for(int j=0; j<A[i].length; j++){
                r[i] += A[i][j] * x[j];
            }
            for(int j=0; j<B[i].length; j++){
                r[i] += B[i][j] * u[j];
            }
        }
        return r;
    }
    
    public void setMatrixes(double[][] A, double[][] B) {
        this.A = A;
        this.B = B;
    }
    
    private boolean check(int N){
        if(this.A==null || this.B==null){
            setMatrixes(new double[2*N][2*N], new double[2*N][N]);
            return true;
        }
        return false;
    }
    
    /**
     * Blackmore 2011 pure matrixes, assume dt=1, pr=0.7869, vr=0.6065
     */
    public void letBeBlackmorePure(final int N) {
        letBeBlackmoreBased(N, 0.7869, 0.6065);
    }
    
    /**
     * Blackmore 2011 based matrixes, assume dt=1
     * <pre>
     * -------------- example 2D -------------
     * A = new double[][]{
     *      {1,     0,      pr,     0},
     *      {0,     1,      0,      pr},
     *      {0,     0,      vr,     0},
     *      {0,     0,      0,      vr},
     * };
     * B = new double[][]{
     *      {1-pr,  0},
     *      {0,     1-pr},
     *      {1-vr,  0},
     *      {0,     1-vr},
     * };
     * </pre>
     * @param N
     * @param pr
     * @param vr 
     */
    public void letBeBlackmoreBased(final int N, final double pr, final double vr) {
        if(check(N)){
            for(int i=0; i<N; i++){
                A[i][i] = 1.0;
                A[i][N+i] = pr;
                A[N+i][N+i] = vr;
            }
            for(int i=0; i<N; i++){
                B[i][i] = 1-pr;
                B[i+N][i] = 1-vr;
            }
        }
    }
    
    /**
     * <pre>
     * -------------- example 2D -------------
     * A = new double[][]{
     *      {1,     0,      dt,     0},
     *      {0,     1,      0,      dt},
     *      {0,     0,      1,      0},
     *      {0,     0,      0,      1},
     * };
     * B = new double[][]{
     *      {dt2,   0},
     *      {0,     dt2},
     *      {dt,    0},
     *      {0,     dt},
     * };
     * </pre>
     * @param N
     * @param dt 
     */
    public void letBeAirFree(final int N, final double dt) {
        if(check(N)){
            for(int i=0; i<2*N; i++){
                A[i][i] = 1.0;
            }
            for(int i=0; i<N; i++){
                A[i][N+i] = dt;
            }
            final double dt2 = dt*dt/2;
            for(int i=0; i<N; i++){
                B[i][i] = dt2;
                B[i+N][i] = dt;
            }
        }
    }
    
    /**
     * <pre>
     * -------------- example 2D -------------
     * A = new double[][]{
     *      {1,     0,      pr,     0},
     *      {0,     1,      0,      pr},
     *      {0,     0,      vr,      0},
     *      {0,     0,      0,      vr},
     * };
     * B = new double[][]{
     *      {dt2,   0},
     *      {0,     dt2},
     *      {dt,    0},
     *      {0,     dt},
     * };
     * </pre>
     * @param N
     * @param mass
     * @param full_throttle
     * @param terminal_velocity
     * @param dt 
     */
    public void letBeAirResistence(final int N, double mass, double full_throttle, double terminal_velocity, final double dt) {
        if(check(N)){
            final double Kd = calculateAirResistance(mass, full_throttle, terminal_velocity);
            final double pr = calculatePositionReduction(mass, terminal_velocity, Kd, dt);
            final double vr = calculateVelocityReduction(mass, terminal_velocity, Kd, dt);
            for(int i=0; i<N; i++){
                A[i][i] = 1.0;
                A[i][N+i] = pr;
                A[N+i][N+i] = vr;
            }
            final double dt2 = dt*dt/2;
            for(int i=0; i<N; i++){
                B[i][i] = dt2;
                B[i+N][i] = dt;
            }
        }
    }
    
    public static double calculateAirResistance(double mass, double full_throttle, double terminal_velocity){
        return (mass * full_throttle) / terminal_velocity;
    }
    public static double calculateVelocityReduction(double mass, double terminal_velocity, double air_resistance, double dt){
        return 1 - air_resistance * terminal_velocity * dt / mass;
    }
    public static double calculatePositionReduction(double mass, double terminal_velocity, double air_resistance, double dt){
        return dt - air_resistance * terminal_velocity * dt * dt / (2*mass);
    }
    
}
