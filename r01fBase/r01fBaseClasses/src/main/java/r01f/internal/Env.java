package r01f.internal;

import java.util.Iterator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumExtended;
import r01f.enums.EnumExtendedWrapper;
import r01f.guids.CommonOIDs.Environment;
import r01f.util.types.Strings;
import r01f.util.types.collections.Lists;

@Accessors(prefix="_")
@RequiredArgsConstructor
public enum Env
 implements EnumExtended<Env> {
	LOC(Environment.forId("loc")),
	DEV(Environment.forId("dev")),
	INT(Environment.forId("int")),
	TEST(Environment.forId("test")),
	PROD(Environment.forId("prod"));

	@Getter private final Environment _env;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	private static EnumExtendedWrapper<Env> WRAPPER = EnumExtendedWrapper.wrapEnumExtended(Env.class);

	@Override
	public boolean isIn(final Env... els) {
		return WRAPPER.isIn(this,els);
	}
	public boolean isNOTIn(final Env... els) {
		return WRAPPER.isNOTIn(this,els);
	}
	@Override
	public boolean is(final Env el) {
		return WRAPPER.is(this,el);
	}
	public boolean isNOT(final Env el) {
		return WRAPPER.isNOT(this,el);
	}
	public static boolean canBeFrom(final Environment env) {
		boolean outCanBe = true;
		try {
			Env.from(env);
		} catch (IllegalArgumentException illArgEx) {
			outCanBe = false;
		}
		return outCanBe;
	}
	public static Env from(final Environment env) {
		Env outEnv = null;
		for (Env e : Env.values()) {
			if (e.getEnv().equals(env)) {
				outEnv = e;
				break;
			}
		}
		if (outEnv == null) throw new IllegalArgumentException(Strings.customized("{} is NOT a valid {} value (valid ones are {})",
																			 	  env,Env.class,Env.availableValuesDebugInfo()));
		return outEnv;
	}
	public static String availableValuesDebugInfo() {
		final StringBuilder sb = new StringBuilder(Env.values().length * 5);
		for (final Iterator<Env> envIt = Lists.newArrayList(Env.values()).iterator(); envIt.hasNext(); ) {
			sb.append(envIt.next().getEnv());
			if (envIt.hasNext()) sb.append(", ");
		}
		return sb.toString();
	}
}
