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
import hudson.model.Cause.UpstreamCause;
import hudson.model.listeners.RunListener;
import hudson.XmlFile;
import hudson.BulkChange;
import hudson.model.*;
import hudson.model.listeners.SaveableListener;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import java.util.logging.Logger;

/**
 * This listener Updtes all build number {@link DownstreamBuildViewAction} to every new build.
 * 
 * @author Shinod.Mohandas
 */
@SuppressWarnings("unchecked")
@Extension
public final class DownstreamBuildViewUpdateListener extends RunListener<AbstractBuild> implements Saveable{

    /** The Logger. */
    private static final Logger LOG = Logger.getLogger(DownstreamBuildViewUpdateListener.class.getName());

    /**
     * {@link Extension} needs parameterless constructor.
     */
    public DownstreamBuildViewUpdateListener() {
        super(AbstractBuild.class);
    }
    
    private AbstractBuild<?, ?> build;
    

    /**
     * {@inheritDoc}
     * 
     * Adds {@link DownstreamBuildViewAction} to the build. Do this in <tt>onCompleted</tt>
     * affected.
     */
    @Override
    public void onStarted(AbstractBuild r,TaskListener listener) {
        r.addAction(new DownstreamBuildViewAction(r));

    	CauseAction ca = r.getAction(CauseAction.class);
        if (ca == null || ca.getCauses() ==null) {
            return;
        }
    	for (Cause c : ca.getCauses()){
    		if( c instanceof UpstreamCause){
    			UpstreamCause upcause = (UpstreamCause)c;
    			String upProjectName = upcause.getUpstreamProject();
    			int buildNumber = upcause.getUpstreamBuild();
    			AbstractProject project = Hudson.getInstance().getItemByFullName(upProjectName, AbstractProject.class);
    			AbstractBuild upBuild = (AbstractBuild)project.getBuildByNumber(buildNumber);
    			build = upBuild;
    			for (DownstreamBuildViewAction action : upBuild.getActions(DownstreamBuildViewAction.class)) {
    				action.addDownstreamBuilds(r.getProject().getFullName(),r.getNumber());
        		}
    			super.onFinalized(build);
                save();
    		}
    	}
    }
    
    public synchronized void save() {
        if(BulkChange.contains(this)) {
        	return;
        }
        try {
        	getConfigFile().write(build);
            SaveableListener.fireOnChange(this, getConfigFile());
        } catch (IOException e) {
        	LOG.info("Failed to save ");
        }
    }
	
	private XmlFile getConfigFile() {
		Run r= (Run)build;
		return new XmlFile(r.XSTREAM,new File(r.getRootDir(),"build.xml" ));
	}

    
}
