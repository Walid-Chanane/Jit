package com.world;

import java.util.Arrays;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparsers;

public class Main {
    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("jit").build()
            .description("am i really writing git in java ?");
        Subparsers subparsers = parser.addSubparsers().title("Command").dest("command");

        Namespace ns = null;
        try {
            ns = parser.parseArgs(Arrays.copyOfRange(args, 1, args.length));
            ns = parser.parseArgs(Arrays.copyOfRange(args, 1, args.length));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        if (ns.getString("command") == null) {
            System.err.println("Error: a subcommand is required.");
            parser.printHelp();
            System.exit(1);
        }

        switch (ns.getString("command")) {
            case "add":
                cmd_add(args);
                break;
            case "catfile":
                cmd_catfile(args);
                break;
            case "check-ignore":
                cmd_check_ignore(args);
                break;
            case "checkout":
                cmd_checkout(args);
                break;
            case "commit":
                cmd_commit(args);
                break;
            case "hash-object":
                cmd_hash_object(args);
                break;
            case "init":
                cmd_init(args);
                break;
            case "log":
                cmd_log(args);
                break;
            case "ls-files":
                cmd_ls_files(args);
                break;
            case "ls-tree":
                cmd_ls_tree(args);
                break;
            case "rev-parse":
                cmd_rev_parse(args);
                break;
            case "rm":
                cmd_rm(args);
                break;
            case "show-ref":
                cmd_show_ref(args);
                break;
            case "status":
                cmd_status(args);
                break;
            case "tag":
                cmd_tag(args);
                break;
            default:
                System.out.println("Bad command!");
                break;
        }
    }
}