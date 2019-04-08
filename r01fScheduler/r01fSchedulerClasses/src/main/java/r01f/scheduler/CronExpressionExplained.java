package r01f.scheduler;

import java.io.Serializable;

import org.quartz.CronExpression;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.util.types.Strings;

@Accessors(prefix="_")
@RequiredArgsConstructor
public class CronExpressionExplained 
  implements Serializable,
			 Debuggable {

	private static final long serialVersionUID = 3341106578675679173L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@Getter private final CronExpression _expression;
	@Getter private final String _description;
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("expr='{}' > {}",
								   _expression != null ? _expression.getCronExpression() : "null",_description);
	}

}
