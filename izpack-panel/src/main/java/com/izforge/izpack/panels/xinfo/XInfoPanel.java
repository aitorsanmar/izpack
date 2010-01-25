/*
 * IzPack - Copyright 2001-2008 Julien Ponge, All Rights Reserved.
 * 
 * http://izpack.org/
 * http://izpack.codehaus.org/
 * 
 * Copyright 2001 Johannes Lehtinen 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.izforge.izpack.panels.xinfo;

import com.izforge.izpack.api.data.ResourceManager;
import com.izforge.izpack.gui.LabelFactory;
import com.izforge.izpack.installer.base.InstallerFrame;
import com.izforge.izpack.installer.base.IzPanel;
import com.izforge.izpack.installer.data.GUIInstallData;

import javax.swing.*;
import java.awt.*;

/**
 * The XInfo panel class - shows some adaptative text (ie by parsing for some variables.
 *
 * @author Julien Ponge
 */
public class XInfoPanel extends IzPanel {

    /**
     *
     */
    private static final long serialVersionUID = 3257009856274970416L;

    /**
     * The text area.
     */
    private JTextArea textArea;

    /**
     * The info to display.
     */
    private String info;

    /**
     * The constructor.
     *
     * @param parent The parent window.
     * @param idata  The installation installDataGUI.
     */
    public XInfoPanel(InstallerFrame parent, GUIInstallData idata, ResourceManager resourceManager) {
        super(parent, idata, resourceManager);

        // We initialize our layout
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gbConstraints = new GridBagConstraints();
        setLayout(layout);

        // We add the components

        JLabel infoLabel = LabelFactory.create(installData.getLangpack().getString("InfoPanel.info"), parent.icons
                .getImageIcon("edit"), JLabel.TRAILING);
        parent.buildConstraints(gbConstraints, 0, 0, 1, 1, 1.0, 0.0);
        gbConstraints.insets = new Insets(5, 5, 5, 5);
        gbConstraints.fill = GridBagConstraints.BOTH;
        gbConstraints.anchor = GridBagConstraints.SOUTHWEST;
        layout.addLayoutComponent(infoLabel, gbConstraints);
        add(infoLabel);

        textArea = new JTextArea();
        textArea.setEditable(false);

        String textAreaFont = idata.getVariable("XInfoPanel.font");
        if (textAreaFont != null && textAreaFont.length() > 0) {
            Font font = Font.decode(textAreaFont);
            textArea.setFont(font);
        }

        JScrollPane scroller = new JScrollPane(textArea);
        parent.buildConstraints(gbConstraints, 0, 1, 1, 1, 1.0, 0.9);
        gbConstraints.anchor = GridBagConstraints.CENTER;
        layout.addLayoutComponent(scroller, gbConstraints);
        add(scroller);
    }

    /**
     * Loads the info text.
     */
    private void loadInfo() {
        try {
            // We read it
            info = resourceManager.getTextResource("XInfoPanel.info");
        }
        catch (Exception err) {
            info = "Error : could not load the info text !";
        }
    }

    /**
     * Parses the text for special variables.
     */
    private void parseText() {
        try {
            // Parses the info text
            info = variableSubstitutor.substitute(info);
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }

    /**
     * Called when the panel becomes active.
     */
    public void panelActivate() {
        // Text handling
        loadInfo();
        parseText();

        // UI handling
        textArea.setText(info);
        textArea.setCaretPosition(0);
    }

    /**
     * Indicates wether the panel has been validated or not.
     *
     * @return Always true.
     */
    public boolean isValidated() {
        return true;
    }
}
