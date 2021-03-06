package menu.item.mask;

import gui.DoubleArrayInputAction;
import gui.GenericDoubleInputWindow;
import gui.Window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JMenuItem;

import model.Image;
import model.MaxSynth;
import utils.MaskFactory;

public class CannyMenuItem extends JMenuItem {

	private static final long serialVersionUID = 1L;

	public CannyMenuItem(final Window window) {
		super("Canny");

		setEnabled(true);

		this.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String[] labels = {"t1", "t2", "sigma1", "sigma2", "sigma3"};
				Double[] values = {30.0, 150.0, 0.5, 1.0, 10.0};
				new GenericDoubleInputWindow(window, labels, values, new DoubleArrayInputAction() {
					
					public void performAction(Window window, Map<String, Double> inputs) {
						Image first = window.getFocusedPanel().getImage().clone();
						Image second = window.getFocusedPanel().getImage().clone();
						Image third = window.getFocusedPanel().getImage().clone();

						first.applyMask(MaskFactory.gaussianMask(inputs.get("sigma1")));
						second.applyMask(MaskFactory.gaussianMask(inputs.get("sigma2")));
						third.applyMask(MaskFactory.gaussianMask(inputs.get("sigma3")));

						int[][] derivationDirections1 = first.getDerivationDirections();
						int[][] derivationDirections2 = second.getDerivationDirections();
						int[][] derivationDirections3 = third.getDerivationDirections();

						first.borderWithNoMaximumsDeletion(derivationDirections1);
						second.borderWithNoMaximumsDeletion(derivationDirections2);
						third.borderWithNoMaximumsDeletion(derivationDirections3);

						first.histeresisThreshold(inputs.get("t1"), inputs.get("t2"));
						second.histeresisThreshold(inputs.get("t1"), inputs.get("t2"));
						third.histeresisThreshold(inputs.get("t1"), inputs.get("t2"));
						
						first.synthesize(new MaxSynth(), second, third);

						window.getUnfocusedPanel().setImage(first);
						window.repaint();
					}
				});
			}
		});
	}
}