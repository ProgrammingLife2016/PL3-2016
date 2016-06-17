package gui;

import javafx.scene.Group;

import gui.views.ribbon.RibbonView;

public class GuiPreProcessor {
	private RibbonView ribbonView = new RibbonView(Launcher.getDatabaseManager());
	private Group collapsedGroup;
	private Group normalGroup;
	private Group snpGroup;
	private Group inDelGroup;
	
	public void createCollapsedRibbons() {
		collapsedGroup = ribbonView.createCollapsedRibbons();
	}
	
	public void createNormalRibbons() {
		normalGroup = ribbonView.createNormalRibbons();
	}
	
	public void createSnips() {
		snpGroup = ribbonView.createSnips();
	}
	
	public void createInDels() {
		inDelGroup = ribbonView.createInDels();
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
	
	public Group getInDelGroup() {
		return this.inDelGroup;
	}
}
