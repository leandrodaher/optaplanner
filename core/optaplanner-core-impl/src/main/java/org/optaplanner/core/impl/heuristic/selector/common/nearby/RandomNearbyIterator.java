package org.optaplanner.core.impl.heuristic.selector.common.nearby;

import java.util.Iterator;
import java.util.Random;

import org.optaplanner.core.impl.heuristic.selector.common.iterator.SelectionIterator;
import org.optaplanner.core.impl.util.MemoizingSupply;

public final class RandomNearbyIterator extends SelectionIterator<Object> {

    private final MemoizingSupply<NearbyDistanceMatrix<Object, Object>> nearbyDistanceMatrixSupply;
    private final NearbyRandom nearbyRandom;
    private final Random workingRandom;
    private final Iterator<Object> replayingIterator;
    private final int nearbySize;
    private final boolean discardNearbyIndexZero;

    public RandomNearbyIterator(MemoizingSupply<NearbyDistanceMatrix<Object, Object>> nearbyDistanceMatrixSupply,
            NearbyRandom nearbyRandom, Random workingRandom, Iterator<Object> replayingIterator, long childSize,
            boolean discardNearbyIndexZero) {
        this.nearbyDistanceMatrixSupply = nearbyDistanceMatrixSupply;
        this.nearbyRandom = nearbyRandom;
        this.workingRandom = workingRandom;
        this.replayingIterator = replayingIterator;
        if (childSize > Integer.MAX_VALUE) {
            throw new IllegalStateException("The valueSelector (" + this
                    + ") has an entitySize (" + childSize
                    + ") which is higher than Integer.MAX_VALUE.");
        }
        this.nearbySize = (int) childSize - (discardNearbyIndexZero ? 1 : 0);
        this.discardNearbyIndexZero = discardNearbyIndexZero;
    }

    @Override
    public boolean hasNext() {
        return replayingIterator.hasNext() && nearbySize > 0;
    }

    @Override
    public Object next() {
        /*
         * The origin iterator is guaranteed to be a replaying iterator.
         * Therefore next() will point to whatever that the related recording iterator was pointing to at the time
         * when its next() was called.
         * As a result, origin here will be constant unless next() on the original recording iterator is called
         * first.
         */
        Object origin = replayingIterator.next();
        int nearbyIndex = nearbyRandom.nextInt(workingRandom, nearbySize);
        if (discardNearbyIndexZero) {
            nearbyIndex++;
        }
        return nearbyDistanceMatrixSupply.read().getDestination(origin, nearbyIndex);
    }

}
