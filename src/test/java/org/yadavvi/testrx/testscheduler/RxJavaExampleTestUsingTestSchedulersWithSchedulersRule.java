package org.yadavvi.testrx.testscheduler;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.yadavvi.testrx.RxJavaExample.WORDS;

/**
 * Created by vishal on 17/2/17.
 */
public class RxJavaExampleTestUsingTestSchedulersWithSchedulersRule {

    private static class TestSchedulerRule implements TestRule {

        private final TestScheduler scheduler = new TestScheduler();

        public TestScheduler getTestScheduler() {
            return scheduler;
        }

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
    public final TestSchedulerRule testSchedulerRule = new TestSchedulerRule();


    @Test
    public void testUsingTestSchedulers() {
        // given:
        TestObserver<String> observer = new TestObserver<String>();

        Observable<String> observable = Observable.fromIterable(WORDS)
                .zipWith(Observable.interval(1, TimeUnit.SECONDS), new BiFunction<String, Long, String>() {
                    public String apply(@NonNull String word, @NonNull Long index) throws Exception {
                        return String.format("%2d. %s", index, word);
                    }
                });

        observable.subscribeOn(testSchedulerRule.getTestScheduler())
                .subscribe(observer);

        // expect:
        observer.assertNoValues();
        observer.assertNotComplete();

        // when:
        testSchedulerRule.getTestScheduler().advanceTimeBy(1, TimeUnit.SECONDS);
        // then:
        observer.assertNoErrors();
        observer.assertValueCount(1);
        observer.assertValues(" 0. the");

        // when:
        testSchedulerRule.getTestScheduler().advanceTimeTo(9, TimeUnit.SECONDS);
        // then
        observer.assertComplete();
        observer.assertNoErrors();
        observer.assertValueCount(9);
        assertThat(observer.values(), hasItem(" 3. fox"));

    }


}