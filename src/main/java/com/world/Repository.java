package com.world;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

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
        this.worktree = Paths.get(path);
        this.jitDir = Paths.get(path, ".git");
        if(force == null) force = false;

        // check if .git directory exists
        if(!force && !Files.isDirectory(this.jitDir)){
          throw new Exception("this is not a git repository");
        }

        // create/read the config file
        Path configPath = Paths.get(this.jitDir.toString(), "config");
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
        assert repo_dir(true, repository.jitDir, "branches") != null;
        assert repo_dir(true, repository.jitDir, "objects") != null;
        assert repo_dir(true, repository.jitDir, "refs", "tags") != null;
        assert repo_dir(true, repository.jitDir, "refs", "heads") != null;

        // create description file
        String description = "unnamed repository, edit the description file to name the repository!";
        Path descriptionPath = repo_file(false, repository.jitDir, "description");
        Files.writeString(descriptionPath, description);
        
        //create HEAD file
        String head = "ref: refs/heads/master\n";
        Path headPath = repo_file(false, repository.jitDir, "HEAD");
        Files.writeString(headPath, head);

        Path configPath = repo_file(false, repository.jitDir, "config");

        
    }
    
    private static Path repo_file(boolean mkdir, Path path, String... subPaths) throws Exception{
        if(repo_dir(mkdir, path, Arrays.copyOf(subPaths, subPaths.length - 1)) != null){
            return Paths.get(path.toString(), subPaths.toString());
        }
        return null;
    }

    private static Path repo_dir(boolean mkdir, Path path, String... subPaths) throws Exception{
        Path completePath = Paths.get(path.toString(), subPaths);
        if (Files.exists(completePath)){
            if (Files.isDirectory(completePath)){
                return completePath;
            }else throw new Exception(completePath + " is not a directory!");
        }

        if (mkdir) {
            Files.createDirectories(completePath);
            return completePath;
        } else return null;
    }
}
