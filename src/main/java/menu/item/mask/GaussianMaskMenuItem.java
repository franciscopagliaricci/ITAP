package menu.item.mask;

import gui.InputDoubleAction;
import gui.InputDoubleWindow;
import gui.Window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import model.Image;
import utils.MaskFactory;

public class GaussianMaskMenuItem extends JMenuItem {

	private static final long serialVersionUID = 1L;

	public GaussianMaskMenuItem(final Window window) {
		super("Gaussian");

		setEnabled(true);

		this.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				new InputDoubleWindow(window, "sigma:", 0.5 , new InputDoubleAction() {
					
					public void performAction(Window window, double input) {
						Image result = window.getFocusedPanel().getImage().clone();
						result.applyMask(MaskFactory.gaussianMask(input));
						window.getUnfocusedPanel().setImage(result);
						window.repaint();
					}
				});
			}
		});
	}
}