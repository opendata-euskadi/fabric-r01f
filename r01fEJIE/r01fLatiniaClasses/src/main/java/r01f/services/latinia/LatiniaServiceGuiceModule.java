package r01f.services.latinia;

import javax.inject.Singleton;

import com.google.inject.Binder;
import com.google.inject.Module;

import r01f.objectstreamer.Marshaller;
import r01f.objectstreamer.MarshallerBuilder;

public class LatiniaServiceGuiceModule 
  implements Module {
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
// 	The latinia properties can be located at any XMLProperties file (the <latinia>...</latinia>
// 	can be in any component XML file with other properties (there does NOT exists an exclusive
// 	XMLProperties file for latinia, the latinia config section <latinia>...</latinia> is embeded
// 	in any other XMLProperties file)
//
// 	BUT the latinia service provider (see below) expect a XMLProperties component
// 	named 'latinia' so this component MUST be created here
/////////////////////////////////////////////////////////////////////////////////////////
	private final LatiniaServiceAPIData _cfg;
	private final Marshaller _marshaller;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public LatiniaServiceGuiceModule(final LatiniaServiceAPIData cfg,
									 final Marshaller marshaller) {
		_cfg = cfg;
		_marshaller = marshaller;
	}
	public LatiniaServiceGuiceModule(final LatiniaServiceAPIData cfg) {
		this(cfg,
			 null);		// no marshaller provided: a new one will be created
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void configure(final Binder binder) {
		// bind the config
		binder.bind(LatiniaServiceAPIData.class)
			  .toInstance(_cfg);
		// If Marshaller not provide one will be created.
		if (_marshaller != null) {
			binder.bind(Marshaller.class)
			         .toInstance(_marshaller);
		} else {
			binder.bind(Marshaller.class)
			       .toInstance(MarshallerBuilder.build());
		}
	
		// bind the service
		binder.bind(LatiniaService.class)			 
		 	  .in(Singleton.class);
	}
}
