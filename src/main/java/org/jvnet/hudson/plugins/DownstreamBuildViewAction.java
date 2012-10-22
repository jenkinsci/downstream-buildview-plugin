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

import java.util.*;

/**
 * @author shinod.mohandas
 *
 */
public class DownstreamBuildViewAction extends AbstractDownstreamBuildViewAction {

    private transient List<DownstreamBuilds> downstreamBuildList;
    private transient String rootURL;
    private static final transient String NOT_BUILT_NUMBER = "</a>#0000<a>";

    public DownstreamBuildViewAction(AbstractBuild<?, ?> build) {
        super(build);
        List<AbstractProject> childs = build.getProject().getDownstreamProjects();
        for (Iterator<AbstractProject> iterator = childs.iterator(); iterator.hasNext();) {
            AbstractProject project = iterator.next();
            addDownstreamBuilds(project.getFullName(),0);
        }
        rootURL = Hudson.getInstance().getRootUrl();
    }

    private List<DownstreamBuilds> findDownstream(HashMap<AbstractProject,
            HashSet<AbstractBuild>> childProjects,
            int depth, List<Integer> parentChildSize){
    	List<DownstreamBuilds> childList = new ArrayList<DownstreamBuilds>();
        Integer childsCount = getChildsCount(childProjects);

        for(Map.Entry<AbstractProject,HashSet<AbstractBuild>> entry : childProjects.entrySet() ){
            AbstractProject downProject = entry.getKey();
            HashSet<AbstractBuild> downBuilds = entry.getValue();

            if (!(parentChildSize.size() > depth)) {
                parentChildSize.add(childsCount);
            }
            List<AbstractProject> downProjects = downProject.getDownstreamProjects();
            if(downBuilds.isEmpty()){
                DownstreamBuilds downstreamBuild = instantiateDownstreamBuids(depth, parentChildSize, childsCount, downProject);
                if (!downProjects.isEmpty()) {
                    HashMap<AbstractProject,HashSet<AbstractBuild>> downChildProjects = getImmediateDownstreamProjectsToBuildsMap(downProjects, null);
                    downstreamBuild.setChilds(findDownstream(downChildProjects, depth + 1, parentChildSize));
                }
                childList.add(downstreamBuild);
            } else {
                for(AbstractBuild downBuild : downBuilds){
                    DownstreamBuilds downstreamBuild = instantiateDownstreamBuids(depth, parentChildSize, childsCount, downProject);
                    downstreamBuild.setBuildNumber(downBuild.getNumber());
                    if (!downProjects.isEmpty()) {
                        HashMap<AbstractProject,HashSet<AbstractBuild>> downChildProjects = getImmediateDownstreamProjectsToBuildsMap(downProjects, downBuild);
                        downstreamBuild.setChilds(findDownstream(downChildProjects, depth + 1, parentChildSize));
                    }
                    childList.add(downstreamBuild);
                }
            }
        }

        return childList;
    }

    private HashMap<AbstractProject, HashSet<AbstractBuild>> getImmediateDownstreamProjectsToBuildsMap(List<AbstractProject> downstreamProjects, AbstractBuild build) {
        HashMap<AbstractProject,HashSet<AbstractBuild>> result = new HashMap<AbstractProject, HashSet<AbstractBuild>>();
        for(AbstractProject downProject : downstreamProjects){
            if(!result.containsKey(downProject)){
                result.put(downProject, new HashSet<AbstractBuild>());
            }
            if(build != null && build.getNumber() != 0){
                Fingerprint.RangeSet downstreamBuildsRange = build.getDownstreamRelationship(downProject);
                if(!downstreamBuildsRange.isEmpty()){
                    List<AbstractBuild> downBuilds =  (List<AbstractBuild>)downProject.getBuilds(downstreamBuildsRange);
                    for(AbstractBuild downBuild : downBuilds){
                        for(Object cause  : downBuild.getCauses()){
                            if(cause instanceof Cause.UpstreamCause){
                                if(((Cause.UpstreamCause)cause).pointsTo(build)){
                                    result.get(downProject).add(downBuild);
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private HashMap<AbstractProject,HashSet<AbstractBuild>> getImmediateDownstreamProjectsToBuildsMap(AbstractProject project, AbstractBuild build){
        List<AbstractProject> downstreamProjects = (List<AbstractProject>)project.getDownstreamProjects();
        HashMap<AbstractProject, HashSet<AbstractBuild>> result = getImmediateDownstreamProjectsToBuildsMap(downstreamProjects, build);
        return result;
    }

    private DownstreamBuilds instantiateDownstreamBuids(int depth, List<Integer> parentChildSize, Integer childsCount, AbstractProject downProject) {
        DownstreamBuilds downstreamBuild = new DownstreamBuilds();
        downstreamBuild.setProjectName(downProject.getFullName());
        downstreamBuild.setProjectUrl(downProject.getUrl());
        downstreamBuild.setBuildNumber(0);
        downstreamBuild.setDepth(depth);
        downstreamBuild.setParentChildSize(parentChildSize);
        downstreamBuild.setChildNumber(childsCount);
        return downstreamBuild;
    }

    private Integer getChildsCount(HashMap<AbstractProject, HashSet<AbstractBuild>> immediateDownstreamProjects) {
        Integer childsCount = 0;
        for(Map.Entry<AbstractProject,HashSet<AbstractBuild>> entry : immediateDownstreamProjects.entrySet() ){
            //if downstream project has running direct builds, then counting builds count, else counting single project as child
            childsCount = childsCount + entry.getValue().size() > 0 ? entry.getValue().size() : 1;
        }
        return childsCount;
    }

    public class DownstreamBuilds {

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

        public String getRootURL() {
            return rootURL;
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

        public String getImageUrl() {
        	if(run == null ){
        		initilize();
        	}
        	if (run == null || run.isBuilding()) {
                return BallColor.GREY.anime().getImage();
            } else {
                return run.getResult().color.getImage();
            }
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

    public String getRootURL() {
        return rootURL;
    }
    
    public List<DownstreamBuilds> getDownstreamBuildList() {
        HashMap<AbstractProject,HashSet<AbstractBuild>> immediateDownstreamProjectsBuilds = getImmediateDownstreamProjectsToBuildsMap(build.getProject(), build);
        downstreamBuildList = findDownstream(immediateDownstreamProjectsBuilds, 1, new ArrayList<Integer>());
        return downstreamBuildList;
    }

    public void setDownstreamBuildList(List<DownstreamBuilds> downstreamBuildList) {
        this.downstreamBuildList = downstreamBuildList;
    }
 }
