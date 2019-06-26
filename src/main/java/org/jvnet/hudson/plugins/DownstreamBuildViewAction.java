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

import org.jvnet.hudson.plugins.DownstreamBuildViewWrapper.DescriptorImpl;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BallColor;
import hudson.model.Hudson;
import hudson.model.Result;
import hudson.model.Run;
import jenkins.model.Jenkins;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author shinod.mohandas
 *
 */
public class DownstreamBuildViewAction extends AbstractDownstreamBuildViewAction {
    private transient List<DownstreamBuilds> downstreamBuildList;
    private static final transient String NOT_BUILT_NUMBER = "</a>#0000<a>";

    @Override public void onAttached(Run<?, ?> r) {
        super.onAttached(r);
        List<AbstractProject> childs = build.getProject().getDownstreamProjects();
        for (Iterator<AbstractProject> iterator = childs.iterator(); iterator.hasNext();) {
            AbstractProject project = iterator.next();
            addDownstreamBuilds(project.getFullName(),0);
        }
    }

    private List<DownstreamBuilds> findDownstream(List<AbstractProject> childs, int depth,List<Integer> parentChildSize,String upProjectName,int upBuildNumber) {
    	List<DownstreamBuilds> childList = new ArrayList<DownstreamBuilds>();
        for (Iterator<AbstractProject> iterator = childs.iterator(); iterator.hasNext();) {
            AbstractProject project = iterator.next();
            DownstreamBuilds downstreamBuild = new DownstreamBuilds();
            downstreamBuild.setProjectName(project.getFullName());
            downstreamBuild.setProjectUrl(project.getUrl());
            AbstractProject upproject = Hudson.getInstance().getItemByFullName(upProjectName, AbstractProject.class);
            if(upBuildNumber!= 0){
            	AbstractBuild upBuild = (AbstractBuild)upproject.getBuildByNumber(upBuildNumber);
            	if(upBuild != null){
            		for (DownstreamBuildViewAction action : upBuild.getActions(DownstreamBuildViewAction.class)) {
            			downstreamBuild.setBuildNumber(action.getDownstreamBuildNumber(project.getFullName()));
            		}
            	}else {
            		downstreamBuild.setBuildNumber(0);
            	}
            }else{
            	downstreamBuild.setBuildNumber(0);
            }

            downstreamBuild.setDepth(depth);
            if (!(parentChildSize.size() > depth)) {
                parentChildSize.add(childs.size());
            }
            downstreamBuild.setParentChildSize(parentChildSize);
            downstreamBuild.setChildNumber(childs.size());
            List<AbstractProject> childProjects = project.getDownstreamProjects();
            if (!childProjects.isEmpty()) {
                downstreamBuild.setChilds(findDownstream(childProjects,depth + 1, parentChildSize,project.getFullName(),downstreamBuild.getBuildNumber()));
            }
            childList.add(downstreamBuild);
        }
        return childList;
    }

    public class DownstreamBuilds{
    	private String projectName, projectUrl,upProjectName;
        private List<DownstreamBuilds> childs;
        private int depth, childNumber,buildNumber,upBuildNumber;
        private List<Integer> parentChildSize;
        private transient AbstractProject project;
        private transient Run<?, ?> run;

        private void initilize(){
        	project = Hudson.getInstance().getItemByFullName(projectName, AbstractProject.class);
        	run = project.getBuildByNumber(buildNumber);
        }

        public List<Integer> getParentChildSize() {
            return parentChildSize;
        }

        public void setParentChildSize(List<Integer> parentChildSize) {
            this.parentChildSize = parentChildSize;
        }

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }

        public String getProjectUrl() {
            return projectUrl;
        }

        public void setProjectUrl(String projectUrl) {
            this.projectUrl = projectUrl;
        }

        public int getBuildNumber() {
        	return buildNumber;
        }

        public String currentBuildNumber() {
        	if(buildNumber == 0){
        		return NOT_BUILT_NUMBER;
        	}
            return Integer.toString(buildNumber);
        }

        public int getDepth() {
            return depth;
        }

        public void setBuildNumber(int buildNumber) {
            this.buildNumber = buildNumber;
        }

        public int getChildNumber() {
            return childNumber;
        }

        public void setChildNumber(int childNumber) {
            this.childNumber = childNumber;
        }

        public DescriptorImpl getDescriptorImpl() {
    		return (DescriptorImpl) Jenkins.getInstance().getDescriptorOrDie(DownstreamBuildViewWrapper.class);
    	}
        public String getImageUrl() {
        	DescriptorImpl descriptor = getDescriptorImpl();
            if(run == null ){
         	  initilize();
              return BallColor.GREY.getImage();
         	}
        	else if(run.isBuilding())
        	{
                return BallColor.GREY.anime().getImage();
        	}
        	return run.getResult().color.getImage();
        }

        public List<DownstreamBuilds> getChilds() {
            return childs;
        }

        public void setChilds(List<DownstreamBuilds> childs) {
            this.childs = childs;
        }

        public void setDepth(int depth) {
            this.depth = depth;
        }

        public String getStatusMessage() {
        	if(project == null ){
        		initilize();
        	}

            if (run == null) {
                return Result.NOT_BUILT.toString();
            } else if (run.isBuilding()) {
                return run.getDurationString();
            } else {
                return run.getTimestamp().getTime().toString() + " - " + run.getResult().toString();
            }

        }

        public String getUpProjectName() {
    		return upProjectName;
    	}
    	public void setUpProjectName(String upProjectName) {
    		this.upProjectName = upProjectName;
    	}
    	public int getUpBuildNumber() {
    		return upBuildNumber;
    	}
    	public void setUpBuildNumber(int upBuildNumber) {
    		this.upBuildNumber = upBuildNumber;
    	}
    }

    public List<DownstreamBuilds> getDownstreamBuildList() {
        List<AbstractProject> childs = build.getProject().getDownstreamProjects();
        downstreamBuildList = findDownstream(childs, 1, new ArrayList<Integer>(),build.getParent().getFullName(),build.getNumber());
        return downstreamBuildList;
    }

    public void setDownstreamBuildList(List<DownstreamBuilds> downstreamBuildList) {
        this.downstreamBuildList = downstreamBuildList;
    }
 }
