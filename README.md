# COMP2042 – Coursework README

## GitHub Repository
https://github.com/Vicki03/CW2025

---

## Compilation Instructions
1. Clone or download the project from the GitHub repository.  
2. Open the project in IntelliJ IDEA (or any JavaFX-supported IDE).  
3. **If Maven does not load automatically**, manually import it:  
   - Right-click **`pom.xml` → “Add as Maven Project”**  
   - Wait for IntelliJ to download all dependencies.  
   - This step may be needed if IntelliJ does not auto-detect the Maven configuration.  
4. Ensure the following settings:  
    - **JDK Version:** 23  
    - **JavaFX SDK:** correctly linked under Run Configuration using:  
      ```
      --module-path "path/to/javafx-sdk-25/lib" --add-modules javafx.controls,javafx.fxml
      ```  
    - Maven dependencies are already defined in the `pom.xml` file.  
5. Run the program using the Main class:  
   `com.comp2042.Main`

---

## Implemented and Working Properly
- Pause and Resume Feature: Allows the game to be paused and resumed using a button.
- Replay Function: Enables restarting the game after it ends.
- Score Display: Displays and updates the player’s score during gameplay.
- Game Over Screen: Shows the game over panel and disables game input.
- Hard Drop (Space Bar): Allows users to instantly drop the brick to the bottom when spacebar is pressed.
- Hold Button and Hold Panel (C): Allows user to swap current brick with next brick once.
- Preview Panel: Allows user to view the next spawn brick.
- Level Service: Difficulty level will increase by 1 when a certain score is reached.

---

## Features Not Implemented
• **High Score Saving System** – This feature was planned to store the player's highest scores across multiple sessions using either a text file or a simple database. It was not implemented due to time constraints and because persistent storage was not a core requirement of the coursework.

• **Multiple Game Modes** – Additional modes such as Marathon, Endless, Sprint, or Time Attack were considered to expand replayability. These were not implemented because the focus was kept on completing and stabilising a single fully functional Tetris mode.

• **T-Spin and Combo Bonus System** – Advanced scoring mechanics such as T-Spins, back-to-back bonuses, and combo chains were not implemented. These rules require complex rotation detection and line-clear pattern analysis, which were beyond the intended scope and would significantly increase development complexity.

## New Java Classes

### **LevelService.java**
1. New utility class in `com.comp2042.tetris.model` that handles level progression and gravity speed scaling based on the player’s score.
2. Maps score thresholds to both level numbers and falling speed (gravity) in milliseconds between automatic drops.
3. Provides two main methods:
    - `gravityMsForScore(int score)` — returns the gravity interval based on the current score, with a minimum speed limit.
    - `levelForScore(int score)` — calculates the player’s level using score thresholds.
4. Ensures gameplay remains fast but manageable by preventing gravity from dropping below a defined minimum.
5. Used by `GameController` to automatically update the brick drop rate and level display whenever the score changes.

---

## Modified Java Classes

### **Main.java**
1. Moved the class to the package `com.comp2042.tetris.application`.
2. Added imports for `GameController` and `GuiController`.
3. Simplified FXML loading with  
   `FXMLLoader loader = new FXMLLoader(getClass().getResource("/gameLayout.fxml"));`.
4. Renamed controller variable to `guiController` and initialized it with  
   `new GameController(guiController);`.
5. Adjusted window size from **300×510** to **350×510**.
6. Added `primaryStage.setResizable(false);` to lock the window size.
7. Added brief Javadoc comments for the class and its methods.
8. Removed unused imports and cleaned up code formatting.

---

### **GameController.java**
1. Moved the class to `com.comp2042.tetris.controller`.
2. Replaced `SimpleBoard` with `GameBoard` for the main game board instance.
3. Added `LevelService` integration to control gravity and levels dynamically based on score.
4. Introduced `currentLevel` field and score listener to adjust gravity and show level-up notifications.
5. Added `showNext(board.getNextViewData())` to display the next block preview in the UI.
6. Implemented hard drop functionality in `onHardDropEvent()` — instantly drops the brick, awards drop bonuses, merges, clears rows, and spawns the next piece.
7. Implemented hold feature in `onHoldEvent()` — allows swapping the current and held bricks, updates both previews, and checks for immediate collision or game over.
8. Updated `onDownEvent()` to refresh next preview after spawning a new brick.
9. Enhanced `createNewGame()` to reset level, gravity, score, and previews when restarting the game.
10. Added detailed Javadoc comments explaining controller responsibilities, method behavior, and event handling.
11. Cleaned up structure, improved readability, and standardized method documentation throughout the file.

---

### **GuiController.java**
1. Moved the class to `com.comp2042.tetris.controller`.
2. Added imports for `EventSource`, `EventType`, `InputEventListener`, `MoveEvent`, and various model/view classes.
3. Added new FXML bindings:  
   `ghostPanel`, `replayButton`, `pauseButton`, `scoreLabel`, `levelLabel`, `nextPanel`, `holdPanel`, `gameOverOverlay`.
