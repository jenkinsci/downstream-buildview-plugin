package org.jvnet.hudson.plugins;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.listeners.RunListener;

import java.util.logging.Logger;

/**
 * This listener adds a {@link DownstreamBuildViewAction} to every new build.
 * 
 * @author Shinod.Mohandas
 */
@SuppressWarnings("unchecked")
@Extension
public final class DownstreamBuildViewRunListener extends RunListener<AbstractBuild> {

    /** The Logger. */
    private static final Logger LOG = Logger.getLogger(DownstreamBuildViewRunListener.class.getName());

    /**
     * {@link Extension} needs parameterless constructor.
     */
    public DownstreamBuildViewRunListener() {
        super(AbstractBuild.class);
    }

    /**
     * {@inheritDoc}
     * 
     * Adds {@link DownstreamBuildViewAction} to the build. Do this in <tt>onFinalized</tt>, so the XML-data of the build is not
     * affected.
     */
    @Override
    public void onFinalized(AbstractBuild r) {
    	final DownstreamBuildViewAction downstreamBuildViewAction = new DownstreamBuildViewAction(r);
        r.addAction(downstreamBuildViewAction);
        LOG.info(r.toString() + ":" + r.getActions().toString());
        LOG.info("Registering " + downstreamBuildViewAction.getDisplayName() + " for " + r);
        super.onFinalized(r);
    	
    }
}
