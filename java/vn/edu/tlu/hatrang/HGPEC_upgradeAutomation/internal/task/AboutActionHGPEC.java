package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task;

import java.awt.event.ActionEvent;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;

public class AboutActionHGPEC extends AbstractCyAction {

    public AboutActionHGPEC() {

        super("About...");
        setPreferredMenu("Apps.autoHGPEC");

    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        AboutDialogHGPEC aboutActionHGPECPanel = new AboutDialogHGPEC(null, true);
        aboutActionHGPECPanel.setLocationRelativeTo(null); //should center on screen

        aboutActionHGPECPanel.setVisible(true);
    }

}
