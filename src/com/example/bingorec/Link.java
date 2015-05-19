package com.example.bingorec;

/**
*
* @author Johan Hagelbäck (johan.hagelback@gmail.com)
*/
public class Link 
{
   public Perceptron node;
   public double weight;
   
   public Link(Perceptron node)
   {
       this.node = node;
       weight = Math.random() - 0.5;
   }
}
