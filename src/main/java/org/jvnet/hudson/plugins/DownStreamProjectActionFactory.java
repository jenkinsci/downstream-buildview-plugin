/*
 * The MIT License
 * 
 * Copyright (c) 2009, Ushus Technologies LTD.,Shinod K Mohandas
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
        LOG.fine(this + " adds DownStreamProjectAction for " + target);
        final ArrayList<Action> actions = new ArrayList<Action>();
        

        // add the  DownstreamBuildViewAction to all run builds, DownstreamBuildViewRunListener will append this to the others.
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
