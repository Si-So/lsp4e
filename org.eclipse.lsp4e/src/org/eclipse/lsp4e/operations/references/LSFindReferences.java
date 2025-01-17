/*******************************************************************************
 * Copyright (c) 2016-23 Red Hat Inc. and others.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  Mickael Istria (Red Hat Inc.) - initial implementation
 *  Michał Niewrzał (Rogue Wave Software Inc.)
 *  Angelo Zerr <angelo.zerr@gmail.com> - fix Bug 526255
 *******************************************************************************/
package org.eclipse.lsp4e.operations.references;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.lsp4e.LSPEclipseUtils;
import org.eclipse.lsp4e.LanguageServerPlugin;
import org.eclipse.lsp4e.internal.LSPDocumentAbstractHandler;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * LSP "Find references" handler.
 *
 */
public class LSFindReferences extends LSPDocumentAbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (HandlerUtil.getActiveEditor(event) instanceof ITextEditor editor) {
			ISelection sel = editor.getSelectionProvider().getSelection();
			if (sel instanceof ITextSelection textSelection) {
				IDocument document = LSPEclipseUtils.getDocument(editor);
				if (document != null) {
					try {
						final var query = new LSSearchQuery(textSelection.getOffset(), document);
						HandlerUtil.getActiveShell(event).getDisplay().asyncExec(() -> NewSearchUI.runQueryInBackground(query));
					} catch (BadLocationException e) {
						LanguageServerPlugin.logError(e);
					}
				}
			}
		}
		return null;
	}

	@Override
	public void setEnabled(Object evaluationContext) {
		setEnabled(ServerCapabilities::getReferencesProvider, this::hasSelection);
	}

	@Override
	public boolean isHandled() {
		return true;
	}

}
