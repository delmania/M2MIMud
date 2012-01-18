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

public class m2mparch2
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

	public m2mparch2()
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
		lb = new Label ("UDPMulticast\nChannel");
		lb.translate (channel1.c());
		add (lb);

		Rectangle process1 = new Rectangle (W+2*BORDER, 2*H+4*BORDER);
		process1.setFillColor (PROCESS_COLOR);
		process1.translate (process1.s(), channel1.s(), 0, BORDER);
		//addToBack (process1);
		lb = new Label ("Application\nProcess");
		lb.translate (lb.bbn(), process1.n(), 0, OFFSET);
		add (lb);

		pl = new Polyline (channel1.s());
		pl.vertBy (3*BORDER);
		pl.setOutlineStroke (CHANNEL_STROKE);
		addToBack (pl);

		pl = new Polyline (process1.sw().getX(), pl.fi().getY());
		pl.horiTo (process1.se());
		pl.setOutlineStroke (NETWORK_STROKE);
		add (pl);
		lb = new Label ("External Network");
		lb.translate (lb.bbn(), pl.bbs(), 0, OFFSET);
		add (lb);

		addToBack (process1);

		Rectangle r = new Rectangle
			(getBoundingBox().getWidth() + 2*OFFSET,
			 getBoundingBox().getHeight() + 2*OFFSET);
		r.setOutlineStroke (null);
		r.translate (r.c(), bbc());
		addToBack (r);
		}

	}
