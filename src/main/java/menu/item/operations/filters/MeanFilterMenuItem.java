package menu.item.operations.filters;

import gui.ApplyFilterWindow;
import gui.InputDoubleAction;
import gui.InputDoubleWindow;
import gui.Window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

public class MeanFilterMenuItem  extends JMenuItem {

	private static final long serialVersionUID = 1L;

	public MeanFilterMenuItem(final Window window) {
		super("Mean");

		setEnabled(true);

		this.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				new InputDoubleWindow(window, "Side:", 3.0, new InputDoubleAction() {
					
					public void performAction(Window window, double input) {
						new ApplyFilterWindow(window, window.getFocusedPanel().getImage(), (int)input, 0, 1);
					}
				});
			}
		});
	}

}
