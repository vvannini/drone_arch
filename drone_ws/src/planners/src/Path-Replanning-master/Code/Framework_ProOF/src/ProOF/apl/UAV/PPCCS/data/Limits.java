/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.PPCCS.data;

/**
 *
 * @author jesimar
 */
public class Limits {
    
    public double V_H_MAX = 30;
    public double V_H_MIN = 11;
    public double V_V_MAX = 10;
    public double V_V_MIN = -10;
    
    public double A_H_MAX = 5;//5;
    public double A_V_MAX = 5;
    public double A_V_MIN = -5;
    
    public double K_V_Vmin = -5.5;//problema no motor
    public double K_V_Vmax = -6.5;//problema no motor
    public double V_H_MAX_m = 24.5;//Problema no motor
    public double V_H_MIN_m = 23.5;//Problema no motor

    public Limits() {
    }        
    
}
