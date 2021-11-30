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
    private int globalIndex = 0;

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
        String v1 = ctx.getChild(0).getText(); // get the first variable before the operand
        String v2 = ctx.getChild(2).getText(); // get the variable after the operand

        // since when the listener enters the subtraction rule, the only string given is, for example, y+10
        // I need to know what variable is being SET with the value, y+10
        // So this loop searches the HashMap keySet to see if the value y+10 is a value for a key in the HashMap
        // and then saves it to a variable called current
        String current = ""; // variable to hold the variable key
            for(String key : varTable.keySet()){
                if(varTable.get(key).value.equals(ctx.getText())){
                    current = key;
            }
        }
        // checking to see if String v1 is a key in the Hashmap
        if(varTable.keySet().contains(v1)){
            mainVisitor.visitVarInsn(Opcodes.ILOAD, varTable.get(v1).index);
            System.out.println(varTable.get(v1).value);
        } 
        else {
            mainVisitor.visitVarInsn(Opcodes.BIPUSH, Integer.parseInt(v1));
        }
        // checking to see if String v2 is a key in the Hashmap
        if(varTable.keySet().contains(v2)){
            mainVisitor.visitVarInsn(Opcodes.ILOAD, varTable.get(v2).index);
        }
        else {
            mainVisitor.visitVarInsn(Opcodes.BIPUSH, Integer.parseInt(v2));
        }

        mainVisitor.visitInsn(Opcodes.IADD);
        mainVisitor.visitVarInsn(Opcodes.ISTORE, varTable.get(current).index); // store the subtracted value into the address/index of the current HashMap key       
    }

    public void exitAddition(KnightCodeParser.AdditionContext ctx){}

    public void enterSubtraction(KnightCodeParser.SubtractionContext ctx){
        System.out.println("ENTER SUBTRACTION");
        String v1 = ctx.getChild(0).getText(); // get the first variable before the operand
        String v2 = ctx.getChild(2).getText(); // get the variable after the operand

        // since when the listener enters the subtraction rule, the only string given is, for example, y-10
        // I need to know what variable is being SET with the value, y-10
        // So this loop searches the HashMap keySet to see if the value y-10 is a value for a key in the HashMap
        // and then saves it to a variable called current
        String current = ""; // variable to hold the variable key
            for(String key : varTable.keySet()){
                if(varTable.get(key).value.equals(ctx.getText())){
                    current = key;
            }
        }
        // checking to see if String v1 is a key in the Hashmap
        if(varTable.keySet().contains(v1)){
            mainVisitor.visitVarInsn(Opcodes.ILOAD, varTable.get(v1).index);
            System.out.println(varTable.get(v1).value);
        } 
        else {
            mainVisitor.visitVarInsn(Opcodes.BIPUSH, Integer.parseInt(v1));
        }
        // checking to see if String v2 is a key in the Hashmap
        if(varTable.keySet().contains(v2)){
            mainVisitor.visitVarInsn(Opcodes.ILOAD, varTable.get(v2).index);
        }
        else {
            mainVisitor.visitVarInsn(Opcodes.BIPUSH, Integer.parseInt(v2));
        }

        mainVisitor.visitInsn(Opcodes.ISUB);
        mainVisitor.visitVarInsn(Opcodes.ISTORE, varTable.get(current).index); // store the subtracted value into the address/index of the current HashMap key     
    }

    public void exitSubtraction(KnightCodeParser.SubtractionContext ctx){}

    public void enterMultiplication(KnightCodeParser.MultiplicationContext ctx){
        /*
            FOR SOME REASON MULTIPLY IS THE ONLY ONE NOT WORKING PROPERLY
            I did 2*2 and got 25 ???
            No idea whats going on there but ill come back to it
        */
        System.out.println("ENTER MULTIPLICATION");
        String v1 = ctx.getChild(0).getText(); // get the first variable before the operand
        String v2 = ctx.getChild(2).getText(); // get the variable after the operand

        // since when the listener enters the subtraction rule, the only string given is, for example, y*10
        // I need to know what variable is being SET with the value, y*10
        // So this loop searches the HashMap keySet to see if the value y*10 is a value for a key in the HashMap
        // and then saves it to a variable called current
        String current = ""; // variable to hold the variable key
            for(String key : varTable.keySet()){
                if(varTable.get(key).value.equals(ctx.getText())){
                    current = key;
            }
        }
        // checking to see if String v1 is a key in the Hashmap
        if(varTable.keySet().contains(v1)){
            mainVisitor.visitVarInsn(Opcodes.ILOAD, varTable.get(v1).index);
            System.out.println(varTable.get(v1).value);
        } 
        else {
            mainVisitor.visitVarInsn(Opcodes.BIPUSH, Integer.parseInt(v1));
        }
        // checking to see if String v2 is a key in the Hashmap
        if(varTable.keySet().contains(v2)){
            mainVisitor.visitVarInsn(Opcodes.ILOAD, varTable.get(v2).index);
        }
        else {
            mainVisitor.visitVarInsn(Opcodes.BIPUSH, Integer.parseInt(v2));
        }

        mainVisitor.visitInsn(Opcodes.IMUL);
        mainVisitor.visitVarInsn(Opcodes.ISTORE, varTable.get(current).index); // store the subtracted value into the address/index of the current HashMap key
    }

    public void exitMultiplication(KnightCodeParser.MultiplicationContext ctx){
    }

    public void enterDivision(KnightCodeParser.DivisionContext ctx){
        System.out.println("ENTER Division");
        String v1 = ctx.getChild(0).getText(); // get the first variable before the operand
        String v2 = ctx.getChild(2).getText(); // get the variable after the operand

        // since when the listener enters the subtraction rule, the only string given is, for example, y/10
        // I need to know what variable is being SET with the value, y/10
        // So this loop searches the HashMap keySet to see if the value y/10 is a value for a key in the HashMap
        // and then saves it to a variable called current
        String current = ""; // variable to hold the variable key
            for(String key : varTable.keySet()){
                if(varTable.get(key).value.equals(ctx.getText())){
                    current = key;
            }
        }
        // checking to see if String v1 is a key in the Hashmap
        if(varTable.keySet().contains(v1)){
            mainVisitor.visitVarInsn(Opcodes.ILOAD, varTable.get(v1).index);
            System.out.println(varTable.get(v1).value);
        } 
        else {
            mainVisitor.visitVarInsn(Opcodes.BIPUSH, Integer.parseInt(v1));
        }
        // checking to see if String v2 is a key in the Hashmap
        if(varTable.keySet().contains(v2)){
            mainVisitor.visitVarInsn(Opcodes.ILOAD, varTable.get(v2).index);
        }
        else {
            mainVisitor.visitVarInsn(Opcodes.BIPUSH, Integer.parseInt(v2));
        }

        mainVisitor.visitInsn(Opcodes.IDIV);
        mainVisitor.visitVarInsn(Opcodes.ISTORE, varTable.get(current).index); // store the subtracted value into the address/index of the current HashMap key        
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
        System.out.println("VAR: "+ctx.getChild(0).getText());
        String varType = ctx.getChild(0).getText(); // string variable to hold varType
        String identifier = ctx.getChild(1).getText(); // string to hold the identifier
        varTable.put(identifier, new Variable(varType, globalIndex));
        globalIndex += 1;

        // print statements
        System.out.println("ID: "+identifier+" TYPE: "+varType);
        System.out.println(varTable.get(identifier).toString());
    }
	
	public void exitVariable(KnightCodeParser.VariableContext ctx) { 
        System.out.println("Exit variable rule. . .");
    }

    public void enterSetvar(KnightCodeParser.SetvarContext ctx){
        System.out.println("Enter set var rule");
        // since our set var statement is defined as SET (identifier) := (value)
        // I can grab ctx.getChild(1) as the identifier
        // and ctx.getChild(3) as the value
        // ctx.getChild(0) -> "SET", ctx.getChild(2) -> ":="
        String ident = ctx.getChild(1).getText();
        String value = ctx.getChild(3).getText();
        System.out.println(ctx.getChild(3).getText());

        // If we have SET x := y-10 for example
        // These IF statements check to see if either y or 10 is a key in the hashmap
        // If either one is a key in the HashMap, then we get the value, parse the value to an Integer, then push it to the stack and store
        // it in the index
        if(varTable.keySet().contains(value)){
            varTable.get(ident).setValue(varTable.get(value).value);
            mainVisitor.visitVarInsn(Opcodes.BIPUSH, Integer.parseInt(varTable.get(ident).value));
            mainVisitor.visitVarInsn(Opcodes.ISTORE, varTable.get(ident).index);
        }
        else {
            try{
                varTable.get(ident).setValue(value);
                mainVisitor.visitVarInsn(Opcodes.BIPUSH, Integer.parseInt(varTable.get(ident).value));
                mainVisitor.visitVarInsn(Opcodes.ISTORE, varTable.get(ident).index);
            } catch (Exception e) {
                // This try catch block was the best solution I could come up with, I ran into errors when trying to parse to an integer
                // if the setVar value was something like x-10
                // So, I just return nothing if there is an error caught
                // and the listener will continue on to the operation rule (i.e. addition)
                return ;
            }
        }
    }

    public void exitSetvar(KnightCodeParser.SetvarContext ctx){
        System.out.println("Exit set var rule. . .");
    }

    public void enterRead(KnightCodeParser.ReadContext ctx){
        String var = ctx.getChild(1).getText();
        System.out.println("READ CONTEXT: "+var);
        varTable.get(var).setIndex(7);
        // Initialize the Scanner class
        mainVisitor.visitTypeInsn(Opcodes.NEW, "java/util/Scanner");
        mainVisitor.visitInsn(Opcodes.DUP);
        mainVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "in", "Ljava/io/InputStream;");
        mainVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/Scanner", "<init>", "(Ljava/io/InputStream;)V", false);
        mainVisitor.visitVarInsn(Opcodes.ASTORE, 9); //store scanner
        mainVisitor.visitVarInsn(Opcodes.ALOAD, 9); //load scanner
        mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextInt", "()I", false); //invoke scanner
        mainVisitor.visitVarInsn(Opcodes.ISTORE, varTable.get(var).index);
        //System.out.println("X ADDRESS: "+varTable.get(var).address);
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
        String varValue = varTable.get(ctx.getChild(1).getText()).value;
        int cond = Integer.parseInt(ctx.getChild(3).getText());
        //int var = 0;
        //var = Integer.parseInt(varValue);
            System.out.println("VAR: "+varValue);
            Label label0 = new Label();
            mainVisitor.visitLabel(label0);
            //mainVisitor.visitLineNumber(8, label0);
            //mainVisitor.visitVarInsn(Opcodes.ILOAD, varTable.get(varValue).address);
            //mainVisitor.visitIntInsn(Opcodes.BIPUSH, cond);
            mainVisitor.visitInsn(Opcodes.ICONST_0);
            mainVisitor.visitVarInsn(Opcodes.ISTORE, 1);
            Label label1 = new Label();
            mainVisitor.visitLabel(label1);
            //mainVisitor.visitLineNumber(9, label1);
            Label label2 = new Label();
            mainVisitor.visitJumpInsn(Opcodes.GOTO, label2);
            Label label3 = new Label();
            mainVisitor.visitLabel(label3);
            //mainVisitor.visitLineNumber(10, label3);
            //mainVisitor.visitFrame(Opcodes.F_APPEND,1, new Object[] {Opcodes.INTEGER}, 0, null);
            mainVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mainVisitor.visitVarInsn(Opcodes.ILOAD, 1);
            mainVisitor.visitVarInsn(Opcodes.ILOAD, varTable.get(ctx.getChild(1).getText()).index);
            mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
            Label label4 = new Label();
            mainVisitor.visitLabel(label4);
            mainVisitor.visitLineNumber(11, label4);
            mainVisitor.visitIincInsn(1, 1);
            mainVisitor.visitLabel(label2);
            mainVisitor.visitLineNumber(9, label2);
            mainVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mainVisitor.visitVarInsn(Opcodes.ILOAD, 1);
            mainVisitor.visitVarInsn(Opcodes.ILOAD, varTable.get(ctx.getChild(1).getText()).index);
            //mainVisitor.visitIntInsn(Opcodes.BIPUSH, var);
            //mainVisitor.visitIntInsn(Opcodes.BIPUSH, var);
            mainVisitor.visitInsn(Opcodes.ICONST_5);
            mainVisitor.visitJumpInsn(Opcodes.IF_ICMPGT, label3);
            Label label7 = new Label();
            mainVisitor.visitLabel(label7);
            mainVisitor.visitLineNumber(15, label7);
            mainVisitor.visitInsn(Opcodes.RETURN);
            //System.out.println(cond);
            //int var = Integer.parseInt(varTable.get(varValue).value);
            
        /*
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
        /*
        Label label7 = new Label();
        mainVisitor.visitLabel(label7);
        //mainVisitor.visitInsn(Opcodes.RETURN);
        */
    }

    public void exitLoop(KnightCodeParser.LoopContext ctx) {}

    public void enterPrint(KnightCodeParser.PrintContext ctx){
        String output = ctx.getChild(1).getText();
        // if the printcontext is a key in the varTable, print a integer value
        // else print the string
        // STILL NEED TO CHECK IF THE VARIABLE IN THE TABLE IS A STRING OR NOT
        if(varTable.keySet().contains(output)){
		    mainVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mainVisitor.visitVarInsn(Opcodes.ILOAD, varTable.get(output).index);
		    mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",  "println", "(I)V", false);
        } else {
            mainVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mainVisitor.visitLdcInsn(output.substring(1, output.length()-1)); //substring to remove the " "
		    mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",  "println", "(Ljava/lang/String;)V", false);
        }
    }
} //end class
