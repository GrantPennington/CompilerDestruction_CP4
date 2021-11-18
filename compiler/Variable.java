package compiler;

public class Variable<T>{
    // Variable class for the datatype and value of each declared variable

    String datatype; // datatype
    T value = null; // generic value

    public Variable(String type){
        // constructor
        datatype = type;
    }

    public void setValue(T val){
        // setter for generic value
        value = val;
    }

    public T getValue(){
        // getter for generic value
        return value;
    }

    public void show(){
        System.out.println(datatype+" -> "+value);
    }

    public String toString(){
        // toString method
        return datatype+", "+value;
    }
}
