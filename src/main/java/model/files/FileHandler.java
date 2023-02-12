package model.files;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileHandler {

    private final String workingDirectory;

    public FileHandler(String workingDirectory, String inputDirectory){
        this.workingDirectory = workingDirectory;
        File workingDirectoryFile = new File(workingDirectory);
        if(workingDirectoryFile.exists()){
            System.out.println("Working directory: " + workingDirectory + " was deleted and will be renewed");
            try {
                FileUtils.deleteDirectory(workingDirectoryFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileUtils.copyDirectory(new File(inputDirectory), workingDirectoryFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<BasicFile> loadFiles(){
        return loadFiles(workingDirectory);
    }

    /**
     * rekurzivní průchod, metoda je static kvůli využití načítání jiných souborů (xsd), než které se kopírují do pracovní cache v konstruktoru
     * @param path cesta do složky, ze které se mají načítat soubory
     */
    public static List<BasicFile> loadFilesWithPath(String path){
        return loadFiles(path);
    }

    /**
     * rekurzivně projde všechny složky, zazipované nechá odzipovat a také je napamuje
     * @param path cesta do složky, ze které se má načítat
     * @return list BasicFile
     */
    private static List<BasicFile> loadFiles(String path){
        List<BasicFile> basicFiles = new ArrayList<>();
        final File folder = new File(path);
        for(File loadedFile: folder.listFiles()){
            if(loadedFile.isDirectory()){
                basicFiles.addAll(loadFiles(loadedFile.getPath()));
            } else {
                BasicFile basicFile = new BasicFile(loadedFile);
                basicFiles.add(basicFile);
                if(basicFile.isZipped())
                    basicFiles.addAll(unZipFile(basicFile));
            }
        }

        return basicFiles;
    }

    private static List<BasicFile> unZipFile(BasicFile file){
        String path = file.getPath();
        List<BasicFile> basicFiles = new ArrayList<>();
        String unzippedDirectoryName = path.split(File.pathSeparator)[path.split(File.pathSeparator).length - 1].replace(".zip", "-TOZIP-") + File.pathSeparator;
        if(unzippedDirectoryName.endsWith(";"))
            unzippedDirectoryName = unzippedDirectoryName.replace(";","");
        File directoryForUnzip = new File(unzippedDirectoryName);
        if(!directoryForUnzip.exists()){
            directoryForUnzip.mkdir();
        }
        String finalUnzippedDirectoryName = unzippedDirectoryName;
        URI uri = URI.create("jar:file:"+new File(path).toURI().getRawPath());
        try {
            FileSystems.newFileSystem(uri, Collections.emptyMap())
                    .getRootDirectories()
                    .forEach(root -> {
                        //handle directories
                        try {
                            Files.walk(root).forEach(tempPath -> {
                                try {
                                    if(!Files.exists(Paths.get(finalUnzippedDirectoryName + tempPath))) {
                                        Files.copy(tempPath, Paths.get(finalUnzippedDirectoryName + tempPath));
                                        basicFiles.add(new BasicFile(new File(finalUnzippedDirectoryName + tempPath)));
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        new File(file.getPath()).delete();
        return basicFiles;
    }

    public void exportData(List<BasicFile> dataToExport, String exportDir) throws IOException {
        if(!exportDir.endsWith("/") || !exportDir.endsWith("\\"))
            exportDir += "/";
        File cacheFolder = new File(workingDirectory);
        File exportFolder = new File(exportDir);
        if(exportFolder.exists())
            FileUtils.deleteDirectory(exportFolder);
        FileUtils.copyDirectory(cacheFolder, exportFolder);

        List<BasicFile> dataForExport = new ArrayList<>(dataToExport);
        String finalExportDir = exportDir;
        dataForExport.forEach(basicFile -> basicFile.setPath(basicFile.getPath().replace(workingDirectory.replace("/", "\\"), finalExportDir)));

        for(BasicFile basicFile : dataForExport){
            if(basicFile.isZipped()){
                File zippedFile = new File(basicFile.getPath().replace(".zip", "-TOZIP-"));
                try {
                    Path pathToZipFile = Files.createFile(Paths.get(zippedFile.getPath().replace("-TOZIP-",".zip")));
                    ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(pathToZipFile));
                    Path walkPath = Paths.get(basicFile.getPath().replace(".zip", "-TOZIP-"));
                    Files.walk(walkPath)
                            .filter(path -> !Files.isDirectory(path))
                            .forEach(path -> {
                                ZipEntry zipEntry = new ZipEntry(walkPath.relativize(path).toString());
                                try {
                                    zipOutputStream.putNextEntry(zipEntry);
                                    Files.copy(path,zipOutputStream);
                                    zipOutputStream.closeEntry();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                    FileUtils.deleteDirectory(new File(basicFile.getPath().replace(".zip", "-TOZIP-")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
