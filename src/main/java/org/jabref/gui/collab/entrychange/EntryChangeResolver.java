package org.jabref.gui.collab.entrychange;

import java.util.Optional;

import org.jabref.gui.DialogService;
import org.jabref.gui.collab.DatabaseChange;
import org.jabref.gui.collab.DatabaseChangeResolver;
import org.jabref.gui.mergeentries.EntriesMergeResult;
import org.jabref.gui.mergeentries.MergeEntriesDialog;
import org.jabref.gui.mergeentries.newmergedialog.ShowDiffConfig;
import org.jabref.gui.mergeentries.newmergedialog.diffhighlighter.DiffHighlighter.BasicDiffMethod;
import org.jabref.gui.mergeentries.newmergedialog.toolbar.ThreeWayMergeToolbar;
import org.jabref.logic.l10n.Localization;
import org.jabref.model.database.BibDatabaseContext;
import org.jabref.preferences.PreferencesService;

public final class EntryChangeResolver extends DatabaseChangeResolver {
    private final EntryChange entryChange;
    private final BibDatabaseContext databaseContext;

    private final PreferencesService preferencesService;

    public EntryChangeResolver(EntryChange entryChange, DialogService dialogService, BibDatabaseContext databaseContext, PreferencesService preferencesService) {
        super(dialogService);
        this.entryChange = entryChange;
        this.databaseContext = databaseContext;
        this.preferencesService = preferencesService;
    }

    @Override
    public Optional<DatabaseChange> askUserToResolveChange() {
        MergeEntriesDialog mergeEntriesDialog = new MergeEntriesDialog(entryChange.getOldEntry(), entryChange.getNewEntry(), preferencesService);
        mergeEntriesDialog.setLeftHeaderText(Localization.lang("On JabRef"));
        mergeEntriesDialog.setRightHeaderText(Localization.lang("On disk"));
        mergeEntriesDialog.configureDiff(new ShowDiffConfig(ThreeWayMergeToolbar.DiffView.SPLIT, BasicDiffMethod.WORDS));

        return dialogService.showCustomDialogAndWait(mergeEntriesDialog)
                            .map(this::mapMergeResultToExternalChange);
    }

    private EntryChange mapMergeResultToExternalChange(EntriesMergeResult entriesMergeResult) {
        return new EntryChange(
                entryChange.getOldEntry(),
                entriesMergeResult.mergedEntry(),
                databaseContext
        );
    }
}
