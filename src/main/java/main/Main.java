package main;

import gui.Graphdrawer;
import gui.Gui;
import gui.TutorialSceneswitch;
import javafx.application.Application;

import java.io.File;

@SuppressWarnings("restriction")
public abstract class Main extends Application {
	
	private static Class c0 = Gui.class;
	private static Class c1 = TutorialSceneswitch.class;
	private static Class c2 = Graphdrawer.class;
	static Class Tutorials[] = {c0, c1, c2};
	
	/**
	 * Launch application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		launch(Tutorials[2], args);
	}
}
