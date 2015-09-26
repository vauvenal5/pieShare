/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieshare.piesharecli;

import io.airlift.airline.Cli;
import io.airlift.airline.Help;
import org.pieshare.piesharecli.commands.LoginUICommand;
import sun.security.ssl.Debug;

/**
 *
 * @author vauvenal5
 */
public class Application {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {

		Cli.CliBuilder<Runnable> builder = Cli.<Runnable>builder("pieShare")
				.withDescription("Your PieShare command line tool.")
				.withDefaultCommand(Help.class)
				.withCommands(Help.class, LoginUICommand.class);

		Cli<Runnable> parser = builder.build();
		parser.parse(args).run();
	}

}
