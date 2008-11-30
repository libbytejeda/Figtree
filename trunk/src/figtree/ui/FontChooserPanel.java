package figtree.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;

import javax.swing.*;

/**
 * A panel for choosing a font from the available system fonts
 */
public class FontChooserPanel extends JPanel {

    /** The font sizes that can be selected. */
    public static final String[] SIZES = {"6", "8", "9", "10", "11", "12", "14", "16", "18",
                                          "20", "22", "24", "28", "36", "48", "72"};

    /** The list of fonts. */
    private JComboBox fontlist;

    /** The list of sizes. */
    private JComboBox sizelist;

    /** The checkbox that indicates whether the font is bold. */
    private JCheckBox bold;

    /** The checkbox that indicates whether or not the font is italic. */
    private JCheckBox italic;

    /**
     * Standard constructor - builds a FontChooserPanel initialised with the specified font.
     *
     * @param font  the initial font to display.
     */
    public FontChooserPanel(final Font font) {

        final GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final String[] fonts = g.getAvailableFontFamilyNames();

        setLayout(new BorderLayout());

        final JPanel leftPanel = new JPanel(new GridLayout(2, 1));
        this.fontlist = new JComboBox(fonts);
        this.sizelist = new JComboBox(SIZES);
        leftPanel.add(this.fontlist);
        final JPanel sizePanel = new JPanel(new BorderLayout(3, 0));
        final JLabel label = new JLabel("Size:");
        label.setHorizontalAlignment(JLabel.RIGHT);
        sizePanel.add(label, BorderLayout.CENTER);
        sizePanel.add(this.sizelist, BorderLayout.EAST);
        leftPanel.add(sizePanel);

        final JPanel rightPanel = new JPanel(new GridLayout(2, 1));
        this.bold = new JCheckBox("Bold");
        this.italic = new JCheckBox("Italic");
        rightPanel.add(this.bold);
        rightPanel.add(this.italic);

        add(leftPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        setSelectedFont(font);
    }

    /**
     * Returns a Font object representing the selection in the panel.
     *
     * @return the font.
     */
    public Font getSelectedFont() {
        return new Font(getSelectedName(), getSelectedStyle(), getSelectedSize());
    }

    /**
     * Returns the selected name.
     *
     * @return the name.
     */
    public String getSelectedName() {
        return (String) this.fontlist.getSelectedItem();
    }

    /**
     * Returns the selected style.
     *
     * @return the style.
     */
    public int getSelectedStyle() {
        if (this.bold.isSelected() && this.italic.isSelected()) {
            return Font.BOLD + Font.ITALIC;
        }
        if (this.bold.isSelected()) {
            return Font.BOLD;
        }
        if (this.italic.isSelected()) {
            return Font.ITALIC;
        }
        else {
            return Font.PLAIN;
        }
    }

    /**
     * Returns the selected size.
     *
     * @return the size.
     */
    public int getSelectedSize() {
        final String selected = (String) this.sizelist.getSelectedItem();
        if (selected != null) {
            return Integer.parseInt(selected);
        }
        else {
            return 10;
        }
    }

    /**
     * Initializes the contents of the dialog from the given font
     * object.
     *
     * @param font the font from which to read the properties.
     */
    public void setSelectedFont (final Font font) {
        if (font == null) {
            throw new NullPointerException();
        }
        this.bold.setSelected(font.isBold());
        this.italic.setSelected(font.isItalic());

        final String fontName = font.getName();
        ListModel model = this.fontlist.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            if (fontName.equals(model.getElementAt(i))) {
                this.fontlist.setSelectedIndex(i);
                break;
            }
        }

        final String fontSize = String.valueOf(font.getSize());
        model = this.sizelist.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            if (fontSize.equals(model.getElementAt(i))) {
                this.sizelist.setSelectedIndex(i);
                break;
            }
        }
    }
}