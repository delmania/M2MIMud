import jark.drawing.DrawObjectGroup;
import jark.drawing.Label;
import jark.drawing.OutlinedDrawObject;
import jark.drawing.Polyline;
import jark.drawing.Rectangle;
import jark.drawing.Strokes;
import jark.drawing.Units;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;

public class m2mparch1
	extends DrawObjectGroup
	{
	private static final double in = Units.inch;
	private static final double W = in*5/4;
	private static final double H = in*3/4;
	private static final double BORDER = in*1/4;
	private static final double HGAP = in*1;
	private static final double VGAP = in*1;
	private static final double OFFSET = in*1/16;

	private static final Font NORMAL_FONT =
		new Font ("sanserif", Font.PLAIN, 12);

	private static final BasicStroke NORMAL_STROKE = Strokes.solid (1);
	private static final BasicStroke CHANNEL_STROKE = Strokes.solid (2);
	private static final BasicStroke NETWORK_STROKE = Strokes.solid (6);

	private static final Color PROCESS_COLOR = new Color (0.9f, 0.9f, 0.9f);
	private static final Color M2MP_COLOR = new Color (255, 204, 204);
	private static final Color CHANNEL_COLOR = new Color (204, 255, 204);

	public m2mparch1()
		{
		super();

		Label lb;
		Polyline pl;

		Label.setDefaultFont (NORMAL_FONT);
		OutlinedDrawObject.setDefaultOutlineStroke (NORMAL_STROKE);

		Rectangle m2mp1 = new Rectangle (W, H);
		m2mp1.setFillColor (M2MP_COLOR);
		add (m2mp1);
		lb = new Label ("M2MP Layer");
		lb.translate (m2mp1.c());
		add (lb);

		Rectangle channel1 = new Rectangle (W, H);
		channel1.setFillColor (CHANNEL_COLOR);
		channel1.translate (channel1.n(), m2mp1.s());
		add (channel1);
		lb = new Label ("Daemon\nChannel");
		lb.translate (channel1.c());
		add (lb);

		Rectangle process1 = new Rectangle (W+2*BORDER, 2*H+4*BORDER);
		process1.setFillColor (PROCESS_COLOR);
		process1.translate (process1.s(), channel1.s(), 0, BORDER);
		//addToBack (process1);
		lb = new Label ("Application\nProcess");
		lb.translate (lb.bbn(), process1.n(), 0, OFFSET);
		add (lb);

		Rectangle m2mp2 = new Rectangle (W, H);
		m2mp2.setFillColor (M2MP_COLOR);
		m2mp2.translate (m2mp2.w(), m2mp1.e(), HGAP+2*BORDER, 0);
		add (m2mp2);
		lb = new Label ("M2MP Layer");
		lb.translate (m2mp2.c());
		add (lb);

		Rectangle channel2 = new Rectangle (W, H);
		channel2.setFillColor (CHANNEL_COLOR);
		channel2.translate (channel2.n(), m2mp2.s());
		add (channel2);
		lb = new Label ("Daemon\nChannel");
		lb.translate (channel2.c());
		add (lb);

		Rectangle process2 = new Rectangle (W+2*BORDER, 2*H+4*BORDER);
		process2.setFillColor (PROCESS_COLOR);
		process2.translate (process2.s(), channel2.s(), 0, BORDER);
		//addToBack (process2);
		lb = new Label ("Application\nProcess");
		lb.translate (lb.bbn(), process2.n(), 0, OFFSET);
		add (lb);

		Rectangle daemon = new Rectangle (W+2*BORDER, 2*H+4*BORDER);
		daemon.setFillColor (PROCESS_COLOR);
		daemon.translate
			(daemon.n(), 
			 (process1.s().getX()+process2.s().getX())/2,
			 process1.s().getY()+VGAP);

		Rectangle channel3 = new Rectangle (W, H);
		channel3.setFillColor (CHANNEL_COLOR);
		channel3.translate (channel3.s(), daemon.s(), 0, -BORDER);
		add (channel3);
		lb = new Label ("UDPMulticast\nChannel");
		lb.translate (channel3.c());
		add (lb);

		pl = new Polyline (channel3.s());
		pl.vertBy (3*BORDER);
		pl.setOutlineStroke (CHANNEL_STROKE);
		addToBack (pl);

		pl = new Polyline (process1.sw().getX(), pl.fi().getY());
		pl.horiTo (process2.se());
		pl.setOutlineStroke (NETWORK_STROKE);
		add (pl);
		lb = new Label ("External Network");
		lb.translate (lb.bbn(), pl.bbs(), 0, OFFSET);
		add (lb);

		addToBack (daemon);
		lb = new Label ("M2MP Daemon\nProcess");
		lb.translate (lb.bbn(), daemon.n(), 0, OFFSET);
		add (lb);

		pl = new Polyline (channel1.s());
		pl.lineTo (daemon.nw().getX()+BORDER, daemon.nw().getY());
		pl.setOutlineStroke (CHANNEL_STROKE);
		addToBack (pl);

		pl = new Polyline (channel2.s());
		pl.lineTo (daemon.ne().getX()-BORDER, daemon.ne().getY());
		pl.setOutlineStroke (CHANNEL_STROKE);
		addToBack (pl);

		addToBack (process1);
		addToBack (process2);

		Rectangle r = new Rectangle
			(getBoundingBox().getWidth() + 2*OFFSET,
			 getBoundingBox().getHeight() + 2*OFFSET);
		r.setOutlineStroke (null);
		r.translate (r.c(), bbc());
		addToBack (r);
		}

	}
