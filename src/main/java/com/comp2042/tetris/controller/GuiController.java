package com.comp2042.tetris.controller;

import com.comp2042.tetris.events.EventSource;
import com.comp2042.tetris.events.EventType;
import com.comp2042.tetris.events.InputEventListener;
import com.comp2042.tetris.events.MoveEvent;
import com.comp2042.tetris.model.DownData;
import com.comp2042.tetris.model.ViewData;
import com.comp2042.tetris.view.GameOverPanel;
import com.comp2042.tetris.view.NotificationPanel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
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

    @FXML
    private GameOverPanel gameOverPanel;

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

    //stores rectangles representing game board
    /**Rectangle matrix for the game board display*/
    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    /** Rectangles that render the active falling brick. */
    private Rectangle[][] rectangles;

    //controls automatic piece movement
    /** Automatic gravity (piece falls) timer. */
    private Timeline timeLine;

    /** UI state flags. */
    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    private int currentLevel = 1;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
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
                    if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                        NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                        groupNotification.getChildren().add(notificationPanel);
                        notificationPanel.showScore(groupNotification.getChildren());
                    }
                    refreshBrick(downData.getViewData());
                    keyEvent.consume();
                }
            }
            if (keyEvent.getCode() == KeyCode.N) {
                newGame(null);
            }
        });
        //hide replay button at the start
        replayButton.setVisible(false);
        //hide game over panel in the beginning
        gameOverPanel.setVisible(false);

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

        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
        brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
        brickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);

        setGravityMs(400); //default gravity speed
    }

    //returns a color based on integer value
    private Paint getFillColor(int i) {
        Paint returnPaint = switch (i) {
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
        return returnPaint;
    }


    //update the brick's position and color in the UI
    private void refreshBrick(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE) {
            brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
            brickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
                }
            }
        }
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
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());
            }
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
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
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


    public void gameOver() {
        timeLine.stop();
        gameOverPanel.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);
        replayButton.setVisible(true); //show replay button when game is over
        replayButton.setDisable(false); //enable replay button
        pauseButton.setDisable(true); //disable pause button
    }

    //implement replay button later
    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);
        replayButton.setVisible(false); //hide replay button when starting a new game
        eventListener.createNewGame(); //reset game state
        gamePanel.requestFocus();
        timeLine.play();
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
        pauseButton.setText("Pause");
        pauseButton.setDisable(false); //enable pause button
    }

    //implement the pause game feature later
    public void pauseGame(ActionEvent actionEvent) {
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
}
