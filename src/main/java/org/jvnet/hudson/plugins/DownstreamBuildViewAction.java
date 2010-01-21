package org.jvnet.hudson.plugins;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BallColor;
import hudson.model.Hudson;
import hudson.model.Result;
import hudson.model.Run;
import hudson.tasks.BuildTrigger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author shinod.mohandas
 *
 */
public class DownstreamBuildViewAction extends AbstractDownstreamBuildViewAction {

    private List<DownstreamBuilds> downstreamBuildList;
    private transient String rootURL;

    public DownstreamBuildViewAction(AbstractBuild<?, ?> build) {
        super(build);
        BuildTrigger buildTrigger = build.getProject().getPublishersList().get(BuildTrigger.class);
        if (buildTrigger != null) {
            List<AbstractProject> childs = buildTrigger.getChildProjects();
            downstreamBuildList = findDownstream(childs, 1, new ArrayList<Integer>());
        }
        rootURL = Hudson.getInstance().getRootUrl();
    }

    private List<DownstreamBuilds> findDownstream(
            List<AbstractProject> childs, int depth,
            List<Integer> parentChildSize) {
        List<DownstreamBuilds> childList = new ArrayList<DownstreamBuilds>();
        for (Iterator<AbstractProject> iterator = childs.iterator(); iterator.hasNext();) {
            AbstractProject project = iterator.next();
            DownstreamBuilds downstreamBuild = new DownstreamBuilds();
            downstreamBuild.setProjectName(project.getName());
            downstreamBuild.setProjectUrl(project.getUrl());
            downstreamBuild.setBuildNumber(Integer.toString(project.getNextBuildNumber()));
            downstreamBuild.setDepth(depth);
            if (!(parentChildSize.size() > depth)) {
                parentChildSize.add(childs.size());
            }
            downstreamBuild.setParentChildSize(parentChildSize);
            downstreamBuild.setChildNumber(childs.size());
            List<AbstractProject> childProjects = project.getDownstreamProjects();
            if (!childProjects.isEmpty()) {
                downstreamBuild.setChilds(findDownstream(childProjects,
                        depth + 1, parentChildSize));
            }

            childList.add(downstreamBuild);

        }
        return childList;
    }

    public class DownstreamBuilds {

        private String projectName, projectUrl, buildNumber;
        private List<DownstreamBuilds> childs;
        private int depth, childNumber;
        private List<Integer> parentChildSize;

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

        public String getBuildNumber() {
            return buildNumber;
        }

        public int getDepth() {
            return depth;
        }

        public void setBuildNumber(String buildNumber) {
            this.buildNumber = buildNumber;
        }

        public int getChildNumber() {
            return childNumber;
        }

        public void setChildNumber(int childNumber) {
            this.childNumber = childNumber;
        }

        public String getImageUrl() {
            AbstractProject proj = Hudson.getInstance().getItemByFullName(
                    projectName, AbstractProject.class);
            Run<?, ?> r = proj.getBuildByNumber(Integer.parseInt(buildNumber));
            return getIconName(r);
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

        public String getIconName(Run<?, ?> r) {
            if (r == null || r.isBuilding()) {
                return BallColor.GREY.anime().getImage();
            } else {
                return r.getResult().color.getImage();
            }
        }

        public String getStatusMessage() {
            AbstractProject proj = Hudson.getInstance().getItemByFullName(
                    projectName, AbstractProject.class);
            Run<?, ?> r = proj.getBuildByNumber(Integer.parseInt(buildNumber));
            if (r == null) {
                return Result.NOT_BUILT.toString();
            } else if (r.isBuilding()) {
                return r.getDurationString();
            } else {
                return r.getTimestamp().getTime().toString() + " - " + r.getResult().toString();
            }
        }
    }

    public String getRootURL() {
        return rootURL;
    }

    public List<DownstreamBuilds> getDownstreamBuildList() {
        return downstreamBuildList;
    }

    public void setDownstreamBuildList(List<DownstreamBuilds> downstreamBuildList) {
        this.downstreamBuildList = downstreamBuildList;
    }
}
