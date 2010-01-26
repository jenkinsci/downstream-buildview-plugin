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

import hudson.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.kohsuke.stapler.export.Exported;


/**
 * @author shinod.mohandas
 * 
 */
public abstract class AbstractDownstreamBuildViewAction implements Action  {
	
	/** Our logger. */
    protected static final Logger LOG = Logger.getLogger(AbstractDownstreamBuildViewAction.class.getName());
    
    
    

	public final AbstractBuild<?, ?> build;
	
	private Map<String,Integer> downstreamBuilds;

	protected AbstractDownstreamBuildViewAction(AbstractBuild build) {
		this.build = build;
	}

	public String getDisplayName() {
		return "Downstream build view";
		// return Messages.AbstractTestResultAction_getDisplayName();
	}

	@Exported(visibility = 2)
	public String getUrlName() {
		return "downstreambuildview";
	}

	public String getIconFileName() {
		return "clipboard.gif";
	}

	public AbstractBuild getBuild() {
		return build;
	}
	
	public int getDownstreamBuildNumber(String ProjectName) {
		initilizeDownstreamMap();
		return downstreamBuilds.get(ProjectName);
	}
	public void addDownstreamBuilds(String dowmstreamProject,int buildNumber) {
		initilizeDownstreamMap();		
		downstreamBuilds.put(dowmstreamProject, buildNumber);
	}
	
	private void initilizeDownstreamMap(){
		if(downstreamBuilds == null)
			downstreamBuilds = new HashMap<String, Integer>();
	}
	
	
}
