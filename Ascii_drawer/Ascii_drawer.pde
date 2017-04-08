int back = 0;
int cellHeight = 32;
int cellWidth = 20;
int rows;
int columns;
int screenWidth = 500;
char[][] characters;
char drawingCharacter = '█';
ArrayList<Character> pendingCharacters;
boolean waitingForAltCode = false;
String altBuffer = "";
int[] selectStartPos = new int[2];
int[] selectCurrentPos = new int[2];
boolean isSelecting = false;
boolean showGrid = true;
void setup() {
  size(500, 500);
  
  rows = height/cellHeight;
  columns = width/cellWidth;
  
  characters = new char[height][width];
 
  pendingCharacters = new ArrayList();
  
  PFont font;
  font = createFont("Courier New", cellHeight);
  textFont(font);
}
 
void draw() {
  
  background(back);
  
  fill(255);
 
  
/*  for(int x = 0; x < selectCurrentPos[0]; x++) {
    for(int y = 0; y < selectCurrentPos[1]; y++) {
      int realX = (selectStartPos[0]+x)*cellWidth;
      int realY = (selectStartPos[1]+y)*cellHeight;
      rect(realX, realY, realX+cellWidth, realY+cellHeight); //<>//
    }
  }*/
  
  if(isSelecting) {
    int selectionStartX = selectStartPos[0]*cellWidth;
    int selectionStartY = selectStartPos[1]*cellHeight;
    
    int selectionBoxWidth = (selectCurrentPos[0]*cellWidth)-selectionStartX;
    int selectionBoxHeight = (selectCurrentPos[1]*cellHeight)-selectionStartY;
    
    rect(selectionStartX, selectionStartY, selectionBoxWidth, selectionBoxHeight);
  }
  
  //print(selectStartPos[0]+" "+selectStartPos[1]);
  
/*  for(int y = 0; y < characters.length; y++) {
    char[] line = characters[y];
    for(int x = 0; x < line.length; x++) {
      text(line[x], y*cellWidth, x*cellHeight);
    }
  }*/
  
    
  stroke(64);
 
  if(showGrid) {
    for (int y = 0; y < rows; y++) {
      line(0,y*cellHeight,width,y*cellHeight);
    }
    
    for (int x = 0; x < columns; x++) {
      line(x*cellWidth,0,x*cellWidth,height);
    }
  }
  
  stroke(0,0,0,0);
  
  for (Character pending : pendingCharacters) {
    boolean invert = isSelecting && pending.x >= selectStartPos[0] && pending.y >= selectStartPos[1] && pending.x < selectCurrentPos[0] && pending.y <= selectCurrentPos[1];
    if(invert) {
      fill(0);
    }
    text(pending.value, pending.x*cellWidth, pending.y*cellHeight);
    if(invert) {
      fill(255);
    }
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
    updateSelection();
  } else {
    
  }
  
}

void mouseReleased() {
  if (mouseButton == RIGHT) {
    finishSelecting();
  }
}

void mouseInput() { 
  touchCharacter(false);
}

void startSelection() {
  if(isSelecting) {
    return;
  }
  isSelecting = true;
  
  selectStartPos[0] = (mouseX/cellWidth);
  selectStartPos[1] = (mouseY/cellHeight);
  
  selectCurrentPos = selectStartPos.clone();
}

void updateSelection() {  
  selectCurrentPos[0] = Math.max((mouseX/cellWidth)+1, selectStartPos[0]+1);
  selectCurrentPos[1] = Math.max((mouseY/cellHeight)+1, selectStartPos[1]+1);
  
}

void finishSelecting() {
  isSelecting = false;
  print("\n");
  String outputText = "";
  char[][] selectionBox = new char[Math.abs(selectCurrentPos[0]-selectStartPos[0])][Math.abs(selectCurrentPos[1]-selectStartPos[1])];
  for(int y = selectStartPos[1]; y <= selectCurrentPos[1]; y++) {
    outputText += "\"";
    for(int x = selectStartPos[0]; x < selectCurrentPos[0]; x++) {
      outputText += characters[x][y] == '\0' ? ' ' : characters[x][y];
    }
    outputText += "\",\n";
  }
  outputText = outputText.replace("\\", "\\\\");
  print(outputText);
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
        drawingCharacter = (char)(int)(Integer.valueOf(altBuffer));
        print("character is "+"█".indexOf(0)+"\n");
      }
      altBuffer = "";
      waitingForAltCode = false;
    }
    return;
  }
  
  switch(key) {
    case 'q':
      showGrid = !showGrid;
      return;
    case 'w':
      pendingCharacters.clear();
      characters = new char[height][width];
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