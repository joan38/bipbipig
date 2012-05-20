/*
 * Copyright (C) 2012 Joan Goyeau <joan.goyeau@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.umlv.ig.bipbip.client;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

/**
 *
 * @author Joan Goyeau <joan.goyeau@gmail.com>
 */
public class LayeredLayoutManager implements LayoutManager {

    @Override
    public void addLayoutComponent(String name, Component comp) {
        // TODO Auto-generated method stub
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        // TODO Auto-generated method stub
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        Component[] components = parent.getComponents();
        if (components.length == 0) {
            return new Dimension(0, 0);
        }
        Dimension d = components[0].getPreferredSize();
        for (int i = 1; i < components.length; i++) {
            Dimension tmp = components[i].getPreferredSize();
            if (d.width < tmp.width) {
                d.width = tmp.width;
            }
            if (d.height < tmp.height) {
                d.height = tmp.height;
            }
        }
        return d;
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        Component[] components = parent.getComponents();
        if (components.length == 0) {
            return new Dimension(0, 0);
        }
        Dimension d = components[0].getMinimumSize();
        for (int i = 1; i < components.length; i++) {
            Dimension tmp = components[i].getMinimumSize();
            if (d.width < tmp.width) {
                d.width = tmp.width;
            }
            if (d.height < tmp.height) {
                d.height = tmp.height;
            }
        }
        return d;
    }

    @Override
    public void layoutContainer(Container parent) {
        Dimension d = parent.getSize();
        for (Component c : parent.getComponents()) {
            c.setSize(d);
        }
    }
}
