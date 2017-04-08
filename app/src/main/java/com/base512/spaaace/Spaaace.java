package com.base512.spaaace;

import android.graphics.Canvas;

import java.util.Random;

import static com.base512.spaaace.Entity.EntityCallback;

public class Spaaace {
	
	final static int CELL_WIDTH = 16;
	final static int CELL_HEIGHT = 32;

	final static int ENTITY_TYPE_STAR = 2;
	final static int ENTITY_TYPE_PLANET = 3;
	
	Animation anim;
	int columns;
	int rows;
	Random random;
	
	public interface Renderer {
		void putChar(Canvas canvas, int row, int column, char c, char color);
	}
	
	Renderer renderer;
	
	public Spaaace(int columns, int rows) {
		this.columns = columns;
		this.rows = rows;
		
		random = new Random();
		
		anim = new Animation(columns, rows);
		//anim.halfDelay(1);

		addAllDatums();
		addAllTripus();
		addAllPlanets();
        addAllStars();
	}
	
	public void setRenderer(Renderer renderer) {
		this.renderer = renderer;
	}
	
	public Entity findEntityAt(int x, int y, int type) {
		final Entity[] planets = anim.getEntitiesOfType(ENTITY_TYPE_PLANET);
		Entity target = null;
		int depth = 999;
		
		for (Entity e : planets) {
			if ((e.fx < x && e.fx + e.width <= x) || e.fx >= x)
				continue;
			if ((e.fy < y && e.fy + e.height <= y) || e.fy >= y)
				continue;
			
			if (e.depth < depth) {
				depth = e.depth;
				target = e;
			}
		}
		
		return target;
	}

	public void draw(Canvas canvas) {
		anim.animate();

        if(random.nextFloat() > 0.99) {
            addPlanet();
        }

        if(random.nextFloat() > 0.98) {
			addDatum();
		}

		if(random.nextFloat() > 0.99) {
			addTripus();
		}
		char[] buffer = anim.screen.text;
		char[] cbuffer = anim.screen.color;
		char c;
		for (int row = 0; row < rows; row++) {
			int r = row * columns;
			for (int col = 0; col < columns; col++) {
				c = buffer[r + col];
				if (c != ' ')
					renderer.putChar(canvas, col, row, c, cbuffer[r + col]);
			}
		}		
	}
	
	private void addAllPlanets() {
		final int screenSize = rows * columns;
		final int planetCount = 1;

        int maxWidth = 0;
        int minWidth = columns - 1;

        int maxHeight = 0;
        int minHeight = rows - 1;

		for (int i = 0; i < planetCount; i++) {
            int x = random.nextInt(minWidth - maxWidth) + maxWidth;
            int y = random.nextInt(minHeight - maxHeight) + maxHeight;
            addPlanet(x, y);
		}
	}

	private void addAllStars() {
        final int screenSize = rows * columns;
        final int starCount = screenSize / 20;

        int maxWidth = 0;
        int minWidth = columns - 1;

        int maxHeight = 0;
        int minHeight = rows - 1;

        for (int i = 0; i < starCount; i++) {
            int x = random.nextInt(minWidth - maxWidth) + maxWidth;
            int y = random.nextInt(minHeight - maxHeight) + maxHeight;
            addStar(x, y, Math.random() > 0.5);
        }
    }

    private void addStar() {
        int maxWidth = 0;
        int minWidth = columns - 1;

        int x = random.nextInt(minWidth - maxWidth) + maxWidth;;
        int y = 0;

        addStar(x, y, false);
    }

