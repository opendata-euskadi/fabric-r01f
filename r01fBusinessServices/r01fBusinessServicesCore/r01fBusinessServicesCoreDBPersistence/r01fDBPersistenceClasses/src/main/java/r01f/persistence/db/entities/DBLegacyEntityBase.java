package r01f.persistence.db.entities;

import javax.persistence.MappedSuperclass;

import org.eclipse.persistence.annotations.OptimisticLocking;
import org.eclipse.persistence.annotations.OptimisticLockingType;

import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import r01f.persistence.db.DBEntity;

/**
 * A fake {@link DBEntity} that fakes all the {@link DBEntity} methods * 
 * just to fake all the system when you have no control over the table
 * and cannot create all the {@link DBEntity} required cols 
 * (ie entityVersion, createTimeStamp, and so on)
 * @param <R>
 */
@MappedSuperclass
@OptimisticLocking(type=OptimisticLockingType.ALL_COLUMNS,
				   cascade=false)	
@Accessors(prefix="_")
@NoArgsConstructor
public abstract class DBLegacyEntityBase 
           implements DBEntity {
	
	private static final long serialVersionUID = -6419576555925325769L;
}
