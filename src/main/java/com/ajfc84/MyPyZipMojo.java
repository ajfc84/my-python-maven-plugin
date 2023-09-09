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

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.*;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Mojo(name = "zip")
public class MyPyZipMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    public void execute() throws MojoExecutionException {
        String artifactId = project.getArtifactId();
        String version = project.getVersion();
        String source  = project.getBuild().getDirectory() + "/sources/";
        String target = project.getBuild().getDirectory() + "/" + artifactId + "-" + version + ".zip";

        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(target))) {
            zipFiles(source, zipOut);
        } catch (FileNotFoundException e) {
            getLog().error("Invalid zip filename");
        } catch (IOException e) {
            getLog().error("Could not close zip archive");
        }
    }

    public void zipFiles(String workdir, ZipOutputStream zipOut) {
        getLog().info("Archiving workdir " + workdir);
        try {
            for (File f : Objects.requireNonNull(new File(workdir).listFiles())) {
                String zipName = workdir.replace(project.getBuild().getDirectory() + "/sources/", "") + f.getName();
                if (f.isHidden())
                    continue;
                else if (f.isDirectory()) {
                    if (!zipName.endsWith("/"))
                        zipName += "/";
                    zipOut.putNextEntry(new ZipEntry(zipName));
                    zipOut.closeEntry();
                    zipFiles(f.getPath() + "/", zipOut);
                }
                else if (f.isFile()) {
                    getLog().info("Archiving file " + zipName);
                    FileInputStream fileIn = new FileInputStream(f);
                    zipOut.putNextEntry(new ZipEntry(zipName));
                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = fileIn.read(bytes)) >= 0) {
                        zipOut.write(bytes, 0, length);
                    }
                    fileIn.close();
                }
            }
        } catch (IOException e) {
            getLog().error("Could not archive file to zip");
            getLog().debug(e.getLocalizedMessage());
        }
    }

    public void python_m_build() {
        String cmd = "python3 -m build";
        CommandLine cmdLine = CommandLine.parse(cmd);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(out);
        DefaultExecutor exec = new DefaultExecutor();
        exec.setStreamHandler(handler);
        exec.setWorkingDirectory(new File(project.getBuild().getDirectory()));

        try {
            int exitCode = exec.execute(cmdLine);
            getLog().info(out.toString().trim());
        } catch (IOException e) {
            getLog().info(e.getLocalizedMessage());
        }
    }

}
