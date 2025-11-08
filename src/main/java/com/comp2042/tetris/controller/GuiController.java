package com.comp2042.tetris.controller;

import com.comp2042.tetris.events.EventSource;
import com.comp2042.tetris.events.EventType;
import com.comp2042.tetris.events.InputEventListener;
import com.comp2042.tetris.events.MoveEvent;
import com.comp2042.tetris.model.DownData;
import com.comp2042.tetris.model.ViewData;
import com.comp2042.tetris.view.NotificationPanel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.Label;

/**
 * Manages the JavaFX UI, translates user input into model actions via InputEventListener,
 * and renders board and active piece. Controller is UI-only; game logic lives in the model.
 */

//manages GUI, handles user input, updates the display of game board and current brick
//shows notifs for scoring, controls game over and new game states
public final class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;

    @FXML
    private GridPane gamePanel;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane brickPanel;

    //ghost layer (behind active brick)
    @FXML
    private GridPane ghostPanel;

    //added replay button
    @FXML
    private Button replayButton;

    //added pause button
    @FXML
    private Button pauseButton;

    //added scoreLabel
    @FXML
    private Label scoreLabel;

    //added levelLabel
    @FXML
    private Label levelLabel;

    //added nextPanel
    @FXML
    private GridPane nextPanel;

    //added holdPanel
    @FXML
    private GridPane holdPanel;

    //@FXML remove later
   // private Rectangle overlayRect;

    @FXML
    private StackPane gameOverOverlay;

    //stores rectangles representing game board
    /**Rectangle matrix for the game board display*/
    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    /** Rectangles that render the active falling brick. */
    private Rectangle[][] rectangles;

    /** Rectangle matrix for the ghost piece display */
    private Rectangle[][] ghostRectangles;

    //keep latest active brick viewdata for ghost computation
    //private ViewData currentBrickView;

    //controls automatic piece movement
    /** Automatic gravity (piece falls) timer. */
    private Timeline timeLine;

    /** UI state flags. */
    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    private Rectangle[][] nextRectangles;
    private Rectangle[][] holdRectangles;
    private static final int PREVIEW_CELL = 16;
    private static final int PREVIEW_SIZE = 4;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        URL fontUrl = getClass().getClassLoader().getResource("digital.ttf");
        if (fontUrl != null) {
            Font.loadFont(fontUrl.toExternalForm(), 38);
        } else {
            System.err.println("Font file 'digital.ttf' not found in resources folder.");
        }

        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(keyEvent -> {
            if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
                if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                    refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                    keyEvent.consume();
                }
                if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                    refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                    keyEvent.consume();
                }
                if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                    refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                    keyEvent.consume();
                }
                if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                    moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                    keyEvent.consume();
                }

                //hard drop when spacebar is pressed
                if(keyEvent.getCode() == KeyCode.SPACE){
                    DownData downData = eventListener.onHardDropEvent(new MoveEvent(EventType.HARD_DROP, EventSource.USER));
                    showScoreBonus(downData);
                    refreshBrick(downData.getViewData());
                    keyEvent.consume();
                }

                //hold/swap when c is pressed on the keyboard
                if (keyEvent.getCode() == KeyCode.C) {
                    refreshBrick(eventListener.onHoldEvent(new MoveEvent(EventType.HOLD, EventSource.USER)));
                    keyEvent.consume();
                }
            }
        });
        //hide replay button at the start
        replayButton.setVisible(false);
        //hide game over panel in the beginning
        //gameOverPanel.setVisible(false);
        gameOverOverlay.setVisible(false);
        gameOverOverlay.setManaged(false);
        gameOverOverlay.setViewOrder(-1);

        //redundant reflection effect
        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);
    }

    //initializes game board and current brick display
    public void initGameView(int[][] boardMatrix, ViewData brick) {
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
            }
        }

        //build ghost rectangles first (they will be behind active bricks)
        ghostRectangles = createBrickLayer(
                brick.getBrickData().length,
                brick.getBrickData()[0].length,
                ghostPanel,
                true);

        rectangles = createBrickLayer(
                brick.getBrickData().length,
                brick.getBrickData()[0].length,
                brickPanel,
                false);


        //position panels
        updatePanelsPosition(brick);


        setGravityMs(400); //default gravity speed

        //preview of next block
        if (nextPanel != null) nextRectangles = initPreviewGrid(nextPanel);
        //preview of held block
        if (holdPanel != null) holdRectangles = initPreviewGrid(holdPanel);
    }

    //returns a color based on integer value
    private Paint getFillColor(int i) {
        return switch (i) {
            case 0 -> Color.TRANSPARENT;
            case 1 -> Color.AQUA;
            case 2 -> Color.BLUEVIOLET;
            case 3 -> Color.DARKGREEN;
            case 4 -> Color.YELLOW;
            case 5 -> Color.RED;
            case 6 -> Color.BEIGE;
            case 7 -> Color.BURLYWOOD;
            default -> Color.WHITE;
        };
    }



    //update the brick's position and color in the UI
    private void refreshBrick(ViewData brick) {
        paintBrickLayer(rectangles, brick.getBrickData(), false);
        renderGhost(brick);
        updatePanelsPosition(brick);

    }

    // compute where the current brick will land and update ghostPanel layout
    private void renderGhost(ViewData brick) {
        paintBrickLayer(ghostRectangles, brick.getBrickData(), true);
    }

    // find maximum Y where brick can be placed without overlapping visible background
    private int computeGhostY(ViewData brick) {
        int[][] shape = brick.getBrickData();
        int x = brick.getxPosition();
        int y = brick.getyPosition();
        int maxRows = displayMatrix.length;

        int testY = y;
        outer:
        while (true) {
            int nextY = testY + 1;
            // check each cell in shape at row = nextY + i
            for (int i = 0; i < shape.length; i++) {
                for (int j = 0; j < shape[i].length; j++) {
                    if (shape[i][j] == 0) continue;
                    int boardRow = nextY + i;
                    int boardCol = x + j;
                    // collision with floor
                    if (boardRow >= maxRows) {
                        break outer;
                    }
                    // treat null displayMatrix entries as empty (above visible area)
                    Rectangle backgroundCell = displayMatrix[boardRow][boardCol];
                    if (backgroundCell != null && !Color.TRANSPARENT.equals(backgroundCell.getFill())) {
                        break outer;
                    }
                }
            }
            testY = nextY;
        }
        return testY;
    }

    //update the game board display
    public void refreshGameBackground(int[][] board) {
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    //set the color and rounded corners of a rectangle
    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
    }

    //handles the moving brick down
    private void moveDown(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onDownEvent(event);
            showScoreBonus(downData);
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    //sets event listener for game logic
    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    //I think this connects to the game score and then updates the score automatically in the ui
    public void bindScore(IntegerProperty integerProperty) {
        if (integerProperty == null || scoreLabel == null){
            return;
        }
        scoreLabel.textProperty().bind(integerProperty.asString("Score: %d"));
    }

    //change speed of the piece drops
    public void setGravityMs(int ms){
        if(timeLine != null){
            timeLine.stop();
        }
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(ms),
                _ -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));

        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    //displays the current level in the ui
    public void showLevel(int level) {
        if (levelLabel != null) {
            levelLabel.setText("Level: " + level);
        }
    }

    public void showLevelUpNotification(int newLevel) {
        NotificationPanel levelUpPanel = new NotificationPanel("LEVEL " + newLevel + "!");
        levelUpPanel.setLayoutY(-50);
        groupNotification.getChildren().add(levelUpPanel);
        levelUpPanel.showScore(groupNotification.getChildren());
    }

    public void showNext(ViewData next) {
        if (nextRectangles == null) return;
        if (next == null) { clearPreview(nextRectangles); return; }
        drawPreviewCentered(nextRectangles, next.getBrickData());
    }

    public void showHeld(ViewData held) {
        if (holdRectangles == null) return;
        if (held == null) { clearPreview(holdRectangles); return; }
        drawPreviewCentered(holdRectangles, held.getBrickData());
    }


    public void gameOver() {
        if (timeLine != null) timeLine.stop();

        isGameOver.set(true);
        pauseButton.setDisable(true);

        // Show overlay on top
        gameOverOverlay.setManaged(true);
        gameOverOverlay.setVisible(true);
        gameOverOverlay.toFront();

        // Optional fade-in for polish
        gameOverOverlay.setOpacity(0);
        javafx.animation.FadeTransition ft =
                new javafx.animation.FadeTransition(Duration.millis(220), gameOverOverlay);
        ft.setToValue(1.0);
        ft.play();

        replayButton.setVisible(true);
        replayButton.setDisable(false);
    }


    //implement replay button later
    public void newGame() {
        if (timeLine != null) timeLine.stop();

        // Hide overlay
        gameOverOverlay.setVisible(false);
        gameOverOverlay.setManaged(false);
        replayButton.setVisible(false);

        // Reset flags/UI
        isPause.set(false);
        isGameOver.set(false);
        pauseButton.setText("Pause");
        pauseButton.setDisable(false);
        groupNotification.getChildren().clear();

        // Reset game state and resume gravity
        eventListener.createNewGame();
        if (timeLine != null) timeLine.play();

        gamePanel.requestFocus();
    }



    //implement the pause game feature later
    public void pauseGame() {
        gamePanel.requestFocus();
        if(isGameOver.getValue()){
            return; //do nothing when game is over
        }
        if(isPause.getValue()){
            //resume
            timeLine.play();
            isPause.setValue(false);
            pauseButton.setText("Pause");
        } else{
            timeLine.pause();
            isPause.setValue(true);
            pauseButton.setText("Resume");
        }


    }

    /**
     * Creates a 2D Rectangle grid for either the active or ghost brick layer.
     */
    private Rectangle[][] createBrickLayer(int rows, int cols, GridPane target, boolean ghost) {
        Rectangle[][] layer = new Rectangle[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Rectangle rect = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rect.setMouseTransparent(true);
                if (ghost) {
                    rect.setFill(Color.TRANSPARENT);
                    rect.setOpacity(0.25);
                } else {
                    rect.setFill(Color.TRANSPARENT);
                }
                layer[r][c] = rect;
                target.add(rect, c, r);
            }
        }
        return layer;
    }

    /**
     * Updates a Rectangle grid's colors based on the brick data.
     */
    private void paintBrickLayer(Rectangle[][] layer, int[][] data, boolean ghost) {
        for (int r = 0; r < data.length; r++) {
            for (int c = 0; c < data[r].length; c++) {
                int v = data[r][c];
                Rectangle rect = layer[r][c];
                if (ghost) {
                    rect.setFill(v > 0 ? getFillColor(v) : Color.TRANSPARENT);
                    rect.setOpacity(v > 0 ? 0.25 : 0.0);
                } else {
                    setRectangleData(v, rect);
                }
            }
        }
    }

    /**
     * Initializes a 4x4 preview grid (used for next/held block displays).
     */
    private Rectangle[][] initPreviewGrid(GridPane panel) {
        Rectangle[][] grid = new Rectangle[PREVIEW_SIZE][PREVIEW_SIZE];
        panel.getChildren().clear();
        panel.setHgap(1);
        panel.setVgap(1);
        for (int r = 0; r < PREVIEW_SIZE; r++) {
            for (int c = 0; c < PREVIEW_SIZE; c++) {
                Rectangle rect = new Rectangle(PREVIEW_CELL, PREVIEW_CELL);
                rect.setFill(Color.TRANSPARENT);
                rect.setArcWidth(6);
                rect.setArcHeight(6);
                rect.setMouseTransparent(true);
                grid[r][c] = rect;
                panel.add(rect, c, r);
            }
        }
        return grid;
    }

    /**
     * Clears all cells in a preview grid to transparent.
     */
    private void clearPreview(Rectangle[][] grid) {
        for (int r = 0; r < PREVIEW_SIZE; r++) {
            for (int c = 0; c < PREVIEW_SIZE; c++) {
                grid[r][c].setFill(Color.TRANSPARENT);
                grid[r][c].setOpacity(1.0);
            }
        }
    }

    /**
     * Draws a centered block shape in the preview grid.
     */
    private void drawPreviewCentered(Rectangle[][] grid, int[][] shape) {
        clearPreview(grid);
        int sRows = shape.length, sCols = shape[0].length;
        int rowOffset = (PREVIEW_SIZE - sRows) / 2;
        int colOffset = (PREVIEW_SIZE - sCols) / 2;
        for (int r = 0; r < sRows; r++) {
            for (int c = 0; c < sCols; c++) {
                if (shape[r][c] > 0) {
                    int pr = r + rowOffset, pc = c + colOffset;
                    if (pr >= 0 && pr < PREVIEW_SIZE && pc >= 0 && pc < PREVIEW_SIZE) {
                        grid[pr][pc].setFill(getFillColor(shape[r][c]));
                        grid[pr][pc].setOpacity(1.0);
                    }
                }
            }
        }
    }

    /** Positions the active brick panel and the ghost panel based on the given brick view. */
    private void updatePanelsPosition(ViewData brick) {
        if (brick == null) return;

        // Active brick position
        brickPanel.setLayoutX(
                gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE
        );
        brickPanel.setLayoutY(
                -42 + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE
        );

        // Ghost panel: same X as active; Y based on landing position
        int ghostY = computeGhostY(brick);
        ghostPanel.setLayoutX(
                gamePanel.getLayoutX() + brick.getxPosition() * ghostPanel.getVgap() + brick.getxPosition() * BRICK_SIZE
        );
        ghostPanel.setLayoutY(
                -42 + gamePanel.getLayoutY() + ghostY * ghostPanel.getHgap() + ghostY * BRICK_SIZE
        );
    }

    /** Shows the +score bonus notification if any lines were cleared. */
    private void showScoreBonus(DownData data) {
        if (data != null && data.getClearRow() != null && data.getClearRow().getLinesRemoved() > 0) {
            NotificationPanel p = new NotificationPanel("+" + data.getClearRow().getScoreBonus());
            groupNotification.getChildren().add(p);
            p.showScore(groupNotification.getChildren());
        }
    }

}
