package menu;

import gui.Window;

import javax.swing.JMenu;

import menu.item.mask.BorderDetectionMenu;
import menu.item.operations.AddImagesMenuItem;
import menu.item.operations.AnisotropicDiffusionMenu;
import menu.item.operations.ApplyFilterMenu;
import menu.item.operations.ApplyNoiseMenu;
import menu.item.operations.ApplyThresholdMenuItem;
import menu.item.operations.DynamicRangeCompressionMenuItem;
import menu.item.operations.IncreaseContrastMenuItem;
import menu.item.operations.IsotropicDiffusionMenuItem;
import menu.item.operations.MultiplyImagesMenuItem;
import menu.item.operations.NegativeMenuItem;
import menu.item.operations.ScalarProductMenuItem;
import menu.item.operations.SubstractImagesMenuItem;
import menu.item.operations.ToGrayscaleMenuItem;

public class OperationsMenu extends JMenu {

	private static final long serialVersionUID = 1L;

	public OperationsMenu(Window window) {
		super("Operations");
		this.setEnabled(true);

		this.add(new AddImagesMenuItem(window));
		this.add(new SubstractImagesMenuItem(window));
		this.add(new MultiplyImagesMenuItem(window));
		this.add(new ScalarProductMenuItem(window));
		this.add(new ToGrayscaleMenuItem(window));
		this.add(new IncreaseContrastMenuItem(window));
		this.add(new DynamicRangeCompressionMenuItem(window));
		this.add(new NegativeMenuItem(window));
		this.add(new ApplyThresholdMenuItem(window));
		this.add(new ApplyNoiseMenu(window));
		this.add(new ApplyFilterMenu(window));
		this.add(new IsotropicDiffusionMenuItem(window));
		this.add(new AnisotropicDiffusionMenu(window));
		this.add(new BorderDetectionMenu(window));
	}
}
