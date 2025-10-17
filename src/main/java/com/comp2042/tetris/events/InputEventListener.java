package com.comp2042.tetris.events;

//interface for handling game input events.
//means methods that must be implemented by any class that wants to handle input events in the game.

import com.comp2042.ViewData;
import com.comp2042.tetris.model.DownData;

public interface InputEventListener {

    DownData onDownEvent(MoveEvent event);

    ViewData onLeftEvent(MoveEvent event);

    ViewData onRightEvent(MoveEvent event);

    ViewData onRotateEvent(MoveEvent event);

    void createNewGame();
}
