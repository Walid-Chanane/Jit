package com.world;

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
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
        // for later. to manually make the sub command required.
        if (ns.getString("command") == null) {
            System.err.println("Error: a subcommand is required.");
            parser.printHelp();
            System.exit(1);
        }
    }
}