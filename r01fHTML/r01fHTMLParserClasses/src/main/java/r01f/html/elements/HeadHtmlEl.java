package r01f.html.elements;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.experimental.Accessors;
import r01f.html.elements.HtmlMetas.MetaHtmlEl;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@Accessors(prefix="_")
public class HeadHtmlEl 
     extends HtmlElementBase {
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////	
	@Getter private final String _title;
	@Getter private final Collection<MetaHtmlEl> _metas;
	@Getter private final String _other;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public HeadHtmlEl(final String title,
					  final Collection<MetaHtmlEl> metas,
					  final String other) {
		super("head");
		_title = title;
		_metas = metas;
		_other = other;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public String asString() {
		StringBuilder outHeadStr = new StringBuilder();
		if (CollectionUtils.hasData(_metas)) {
			for (MetaHtmlEl meta : _metas) {
				String metaStr = meta.asString();
				if (Strings.isNOTNullOrEmpty(metaStr)) outHeadStr.append("\n")
																 .append(meta.asString());
			}
		}
		if (Strings.isNOTNullOrEmpty(_other)) outHeadStr.append("\n")
														.append(_other);
		if (Strings.isNOTNullOrEmpty(_title)) outHeadStr.append(Strings.removeNewlinesOrCarriageRetuns(Strings.customized("\n<title>{}</title>",
																	   			   										  _title)));
		return outHeadStr.toString();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Mix this head with the given one
	 * IMPORTANT: The given head has precedence over this head
	 * @param otherHead
	 * @return
	 */
	public HeadHtmlEl newHeadMixingWith(final HeadHtmlEl otherHead) {
		String otherTitle = otherHead != null ? otherHead.getTitle() : null;
		Collection<MetaHtmlEl> otherMetas = otherHead != null ? otherHead.getMetas() : null;
		String otherRestOfHead = otherHead != null ? otherHead.getOther() : null;
		
		String title = _title(_title,
							  otherTitle);
		Collection<MetaHtmlEl> metas = _metas(_metas, 
											  otherMetas);
		String restOfHead = _restOfHead(_other,
										otherRestOfHead);
		HeadHtmlEl finalHead = (title != null || CollectionUtils.hasData(metas) || restOfHead != null) ? new HeadHtmlEl(title,
								  																					    metas,
								  																					    restOfHead)
																								 	   : null;	// no head
		return finalHead;
	}
	private static String _title(final String thisTitle,
						  		 final String otherTitle) {
		if (Strings.isNOTNullOrEmpty(thisTitle)) return thisTitle;
		if (Strings.isNOTNullOrEmpty(otherTitle)) return otherTitle;
		return null;
	}
	private static Collection<MetaHtmlEl> _metas(final Collection<MetaHtmlEl> thisMetas,
										   	     final Collection<MetaHtmlEl> otherMetas) {
		Collection<MetaHtmlEl> outMetas = null;
		if (CollectionUtils.hasData(thisMetas)
		 && CollectionUtils.hasData(otherMetas)) {
			outMetas = Lists.newArrayListWithExpectedSize(thisMetas.size() + otherMetas.size());
			
			// This metas has preference over the other ones
			Map<String,MetaHtmlEl> otherMetasIndexed = Maps.newHashMapWithExpectedSize(otherMetas.size());
			for (MetaHtmlEl otherMeta : otherMetas) {
				otherMetasIndexed.put(otherMeta.getKey(),otherMeta);
			}
			
			for (MetaHtmlEl thisMeta : thisMetas) {
				MetaHtmlEl otherMeta = otherMetasIndexed.get(thisMeta.getKey());			// exists in other?
				if (otherMeta != null) otherMetasIndexed.remove(thisMeta.getKey());			// if so, remove the meta in other (this meta has preference)		
			}
			outMetas.addAll(thisMetas);							// this metas has precedence over other metas
			outMetas.addAll(otherMetasIndexed.values());		// the remaining other metas
		} else if (CollectionUtils.hasData(thisMetas)) {
			outMetas = thisMetas;
		} else if (CollectionUtils.hasData(otherMetas)) {
			outMetas = otherMetas;
		}
		return outMetas;
	}
	private static String _restOfHead(final String thisRestOfHead,
							   	      final String otherRestOfHead) {
		int length = (Strings.isNOTNullOrEmpty(thisRestOfHead) ? thisRestOfHead.length() : 0) + 
					 (Strings.isNOTNullOrEmpty(otherRestOfHead) ? otherRestOfHead.length() : 0);
		StringBuilder sb = null;
		if (length > 0) {
			sb = new StringBuilder(length);
			if (Strings.isNOTNullOrEmpty(otherRestOfHead)) sb.append(otherRestOfHead);
			if (Strings.isNOTNullOrEmpty(thisRestOfHead)) sb.append(thisRestOfHead);
		}
		return sb != null ? sb.toString() : null;
	}
}