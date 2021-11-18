package compiler;

import java.io.IOException;
//ANTLR packages
import lexparse.*; //classes for lexer parser
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.gui.Trees;

public class kcc{
    public static void main(String[] args){
        CharStream input;
        KnightCodeLexer lexer;
        CommonTokenStream tokens;
        KnightCodeParser parser;

        try{
            input = CharStreams.fromFileName(args[0]);  //get the input
            lexer = new KnightCodeLexer(input); //create the lexer
            tokens = new CommonTokenStream(lexer); //create the token stream
            parser = new KnightCodeParser(tokens); //create the parser
       
            ParseTree tree = parser.file();  //set the start location of the parser
            
            
            //Trees.inspect(tree, parser);
            
            String classFile = args[1];		
	
	        myListener listener = new myListener(classFile);
	        ParseTreeWalker walker = new ParseTreeWalker();
	        walker.walk(listener, tree);
        
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }

    }
}