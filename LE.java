import soot.Local;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.Pair;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

class LE implements FlowSet<HashMap<Local, Pair<Integer, Integer>>> {


    /**
     * Clones the current FlowSet.
     */
    @Override
    public FlowSet<HashMap<Local, Pair<Integer, Integer>>> clone() {
        return null;
    }

    /**
     * returns an empty set, most often more efficient than: <code>((FlowSet)clone()).clear()</code>
     */
    @Override
    public FlowSet<HashMap<Local, Pair<Integer, Integer>>> emptySet() {
        return null;
    }

    /**
     * Copies the current FlowSet into dest.
     *
     * @param dest
     */
    @Override
    public void copy(FlowSet<HashMap<Local, Pair<Integer, Integer>>> dest) {

    }

    /**
     * Sets this FlowSet to the empty set (more generally, the bottom element of the lattice.)
     */
    @Override
    public void clear() {

    }

    /**
     * Returns the union (join) of this FlowSet and <code>other</code>, putting result into <code>this</code>.
     *
     * @param other
     */
    @Override
    public void union(FlowSet<HashMap<Local, Pair<Integer, Integer>>> other) {

    }

    /**
     * Returns the union (join) of this FlowSet and <code>other</code>, putting result into <code>dest</code>.
     * <code>dest</code>, <code>other</code> and <code>this</code> could be the same object.
     *
     * @param other
     * @param dest
     */
    @Override
    public void union(FlowSet<HashMap<Local, Pair<Integer, Integer>>> other, FlowSet<HashMap<Local, Pair<Integer, Integer>>> dest) {

    }

    /**
     * Returns the intersection (meet) of this FlowSet and <code>other</code>, putting result into <code>this</code>.
     *
     * @param other
     */
    @Override
    public void intersection(FlowSet<HashMap<Local, Pair<Integer, Integer>>> other) {

    }

    /**
     * Returns the intersection (meet) of this FlowSet and <code>other</code>, putting result into <code>dest</code>.
     * <code>dest</code>, <code>other</code> and <code>this</code> could be the same object.
     *
     * @param other
     * @param dest
     */
    @Override
    public void intersection(FlowSet<HashMap<Local, Pair<Integer, Integer>>> other, FlowSet<HashMap<Local, Pair<Integer, Integer>>> dest) {

    }

    /**
     * Returns the set difference (this intersect ~other) of this FlowSet and <code>other</code>, putting result into
     * <code>this</code>.
     *
     * @param other
     */
    @Override
    public void difference(FlowSet<HashMap<Local, Pair<Integer, Integer>>> other) {

    }

    /**
     * Returns the set difference (this intersect ~other) of this FlowSet and <code>other</code>, putting result into
     * <code>dest</code>. <code>dest</code>, <code>other</code> and <code>this</code> could be the same object.
     *
     * @param other
     * @param dest
     */
    @Override
    public void difference(FlowSet<HashMap<Local, Pair<Integer, Integer>>> other, FlowSet<HashMap<Local, Pair<Integer, Integer>>> dest) {

    }

    /**
     * Returns true if this FlowSet is the empty set.
     */
    @Override
    public boolean isEmpty() {
        return false;
    }

    /**
     * Returns the size of the current FlowSet.
     */
    @Override
    public int size() {
        return 0;
    }

    /**
     * Adds <code>obj</code> to <code>this</code>.
     *
     * @param obj
     */
    @Override
    public void add(HashMap<Local, Pair<Integer, Integer>> obj) {

    }

    /**
     * puts <code>this</code> union <code>obj</code> into <code>dest</code>.
     *
     * @param obj
     * @param dest
     */
    @Override
    public void add(HashMap<Local, Pair<Integer, Integer>> obj, FlowSet<HashMap<Local, Pair<Integer, Integer>>> dest) {

    }

    /**
     * Removes <code>obj</code> from <code>this</code>.
     *
     * @param obj
     */
    @Override
    public void remove(HashMap<Local, Pair<Integer, Integer>> obj) {

    }

    /**
     * Puts <code>this</code> minus <code>obj</code> into <code>dest</code>.
     *
     * @param obj
     * @param dest
     */
    @Override
    public void remove(HashMap<Local, Pair<Integer, Integer>> obj, FlowSet<HashMap<Local, Pair<Integer, Integer>>> dest) {

    }

    /**
     * Returns true if this FlowSet contains <code>obj</code>.
     *
     * @param obj
     */
    @Override
    public boolean contains(HashMap<Local, Pair<Integer, Integer>> obj) {
        return false;
    }

    /**
     * Returns true if the <code>other</code> FlowSet is a subset of <code>this</code> FlowSet.
     *
     * @param other
     */
    @Override
    public boolean isSubSet(FlowSet<HashMap<Local, Pair<Integer, Integer>>> other) {
        return false;
    }

    /**
     * returns an iterator over the elements of the flowSet. Note that the iterator might be backed, and hence be faster in the
     * creation, than doing <code>toList().iterator()</code>.
     */
    @Override
    public Iterator<HashMap<Local, Pair<Integer, Integer>>> iterator() {
        return null;
    }

    /**
     * Returns an unbacked list of contained objects for this FlowSet.
     */
    @Override
    public List<HashMap<Local, Pair<Integer, Integer>>> toList() {
        return List.of();
    }
}