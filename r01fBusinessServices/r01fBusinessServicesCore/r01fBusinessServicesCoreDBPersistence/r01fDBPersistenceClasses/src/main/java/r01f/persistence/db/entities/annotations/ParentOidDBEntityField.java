package r01f.persistence.db.entities.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import r01f.persistence.db.DBEntity;

/**
 * Annotation used to mark a child {@link DBEntity} field that will store the parent entity's oid in a parent-child relation
 * <pre class='brush:java'>
 * 		@Entity
 * 		public class MyChildDBEntity
 * 			 extends DBEntityBase {
 * 			@Column(name="PARENT_OID",length=OID.OID_LENGTH,nullable=false) @Basic
 * 			@Getter @Setter private String _parentOid;
 * 			...
 * 		}
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ParentOidDBEntityField {
	/* nothing */
}
