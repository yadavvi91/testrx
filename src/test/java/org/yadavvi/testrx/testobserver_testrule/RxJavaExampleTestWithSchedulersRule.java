package org.yadavvi.testrx.testobserver_testrule;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.yadavvi.testrx.RxJavaExample.WORDS;

/**
 * Created by vishal on 17/2/17.
 */
public class RxJavaExampleTestWithSchedulersRule {

    private static class ImmediateSchedulersRule implements TestRule {

        public Statement apply(Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    RxJavaPlugins.setIoSchedulerHandler(new Function<Scheduler, Scheduler>() {
                        public Scheduler apply(@NonNull Scheduler scheduler) throws Exception {
                            return Schedulers.trampoline();
                        }
                    });
                    RxJavaPlugins.setComputationSchedulerHandler(new Function<Scheduler, Scheduler>() {
                        public Scheduler apply(@NonNull Scheduler scheduler) throws Exception {
                            return Schedulers.trampoline();
                        }
                    });
                    RxJavaPlugins.setNewThreadSchedulerHandler(new Function<Scheduler, Scheduler>() {
                        public Scheduler apply(@NonNull Scheduler scheduler) throws Exception {
                            return Schedulers.trampoline();
                        }
                    });
                }
            };
        }
    }

    @Rule
    public final ImmediateSchedulersRule schedulers = new ImmediateSchedulersRule();

    @Test
    public void testUsingImmediateSchedulersRule() {
        // given
        final TestObserver<String> observer = new TestObserver<String>();
        Observable<String> observable = Observable.fromIterable(WORDS)
                .zipWith(Observable.range(1, Integer.MAX_VALUE), new BiFunction<String, Integer, String>() {
                    public String apply(@NonNull String word, @NonNull Integer index) throws Exception {
                        return String.format("%2d. %s", index, word);
                    }
                });

        // when
        observable.subscribeOn(Schedulers.computation())
                .subscribe(observer);

        // then
        observer.assertComplete();
        observer.assertNoErrors();
        observer.assertValueCount(9);
        assertThat(observer.values(), hasItem(" 4. fox"));
    }

}