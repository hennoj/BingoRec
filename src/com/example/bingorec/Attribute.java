package com.example.bingorec;

/**
*
* @author Johan Hagelbäck (johan.hagelback@gmail.com)
*/
public class Attribute implements java.io.Serializable
{
   public String id;
   public double value;
   public boolean isClassAttribute;
   
   public Attribute(String id, double value)
   {
       this.id = id;
       this.value = value;
       isClassAttribute = false;
   }
   
   public Attribute(String id, double value, boolean isClassAttribute)
   {
       this.id = id;
       this.value = value;
       this.isClassAttribute = isClassAttribute;
   }
   
   public String toString()
   {
       String str = "" + (int)value;
       return str;
   }
}


