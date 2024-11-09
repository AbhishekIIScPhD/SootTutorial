import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ForwardBranchedFlowAnalysis;
import soot.toolkits.scalar.Pair;
import soot.util.Chain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class APA extends ForwardBranchedFlowAnalysis<HashMap<Local, Set<APA.AllocObj>>> {
    SootMethod analysisMethod;

    protected class AllocObj{
        String aObj;
        Integer label;
    }

    public APA(UnitGraph graph, SootMethod analysisMethod) {
        super(graph);
        this.analysisMethod = analysisMethod;
    }

    /**
     * Given the merge of the <code>in</code> sets, compute the <code>fallOut</code> and <code>branchOuts</code> set for
     * <code>s</code>.
     *
     * @param in
     * @param s
     * @param fallOut
     * @param branchOuts
     */
    @Override
    protected void flowThrough(HashMap<Local, Set<AllocObj>> in, Unit s, List<HashMap<Local, Set<AllocObj>>> fallOut,
                               List<HashMap<Local, Set<AllocObj>>> branchOuts) {

    }

    /**
     * Returns the flow object corresponding to the initial values for each graph node.
     */
    @Override
    protected HashMap<Local, Set<AllocObj>> newInitialFlow() {
        return null;
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
    protected void merge(HashMap<Local, Set<AllocObj>> in1, HashMap<Local, Set<AllocObj>> in2,
                         HashMap<Local, Set<AllocObj>> out) {

    }

    /**
     * Creates a copy of the <code>source</code> flow object in <code>dest</code>.
     *
     * @param source
     * @param dest
     */
    @Override
    protected void copy(HashMap<Local, Set<AllocObj>> source, HashMap<Local, Set<AllocObj>> dest) {

    }
}