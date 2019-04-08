package r01f.types.url.web;

import com.google.common.base.Preconditions;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import r01f.locale.Language;
import r01f.patterns.IsBuilder;
import r01f.types.tag.StringTagList;
import r01f.util.types.collections.CollectionUtils;

/**
 * Builder for {@link WebLinkText} objects
 * Usage:
 * <pre class="brush:java'>
 * WebLinkText linkText = WebLinkTextBuilder.in(Language.ENGLISH)
 *								  .text("Google")
 * 								  .taggedAs("myLink","this_is_a_link")
 *								  .build();
 * </pre>
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class WebLinkTextBuilder
		   implements IsBuilder {
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public static UrlLinkBuilderTextStep in(final Language lang) {
		Preconditions.checkArgument(lang != null,"The lang MUST NOT be null");
		WebLinkText link = new WebLinkText(lang);
		return new WebLinkTextBuilder() { /* nothing */ }
						.new UrlLinkBuilderTextStep(link);
	}
	public UrlLinkBuilderTextStep langNotSpecified() {
		WebLinkText link = new WebLinkText();
		return new UrlLinkBuilderTextStep(link);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  LINK
/////////////////////////////////////////////////////////////////////////////////////////
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public final class UrlLinkBuilderTextStep {
		private final WebLinkText _link;

		public UrlLinkBuilderTextStep text(final String text) {
			_link.setText(text);
			return this;
		}
		public UrlLinkBuilderTextStep title(final String title) {
			_link.setTitle(title);
			return this;
		}
		public UrlLinkBuilderTextStep description(final String description) {
			_link.setDescription(description);
			return this;
		}
		public UrlLinkBuilderTextStep taggedAs(final String... tags) {
			if (CollectionUtils.hasData(tags)) _link.setTags(new StringTagList(tags));
			return this;
		}
		public UrlLinkBuilderTextStep taggedAs(final StringTagList tags) {
			if (CollectionUtils.hasData(tags)) _link.setTags(tags);
			return this;
		}
		public WebLinkText build() {
			return _link;
		}
	}
}
