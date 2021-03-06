package menu;

import gui.Window;

import javax.swing.JMenu;

import menu.item.mask.BorderDetectionMenu;
import menu.item.mask.CannyMenuItem;
import menu.item.mask.GaussianLaplacianMenuItem;
import menu.item.mask.GaussianMaskMenuItem;
import menu.item.mask.HisteresisThresholdMenuItem;
import menu.item.mask.HoughCirclesMenuItem;
import menu.item.mask.HoughMenuItem;
import menu.item.mask.LapacianWithCrossersMenuItem;
import menu.item.mask.LaplacianMenuItem;
import menu.item.mask.NotMaximumSupressionMenuItem;
import menu.item.mask.SusanMenuItem;
import menu.item.mask.WithDirectionMenu;

public class MaskMenu extends JMenu {

	private static final long serialVersionUID = 1L;

	public MaskMenu(Window window) {
		super("Mask");
		this.setEnabled(true);

		this.add(new BorderDetectionMenu(window));
		this.add(new WithDirectionMenu(window));
		this.add(new LaplacianMenuItem(window));
		this.add(new LapacianWithCrossersMenuItem(window));
		this.add(new GaussianLaplacianMenuItem(window));
		this.add(new GaussianMaskMenuItem(window));
		this.add(new NotMaximumSupressionMenuItem(window));
		this.add(new HisteresisThresholdMenuItem(window));
		this.add(new CannyMenuItem(window));
		this.add(new SusanMenuItem(window));
		this.add(new HoughMenuItem(window));
		this.add(new HoughCirclesMenuItem(window));
	}
}