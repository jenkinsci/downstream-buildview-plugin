package org.jvnet.hudson.plugins;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.TransientProjectActionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * Extends project actions for all jobs.
 * 
 * @author mfriedenhagen
 */
@Extension
public class DownStreamProjectActionFactory extends TransientProjectActionFactory {

    /** Our logger. */
    private static final Logger LOG = Logger.getLogger(DownStreamProjectActionFactory.class.getName());

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<? extends Action> createFor(@SuppressWarnings("unchecked") AbstractProject target) {
        LOG.fine(this + " adds JsJobAction for " + target);
        final ArrayList<Action> actions = new ArrayList<Action>();
        

        // add the JsBuildAction to all run builds, JsRunListener will append this to the others.
        final List<?> builds = (List<?>) target.getBuilds();
        for (Object object : builds) {
            final AbstractBuild<?, ?> build = (AbstractBuild<?, ?>) object;
            final List<DownstreamBuildViewAction> dBuildViewAction = build.getActions(DownstreamBuildViewAction.class);
            if (dBuildViewAction.size() == 0) {                
                final DownstreamBuildViewAction downstreamBuildViewAction = new DownstreamBuildViewAction(build);
                build.addAction(downstreamBuildViewAction);
                LOG.fine("Adding " + downstreamBuildViewAction + " to " + build);
            } else {
                LOG.fine(build + " already has " + dBuildViewAction);
            }
            LOG.fine(build + ":" + build.getActions());
        }
        return actions;
    }

}
