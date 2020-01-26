package sat;

import immutable.EmptyImList;
import immutable.ImList;
import sat.env.Bool;
import sat.env.Environment;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.Literal;
import sat.formula.PosLiteral;

/**
 * A simple DPLL SAT solver. See http://en.wikipedia.org/wiki/DPLL_algorithm
 */
public class SATSolver {

    /**
     * Solve the problem using a simple version of DPLL with backtracking and
     * unit propagation. The returned environment binds literals of class
     * bool.Variable rather than the special literals used in clausification of
     * class clausal.Literal, so that clients can more readily use it.
     *
     * @return an environment for which the problem evaluates to Bool.TRUE, or
     *         null if no such environment exists.
     */
    public static Environment solve(Formula formula) {
        return solve(formula.getClauses(),new Environment());
    }

    /**
     * Takes a partial assignment of variables to values, and recursively
     * searches for a complete satisfying assignment.
     *
     * @param clauses
     *            formula in conjunctive normal form
     * @param env
     *            assignment of some or all variables in clauses to true or
     *            false values.
     * @return an environment for which all the clauses evaluate to Bool.TRUE,
     *         or null if no such environment exists.
     */
    private static Environment solve(ImList<Clause> clauses, Environment env) {
        if (clauses == null){
            return null;
        }
        if (clauses.size()==0){
            return env;
        }
        Clause minClause = clauses.first();
        int minSize = minClause.size();
        for(Clause c: clauses){
            if (c.isEmpty()){
                return null;
            }
            else if(c.isUnit()){
                Literal l = c.chooseLiteral();
                env= env.put(l.getVariable(),l instanceof PosLiteral ? Bool.TRUE:Bool.FALSE);
                return solve(substitute(clauses, l), env);
            }
            else{
                if(c.size()<minSize){
                    minSize = c.size();
                    minClause = c;
                }
            }
        }
        Literal l = minClause.iterator().next();
        Environment newE = solve(substitute(clauses, l), env.put(l.getVariable(), l instanceof PosLiteral ? Bool.TRUE:Bool.FALSE));

        if (newE==null)
            newE = solve(substitute(clauses, l.getNegation()), env.put(l.getVariable(), l instanceof PosLiteral ? Bool.FALSE:Bool.TRUE));
        return newE;
    }

    /**
     * given a clause list and literal, produce a new list resulting from
     * setting that literal to true
     *
     * @param clauses
     *            , a list of clauses
     * @param l
     *            , a literal to set to true
     * @return a new list of clauses resulting from setting l to true
     */
    private static ImList<Clause> substitute(ImList<Clause> clauses,
            Literal l) {
        ImList<Clause> newC = new EmptyImList<>();

        for(Clause c: clauses){
            if (c.contains(l) || c.contains(l.getNegation())){
                c = c.reduce(l);
                if (c == null)
                    continue;
                else if (c.size()==0)
                    return null;
            }
            newC = newC.add(c);
        }
        return newC;
    }
}
