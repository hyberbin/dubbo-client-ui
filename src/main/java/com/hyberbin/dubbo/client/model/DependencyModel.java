package com.hyberbin.dubbo.client.model;

import java.io.File;
import org.apache.maven.model.Dependency;

public class DependencyModel extends Dependency {

    private File file;

    public DependencyModel(String groupId, String artifactId, String version) {
        this.setArtifactId(artifactId);
        this.setGroupId(groupId);
        this.setVersion(version);
    }

    public DependencyModel(Dependency dependency) {
        this.setArtifactId(dependency.getArtifactId());
        this.setGroupId(dependency.getGroupId());
        this.setVersion(dependency.getVersion());
    }

    public DependencyModel() {
    }


    @Override
    public boolean equals(Object obj) {
        return obj != null
                && ((Dependency) obj).getArtifactId().equals(getArtifactId())
                && ((Dependency) obj).getGroupId().equals(getGroupId())
                && ((Dependency) obj).getVersion().equals(getVersion());
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
