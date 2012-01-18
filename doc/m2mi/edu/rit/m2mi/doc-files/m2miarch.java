import jark.drawing.Arrowhead;
import jark.drawing.DecoratedDrawObject;
import jark.drawing.DrawObjectGroup;
import jark.drawing.Label;
import jark.drawing.OutlinedDrawObject;
import jark.drawing.Rectangle;
import jark.drawing.RoundPolyline;
import jark.drawing.RoundRectangle;
import jark.drawing.SolidArrowhead;
import jark.drawing.Strokes;
import jark.drawing.Units;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;

public class m2miarch
	extends DrawObjectGroup
	{
	private static final double in = Units.inch;
	private static final double OBJECT_W = in*3/4;
	private static final double OBJECT_H = in*3/4;
	private static final double MI_W = in*1/4;
	private static final double MI_H = in*1/4;
	private static final double GAP = in*3/8;
	private static final double ROUND = in*1/4;
	private static final double OFFSET = in*1/16;
	private static final double BORDER = in*1/4;

	private static final Font NORMAL_FONT =
		new Font ("sanserif", Font.PLAIN, 12);

	private static final BasicStroke NORMAL_STROKE = Strokes.solid (1);
	private static final BasicStroke CALL_STROKE = Strokes.solid (2);
	private static final BasicStroke NETWORK_STROKE = Strokes.solid (6);

	private static final Color OBJECT_COLOR = new Color (0.9f, 0.9f, 0.9f);
	private static final Color MI_COLOR = Color.black;
	private static final Color SP_COLOR = Color.red;
	private static final Color DPSH_COLOR = Color.blue;
	private static final Color DH_COLOR = Color.green;
	private static final Color PROCESS_COLOR = new Color (247, 255, 155);
	private static final Color BG_COLOR = new Color (215, 242, 255);

	public m2miarch()
		{
		super();

		Label lb;
		RoundPolyline pl;

		Arrowhead.setDefaultAutosize (false);
		Label.setDefaultFont (NORMAL_FONT);
		OutlinedDrawObject.setDefaultOutlineStroke (NORMAL_STROKE);

		Rectangle calling = new Rectangle (OBJECT_W, OBJECT_H);
		calling.setFillColor (OBJECT_COLOR);
		add (calling);
		lb = new Label ("Calling\nObject");
		lb.translate (calling.c());
		add (lb);
		Rectangle callingMI = new Rectangle (MI_W, MI_H);
		callingMI.setFillColor (MI_COLOR);
		callingMI.translate (callingMI.n(), calling.s(), 0, 2*GAP);
		add (callingMI);
		pl = new RoundPolyline (calling.s());
		pl.vertTo (callingMI.n());
		pl.setOutlineStroke (CALL_STROKE);
		addToBack (new DecoratedDrawObject (pl, null, new SolidArrowhead()));

		Rectangle called1 = new Rectangle (OBJECT_W, OBJECT_H);
		called1.setFillColor (OBJECT_COLOR);
		called1.translate (called1.w(), calling.e(), GAP, 0);
		add (called1);
		lb = new Label ("Called\nObject");
		lb.translate (called1.c());
		add (lb);
		Rectangle called1MI = new Rectangle (MI_W, MI_H);
		called1MI.setFillColor (MI_COLOR);
		called1MI.translate (called1MI.n(), called1.s(), 0, 2*GAP);
		add (called1MI);
		pl = new RoundPolyline (called1.s());
		pl.vertTo (called1MI.n());
		pl.setOutlineStroke (CALL_STROKE);
		addToBack (new DecoratedDrawObject (pl, new SolidArrowhead(), null));

		Rectangle called2 = new Rectangle (OBJECT_W, OBJECT_H);
		called2.setFillColor (OBJECT_COLOR);
		called2.translate (called2.w(), called1.e(), GAP, 0);
		add (called2);
		lb = new Label ("Called\nObject");
		lb.translate (called2.c());
		add (lb);
		Rectangle called2MI = new Rectangle (MI_W, MI_H);
		called2MI.setFillColor (MI_COLOR);
		called2MI.translate (called2MI.n(), called2.s(), 0, 2*GAP);
		add (called2MI);
		pl = new RoundPolyline (called2.s());
		pl.vertTo (called2MI.n());
		pl.setOutlineStroke (CALL_STROKE);
		addToBack (new DecoratedDrawObject (pl, new SolidArrowhead(), null));
		lb = new Label ("Proxies");
		lb.translate
			((called1MI.e().getX()+called2MI.w().getX())/2,
			 called1MI.e().getY());
		add (lb);

		pl = new RoundPolyline (callingMI.s());
		pl.vertBy (ROUND);
		pl.vertBy (ROUND);
		pl.setRoundCorner (pl.size()-1, true);
		pl.horiBy (ROUND);
		pl.horiTo (called1MI.s().getX()-ROUND);
		pl.horiBy (ROUND);
		pl.setRoundCorner (pl.size()-1, true);
		pl.vertBy (-ROUND);
		pl.vertTo (called1MI.s());
		pl.setOutlineColor (SP_COLOR);
		pl.setOutlineStroke (CALL_STROKE);
		addToBack (new DecoratedDrawObject (pl, null, new SolidArrowhead()));

		pl = new RoundPolyline (callingMI.s());
		pl.vertBy (ROUND);
		pl.vertBy (ROUND);
		pl.setRoundCorner (pl.size()-1, true);
		pl.horiBy (ROUND);
		pl.horiTo (called2MI.s().getX()-ROUND);
		pl.horiBy (ROUND);
		pl.setRoundCorner (pl.size()-1, true);
		pl.vertBy (-ROUND);
		pl.vertTo (called2MI.s());
		pl.setOutlineColor (SP_COLOR);
		pl.setOutlineStroke (CALL_STROKE);
		addToBack (new DecoratedDrawObject (pl, null, new SolidArrowhead()));

		pl = new RoundPolyline (callingMI.s());
		pl.vertBy (3*ROUND);
		pl.setOutlineColor (SP_COLOR);
		pl.setOutlineStroke (CALL_STROKE);
		addToBack (new DecoratedDrawObject (pl, null, new SolidArrowhead()));

		Rectangle m2mi1 = new Rectangle
			(called2.e().getX()-calling.w().getX(),
			 4*ROUND+MI_H);
		m2mi1.setFillColor (OBJECT_COLOR);
		m2mi1.translate
			(m2mi1.nw(), calling.sw().getX(), callingMI.nw().getY()-ROUND);
		addToBack (m2mi1);
		lb = new Label
			("M2MI\nLayer",
			 OFFSET, OFFSET, OFFSET, OFFSET,
			 Label.HJ_CENTER, Label.VJ_CENTER);
		lb.translate
			((calling.se().getX()+called1.sw().getX())/2,
			 m2mi1.c().getY());
		add (lb);

		Rectangle m2mp1 = new Rectangle
			(m2mi1.e().getX()-m2mi1.w().getX(),
			 2*ROUND+MI_H);
		m2mp1.setFillColor (OBJECT_COLOR);
		m2mp1.translate (m2mp1.n(), m2mi1.s());
		add (m2mp1);
		lb = new Label
			("M2MP\nLayer",
			 OFFSET, OFFSET, OFFSET, OFFSET,
			 Label.HJ_CENTER, Label.VJ_CENTER);
		lb.translate
			((called1.se().getX()+called2.sw().getX())/2,
			 m2mp1.c().getY());
		add (lb);

		pl = new RoundPolyline (pl.fi());
		pl.vertTo (m2mp1.s());
		pl.setOutlineColor (DPSH_COLOR);
		pl.setOutlineStroke (CALL_STROKE);
		add (new DecoratedDrawObject (pl, null, new SolidArrowhead()));

		Rectangle called3 = new Rectangle (OBJECT_W, OBJECT_H);
		called3.setFillColor (OBJECT_COLOR);
		called3.translate
			(called3.w(),
			 called2.e().getX()+2*ROUND+GAP,
			 called2.e().getY());
		add (called3);
		lb = new Label ("Called\nObject");
		lb.translate (called3.c());
		add (lb);
		Rectangle called3MI = new Rectangle (MI_W, MI_H);
		called3MI.setFillColor (MI_COLOR);
		called3MI.translate (called3MI.n(), called3.s(), 0, 2*GAP);
		add (called3MI);
		pl = new RoundPolyline (called3.s());
		pl.vertTo (called3MI.n());
		pl.setOutlineStroke (CALL_STROKE);
		addToBack (new DecoratedDrawObject (pl, new SolidArrowhead(), null));

		Rectangle called4 = new Rectangle (OBJECT_W, OBJECT_H);
		called4.setFillColor (OBJECT_COLOR);
		called4.translate (called4.w(), called3.e(), GAP, 0);
		add (called4);
		lb = new Label ("Called\nObject");
		lb.translate (called4.c());
		add (lb);
		Rectangle called4MI = new Rectangle (MI_W, MI_H);
		called4MI.setFillColor (MI_COLOR);
		called4MI.translate (called4MI.n(), called4.s(), 0, 2*GAP);
		add (called4MI);
		pl = new RoundPolyline (called4.s());
		pl.vertTo (called4MI.n());
		pl.setOutlineStroke (CALL_STROKE);
		addToBack (new DecoratedDrawObject (pl, new SolidArrowhead(), null));

		Rectangle called5 = new Rectangle (OBJECT_W, OBJECT_H);
		called5.setFillColor (OBJECT_COLOR);
		called5.translate (called5.w(), called4.e(), GAP, 0);
		add (called5);
		lb = new Label ("Called\nObject");
		lb.translate (called5.c());
		add (lb);
		Rectangle called5MI = new Rectangle (MI_W, MI_H);
		called5MI.setFillColor (MI_COLOR);
		called5MI.translate (called5MI.n(), called5.s(), 0, 2*GAP);
		add (called5MI);
		pl = new RoundPolyline (called5.s());
		pl.vertTo (called5MI.n());
		pl.setOutlineStroke (CALL_STROKE);
		addToBack (new DecoratedDrawObject (pl, new SolidArrowhead(), null));
		lb = new Label ("Proxies");
		lb.translate
			((called4MI.e().getX()+called5MI.w().getX())/2,
			 called4MI.e().getY());
		add (lb);

		pl = new RoundPolyline (called3MI.s());
		pl.vertBy (3*ROUND);
		pl.setOutlineColor (SP_COLOR);
		pl.setOutlineStroke (CALL_STROKE);
		addToBack (new DecoratedDrawObject (pl, new SolidArrowhead(), null));

		pl = new RoundPolyline (called4MI.s());
		pl.vertBy (ROUND);
		pl.vertBy (ROUND);
		pl.setRoundCorner (pl.size()-1, true);
		pl.horiBy (-ROUND);
		pl.horiTo (called3MI.s().getX()+ROUND);
		pl.horiBy (-ROUND);
		pl.setRoundCorner (pl.size()-1, true);
		pl.vertBy (ROUND);
		pl.setOutlineColor (SP_COLOR);
		pl.setOutlineStroke (CALL_STROKE);
		addToBack (new DecoratedDrawObject (pl, new SolidArrowhead(), null));

		pl = new RoundPolyline (called5MI.s());
		pl.vertBy (ROUND);
		pl.vertBy (ROUND);
		pl.setRoundCorner (pl.size()-1, true);
		pl.horiBy (-ROUND);
		pl.horiTo (called3MI.s().getX()+ROUND);
		pl.horiBy (-ROUND);
		pl.setRoundCorner (pl.size()-1, true);
		pl.vertBy (ROUND);
		pl.setOutlineColor (SP_COLOR);
		pl.setOutlineStroke (CALL_STROKE);
		addToBack (new DecoratedDrawObject (pl, new SolidArrowhead(), null));

		Rectangle m2mi2 = new Rectangle
			(called5.e().getX()-called3.w().getX(),
			 4*ROUND+MI_H);
		m2mi2.setFillColor (OBJECT_COLOR);
		m2mi2.translate
			(m2mi2.nw(), called3.sw().getX(), called3MI.nw().getY()-ROUND);
		addToBack (m2mi2);
		lb = new Label
			("M2MI\nLayer",
			 OFFSET, OFFSET, OFFSET, OFFSET,
			 Label.HJ_CENTER, Label.VJ_CENTER);
		lb.translate
			((called3.se().getX()+called4.sw().getX())/2,
			 m2mi2.c().getY());
		add (lb);

		Rectangle m2mp2 = new Rectangle
			(m2mi2.e().getX()-m2mi2.w().getX(),
			 2*ROUND+MI_H);
		m2mp2.setFillColor (OBJECT_COLOR);
		m2mp2.translate (m2mp2.n(), m2mi2.s());
		add (m2mp2);
		lb = new Label
			("M2MP\nLayer",
			 OFFSET, OFFSET, OFFSET, OFFSET,
			 Label.HJ_CENTER, Label.VJ_CENTER);
		lb.translate
			((called4.se().getX()+called5.sw().getX())/2,
			 m2mp2.c().getY());
		add (lb);

		Rectangle mf2 = new Rectangle (MI_W, MI_H);
		mf2.translate (mf2.w(), m2mp2.w(), ROUND, 0);
		mf2.setOutlineColor (DPSH_COLOR);
		mf2.setFillColor (DPSH_COLOR);
		add (mf2);
		lb = new Label
			("Message\nFilter",
			 OFFSET, OFFSET, OFFSET, OFFSET,
			 Label.HJ_LEFT, Label.VJ_CENTER);
		lb.translate (mf2.e());
		add (lb);
		pl = new RoundPolyline (mf2.n());
		pl.vertTo (m2mp2.n());
		pl.setOutlineColor (DPSH_COLOR);
		pl.setOutlineStroke (CALL_STROKE);
		add (new DecoratedDrawObject (pl, null, new SolidArrowhead()));

		Rectangle sm1 = new Rectangle
			(m2mp2.e().getX()-m2mp1.w().getX(),
			 2*ROUND);
		sm1.setFillColor (OBJECT_COLOR);
		sm1.translate (sm1.nw(), m2mp1.sw());
		add (sm1);
		lb = new Label
			("Shared Memory",
			 OFFSET, OFFSET, OFFSET, OFFSET,
			 Label.HJ_CENTER, Label.VJ_CENTER);
		lb.translate
			((called4.se().getX()+called5.sw().getX())/2,
			 sm1.c().getY());
		add (lb);

		pl = new RoundPolyline (callingMI.s().getX(), sm1.n().getY());
		pl.vertBy (ROUND);
		pl.setRoundCorner (pl.size()-1, true);
		pl.horiBy (ROUND);
		pl.horiTo (mf2.s().getX()-ROUND);
		pl.horiBy (ROUND);
		pl.setRoundCorner (pl.size()-1, true);
		pl.vertBy (-ROUND);
		pl.vertTo (mf2.s());
		pl.setOutlineColor (DPSH_COLOR);
		pl.setOutlineStroke (CALL_STROKE);
		add (new DecoratedDrawObject (pl, null, new SolidArrowhead()));

		pl = new RoundPolyline (callingMI.s().getX(), sm1.n().getY());
		pl.vertTo (sm1.s());
		pl.setOutlineColor (DPSH_COLOR);
		pl.setOutlineStroke (CALL_STROKE);
		add (new DecoratedDrawObject (pl, null, new SolidArrowhead()));

		Rectangle process1 = new Rectangle
			(m2mi1.e().getX()-m2mi1.w().getX()+2*ROUND,
			 sm1.s().getY()-calling.n().getY()+3*ROUND);
		process1.setFillColor (PROCESS_COLOR);
		process1.translate (process1.nw(), calling.nw(), -ROUND, -2*ROUND);
		addToBack (process1);
		lb = new Label
			("Process 1",
			 OFFSET, OFFSET, OFFSET, OFFSET,
			 Label.HJ_CENTER, Label.VJ_TOP);
		lb.translate (process1.n());
		add (lb);

		Rectangle process2 = new Rectangle
			(m2mi2.e().getX()-m2mi2.w().getX()+2*ROUND,
			 sm1.s().getY()-called3.n().getY()+3*ROUND);
		process2.setFillColor (PROCESS_COLOR);
		process2.translate (process2.nw(), called3.nw(), -ROUND, -2*ROUND);
		addToBack (process2);
		lb = new Label
			("Process 2",
			 OFFSET, OFFSET, OFFSET, OFFSET,
			 Label.HJ_CENTER, Label.VJ_TOP);
		lb.translate (process2.n());
		add (lb);

		Rectangle router1 = new Rectangle (2*OBJECT_W, OBJECT_H);
		router1.setFillColor (OBJECT_COLOR);
		router1.translate (router1.nw(), sm1.sw());
		add (router1);
		pl = new RoundPolyline (callingMI.s().getX(), router1.n().getY());
		pl.vertTo (router1.s());
		pl.setOutlineColor (DH_COLOR);
		pl.setOutlineStroke (CALL_STROKE);
		add (new DecoratedDrawObject (pl, null, new SolidArrowhead()));
		lb = new Label ("M2MP Daemon");
		lb.translate (router1.c());
		add (lb);

		Rectangle hostA = new Rectangle
			(process2.e().getX()-process1.w().getX()+2*ROUND,
			 router1.s().getY()-process1.n().getY()+3*ROUND);
		hostA.translate (hostA.nw(), process1.nw(), -ROUND, -2*ROUND);
		addToBack (hostA);
		lb = new Label
			("Device A",
			 OFFSET, OFFSET, OFFSET, OFFSET,
			 Label.HJ_CENTER, Label.VJ_TOP);
		lb.translate (hostA.n());
		add (lb);

		// Device B

		Rectangle called6 = new Rectangle (OBJECT_W, OBJECT_H);
		called6.setFillColor (OBJECT_COLOR);
		called6.translate
			(called6.w(),
			 called5.e().getX()+4*ROUND+GAP,
			 called5.e().getY());
		add (called6);
		lb = new Label ("Called\nObject");
		lb.translate (called6.c());
		add (lb);
		Rectangle called6MI = new Rectangle (MI_W, MI_H);
		called6MI.setFillColor (MI_COLOR);
		called6MI.translate (called6MI.n(), called6.s(), 0, 2*GAP);
		add (called6MI);
		pl = new RoundPolyline (called6.s());
		pl.vertTo (called6MI.n());
		pl.setOutlineStroke (CALL_STROKE);
		addToBack (new DecoratedDrawObject (pl, new SolidArrowhead(), null));

		Rectangle called7 = new Rectangle (OBJECT_W, OBJECT_H);
		called7.setFillColor (OBJECT_COLOR);
		called7.translate (called7.w(), called6.e(), GAP, 0);
		add (called7);
		lb = new Label ("Called\nObject");
		lb.translate (called7.c());
		add (lb);
		Rectangle called7MI = new Rectangle (MI_W, MI_H);
		called7MI.setFillColor (MI_COLOR);
		called7MI.translate (called7MI.n(), called7.s(), 0, 2*GAP);
		add (called7MI);
		pl = new RoundPolyline (called7.s());
		pl.vertTo (called7MI.n());
		pl.setOutlineStroke (CALL_STROKE);
		addToBack (new DecoratedDrawObject (pl, new SolidArrowhead(), null));

		Rectangle called8 = new Rectangle (OBJECT_W, OBJECT_H);
		called8.setFillColor (OBJECT_COLOR);
		called8.translate (called8.w(), called7.e(), GAP, 0);
		add (called8);
		lb = new Label ("Called\nObject");
		lb.translate (called8.c());
		add (lb);
		Rectangle called8MI = new Rectangle (MI_W, MI_H);
		called8MI.setFillColor (MI_COLOR);
		called8MI.translate (called8MI.n(), called8.s(), 0, 2*GAP);
		add (called8MI);
		pl = new RoundPolyline (called8.s());
		pl.vertTo (called8MI.n());
		pl.setOutlineStroke (CALL_STROKE);
		addToBack (new DecoratedDrawObject (pl, new SolidArrowhead(), null));
		lb = new Label ("Proxies");
		lb.translate
			((called7MI.e().getX()+called8MI.w().getX())/2,
			 called7MI.e().getY());
		add (lb);

		pl = new RoundPolyline (called6MI.s());
		pl.vertBy (3*ROUND);
		pl.setOutlineColor (SP_COLOR);
		pl.setOutlineStroke (CALL_STROKE);
		addToBack (new DecoratedDrawObject (pl, new SolidArrowhead(), null));

		pl = new RoundPolyline (called7MI.s());
		pl.vertBy (ROUND);
		pl.vertBy (ROUND);
		pl.setRoundCorner (pl.size()-1, true);
		pl.horiBy (-ROUND);
		pl.horiTo (called6MI.s().getX()+ROUND);
		pl.horiBy (-ROUND);
		pl.setRoundCorner (pl.size()-1, true);
		pl.vertBy (ROUND);
		pl.setOutlineColor (SP_COLOR);
		pl.setOutlineStroke (CALL_STROKE);
		addToBack (new DecoratedDrawObject (pl, new SolidArrowhead(), null));

		pl = new RoundPolyline (called8MI.s());
		pl.vertBy (ROUND);
		pl.vertBy (ROUND);
		pl.setRoundCorner (pl.size()-1, true);
		pl.horiBy (-ROUND);
		pl.horiTo (called6MI.s().getX()+ROUND);
		pl.horiBy (-ROUND);
		pl.setRoundCorner (pl.size()-1, true);
		pl.vertBy (ROUND);
		pl.setOutlineColor (SP_COLOR);
		pl.setOutlineStroke (CALL_STROKE);
		addToBack (new DecoratedDrawObject (pl, new SolidArrowhead(), null));

		Rectangle m2mi3 = new Rectangle
			(called8.e().getX()-called6.w().getX(),
			 4*ROUND+MI_H);
		m2mi3.setFillColor (OBJECT_COLOR);
		m2mi3.translate
			(m2mi3.nw(), called6.sw().getX(), called6MI.nw().getY()-ROUND);
		addToBack (m2mi3);
		lb = new Label
			("M2MI\nLayer",
			 OFFSET, OFFSET, OFFSET, OFFSET,
			 Label.HJ_CENTER, Label.VJ_CENTER);
		lb.translate
			((called6.se().getX()+called7.sw().getX())/2,
			 m2mi3.c().getY());
		add (lb);

		Rectangle m2mp3 = new Rectangle
			(m2mi3.e().getX()-m2mi3.w().getX(),
			 2*ROUND+MI_H);
		m2mp3.setFillColor (OBJECT_COLOR);
		m2mp3.translate (m2mp3.n(), m2mi3.s());
		add (m2mp3);
		lb = new Label
			("M2MP\nLayer",
			 OFFSET, OFFSET, OFFSET, OFFSET,
			 Label.HJ_CENTER, Label.VJ_CENTER);
		lb.translate
			((called7.se().getX()+called8.sw().getX())/2,
			 m2mp3.c().getY());
		add (lb);

		Rectangle mf3 = new Rectangle (MI_W, MI_H);
		mf3.translate (mf3.w(), m2mp3.w(), ROUND, 0);
		mf3.setOutlineColor (DPSH_COLOR);
		mf3.setFillColor (DPSH_COLOR);
		add (mf3);
		lb = new Label
			("Message\nFilter",
			 OFFSET, OFFSET, OFFSET, OFFSET,
			 Label.HJ_LEFT, Label.VJ_CENTER);
		lb.translate (mf3.e());
		add (lb);
		pl = new RoundPolyline (mf3.n());
		pl.vertTo (m2mp3.n());
		pl.setOutlineColor (DPSH_COLOR);
		pl.setOutlineStroke (CALL_STROKE);
		add (new DecoratedDrawObject (pl, null, new SolidArrowhead()));

		Rectangle sm2 = new Rectangle
			(m2mp3.e().getX()-m2mp3.w().getX(),
			 2*ROUND);
		sm2.setFillColor (OBJECT_COLOR);
		sm2.translate (sm2.nw(), m2mp3.sw());
		add (sm2);
		lb = new Label
			("Shared Memory",
			 OFFSET, OFFSET, OFFSET, OFFSET,
			 Label.HJ_CENTER, Label.VJ_CENTER);
		lb.translate
			((called7.se().getX()+called8.sw().getX())/2,
			 sm2.c().getY());
		add (lb);

		pl = new RoundPolyline (called6MI.s().getX(), sm2.s().getY());
		pl.vertTo (mf3.s());
		pl.setOutlineColor (DPSH_COLOR);
		pl.setOutlineStroke (CALL_STROKE);
		add (new DecoratedDrawObject (pl, null, new SolidArrowhead()));

		Rectangle process3 = new Rectangle
			(m2mi3.e().getX()-m2mi3.w().getX()+2*ROUND,
			 sm2.s().getY()-called6.n().getY()+3*ROUND);
		process3.setFillColor (PROCESS_COLOR);
		process3.translate (process3.nw(), called6.nw(), -ROUND, -2*ROUND);
		addToBack (process3);
		lb = new Label
			("Process 3",
			 OFFSET, OFFSET, OFFSET, OFFSET,
			 Label.HJ_CENTER, Label.VJ_TOP);
		lb.translate (process3.n());
		add (lb);

		Rectangle router2 = new Rectangle (2*OBJECT_W, OBJECT_H);
		router2.setFillColor (OBJECT_COLOR);
		router2.translate (router2.nw(), sm2.sw());
		add (router2);
		pl = new RoundPolyline (called6MI.s().getX(), router2.s().getY());
		pl.vertTo (router2.n());
		pl.setOutlineColor (DH_COLOR);
		pl.setOutlineStroke (CALL_STROKE);
		add (new DecoratedDrawObject (pl, null, new SolidArrowhead()));
		lb = new Label ("M2MP Daemon");
		lb.translate (router2.c());
		add (lb);

		Rectangle hostB = new Rectangle
			(process3.e().getX()-process3.w().getX()+2*ROUND,
			 router2.s().getY()-process3.n().getY()+3*ROUND);
		hostB.translate (hostB.nw(), process3.nw(), -ROUND, -2*ROUND);
		addToBack (hostB);
		lb = new Label
			("Device B",
			 OFFSET, OFFSET, OFFSET, OFFSET,
			 Label.HJ_CENTER, Label.VJ_TOP);
		lb.translate (hostB.n());
		add (lb);

		pl = new RoundPolyline (callingMI.s().getX(), router1.s().getY());
		pl.vertBy (2*ROUND);
		pl.vertBy (ROUND);
		pl.setRoundCorner (pl.size()-1, true);
		pl.horiBy (ROUND);
		pl.horiTo (called6MI.s().getX()-ROUND);
		pl.horiBy (ROUND);
		pl.setRoundCorner (pl.size()-1, true);
		pl.vertBy (-ROUND);
		pl.vertTo (router2.s());
		pl.setOutlineColor (DH_COLOR);
		pl.setOutlineStroke (CALL_STROKE);
		add (new DecoratedDrawObject (pl, null, new SolidArrowhead()));

		pl = new RoundPolyline (hostA.w().getX(), pl.bbs().getY());
		pl.horiTo (hostB.e());
		pl.setOutlineStroke (NETWORK_STROKE);
		addToBack (pl);
		lb = new Label
			("Broadcast Network",
			 OFFSET, OFFSET, OFFSET, OFFSET,
			 Label.HJ_CENTER, Label.VJ_TOP);
		lb.translate (pl.bbc());
		add (lb);

		Rectangle r = new Rectangle
			(getBoundingBox().getWidth() + 2*BORDER,
			 getBoundingBox().getHeight() + 2*BORDER);
		r.setOutlineStroke (null);
		r.setFillColor (BG_COLOR);
		r.translate (r.c(), bbc());
		addToBack (r);

		scale (0.75, 0.75);
		}

	}
