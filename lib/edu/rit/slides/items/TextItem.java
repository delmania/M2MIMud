//******************************************************************************
//
// File:    TextItem.java
// Package: edu.rit.slides.items
// Unit:    Class edu.rit.slides.items.TextItem
//
// This Java source file is copyright (C) 2001-2004 by Alan Kaminsky. All rights
// reserved. For further information, contact the author, Alan Kaminsky, at
// ark@cs.rit.edu.
//
// This Java source file is part of the M2MI Library ("The Library"). The
// Library is free software; you can redistribute it and/or modify it under the
// terms of the GNU General Public License as published by the Free Software
// Foundation; either version 2 of the License, or (at your option) any later
// version.
//
// The Library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
// FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
// details.
//
// A copy of the GNU General Public License is provided in the file gpl.txt. You
// may also obtain a copy of the GNU General Public License on the World Wide
// Web at http://www.gnu.org/licenses/gpl.html or by writing to the Free
// Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
// USA.
//
//******************************************************************************

package edu.rit.slides.items;

import java.awt.Font;
import java.awt.Graphics2D;

import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;

import java.awt.geom.Rectangle2D;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Class TextItem provides a slide item consisting of a single line of text.
 * The text is all one font and color.
 * <P>
 * The line of text occupies a certain rectangular box. The text box includes
 * the text's ascenders and descenders, but not the leading. The text box may be
 * placed on the slide at one of nine positions relative to a specified "anchor
 * point." The text position is specified by one of the following constants:
 * <UL>
 * <LI>
 * <TT>TextItem.ABOVE_LEFT</TT> -- The text box is above and to the left of the
 * anchor point; equivalently, the text box's lower right corner is located at
 * the anchor point.
 * <BR>&nbsp;
 * <LI>
 * <TT>TextItem.ABOVE</TT> -- The text box is above the anchor point;
 * equivalently, the text box's lower center point is located at the anchor
 * point.
 * <BR>&nbsp;
 * <LI>
 * <TT>TextItem.ABOVE_RIGHT</TT> -- The text box is above and to the right of
 * the anchor point; equivalently, the text box's lower left corner is located
 * at the anchor point.
 * <BR>&nbsp;
 * <LI>
 * <TT>TextItem.LEFT</TT> -- The text box is to the left of the anchor point;
 * equivalently, the text box's right center point is located at the anchor
 * point.
 * <BR>&nbsp;
 * <LI>
 * <TT>TextItem.CENTER</TT> -- The text box is centered on the anchor point;
 * equivalently, the text box's center point is located at the anchor point.
 * <BR>&nbsp;
 * <LI>
 * <TT>TextItem.RIGHT</TT> -- The text box is to the right of the anchor point;
 * equivalently, the text box's left center point is located at the anchor
 * point.
 * <BR>&nbsp;
 * <TT>TextItem.BELOW_LEFT</TT> -- The text box is below and to the left of the
 * anchor point; equivalently, the text box's upper right corner is located at
 * the anchor point.
 * <BR>&nbsp;
 * <LI>
 * <TT>TextItem.BELOW</TT> -- The text box is below the anchor point;
 * equivalently, the text box's upper center point is located at the anchor
 * point.
 * <BR>&nbsp;
 * <LI>
 * <TT>TextItem.BELOW_RIGHT</TT> -- The text box is below and to the right of
 * the anchor point; equivalently, the text box's upper left corner is located
 * at the anchor point.
 * </UL>
 * <P>
 * The text may optionally have a {@link Bullet </CODE>Bullet<CODE>} attached.
 * The bullet is always located to the left of the text. The offset from the
 * left side of the bullet to the left side of the text is specified when the
 * bullet object is constructed.
 * <P>
 * Class TextItem keeps track of the "last text item." The static
 * <TT>TextItem.last()</TT> method returns a reference to the last created text
 * item.
 * <P>
 * When a TextItem is created, the "last point" (returned by the
 * <TT>Point.last()</TT> method) is set to the text item's anchor point, offset
 * in the positive Y direction by a distance equal to the "newline factor" times
 * the font size. This is typically the point at which to place the next text
 * item.
 * <P>
 * Static <TT>setDefaultXXX()</TT> methods are provided to set the default
 * attribute values (position, font, fill, bullet, and newline factor). If an
 * attribute value is not specified when constructing a text item, the current
 * default attribute value is used.
 *
 * @author  Alan Kaminsky
 * @version 22-Nov-2004
 */
