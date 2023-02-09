/*******************************************************************************
 * Copyright (c) 2023 Avaloq Group AG.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  Rubén Porras Campo (Avaloq Group AG) - Initial Implementation
 *******************************************************************************/
package org.eclipse.lsp4e.operations.semanticTokens;

import java.util.function.Consumer;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.text.IDocument;
import org.eclipse.lsp4e.LanguageServers.LanguageServerDocumentExecutor;
import org.eclipse.lsp4e.Versioned;
import org.eclipse.lsp4e.internal.DocumentUtil;
import org.eclipse.lsp4e.internal.Pair;
import org.eclipse.lsp4j.SemanticTokens;
import org.eclipse.lsp4j.SemanticTokensLegend;

/**
 * Specialization of <code>Versioned</code> for semanticTokens
 *
 */
public class VersionedSemanticTokens extends Versioned<Pair<SemanticTokens, SemanticTokensLegend>>{

	private final IDocument document;

	public VersionedSemanticTokens(long version, Pair<SemanticTokens, SemanticTokensLegend> data, IDocument document) {
		super(version, data);
		this.document = document;
	}

	/**
	 * Apply the semantic tokens from the server, provided the document is unchanged since the request used
	 * to compute the edits
	 *
	 */
	public void apply(Consumer<Pair<SemanticTokens, SemanticTokensLegend>> first, Consumer<Long> second) {
		if (getVersion() == DocumentUtil.getDocumentModificationStamp(document)) {
			first.accept(get());
			second.accept(getVersion());
		}
	}

	public static VersionedSemanticTokens toVersionedSemantikTokens(LanguageServerDocumentExecutor executor, SemanticTokens data, @Nullable SemanticTokensLegend semanticTokensLegend) {
		return new VersionedSemanticTokens(executor.getStartVersion(), Pair.of(data, semanticTokensLegend), executor.getDocument());
	}
}