import soot.*;
import soot.jimple.*;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.ForwardBranchedFlowAnalysis;
import soot.toolkits.scalar.ForwardFlowAnalysis;
import soot.toolkits.scalar.Pair;
import soot.util.Chain;

import java.util.HashMap;
import java.util.logging.Logger;


public class IVA extends ForwardFlowAnalysis<Unit, HashMap<Local, Pair<Integer, Integer>>>{

    SootMethod analysisMethod;
    static Integer max = Integer.MAX_VALUE;
    static Integer min = Integer.MIN_VALUE;

    public IVA(DirectedGraph<Unit> g, SootMethod m){
        super(g);
        analysisMethod = m;
        doAnalysis();
    }

    /**
     * Returns the flow object corresponding to the initial values for each graph node.
     */
    @Override
    protected HashMap<Local, Pair<Integer, Integer>> newInitialFlow() {
        HashMap<Local, Pair<Integer, Integer>> initMap = new HashMap<>();
        Chain<Local> localVars = analysisMethod.getActiveBody().getLocals();
        for (Local l : localVars){
            initMap.put(l, new Pair<>(max, min));
        }
        return initMap;
    }

    /**
     * Compute the merge of the <code>in1</code> and <code>in2</code> sets, putting the result into <code>out</code>. The
     * behavior of this function depends on the implementation ( it may be necessary to check whether <code>in1</code> and
     * <code>in2</code> are equal or aliased ). Used by the doAnalysis method.
     *
     * @param in1
     * @param in2
     * @param out
     */
    @Override
    protected void merge(HashMap<Local, Pair<Integer, Integer>> in1, HashMap<Local, Pair<Integer, Integer>> in2, HashMap<Local, Pair<Integer, Integer>> out) {
        in1.putAll(out);
        in2.putAll(out);
    }

    /**
     * Creates a copy of the <code>source</code> flow object in <code>dest</code>.
     *
     * @param source
     * @param dest
     */
    @Override
    protected void copy(HashMap<Local, Pair<Integer, Integer>> source, HashMap<Local, Pair<Integer, Integer>> dest) {
        dest.putAll(source);
    }

    /**
     * Given the merge of the <code>out</code> sets, compute the <code>in</code> set for <code>s</code> (or in to out,
     * depending on direction).
     * <p>
     * This function often causes confusion, because the same interface is used for both forward and backward flow analyses.
     * The first parameter is always the argument to the flow function (i.e. it is the "in" set in a forward analysis and the
     * "out" set in a backward analysis), and the third parameter is always the result of the flow function (i.e. it is the
     * "out" set in a forward analysis and the "in" set in a backward analysis).
     *
     * @param in  the input flow
     * @param d   the current node
     * @param out the returned flow
     **/
    @Override
    protected void flowThrough(HashMap<Local, Pair<Integer, Integer>> in, Unit d, HashMap<Local, Pair<Integer, Integer>> out) {
        Stmt s = (Stmt)d;
        System.out.println("stmt : " + s);
        out.putAll(in);
        if(s instanceof AssignStmt){
            handleAssignStmt((AssignStmt) s, out);
        } else if (s instanceof IfStmt){
            IfStmt ifStmt = (IfStmt)s;
            handleIfStmt(ifStmt, out);
        }
    }

    private void handleIfStmt(IfStmt ifStmt, HashMap<Local, Pair<Integer, Integer>> facts){}

    private Pair<Integer,Integer> getBot(){
        return new Pair<>(max,max);
    }
    private boolean isbot(Pair<Integer, Integer> p){
        return (p.getO2().intValue() == max && p.getO1().intValue() == max)
                || (p.getO2().intValue() == min && p.getO1().intValue() == min);
    }

    private boolean isXtremes(Integer a){
        return a == max || a == min;
    }

    private Boolean hasZero(Pair<Integer, Integer> interval){
        return 0 >= interval.getO1() && interval.getO2() <= 0;
    }

    private Integer sum(Integer a, Integer b){
        if (isXtremes(a) ){
            return a;
        } else if (isXtremes(b)){
            return b;
        } else{
            return a + b;
        }
    }

    private Integer diff(Integer a, Integer b){
        if (isXtremes(a) ){
            return a;
        } else if (isXtremes(b)){
            return b;
        } else{
            return a - b;
        }
    }

    private Integer mul(Integer a, Integer b){
        if (isXtremes(a) ){
            return a;
        } else if (isXtremes(b)){
            return b;
        } else{
            return a * b;
        }
    }

    private Integer div(Integer a, Integer b){ // a/b
        if (isXtremes(a) ){
            return a;
        } else if (isXtremes(b)){
            return 0;
        } else {
            return a / b;
        }
    }


