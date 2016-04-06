package de.fu_berlin.inf.dpp.editor;

import de.fu_berlin.inf.dpp.activities.SPath;
import de.fu_berlin.inf.dpp.activities.TextSelectionActivity;
import de.fu_berlin.inf.dpp.session.User;

/**
 * Empty abstract implementation of the {@link ISharedEditorListener} interface.
 */
public abstract class AbstractSharedEditorListener implements
    ISharedEditorListener {

    @Override
    public void editorActivated(User user, SPath filePath) {
        // Does nothing by default
    }

    @Override
    public void editorClosed(User user, SPath filePath) {
        // Does nothing by default
    }

    @Override
    public void followModeChanged(User target, boolean isFollowed) {
        // Does nothing by default
    }

    @Override
    public void textEdited(User user, SPath filePath, int offset,
        String replacedText, String text) {
        // Does nothing by default
    }

    @Override
    public void textSelectionChanged(TextSelectionActivity selection) {
        // Does nothing by default
    }

    @Override
    public void jumpedToUser(User target) {
        // Does nothing by default
    }
}
