package com.comp2042.tetris.model;

/**
 * Represents the result of a downward movement or drop operation.
 * <p>
 * A {@code DownData} object bundles together both:
 * <ul>
 *     <li>The {@link ClearRow} result — if any lines were cleared and
 *         the updated board matrix after the move.</li>
 *     <li>The {@link ViewData} — describing the new position and shape
 *         of the active brick for rendering.</li>
 * </ul>
 * </p>
 *
 * <p>
 * This class is used as a return type from movement-related methods
 * (such as {@code onDownEvent()} and {@code onHardDropEvent()}) to
 * efficiently pass both model and view updates back to the controller.
 * </p>
 */
//data class to hold information about cleared rows and the current view state
public final class DownData {
    /** Information about any cleared rows and updated matrix. */
    private final ClearRow clearRow;

    /** Updated view information for the active brick. */
    private final ViewData viewData;

    /**
     * Constructs a new {@code DownData} instance containing both clear-row
     * and updated view information.
     *
     * @param clearRow the {@link ClearRow} result of the downward move
     * @param viewData the {@link ViewData} for the updated brick position
     */
    public DownData(ClearRow clearRow, ViewData viewData) {
        this.clearRow = clearRow;
        this.viewData = viewData;
    }

    /**
     * Returns the clear-row data from this downward move.
     *
     * @return the {@link ClearRow} result, or {@code null} if no rows were cleared
     */
    public ClearRow getClearRow() {
        return clearRow;
    }

    /**
     * Returns the view data after the move.
     *
     * @return the {@link ViewData} representing the brick’s new state
     */
    public ViewData getViewData() {
        return viewData;
    }
}