    private void handleAddExpr(Value lhs, Value rhs, HashMap<Local, Pair<Integer, Integer>> facts){
        Local lvar = (Local) lhs;
        BinopExpr binopExpr = (BinopExpr)rhs;
        Value oper1 = binopExpr.getOp1();
        Value oper2 = binopExpr.getOp2();

        if(oper1 instanceof IntConstant && oper2 instanceof IntConstant){ // operands are constants
            IntConstant constVal1 = (IntConstant)oper1;
            IntConstant constVal2 = (IntConstant)oper2;
            if (isbot(new Pair<>(constVal1.value, constVal1.value)) || isbot(new Pair<>(constVal2.value, constVal2.value))){
                facts.put(lvar, getBot());
            } else {
                facts.put(lvar, new Pair<>(sum(constVal1.value, constVal2.value), sum(constVal2.value, constVal1.value)));
            }
        } else if (oper1 instanceof Local && oper2 instanceof IntConstant){ // one of the operands are variables
            IntConstant constVal2 = (IntConstant)oper2;
            Pair<Integer, Integer> interval = facts.get(oper1);
            if (isbot(interval) || isbot(new Pair<>(constVal2.value, constVal2.value))){
                facts.put(lvar, getBot());
            } else{
                facts.put(lvar, new Pair<>(sum(interval.getO1(), constVal2.value), sum(interval.getO2(), constVal2.value)));
            }
        } else if (oper1 instanceof IntConstant && oper2 instanceof Local){
            IntConstant constVal1 = (IntConstant)oper1;
            Pair<Integer, Integer> interval = facts.get(oper2);
            if (isbot(interval) || isbot(new Pair<>(constVal1.value, constVal1.value))){
                facts.put(lvar, getBot());
            } else{
                facts.put(lvar, new Pair<>(sum(interval.getO1(), constVal1.value), sum(interval.getO2(), constVal1.value)));
            }
        } else { // operands are variables
            assert oper1 instanceof Local;
            assert oper2 instanceof Local;
            Pair<Integer, Integer> interval1 = facts.get((Local) oper1);
            Pair<Integer, Integer> interval2 = facts.get((Local) oper2);
            if(isbot(interval1) || isbot(interval2)){
                facts.put(lvar, getBot());
            } else {
                facts.put(lvar, new Pair<>(sum(interval1.getO1(), interval2.getO1()), sum(interval1.getO2(), interval2.getO2())));
            }
        }
    }

    void handleDiffExpr(Value lhs, Value rhs, HashMap<Local, Pair<Integer, Integer>> facts){
        Local lvar = (Local) lhs;
        BinopExpr binopExpr = (BinopExpr)rhs;
        Value oper1 = binopExpr.getOp1();
        Value oper2 = binopExpr.getOp2();

        if(oper1 instanceof IntConstant && oper2 instanceof IntConstant){ // operands are constants
            IntConstant constVal1 = (IntConstant)oper1;
            IntConstant constVal2 = (IntConstant)oper2;
            facts.put(lvar, new Pair<>(diff(constVal1.value, constVal2.value), diff(constVal2.value, constVal1.value)));
        } else if (oper1 instanceof Local && oper2 instanceof IntConstant){ // one of the operands are variables
            IntConstant constVal2 = (IntConstant)oper2;
            Pair<Integer, Integer> interval = facts.get(oper1);
            if (isbot(interval)){
                facts.put(lvar, getBot());
            } else{
                facts.put(lvar, new Pair<>(diff(interval.getO1(), constVal2.value), diff(interval.getO2(), constVal2.value)));
            }
        } else if (oper1 instanceof IntConstant && oper2 instanceof Local){
            IntConstant constVal1 = (IntConstant)oper1;
            Pair<Integer, Integer> interval = facts.get(oper2);
            facts.put(lvar, new Pair<>(interval.getO1()+constVal1.value, interval.getO2()+constVal1.value));
            if (isbot(interval)){
                facts.put(lvar, getBot());
            } else{
                facts.put(lvar, new Pair<>(diff(interval.getO1(), constVal1.value), diff(interval.getO2(), constVal1.value)));
            }
        } else { // operands are variables
            assert oper1 instanceof Local;
            assert oper2 instanceof Local;
            Pair<Integer, Integer> interval1 = facts.get((Local) oper1);
            Pair<Integer, Integer> interval2 = facts.get((Local) oper2);
            if(isbot(interval1) || isbot(interval2)){
                facts.put(lvar, getBot());
            } else {
                facts.put(lvar, new Pair<>(diff(interval1.getO1(), interval2.getO2()), diff(interval1.getO2(), interval2.getO1())));
            }
        }
    }

