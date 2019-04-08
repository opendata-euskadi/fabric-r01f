package r01f.concurrent;

import java.util.concurrent.ExecutorService;

import com.google.common.annotations.GwtIncompatible;

import lombok.extern.slf4j.Slf4j;
import r01f.types.TimeLapse;

@GwtIncompatible
@Slf4j
public class Threads {
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public static void safeSleep(final long milis) {
		try {
			Thread.sleep(milis);
		} catch (InterruptedException ignore) {
			ignore.printStackTrace();
		}
	}
	public static void safeSleep(final TimeLapse lapse) {
		Threads.safeSleep(lapse.asMilis());
	}
	public static void waitExecutorServiceToShutdow(final ExecutorService threadPoolExecutor,
												    final TimeLapse checkEvery) {
		try {
			while (!threadPoolExecutor.isShutdown()) {
				log.warn("... still running; wait for {}",checkEvery);
				System.out.println("... still running");
				Thread.sleep(checkEvery.asMilis());
			}
		} catch(Throwable th) {
			log.error("Could not check if the thread pool executor is shouted down: {}",th.getMessage(),th);
			th.printStackTrace(System.out);
		}
	}
}
