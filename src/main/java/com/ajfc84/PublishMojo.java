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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.*;
import java.util.Map;


@Mojo(name = "publish")
public class PublishMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    public void execute() throws MojoExecutionException {
        ProcessBuilder builder = new ProcessBuilder("./ops.sh", "--publish");
        Map<String, String> env = builder.environment();
        builder.directory(new File(project.getBuild().getDirectory() + "/sources/"));
        builder.redirectErrorStream(true);
        builder.inheritIO();
        try {
            Process process = builder.start();
            process.waitFor();
        } catch (IOException e) {
            getLog().error(e.getLocalizedMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
