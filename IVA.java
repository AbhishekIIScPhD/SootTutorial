import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.*;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JIfStmt;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ForwardBranchedFlowAnalysis;
import soot.toolkits.scalar.ForwardFlowAnalysis;
import soot.toolkits.scalar.Pair;
import soot.util.Chain;

import java.util.*;

public class IVA extends ForwardBranchedFlowAnalysis<HashMap<Local, Pair<Integer, Integer>>> {
    SootMethod analysisMethod;
    static Integer max = Integer.MAX_VALUE;
    static Integer min = Integer.MIN_VALUE;

    public IVA(UnitGraph g, SootMethod m) {
        super(g);
        analysisMethod = m;
        doAnalysis();
    }

    /**
     * Given the merge of the <code>in</code> sets, compute the <code>fallOut</code> and <code>branchOuts</code> set for
     * <code>s</code>.
     *
     * @param in
     * @param d
     * @param fallOut
     * @param branchOuts
     */
    @Override
    protected void flowThrough(HashMap<Local, Pair<Integer, Integer>> in, Unit d, List<HashMap<Local, Pair<Integer, Integer>>> fallOut, List<HashMap<Local, Pair<Integer, Integer>>> branchOuts) {
        HashMap<Local, Pair<Integer, Integer>> fout = new HashMap<>();
        HashMap<Local, Pair<Integer, Integer>> bout = new HashMap<>();
        Stmt s = (Stmt) d;
        System.out.println("stmt : " + s);
        if (s instanceof JAssignStmt) {
            handleAssignStmt((JAssignStmt) s, fout);
        } else if (s instanceof JIfStmt) {
            JIfStmt ifStmt = (JIfStmt) s;
            handleIfStmt((JIfStmt) ifStmt, in, fout, bout);
        }
    }

