package barrysims.clipper;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.openapi.ide.CopyPasteManager;

import java.awt.datatransfer.StringSelection;
import java.util.List;

/**
 * Extracts identifiers from the current editor window
 * and copies them to the clipboard history
 *
 * Only works for scala source files
 */
public class ClipIdentifiers extends AnAction {

    public void actionPerformed (AnActionEvent e) {

        final Editor editor = e.getData(PlatformDataKeys.EDITOR);
        final Project project = e.getProject();

        if (editor == null || project == null) {
            showBalloonPopup(e, "project or editor null", MessageType.ERROR);
            return;
        }

        final CopyPasteManager cpm = CopyPasteManager.getInstance();

        final Document document = editor.getDocument();
        final String text = document.getText();
        final List<String> identifiers = IdentifierGrabber.apply(text);

        for (String id : identifiers) {
            StringSelection strSel = new StringSelection(id);
            cpm.setContents(strSel);
        }

        showBalloonPopup(e, "Copied Identifiers" , MessageType.INFO);
    }

    /**
     * Utility for showing balloon popup messages
     */
    private void showBalloonPopup(AnActionEvent actionEvent, String htmlText, MessageType messageType) {
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(DataKeys.PROJECT.getData(actionEvent.getDataContext()));
        if (statusBar == null) throw new Error("null status bar");

        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(htmlText, messageType, null)
                .setFadeoutTime(1000)
                .createBalloon()
                .show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.atRight);
    }
 }
