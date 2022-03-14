package model.files;

import java.io.File;

public class BasicFile {

    private String path;
    private String name;
    private boolean zipped;

    public BasicFile(File file){
        this.path = file.getPath();
        this.name = file.getName();
        this.zipped = file.getPath().endsWith(".zip");
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isZipped() {
        return zipped;
    }

    public void setZipped(boolean zipped) {
        this.zipped = zipped;
    }

    @Override
    public String toString() {
        return "BasicFile{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", zipped=" + zipped +
                '}';
    }
}
