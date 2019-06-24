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
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * This DownstreamBuildViewWrapper removes Grey color ball blinking to every downstream job.
 * 
 * @author Uppuluri Mahesh
 */
 public class DownstreamBuildViewWrapper extends BuildWrapper { 
	 
	@Extension
	@Symbol("disableBlink")
 	public static final class DescriptorImpl extends BuildWrapperDescriptor {

 		private boolean disableBlink = false;

		public DescriptorImpl() {
 			load();
 		}

 		@Override
 		public boolean isApplicable(AbstractProject<?, ?> abstractProject) {
 			return false;
 		}

 		public String getDisplayName() {
 			return "";
 		}

 		@Override
 		public boolean configure(StaplerRequest req, JSONObject formData) throws Descriptor.FormException {
 			this.disableBlink = false;
 			disableBlink = formData.getBoolean("disableBlink");
 			save();
			return super.configure(req, formData);
 		}
 		
 		public boolean isDisableBlink() {
			return disableBlink;
		}
 		
 		@DataBoundSetter
		public void setDisableBlink(boolean disableBlink) {
			this.disableBlink = disableBlink;
		}

 	}
 }

 