	private void addStar(int x, int y, boolean isBackground) {
		final String[][] shape = {
                {"+"}, {"*"}, {"●"}
		};

        final String[][] shapeSpeed2 = {
                {
                        "*",
                        "*"
                },
                {
                        "*",
                        "●"
                }
        };

        final String[][] shapeSpeed3 = {
                {
                        "*",
                        "*",
                        "*",
                        "*"
                },
                {
                        "*",
                        "*",
                        "*",
                        "●"
                }
        };

		final String[][] shapeSpeed4 = {
				{
						"*",
						"*",
						"*",
						"*",
						"*",
						"*",
						"*"
				},
				{
						"*",
						"*",
						"*",
						"*",
						"*",
						"*",
						"●"
				}
		};

        int depth = random.nextInt(rows);
        float speed = isBackground ? 0.00f : random.nextFloat() * (random.nextFloat() > 0.70f ? random.nextFloat() > 0.70f ? 50 : 20 : 3);

        String[][] starShape = speed < 1.5f ?  shape : speed < 3 ? shapeSpeed2 : speed < 15 ? shapeSpeed3 : shapeSpeed4;

		final Entity entity = new Entity("star", starShape, 0, 0, depth);
		entity.type = ENTITY_TYPE_STAR;
		entity.callbackArguments[1] = speed;
		entity.callbackArguments[3] = random.nextFloat();
		entity.dieOffscreen = true;
		entity.physical = true;
		entity.defaultColor = random.nextFloat() > 0.75 ? isBackground ? 'y' : 'Y' : isBackground ? 'w' : 'W';
        entity.deathCallback = starDeathCallback;

        entity.fy = y;
        entity.fx = x;
		
		anim.addEntity(entity);		
	}

	private void addPlanet() {
        int maxWidth = 0;
        int minWidth = columns - 1;

        int x = random.nextInt(minWidth - maxWidth) + maxWidth;;
        int y = 0;

        addPlanet(x, y);
    }
	
	private void addPlanet(int x, int y) {
		final String planetImage[][] = {
			{
                    "???██████????",
                    "?██████████?",
                    "████████████",
                    "████████████",
                    "?██████████?",
                    "???██████????",
            },
			{
                    "   111211    ",
                    " 2221122111 ",
                    "112222222221",
                    "221211112222",
                    " 1112211221 ",
                    "   112112    ",
			},
            {
                    " ███████ ",
                    "█████████",
                    "█████████",
                    " ███████ "
            },
            {
                    " 1112211 ",
                    "211122211",
                    "221222112",
                    " 2111211 "
            }
		};
		
        // 1: land
        // 2: sea

		int planetNum = random.nextInt(planetImage.length / 2);
		int planetIndex = planetNum * 2;
		float speed = random.nextFloat() * 2 + 0.25F;
		int depth = random.nextInt(rows);
		String[] colorMask = planetImage[planetIndex + 1];

        randColorMono(colorMask);
		
		final Entity planetObject = new Entity("planet", planetImage[planetIndex], colorMask, 0, 0, depth);
		planetObject.type = ENTITY_TYPE_PLANET;
		planetObject.callback = tripusCallback;
		planetObject.autoTrans = true;
		planetObject.dieOffscreen = true;
		planetObject.deathCallback = planetDeathCallback;
		planetObject.callbackArguments[1] = speed;
		planetObject.physical = true;
        planetObject.defaultColor = 'c';
		planetObject.collHandler = shipCollision;

        planetObject.fy = y;
        planetObject.fx = x;
		
		anim.addEntity(planetObject);
	}

	EntityCallback planetCallback = new EntityCallback() {
		public void run(Entity entity) {
			entity.move();
		}
	};
	
	EntityCallback planetDeathCallback = new EntityCallback() {
		public void run(Entity entity) {
			//addPlanet();
		}
	};

	EntityCallback starDeathCallback = new EntityCallback() {
        @Override
        public void run(Entity entity) {
            addStar();
        }
    };
	
	EntityCallback planetCollision = new EntityCallback() {
		public void run(Entity entity) {
			/*for (Entity e : entity.collisions) {
				if (e.type == ENTITY_TYPE_PLANET) {
					addExplosion(e.fx, e.fy, e.depth);
					entity.kill();
					break;
				}
			}*/
		}
	};