public class TextItem
	extends SlideItem
	implements Externalizable
	{

// Exported constants.

	/**
	 * The text box is positioned above and to the left of the anchor point.
	 */
	public static final int ABOVE_LEFT = 0;

	/**
	 * The text box is positioned above the anchor point.
	 */
	public static final int ABOVE = 1;

	/**
	 * The text box is positioned above and to the right of the anchor point.
	 */
	public static final int ABOVE_RIGHT = 2;

	/**
	 * The text box is positioned to the left of the anchor point.
	 */
	public static final int LEFT = 3;

	/**
	 * The text box is centered on the anchor point.
	 */
	public static final int CENTER = 4;

	/**
	 * The text box is positioned to the right of the anchor point.
	 */
	public static final int RIGHT = 5;

	/**
	 * The text box is positioned below and to the left of the anchor point.
	 */
	public static final int BELOW_LEFT = 6;

	/**
	 * The text box is positioned below the anchor point.
	 */
	public static final int BELOW = 7;

	/**
	 * The text box is positioned below and to the right of the anchor point.
	 */
	public static final int BELOW_RIGHT = 8;

	/**
	 * The normal text font (sanserif, plain, 24 point).
	 */
	public static final Font NORMAL_FONT =
		new Font ("sanserif", Font.PLAIN, 24);

	/**
	 * The normal fill (black).
	 */
	public static final Fill NORMAL_FILL = ColorFill.BLACK;

	/**
	 * The normal newline factor (1.25).
	 */
	public static final double NORMAL_NEWLINE = 1.25;

// Hidden data members.

	private static final long serialVersionUID = -1403602143925938763L;

	// Attributes of this text item.
	String myText;
	Point myAnchor;
	int myPosition;
	Font myFont;
	Fill myFill;
	Bullet myBullet;
	double myNewline;

	// Default attribute values.
	static int theDefaultPosition = BELOW_RIGHT;
	static Font theDefaultFont = NORMAL_FONT;
	static Fill theDefaultFill = NORMAL_FILL;
	static Bullet theDefaultBullet = null;
	static double theDefaultNewline = NORMAL_NEWLINE;

	// The last text item.
	static TextItem theLastTextItem;

// Exported constructors.

	/**
	 * Construct a new, empty text item.
	 */
	public TextItem()
		{
		}

	/**
	 * Construct a new text item with the given text. The anchor point is
	 * <TT>Point.last()</TT>. The default text position is used. The default
	 * font is used. The default fill paint is used. The text has no bullet. The
	 * default newline factor is used.
	 *
	 * @param  theText    Text.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null.
	 */
	public TextItem
		(String theText)
		{
		this
			(theText,
			 Point.last(),
			 theDefaultPosition,
			 theDefaultFont,
			 theDefaultFill,
			 theDefaultBullet,
			 theDefaultNewline);
		}

	/**
	 * Construct a new text item with the given text. The anchor point is
	 * <TT>Point.last()</TT>. The default text position is used. The default
	 * font is used. The default fill paint is used. The text has no bullet. The
	 * given newline factor is used.
	 *
	 * @param  theText     Text.
	 * @param  theNewline  Newline factor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null.
	 */
	public TextItem
		(String theText,
		 double theNewline)
		{
		this
			(theText,
			 Point.last(),
			 theDefaultPosition,
			 theDefaultFont,
			 theDefaultFill,
			 theDefaultBullet,
			 theNewline);
		}

	/**
	 * Construct a new text item with the given text. The anchor point is
	 * <TT>Point.last()</TT>. The default text position is used. The default
	 * font is used. The default fill paint is used. The given bullet is used.
	 * The default newline factor is used.
	 *
	 * @param  theText    Text.
	 * @param  theBullet  Bullet. If null, no bullet is drawn.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null.
	 */
	public TextItem
		(String theText,
		 Bullet theBullet)
		{
		this
			(theText,
			 Point.last(),
			 theDefaultPosition,
			 theDefaultFont,
			 theDefaultFill,
			 theBullet,
			 theDefaultNewline);
		}

	/**
	 * Construct a new text item with the given text. The anchor point is
	 * <TT>Point.last()</TT>. The default text position is used. The default
	 * font is used. The default fill paint is used. The given bullet is used.
	 * The given newline factor is used.
	 *
	 * @param  theText     Text.
	 * @param  theBullet   Bullet. If null, no bullet is drawn.
	 * @param  theNewline  Newline factor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null.
	 */
	public TextItem
		(String theText,
		 Bullet theBullet,
		 double theNewline)
		{
		this
			(theText,
			 Point.last(),
			 theDefaultPosition,
			 theDefaultFont,
			 theDefaultFill,
			 theBullet,
			 theNewline);
		}

	/**
	 * Construct a new text item with the given text. The anchor point is
	 * <TT>Point.last()</TT>. The default text position is used. The default
	 * font is used. The given fill paint is used. The text has no bullet. The
	 * default newline factor is used.
	 *
	 * @param  theText    Text.
	 * @param  theFill    Fill paint.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theFill</TT> is null.
	 */
	public TextItem
		(String theText,
		 Fill theFill)
		{
		this
			(theText,
			 Point.last(),
			 theDefaultPosition,
			 theDefaultFont,
			 theFill,
			 theDefaultBullet,
			 theDefaultNewline);
		}

	/**
	 * Construct a new text item with the given text. The anchor point is
	 * <TT>Point.last()</TT>. The default text position is used. The default
	 * font is used. The given fill paint is used. The text has no bullet. The
	 * given newline factor is used.
	 *
	 * @param  theText     Text.
	 * @param  theFill     Fill paint.
	 * @param  theNewline  Newline factor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theFill</TT> is null.
	 */
	public TextItem
		(String theText,
		 Fill theFill,
		 double theNewline)
		{
		this
			(theText,
			 Point.last(),
			 theDefaultPosition,
			 theDefaultFont,
			 theFill,
			 theDefaultBullet,
			 theNewline);
		}

	/**
	 * Construct a new text item with the given text. The anchor point is
	 * <TT>Point.last()</TT>. The default text position is used. The default
	 * font is used. The given fill paint is used. The given bullet is used. The
	 * default newline factor is used.
	 *
	 * @param  theText    Text.
	 * @param  theFill    Fill paint.
	 * @param  theBullet  Bullet. If null, no bullet is drawn.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theFill</TT> is null.
	 */
	public TextItem
		(String theText,
		 Fill theFill,
		 Bullet theBullet)
		{
		this
			(theText,
			 Point.last(),
			 theDefaultPosition,
			 theDefaultFont,
			 theFill,
			 theBullet,
			 theDefaultNewline);
		}

	/**
	 * Construct a new text item with the given text. The anchor point is
	 * <TT>Point.last()</TT>. The default text position is used. The default
	 * font is used. The given fill paint is used. The given bullet is used. The
	 * given newline factor is used.
	 *
	 * @param  theText     Text.
	 * @param  theFill     Fill paint.
	 * @param  theBullet   Bullet. If null, no bullet is drawn.
	 * @param  theNewline  Newline factor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theFill</TT> is null.
	 */
	public TextItem
		(String theText,
		 Fill theFill,
		 Bullet theBullet,
		 double theNewline)
		{
		this
			(theText,
			 Point.last(),
			 theDefaultPosition,
			 theDefaultFont,
			 theFill,
			 theBullet,
			 theNewline);
		}

	/**
	 * Construct a new text item with the given text. The anchor point is
	 * <TT>Point.last()</TT>. The default text position is used. The given font
	 * is used. The default fill paint is used. The text has no bullet. The
	 * default newline factor is used.
	 *
	 * @param  theText    Text.
	 * @param  theFont    Font.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theFont</TT> is null.
	 */
	public TextItem
		(String theText,
		 Font theFont)
		{
		this
			(theText,
			 Point.last(),
			 theDefaultPosition,
			 theFont,
			 theDefaultFill,
			 theDefaultBullet,
			 theDefaultNewline);
		}

	/**
	 * Construct a new text item with the given text. The anchor point is
	 * <TT>Point.last()</TT>. The default text position is used. The given font
	 * is used. The default fill paint is used. The text has no bullet. The
	 * given newline factor is used.
	 *
	 * @param  theText     Text.
	 * @param  theFont     Font.
	 * @param  theNewline  Newline factor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theFont</TT> is null.
	 */
	public TextItem
		(String theText,
		 Font theFont,
		 double theNewline)
		{
		this
			(theText,
			 Point.last(),
			 theDefaultPosition,
			 theFont,
			 theDefaultFill,
			 theDefaultBullet,
			 theNewline);
		}

	/**
	 * Construct a new text item with the given text. The anchor point is
	 * <TT>Point.last()</TT>. The default text position is used. The given font
	 * is used. The default fill paint is used. The given bullet is used. The
	 * default newline factor is used.
	 *
	 * @param  theText    Text.
	 * @param  theFont    Font.
	 * @param  theBullet  Bullet. If null, no bullet is drawn.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theFont</TT> is null.
	 */
	public TextItem
		(String theText,
		 Font theFont,
		 Bullet theBullet)
		{
		this
			(theText,
			 Point.last(),
			 theDefaultPosition,
			 theFont,
			 theDefaultFill,
			 theBullet,
			 theDefaultNewline);
		}

	/**
	 * Construct a new text item with the given text. The anchor point is
	 * <TT>Point.last()</TT>. The default text position is used. The given font
	 * is used. The default fill paint is used. The given bullet is used. The
	 * given newline factor is used.
	 *
	 * @param  theText     Text.
	 * @param  theFont     Font.
	 * @param  theBullet   Bullet. If null, no bullet is drawn.
	 * @param  theNewline  Newline factor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theFont</TT> is null.
	 */
	public TextItem
		(String theText,
		 Font theFont,
		 Bullet theBullet,
		 double theNewline)
		{
		this
			(theText,
			 Point.last(),
			 theDefaultPosition,
			 theFont,
			 theDefaultFill,
			 theBullet,
			 theNewline);
		}

	/**
	 * Construct a new text item with the given text. The anchor point is
	 * <TT>Point.last()</TT>. The default text position is used. The given font
	 * is used. The given fill paint is used. The text has no bullet. The
	 * default newline factor is used.
	 *
	 * @param  theText    Text.
	 * @param  theFont    Font.
	 * @param  theFill    Fill paint.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theFont</TT> is null or <TT>theFill</TT> is null.
	 */
	public TextItem
		(String theText,
		 Font theFont,
		 Fill theFill)
		{
		this
			(theText,
			 Point.last(),
			 theDefaultPosition,
			 theFont,
			 theFill,
			 theDefaultBullet,
			 theDefaultNewline);
		}

	/**
	 * Construct a new text item with the given text. The anchor point is
	 * <TT>Point.last()</TT>. The default text position is used. The given font
	 * is used. The given fill paint is used. The text has no bullet. The given
	 * newline factor is used.
	 *
	 * @param  theText     Text.
	 * @param  theFont     Font.
	 * @param  theFill     Fill paint.
	 * @param  theNewline  Newline factor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theFont</TT> is null or <TT>theFill</TT> is null.
	 */
	public TextItem
		(String theText,
		 Font theFont,
		 Fill theFill,
		 double theNewline)
		{
		this
			(theText,
			 Point.last(),
			 theDefaultPosition,
			 theFont,
			 theFill,
			 theDefaultBullet,
			 theNewline);
		}

	/**
	 * Construct a new text item with the given text. The anchor point is
	 * <TT>Point.last()</TT>. The default text position is used. The given font
	 * is used. The given fill paint is used. The given bullet is used. The
	 * default newline factor is used.
	 *
	 * @param  theText    Text.
	 * @param  theFont    Font.
	 * @param  theFill    Fill paint.
	 * @param  theBullet  Bullet. If null, no bullet is drawn.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theFont</TT> is null or <TT>theFill</TT> is null.
	 */
	public TextItem
		(String theText,
		 Font theFont,
		 Fill theFill,
		 Bullet theBullet)
		{
		this
			(theText,
			 Point.last(),
			 theDefaultPosition,
			 theFont,
			 theFill,
			 theBullet,
			 theDefaultNewline);
		}

	/**
	 * Construct a new text item with the given text. The anchor point is
	 * <TT>Point.last()</TT>. The default text position is used. The given font
	 * is used. The given fill paint is used. The given bullet is used. The
	 * given newline factor is used.
	 *
	 * @param  theText     Text.
	 * @param  theFont     Font.
	 * @param  theFill     Fill paint.
	 * @param  theBullet   Bullet. If null, no bullet is drawn.
	 * @param  theNewline  Newline factor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theFont</TT> is null or <TT>theFill</TT> is null.
	 */
	public TextItem
		(String theText,
		 Font theFont,
		 Fill theFill,
		 Bullet theBullet,
		 double theNewline)
		{
		this
			(theText,
			 Point.last(),
			 theDefaultPosition,
			 theFont,
			 theFill,
			 theBullet,
			 theNewline);
		}

	/**
	 * Construct a new text item with the given text. The anchor point is
	 * <TT>Point.last()</TT>. The given text position is used. The default font
	 * is used. The default fill paint is used. The text has no bullet. The
	 * default newline factor is used.
	 *
	 * @param  theText      Text.
	 * @param  thePosition  Text position relative to the anchor point.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public TextItem
		(String theText,
		 int thePosition)
		{
		this
			(theText,
			 Point.last(),
			 thePosition,
			 theDefaultFont,
			 theDefaultFill,
			 theDefaultBullet,
			 theDefaultNewline);
		}

	/**
	 * Construct a new text item with the given text. The anchor point is
	 * <TT>Point.last()</TT>. The given text position is used. The default font
	 * is used. The default fill paint is used. The text has no bullet. The
	 * given newline factor is used.
	 *
	 * @param  theText      Text.
	 * @param  thePosition  Text position relative to the anchor point.
	 * @param  theNewline   Newline factor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public TextItem
		(String theText,
		 int thePosition,
		 double theNewline)
		{
		this
			(theText,
			 Point.last(),
			 thePosition,
			 theDefaultFont,
			 theDefaultFill,
			 theDefaultBullet,
			 theNewline);
		}

	/**
	 * Construct a new text item with the given text. The anchor point is
	 * <TT>Point.last()</TT>. The given text position is used. The default font
	 * is used. The default fill paint is used. The given bullet is used. The
	 * default newline factor is used.
	 *
	 * @param  theText      Text.
	 * @param  thePosition  Text position relative to the anchor point.
	 * @param  theBullet    Bullet. If null, no bullet is drawn.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public TextItem
		(String theText,
		 int thePosition,
		 Bullet theBullet)
		{
		this
			(theText,
			 Point.last(),
			 thePosition,
			 theDefaultFont,
			 theDefaultFill,
			 theBullet,
			 theDefaultNewline);
		}

	/**
	 * Construct a new text item with the given text. The anchor point is
	 * <TT>Point.last()</TT>. The given text position is used. The default font
	 * is used. The default fill paint is used. The given bullet is used. The
	 * given newline factor is used.
	 *
	 * @param  theText      Text.
	 * @param  thePosition  Text position relative to the anchor point.
	 * @param  theBullet    Bullet. If null, no bullet is drawn.
	 * @param  theNewline   Newline factor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public TextItem
		(String theText,
		 int thePosition,
		 Bullet theBullet,
		 double theNewline)
		{
		this
			(theText,
			 Point.last(),
			 thePosition,
			 theDefaultFont,
			 theDefaultFill,
			 theBullet,
			 theNewline);
		}

	/**
	 * Construct a new text item with the given text. The anchor point is
	 * <TT>Point.last()</TT>. The given text position is used. The default font
	 * is used. The given fill paint is used. The text has no bullet. The
	 * default newline factor is used.
	 *
	 * @param  theText      Text.
	 * @param  thePosition  Text position relative to the anchor point.
	 * @param  theFill      Fill paint.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theFill</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public TextItem
		(String theText,
		 int thePosition,
		 Fill theFill)
		{
		this
			(theText,
			 Point.last(),
			 thePosition,
			 theDefaultFont,
			 theFill,
			 theDefaultBullet,
			 theDefaultNewline);
		}

	/**
	 * Construct a new text item with the given text. The anchor point is
	 * <TT>Point.last()</TT>. The given text position is used. The default font
	 * is used. The given fill paint is used. The text has no bullet. The given
	 * newline factor is used.
	 *
	 * @param  theText      Text.
	 * @param  thePosition  Text position relative to the anchor point.
	 * @param  theFill      Fill paint.
	 * @param  theNewline   Newline factor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theFill</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public TextItem
		(String theText,
		 int thePosition,
		 Fill theFill,
		 double theNewline)
		{
		this
			(theText,
			 Point.last(),
			 thePosition,
			 theDefaultFont,
			 theFill,
			 theDefaultBullet,
			 theNewline);
		}

	/**
	 * Construct a new text item with the given text. The anchor point is
	 * <TT>Point.last()</TT>. The given text position is used. The default font
	 * is used. The given fill paint is used. The given bullet is used. The
	 * default newline factor is used.
	 *
	 * @param  theText      Text.
	 * @param  thePosition  Text position relative to the anchor point.
	 * @param  theFill      Fill paint.
	 * @param  theBullet    Bullet. If null, no bullet is drawn.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theFill</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public TextItem
		(String theText,
		 int thePosition,
		 Fill theFill,
		 Bullet theBullet)
		{
		this
			(theText,
			 Point.last(),
			 thePosition,
			 theDefaultFont,
			 theFill,
			 theBullet,
			 theDefaultNewline);
		}

	/**
	 * Construct a new text item with the given text. The anchor point is
	 * <TT>Point.last()</TT>. The given text position is used. The default font
	 * is used. The given fill paint is used. The given bullet is used. The
	 * given newline factor is used.
	 *
	 * @param  theText      Text.
	 * @param  thePosition  Text position relative to the anchor point.
	 * @param  theFill      Fill paint.
	 * @param  theBullet    Bullet. If null, no bullet is drawn.
	 * @param  theNewline   Newline factor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theFill</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public TextItem
		(String theText,
		 int thePosition,
		 Fill theFill,
		 Bullet theBullet,
		 double theNewline)
		{
		this
			(theText,
			 Point.last(),
			 thePosition,
			 theDefaultFont,
			 theFill,
			 theBullet,
			 theNewline);
		}

	/**
	 * Construct a new text item with the given text. The anchor point is
	 * <TT>Point.last()</TT>. The given text position is used. The given font is
	 * used. The default fill paint is used. The text has no bullet. The default
	 * newline factor is used.
	 *
	 * @param  theText      Text.
	 * @param  thePosition  Text position relative to the anchor point.
	 * @param  theFont      Font.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theFont</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public TextItem
		(String theText,
		 int thePosition,
		 Font theFont)
		{
		this
			(theText,
			 Point.last(),
			 thePosition,
			 theFont,
			 theDefaultFill,
			 theDefaultBullet,
			 theDefaultNewline);
		}

	/**
	 * Construct a new text item with the given text. The anchor point is
	 * <TT>Point.last()</TT>. The given text position is used. The given font is
	 * used. The default fill paint is used. The text has no bullet. The given
	 * newline factor is used.
	 *
	 * @param  theText      Text.
	 * @param  thePosition  Text position relative to the anchor point.
	 * @param  theFont      Font.
	 * @param  theNewline   Newline factor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theFont</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public TextItem
		(String theText,
		 int thePosition,
		 Font theFont,
		 double theNewline)
		{
		this
			(theText,
			 Point.last(),
			 thePosition,
			 theFont,
			 theDefaultFill,
			 theDefaultBullet,
			 theNewline);
		}

	/**
	 * Construct a new text item with the given text. The anchor point is
	 * <TT>Point.last()</TT>. The given text position is used. The given font is
	 * used. The default fill paint is used. The given bullet is used. The
	 * default newline factor is used.
	 *
	 * @param  theText      Text.
	 * @param  thePosition  Text position relative to the anchor point.
	 * @param  theFont      Font.
	 * @param  theBullet    Bullet. If null, no bullet is drawn.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theFont</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public TextItem
		(String theText,
		 int thePosition,
		 Font theFont,
		 Bullet theBullet)
		{
		this
			(theText,
			 Point.last(),
			 thePosition,
			 theFont,
			 theDefaultFill,
			 theBullet,
			 theDefaultNewline);
		}

	/**
	 * Construct a new text item with the given text. The anchor point is
	 * <TT>Point.last()</TT>. The given text position is used. The given font is
	 * used. The default fill paint is used. The given bullet is used. The given
	 * newline factor is used.
	 *
	 * @param  theText      Text.
	 * @param  thePosition  Text position relative to the anchor point.
	 * @param  theFont      Font.
	 * @param  theBullet    Bullet. If null, no bullet is drawn.
	 * @param  theNewline   Newline factor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theFont</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public TextItem
		(String theText,
		 int thePosition,
		 Font theFont,
		 Bullet theBullet,
		 double theNewline)
		{
		this
			(theText,
			 Point.last(),
			 thePosition,
			 theFont,
			 theDefaultFill,
			 theBullet,
			 theNewline);
		}

	/**
	 * Construct a new text item with the given text. The anchor point is
	 * <TT>Point.last()</TT>. The given text position is used. The given font is
	 * used. The given fill paint is used. The text has no bullet. The default
	 * newline factor is used.
	 *
	 * @param  theText      Text.
	 * @param  thePosition  Text position relative to the anchor point.
	 * @param  theFont      Font.
	 * @param  theFill      Fill paint.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theFont</TT> is null or <TT>theFill</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public TextItem
		(String theText,
		 int thePosition,
		 Font theFont,
		 Fill theFill)
		{
		this
			(theText,
			 Point.last(),
			 thePosition,
			 theFont,
			 theFill,
			 theDefaultBullet,
			 theDefaultNewline);
		}

	/**
	 * Construct a new text item with the given text. The anchor point is
	 * <TT>Point.last()</TT>. The given text position is used. The given font is
	 * used. The given fill paint is used. The text has no bullet. The given
	 * newline factor is used.
	 *
	 * @param  theText      Text.
	 * @param  thePosition  Text position relative to the anchor point.
	 * @param  theFont      Font.
	 * @param  theFill      Fill paint.
	 * @param  theNewline   Newline factor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theFont</TT> is null or <TT>theFill</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public TextItem
		(String theText,
		 int thePosition,
		 Font theFont,
		 Fill theFill,
		 double theNewline)
		{
		this
			(theText,
			 Point.last(),
			 thePosition,
			 theFont,
			 theFill,
			 theDefaultBullet,
			 theNewline);
		}

	/**
	 * Construct a new text item with the given text. The anchor point is
	 * <TT>Point.last()</TT>. The given text position is used. The given font is
	 * used. The given fill paint is used. The given bullet is used. The default
	 * newline factor is used.
	 *
	 * @param  theText      Text.
	 * @param  thePosition  Text position relative to the anchor point.
	 * @param  theFont      Font.
	 * @param  theFill      Fill paint.
	 * @param  theBullet    Bullet. If null, no bullet is drawn.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theFont</TT> is null or <TT>theFill</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public TextItem
		(String theText,
		 int thePosition,
		 Font theFont,
		 Fill theFill,
		 Bullet theBullet)
		{
		this
			(theText,
			 Point.last(),
			 thePosition,
			 theFont,
			 theFill,
			 theBullet,
			 theDefaultNewline);
		}

	/**
	 * Construct a new text item with the given text. The anchor point is
	 * <TT>Point.last()</TT>. The given text position is used. The given font is
	 * used. The given fill paint is used. The given bullet is used. The given
	 * newline factor is used.
	 *
	 * @param  theText      Text.
	 * @param  thePosition  Text position relative to the anchor point.
	 * @param  theFont      Font.
	 * @param  theFill      Fill paint.
	 * @param  theBullet    Bullet. If null, no bullet is drawn.
	 * @param  theNewline   Newline factor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theFont</TT> is null or <TT>theFill</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public TextItem
		(String theText,
		 int thePosition,
		 Font theFont,
		 Fill theFill,
		 Bullet theBullet,
		 double theNewline)
		{
		this
			(theText,
			 Point.last(),
			 thePosition,
			 theFont,
			 theFill,
			 theBullet,
			 theNewline);
		}

	/**
	 * Construct a new text item with the given text and anchor point. The
	 * default text position is used. The default font is used. The default fill
	 * paint is used. The text has no bullet. The default newline factor is
	 * used.
	 *
	 * @param  theText    Text.
	 * @param  theAnchor  Anchor point.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theAnchor</TT> is null.
	 */
	public TextItem
		(String theText,
		 Point theAnchor)
		{
		this
			(theText,
			 theAnchor,
			 theDefaultPosition,
			 theDefaultFont,
			 theDefaultFill,
			 theDefaultBullet,
			 theDefaultNewline);
		}

	/**
	 * Construct a new text item with the given text and anchor point. The
	 * default text position is used. The default font is used. The default fill
	 * paint is used. The text has no bullet. The given newline factor is used.
	 *
	 * @param  theText     Text.
	 * @param  theAnchor   Anchor point.
	 * @param  theNewline  Newline factor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theAnchor</TT> is null.
	 */
	public TextItem
		(String theText,
		 Point theAnchor,
		 double theNewline)
		{
		this
			(theText,
			 theAnchor,
			 theDefaultPosition,
			 theDefaultFont,
			 theDefaultFill,
			 theDefaultBullet,
			 theNewline);
		}

	/**
	 * Construct a new text item with the given text and anchor point. The
	 * default text position is used. The default font is used. The default fill
	 * paint is used. The given bullet is used. The default newline factor is
	 * used.
	 *
	 * @param  theText    Text.
	 * @param  theAnchor  Anchor point.
	 * @param  theBullet  Bullet. If null, no bullet is drawn.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theAnchor</TT> is null.
	 */
	public TextItem
		(String theText,
		 Point theAnchor,
		 Bullet theBullet)
		{
		this
			(theText,
			 theAnchor,
			 theDefaultPosition,
			 theDefaultFont,
			 theDefaultFill,
			 theBullet,
			 theDefaultNewline);
		}

	/**
	 * Construct a new text item with the given text and anchor point. The
	 * default text position is used. The default font is used. The default fill
	 * paint is used. The given bullet is used. The given newline factor is
	 * used.
	 *
	 * @param  theText     Text.
	 * @param  theAnchor   Anchor point.
	 * @param  theBullet   Bullet. If null, no bullet is drawn.
	 * @param  theNewline  Newline factor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theAnchor</TT> is null.
	 */
	public TextItem
		(String theText,
		 Point theAnchor,
		 Bullet theBullet,
		 double theNewline)
		{
		this
			(theText,
			 theAnchor,
			 theDefaultPosition,
			 theDefaultFont,
			 theDefaultFill,
			 theBullet,
			 theNewline);
		}

	/**
	 * Construct a new text item with the given text and anchor point. The
	 * default text position is used. The default font is used. The given fill
	 * paint is used. The text has no bullet. The default newline factor is
	 * used.
	 *
	 * @param  theText    Text.
	 * @param  theAnchor  Anchor point.
	 * @param  theFill    Fill paint.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theAnchor</TT> is null or <TT>theFill</TT> is null.
	 */
	public TextItem
		(String theText,
		 Point theAnchor,
		 Fill theFill)
		{
		this
			(theText,
			 theAnchor,
			 theDefaultPosition,
			 theDefaultFont,
			 theFill,
			 theDefaultBullet,
			 theDefaultNewline);
		}

	/**
	 * Construct a new text item with the given text and anchor point. The
	 * default text position is used. The default font is used. The given fill
	 * paint is used. The text has no bullet. The given newline factor is used.
	 *
	 * @param  theText     Text.
	 * @param  theAnchor   Anchor point.
	 * @param  theFill     Fill paint.
	 * @param  theNewline  Newline factor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theAnchor</TT> is null or <TT>theFill</TT> is null.
	 */
	public TextItem
		(String theText,
		 Point theAnchor,
		 Fill theFill,
		 double theNewline)
		{
		this
			(theText,
			 theAnchor,
			 theDefaultPosition,
			 theDefaultFont,
			 theFill,
			 theDefaultBullet,
			 theNewline);
		}

	/**
	 * Construct a new text item with the given text and anchor point. The
	 * default text position is used. The default font is used. The given fill
	 * paint is used. The given bullet is used. The default newline factor is
	 * used.
	 *
	 * @param  theText    Text.
	 * @param  theAnchor  Anchor point.
	 * @param  theFill    Fill paint.
	 * @param  theBullet  Bullet. If null, no bullet is drawn.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theAnchor</TT> is null or <TT>theFill</TT> is null.
	 */
	public TextItem
		(String theText,
		 Point theAnchor,
		 Fill theFill,
		 Bullet theBullet)
		{
		this
			(theText,
			 theAnchor,
			 theDefaultPosition,
			 theDefaultFont,
			 theFill,
			 theBullet,
			 theDefaultNewline);
		}

	/**
	 * Construct a new text item with the given text and anchor point. The
	 * default text position is used. The default font is used. The given fill
	 * paint is used. The given bullet is used. The given newline factor is
	 * used.
	 *
	 * @param  theText     Text.
	 * @param  theAnchor   Anchor point.
	 * @param  theFill     Fill paint.
	 * @param  theBullet   Bullet. If null, no bullet is drawn.
	 * @param  theNewline  Newline factor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theAnchor</TT> is null or <TT>theFill</TT> is null.
	 */
	public TextItem
		(String theText,
		 Point theAnchor,
		 Fill theFill,
		 Bullet theBullet,
		 double theNewline)
		{
		this
			(theText,
			 theAnchor,
			 theDefaultPosition,
			 theDefaultFont,
			 theFill,
			 theBullet,
			 theNewline);
		}

	/**
	 * Construct a new text item with the given text and anchor point. The
	 * default text position is used. The given font is used. The default fill
	 * paint is used. The text has no bullet. The default newline factor is
	 * used.
	 *
	 * @param  theText    Text.
	 * @param  theAnchor  Anchor point.
	 * @param  theFont    Font.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theAnchor</TT> is null or <TT>theFont</TT> is null.
	 */
	public TextItem
		(String theText,
		 Point theAnchor,
		 Font theFont)
		{
		this
			(theText,
			 theAnchor,
			 theDefaultPosition,
			 theFont,
			 theDefaultFill,
			 theDefaultBullet,
			 theDefaultNewline);
		}

	/**
	 * Construct a new text item with the given text and anchor point. The
	 * default text position is used. The given font is used. The default fill
	 * paint is used. The text has no bullet. The given newline factor is used.
	 *
	 * @param  theText     Text.
	 * @param  theAnchor   Anchor point.
	 * @param  theFont     Font.
	 * @param  theNewline  Newline factor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theAnchor</TT> is null or <TT>theFont</TT> is null.
	 */
	public TextItem
		(String theText,
		 Point theAnchor,
		 Font theFont,
		 double theNewline)
		{
		this
			(theText,
			 theAnchor,
			 theDefaultPosition,
			 theFont,
			 theDefaultFill,
			 theDefaultBullet,
			 theNewline);
		}

	/**
	 * Construct a new text item with the given text and anchor point. The
	 * default text position is used. The given font is used. The default fill
	 * paint is used. The given bullet is used. The default newline factor is
	 * used.
	 *
	 * @param  theText    Text.
	 * @param  theAnchor  Anchor point.
	 * @param  theFont    Font.
	 * @param  theBullet  Bullet. If null, no bullet is drawn.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theAnchor</TT> is null or <TT>theFont</TT> is null.
	 */
	public TextItem
		(String theText,
		 Point theAnchor,
		 Font theFont,
		 Bullet theBullet)
		{
		this
			(theText,
			 theAnchor,
			 theDefaultPosition,
			 theFont,
			 theDefaultFill,
			 theBullet,
			 theDefaultNewline);
		}

	/**
	 * Construct a new text item with the given text and anchor point. The
	 * default text position is used. The given font is used. The default fill
	 * paint is used. The given bullet is used. The given newline factor is
	 * used.
	 *
	 * @param  theText     Text.
	 * @param  theAnchor   Anchor point.
	 * @param  theFont     Font.
	 * @param  theBullet   Bullet. If null, no bullet is drawn.
	 * @param  theNewline  Newline factor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theAnchor</TT> is null or <TT>theFont</TT> is null.
	 */
	public TextItem
		(String theText,
		 Point theAnchor,
		 Font theFont,
		 Bullet theBullet,
		 double theNewline)
		{
		this
			(theText,
			 theAnchor,
			 theDefaultPosition,
			 theFont,
			 theDefaultFill,
			 theBullet,
			 theNewline);
		}

	/**
	 * Construct a new text item with the given text and anchor point. The
	 * default text position is used. The given font is used. The given fill
	 * paint is used. The text has no bullet. The default newline factor is
	 * used.
	 *
	 * @param  theText    Text.
	 * @param  theAnchor  Anchor point.
	 * @param  theFont    Font.
	 * @param  theFill    Fill paint.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theAnchor</TT> is null or <TT>theFont</TT> is null or
	 *     <TT>theFill</TT> is null.
	 */
	public TextItem
		(String theText,
		 Point theAnchor,
		 Font theFont,
		 Fill theFill)
		{
		this
			(theText,
			 theAnchor,
			 theDefaultPosition,
			 theFont,
			 theFill,
			 theDefaultBullet,
			 theDefaultNewline);
		}

	/**
	 * Construct a new text item with the given text and anchor point. The
	 * default text position is used. The given font is used. The given fill
	 * paint is used. The text has no bullet. The given newline factor is used.
	 *
	 * @param  theText     Text.
	 * @param  theAnchor   Anchor point.
	 * @param  theFont     Font.
	 * @param  theFill     Fill paint.
	 * @param  theNewline  Newline factor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theAnchor</TT> is null or <TT>theFont</TT> is null or
	 *     <TT>theFill</TT> is null.
	 */
	public TextItem
		(String theText,
		 Point theAnchor,
		 Font theFont,
		 Fill theFill,
		 double theNewline)
		{
		this
			(theText,
			 theAnchor,
			 theDefaultPosition,
			 theFont,
			 theFill,
			 theDefaultBullet,
			 theNewline);
		}

	/**
	 * Construct a new text item with the given text and anchor point. The
	 * default text position is used. The given font is used. The given fill
	 * paint is used. The given bullet is used. The default newline factor is
	 * used.
	 *
	 * @param  theText    Text.
	 * @param  theAnchor  Anchor point.
	 * @param  theFont    Font.
	 * @param  theFill    Fill paint.
	 * @param  theBullet  Bullet. If null, no bullet is drawn.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theAnchor</TT> is null or <TT>theFont</TT> is null or
	 *     <TT>theFill</TT> is null.
	 */
	public TextItem
		(String theText,
		 Point theAnchor,
		 Font theFont,
		 Fill theFill,
		 Bullet theBullet)
		{
		this
			(theText,
			 theAnchor,
			 theDefaultPosition,
			 theFont,
			 theFill,
			 theBullet,
			 theDefaultNewline);
		}

	/**
	 * Construct a new text item with the given text and anchor point. The
	 * default text position is used. The given font is used. The given fill
	 * paint is used. The given bullet is used. The given newline factor is
	 * used.
	 *
	 * @param  theText     Text.
	 * @param  theAnchor   Anchor point.
	 * @param  theFont     Font.
	 * @param  theFill     Fill paint.
	 * @param  theBullet   Bullet. If null, no bullet is drawn.
	 * @param  theNewline  Newline factor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theAnchor</TT> is null or <TT>theFont</TT> is null or
	 *     <TT>theFill</TT> is null.
	 */
	public TextItem
		(String theText,
		 Point theAnchor,
		 Font theFont,
		 Fill theFill,
		 Bullet theBullet,
		 double theNewline)
		{
		this
			(theText,
			 theAnchor,
			 theDefaultPosition,
			 theFont,
			 theFill,
			 theBullet,
			 theNewline);
		}

	/**
	 * Construct a new text item with the given text and anchor point. The given
	 * text position is used. The default font is used. The default fill paint
	 * is used. The text has no bullet. The default newline factor is used.
	 *
	 * @param  theText      Text.
	 * @param  theAnchor    Anchor point.
	 * @param  thePosition  Text position relative to the anchor point.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theAnchor</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public TextItem
		(String theText,
		 Point theAnchor,
		 int thePosition)
		{
		this
			(theText,
			 theAnchor,
			 thePosition,
			 theDefaultFont,
			 theDefaultFill,
			 theDefaultBullet,
			 theDefaultNewline);
		}

	/**
	 * Construct a new text item with the given text and anchor point. The
	 * given text position is used. The default font is used. The default fill
	 * paint is used. The text has no bullet. The given newline factor is used.
	 *
	 * @param  theText      Text.
	 * @param  theAnchor    Anchor point.
	 * @param  thePosition  Text position relative to the anchor point.
	 * @param  theNewline   Newline factor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theAnchor</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public TextItem
		(String theText,
		 Point theAnchor,
		 int thePosition,
		 double theNewline)
		{
		this
			(theText,
			 theAnchor,
			 thePosition,
			 theDefaultFont,
			 theDefaultFill,
			 theDefaultBullet,
			 theNewline);
		}

	/**
	 * Construct a new text item with the given text and anchor point. The given
	 * text position is used. The default font is used. The default fill paint
	 * is used. The given bullet is used. The default newline factor is used.
	 *
	 * @param  theText      Text.
	 * @param  theAnchor    Anchor point.
	 * @param  thePosition  Text position relative to the anchor point.
	 * @param  theBullet    Bullet. If null, no bullet is drawn.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theAnchor</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public TextItem
		(String theText,
		 Point theAnchor,
		 int thePosition,
		 Bullet theBullet)
		{
		this
			(theText,
			 theAnchor,
			 thePosition,
			 theDefaultFont,
			 theDefaultFill,
			 theBullet,
			 theDefaultNewline);
		}

	/**
	 * Construct a new text item with the given text and anchor point. The given
	 * text position is used. The default font is used. The default fill paint
	 * is used. The given bullet is used. The given newline factor is used.
	 *
	 * @param  theText      Text.
	 * @param  theAnchor    Anchor point.
	 * @param  thePosition  Text position relative to the anchor point.
	 * @param  theBullet    Bullet. If null, no bullet is drawn.
	 * @param  theNewline   Newline factor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theAnchor</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public TextItem
		(String theText,
		 Point theAnchor,
		 int thePosition,
		 Bullet theBullet,
		 double theNewline)
		{
		this
			(theText,
			 theAnchor,
			 thePosition,
			 theDefaultFont,
			 theDefaultFill,
			 theBullet,
			 theNewline);
		}

	/**
	 * Construct a new text item with the given text and anchor point. The given
	 * text position is used. The default font is used. The given fill paint is
	 * used. The text has no bullet. The default newline factor is used.
	 *
	 * @param  theText      Text.
	 * @param  theAnchor    Anchor point.
	 * @param  thePosition  Text position relative to the anchor point.
	 * @param  theFill      Fill paint.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theAnchor</TT> is null or <TT>theFill</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public TextItem
		(String theText,
		 Point theAnchor,
		 int thePosition,
		 Fill theFill)
		{
		this
			(theText,
			 theAnchor,
			 thePosition,
			 theDefaultFont,
			 theFill,
			 theDefaultBullet,
			 theDefaultNewline);
		}

	/**
	 * Construct a new text item with the given text and anchor point. The
	 * given text position is used. The default font is used. The given fill
	 * paint is used. The text has no bullet. The given newline factor is used.
	 *
	 * @param  theText      Text.
	 * @param  theAnchor    Anchor point.
	 * @param  thePosition  Text position relative to the anchor point.
	 * @param  theFill      Fill paint.
	 * @param  theNewline   Newline factor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theAnchor</TT> is null or <TT>theFill</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public TextItem
		(String theText,
		 Point theAnchor,
		 int thePosition,
		 Fill theFill,
		 double theNewline)
		{
		this
			(theText,
			 theAnchor,
			 thePosition,
			 theDefaultFont,
			 theFill,
			 theDefaultBullet,
			 theNewline);
		}

	/**
	 * Construct a new text item with the given text and anchor point. The given
	 * text position is used. The default font is used. The given fill paint is
	 * used. The given bullet is used. The default newline factor is used.
	 *
	 * @param  theText      Text.
	 * @param  theAnchor    Anchor point.
	 * @param  thePosition  Text position relative to the anchor point.
	 * @param  theFill      Fill paint.
	 * @param  theBullet    Bullet. If null, no bullet is drawn.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theAnchor</TT> is null or <TT>theFill</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public TextItem
		(String theText,
		 Point theAnchor,
		 int thePosition,
		 Fill theFill,
		 Bullet theBullet)
		{
		this
			(theText,
			 theAnchor,
			 thePosition,
			 theDefaultFont,
			 theFill,
			 theBullet,
			 theDefaultNewline);
		}

	/**
	 * Construct a new text item with the given text and anchor point. The given
	 * text position is used. The default font is used. The given fill paint is
	 * used. The given bullet is used. The given newline factor is used.
	 *
	 * @param  theText      Text.
	 * @param  theAnchor    Anchor point.
	 * @param  thePosition  Text position relative to the anchor point.
	 * @param  theFill      Fill paint.
	 * @param  theBullet    Bullet. If null, no bullet is drawn.
	 * @param  theNewline   Newline factor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theAnchor</TT> is null or <TT>theFill</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public TextItem
		(String theText,
		 Point theAnchor,
		 int thePosition,
		 Fill theFill,
		 Bullet theBullet,
		 double theNewline)
		{
		this
			(theText,
			 theAnchor,
			 thePosition,
			 theDefaultFont,
			 theFill,
			 theBullet,
			 theNewline);
		}

	/**
	 * Construct a new text item with the given text and anchor point. The given
	 * text position is used. The given font is used. The default fill paint is
	 * used. The text has no bullet. The default newline factor is used.
	 *
	 * @param  theText      Text.
	 * @param  theAnchor    Anchor point.
	 * @param  thePosition  Text position relative to the anchor point.
	 * @param  theFont      Font.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theAnchor</TT> is null or <TT>theFont</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public TextItem
		(String theText,
		 Point theAnchor,
		 int thePosition,
		 Font theFont)
		{
		this
			(theText,
			 theAnchor,
			 thePosition,
			 theFont,
			 theDefaultFill,
			 theDefaultBullet,
			 theDefaultNewline);
		}

	/**
	 * Construct a new text item with the given text and anchor point. The
	 * given text position is used. The given font is used. The default fill
	 * paint is used. The text has no bullet. The given newline factor is used.
	 *
	 * @param  theText      Text.
	 * @param  theAnchor    Anchor point.
	 * @param  thePosition  Text position relative to the anchor point.
	 * @param  theFont      Font.
	 * @param  theNewline   Newline factor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theAnchor</TT> is null or <TT>theFont</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public TextItem
		(String theText,
		 Point theAnchor,
		 int thePosition,
		 Font theFont,
		 double theNewline)
		{
		this
			(theText,
			 theAnchor,
			 thePosition,
			 theFont,
			 theDefaultFill,
			 theDefaultBullet,
			 theNewline);
		}

	/**
	 * Construct a new text item with the given text and anchor point. The given
	 * text position is used. The given font is used. The default fill paint is
	 * used. The given bullet is used. The default newline factor is used.
	 *
	 * @param  theText      Text.
	 * @param  theAnchor    Anchor point.
	 * @param  thePosition  Text position relative to the anchor point.
	 * @param  theFont      Font.
	 * @param  theBullet    Bullet. If null, no bullet is drawn.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theAnchor</TT> is null or <TT>theFont</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public TextItem
		(String theText,
		 Point theAnchor,
		 int thePosition,
		 Font theFont,
		 Bullet theBullet)
		{
		this
			(theText,
			 theAnchor,
			 thePosition,
			 theFont,
			 theDefaultFill,
			 theBullet,
			 theDefaultNewline);
		}

	/**
	 * Construct a new text item with the given text and anchor point. The given
	 * text position is used. The given font is used. The default fill paint is
	 * used. The given bullet is used. The given newline factor is used.
	 *
	 * @param  theText      Text.
	 * @param  theAnchor    Anchor point.
	 * @param  thePosition  Text position relative to the anchor point.
	 * @param  theFont      Font.
	 * @param  theBullet    Bullet. If null, no bullet is drawn.
	 * @param  theNewline   Newline factor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theAnchor</TT> is null or <TT>theFont</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public TextItem
		(String theText,
		 Point theAnchor,
		 int thePosition,
		 Font theFont,
		 Bullet theBullet,
		 double theNewline)
		{
		this
			(theText,
			 theAnchor,
			 thePosition,
			 theFont,
			 theDefaultFill,
			 theBullet,
			 theNewline);
		}

	/**
	 * Construct a new text item with the given text and anchor point. The given
	 * text position is used. The given font is used. The given fill paint is
	 * used. The text has no bullet. The default newline factor is used.
	 *
	 * @param  theText      Text.
	 * @param  theAnchor    Anchor point.
	 * @param  thePosition  Text position relative to the anchor point.
	 * @param  theFont      Font.
	 * @param  theFill      Fill paint.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theAnchor</TT> is null or <TT>theFont</TT> is null or
	 *     <TT>theFill</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public TextItem
		(String theText,
		 Point theAnchor,
		 int thePosition,
		 Font theFont,
		 Fill theFill)
		{
		this
			(theText,
			 theAnchor,
			 thePosition,
			 theFont,
			 theFill,
			 theDefaultBullet,
			 theDefaultNewline);
		}

	/**
	 * Construct a new text item with the given text and anchor point. The
	 * given text position is used. The given font is used. The given fill
	 * paint is used. The text has no bullet. The given newline factor is used.
	 *
	 * @param  theText      Text.
	 * @param  theAnchor    Anchor point.
	 * @param  thePosition  Text position relative to the anchor point.
	 * @param  theFont      Font.
	 * @param  theFill      Fill paint.
	 * @param  theNewline   Newline factor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theAnchor</TT> is null or <TT>theFont</TT> is null or
	 *     <TT>theFill</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public TextItem
		(String theText,
		 Point theAnchor,
		 int thePosition,
		 Font theFont,
		 Fill theFill,
		 double theNewline)
		{
		this
			(theText,
			 theAnchor,
			 thePosition,
			 theFont,
			 theFill,
			 theDefaultBullet,
			 theNewline);
		}

	/**
	 * Construct a new text item with the given text and anchor point. The given
	 * text position is used. The given font is used. The given fill paint is
	 * used. The given bullet is used. The default newline factor is used.
	 *
	 * @param  theText      Text.
	 * @param  theAnchor    Anchor point.
	 * @param  thePosition  Text position relative to the anchor point.
	 * @param  theFont      Font.
	 * @param  theFill      Fill paint.
	 * @param  theBullet    Bullet. If null, no bullet is drawn.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theAnchor</TT> is null or <TT>theFont</TT> is null or
	 *     <TT>theFill</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public TextItem
		(String theText,
		 Point theAnchor,
		 int thePosition,
		 Font theFont,
		 Fill theFill,
		 Bullet theBullet)
		{
		this
			(theText,
			 theAnchor,
			 thePosition,
			 theFont,
			 theFill,
			 theBullet,
			 theDefaultNewline);
		}

	/**
	 * Construct a new text item with the given text and anchor point. The given
	 * text position is used. The given font is used. The given fill paint is
	 * used. The given bullet is used. The given newline factor is used.
	 *
	 * @param  theText      Text.
	 * @param  theAnchor    Anchor point.
	 * @param  thePosition  Text position relative to the anchor point.
	 * @param  theFont      Font.
	 * @param  theFill      Fill paint.
	 * @param  theBullet    Bullet. If null, no bullet is drawn.
	 * @param  theNewline   Newline factor.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theText</TT> is null or
	 *     <TT>theAnchor</TT> is null or <TT>theFont</TT> is null or
	 *     <TT>theFill</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public TextItem
		(String theText,
		 Point theAnchor,
		 int thePosition,
		 Font theFont,
		 Fill theFill,
		 Bullet theBullet,
		 double theNewline)
		{
		super();

		// Verify preconditions.
		if (theText == null)
			{
			throw new NullPointerException ("theText is null");
			}
		if (theAnchor == null)
			{
			throw new NullPointerException ("theAnchor is null");
			}
		if (ABOVE_LEFT > thePosition || thePosition > BELOW_RIGHT)
			{
			throw new IllegalArgumentException ("thePosition is illegal");
			}
		if (theFont == null)
			{
			throw new NullPointerException ("theFont is null");
			}
		if (theFill == null)
			{
			throw new NullPointerException ("theFill is null");
			}

		// Record attributes.
		myText = theText;
		myAnchor = theAnchor;
		myPosition = thePosition;
		myFont = theFont;
		myFill = theFill;
		myBullet = theBullet;
		myNewline = theNewline;

		// Record last text item.
		theLastTextItem = this;

		// Record last point.
		Point.theLastPoint = next();
		}

// Exported operations.

	/**
	 * Returns the last text item created. If no text items have been created
	 * yet, null is returned.
	 */
	public static TextItem last()
		{
		return theLastTextItem;
		}

	/**
	 * Set the default text position. Before calling this method the first time,
	 * the default text position is <TT>BELOW_RIGHT</TT>.
	 *
	 * @param  thePosition  Default text position relative to the anchor point.
	 *
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>thePosition</TT> is not one of
	 *     the allowed values.
	 */
	public static void setDefaultPosition
		(int thePosition)
		{
		if (ABOVE_LEFT > thePosition || thePosition > BELOW_RIGHT)
			{
			throw new IllegalArgumentException ("thePosition is illegal");
			}
		theDefaultPosition = thePosition;
		}

	/**
	 * Set the default font. Before calling this method the first time, the
	 * default font is sanserif, plain, 24 point.
	 *
	 * @param  theFont  Font.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theFont</TT> is null.
	 */
	public static void setDefaultFont
		(Font theFont)
		{
		if (theFont == null)
			{
			throw new NullPointerException ("theFont is null");
			}
		theDefaultFont = theFont;
		}

	/**
	 * Set the default fill paint. Before calling this method the first time,
	 * the default fill paint is black.
	 *
	 * @param  theFill  Fill paint.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>theFill</TT> is null.
	 */
	public static void setDefaultFill
		(Fill theFill)
		{
		if (theFill == null)
			{
			throw new NullPointerException ("theFill is null");
			}
		theDefaultFill = theFill;
		}

	/**
	 * Set the default bullet. Before calling this method the first time, the
	 * default is no bullet.
	 *
	 * @param  theBullet  Bullet. If null, no bullet is drawn.
	 */
	public static void setDefaultBullet
		(Bullet theBullet)
		{
		theDefaultBullet = theBullet;
		}

	/**
	 * Set the default newline factor. Before calling this method the first
	 * time, the default newline factor is 1.25.
	 *
	 * @param  theNewline  Newline factor.
	 */
	public static void setDefaultNewline
		(double theNewline)
		{
		theDefaultNewline = theNewline;
		}

	/**
	 * Returns this text item's location, namely its anchor point.
	 */
	public Point location()
		{
		return myAnchor;
		}

	/**
	 * Returns this text item's next location. The next location is this text
	 * item's anchor point, offset in the positive Y direction by a distance
	 * equal to this text item's newline factor times this text item's font
	 * size. This is typically the point at which to place the next text item.
	 */
	public Point next()
		{
		return myAnchor.s (myNewline * myFont.getSize2D());
		}

	/**
	 * Draw this slide item in the given graphics context. This method is
	 * allowed to change the graphics context's paint, stroke, and transform,
	 * and it doesn't have to change them back.
	 *
	 * @param  g2d  2-D graphics context.
	 */
	public void draw
		(Graphics2D g2d)
		{
		// Set up font and get metrics.
		Font oldFont = g2d.getFont();
		g2d.setFont (myFont);
		FontRenderContext frc = g2d.getFontRenderContext();
		Rectangle2D bounds = myFont.getStringBounds (myText, frc);
		LineMetrics metrics = myFont.getLineMetrics (myText, frc);
		double a = metrics.getAscent();
		double d = metrics.getDescent();
		double w = bounds.getWidth();
		double h = a + d;

		// Compute (x,y) coordinates of left end of text baseline.
		double x;
		double y;
		switch (myPosition)
			{
			case ABOVE_LEFT:
				x = myAnchor.x - w;
				y = myAnchor.y - d;
				break;
			case ABOVE:
				x = myAnchor.x - w / 2;
				y = myAnchor.y - d;
				break;
			case ABOVE_RIGHT:
				x = myAnchor.x;
				y = myAnchor.y - d;
				break;
			case LEFT:
				x = myAnchor.x - w;
				y = myAnchor.y + a / 2;
				break;
			case CENTER:
				x = myAnchor.x - w / 2;
				y = myAnchor.y + a / 2;
				break;
			case RIGHT:
				x = myAnchor.x;
				y = myAnchor.y + a / 2;
				break;
			case BELOW_LEFT:
				x = myAnchor.x - w;
				y = myAnchor.y + a;
				break;
			case BELOW:
				x = myAnchor.x - w / 2;
				y = myAnchor.y + a;
				break;
			case BELOW_RIGHT:
				x = myAnchor.x;
				y = myAnchor.y + a;
				break;
			default:
				// Shouldn't happen.
				throw new IllegalStateException();
			}
//System.out.println ("bounds = " + bounds);
//System.out.println ("a = " + a);
//System.out.println ("d = " + d);
//System.out.println ("x = " + x);
//System.out.println ("y = " + y);

		// Draw label text.
		myFill.setGraphicsContext (g2d);
		g2d.drawString (myText, (float) x, (float) y);

		// Draw bullet if any.
		if (myBullet != null)
			{
			myBullet.draw (g2d, a, x, y);
			}

		// Restore font.
		g2d.setFont (oldFont);
		}

	/**
	 * Write this text item to the given object output stream.
	 *
	 * @param  out  Object output stream.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void writeExternal
		(ObjectOutput out)
		throws IOException
		{
		out.writeObject (myText);
		out.writeObject (myAnchor);
		out.writeByte ((byte) myPosition);
		out.writeObject (myFont);
		out.writeObject (myFill);
		out.writeObject (myBullet);
		out.writeDouble (myNewline);
		}

	/**
	 * Read this text item from the given object input stream.
	 *
	 * @param  in  Object input stream.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 * @exception  ClassNotFoundException
	 *     Thrown if any class needed to deserialize this text item cannot be
	 *     found.
	 */
	public void readExternal
		(ObjectInput in)
		throws IOException, ClassNotFoundException
		{
		myText = (String) in.readObject();
		myAnchor = (Point) in.readObject();
		myPosition = in.readByte();
		myFont = (Font) in.readObject();
		myFill = (Fill) in.readObject();
		myBullet = (Bullet) in.readObject();
		myNewline = in.readDouble();
		}

	}
