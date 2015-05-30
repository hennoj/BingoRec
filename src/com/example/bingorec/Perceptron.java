package com.example.bingorec;



/**
 *
 * @author Johan Hagelbäck (johan.hagelback@gmail.com)
 */
public class Perceptron implements java.io.Serializable
{
    public double[] w;
    
    public Perceptron(int noInputs)
    {
        w = new double[noInputs];
        for (int i = 0; i < noInputs; i++)
        {
            w[i] = Math.random() - 0.5;
        }
    }
    
    public double calculateOutput(double[] inputs)
    {
        double wsum = calculateWeightedSum(inputs);
        double s = calculateSigmoid(wsum);
        //System.out.println("Output: " + wsum + " -> " + s);
        return s;
    }
    
    private double calculateWeightedSum(double[] inputs)
    {
        double sum = 0;
        
        for(int i = 0; i < inputs.length; i++)
        {
            //System.out.println("\t " + inputs[i] + " * " + w[i]);
            sum += inputs[i] * w[i];
        }
        
        return sum;
    }
    
    private double calculateSigmoid(double t)
    {
        double s = 1.0 / (1.0 + Math.pow(Math.E, -t));
        return s;
    }
}