	private void addAllTripus() {
		final int screenSize = rows * columns;
		final double TripusCount = 1;

		int maxWidth = 0;
		int minWidth = columns - 1;

		int maxHeight = 0;
		int minHeight = rows - 1;

		for (int i = 0; i < TripusCount; i++) {
			int x = random.nextInt(minWidth - maxWidth) + maxWidth;
			int y = random.nextInt(minHeight - maxHeight) + maxHeight;
			addTripus(x, y);
		}
	}

	private void addTripus() {
		int maxWidth = 0;
		int minWidth = columns - 1;

		int x = random.nextInt(minWidth - maxWidth) + maxWidth;;
		int y = 0;

		addTripus(x, y);
	}

	private void addTripus(int x, int y) {
		final String TripusImage[][] = {
				{
						"|    |    | ",
						"\\\\  |||  // ",
						" \\\\ ||| //  ",
						"  \\\\_|_//   ",
						"   \\   /    ",
						"    \\_/     ",

				},
				{
						"r    r    r ",
						"ww  yyy  ww ",
						" ww ryr ww  ",
						"  wwwywww   ",
						"   w   w    ",
						"    www     ",

				},
		};

		// 1: land
		// 2: sea

		//randColorMono(colorMask);

		int TripusNum = random.nextInt(TripusImage.length / 2);
		int datumIndex = TripusNum * 2;
		float speed = random.nextFloat() * 2 + 0.25F;
		int depth = random.nextInt(rows);
		String[] colorMask = TripusImage[datumIndex + 1];

		final Entity tripusObject = new Entity("tripus", TripusImage[datumIndex], colorMask, 0, 0, depth);
		tripusObject.type = ENTITY_TYPE_PLANET;
		tripusObject.callback = tripusCallback;
		tripusObject.autoTrans = true;
		tripusObject.dieOffscreen = true;
		tripusObject.deathCallback = tripusDeathCallback;
		tripusObject.callbackArguments[1] = speed;
		tripusObject.physical = true;
		tripusObject.defaultColor = 'r';
		tripusObject.collHandler = shipCollision;

		tripusObject.fy = y;
		tripusObject.fx = x;

		anim.addEntity(tripusObject);
	}

	private void addAllDatums() {
		final int screenSize = rows * columns;
		final double datumCount = 1;

		int maxWidth = 0;
		int minWidth = columns - 1;

		int maxHeight = 0;
		int minHeight = rows - 1;

		for (int i = 0; i < datumCount; i++) {
			int x = random.nextInt(minWidth - maxWidth) + maxWidth;
			int y = random.nextInt(minHeight - maxHeight) + maxHeight;
			addDatum(x, y);
		}
	}

	private void addDatum() {
		int maxWidth = 0;
		int minWidth = columns - 1;

		int maxHeight = 0;
		int minHeight = rows - 1;

		int x = random.nextInt(minWidth - maxWidth) + maxWidth;;
		int y = minHeight;

		addDatum(x, y);
	}

	private void addDatum(int x, int y) {
		final String datumImage[][] = {
				{
						"?/\\?",
						"/\\/\\",
						"\\||/",
				},
				{
						" gg ",
						"gggg",
						"gyyg",
				},
		};

		// 1: land
		// 2: sea

		//randColorMono(colorMask);

		int datumNum = random.nextInt(datumImage.length / 2);
		int datumIndex = datumNum * 2;
		float speed = random.nextFloat() + 0.25F;
		int depth = random.nextInt(rows);
		String[] colorMask = datumImage[datumIndex + 1];

		final Entity datumObject = new Entity("datum", datumImage[datumIndex], colorMask, 0, 0, depth);
		datumObject.type = ENTITY_TYPE_PLANET;
		datumObject.callback = tripusCallback;
		datumObject.autoTrans = true;
		datumObject.dieOffscreen = true;
		datumObject.deathCallback = tripusDeathCallback;
		datumObject.callbackArguments[1] = -speed;
		datumObject.physical = true;
		datumObject.defaultColor = 'c';
		datumObject.collHandler = shipCollision;

		datumObject.fy = y;
		datumObject.fx = x;

		anim.addEntity(datumObject);
	}

