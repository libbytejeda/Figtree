package figtree.treeviewer.painters;

import figtree.treeviewer.AttributeColourController;
import figtree.treeviewer.ControllerOptionsPanel;
import figtree.treeviewer.TreeViewer;
import figtree.treeviewer.decorators.ColourDecorator;
import figtree.treeviewer.decorators.Decorator;
import figtree.ui.components.RealNumberField;
import jam.controlpalettes.AbstractController;
import jam.controlpalettes.ControllerListener;
import jam.panels.OptionsPanel;
import jebl.util.Attributable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.prefs.Preferences;

/**
 * @author Andrew Rambaut
 * @version $Id: ScaleBarPainterController.java 760 2007-08-21 00:05:45Z rambaut $
 */
public class LegendPainterController extends AbstractController {

    public static Preferences PREFS = Preferences.userNodeForPackage(TreeViewer.class);

    public static final String CONTROLLER_KEY = "legend";

    public static final String FONT_NAME_KEY = "fontName";
    public static final String FONT_SIZE_KEY = "fontSize";
    public static final String FONT_STYLE_KEY = "fontStyle";

    public static final String ATTRIBUTE_KEY = "attribute";

    // The defaults if there is nothing in the preferences
    public static String DEFAULT_FONT_NAME = "sansserif";
    public static int DEFAULT_FONT_SIZE = 10;
    public static int DEFAULT_FONT_STYLE = Font.PLAIN;

    public static String DEFAULT_ATTRIBUTE_KEY = "";

    public LegendPainterController(final LegendPainter legendPainter,
                                   final AttributeColourController colourController,
                                   final TreeViewer treeViewer) {
        final String defaultFontName = PREFS.get(CONTROLLER_KEY + "." + FONT_NAME_KEY, DEFAULT_FONT_NAME);
        final int defaultFontStyle = PREFS.getInt(CONTROLLER_KEY + "." + FONT_STYLE_KEY, DEFAULT_FONT_STYLE);
        final int defaultFontSize = PREFS.getInt(CONTROLLER_KEY + "." + FONT_SIZE_KEY, DEFAULT_FONT_SIZE);

        final String attribute = PREFS.get(CONTROLLER_KEY + "." + ATTRIBUTE_KEY, DEFAULT_ATTRIBUTE_KEY);

        legendPainter.setFont(new Font(defaultFontName, defaultFontStyle, defaultFontSize));
        legendPainter.setDisplayAttribute(attribute);

        optionsPanel = new ControllerOptionsPanel(2, 2);

        titleCheckBox = new JCheckBox(getTitle());
        titleCheckBox.setSelected(legendPainter.isVisible());

        attributeCombo = new JComboBox();
        attributeCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String attribute = (String) attributeCombo.getSelectedItem();
                legendPainter.setDisplayAttribute(attribute);
            }
        });
        new AttributeComboHelper(attributeCombo, treeViewer);

        colourController.setupControls(attributeCombo, null);
        colourController.addControllerListener(new ControllerListener() {
            @Override
            public void controlsChanged() {
                ColourDecorator colourDecorator = (ColourDecorator)colourController.getColourDecorator(attributeCombo, null);
                legendPainter.setColourDecorator(colourDecorator);
            }
        });

        attributeCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                legendPainter.setDisplayAttribute((String) attributeCombo.getSelectedItem());
            }
        });

        Font font = legendPainter.getFont();
        fontSizeSpinner = new JSpinner(new SpinnerNumberModel(font.getSize(), 0.01, 48, 1));

        fontSizeSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                final float size = ((Double) fontSizeSpinner.getValue()).floatValue();
                Font font = legendPainter.getFont().deriveFont(size);
                legendPainter.setFont(font);
            }
        });


        final JLabel label1 = optionsPanel.addComponentWithLabel("Attribute:", attributeCombo);
        final JLabel label2 = optionsPanel.addComponentWithLabel("Font Size:", fontSizeSpinner);

        addComponent(label1);
        addComponent(attributeCombo);
        addComponent(label2);
        addComponent(fontSizeSpinner);
        enableComponents(titleCheckBox.isSelected());

        titleCheckBox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                enableComponents(titleCheckBox.isSelected());
                legendPainter.setVisible(titleCheckBox.isSelected());
            }
        });

//        autoScaleCheck.addChangeListener(new ChangeListener() {
//            public void stateChanged(ChangeEvent changeEvent) {
//                if (autoScaleCheck.isSelected()) {
//                    scaleBarPainter.setAutomaticScale(true);
//                    double range = scaleBarPainter.getScaleRange();
//                    scaleRangeText.setValue(range);
//                    label1.setEnabled(false);
//                    scaleRangeText.setEnabled(false);
//                } else {
//                    label1.setEnabled(true);
//                    scaleRangeText.setEnabled(true);
//                    scaleBarPainter.setAutomaticScale(false);
//                }
//            }
//        });

    }

    public JComponent getTitleComponent() {
        return titleCheckBox;
    }

    public JPanel getPanel() {
        return optionsPanel;
    }

    public boolean isInitiallyVisible() {
        return false;
    }

    public void initialize() {
    }

    public void setSettings(Map<String,Object> settings) {
        titleCheckBox.setSelected((Boolean)settings.get(CONTROLLER_KEY + "." + IS_SHOWN));
        attributeCombo.setSelectedItem((String) settings.get(CONTROLLER_KEY + "." + ATTRIBUTE_KEY));
        fontSizeSpinner.setValue((Double)settings.get(CONTROLLER_KEY + "." + FONT_SIZE_KEY));
    }

    public void getSettings(Map<String, Object> settings) {
        settings.put(CONTROLLER_KEY + "." + IS_SHOWN, titleCheckBox.isSelected());
        settings.put(CONTROLLER_KEY + "." + ATTRIBUTE_KEY, attributeCombo.getSelectedItem());
        settings.put(CONTROLLER_KEY + "." + FONT_SIZE_KEY, fontSizeSpinner.getValue());
    }

    private final JCheckBox titleCheckBox;
    private final OptionsPanel optionsPanel;

    private final JComboBox attributeCombo;
    private final JSpinner fontSizeSpinner;

    public String getTitle() {
        return "Legend";
    }
}