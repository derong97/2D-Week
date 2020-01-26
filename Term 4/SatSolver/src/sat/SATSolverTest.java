package sat;

/*
import static org.junit.Assert.*;

import org.junit.Test;
*/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import sat.env.*;
import sat.formula.*;


public class SATSolverTest {
    Literal a = PosLiteral.make("a");
    Literal b = PosLiteral.make("b");
    Literal c = PosLiteral.make("c");
    Literal na = a.getNegation();
    Literal nb = b.getNegation();
    Literal nc = c.getNegation();

    public static void main(String[] args) throws Exception {

        Formula f = parseCNF("C:/Users/deron/Downloads/sat3Large.cnf");
        System.out.println("SAT solver starts!!!");
        long started = System.nanoTime();
        Environment e = SATSolver.solve(f);
        long time = System.nanoTime();
        long timeTaken= time - started;
        System.out.println("Time:" + timeTaken/1000000.0 + "ms");
        System.out.println((e == null ? "Not satisfiable": "Satisfiable"));
        System.out.println(e);
        String[] data;
        if (e != null){
            data = e.toString().substring(13, e.toString().length()-1).replaceAll("->",":").split(", ");
            writeUsingBufferedWriter(data);
        }

    }
    private static void writeUsingBufferedWriter(String[] data) {
        File file = new File("C:/Users/deron/Downloads/BoolAssignment.txt");
        FileWriter fr = null;
        BufferedWriter br = null;
        try{
            fr = new FileWriter(file);
            br = new BufferedWriter(fr);
            for(String var: data){
                br.write(var + '\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                br.close();
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Formula parseCNF(String filename) throws IOException {
        Formula f = new Formula();
        Clause currentC = new Clause();
        String currentL;
        String[] s;

        BufferedReader br = new BufferedReader(new FileReader(filename));
        while ((currentL = br.readLine()) != null){

            if (currentL.isEmpty()){
                continue;
            }

            s = currentL.trim().split("\\s+");
            if(!(s[0].equals("c") || s[0].equals("p"))){
                for(String l: s){
                    if(l.equals("0")) {
                        if(currentC == null){
                            currentC = new Clause();
                        }
                        else{
                            f = f.addClause(currentC);
                            currentC = new Clause();
                        }
                    }
                    else {
                        if (l.charAt(0) == '-') {
                            currentC = currentC.add(NegLiteral.make(l.substring(1)));
                        } else {
                            currentC = currentC.add(PosLiteral.make(l));
                        }
                    }
                }
            }
        }
        return f;
    }
	
    public void testSATSolver1(){
    	// (a v b)
    	Environment e = SATSolver.solve(makeFm(makeCl(a,b), makeCl(na,b), makeCl(nb)));
        System.out.println(e);
/*
    	assertTrue( "one of the literals should be set to true",
    			Bool.TRUE == e.get(a.getVariable())  
    			|| Bool.TRUE == e.get(b.getVariable())	);
    	
*/
    }
    
    
    public void testSATSolver2(){
    	// (~a)
    	Environment e = SATSolver.solve(makeFm(makeCl(na)));
/*
    	assertEquals( Bool.FALSE, e.get(na.getVariable()));
*/
    }
    
    private static Formula makeFm(Clause... e) {
        Formula f = new Formula();
        for (Clause c : e) {
            f = f.addClause(c);
        }
        return f;
    }
    
    private static Clause makeCl(Literal... e) {
        Clause c = new Clause();
        for (Literal l : e) {
            c = c.add(l);
        }
        return c;
    }
    
    
    
}