package r01f.resources;

import r01f.patterns.IsBuilder;


public class ResourcesReloadControlBuilder 
  implements IsBuilder {
	/**
	 * Creates the default {@link ResourcesReloadControl}
	 * @return
	 */
	public static ResourcesReloadControl createDefault() {
		return ResourcesReloadControlBuilder.createFor(ResourcesReloadControlDef.DEFAULT);
	}
	/**
	 * Creates a {@link ResourcesReloadControl} using a definition
	 * @param def
	 * @return
	 */
	public static ResourcesReloadControl createFor(final ResourcesReloadControlDef def) {
		ResourcesReloadControl outCtrl = null;
		if (def.getImpl() == null) return new ResourcesReloadControlVoid(def);	// throw new IllegalStateException("NO ResourcesReloadPolicy was set");
		switch(def.getImpl()) {
		case BBDD:
			outCtrl = new ResourcesReloadControlBBDDFlagBased(def);
			break;
		case FILE_LAST_MODIF_TIMESTAMP:
			outCtrl = new ResourcesReloadControlFileLastModifTimeStampBased(def);
			break;
		case NO_RELOAD:
			// nothing
			break;
		case PERIODIC:
			outCtrl = new ResourcesReloadControlPeriodic(def);
			break;
		case VOID:
			outCtrl = new ResourcesReloadControlVoid(def);
			break;
		default:
			break;
		}
		return outCtrl;
	}
}
