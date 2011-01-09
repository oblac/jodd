// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.swing.spy;

import javax.swing.JPanel;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import java.awt.AWTEvent;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;

/**
 * Glass pane for some spy effects.
 */
public class SwingSpyGlassPane extends JPanel implements AWTEventListener {

    private final RootPaneContainer rootPaneContainer;
    private final Color fontColor;
    private final Color highlightColor;
    private final AlphaComposite alpha;
    private final Font font;
    private Point point;
    private Rectangle highcmp;

    public SwingSpyGlassPane(RootPaneContainer rootPaneContainer) {
        super(null);
	    setName("SwingSpyGlass");
        this.rootPaneContainer = rootPaneContainer;
        this.fontColor = Color.RED.darker();
        this.highlightColor = new Color(0xFFDDDD);
        this.font = new Font("sansserif", Font.BOLD, 14);
        this.alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setComposite(alpha);
        if (highcmp != null) {
            g2.setColor(highlightColor);
            g2.fillRect((int)highcmp.getMinX(), (int)highcmp.getMinY(), (int) highcmp.getWidth(), (int) highcmp.getHeight());
        }
        if (point != null) {
            g2.setColor(fontColor);
            g2.setFont(font);
            g2.drawString("Spy", point.x + 15, point.y + 20);
        }
        g2.dispose();
    }

    public void eventDispatched(AWTEvent event) {
        if (event instanceof MouseEvent) {
            MouseEvent me = (MouseEvent) event;
            Component mecmp = me.getComponent();

            if (SwingUtilities.isDescendingFrom(mecmp, (Component) rootPaneContainer) == false) {
                return;
            }
            if ((me.getID() == MouseEvent.MOUSE_EXITED) && (mecmp == rootPaneContainer)) {
                highcmp = null;
                point = null;
            } else {
                MouseEvent converted = SwingUtilities.convertMouseEvent(mecmp, me, this);
                point = converted.getPoint();
                Component parent = mecmp;
                Rectangle rect = new Rectangle();
                rect.width = mecmp.getWidth();
                rect.height = mecmp.getHeight();
                Rectangle parentBounds = new Rectangle();
                while ((parent != null) && (parent != this.getRootPane()) && (parent != rootPaneContainer)) {
                    parent.getBounds(parentBounds);
                    rect.x += parentBounds.x;
                    rect.y += parentBounds.y;
                    parent = parent.getParent();
                }
                highcmp = rect;
            }
            repaint();
        }
    }

    /**
     * If someone adds a mouseListener to the GlassPane or set a new cursor
     * we expect that he knows what he is doing and return the super.contains(x, y) otherwise
     * we return false to respect the cursors for the underneath components.
     */
    @Override
    public boolean contains(int x, int y) {
        if (getMouseListeners().length == 0 && getMouseMotionListeners().length == 0
                && getMouseWheelListeners().length == 0
                && getCursor() == Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)) {
            return false;
        }
        return super.contains(x, y);
    }
}
