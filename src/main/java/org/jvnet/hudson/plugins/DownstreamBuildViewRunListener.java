package org.jvnet.hudson.plugins;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.listeners.RunListener;
import hudson.XmlFile;
import hudson.BulkChange;
import hudson.model.*;
import hudson.model.listeners.SaveableListener;
import hudson.util.XStream2;
import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import java.util.logging.Logger;

/**
 * This listener adds a {@link DownstreamBuildViewAction} to every new build.
 * 
 * @author Shinod.Mohandas
 */
@SuppressWarnings("unchecked")
@Extension
public final class DownstreamBuildViewRunListener extends RunListener<AbstractBuild> implements Saveable{

    /** The Logger. */
    private static final Logger LOG = Logger.getLogger(DownstreamBuildViewRunListener.class.getName());

    /**
     * {@link Extension} needs parameterless constructor.
     */
    public DownstreamBuildViewRunListener() {
        super(AbstractBuild.class);
    }
    
    private AbstractBuild<?, ?> build;
    
 //   public static final XStream XSTREAM = new XStream2();

    /**
     * {@inheritDoc}
     * 
     * Adds {@link DownstreamBuildViewAction} to the build. Do this in <tt>onCompleted</tt>
     * affected.
     */
    @Override
    public void onCompleted(AbstractBuild r,TaskListener listener) {
    	build = r;
    	final DownstreamBuildViewAction downstreamBuildViewAction = new DownstreamBuildViewAction(r);
        r.addAction(downstreamBuildViewAction);
        LOG.info(r.toString() + ":" + r.getActions().toString());
        LOG.info("Registering " + downstreamBuildViewAction.getDisplayName() + " for " + r);
        super.onFinalized(r);
        save();
    	
    }
    
    public synchronized void save() {
        if(BulkChange.contains(this))   return;
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
