package r01f.persistence.db.entities.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import r01f.persistence.db.DBEntity;

/**
 * Annotation used to mark a {@link DBEntity}'s field that will be included when querying for 
 * the entity's summary
 * <pre class='brush:java'>
 * 		@Entity
 * 		public class MyDBEntity
 * 			 extends DBEntityBase {
 * 
 * 			@DBEntitySummaryField	// this field will be included in the summary	
 * 			@Column(name="NAME",length=OID.OID_LENGTH,nullable=false) @Basic
 * 			@Getter @Setter private String _name;
 * 			...
 * 		}
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DBEntitySummaryField {
	/* nothing */
}