    void handleMulExpr(Value lhs, Value rhs, HashMap<Local, Pair<Integer, Integer>> facts){
        Local lvar = (Local) lhs;
        BinopExpr binopExpr = (BinopExpr)rhs;
        Value oper1 = binopExpr.getOp1();
        Value oper2 = binopExpr.getOp2();

        if(oper1 instanceof IntConstant && oper2 instanceof IntConstant){ // operands are constants
            IntConstant constVal1 = (IntConstant)oper1;
            IntConstant constVal2 = (IntConstant)oper2;
            facts.put(lvar, new Pair<>(mul(constVal1.value, constVal2.value), mul(constVal2.value, constVal1.value)));
        } else if (oper1 instanceof Local && oper2 instanceof IntConstant){ // one of the operands are variables
            IntConstant constVal2 = (IntConstant)oper2;
            Pair<Integer, Integer> interval = facts.get(oper1);
            if (isbot(interval)){
                facts.put(lvar, getBot());
            } else{
                facts.put(lvar, new Pair<>(mul(interval.getO1(), constVal2.value), mul(interval.getO2(), constVal2.value)));
            }
        } else if (oper1 instanceof IntConstant && oper2 instanceof Local){
            IntConstant constVal1 = (IntConstant)oper1;
            Pair<Integer, Integer> interval = facts.get(oper2);
            facts.put(lvar, new Pair<>(interval.getO1()+constVal1.value, interval.getO2()+constVal1.value));
            if (isbot(interval)){
                facts.put(lvar, getBot());
            } else{
                facts.put(lvar, new Pair<>(mul(interval.getO1(), constVal1.value), mul(interval.getO2(), constVal1.value)));
            }
        } else { // operands are variables
            assert oper1 instanceof Local;
            assert oper2 instanceof Local;
            Pair<Integer, Integer> interval1 = facts.get((Local) oper1);
            Pair<Integer, Integer> interval2 = facts.get((Local) oper2);
            if(isbot(interval1) || isbot(interval2)){
                facts.put(lvar, getBot());
            } else {
                facts.put(lvar, new Pair<>(mul(interval1.getO1(), interval2.getO1()), mul(interval1.getO2(), interval2.getO2())));
            }
        }
    }

    private void handleDivExpr(Value lhs, Value rhs, HashMap<Local, Pair<Integer, Integer>> facts){
        Local lvar = (Local) lhs;
        BinopExpr binopExpr = (BinopExpr)rhs;
        Value oper1 = binopExpr.getOp1();
        Value oper2 = binopExpr.getOp2();

        if(oper1 instanceof IntConstant && oper2 instanceof IntConstant){ // operands are constants
            IntConstant constVal1 = (IntConstant)oper1;
            IntConstant constVal2 = (IntConstant)oper2;
            facts.put(lvar, new Pair<>(div(constVal1.value, constVal2.value), div(constVal2.value, constVal1.value)));
        } else if (oper1 instanceof Local && oper2 instanceof IntConstant){ // one of the operands are variables
            IntConstant constVal2 = (IntConstant)oper2;
            Pair<Integer, Integer> interval = facts.get(oper1);
            if (isbot(interval)){
                facts.put(lvar, getBot());
            } else if (!hasZero(new Pair<>(constVal2.value, constVal2.value))){
                facts.put(lvar, new Pair<>(div(interval.getO1(), constVal2.value), div(interval.getO2(), constVal2.value)));
            } else {
                facts.put(lvar, getBot());
            }
        } else if (oper1 instanceof IntConstant && oper2 instanceof Local){
            IntConstant constVal1 = (IntConstant)oper1;
            Pair<Integer, Integer> interval = facts.get(oper2);
            facts.put(lvar, new Pair<>(interval.getO1()+constVal1.value, interval.getO2()+constVal1.value));
            if (isbot(interval)){
                facts.put(lvar, getBot());
            } else if (!hasZero(interval)){
                facts.put(lvar, new Pair<>(div(constVal1.value, interval.getO1()), div(constVal1.value, interval.getO2())));
            } else{
                facts.put(lvar, getBot());
            }
        } else { // operands are variables
            assert oper1 instanceof Local;
            assert oper2 instanceof Local;
            Pair<Integer, Integer> interval1 = facts.get((Local) oper1);
            Pair<Integer, Integer> interval2 = facts.get((Local) oper2);
            if(isbot(interval1) || isbot(interval2)) {
                facts.put(lvar, getBot());
            } else if (!hasZero(interval2)) {
                facts.put(lvar, new Pair<>(div(interval1.getO1(), interval2.getO1()), div(interval1.getO2(), interval2.getO2())));
            } else {
                facts.put(lvar, getBot());
            }
        }
    }

    private void handleAssignStmt(AssignStmt assignStmt, HashMap<Local, Pair<Integer, Integer>> facts){
        Value rhs = assignStmt.getRightOp();
        Value lhs = assignStmt.getLeftOp();
        Local lvar = (Local) lhs;
        if (rhs instanceof IntConstant){
            IntConstant constVal = (IntConstant)rhs;
            if (assignStmt.getLeftOp() instanceof Local){
                if (facts.containsKey((Local)lvar)) {
                    facts.put(lvar, new Pair<>(constVal.value, constVal.value));
                } else {
                    System.out.println("Error : VARIABLE NOT ANALYZED");
                }
            }
        } else if (rhs instanceof Local){
            if (facts.containsKey((Local)rhs)) {
                facts.put(lvar, facts.get(rhs));
            }
        } else if (rhs instanceof BinopExpr){
            BinopExpr binopExpr = (BinopExpr)rhs;
            if (binopExpr instanceof AddExpr){
                handleAddExpr(lhs, rhs, facts);
            } else if (binopExpr instanceof SubExpr){
                handleDiffExpr(lhs, rhs, facts);
            } else if (binopExpr instanceof MulExpr){
                handleMulExpr(lhs, rhs, facts);
            } else if (binopExpr instanceof DivExpr){
                handleDivExpr(lhs, rhs, facts);
            }
        }
    }
}
