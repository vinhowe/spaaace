package com.base512.spaaace;

import android.graphics.Canvas;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

public class SpaaaceWallpaperService extends WallpaperService {

	public static void reset() {

	}

	@Override
	public Engine onCreateEngine() {
		return new SpaaaceWallpaperEngine();
	}

	public class SpaaaceWallpaperEngine extends Engine {
        private final long serialVersionUID = 1L;
        private int width, height;
        private int columns, rows;
        private int cellWidth, cellHeight;
        private long previousTime;
        private Spaaace spaaace;
        private TerminalBlitter blitter;
        private boolean running;

        private boolean visible = true;

		private Handler handler = new Handler();

        Spaaace.Renderer renderer = new Spaaace.Renderer() {
            public void putChar(Canvas canvas, int column, int row, char c, char color) {
                blitter.setChar(canvas, column, row, c, color);
            }
        };

		/**
		 * The main runnable that is given to the Handler to draw the animation
		 */
		private Runnable drawRunnable = new Runnable() {
			public void run() {
				draw();
			}
		};

		/** Draws all of the bit sequences on the screen */
		private void draw() {
            if(visible) {
                long currentTime = System.currentTimeMillis();
                long elapsed = currentTime - previousTime;
                if (elapsed > 150) {
/*                    // We can't have just one reset flag, because then the preview
                    // would consume that flag and the actual wallpaper wouldn't be
                    // reset
                    if (previewReset && isPreview()) {
                        previewReset = false;
                        resetSequences();
                    } else if (reset && !isPreview()) {
                        reset = false;
                        resetSequences();
                    }*/
                    SurfaceHolder holder = getSurfaceHolder();
                    Canvas c = holder.lockCanvas();
                    try {
                        if (c != null) {
                            c.drawARGB(255, 0, 1, 10);

                            if (blitter != null && spaaace != null) {
                                spaaace.draw(c);
                            }

                        }
                    } finally {
                        if (c != null) {
                            holder.unlockCanvasAndPost(c);
                        }
                    }
                    previousTime = currentTime;

                }
                // Remove the runnable, and only schedule the next run if
                // visible
                handler.removeCallbacks(drawRunnable);

                handler.post(drawRunnable);
            } else {
                pause();
            }
		}

		private void resetCharacters() {
            cellWidth = Spaaace.CELL_WIDTH;
            cellHeight = Spaaace.CELL_HEIGHT;
            columns = width / cellWidth;
            rows = height / cellHeight;

            spaaace = new Spaaace(columns, rows);
            spaaace.setRenderer(renderer);
            blitter = new TerminalBlitter(columns, rows, cellWidth, cellHeight);

            running = true;
        }

		private void pause() {
			//handler.removeCallbacks(drawRunnable);
		}

		private void start() {
			handler.post(drawRunnable);
		}

		private void stop() {
			handler.removeCallbacks(drawRunnable);
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			super.onSurfaceCreated(holder);
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			pause();
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);
			this.width = width;
            this.height = height;

			// Initialize characters
			resetCharacters();
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			super.onVisibilityChanged(visible);
			if (visible) {
				start();
			} else {
				pause();
			}
			this.visible = visible;
		}
	}
}
