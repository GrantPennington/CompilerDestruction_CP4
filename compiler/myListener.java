package compiler;

import java.util.HashMap;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Label;
import java.util.regex.*;
import lexparse.*;

public class myListener extends KnightCodeBaseListener{
    private ClassWriter cw;  //ASM ClassWriter 
	private MethodVisitor mainVisitor; //ASM MethodVisitor
	private String programName; //name of the .class output file

    HashMap<String, Variable> varTable = new HashMap<String, Variable>(); //hash map of type String and class Variable to store the declared variables


	public myListener(String programName){

		this.programName = programName;
	
    }//end constructor

	public void setupClass(){
		
		//Set up the classwriter
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(Opcodes.V11, Opcodes.ACC_PUBLIC,this.programName, null, "java/lang/Object",null);
	
		//Use local MethodVisitor to create the constructor for the object
		MethodVisitor mv=cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0); //load the first local variable: this
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V",false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1,1);
        mv.visitEnd();
       	
		//Use global MethodVisitor to write bytecode according to entries in the parsetree	
	 	mainVisitor = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC,  "main", "([Ljava/lang/String;)V", null, null);
        mainVisitor.visitCode();

	}//end setupClass

	public void closeClass(){
		//Use global MethodVisitor to finish writing the bytecode and write the binary file.
		mainVisitor.visitInsn(Opcodes.RETURN);
		mainVisitor.visitMaxs(3, 3);
		mainVisitor.visitEnd();

		cw.visitEnd();

        byte[] b = cw.toByteArray();

        Utilities.writeFile(b,this.programName+".class");
        
        System.out.println("Done!");

	}//end closeClass

    public void enterFile(KnightCodeParser.FileContext ctx){

		System.out.println("Enter program rule for first time");
		setupClass();
	}

	public void exitFile(KnightCodeParser.FileContext ctx){

		System.out.println("Leaving program rule. . .");
		closeClass();

	}

	public void enterDeclare(KnightCodeParser.DeclareContext ctx){

		System.out.println("Enter Declare rule for first time");
	}

	public void exitDeclare(KnightCodeParser.DeclareContext ctx){

		System.out.println("Leaving Declare rule. . .");
        // for loop to view the contents of the HashMap (for testing purposes)
        for(String key : varTable.keySet()){ 
            System.out.println("KEY: "+key+" VALUE: "+varTable.get(key).toString()); 
        }

	}

    public void enterBody(KnightCodeParser.BodyContext ctx){

		System.out.println("Enter body for first time");
	
	}

	public void exitBody(KnightCodeParser.BodyContext ctx){

		System.out.println("Leaving body. . .");
        // for loop to view the contents of the HashMap (for testing purposes)
        for(String key : varTable.keySet()){ 
            System.out.println("KEY: "+key+" VALUE: "+varTable.get(key).toString()); 
        }

	}

    public void enterAddition(KnightCodeParser.AdditionContext ctx){
        System.out.println("ENTER ADDITION");
        System.out.println(ctx.getChild(0).getText());
        
        for(int i = 0; i < ctx.getText().length(); i++){
            String currentVar = ctx.getChild(i).getText();
            if(!currentVar.equals("+")){
                try{
                    int val = Integer.parseInt(varTable.get(currentVar).value);
                    mainVisitor.visitIntInsn(Opcodes.BIPUSH, val);
                    mainVisitor.visitVarInsn(Opcodes.ISTORE, i+1);
                    varTable.get(currentVar).setAddress(i+1);
                } catch(Exception e) {
                    return; // return nothing from this method if there is an exception
                }
            }
        }
        //get the variable/key that the addition belongs to
        String current = ""; // variable to hold the variable key
        for(String key : varTable.keySet()){
            if(varTable.get(key).getValue().equals(ctx.getText())){
                current = key;
                varTable.get(key).setAddress(5);
            }
        }
        mainVisitor.visitVarInsn(Opcodes.ILOAD, 1);
        mainVisitor.visitVarInsn(Opcodes.ILOAD, 3);
        mainVisitor.visitInsn(Opcodes.IADD);
        mainVisitor.visitVarInsn(Opcodes.ISTORE, varTable.get(current).getAddress()); // store the value of x+y in address of variable        
    }

    public void exitAddition(KnightCodeParser.AdditionContext ctx){}

    public void enterSubtraction(KnightCodeParser.SubtractionContext ctx){
        System.out.println("ENTER SUBTRACTION");
        System.out.println(ctx.getChild(0).getText());
        
        // to solve the most recent error ive ran into, I am adding this code to grab the value from the hasmap, that is being evaluated/parsed to an integer,
        // to check if it is an integer or a string before evaluating
            for(int i = 0; i < ctx.getText().length(); i++){
                String currentVar = ctx.getChild(i).getText();
                if(!currentVar.equals("-")){
                    try{
                        int val = Integer.parseInt(varTable.get(currentVar).value);
                        mainVisitor.visitIntInsn(Opcodes.BIPUSH, val);
                        mainVisitor.visitVarInsn(Opcodes.ISTORE, i+1);
                        varTable.get(currentVar).setAddress(i+1);
                    } catch(Exception e) {
                        return; // return nothing from this method if there is an exception
                    }
                }
            }
            // get the variable/key that the subtraction belongs to
            // i.e. z = x-y, we evaluated x-y -> 5-2 = 7, now we have 7, and we need to know where we got it from
            // so I search the HashMap looking for the key that has the value pair of x-y
            // this is so I can store the address of where the value 7 is stored, in the HashMap
            String current = ""; // variable to hold the variable key
            for(String key : varTable.keySet()){
                if(varTable.get(key).getValue().equals(ctx.getText())){
                    current = key;
                    varTable.get(key).setAddress(5);
                }
            }
            mainVisitor.visitVarInsn(Opcodes.ILOAD, 1);
            mainVisitor.visitVarInsn(Opcodes.ILOAD, 3);
            mainVisitor.visitInsn(Opcodes.ISUB);
            mainVisitor.visitVarInsn(Opcodes.ISTORE, varTable.get(current).getAddress()); // store the value of x+y in address of variable    
    }

    public void exitSubtraction(KnightCodeParser.SubtractionContext ctx){

    }

    public void enterMultiplication(KnightCodeParser.MultiplicationContext ctx){
        System.out.println("ENTER MULTIPLICATION");
        System.out.println(ctx.getChild(0).getText());
        
        for(int i = 0; i < ctx.getText().length(); i++){
            String currentVar = ctx.getChild(i).getText();
            if(!currentVar.equals("*")){
                try{
                    int val = Integer.parseInt(varTable.get(currentVar).value);
                    mainVisitor.visitIntInsn(Opcodes.BIPUSH, val);
                    mainVisitor.visitVarInsn(Opcodes.ISTORE, i+1);
                    varTable.get(currentVar).setAddress(i+1);
                } catch(Exception e) {
                    return; // return nothing from this method if there is an exception
                }
            }
        }
        //get the variable/key that the addition belongs to
        String current = ""; // variable to hold the variable key
        for(String key : varTable.keySet()){
            if(varTable.get(key).getValue().equals(ctx.getText())){
                current = key;
                varTable.get(key).setAddress(5);
            }
        }
        mainVisitor.visitVarInsn(Opcodes.ILOAD, 1);
        mainVisitor.visitVarInsn(Opcodes.ILOAD, 3);
        mainVisitor.visitInsn(Opcodes.IMUL);
        mainVisitor.visitVarInsn(Opcodes.ISTORE, varTable.get(current).getAddress()); // store the value in address of variable        
    }

    public void exitMultiplication(KnightCodeParser.MultiplicationContext ctx){
    }

    public void enterDivision(KnightCodeParser.DivisionContext ctx){
        System.out.println("ENTER Division");
        System.out.println(ctx.getChild(0).getText());
        
        for(int i = 0; i < ctx.getText().length(); i++){
            String currentVar = ctx.getChild(i).getText();
            if(!currentVar.equals("/")){
                try{
                    int val = Integer.parseInt(varTable.get(currentVar).value);
                    mainVisitor.visitIntInsn(Opcodes.BIPUSH, val);
                    mainVisitor.visitVarInsn(Opcodes.ISTORE, i+1);
                    varTable.get(currentVar).setAddress(i+1);
                } catch(Exception e) {
                    return; // return nothing from this method if there is an exception
                }
            }
        }
        //get the variable/key that the addition belongs to
        String current = ""; // variable to hold the variable key
        for(String key : varTable.keySet()){
            if(varTable.get(key).getValue().equals(ctx.getText())){
                current = key;
                varTable.get(key).setAddress(5);
            }
        }
        mainVisitor.visitVarInsn(Opcodes.ILOAD, 1);
        mainVisitor.visitVarInsn(Opcodes.ILOAD, 3);
        mainVisitor.visitInsn(Opcodes.IDIV);
        mainVisitor.visitVarInsn(Opcodes.ISTORE, varTable.get(current).getAddress()); // store the value in address of variable        
    }

    public void exitDivision(KnightCodeParser.DivisionContext ctx){
    }

    public void enterComp(KnightCodeParser.CompContext ctx) {
        System.out.println("COMP CONTEXT");
        System.out.println(ctx.getText());
    }

    public void exitComp(KnightCodeParser.CompContext ctx) {}

    public void enterVariable(KnightCodeParser.VariableContext ctx) { 
        System.out.println("Enter variable rule");

        String varType = ""; // string variable to hold varType
        String identifier = ""; // string to hold the identifier
        for(int i = 0; i < ctx.getText().length(); i++){
            // for loop that iterates over the ctx.getText()
            // this loop pulls out the datatype and identifier and assigns them
            if(!Character.isLowerCase(ctx.getText().charAt(i))){
                varType += ctx.getText().charAt(i);
            }
            else {
                identifier = ""+ctx.getText().charAt(i);
            }
        }
        varTable.put(identifier, new Variable(varType)); // store the values gotten from the for loop into the hash map
        // print statements
        System.out.println("ID: "+identifier+" TYPE: "+varType);
        System.out.println(varTable.get(identifier).toString());
    }
	
	public void exitVariable(KnightCodeParser.VariableContext ctx) { 
        System.out.println("Exit variable rule. . .");
    }

    public void enterSetvar(KnightCodeParser.SetvarContext ctx){
        System.out.println("Enter set var rule");
        
        String key = ctx.getText().substring(ctx.getText().indexOf('T')+1, ctx.getText().indexOf(':')); //substring to extract key value from SET statement
        String value = ctx.getText().substring(ctx.getText().indexOf('=')+1); // substring to extract value from SET statement
        
        // update the value of the Variable object using the setter method
        // cast the value to the proper type i.e. Integer or String
        varTable.get(key).setValue(value);
        System.out.println(varTable.get(key));
    }

    public void exitSetvar(KnightCodeParser.SetvarContext ctx){
        System.out.println("Exit set var rule. . .");
    }

    public void enterRead(KnightCodeParser.ReadContext ctx){
        String var = ctx.getChild(1).getText();
        System.out.println("READ CONTEXT: "+var);
        varTable.get(var).setAddress(7);
        // Initialize the Scanner class
        mainVisitor.visitTypeInsn(Opcodes.NEW, "java/util/Scanner");
        mainVisitor.visitInsn(Opcodes.DUP);
        mainVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "in", "Ljava/io/InputStream;");
        mainVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/Scanner", "<init>", "(Ljava/io/InputStream;)V", false);
        mainVisitor.visitVarInsn(Opcodes.ASTORE, 9); //store scanner
        mainVisitor.visitVarInsn(Opcodes.ALOAD, 9); //load scanner
        mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextInt", "()I", false); //invoke scanner
        mainVisitor.visitVarInsn(Opcodes.ISTORE, varTable.get(var).getAddress());
        // ONLY WORKS FOR READING INTEGERS RIGHT NOW
        // STILL NEED TO FIGURE OUT HOW TO GET STRINGS
    }
    
    public void exitRead(KnightCodeParser.ReadContext ctx){}

    public void enterLoop(KnightCodeParser.LoopContext ctx) {
        System.out.println("ENTER THAT LOOOOP");
        System.out.println(ctx.getText());
        String context = ctx.getText();

        // get the comparison string
        String comp = context.substring(context.indexOf('E')+1, context.indexOf('D'));
        System.out.println(comp);
        String compSymb = comp.substring(1,2);

        Label label0 = new Label();
        mainVisitor.visitLabel(label0);
        mainVisitor.visitInsn(Opcodes.ICONST_0);
        mainVisitor.visitVarInsn(Opcodes.ISTORE, 1);
        Label label1 = new Label();
        mainVisitor.visitLabel(label1);
        Label label2 = new Label();
        mainVisitor.visitJumpInsn(Opcodes.GOTO, label2);
        // remove label 3 after test
        Label label3 = new Label();
        mainVisitor.visitLabel(label3);
        mainVisitor.visitFrame(Opcodes.F_APPEND,1, new Object[] {Opcodes.INTEGER}, 0, null);
        mainVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mainVisitor.visitVarInsn(Opcodes.ILOAD, 1);
        mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
        Label label4 = new Label();
        mainVisitor.visitLabel(label4);
        mainVisitor.visitIincInsn(1, 4);
        mainVisitor.visitLabel(label2);
        mainVisitor.visitVarInsn(Opcodes.ILOAD, 1);
        mainVisitor.visitInsn(Opcodes.ICONST_5);

        if(compSymb.equals("<")){
            mainVisitor.visitJumpInsn(Opcodes.IF_ICMPLT, label3);
        }
        else if(compSymb.equals(">")){
            mainVisitor.visitJumpInsn(Opcodes.IF_ICMPGT, label3);
        }
        else if(compSymb.equals(":=")){
            mainVisitor.visitJumpInsn(Opcodes.IF_ICMPEQ, label3);
        }
        else if(compSymb.equals("<>")){
            mainVisitor.visitJumpInsn(Opcodes.IF_ICMPNE, label3);
        }
        Label label7 = new Label();
        mainVisitor.visitLabel(label7);
        //mainVisitor.visitInsn(Opcodes.RETURN);
    }

    public void exitLoop(KnightCodeParser.LoopContext ctx) {}

    public void enterPrint(KnightCodeParser.PrintContext ctx){
        String output = ctx.getChild(1).getText();
        // if the printcontext is a key in the varTable, print a integer value
        // else print the string
        // STILL NEED TO CHECK IF THE VARIABLE IN THE TABLE IS A STRING OR NOT
        if(varTable.keySet().contains(output)){ 
		    mainVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mainVisitor.visitVarInsn(Opcodes.ILOAD, varTable.get(output).getAddress());
		    mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",  "println", "(I)V", false);
        } else {
            mainVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mainVisitor.visitLdcInsn(output.substring(1, output.length()-1)); //substring to remove the " "
		    mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",  "println", "(Ljava/lang/String;)V", false);
        }
    }
} //end class