	EntityCallback tripusCallback = new EntityCallback() {
		public void run(Entity entity) {
			entity.move();
		}
	};

	EntityCallback tripusDeathCallback = new EntityCallback() {
		public void run(Entity entity) {
			//addDatum();
		}
	};

	EntityCallback shipCollision = new EntityCallback() {
		public void run(Entity entity) {
			/*for (Entity e : entity.collisions) {
				if (e.type == ENTITY_TYPE_PLANET) {
					addExplosion(e.fx, e.fy, e.depth);
					entity.kill();
					break;
				}
			}*/
		}
	};

	private void addExplosion(float fx, float fy, int depth) {
		final String[][] explosionImage = {
			{
                    "   ██\\███    ",
                    " ██░░\\░░███ ",
                    "░████{░██░██",
                    "███░░░|░███░░█",
                    " ██░/░░████ ",
                    "   ███░██     "
			},
			{
                    "   ██\\░░█    ",
                    " ██\\█\\░}███ ",
                    "██{█\\{█░░}░█",
                    "░░░██|██]░░]█",
                    " ███/█\\░}██ ",
                    "   /█░░\\█     "
			},
			{
                    "   ░░\\░░░    ",
                    " (░░\\░\\░}( } ",
                    "( )░\\{░( }( )",
                    "{░░(░(  )( ]░",
                    " █{░/█\\░}░) ",
                    "   /( )\\█     "
			},
            {
                    "   ( )(   )    ",
                    " (   )(  )(  )",
                    "(        }( )",
                    "{   )(  )(   ]",
                    "  {      }( ) ",
                    "   (      )   "
            },
            {
                    "               ",
                    "      (      )",
                    "(         (  ",
                    "    )         ",
                    "              ",
                    "  (           "
            },
            {
                    "              ",
                    "              ",
                    "              ",
                    "              ",
                    "              ",
                    "              "
            }
		};

		final Entity entity = new Entity("explosion", explosionImage, (int)fx - 4, (int)fy - 2, depth - 2);
		entity.defaultColor = 'R';
		entity.callbackArguments[3] = 0.25F;
		entity.transparent = ' ';
		entity.dieFrame = 50;
		anim.addEntity(entity);		
	}

    private void randColorMono(String[] mask) {
        final char[] colors = { 'c','r','y','b','g','m' };
        final char[] keys = { '1', '2' };

        char colorShade = colors[random.nextInt(colors.length)];
        char colorShadeBright = Character.toUpperCase(colorShade);
        char[] newColors = new char[] {
                colorShade,
                colorShadeBright
        };

        for (int i = 0; i < mask.length; i++) {
            mask[i] = mask[i].replace('4', 'Y');
            mask[i] = mask[i].replace('5', 'Y');
            for (int j = 0; j < keys.length; j++) {
                mask[i] = mask[i].replace(keys[j], newColors[j]);
            }
        }
    }

	private void randColor(String[] mask) {
		final char[] colors = { 'c','C','r','R','y','Y','b','B','g','G','m','M' };
		final char[] keys = { '1', '2', '3', '5', '6', '7' };
		
		char[] newColors = new char[keys.length];
		for (int i = 0; i < newColors.length; i++)
			newColors[i] = colors[random.nextInt(colors.length)];
		
		// Set eye white, rest as random color
		for (int i = 0; i < mask.length; i++) {
			mask[i] = mask[i].replace('4', 'W');
			for (int j = 0; j < keys.length; j++) {
				mask[i] = mask[i].replace(keys[j], newColors[j]);
			}
		}
	}
}
