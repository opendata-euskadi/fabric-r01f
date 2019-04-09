package r01f.notifier;

import java.io.InputStream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(prefix="_")
@RequiredArgsConstructor
public class NotifierAttachment {
	@Getter private final String _name;
	@Getter private final String _description;
	@Getter private final InputStream _content;
}
