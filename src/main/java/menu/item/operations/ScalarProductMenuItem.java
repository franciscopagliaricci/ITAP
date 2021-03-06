package menu.item.operations;

import gui.InputDoubleAction;
import gui.InputDoubleWindow;
import gui.Window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

public class ScalarProductMenuItem extends JMenuItem {

	private static final long serialVersionUID = 1L;

	public ScalarProductMenuItem(final Window window) {
		super("Scalar product");

		setEnabled(true);

		this.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				new InputDoubleWindow(window, "Factor", 1.0, new InputDoubleAction() {
					
					public void performAction(Window window, double input) {
						window.getFocusedPanel().getImage().multiply(input);
						window.repaint();
					}
				});
			}
		});
	}
}
