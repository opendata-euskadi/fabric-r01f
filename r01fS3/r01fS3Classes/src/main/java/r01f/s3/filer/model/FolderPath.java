package r01f.s3.filer.model;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;

import lombok.NoArgsConstructor;
import r01f.annotations.Immutable;
import r01f.guids.OIDBaseMutable;
import r01f.guids.OIDTyped;
import r01f.types.Path;


@Immutable
@NoArgsConstructor
public class FolderPath
	 extends OIDBaseMutable<String>
  implements OIDTyped<String>  {

	private static final long serialVersionUID = 4162366466990455545L;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTANT
/////////////////////////////////////////////////////////////////////////////////////////
	public static final String DELIMITER = "/";
/////////////////////////////////////////////////////////////////////////////////////////
// 	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	FolderPath(final String id) {
		super(id);
	}
	public static FolderPath forPath(final Path path) {
		return new FolderPath(path.asString() + DELIMITER);
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public static Collection<FolderPath> getAllFoldersForPath(final Path path) {
		Collection<FolderPath> st = FluentIterable.from(Splitter.on(DELIMITER).split(path.asString()))
											.transform(new Function<String,FolderPath>() {
																String _previous ;
																@Override
																public FolderPath apply(final String input) {
																	  String rejoined = (_previous !=null) ?
																			     Joiner.on(DELIMITER).join(_previous,input)
																				: input;
															           _previous = rejoined;
															          return FolderPath.forPath(Path.valueOf(rejoined));
																}
														})
											.toList();
			return st;
	}
}