    /**
     * Returns the flow object corresponding to the initial values for each graph node.
     */
    @Override
    protected HashMap<Local, Pair<Integer, Integer>> newInitialFlow() {
        HashMap<Local, Pair<Integer, Integer>> initMap = new HashMap<>();
        Chain<Local> localVars = analysisMethod.getActiveBody().getLocals();
        for (Local l : localVars) {
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
    protected void merge(HashMap<Local, Pair<Integer, Integer>> in1, HashMap<Local, Pair<Integer, Integer>> in2,
                         HashMap<Local, Pair<Integer, Integer>> out) {
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
    protected void copy(HashMap<Local, Pair<Integer, Integer>> source,
                        HashMap<Local, Pair<Integer, Integer>> dest) {
        dest.putAll(source);
    }

    private void handleIfStmt(JIfStmt ifStmt, HashMap<Local, Pair<Integer, Integer>> in, HashMap<Local, Pair<Integer, Integer>> fout,
                              HashMap<Local, Pair<Integer, Integer>> bout) {
        ConditionExpr cond = (ConditionExpr)ifStmt.getCondition();
        Pair<Integer, Integer> interval1, interval2;
        interval1 = in.get((Local) cond.getOp1());
        interval2 = in.get((Local) cond.getOp2());
    }

    private Pair<Integer, Integer> getBot() {
        return new Pair<>(max, max);
    }

    private boolean isbot(Pair<Integer, Integer> p) {
        return (p.getO2().intValue() == max && p.getO1().intValue() == max)
                || (p.getO2().intValue() == min && p.getO1().intValue() == min);
    }

    private boolean isXtremes(Integer a) {
        return a == max || a == min;
    }

    private Boolean hasZero(Pair<Integer, Integer> interval) {
        return 0 >= interval.getO1() && interval.getO2() <= 0;
    }

    private Boolean isPositive(Pair<Integer, Integer> interval) {
        return interval.getO1() >= 0;
    }

    private Boolean ismixed(Pair<Integer, Integer> interval) {
        return !(isPositive(interval) && isNegative(interval));
    }

    private Boolean isNegative(Pair<Integer, Integer> interval) {
        return interval.getO2() < 0;
    }

    Pair<Integer, Integer> getUnitInterval(Integer val) {
        return new Pair<>(val, val);
    }

    private Integer sum(Integer a, Integer b) {
        if (isXtremes(a)) {
            return a;
        } else if (isXtremes(b)) {
            return b;
        } else {
            try {
                Integer val = Math.addExact(a, b);
                return val;
            } catch (ArithmeticException e) {
                return ((a >= 0 && b >= 0) || (a < 0 && b < 0)) ? max : min;
            }

        }
    }

    private Integer diff(Integer a, Integer b) {
        if (isXtremes(a)) {
            return a;
        } else if (isXtremes(b)) {
            return b;
        } else {
            try {
                return Math.subtractExact(a, b);
            } catch (ArithmeticException e) {
                return ((a >= 0 && b >= 0) || (a < 0 && b < 0)) ? max : min;
            }
        }
    }

    private Integer mul(Integer a, Integer b) {
        if (isXtremes(a)) {
            return a;
        } else if (isXtremes(b)) {
            return b;
        } else {
            try {
                return Math.multiplyExact(a, b);
            } catch (ArithmeticException e) {
                return ((a >= 0 && b >= 0) || (a < 0 && b < 0)) ? max : min;
            }
        }
    }

    private Integer div(Integer a, Integer b) { // a/b
        if (isXtremes(a)) {
            return a;
        } else if (isXtremes(b)) {
            return 0;
        } else {
            return a / b;
        }
    }

    private ArrayList<Pair<Integer, Integer>> prepareIntervals(Value lhs, Value rhs, HashMap<Local, Pair<Integer, Integer>> facts) {
        BinopExpr binopExpr = (BinopExpr) rhs;
        Value oper1 = binopExpr.getOp1();
        Value oper2 = binopExpr.getOp2();
        Pair<Integer, Integer> interval1, interval2;

        if (oper1 instanceof IntConstant && oper2 instanceof IntConstant) { // operands are constants
            IntConstant constVal1 = (IntConstant) oper1;
            IntConstant constVal2 = (IntConstant) oper2;
            interval1 = getUnitInterval(constVal1.value);
            interval2 = getUnitInterval(constVal2.value);
        } else if (oper1 instanceof Local && oper2 instanceof IntConstant) { // one of the operands are variables
            IntConstant constVal2 = (IntConstant) oper2;
            interval1 = facts.get(oper1);
            interval2 = getUnitInterval(constVal2.value);
        } else if (oper1 instanceof IntConstant && oper2 instanceof Local) {
            IntConstant constVal1 = (IntConstant) oper1;
            interval1 = getUnitInterval(constVal1.value);
            interval2 = facts.get(oper2);
        } else { // operands are variables
            assert oper1 instanceof Local;
            assert oper2 instanceof Local;
            interval1 = facts.get((Local) oper1);
            interval2 = facts.get((Local) oper2);
        }
        ArrayList<Pair<Integer, Integer>> intervals = new ArrayList<>(Arrays.asList(interval1, interval2));
        return intervals;
    }

    private void handleAddExpr(Value lhs, Value rhs, HashMap<Local, Pair<Integer, Integer>> facts) {
        Local lvar = (Local) lhs;
        ArrayList<Pair<Integer, Integer>> intervals = prepareIntervals(lhs, rhs, facts);
        Pair<Integer, Integer> interval1, interval2;
        interval1 = intervals.get(0);
        interval2 = intervals.get(1);

        if (isbot(interval1) || isbot(interval2)) {
            facts.put(lvar, getBot());
        } else {
            facts.put(lvar, new Pair<>(sum(interval1.getO1(), interval2.getO1()), sum(interval1.getO2(), interval2.getO2())));
        }
    }

    private void handleDiffExpr(Value lhs, Value rhs, HashMap<Local, Pair<Integer, Integer>> facts) {
        Local lvar = (Local) lhs;
        ArrayList<Pair<Integer, Integer>> intervals = prepareIntervals(lhs, rhs, facts);
        Pair<Integer, Integer> interval1, interval2;
        interval1 = intervals.get(0);
        interval2 = intervals.get(1);

        if (isbot(interval1) || isbot(interval2)) {
            facts.put(lvar, getBot());
        } else {
            Integer val1 = diff(interval1.getO1(), interval2.getO1());
            Integer val2 = diff(interval1.getO2(), interval2.getO2());
            Integer val3 = diff(interval1.getO1(), interval2.getO2());
            Integer val4 = diff(interval1.getO2(), interval2.getO1());
            ArrayList<Integer> minmax = new ArrayList<>(Arrays.asList(val1, val2, val3, val4));
            Integer minVal = Collections.min(minmax);
            Integer maxVal = Collections.max(minmax);
            facts.put(lvar, new Pair<>(minVal, maxVal));
        }
    }

    private void handleMulExpr(Value lhs, Value rhs, HashMap<Local, Pair<Integer, Integer>> facts) {
        Local lvar = (Local) lhs;
        ArrayList<Pair<Integer, Integer>> intervals = prepareIntervals(lhs, rhs, facts);
        Pair<Integer, Integer> interval1, interval2;
        interval1 = intervals.get(0);
        interval2 = intervals.get(1);
        if (isbot(interval1) || isbot(interval2)) {
            facts.put(lvar, getBot());
        } else {
            Integer val1 = mul(interval1.getO1(), interval2.getO1());
            Integer val2 = mul(interval1.getO2(), interval2.getO2());
            Integer val3 = mul(interval1.getO1(), interval2.getO2());
            Integer val4 = mul(interval1.getO2(), interval2.getO1());
            ArrayList<Integer> minmax = new ArrayList<>(Arrays.asList(val1, val2, val3, val4));
            Integer minVal = Collections.min(minmax);
            Integer maxVal = Collections.max(minmax);
            facts.put(lvar, new Pair<>(minVal, maxVal));
        }
    }

    private void handleDivExpr(Value lhs, Value rhs, HashMap<Local, Pair<Integer, Integer>> facts) {
        Local lvar = (Local) lhs;
        ArrayList<Pair<Integer, Integer>> intervals = prepareIntervals(lhs, rhs, facts);
        Pair<Integer, Integer> interval1, interval2;
        interval1 = intervals.get(0);
        interval2 = intervals.get(1);

        if (isbot(interval1) || isbot(interval2)) {
            facts.put(lvar, getBot());
        } else if (!hasZero(interval2)) {
            facts.put(lvar, new Pair<>(div(interval1.getO1(), interval2.getO1()), div(interval1.getO2(), interval2.getO2())));
        } else {
            facts.put(lvar, getBot());
        }
    }

    private void handleAssignStmt(JAssignStmt assignStmt, HashMap<Local, Pair<Integer, Integer>> facts) {
        Value rhs = assignStmt.getRightOp();
        Value lhs = assignStmt.getLeftOp();
        Local lvar = (Local) lhs;
        if (rhs instanceof IntConstant) {
            IntConstant constVal = (IntConstant) rhs;
            if (assignStmt.getLeftOp() instanceof Local) {
                if (facts.containsKey((Local) lvar)) {
                    facts.put(lvar, getUnitInterval(constVal.value));
                } else {
                    System.out.println("Error : VARIABLE NOT ANALYZED");
                }
            }
        } else if (rhs instanceof Local) {
            if (facts.containsKey((Local) rhs)) {
                facts.put(lvar, facts.get(rhs));
            }
        } else if (rhs instanceof BinopExpr) {
            BinopExpr binopExpr = (BinopExpr) rhs;
            if (binopExpr instanceof AddExpr) {
                handleAddExpr(lhs, rhs, facts);
            } else if (binopExpr instanceof SubExpr) {
                handleDiffExpr(lhs, rhs, facts);
            } else if (binopExpr instanceof MulExpr) {
                handleMulExpr(lhs, rhs, facts);
            } else if (binopExpr instanceof DivExpr) {
                handleDivExpr(lhs, rhs, facts);
            }
        }
    }
}
