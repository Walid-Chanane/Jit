package com.world;

import java.nio.file.Files;
import java.nio.file.Path;

import org.ini4j.Ini;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Repository {
    private Path worktree;
    private Path jitDir;
    private Ini config;

    public Repository(String path, Boolean force) throws Exception{
        // initialize repository's attributes
        this.worktree = Path.of(path);
        this.jitDir = Path.of(path, ".jit");
        if(force == null) force = false;

        // check if .jit directory exists
        if(!force && !Files.isDirectory(this.jitDir)){
          throw new Exception("this is not a jit repository");
        }

        // create/read the config file
        Path configPath = Path.of(this.jitDir.toString(), "config");
        if (Files.exists(configPath)) {
            if(Files.isRegularFile(configPath)) {
                this.config = new Ini();
                this.config.load(configPath.toFile());
            }
        } else if (!force) {
            throw new Exception("config file missing!");
        }

        //check the version in the config file
        if (!force){
            String version = config.get("core", "repositoryformatversion");
            if (version.equals("0")) throw new Exception("Weird version: " + version);
        }
    }

    public static void createRepository(String path) throws Exception{
        Repository repository = new Repository(path, true);
        // make sure the directory either exists or is empty
        if (Files.exists(repository.worktree)) {
            if (!Files.isDirectory(repository.worktree)) {
                throw new Exception(repository.worktree + " is not a directory!");
            }
            if (Files.exists(repository.jitDir) &&  Files.list(repository.jitDir) != null) {
                throw new Exception("Jit directory not empty!");
            }
        } else {
            Files.createDirectories(repository.worktree);
        }

        // check the subdirectories, if non existant, create them
        Files.createDirectories(Path.of(repository.jitDir.toString(), "branches" ));
        Files.createDirectories(Path.of(repository.jitDir.toString(), "objects" ));
        Files.createDirectories(Path.of(repository.jitDir.toString(), "refs", "tags" ));
        Files.createDirectories(Path.of(repository.jitDir.toString(), "refs", "heads" ));

        // create description file
        String description = "unnamed repository, edit the description file to name the repository!\n";
        Path descriptionPath = Path.of(repository.jitDir.toString(), "description");
        Files.writeString(descriptionPath, description);
        
        //create HEAD file
        String head = "ref: refs/heads/master\n";
        Path headPath = Path.of(repository.jitDir.toString(), "HEAD");
        Files.writeString(headPath, head);
        
        //create the config file
        Path configPath = Path.of(repository.jitDir.toString(), "config");
        String configContent = repo_default_config().toString();
        Files.writeString(configPath, configContent);

    }
    
    //returns a default config file
    private static Ini repo_default_config(){
        Ini conf = new Ini();
        conf.add("core");
        conf.add("core", "repositoryformatversion", 0);
        conf.add("core", "filemode", false);
        conf.add("core", "bare", false);
        return conf;
    }

    // find the root directory of the current jit repository
    private static Repository findRepository(Path path, Boolean required) throws Exception{
        if (path == null) path = Path.of(".");
        if (required == null) required = false;

        path = path.toAbsolutePath();

        // if the directory contains the jit directory then it is the root of the repository
        if(Files.isDirectory(Path.of(path.toString(), ".jit"))){
            return new Repository(path.toString(), false);
        }

        // if the parent is the same as the path it means we are in the root directory, there is no parent
        Path parent = Path.of(path.toString(), "..");
        if (parent == path){
            if (required){
                throw new Exception("yo wtf");
            } else {
                return null;
            }
        }

        // check the predecessors
        return findRepository(path, required);
    }
}
