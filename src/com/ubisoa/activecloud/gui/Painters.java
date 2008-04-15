package com.ubisoa.activecloud.gui;

import java.awt.Color;
import java.awt.LinearGradientPaint;

import org.jdesktop.swingx.painter.CheckerboardPainter;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.GlossPainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.painter.PinstripePainter;

public class Painters {
	public static Painter matteGlossPinstripe(){
		MattePainter mp = new MattePainter(Colors.LightBlue.alpha(0.5f));
		GlossPainter gp = new GlossPainter(Colors.White.alpha(0.3f),
				GlossPainter.GlossPosition.TOP);
		PinstripePainter pp = new PinstripePainter(Colors.Gray.alpha(0.2f),45d);
		return (new CompoundPainter(mp,gp,pp));
	}
	
	public static Painter matteDark(){
		MattePainter mp = new MattePainter(Colors.Dark.alpha(0.9f));
		PinstripePainter pp = new PinstripePainter(Colors.Dark.alpha(0.4f),45d);
		return (new CompoundPainter(mp,pp));
	}
	
	public static Painter checkerboard(){
		return new CheckerboardPainter();
	}
}
