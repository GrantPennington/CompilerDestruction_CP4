package compiler;

public class Variable{
    // Variable class for the datatype and value of each declared variable

    String datatype; // datatype
    String value = null; // generic value
    int address = 0;

    public Variable(String type){
        // constructor
        datatype = type;
    }

    public void setValue(String val){
        // setter for generic value
        value = val;
    }

    public String getValue(){
        // getter for generic value
        return value;
    }

    public void setAddress(int addy){
        // setter for generic value
        address = addy;
    }

    public int getAddress(){
        // getter for generic value
        return address;
    }

    public void show(){
        System.out.println(datatype+" -> "+value);
    }

    public String toString(){
        // toString method
        return datatype+", "+value;
    }
}
