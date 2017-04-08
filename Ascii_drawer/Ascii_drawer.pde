int back = 0;
int cellHeight = 32;
int cellWidth = 20;
int rows;
int columns;
int screenWidth = 500;
char[][] characters;
char drawingCharacter = '.';
ArrayList<Character> pendingCharacters;
boolean waitingForAltCode = false;
String altBuffer = "";
 
void setup() {
  size(1920, 1080);
  
  rows = height/cellHeight;
  columns = width/cellWidth;
  
  characters = new char[height][width];
 
  pendingCharacters = new ArrayList();
  
  PFont font;
  font = createFont("Monospaced", cellHeight);
  textFont(font);
}
 
void draw() {
  
  background(back);
  
/*  for(int y = 0; y < characters.length; y++) {
    char[] line = characters[y];
    for(int x = 0; x < line.length; x++) {
      text(line[x], y*cellWidth, x*cellHeight);
    }
  }*/
  
    fill(255);
  stroke(64);
 
  
  for (int y = 0; y < rows; y++) {
    line(0,y*cellHeight,width,y*cellHeight);
  }
  
  for (int x = 0; x < columns; x++) {
    line(x*cellWidth,0,x*cellWidth,height);
  }
  
  stroke(0,0,0,0);
  
  for (Character pending : pendingCharacters) {
    text(pending.value, pending.x*cellWidth, pending.y*cellHeight);
  }
  //pendingCharacters.clear();
  //text(mouseX, 50, 50);
  
}

void drawLine(int x1, int y1, int x2, int y2) {
  
}

void mousePressed() {
  if (mouseButton == LEFT) {
    mouseInput();
  } else if (mouseButton == RIGHT) {
    startSelection();
  } else {
  }
}

void mouseDragged() {
    if (mouseButton == LEFT) {
    mouseInput();
  } else if (mouseButton == RIGHT) {
    //saveIt
  } else {
  }
  
}


void mouseInput() { 
  touchCharacter(false);
}

void startSelection() {
  
}

void touchCharacter(boolean toggle) {
  int cellX = (mouseX/cellWidth);
  int cellY = (mouseY/cellHeight)+1;
  
  cellX = Math.max(Math.min(cellX, columns), 0);
  cellY = Math.max(Math.min(cellY, rows), 0);
  
  Character newCharacter = new Character(drawingCharacter, cellX, cellY);
  
  for(int i = 0; i < pendingCharacters.size(); i++) {
    Character character = pendingCharacters.get(i);
    if(character.samePosition(newCharacter)) {
      if(!character.equals(newCharacter)) {
        removeCharacter(cellX, cellY, i);
        setCharacter(newCharacter, cellX, cellY);
        return;
      }
      if(toggle) {
        removeCharacter(cellX, cellY, i);
      }
      
      return;
    }
  }
  setCharacter(newCharacter, cellX, cellY);
  

}

void removeCharacter(int x, int y, int i) {
  pendingCharacters.remove(i);
  characters[x][y] = 0;
}

void setCharacter(Character character, int x, int y) {
  characters[x][y] = character.value;
  pendingCharacters.add(character);
}

void keyPressed() {
  if(waitingForAltCode) {    
    
    
    if (key == CODED && keyCode == ENTER) {
      if (!altBuffer.equals("")) {
        drawingCharacter = (char)(int)(Integer.valueOf(altBuffer));
      }
      altBuffer = "";
      waitingForAltCode = false;
    }
    String keyString = String.valueOf(key);
    int number;
    
    try {
      number = Integer.valueOf(keyString);
      altBuffer += keyString;
    } catch (NumberFormatException e) {
      if (!altBuffer.equals("")) {
        drawingCharacter = (char)(int)(Integer.valueOf(altBuffer)%255);
        print("character is "+drawingCharacter);
      }
      altBuffer = "";
      waitingForAltCode = false;
    }
    return;
  }
  
    int keyIndex = -1;
  if (key >= '!' && key <= '~') { 
    keyIndex = key - 'A';
  } else if (key >= 128 && key <= 254) {
    keyIndex = key - 'a';
  }
  if (keyIndex == -1) {
    if(keyCode == CONTROL) {
      print("waiting for alt code\n");
      waitingForAltCode = true;
    }
    drawingCharacter = ' ';
  } else {
    drawingCharacter = key;
  }
}