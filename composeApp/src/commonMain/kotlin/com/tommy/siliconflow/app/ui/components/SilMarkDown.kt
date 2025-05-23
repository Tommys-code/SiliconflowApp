package com.tommy.siliconflow.app.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography
import com.mikepenz.markdown.model.Input
import com.mikepenz.markdown.model.MarkdownState
import com.mikepenz.markdown.model.ReferenceLinkHandler
import com.mikepenz.markdown.model.ReferenceLinkHandlerImpl
import com.mikepenz.markdown.model.State
import org.intellij.markdown.flavours.MarkdownFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser

@Composable
fun SilMarkDown(
    state: State,
    modifier: Modifier = Modifier,
    paragraphStyle: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    Markdown(
        state = state,
        modifier = modifier,
        colors = markdownColor(),
        typography = markdownTypography(
            paragraph = paragraphStyle,
            h1 = MaterialTheme.typography.headlineLarge,
            h2 = MaterialTheme.typography.headlineMedium,
            h3 = MaterialTheme.typography.headlineSmall,
        )
    )
}

suspend fun parseMarkdown(
    content: String,
    lookupLinks: Boolean = true,
    flavour: MarkdownFlavourDescriptor = GFMFlavourDescriptor(),
    parser: MarkdownParser = MarkdownParser(flavour),
    referenceLinkHandler: ReferenceLinkHandler = ReferenceLinkHandlerImpl(),
): State {
    val markdownState = MarkdownState(
        Input(
            content = content,
            lookupLinks = lookupLinks,
            flavour = flavour,
            parser = parser,
            referenceLinkHandler = referenceLinkHandler,
        )
    )
    return markdownState.parse()
}
