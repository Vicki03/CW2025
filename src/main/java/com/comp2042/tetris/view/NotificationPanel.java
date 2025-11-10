package com.comp2042.tetris.view;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * A transient notification overlay for displaying short messages such as
 * score bonuses or level-up announcements.
 * <p>
 * Each notification fades and moves upward before disappearing automatically.
 * It is typically used by
 * {@link com.comp2042.tetris.controller.GuiController}
 * to show visual feedback during gameplay.
 * </p>
 *
 * <p>
 * The panel is styled with a glowing text effect and smooth animations
 * implemented using {@link FadeTransition} and {@link TranslateTransition}.
 * </p>
 */
public class NotificationPanel extends BorderPane {

    /**
     * Constructs a {@code NotificationPanel} displaying the specified text.
     * <p>
     * The label is centered, styled with a glowing white text effect,
     * and uses the CSS class {@code bonusStyle} for additional styling.
     * </p>
     *
     * @param text the message to display (e.g., "+100", "LEVEL UP!")
     */
    public NotificationPanel(String text) {
        setMinHeight(200);
        setMinWidth(220);
        final Label score = new Label(text);
        score.getStyleClass().add("bonusStyle");
        final Effect glow = new Glow(0.6);
        score.setEffect(glow);
        score.setTextFill(Color.WHITE);
        setCenter(score);

    }

    /**
     * Plays a fade-and-translate animation to display the notification,
     * then removes it from the scene once the animation finishes.
     *
     * @param list the {@link ObservableList} of {@link Node}s to which
     *             this notification belongs (used to remove it afterward)
     */
    public void showScore(ObservableList<Node> list) {
        FadeTransition ft = new FadeTransition(Duration.millis(2000), this);
        TranslateTransition tt = new TranslateTransition(Duration.millis(2500), this);
        tt.setToY(this.getLayoutY() - 40);
        ft.setFromValue(1);
        ft.setToValue(0);
        ParallelTransition transition = new ParallelTransition(tt, ft);
        transition.setOnFinished(_ -> list.remove(NotificationPanel.this));
        transition.play();
    }
}