4. Added ghost block rendering using `ghostPanel`, `ghostRectangles`, and `renderGhost(ViewData)` with opacity adjustments.
5. Added next block preview feature via `nextPanel`, `nextRectangles`, `initPreviewGrid()`, and `showNext(ViewData)`.
6. Added hold piece preview via `holdPanel`, `holdRectangles`, and `showHeld(ViewData)`.
7. Implemented hard drop (SPACE) and hold (C) keyboard events with `onHardDropEvent()` and `onHoldEvent()`.
8. Integrated `LevelService` UI updates through `showLevel(int)` and `showLevelUpNotification(int)`.
9. Added pause/resume and game-over overlay controls with animation and replay button logic.
10. Improved initialization in `initGameView()` to create layers for the board, ghost, next, and held pieces.
11. Added helper methods like `paintBrickLayer()`, `updatePanelsPosition()`, and `computeGhostY()` for accurate rendering.
12. Enhanced UI polish: custom font loading, reflection effect, and fade-in transitions.
13. Added detailed Javadoc comments for all new methods and improved readability and layout consistency.

---

### **EventType.java**
1. Defined an enum for all input/timer actions the game reacts to.
2. Included move actions: **LEFT, RIGHT, DOWN, ROTATE**.
3. Added advanced actions: **HARD_DROP** and **HOLD** for instant drop and piece swap.
4. Documented each constant with brief Javadoc so usage is clear in controller/UI code.
5. Kept formatting consistent with the event system.

---

### **InputEventListener.java**
1. Added new interface methods to support extended input handling:
    - `onHardDropEvent(MoveEvent event)`
    - `onHoldEvent(MoveEvent event)`
2. Updated existing method signatures to align with the new `EventType` enum.
3. Added concise Javadoc comments explaining each callback.
4. Ensured consistent formatting across all listener methods.

---

### **MoveEvent.java**
1. Defines an event object carrying the action type (`EventType`) and origin (`EventSource`).
2. Provides a constructor to set both fields when creating an event.
3. Exposes getters like `getEventType()` and `getEventSource()`.
4. Includes brief Javadoc explaining usage.
5. Organized under `com.comp2042.tetris.events`.

---

### **GameBoard.java**
1. Replaced `SimpleBoard` as the main board implementation.
2. Added fields: `currentBrick`, `heldBrick`, `holdUsed`, and `nextViewData`.
3. Updated `createNewBrick()` to spawn correctly, reset hold state, and load next preview.
4. Implemented `holdCurrentBrick()` with collision checks using `MatrixOperations.intersect(...)`.
5. Added `getHeldBrickViewData()` and `getNextBrickViewData()` to expose previews.
6. Ensured `newGame()` initializes hold and preview states properly.
7. Integrated consistent collision handling and safe spawn offsets.
8. Added clear Javadoc comments.

---

### **Score.java**
1. Added Javadoc comments for class and methods (`add()`, `reset()`).
2. Ensured scoring uses a JavaFX `IntegerProperty` for live UI updates.
3. Cleaned the structure and removed unnecessary code.
4. Integrated with `LevelService` for dynamic gravity and level changes.
5. Improved readability.

---

### **GameOverPanel.java**
1. Added detailed Javadoc comments.
2. Improved layout — centered elements and consistent styling.
3. Added clear methods for showing/hiding overlay.
4. Integrated with controller for replay actions.
5. Cleaned redundant code.

---

### **NotificationPanel.java**
1. Moved to `com.comp2042.tetris.view`.
2. Added Javadoc comments describing purpose and animations.
3. Replaced old EventHandler with lambda expression.
4. Standardized fade/translate durations.
5. Documented glow effect and styling.
6. Cleaned imports and formatting.

---

### **gameLayout.fxml**
1. Added `<GridPane fx:id="ghostPanel">` before `brickPanel`.
2. Added `<Label fx:id="levelLabel">`.
3. Added next preview: `<GridPane fx:id="nextPanel">`.
4. Added hold preview: `<GridPane fx:id="holdPanel">`.
5. Added `StackPane fx:id="gameOverOverlay"`.
6. Added new FXML controls (pauseButton, replayButton).
7. Fixed malformed XML tags.
8. Improved side panel layout.
9. Ensured correct z-order (ghost → active brick → overlays).
10. General cleanup of IDs and attributes.

---

## Unexpected Problems
- **FXML Parsing Error (“Insets is not a valid type”)** – Occurred due to a missing import. Fixed by adding `<?import javafx.geometry.Insets?>`.
- **Replay Button Not Clickable** – Fixed by adding `pickOnBounds="false"` and adjusting overlay event handling.
- **Score Label Not Displaying Properly** – Fixed binding, adjusted color/alignment, added spacing.
- **Ghost Block Rendering Issues** – Fixed panel ordering and improved Y-offset calculation.
- **Hold Feature Collision Handling** – Fixed by adding collision checks and resetting spawn offset.
- **Next Block Preview Misalignment** – Fixed centering logic and preview scaling.
- **Window Resizing Issue** – Fixed by locking window size with `setResizable(false)`.
- **Initial Brick Spawn Causing Instant Game Over** – Fixed spawn Y-offset in `GameBoard`.
