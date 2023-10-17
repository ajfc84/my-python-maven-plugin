package com.ajfc84;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;


@Mojo(name = "resources", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class ResourcesMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;
    public void execute() throws MojoExecutionException {
        String source_dir = project.getBuild().getSourceDirectory();
        String destination_dir = project.getBuild().getDirectory() + "/build/";
        File source = new File(source_dir);
        File destination = new File(destination_dir);
        try {
            FileUtils.copyDirectory(source, destination);
            getLog().info("Copying resource files from " + source_dir + " to " + destination_dir);
        } catch (IOException e) {
            getLog().error("Failed to copy dir!");
        }
    }

}
