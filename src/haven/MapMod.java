/*
 *  This file is part of the Haven & Hearth game client.
 *  Copyright (C) 2009 Fredrik Tolf <fredrik@dolda2000.com>, and
 *                     Björn Johannessen <johannessen.bjorn@gmail.com>
 *
 *  Redistribution and/or modification of this file is subject to the
 *  terms of the GNU Lesser General Public License, version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Other parts of this source tree adhere to other copying
 *  rights. Please see the file `COPYING' in the root directory of the
 *  source tree for details.
 *
 *  A copy the GNU Lesser General Public License is distributed along
 *  with the source tree of which this file is a part in the file
 *  `doc/LPGL-3'. If it is missing for any reason, please see the Free
 *  Software Foundation's website at <http://www.fsf.org/>, or write
 *  to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA 02111-1307 USA
 */

package haven;

public class MapMod extends Window implements MapView.Grabber {
    MCache.Overlay ol;
    MCache map;
    boolean walkmod;
    CheckBox cbox;
    Button btn;
    Label text;
    Coord sc, c1, c2;
    TextEntry tilenum;
    public final static String fmt = "Selected: %d" + (char)(0xD7) + "%d";
    
    static {
        Widget.addtype("mapmod", new WidgetFactory() {
            public Widget create(Coord c, Widget parent, Object[] args) {
                return(new MapMod(c, parent));
            }
        });
    }

    public MapMod(Coord c, Widget parent) {
        super(c, new Coord(200, 100), parent, "Kartlasskostning");
        map = ui.sess.glob.map;
        walkmod = true;
        ui.mainview.enol(17);
        ui.mainview.grab(this);
        cbox = new CheckBox(Coord.z, this, "Walk drawing");
        btn = new Button(asz.add(-50, -30), 40, this, "Change");
        text = new Label(Coord.z, this, String.format(fmt, 0, 0));
        tilenum = new TextEntry(new Coord(0, 40), new Coord(50, 17), this, "0");
        tilenum.canactivate = true;
    }

    public void destroy() {
        ui.mainview.disol(17);
        if(walkmod)
            ui.mainview.release(this);
        if(ol != null)
            ol.destroy();
        super.destroy();
    }

	
    public void mmousedown(Coord mc, int button) {
        Coord tc = mc.div(MCache.tilesz);
        if(ol != null)
            ol.destroy();
        ol = map.new Overlay(tc, tc, 1 << 17);
        sc = tc;
        dm = true;
        ui.grabmouse(ui.mainview);
    }
	
    public void mmouseup(Coord mc, int button) {
        dm = false;
        ui.grabmouse(null);
    }
	
    public void mmousemove(Coord mc) {
        if(!dm)
            return;
        Coord tc = mc.div(MCache.tilesz);
        Coord c1 = new Coord(0, 0), c2 = new Coord(0, 0);
        if(tc.x < sc.x) {
            c1.x = tc.x;
            c2.x = sc.x;
        } else {
            c1.x = sc.x;
            c2.x = tc.x;			
        }
        if(tc.y < sc.y) {
            c1.y = tc.y;
            c2.y = sc.y;
        } else {
            c1.y = sc.y;
            c2.y = tc.y;			
        }
        ol.update(c1, c2);
        this.c1 = c1;
        this.c2 = c2;
        text.settext(String.format(fmt, c2.x - c1.x + 1, c2.y - c1.y + 1));
    }

    public void wdgmsg(Widget sender, String msg, Object... args) {
        if(sender == btn) {
            if((c1 != null) && (c2 != null))
                wdgmsg("mod", c1, c2);
            return;
        }
        if(sender == cbox) {
            walkmod = (Boolean)args[0];
            if(!walkmod) {
                ui.mainview.grab(this);
            } else {
                if(ol != null)
                    ol.destroy();
                ol = null;
                ui.mainview.release(this);
            }
            wdgmsg("wm", walkmod?1:0);
            return;
        }
        if(sender == tilenum) {
            wdgmsg("tnum", Integer.parseInt(tilenum.text));
            return;
        }
        super.wdgmsg(sender, msg, args);
    }
}
