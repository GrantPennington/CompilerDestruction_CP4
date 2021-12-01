/**
* This is the Variable class. I use the variable class to store the datatype, value, and index of each declared variable
* @author Grant Pennington
* @version 1.0
* Programming Project four
* CS322 - Compiler Construction
* Fall 2021
**/

package compiler;

public class Variable{
    // Variable class for the datatype and value of each declared variable

    String datatype; // datatype
    String value = ""; // generic value
    int index = 0;

    public Variable(String type, int ind){
        // constructor
        datatype = type;
        index = ind;
    }

    public void setValue(String val){
        // setter for generic value
        value = val;
    }

    public String getValue(){
        // getter for generic value
        return value;
    }

    public void setIndex(int ind){
        // setter for generic value
        index = ind;
    }

    public int getIndex(){
        // getter for generic value
        return index;
    }

    public String toString(){
        return "type -> "+datatype+", value -> "+value+", index - > "+index;
    }
}
