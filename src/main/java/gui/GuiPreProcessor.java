package gui;

import gui.views.ribbon.RibbonView;
import javafx.scene.Group;

public class GuiPreProcessor {
	private RibbonView ribbonView = new RibbonView(Launcher.getDatabaseManager());
	private Group collapsedGroup;
	private Group normalGroup;
	private Group snpGroup;
	
	public void createCollapsedRibbons() {
		collapsedGroup = ribbonView.createCollapsedRibbons();
	}
	
	public void createNormalRibbons() {
		normalGroup = ribbonView.createNormalRibbons();
	}
	
	public void createSnips() {
		snpGroup = ribbonView.createSnips();
	}

	public Group getCollapsedGroup() {
		return this.collapsedGroup;
	}
	
	public Group getNormalGroup() {
		return this.normalGroup;
	}
	
	public Group getSnpGroup() {
		return this.snpGroup;
	}
}
