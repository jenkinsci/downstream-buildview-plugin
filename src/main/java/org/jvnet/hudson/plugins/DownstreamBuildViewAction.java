/**
 * 
 */
package org.jvnet.hudson.plugins;

import hudson.Extension;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.model.listeners.RunListener;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildTrigger;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.matrix.MatrixAggregatable;
import hudson.matrix.MatrixAggregator;
import hudson.matrix.MatrixBuild;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.logging.Logger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author shinod.mohandas
 *
 */
public class DownstreamBuildViewAction extends AbstractDownstreamBuildViewAction{
	


	private List<DownstreamBuilds> downstreamBuildList;

	public DownstreamBuildViewAction(AbstractBuild<?, ?> build) {
		super(build);
		BuildTrigger buildTrigger = build.getProject().getPublishersList()
				.get(BuildTrigger.class);
		if (buildTrigger != null) {
			List<AbstractProject> childs = buildTrigger.getChildProjects();
			downstreamBuildList = findDownstream(childs, 1,
					new ArrayList<Integer>());
		}
	}

	private List<DownstreamBuilds> findDownstream(
			List<AbstractProject> childs, int depth,
			List<Integer> parentChildSize) {
		List<DownstreamBuilds> childList = new ArrayList<DownstreamBuilds>();
		for (Iterator<AbstractProject> iterator = childs.iterator(); iterator
				.hasNext();) {
			AbstractProject project = iterator.next();
			DownstreamBuilds downstreamBuild = new DownstreamBuilds();
			downstreamBuild.setProjectName(project.getName());
			downstreamBuild.setProjectUrl(project.getUrl());
			downstreamBuild.setBuildNumber(Integer.toString(project
					.getNextBuildNumber()));
			downstreamBuild.setRootURL(Hudson.getInstance().getRootUrl());
			downstreamBuild.setDepth(depth);
			if (!(parentChildSize.size() > depth))
				parentChildSize.add(childs.size());
			downstreamBuild.setParentChildSize(parentChildSize);
			downstreamBuild.setChildNumber(childs.size());
			List<AbstractProject> childProjects = project
					.getDownstreamProjects();
			if (!childProjects.isEmpty()) {
				downstreamBuild.setChilds(findDownstream(childProjects,
						depth + 1, parentChildSize));
			}

			childList.add(downstreamBuild);

		}
		return childList;
	}

	public class DownstreamBuilds {

		private String projectName, projectUrl, buildNumber, imageUrl,
				rootURL, statusMessage;
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
			Run r = proj.getBuildByNumber(Integer.parseInt(buildNumber));
			return rootURL+"images/16x16/"+getIconName(r);
		}

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}

		public List<DownstreamBuilds> getChilds() {
			return childs;
		}

		public void setChilds(List<DownstreamBuilds> childs) {
			this.childs = childs;
		}

		public String getRootURL() {
			if(rootURL == null)
				return "";
			else if (rootURL.contains("job/"))
				return rootURL.substring(0,rootURL.indexOf("job/"));
			else 
				return rootURL;
		}

		public void setRootURL(String rootURL) {
			this.rootURL = rootURL;
		}

		public void setDepth(int depth) {
			this.depth = depth;
		}

		public String getIconName(Run r) {
			if (r == null)
				return BallColor.GREY.anime().getImage();
			else
				return r.getIconColor().getImage();
		}

		public String getStatusMessage() {
			AbstractProject proj = Hudson.getInstance().getItemByFullName(
					projectName, AbstractProject.class);
			Run r = proj.getBuildByNumber(Integer.parseInt(buildNumber));
			if (r == null)
				return Result.NOT_BUILT.toString();
			else if (r.isBuilding())
				return r.getDurationString();
			else
				return r.getTimestamp().getTime().toString();
		}

		public void setStatusMessage(String statusMessage) {
			this.statusMessage = statusMessage;
		}

	}

	public List getDownstreamBuildList() {
		return downstreamBuildList;
	}

	public void setDownstreamBuildList(List downstreamBuildList) {
		this.downstreamBuildList = downstreamBuildList;
	}



}
