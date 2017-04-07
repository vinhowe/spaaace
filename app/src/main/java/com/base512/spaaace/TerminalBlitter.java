package com.base512.spaaace;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;


public class TerminalBlitter {
	int columns;
	int rows;
	int cellWidth;
	int cellHeight;
	static final int[] colors = new int[] {
		0x7f0000,	// r
		0x007f00,	// g
		0x7f7f00,	// y
		0x00007f,	// b
		0x7f007f,	// m
		0x007f7f,	// c
		0x7f7f7f,	// w
		0xff0000,	// R
		0x00ff00,	// G
		0xffff00,	// Y
		0x0000ff,	// B
		0xff00ff,	// M
		0x00ffff,	// C
		0xffffff		// W
	};

	private final TextPaint[] paints;
	
	public TerminalBlitter(int columns, int rows, int cellWidth, int cellHeight) {
		//Log.i("Spaaace", "TerminalBlitter constructor: " + columns + "x" + rows + " (" + cellWidth + "x" + cellHeight + ")");
		this.columns = columns;
		this.rows = rows;
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;

		paints = new TextPaint[colors.length];

		setupPaints();
	}

	void setupPaints() {
		for(int i = 0; i < colors.length; i++) {
            TextPaint colorTextPaint = new TextPaint();
            colorTextPaint.setStyle(Paint.Style.FILL);
            colorTextPaint.setAntiAlias(true);
            colorTextPaint.setTypeface(Typeface.DEFAULT);
            colorTextPaint.setTextSize(Spaaace.CELL_HEIGHT);
            colorTextPaint.setColor(colors[i]);
            colorTextPaint.setAlpha(255);
            colorTextPaint.setTextAlign(Paint.Align.CENTER);
			paints[i] = colorTextPaint;
		}
	}

	public void setChar(Canvas canvas, int column, int row, char c, char color) {
		int n;
		String ca = Character.toString(c);
		
		switch (color) {
			case 'r': n = 0; break; case 'R': n = 7; break;
			case 'g': n = 1; break; case 'G': n = 8; break;
			case 'y': n = 2; break; case 'Y': n = 9; break;
			case 'b': n = 3; break; case 'B': n = 10; break;
			case 'm': n = 4; break; case 'M': n = 11; break;
			case 'c': n = 5; break; case 'C': n = 12; break;
			case 'w': n = 6; break; case 'W': n = 13; break;
			default: n = 6;
		}
		 
		int x = column * cellWidth;
		int y = row * cellHeight;

		if (canvas != null) {
			canvas.drawText(ca, x, y, paints[n]);
		}
	}
}
