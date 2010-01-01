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
        LOG.info(this + " adds DownStreamProjectAction for " + target);
        final ArrayList<Action> actions = new ArrayList<Action>();
        

        // add the  DownstreamBuildViewAction to all run builds, DownstreamBuildViewRunListener will append this to the others.
        final List<?> builds = (List<?>) target.getBuilds();
        for (Object object : builds) {
            final AbstractBuild<?, ?> build = (AbstractBuild<?, ?>) object;
            final List<DownstreamBuildViewAction> dBuildViewAction = build.getActions(DownstreamBuildViewAction.class);
            if (dBuildViewAction.size() == 0) {                
                final DownstreamBuildViewAction downstreamBuildViewAction = new DownstreamBuildViewAction(build);
                build.addAction(downstreamBuildViewAction);
                LOG.info("Adding " + downstreamBuildViewAction + " to " + build);
            } else {
                LOG.info(build + " already has " + dBuildViewAction);
            }
            LOG.info(build + ":" + build.getActions());
        }
        return actions;
    }

}
