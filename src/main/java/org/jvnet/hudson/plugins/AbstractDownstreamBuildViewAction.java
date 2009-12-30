/**
 * 
 */
package org.jvnet.hudson.plugins;

import hudson.model.*;
import org.kohsuke.stapler.export.Exported;

/**
 * @author shinod.mohandas
 *
 */
public abstract class AbstractDownstreamBuildViewAction implements Action{
	
	public final AbstractBuild<?,?> build;

    protected AbstractDownstreamBuildViewAction(AbstractBuild build) {
        this.build = build;
    }
    
    
    public String getDisplayName() {
    	return "Downstream build view";
    	//return Messages.AbstractTestResultAction_getDisplayName();
    }
    
    @Exported(visibility=2)
    public String getUrlName() {
        return "downstreambuildview";
    }

    public String getIconFileName() {
        return "clipboard.gif";
    }
    public AbstractBuild getBuild(){
    	return build;
    }
   

}
