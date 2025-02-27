package org.optaplanner.constraint.streams.drools;

import org.drools.core.rule.consequence.InternalMatch;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.event.rule.RuleEventListener;

public final class OptaPlannerRuleEventListener implements RuleEventListener {

    @Override
    public void onUpdateMatch(Match match) {
        undoPreviousMatch((InternalMatch) match);
    }

    @Override
    public void onDeleteMatch(Match match) {
        undoPreviousMatch((InternalMatch) match);
    }

    public void undoPreviousMatch(InternalMatch match) {
        Runnable callback = match.getCallback();
        /*
         * Null callbacks can happen and are safe to ignore.
         *
         * If we insert a fact and then immediately delete it without firing any rules inbetween,
         * a dummy match will be created by Drools and that match will not have our callback in it.
         * Although this is inefficient, it was decided that the cure would have been worse than the disease.
         */
        if (callback != null) {
            callback.run();
            match.setCallback(null);
        }
    }

}
